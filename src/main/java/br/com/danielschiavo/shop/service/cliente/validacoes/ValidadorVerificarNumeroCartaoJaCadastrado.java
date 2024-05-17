package br.com.danielschiavo.shop.service.cliente.validacoes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.danielschiavo.shop.model.ValidacaoException;
import br.com.danielschiavo.shop.model.cliente.Cliente;
import br.com.danielschiavo.shop.model.cliente.cartao.CadastrarCartaoDTO;
import br.com.danielschiavo.shop.repository.cliente.CartaoRepository;

@Service
public class ValidadorVerificarNumeroCartaoJaCadastrado implements ValidadorCadastrarNovoCartao {

	@Autowired
	private CartaoRepository cartaoRepository;
	
	@Override
	public void validar(CadastrarCartaoDTO cartaoDTO, Cliente cliente) {
		cartaoRepository.findByNumeroCartaoAndTipoCartaoAndCliente(cartaoDTO.numeroCartao(), cartaoDTO.tipoCartao(), cliente)
					.ifPresent(cartao -> { 
						throw new ValidacaoException("O usuário já possui um cartão com esse número e tipoCartao cadastrado");
						});
	}

}
