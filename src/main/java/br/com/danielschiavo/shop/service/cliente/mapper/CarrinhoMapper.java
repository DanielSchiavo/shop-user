package br.com.danielschiavo.shop.service.cliente.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import br.com.danielschiavo.shop.model.cliente.carrinho.Carrinho;
import br.com.danielschiavo.shop.model.cliente.carrinho.MostrarCarrinhoClienteDTO;

@Mapper(componentModel = "spring")
public abstract class CarrinhoMapper {

	@Mapping(target = "itemsCarrinho", ignore = true)
	@Mapping(target = "valorTotal", ignore = true) 
	public abstract MostrarCarrinhoClienteDTO carrinhoParaMostrarCarrinhoClienteDTO(Carrinho carrinho);
	
}
