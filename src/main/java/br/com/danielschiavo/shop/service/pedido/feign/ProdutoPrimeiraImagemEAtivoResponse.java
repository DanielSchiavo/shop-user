package br.com.danielschiavo.shop.service.pedido.feign;

import java.math.BigDecimal;

public record ProdutoPrimeiraImagemEAtivoResponse(
				Long produtoId,
				String nome,
				BigDecimal preco,
				String primeiraImagem,
				Boolean ativo
		) {

}
