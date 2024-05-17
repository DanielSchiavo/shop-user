package br.com.danielschiavo.shop.controller.pedido;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.danielschiavo.shop.model.pedido.dto.CriarPedidoDTO;
import br.com.danielschiavo.shop.model.pedido.dto.MostrarPedidoDTO;
import br.com.danielschiavo.shop.service.pedido.PedidoUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Pedido - User", description = "Todos endpoints relacionados com os pedidos do cliente, que o próprio poderá utilizar")
public class PedidoUserController {

	@Autowired
	private PedidoUserService pedidoService;
	
	@GetMapping("/cliente/pedido")
	@Operation(summary = "Pega todos os pedidos do cliente")
	public ResponseEntity<Page<MostrarPedidoDTO>> pegarPedidosClientePorIdToken(Pageable pageable) {
		Page<MostrarPedidoDTO> pagePedidosAConfirmar = pedidoService.pegarPedidosClientePorIdToken(pageable);
		return ResponseEntity.ok(pagePedidosAConfirmar);
	}
	
	@PostMapping("/cliente/pedido")
	@Operation(summary = "Cria um pedido em nome do cliente autenticado que está no token")
	public ResponseEntity<?> criarPedidoPorIdToken(@RequestBody @Valid CriarPedidoDTO pedidoDTO) {
		try {
			MostrarPedidoDTO mostrarPedidoDTO = pedidoService.criarPedidoPorIdToken(pedidoDTO);
			return ResponseEntity.ok(mostrarPedidoDTO);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getCause().getMessage());
		}
		
	}	
}
