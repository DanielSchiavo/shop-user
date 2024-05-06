package br.com.danielschiavo.shop.service.cliente.mapper;


import java.time.LocalDate;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import br.com.danielschiavo.shop.model.cliente.Cliente;
import br.com.danielschiavo.shop.model.cliente.dto.AlterarClienteDTO;
import br.com.danielschiavo.shop.model.cliente.dto.CadastrarClienteDTO;
import br.com.danielschiavo.shop.model.cliente.dto.MostrarClienteDTO;
import br.com.danielschiavo.shop.model.cliente.endereco.Endereco;
import br.com.danielschiavo.shop.model.filestorage.ArquivoInfoDTO;
import br.com.danielschiavo.shop.service.cliente.MostrarClientePaginaInicialDTO;
import br.com.danielschiavo.shop.service.filestorage.FileStoragePerfilService;

@Mapper(componentModel = "spring")
public abstract class ClienteMapper {
	
    @Mapping(target = "fotoPerfil", source = "fotoPerfil", qualifiedByName = "stringParaArquivoInfoDTO")
    @Mapping(target = "enderecos", ignore = true)
    @Mapping(target = "cartoes", ignore = true)
	public abstract MostrarClienteDTO clienteParaMostrarClienteDTO(Cliente cliente, @Context FileStoragePerfilService fileStorageService);

    @Named("stringParaArquivoInfoDTO")
    public ArquivoInfoDTO stringParaArquivoInfoDTO(String nomeArquivo, @Context FileStoragePerfilService fileStorageService) {
    	return fileStorageService.pegarFotoPerfilPorNome(nomeArquivo);
    }

    @Mapping(target = "fotoPerfil", source = "fotoPerfil", qualifiedByName = "stringParaArquivoInfoDTO")
	public abstract MostrarClientePaginaInicialDTO clienteParaMostrarClientePaginaInicialDTO(Cliente cliente, @Context FileStoragePerfilService fileStorageService);
    
    public Cliente cadastrarClienteDtoParaCliente(CadastrarClienteDTO clienteDTO) {
		Cliente cliente = new Cliente();
		cliente.setCpf(clienteDTO.cpf());
		cliente.setNome(clienteDTO.nome());
		cliente.setSobrenome(clienteDTO.sobrenome());
		cliente.setDataNascimento(clienteDTO.dataNascimento());
		cliente.setDataCriacaoConta(LocalDate.now());
		cliente.setEmail(clienteDTO.email());
		cliente.setSenha(clienteDTO.senha());
		cliente.setCelular(clienteDTO.celular());
		if (clienteDTO.fotoPerfil() != null) {
			cliente.setFotoPerfil(clienteDTO.fotoPerfil());
		}
		else {
			cliente.setFotoPerfil("Padrao.jpeg");
		}
		return cliente;
    }
    
    public void alterarClienteDtoSetarAtributosEmCliente(AlterarClienteDTO alterarClienteDTO, Cliente cliente) {
		if (alterarClienteDTO.cpf() != null) {
			cliente.setCpf(alterarClienteDTO.cpf());
		}
		if (alterarClienteDTO.nome() != null) {
			cliente.setNome(alterarClienteDTO.nome());
		}
		if (alterarClienteDTO.sobrenome() != null) {
			cliente.setSobrenome(alterarClienteDTO.sobrenome());
		}
		if (alterarClienteDTO.dataNascimento() != null) {
			cliente.setDataNascimento(alterarClienteDTO.dataNascimento());
		}
		if (alterarClienteDTO.email() != null) {
			cliente.setEmail(alterarClienteDTO.email());
		}
		if (alterarClienteDTO.senha() != null) {
			cliente.setSenha(alterarClienteDTO.senha());
		}
		if (alterarClienteDTO.celular() != null) {
			cliente.setCelular(alterarClienteDTO.celular());
		}
    }
    
	public Endereco cadastrarClienteDtoParaEndereco(CadastrarClienteDTO clienteDTO, Cliente cliente) {
		Endereco endereco = new Endereco();
		endereco.setCep(clienteDTO.endereco().cep());
		endereco.setRua(clienteDTO.endereco().rua());
		endereco.setNumero(clienteDTO.endereco().numero());
		endereco.setComplemento(clienteDTO.endereco().complemento());
		endereco.setBairro(clienteDTO.endereco().bairro());
		endereco.setCidade(clienteDTO.endereco().cidade());
		endereco.setEstado(clienteDTO.endereco().estado());
		endereco.setEnderecoPadrao(clienteDTO.endereco().enderecoPadrao());
		endereco.setCliente(cliente);
		return endereco;
	}
}
