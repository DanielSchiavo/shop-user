package br.com.danielschiavo.shop.service.cliente.validacoes;

import br.com.danielschiavo.shop.model.cliente.Cliente;
import br.com.danielschiavo.shop.model.cliente.cartao.CadastrarCartaoDTO;

public interface ValidadorCadastrarNovoCartao {
	
	void validar(CadastrarCartaoDTO cartaoDTO, Cliente cliente);

}
