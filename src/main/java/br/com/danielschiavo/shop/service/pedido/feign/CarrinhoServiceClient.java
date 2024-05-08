package br.com.danielschiavo.shop.service.pedido.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;


@FeignClient(name = "file-storage-service", url = "http://localhost:8080/shop/publico/produto")
public interface CarrinhoServiceClient {
	
	@DeleteMapping("/{produtosId}")
	void deletarProdutosNoCarrinho(@PathVariable("produtosId") List<Long> produtosId, @RequestHeader("Authorization") String token);

}
