package br.com.danielschiavo.shop.controller.cliente;


import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.danielschiavo.shop.model.MensagemErroDTO;
import br.com.danielschiavo.shop.model.ValidacaoException;
import br.com.danielschiavo.shop.model.cliente.dto.AlterarClienteDTO;
import br.com.danielschiavo.shop.model.cliente.dto.AlterarFotoPerfilDTO;
import br.com.danielschiavo.shop.model.cliente.dto.CadastrarClienteDTO;
import br.com.danielschiavo.shop.model.cliente.dto.MostrarClienteDTO;
import br.com.danielschiavo.shop.model.filestorage.ArquivoInfoDTO;
import br.com.danielschiavo.shop.service.cliente.ClienteUserService;
import br.com.danielschiavo.shop.service.cliente.MostrarClientePaginaInicialDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/shop")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Cliente - User", description = "Todos endpoints relacionados com o cliente, que o próprio poderá utilizar")
public class ClienteUserController {

	@Autowired
	private ClienteUserService clienteUserService;
	
	@DeleteMapping("/cliente/foto-perfil")
	@Operation(summary = "Deleta foto do perfil do cliente")
	public ResponseEntity<?> deletarFotoPerfilClientePorIdToken() {
		try {
			clienteUserService.deletarFotoPerfilPorIdToken();
			return ResponseEntity.noContent().build();

		} catch (ValidacaoException e) {
			HttpStatus status = HttpStatus.NOT_FOUND;
			return ResponseEntity.status(status).body(new MensagemErroDTO(status, e));
		} catch (IOException e) {
			HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
			return ResponseEntity.status(status).body(new MensagemErroDTO(status.toString(), "Falha interna no servidor ao tentar excluir o arquivo."));
		}
	}
	
	@GetMapping("/cliente/pagina-inicial")
	public ResponseEntity<?> detalharClientePaginaInicialPorIdToken() {
		MostrarClientePaginaInicialDTO mostrarClientePaginaInicialDTO = clienteUserService.detalharClientePorIdTokenPaginaInicial();
		
		return ResponseEntity.ok(mostrarClientePaginaInicialDTO);
	}

	@GetMapping("/cliente")
	@Operation(summary = "Mostra todos os dados do cliente")
	public ResponseEntity<?> detalharClientePorIdToken() {
		MostrarClienteDTO detalharClienteDTO = clienteUserService.detalharClientePorIdToken();
		
		return ResponseEntity.ok(detalharClienteDTO);
	}
	
	@PostMapping("/publico/cadastrar/cliente")
	@Operation(summary = "Cadastro de cliente")
	public ResponseEntity<?> cadastrarCliente(@RequestBody @Valid CadastrarClienteDTO cadastrarClienteDTO,
			UriComponentsBuilder uriBuilder) {
		try {
			Map<String, Object> respostaCadastrarCliente = clienteUserService.cadastrarCliente(cadastrarClienteDTO);
			var uri = uriBuilder.path("/shop/cliente/{id}").buildAndExpand(respostaCadastrarCliente.get("clienteId")).toUri();
			return ResponseEntity.created(uri).body(respostaCadastrarCliente.get("mensagem"));
		} catch (ValidacaoException e) {
			return ResponseEntity.badRequest().body(new MensagemErroDTO(HttpStatus.BAD_REQUEST, e));
		}
		
	}
	
	@PutMapping("/cliente")
	@Operation(summary = "Cliente altera seus próprios dados")
	public ResponseEntity<?> alterarClientePorIdToken(@RequestBody @Valid AlterarClienteDTO alterarClienteDTO) {
		String respostaAlterarCliente = clienteUserService.alterarClientePorIdToken(alterarClienteDTO);

		return ResponseEntity.ok(respostaAlterarCliente);
	}
	
	@PutMapping("/cliente/foto-perfil")
	@Operation(summary = "Alterar a foto do perfil do cliente")
	public ResponseEntity<?> alterarFotoPerfilPorIdToken(@RequestBody @Valid AlterarFotoPerfilDTO alterarFotoPerfilDTO) {
		try {
			ArquivoInfoDTO arquivoInfoDTO = clienteUserService.alterarFotoPerfilPorIdToken(alterarFotoPerfilDTO);
			return ResponseEntity.ok(arquivoInfoDTO);
			
		} catch (ValidacaoException e) {
			HttpStatus status = HttpStatus.NOT_FOUND;
			return ResponseEntity.status(status).body(new MensagemErroDTO(status, e));
		}
	}
}
