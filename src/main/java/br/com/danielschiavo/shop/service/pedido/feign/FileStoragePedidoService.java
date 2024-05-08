package br.com.danielschiavo.shop.service.pedido.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.danielschiavo.shop.model.filestorage.ArquivoInfoDTO;

@FeignClient(name = "file-storage-service", url = "http://localhost:8080/shop/cliente/filestorage/pedido")
public interface FileStoragePedidoService {
	
	@GetMapping("/{nomeImagemPedido}")
	ArquivoInfoDTO pegarImagemPedido(@PathVariable("nomeImagemPedido") String nomeImagemPedido);
	
	@PostMapping
	String persistirOuRecuperarImagemPedido(@RequestBody PedidoImagemProdutoRequest pedidoImagemRequest);

}
