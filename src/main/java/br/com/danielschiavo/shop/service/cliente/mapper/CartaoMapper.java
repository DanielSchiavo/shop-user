package br.com.danielschiavo.shop.service.cliente.mapper;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;

import br.com.danielschiavo.shop.model.cliente.Cliente;
import br.com.danielschiavo.shop.model.cliente.cartao.CadastrarCartaoDTO;
import br.com.danielschiavo.shop.model.cliente.cartao.Cartao;
import br.com.danielschiavo.shop.model.cliente.cartao.MostrarCartaoDTO;

@Mapper(componentModel = "spring")
public abstract class CartaoMapper {

	public Cartao cadastrarCartaoDtoParaCartao(CadastrarCartaoDTO cartaoDTO, Cliente cliente) {
		Cartao cartao = new Cartao();
		cartao.setNumeroCartao(cartaoDTO.numeroCartao());
		cartao.setNomeNoCartao(cartaoDTO.nomeNoCartao());
		cartao.setValidadeCartao(cartaoDTO.validadeCartao());
		cartao.setCartaoPadrao(cartaoDTO.cartaoPadrao());
		cartao.setTipoCartao(cartaoDTO.tipoCartao());
		cartao.setCliente(cliente);
		return cartao;
	}
	
	public abstract MostrarCartaoDTO cartaoParaMostrarCartaoDto(Cartao cartao);
	
	public List<MostrarCartaoDTO> listaCartaoParaListaMostrarCartaoDto(List<Cartao> cartoes) {
		List<MostrarCartaoDTO> listaMostrarCartaoDTO = new ArrayList<>();
		cartoes.forEach(cartao -> {
			MostrarCartaoDTO mostrarCartaoDTO = cartaoParaMostrarCartaoDto(cartao);
			listaMostrarCartaoDTO.add(mostrarCartaoDTO);
		});
		return listaMostrarCartaoDTO;
	}
}
