package br.com.danielschiavo.shop.service.cliente.mapper;

import br.com.danielschiavo.shop.model.cliente.endereco.Endereco;
import br.com.danielschiavo.shop.model.cliente.endereco.MostrarEnderecoDTO;
import br.com.danielschiavo.shop.model.cliente.endereco.MostrarEnderecoDTO.MostrarEnderecoDTOBuilder;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-05-06T22:41:55-0300",
    comments = "version: 1.4.2.Final, compiler: Eclipse JDT (IDE) 3.36.0.v20231114-0937, environment: Java 17.0.3 (Azul Systems, Inc.)"
)
@Component
public class EnderecoMapperImpl extends EnderecoMapper {

    @Override
    public MostrarEnderecoDTO enderecoParaMostrarEnderecoDTO(Endereco endereco) {
        if ( endereco == null ) {
            return null;
        }

        MostrarEnderecoDTOBuilder mostrarEnderecoDTO = MostrarEnderecoDTO.builder();

        mostrarEnderecoDTO.bairro( endereco.getBairro() );
        mostrarEnderecoDTO.cep( endereco.getCep() );
        mostrarEnderecoDTO.cidade( endereco.getCidade() );
        mostrarEnderecoDTO.complemento( endereco.getComplemento() );
        mostrarEnderecoDTO.enderecoPadrao( endereco.getEnderecoPadrao() );
        mostrarEnderecoDTO.estado( endereco.getEstado() );
        mostrarEnderecoDTO.id( endereco.getId() );
        mostrarEnderecoDTO.numero( endereco.getNumero() );
        mostrarEnderecoDTO.rua( endereco.getRua() );

        return mostrarEnderecoDTO.build();
    }
}
