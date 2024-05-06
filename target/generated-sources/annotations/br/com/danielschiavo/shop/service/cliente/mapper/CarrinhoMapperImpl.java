package br.com.danielschiavo.shop.service.cliente.mapper;

import br.com.danielschiavo.shop.model.cliente.carrinho.Carrinho;
import br.com.danielschiavo.shop.model.cliente.carrinho.MostrarCarrinhoClienteDTO;
import br.com.danielschiavo.shop.model.cliente.carrinho.MostrarCarrinhoClienteDTO.MostrarCarrinhoClienteDTOBuilder;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-05-06T16:19:44-0300",
    comments = "version: 1.4.2.Final, compiler: Eclipse JDT (IDE) 3.36.0.v20231114-0937, environment: Java 17.0.3 (Azul Systems, Inc.)"
)
@Component
public class CarrinhoMapperImpl extends CarrinhoMapper {

    @Override
    public MostrarCarrinhoClienteDTO carrinhoParaMostrarCarrinhoClienteDTO(Carrinho carrinho) {
        if ( carrinho == null ) {
            return null;
        }

        MostrarCarrinhoClienteDTOBuilder mostrarCarrinhoClienteDTO = MostrarCarrinhoClienteDTO.builder();

        return mostrarCarrinhoClienteDTO.build();
    }
}
