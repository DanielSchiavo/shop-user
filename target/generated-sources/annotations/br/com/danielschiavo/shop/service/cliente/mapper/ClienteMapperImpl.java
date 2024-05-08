package br.com.danielschiavo.shop.service.cliente.mapper;

import br.com.danielschiavo.shop.model.cliente.Cliente;
import br.com.danielschiavo.shop.model.cliente.Cliente.ClienteBuilder;
import br.com.danielschiavo.shop.model.cliente.dto.AlterarClienteDTO;
import br.com.danielschiavo.shop.model.cliente.dto.CadastrarClienteDTO;
import br.com.danielschiavo.shop.model.cliente.dto.MostrarClienteDTO;
import br.com.danielschiavo.shop.model.cliente.dto.MostrarClienteDTO.MostrarClienteDTOBuilder;
import br.com.danielschiavo.shop.model.filestorage.ArquivoInfoDTO;
import br.com.danielschiavo.shop.service.cliente.MostrarClientePaginaInicialDTO;
import br.com.danielschiavo.shop.service.cliente.MostrarClientePaginaInicialDTO.MostrarClientePaginaInicialDTOBuilder;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-05-07T21:09:18-0300",
    comments = "version: 1.4.2.Final, compiler: Eclipse JDT (IDE) 3.36.0.v20231114-0937, environment: Java 17.0.3 (Azul Systems, Inc.)"
)
@Component
public class ClienteMapperImpl extends ClienteMapper {

    @Override
    public MostrarClienteDTO clienteParaMostrarClienteDTO(Cliente cliente, ArquivoInfoDTO arquivoInfoDTO) {
        if ( cliente == null && arquivoInfoDTO == null ) {
            return null;
        }

        MostrarClienteDTOBuilder mostrarClienteDTO = MostrarClienteDTO.builder();

        if ( cliente != null ) {
            mostrarClienteDTO.celular( cliente.getCelular() );
            mostrarClienteDTO.cpf( cliente.getCpf() );
            mostrarClienteDTO.dataCriacaoConta( cliente.getDataCriacaoConta() );
            mostrarClienteDTO.dataNascimento( cliente.getDataNascimento() );
            mostrarClienteDTO.email( cliente.getEmail() );
            mostrarClienteDTO.id( cliente.getId() );
            mostrarClienteDTO.nome( cliente.getNome() );
            mostrarClienteDTO.sobrenome( cliente.getSobrenome() );
        }
        if ( arquivoInfoDTO != null ) {
            mostrarClienteDTO.fotoPerfil( arquivoInfoDTO );
        }

        return mostrarClienteDTO.build();
    }

    @Override
    public MostrarClientePaginaInicialDTO clienteParaMostrarClientePaginaInicialDTO(Cliente cliente, ArquivoInfoDTO arquivoInfoDTO) {
        if ( cliente == null && arquivoInfoDTO == null ) {
            return null;
        }

        MostrarClientePaginaInicialDTOBuilder mostrarClientePaginaInicialDTO = MostrarClientePaginaInicialDTO.builder();

        if ( cliente != null ) {
            mostrarClientePaginaInicialDTO.nome( cliente.getNome() );
        }
        if ( arquivoInfoDTO != null ) {
            mostrarClientePaginaInicialDTO.fotoPerfil( arquivoInfoDTO );
        }

        return mostrarClientePaginaInicialDTO.build();
    }

    @Override
    public Cliente cadastrarClienteDtoParaCliente(CadastrarClienteDTO cadastrarClienteDTO) {
        if ( cadastrarClienteDTO == null ) {
            return null;
        }

        ClienteBuilder cliente = Cliente.builder();

        if ( cadastrarClienteDTO.fotoPerfil() != null ) {
            cliente.fotoPerfil( cadastrarClienteDTO.fotoPerfil() );
        }
        else {
            cliente.fotoPerfil( "Padrao.jpeg" );
        }
        cliente.celular( cadastrarClienteDTO.celular() );
        cliente.cpf( cadastrarClienteDTO.cpf() );
        cliente.dataNascimento( cadastrarClienteDTO.dataNascimento() );
        cliente.email( cadastrarClienteDTO.email() );
        cliente.nome( cadastrarClienteDTO.nome() );
        cliente.senha( cadastrarClienteDTO.senha() );
        cliente.sobrenome( cadastrarClienteDTO.sobrenome() );

        cliente.dataCriacaoConta( java.time.LocalDate.now() );

        return cliente.getCliente();
    }

    @Override
    public Cliente alterarClienteDtoSetarAtributosEmCliente(AlterarClienteDTO alterarClienteDTO, Cliente cliente) {
        if ( alterarClienteDTO == null ) {
            return null;
        }

        cliente.setCelular( alterarClienteDTO.celular() );
        cliente.setCpf( alterarClienteDTO.cpf() );
        cliente.setDataNascimento( alterarClienteDTO.dataNascimento() );
        cliente.setEmail( alterarClienteDTO.email() );
        cliente.setNome( alterarClienteDTO.nome() );
        cliente.setSenha( alterarClienteDTO.senha() );
        cliente.setSobrenome( alterarClienteDTO.sobrenome() );

        return cliente;
    }
}
