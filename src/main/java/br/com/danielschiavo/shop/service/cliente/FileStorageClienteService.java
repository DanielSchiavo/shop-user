package br.com.danielschiavo.shop.service.cliente;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import br.com.danielschiavo.shop.model.MensagemErroDTO;
import br.com.danielschiavo.shop.model.filestorage.ArquivoInfoDTO;

@FeignClient(name = "file-storage-service", url = "http://localhost:8080/shop/cliente/filestorage/foto-perfil")
public interface FileStorageClienteService {
	
	@GetMapping("/{nomeFotoPerfil}")
	ArquivoInfoDTO getFotoPerfil(@PathVariable("nomeFotoPerfil") String nomeFotoPerfil);
	
	@DeleteMapping("/{nomeFotoPerfil}")
	MensagemErroDTO deletarFotoPerfil(@PathVariable("nomeFotoPerfil") String nomeFotoPerfil);

}
