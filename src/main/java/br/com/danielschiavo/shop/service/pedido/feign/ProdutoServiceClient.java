package br.com.danielschiavo.shop.service.pedido.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "file-storage-service", url = "http://localhost:8080/shop/publico/produto")
public interface ProdutoServiceClient {
	
	@GetMapping("/{produtosId}")
	List<ProdutoPrimeiraImagemEAtivoResponse> pegarPrimeiraImagemEVerificarSeEstaAtivo(@PathVariable("produtosId") List<Long> produtosId);

}
