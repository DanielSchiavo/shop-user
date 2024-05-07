package br.com.danielschiavo.shop.service.pedido.feign;

public record PedidoImagemProdutoRequest(
		String nomePrimeiraImagemProduto,
		Long produtoId
		) {

}
