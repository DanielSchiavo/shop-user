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
import org.springframework.web.bind.annotation.RestController;

import br.com.danielschiavo.shop.model.MensagemErroDTO;
import br.com.danielschiavo.shop.model.ValidacaoException;
import br.com.danielschiavo.shop.model.cliente.endereco.AlterarEnderecoDTO;
import br.com.danielschiavo.shop.model.cliente.endereco.CadastrarEnderecoDTO;
import br.com.danielschiavo.shop.model.cliente.endereco.MostrarEnderecoDTO;
import br.com.danielschiavo.shop.service.cliente.EnderecoUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Cliente - Endereço", description = "Todos endpoints relacionados com os endereços do cliente, que o próprio poderá utilizar")
public class EnderecoUserController {

	@Autowired
	private EnderecoUserService enderecoService;
	
	@DeleteMapping("/cliente/endereco/{idEndereco}")
	@Operation(summary = "Deletar um endereço por id")
	public ResponseEntity<?> deletarEndereco(@PathVariable Long enderecoId, HttpServletRequest request) {
		try {
			enderecoService.deletarEnderecoPorIdToken(enderecoId);
			return ResponseEntity.noContent().build();
			
		} catch (ValidacaoException e) {
			HttpStatus status = HttpStatus.NOT_FOUND;
			return ResponseEntity.status(status).body(new MensagemErroDTO(status, e, request));
		}
	}
	
	@GetMapping("/cliente/endereco")
	@Operation(summary = "Pegar todos endereços do cliente")
	public ResponseEntity<?> pegarEnderecosClientePorIdToken(HttpServletRequest request) {
		try {
			List<MostrarEnderecoDTO> mostrarEnderecoDTO = enderecoService.pegarEnderecosClientePorIdToken();
			return ResponseEntity.status(HttpStatus.OK).body(mostrarEnderecoDTO);
			
		} catch (ValidacaoException e) {
			HttpStatus status = HttpStatus.NOT_FOUND;
			return ResponseEntity.status(status).body(new MensagemErroDTO(status, e, request));
		}
	}
	
	@GetMapping("/cliente/endereco/{enderecoId}")
	@Operation(summary = "Pegar endereço do cliente por id")
	public ResponseEntity<?> pegarEnderecoPorId(@PathVariable Long enderecoId, HttpServletRequest request) {
		try {
			MostrarEnderecoDTO mostrarEnderecoDTO = enderecoService.pegarEnderecoPorId(enderecoId);
			return ResponseEntity.status(HttpStatus.OK).body(mostrarEnderecoDTO);
			
		} catch (ValidacaoException e) {
			HttpStatus status = HttpStatus.NOT_FOUND;
			return ResponseEntity.status(status).body(new MensagemErroDTO(status, e, request));
		}
	}
	
	@PostMapping("/cliente/endereco")
	@Operation(summary = "Cadastrar novo endereço para o cliente")
	public ResponseEntity<MostrarEnderecoDTO> cadastrarNovoEndereco(@RequestBody @Valid CadastrarEnderecoDTO novoEnderecoDTO) {
		MostrarEnderecoDTO mostrarEnderecoDTO = enderecoService.cadastrarNovoEnderecoPorIdToken(novoEnderecoDTO);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(mostrarEnderecoDTO);
	}
	
	@PutMapping("/cliente/endereco/{idEndereco}")
	@Operation(summary = "Alterar um endereço por id")
	public ResponseEntity<?> alterarEnderecoPorIdToken(@PathVariable Long idEndereco, @RequestBody AlterarEnderecoDTO novoEnderecoDTO, HttpServletRequest request) {
		try {
			MostrarEnderecoDTO enderecoDTO = enderecoService.alterarEnderecoPorIdToken(novoEnderecoDTO, idEndereco);
			return ResponseEntity.ok().body(enderecoDTO);
			
		} catch (ValidacaoException e) {
			HttpStatus status = HttpStatus.NOT_FOUND;
			return ResponseEntity.status(status).body(new MensagemErroDTO(status, e, request));
		}
	}
	
}
