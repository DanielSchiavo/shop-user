package br.com.danielschiavo.shop.service.produto;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import br.com.danielschiavo.shop.model.filestorage.ArquivoInfoDTO;

@FeignClient(name = "file-storage-service", url = "http://localhost:8080/shop/cliente/filestorage/foto-perfil")
public interface FileStorageProdutoService {
	
	@GetMapping("/{arquivo}")
	ArquivoInfoDTO pegarArquivoProduto(@PathVariable("arquivo") String arquivo);
	
	@GetMapping("/{arquivo}")
	List<ArquivoInfoDTO> pegarArquivosProduto(@PathVariable("arquivo") List<String> arquivos);

}
