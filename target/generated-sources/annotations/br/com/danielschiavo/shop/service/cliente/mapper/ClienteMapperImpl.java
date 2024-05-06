package br.com.danielschiavo.shop.service.cliente.mapper;

import br.com.danielschiavo.shop.model.cliente.Cliente;
import br.com.danielschiavo.shop.model.cliente.dto.MostrarClienteDTO;
import br.com.danielschiavo.shop.model.cliente.dto.MostrarClienteDTO.MostrarClienteDTOBuilder;
import br.com.danielschiavo.shop.model.cliente.role.Role;
import br.com.danielschiavo.shop.service.cliente.MostrarClientePaginaInicialDTO;
import br.com.danielschiavo.shop.service.cliente.MostrarClientePaginaInicialDTO.MostrarClientePaginaInicialDTOBuilder;
import br.com.danielschiavo.shop.service.filestorage.FileStoragePerfilService;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-05-06T16:28:50-0300",
    comments = "version: 1.4.2.Final, compiler: Eclipse JDT (IDE) 3.36.0.v20231114-0937, environment: Java 17.0.3 (Azul Systems, Inc.)"
)
@Component
public class ClienteMapperImpl extends ClienteMapper {

    @Override
    public MostrarClienteDTO clienteParaMostrarClienteDTO(Cliente cliente, FileStoragePerfilService fileStorageService) {
        if ( cliente == null ) {
            return null;
        }

        MostrarClienteDTOBuilder mostrarClienteDTO = MostrarClienteDTO.builder();

        mostrarClienteDTO.fotoPerfil( stringParaArquivoInfoDTO( cliente.getFotoPerfil(), fileStorageService ) );
        mostrarClienteDTO.celular( cliente.getCelular() );
        mostrarClienteDTO.cpf( cliente.getCpf() );
        mostrarClienteDTO.dataCriacaoConta( cliente.getDataCriacaoConta() );
        mostrarClienteDTO.dataNascimento( cliente.getDataNascimento() );
        mostrarClienteDTO.email( cliente.getEmail() );
        mostrarClienteDTO.id( cliente.getId() );
        mostrarClienteDTO.nome( cliente.getNome() );
        Set<Role> set = cliente.getRoles();
        if ( set != null ) {
            mostrarClienteDTO.roles( new HashSet<Role>( set ) );
        }
        mostrarClienteDTO.sobrenome( cliente.getSobrenome() );

        return mostrarClienteDTO.build();
    }

    @Override
    public MostrarClientePaginaInicialDTO clienteParaMostrarClientePaginaInicialDTO(Cliente cliente, FileStoragePerfilService fileStorageService) {
        if ( cliente == null ) {
            return null;
        }

        MostrarClientePaginaInicialDTOBuilder mostrarClientePaginaInicialDTO = MostrarClientePaginaInicialDTO.builder();

        mostrarClientePaginaInicialDTO.fotoPerfil( stringParaArquivoInfoDTO( cliente.getFotoPerfil(), fileStorageService ) );
        mostrarClientePaginaInicialDTO.nome( cliente.getNome() );

        return mostrarClientePaginaInicialDTO.build();
    }
}
