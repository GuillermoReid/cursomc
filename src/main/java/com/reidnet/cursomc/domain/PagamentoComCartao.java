package com.reidnet.cursomc.domain;

import javax.persistence.Entity;

import com.reidnet.cursomc.domain.enums.EstadoPagamento;

@Entity
public class PagamentoComCartao extends Pagamento {
	private static final long serialVersionUID = 1L;
	
	
	private Integer numeroDeParcelas;
	
	public PagamentoComCartao(){
		
	}

	public PagamentoComCartao(Integer id, EstadoPagamento estado, Pedido pedido, Integer numParcelas) {
		super(id, estado, pedido);
		this.numeroDeParcelas = numParcelas;
	}

	public Integer getNumeroDeParcelas() {
		return numeroDeParcelas;
	}

	public void setNumeroDeParcelas(Integer numeroDeParcelas) {
		this.numeroDeParcelas = numeroDeParcelas;
	}
	
	

}
