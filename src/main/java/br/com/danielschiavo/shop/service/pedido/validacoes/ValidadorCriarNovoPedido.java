package br.com.danielschiavo.shop.service.pedido.validacoes;

import br.com.danielschiavo.shop.model.cliente.Cliente;
import br.com.danielschiavo.shop.model.pedido.dto.CriarPedidoDTO;

public interface ValidadorCriarNovoPedido {
	
	void validar(CriarPedidoDTO pedidoDTO, Cliente cliente);
	
}
