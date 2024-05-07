package br.com.danielschiavo.shop.service.pedido.feign.endereco;

public record EnderecoResponse(
		String cep,
		String rua,
		String numero,
		String complemento,
		String bairro,
		String cidade,
		String estado
		) {

}
