package br.com.danielschiavo.shop.service.cliente.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import br.com.danielschiavo.shop.model.cliente.Cliente;
import br.com.danielschiavo.shop.model.cliente.dto.AlterarClienteDTO;
import br.com.danielschiavo.shop.model.cliente.dto.CadastrarClienteDTO;
import br.com.danielschiavo.shop.model.cliente.dto.MostrarClienteDTO;
import br.com.danielschiavo.shop.model.filestorage.ArquivoInfoDTO;
import br.com.danielschiavo.shop.service.cliente.MostrarClientePaginaInicialDTO;

@Mapper(componentModel = "spring")
public abstract class ClienteMapper {
	
	@Mapping(target = "fotoPerfil", source = "arquivoInfoDTO")
	public abstract MostrarClienteDTO clienteParaMostrarClienteDTO(Cliente cliente, ArquivoInfoDTO arquivoInfoDTO);

	@Mapping(target = "fotoPerfil", source = "arquivoInfoDTO")
	public abstract MostrarClientePaginaInicialDTO clienteParaMostrarClientePaginaInicialDTO(Cliente cliente, ArquivoInfoDTO arquivoInfoDTO);
    
    @Mapping(target = "dataCriacaoConta", expression = "java(java.time.LocalDate.now())")
    @Mapping(target = "fotoPerfil", source = "cadastrarClienteDTO.fotoPerfil", defaultValue = "Padrao.jpeg")
    public abstract Cliente cadastrarClienteDtoParaCliente(CadastrarClienteDTO cadastrarClienteDTO);
    
    public abstract Cliente alterarClienteDtoSetarAtributosEmCliente(AlterarClienteDTO alterarClienteDTO, @MappingTarget Cliente cliente);
    
}
