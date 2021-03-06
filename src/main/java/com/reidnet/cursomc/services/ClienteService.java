package com.reidnet.cursomc.services;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.reidnet.cursomc.domain.Cidade;
import com.reidnet.cursomc.domain.Cliente;
import com.reidnet.cursomc.domain.Endereco;
import com.reidnet.cursomc.domain.enums.Perfil;
import com.reidnet.cursomc.domain.enums.TipoCliente;
import com.reidnet.cursomc.dto.ClienteDTO;
import com.reidnet.cursomc.dto.ClienteNewDTO;
import com.reidnet.cursomc.repositories.CidadeRepository;
import com.reidnet.cursomc.repositories.ClienteRepository;
import com.reidnet.cursomc.repositories.EnderecoRepository;
import com.reidnet.cursomc.security.UserSS;
import com.reidnet.cursomc.services.exceptions.AuthorizationException;
import com.reidnet.cursomc.services.exceptions.DataIntegrityException;
import com.reidnet.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository repo;

	@Autowired
	private CidadeRepository cidadeRepository;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Autowired
	private BCryptPasswordEncoder pe;


	@Transactional
	public Cliente insert(Cliente obj) {
		obj.setId(null);
		repo.save(obj);
		enderecoRepository.saveAll(obj.getEnderecos());
		return obj;
	}
	
	
	public Cliente find(Integer id) {
		
		UserSS user = UserService.authenticated();
		if(user != null) { 
			System.out.println("*************************");
			System.out.println("Usuario é Admin:" + user.hasRole(Perfil.ADMIN));
			System.out.println("*************************");
		}else {
			System.out.println("*************************");
			System.out.println("Usuario nao logado");
			System.out.println("*************************");
		}
		
		if (user == null || !user.hasRole(Perfil.ADMIN) && !id.equals(user.getId())) {
			throw new AuthorizationException("Aceso Negado.");
		}
		Optional<Cliente> obj = repo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Cliente.class.getName()));
	}

	private void updateData(Cliente newObj, Cliente obj) {
		newObj.setNome(obj.getNome());
		newObj.setEmail(obj.getEmail());
	}

	public Cliente update(Cliente obj) {

		Cliente newObj = find(obj.getId());
		updateData(newObj, obj);
		return repo.save(newObj);

	}

	public void delete(Cliente obj) {
		find(obj.getId());
		try {
			repo.deleteById(obj.getId());
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possivel excluir porque existem pedidos relacionados");
		}
	}

	public List<Cliente> findAll() {
		return repo.findAll();
	}

	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return repo.findAll(pageRequest);
	}

	public Cliente fromDTO(ClienteDTO objDTO) {
		return new Cliente(objDTO.getId(), objDTO.getNome(), objDTO.getEmail(), null, null, null);
	}

	public Cliente fromDTO(ClienteNewDTO objDTO) {

		 Cliente cli = new Cliente(null, objDTO.getNome(), objDTO.getEmail(), objDTO.getCpfOuCnpj(), TipoCliente.toEnum(objDTO.getTipo()), pe.encode(objDTO.getSenha()));
		 Cidade cid = cidadeRepository.findById(objDTO.getCidadeId()).get();
		 Endereco end = new Endereco(null, objDTO.getLogradouro(), objDTO.getNumero(), objDTO.getComplemento(), objDTO.getBairro(), objDTO.getCep(), cli, cid);
		 cli.getEnderecos().add(end);
		 cli.getTelefones().add(objDTO.getTelefone1());
		 
		 if (objDTO.getTelefone2() != null) {
			 cli.getTelefones().add(objDTO.getTelefone2());		 
		 }
		 if (objDTO.getTelefone3() != null) {
			 cli.getTelefones().add(objDTO.getTelefone3());		 
		 }
		 return cli;
	}



}
