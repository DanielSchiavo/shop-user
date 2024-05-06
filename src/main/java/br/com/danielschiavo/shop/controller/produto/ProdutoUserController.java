package br.com.danielschiavo.shop.controller.produto;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.danielschiavo.shop.model.produto.dto.DetalharProdutoDTO;
import br.com.danielschiavo.shop.model.produto.dto.MostrarProdutosDTO;
import br.com.danielschiavo.shop.service.produto.ProdutoUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/shop")
@Tag(name = "Produto - User", description = "Todos endpoints relacionados com os produtos da loja, de uso publico")
public class ProdutoUserController {

	@Autowired
	private ProdutoUserService produtoService;
	
	@GetMapping("/publico/produto")
	@Operation(summary = "Lista todos os produtos da loja")
	public ResponseEntity<Page<MostrarProdutosDTO>> listarProdutos(Pageable pageable) throws IOException {
		Page<MostrarProdutosDTO> pageableMostrarProdutosDTO = produtoService.listarProdutos(pageable);
		
		return ResponseEntity.ok(pageableMostrarProdutosDTO);
	}
	
	@GetMapping("/publico/produto/{idProduto}")
	@Operation(summary = "Pega todos os dados do produto com id fornecido no parametro da requisição")
	public ResponseEntity<DetalharProdutoDTO> detalharProdutoPorId(@PathVariable Long idProduto) {
		DetalharProdutoDTO detalharProdutoDTO = produtoService.detalharProdutoPorId(idProduto);
		
		return ResponseEntity.ok(detalharProdutoDTO);
	}
	
}
