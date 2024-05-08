package br.com.danielschiavo.shop.service.cliente.mapper;

import br.com.danielschiavo.shop.model.cliente.cartao.Cartao;
import br.com.danielschiavo.shop.model.cliente.cartao.MostrarCartaoDTO;
import br.com.danielschiavo.shop.model.cliente.cartao.MostrarCartaoDTO.MostrarCartaoDTOBuilder;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-05-06T22:41:55-0300",
    comments = "version: 1.4.2.Final, compiler: Eclipse JDT (IDE) 3.36.0.v20231114-0937, environment: Java 17.0.3 (Azul Systems, Inc.)"
)
@Component
public class CartaoMapperImpl extends CartaoMapper {

    @Override
    public MostrarCartaoDTO cartaoParaMostrarCartaoDto(Cartao cartao) {
        if ( cartao == null ) {
            return null;
        }

        MostrarCartaoDTOBuilder mostrarCartaoDTO = MostrarCartaoDTO.builder();

        mostrarCartaoDTO.cartaoPadrao( cartao.getCartaoPadrao() );
        mostrarCartaoDTO.id( cartao.getId() );
        mostrarCartaoDTO.nomeBanco( cartao.getNomeBanco() );
        mostrarCartaoDTO.numeroCartao( cartao.getNumeroCartao() );
        mostrarCartaoDTO.tipoCartao( cartao.getTipoCartao() );

        return mostrarCartaoDTO.build();
    }
}
