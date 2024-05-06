package br.com.danielschiavo.shop.service.cliente.mapper;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;

import br.com.danielschiavo.shop.model.cliente.endereco.AlterarEnderecoDTO;
import br.com.danielschiavo.shop.model.cliente.endereco.CadastrarEnderecoDTO;
import br.com.danielschiavo.shop.model.cliente.endereco.Endereco;
import br.com.danielschiavo.shop.model.cliente.endereco.MostrarEnderecoDTO;

@Mapper(componentModel = "spring")
public abstract class EnderecoMapper {

	public abstract MostrarEnderecoDTO enderecoParaMostrarEnderecoDTO(Endereco endereco);
	
	public List<MostrarEnderecoDTO> listaEnderecoParaMostrarEnderecoDTO(List<Endereco> enderecos){
		List<MostrarEnderecoDTO> listaMostrarEnderecoDTO = new ArrayList<>();
		enderecos.forEach(endereco -> {
			MostrarEnderecoDTO mostrarEnderecoDTO = enderecoParaMostrarEnderecoDTO(endereco);
			listaMostrarEnderecoDTO.add(mostrarEnderecoDTO);
		});
		return listaMostrarEnderecoDTO;
	}
	
	public Endereco cadastrarEnderecoDtoParaEndereco(CadastrarEnderecoDTO cadastrarEnderecoDTO) {
		Endereco endereco = new Endereco();
		endereco.setCep(cadastrarEnderecoDTO.cep());
		endereco.setRua(cadastrarEnderecoDTO.rua());
		endereco.setNumero(cadastrarEnderecoDTO.numero());
		if (cadastrarEnderecoDTO.complemento() != null) {
			endereco.setComplemento(cadastrarEnderecoDTO.complemento());
		}
		endereco.setBairro(cadastrarEnderecoDTO.bairro());
		endereco.setCidade(cadastrarEnderecoDTO.cidade());
		endereco.setEstado(cadastrarEnderecoDTO.estado());
		if (cadastrarEnderecoDTO.enderecoPadrao() != null) {
			endereco.setEnderecoPadrao(cadastrarEnderecoDTO.enderecoPadrao());
		}
		return endereco;
	}
	
	public void alterarEnderecoDtoParaEndereco(AlterarEnderecoDTO enderecoDTO, Endereco endereco) {
		if (enderecoDTO.cep() != null) {
			endereco.setCep(enderecoDTO.cep());
		}
		if (enderecoDTO.rua() != null) {
			endereco.setRua(enderecoDTO.rua());
		}
		if (enderecoDTO.numero() != null) {
			endereco.setNumero(enderecoDTO.numero());
		}
		if (enderecoDTO.complemento() != null) {
			endereco.setComplemento(enderecoDTO.complemento());
		}
		if (enderecoDTO.bairro() != null) {
			endereco.setBairro(enderecoDTO.bairro());
		}
		if (enderecoDTO.cidade() != null) {
			endereco.setCidade(enderecoDTO.cidade());
		}
		if (enderecoDTO.estado() != null) {
			endereco.setEstado(enderecoDTO.estado());
		}
		if (enderecoDTO.enderecoPadrao() != null) {
			endereco.setEnderecoPadrao(enderecoDTO.enderecoPadrao());
		}
	}
}
