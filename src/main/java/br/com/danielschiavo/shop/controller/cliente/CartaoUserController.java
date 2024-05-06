package br.com.danielschiavo.shop.controller.cliente;


import java.util.List;

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
import br.com.danielschiavo.shop.model.cliente.cartao.CadastrarCartaoDTO;
import br.com.danielschiavo.shop.model.cliente.cartao.MostrarCartaoDTO;
import br.com.danielschiavo.shop.service.cliente.CartaoUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/shop")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Cliente - Cartão", description = "Todos endpoints relacionados com os cartões do cliente, que o próprio poderá utilizar")
public class CartaoUserController {

	@Autowired
	private CartaoUserService cartaoService;

	@DeleteMapping("/cliente/cartao/{idCartao}")
	@Operation(summary = "Deleta o cartão que contém o id fornecido")
	public ResponseEntity<?> deletarCartaoPorIdToken(@PathVariable Long idCartao) {
		try {
			cartaoService.deletarCartaoPorIdToken(idCartao);
			return ResponseEntity.noContent().build();
			
		} catch (ValidacaoException e) {
			HttpStatus status = HttpStatus.NOT_FOUND;
			return ResponseEntity.status(status).body(new MensagemErroDTO(status, e));
		}
	}

	@GetMapping("/cliente/cartao")
	@Operation(summary = "Pega todos os cartões do usuário que está logado")
	public ResponseEntity<?> pegarCartoesClientePorIdToken() {
		try {
			List<MostrarCartaoDTO> listMostrarCartaoDTO = cartaoService.pegarCartoesClientePorIdToken();
			return ResponseEntity.status(HttpStatus.OK).body(listMostrarCartaoDTO);

		} catch (ValidacaoException e) {
			HttpStatus status = HttpStatus.NOT_FOUND;
			return ResponseEntity.status(status).body(new MensagemErroDTO(status, e));
		}
	}
	
	@PostMapping("/cliente/cartao")
	@Operation(summary = "Cadastra um novo cartão para o usuário")
	public ResponseEntity<MostrarCartaoDTO> cadastrarNovoCartaoPorIdToken(@RequestBody @Valid CadastrarCartaoDTO cartaoDTO) {
		MostrarCartaoDTO detalharCartaoDTO = cartaoService.cadastrarNovoCartaoPorIdToken(cartaoDTO);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(detalharCartaoDTO);
	}
	
	@PutMapping("/cliente/cartao/{idCartao}")
	@Operation(summary = "Altera o cartão padrão do usuário", description = "Se essa requisição for enviada fornecendo um id de cartão que esteja atribuido cartaoPadrao = false, então esse cartão será definido como cartaoPadrao = true e todos os outros cartões do cliente como false. Se o cartão fornecido no parâmetro através do id estiver como cartaoPadrao = true, esse cartão será definido como cartaoPadrao = false")
	public ResponseEntity<?> alterarCartaoPadraoPorIdToken(@PathVariable Long idCartao) {
		try {
			cartaoService.alterarCartaoPadraoPorIdToken(idCartao);
			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (ValidacaoException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
	
	

}
