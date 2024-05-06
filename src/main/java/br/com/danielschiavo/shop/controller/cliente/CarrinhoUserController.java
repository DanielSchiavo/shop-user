package br.com.danielschiavo.shop.controller.cliente;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.danielschiavo.shop.model.MensagemErroDTO;
import br.com.danielschiavo.shop.model.ValidacaoException;
import br.com.danielschiavo.shop.model.cliente.carrinho.MostrarCarrinhoClienteDTO;
import br.com.danielschiavo.shop.model.cliente.carrinho.itemcarrinho.AdicionarItemCarrinhoDTO;
import br.com.danielschiavo.shop.service.cliente.CarrinhoUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/shop")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Cliente - Carrinho", description = "Todos endpoints relacionados com o carrinho do cliente, que o próprio poderá utilizar")
public class CarrinhoUserController {
	
	@Autowired
	private CarrinhoUserService carrinhoService;
	
	@DeleteMapping("/cliente/carrinho/{idProduto}")
	@Operation(summary = "Deleta um produto do carrinho")
	public ResponseEntity<Object> deletarProdutoNoCarrinhoPorIdToken(@PathVariable Long idProduto) {
		try {
			carrinhoService.deletarProdutoNoCarrinhoPorIdToken(idProduto);
			return ResponseEntity.noContent().build();
			
		} catch (ValidacaoException e) {
			HttpStatus status = HttpStatus.NOT_FOUND;
			return ResponseEntity.status(status).body(new MensagemErroDTO(status, e));
		}
	}
	
	@GetMapping("/cliente/carrinho")
	@Operation(summary = "Pega todos os produtos que estão no carrinho do cliente")
	public ResponseEntity<?> pegarCarrinhoClientePorIdToken() {
		try {
			MostrarCarrinhoClienteDTO mostrarCarrinhoClienteDTO = carrinhoService.pegarCarrinhoClientePorIdToken();
			
			return ResponseEntity.ok(mostrarCarrinhoClienteDTO);
		} catch (ValidacaoException e) {
			HttpStatus status = HttpStatus.NOT_FOUND;
			return ResponseEntity.status(status).body(new MensagemErroDTO(status, e));
		}
	}
	
	@PostMapping("/cliente/carrinho")
	@Operation(summary = "Adiciona um produto no carrinho, se o cliente não tiver um carrinho, também cria automáticamente")
	public ResponseEntity<Object> adicionarProdutosNoCarrinhoPorIdToken(@RequestBody @Valid AdicionarItemCarrinhoDTO itemCarrinhoDTO) {
		try {
			carrinhoService.adicionarProdutosNoCarrinhoPorIdToken(itemCarrinhoDTO);
			return ResponseEntity.ok().build();
			
		} catch (ValidacaoException e) {
			HttpStatus status = HttpStatus.NOT_FOUND;
			return ResponseEntity.status(status).body(new MensagemErroDTO(status, e));
		}
	}
	
	@PutMapping("/cliente/carrinho")
	@Operation(summary = "Seta a quantidade de determinado produto que está no carrinho")
	public ResponseEntity<Object> setarQuantidadeProdutoNoCarrinhoPorIdToken(@RequestBody @Valid AdicionarItemCarrinhoDTO itemCarrinhoDTO) {
		try {
			carrinhoService.setarQuantidadeProdutoNoCarrinhoPorIdToken(itemCarrinhoDTO);
			return ResponseEntity.ok().build();
			
		} catch (ValidacaoException e) {
			HttpStatus status = HttpStatus.NOT_FOUND;
			return ResponseEntity.status(status).body(new MensagemErroDTO(status, e));
		}
	}
}
