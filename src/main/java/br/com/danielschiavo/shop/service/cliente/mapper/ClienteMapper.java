package br.com.danielschiavo.shop.service.cliente.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import br.com.danielschiavo.shop.model.cliente.Cliente;
import br.com.danielschiavo.shop.model.cliente.dto.AlterarClienteDTO;
import br.com.danielschiavo.shop.model.cliente.dto.CadastrarClienteDTO;
import br.com.danielschiavo.shop.model.filestorage.ArquivoInfoDTO;
import br.com.danielschiavo.shop.service.cliente.MostrarClientePaginaInicialDTO;

@Mapper(componentModel = "spring")
public abstract class ClienteMapper {
	
	@Mapping(target = "fotoPerfil", source = "arquivoInfoDTO")
	public abstract MostrarClientePaginaInicialDTO clienteParaMostrarClientePaginaInicialDTO(Cliente cliente, ArquivoInfoDTO arquivoInfoDTO);
    
    @Mapping(target = "dataCriacaoConta", expression = "java(java.time.LocalDate.now())")
    @Mapping(target = "fotoPerfil", source = "cadastrarClienteDTO.fotoPerfil", defaultValue = "Padrao.jpeg")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "carrinho", ignore = true)
    @Mapping(target = "adicionarRole", ignore = true)
    @Mapping(target = "clientes", ignore = true)
    public abstract Cliente cadastrarClienteDtoParaCliente(CadastrarClienteDTO cadastrarClienteDTO);
    
    @Mapping(target = "fotoPerfil", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "carrinho", ignore = true)
    @Mapping(target = "dataCriacaoConta", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "roles", ignore = true)
    public abstract Cliente alterarClienteDTOVerificarESetarAtributosEmCliente(AlterarClienteDTO alterarClienteDTO, @MappingTarget Cliente cliente);
    
}
