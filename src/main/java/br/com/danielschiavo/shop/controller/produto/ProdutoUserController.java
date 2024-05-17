package br.com.danielschiavo.shop.controller.produto;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import br.com.danielschiavo.shop.model.pedido.dto.ProdutoPedidoDTO;
import br.com.danielschiavo.shop.model.produto.dto.DetalharProdutoDTO;
import br.com.danielschiavo.shop.model.produto.dto.MostrarProdutosDTO;
import br.com.danielschiavo.shop.service.produto.ProdutoUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
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
	
	@GetMapping("/publico/produto/{produtoId}")
	@Operation(summary = "Pega todos os dados do produto com id fornecido no parametro da requisição")
	public ResponseEntity<DetalharProdutoDTO> detalharProdutoPorId(@PathVariable Long produtoId) {
		DetalharProdutoDTO detalharProdutoDTO = produtoService.detalharProdutoPorId(produtoId);
		
		return ResponseEntity.ok(detalharProdutoDTO);
	}
	
	@GetMapping("/publico/produto/{produtosId}/pedido")
	@Operation(summary = "Pega todos os dados do produto com id fornecido no parametro da requisição")
	public ResponseEntity<List<ProdutoPedidoDTO>> detalharProdutosPorIdParaFazerPedido(@PathVariable List<Long> produtosId) {
		List<ProdutoPedidoDTO> produtoPedidoDTO = produtoService.detalharProdutosPorIdParaFazerPedido(produtosId);
		
		return ResponseEntity.ok(produtoPedidoDTO);
	}
	
}
