package com.reidnet.cursomc.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.reidnet.cursomc.domain.Categoria;
import com.reidnet.cursomc.dto.CategoriaDTO;
import com.reidnet.cursomc.repositories.CategoriaRepository;
import com.reidnet.cursomc.services.exceptions.DataIntegrityException;
import com.reidnet.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class CategoriaService {

	@Autowired
	private CategoriaRepository repo;

	public Categoria find(Integer id) {

		Optional<Categoria> obj = repo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Categoria.class.getName()));

	}

	private void updateData(Categoria newObj, Categoria obj) {
		newObj.setNome(obj.getNome());
	}

	public Categoria insert(Categoria obj) {
		obj.setId(null);
		return repo.save(obj);
	}

	public Categoria update(Categoria obj) {

		Categoria newObj = find(obj.getId());
		updateData(newObj, obj);
		return repo.save(newObj);

	}

	public void delete(Categoria obj) {
		find(obj.getId());
		try {
			repo.deleteById(obj.getId());
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possivel excluir uma Categoria que contem produtos");

		}
	}

	public List<Categoria> findAll() {
		return repo.findAll();

	}

	public Page<Categoria> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {

		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return repo.findAll(pageRequest);
	}

	public Categoria fromDTO(CategoriaDTO objDTO) {
		return new Categoria(objDTO.getId(), objDTO.getNome());

	}

}
