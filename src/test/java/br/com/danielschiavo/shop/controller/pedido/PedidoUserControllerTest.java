package br.com.danielschiavo.shop.controller.pedido;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import br.com.danielschiavo.shop.JwtUtilTest;
import br.com.danielschiavo.shop.model.pedido.StatusPedido;
import br.com.danielschiavo.shop.model.pedido.TipoEntrega;
import br.com.danielschiavo.shop.model.pedido.dto.CriarPagamentoDTO;
import br.com.danielschiavo.shop.model.pedido.dto.CriarPedidoDTO;
import br.com.danielschiavo.shop.model.pedido.dto.MostrarPedidoDTO;
import br.com.danielschiavo.shop.model.pedido.dto.MostrarProdutoDoPedidoDTO;
import br.com.danielschiavo.shop.model.pedido.dto.CriarPagamentoDTO.CriarPagamentoDTOBuilder;
import br.com.danielschiavo.shop.model.pedido.dto.CriarPedidoDTO.CriarPedidoDTOBuilder;
import br.com.danielschiavo.shop.model.pedido.dto.MostrarPedidoDTO.MostrarPedidoDTOBuilder;
import br.com.danielschiavo.shop.model.pedido.dto.MostrarProdutoDoPedidoDTO.MostrarProdutoDoPedidoDTOBuilder;
import br.com.danielschiavo.shop.model.pedido.entrega.CriarEntregaDTO;
import br.com.danielschiavo.shop.model.pedido.entrega.MostrarEntregaDTO;
import br.com.danielschiavo.shop.model.pedido.entrega.CriarEntregaDTO.CriarEntregaDTOBuilder;
import br.com.danielschiavo.shop.model.pedido.entrega.MostrarEntregaDTO.MostrarEntregaDTOBuilder;
import br.com.danielschiavo.shop.model.pedido.itempedido.AdicionarItemPedidoDTO;
import br.com.danielschiavo.shop.model.pedido.itempedido.AdicionarItemPedidoDTO.AdicionarItemPedidoDTOBuilder;
import br.com.danielschiavo.shop.model.pedido.pagamento.MetodoPagamento;
import br.com.danielschiavo.shop.model.pedido.pagamento.MostrarPagamentoDTO;
import br.com.danielschiavo.shop.model.pedido.pagamento.StatusPagamento;
import br.com.danielschiavo.shop.model.pedido.pagamento.MostrarPagamentoDTO.MostrarPagamentoDTOBuilder;
import br.com.danielschiavo.shop.service.pedido.PedidoUserService;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ActiveProfiles("dev")
class PedidoUserControllerTest {

	@Autowired
	private MockMvc mvc;
	
	private String tokenUser = JwtUtilTest.generateTokenUser();
	
    @MockBean
    private PedidoUserService pedidoUserService;
    
	@Autowired
	private JacksonTester<Page<MostrarPedidoDTO>> pageMostrarPedidoDTOJson;
	
	@Autowired
	private JacksonTester<MostrarPedidoDTO> mostrarPedidoDTOJson;
	
	@Autowired
	private JacksonTester<CriarPedidoDTO> criarPedidoDTOJson;
	
	private MostrarEntregaDTOBuilder mostrarEntregaDTOBuilder = MostrarEntregaDTO.builder();
	private MostrarPagamentoDTOBuilder mostrarPagamentoDTOBuilder = MostrarPagamentoDTO.builder();
	private MostrarProdutoDoPedidoDTOBuilder mostrarProdutoDoPedidoDTOBuilder = MostrarProdutoDoPedidoDTO.builder();
	private MostrarPedidoDTOBuilder mostrarPedidoDTOBuilder = MostrarPedidoDTO.builder();
	
	private CriarPagamentoDTOBuilder criarPagamentoDTOBuilder = CriarPagamentoDTO.builder();
	private CriarEntregaDTOBuilder criarEntregaDTOBuilder = CriarEntregaDTO.builder();
	private	AdicionarItemPedidoDTOBuilder adicionarItemPedidoDTOBuilder = AdicionarItemPedidoDTO.builder();
	private CriarPedidoDTOBuilder criarPedidoDTOBuilder = CriarPedidoDTO.builder();
	
	@Test
	@DisplayName("Pegar pedidos cliente por id token deve retornar http 200 quando token enviado é válido")
	void pegarPedidosClientePorIdToken_ClienteValido_DeveRetornarOk() throws IOException, Exception {
		//ARRANGE
		MostrarEntregaDTO mostrarEntregaDTO = mostrarEntregaDTOBuilder.tipoEntrega(TipoEntrega.RETIRADA_NA_LOJA).build();
		MostrarPagamentoDTO mostrarPagamentoDTO = mostrarPagamentoDTOBuilder.metodoPagamento(MetodoPagamento.BOLETO).statusPagamento(StatusPagamento.PENDENTE).build();
		byte[] bytesImagem = "Hello world".getBytes();
		MostrarProdutoDoPedidoDTO mostrarProdutoDoPedidoDTO = mostrarProdutoDoPedidoDTOBuilder.idProduto(1L).nomeProduto("Produto 1").preco(BigDecimal.valueOf(400.00)).quantidade(2).subTotal(BigDecimal.valueOf(800.00)).primeiraImagem(bytesImagem).build();
		MostrarProdutoDoPedidoDTO mostrarProdutoDoPedidoDTO2 = mostrarProdutoDoPedidoDTOBuilder.idProduto(2L).nomeProduto("Produto 2").preco(BigDecimal.valueOf(200.00)).quantidade(2).subTotal(BigDecimal.valueOf(400.00)).primeiraImagem(bytesImagem).build();
		Long idCliente = 2L;
		MostrarPedidoDTO mostrarPedidoDTO = mostrarPedidoDTOBuilder.idPedido(UUID.randomUUID()).idCliente(idCliente).valorTotal(BigDecimal.valueOf(1200.00)).dataPedido(LocalDateTime.now()).statusPedido(StatusPedido.A_PAGAR).entrega(mostrarEntregaDTO).pagamento(mostrarPagamentoDTO).produtos(new ArrayList<>(List.of(mostrarProdutoDoPedidoDTO, mostrarProdutoDoPedidoDTO2))).build();
		Page<MostrarPedidoDTO> pageMostrarPedidoDTO = new PageImpl<>(List.of(mostrarPedidoDTO));
		when(pedidoUserService.pegarPedidosClientePorIdToken(any())).thenReturn(pageMostrarPedidoDTO);
		
		//ACT
		var response = mvc.perform(get("/shop/cliente/pedido")
								  .header("Authorization", "Bearer " + tokenUser))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        var jsonEsperado = pageMostrarPedidoDTOJson.write(pageMostrarPedidoDTO).getJson();
        JSONAssert.assertEquals(jsonEsperado, response.getContentAsString(), JSONCompareMode.LENIENT);
	}
	
	@Test
	@DisplayName("Pegar pedidos cliente por id token deve retornar http 403 quando token não é enviado")
	void pegarPedidosClientePorIdToken_TokenNaoEnviado_DeveRetornarForbidden() throws IOException, Exception {
		var response = mvc.perform(get("/shop/cliente/pedido"))
								  .andReturn().getResponse();
		
		assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
	@Test
	@DisplayName("Criar pedido por id token deve retornar http 200 quando token e dto enviado são válidos")
	void criarPedidoPorIdToken_PeloCarrinhoETokenValido_DeveRetornarOk() throws IOException, Exception {
		//ARRANGE
		MostrarEntregaDTO mostrarEntregaDTO = mostrarEntregaDTOBuilder.tipoEntrega(TipoEntrega.RETIRADA_NA_LOJA).build();
		MostrarPagamentoDTO mostrarPagamentoDTO = mostrarPagamentoDTOBuilder.metodoPagamento(MetodoPagamento.BOLETO).statusPagamento(StatusPagamento.PENDENTE).build();
		byte[] bytesImagem = "Hello world".getBytes();
		MostrarProdutoDoPedidoDTO mostrarProdutoDoPedidoDTO = mostrarProdutoDoPedidoDTOBuilder.idProduto(1L).nomeProduto("Produto 1").preco(BigDecimal.valueOf(400.00)).quantidade(2).subTotal(BigDecimal.valueOf(800.00)).primeiraImagem(bytesImagem).build();
		MostrarProdutoDoPedidoDTO mostrarProdutoDoPedidoDTO2 = mostrarProdutoDoPedidoDTOBuilder.idProduto(2L).nomeProduto("Produto 2").preco(BigDecimal.valueOf(200.00)).quantidade(2).subTotal(BigDecimal.valueOf(400.00)).primeiraImagem(bytesImagem).build();
		Long idCliente = 2L;
		MostrarPedidoDTO mostrarPedidoDTO = mostrarPedidoDTOBuilder.idPedido(UUID.randomUUID()).idCliente(idCliente).valorTotal(BigDecimal.valueOf(1200.00)).dataPedido(LocalDateTime.now()).statusPedido(StatusPedido.A_PAGAR).entrega(mostrarEntregaDTO).pagamento(mostrarPagamentoDTO).produtos(new ArrayList<>(List.of(mostrarProdutoDoPedidoDTO, mostrarProdutoDoPedidoDTO2))).build();
		when(pedidoUserService.criarPedidoPorIdToken(any())).thenReturn(mostrarPedidoDTO);
		
		//ACT
		CriarPagamentoDTO criarPagamentoDTO = criarPagamentoDTOBuilder.metodoPagamento(MetodoPagamento.BOLETO).build();
		CriarEntregaDTO criarEntregaDTO = criarEntregaDTOBuilder.tipoEntrega(TipoEntrega.RETIRADA_NA_LOJA).build();
		AdicionarItemPedidoDTO adicionarItemPedidoDTO = adicionarItemPedidoDTOBuilder.idProduto(1L).quantidade(2).build();
		AdicionarItemPedidoDTO adicionarItemPedidoDTO2 = adicionarItemPedidoDTOBuilder.idProduto(2L).quantidade(2).build();
		CriarPedidoDTO criarPedidoDTO = criarPedidoDTOBuilder.pagamento(criarPagamentoDTO).entrega(criarEntregaDTO).veioPeloCarrinho(true).items(new ArrayList<>(List.of(adicionarItemPedidoDTO, adicionarItemPedidoDTO2))).build();
		var response = mvc.perform(post("/shop/cliente/pedido")
								  .header("Authorization", "Bearer " + tokenUser)
								  .contentType(MediaType.APPLICATION_JSON)
								  .content(criarPedidoDTOJson.write(criarPedidoDTO).getJson()))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		var jsonEsperado = mostrarPedidoDTOJson.write(mostrarPedidoDTO).getJson();
        JSONAssert.assertEquals(jsonEsperado, response.getContentAsString(), JSONCompareMode.LENIENT);
	}
	
	@Test
	@DisplayName("Criar pedido por id token deve retornar http 200 quando token e dto enviado são válidos")
	void criarPedidoPorIdToken_BotaoComprarAgora_DeveRetornarOk() throws IOException, Exception {
		//ARRANGE
		MostrarEntregaDTO mostrarEntregaDTO = mostrarEntregaDTOBuilder.tipoEntrega(TipoEntrega.RETIRADA_NA_LOJA).build();
		MostrarPagamentoDTO mostrarPagamentoDTO = mostrarPagamentoDTOBuilder.metodoPagamento(MetodoPagamento.BOLETO).statusPagamento(StatusPagamento.PENDENTE).build();
		byte[] bytesImagem = "Hello world".getBytes();
		MostrarProdutoDoPedidoDTO mostrarProdutoDoPedidoDTO = mostrarProdutoDoPedidoDTOBuilder.idProduto(1L).nomeProduto("Produto 1").preco(BigDecimal.valueOf(400.00)).quantidade(2).subTotal(BigDecimal.valueOf(800.00)).primeiraImagem(bytesImagem).build();
		MostrarProdutoDoPedidoDTO mostrarProdutoDoPedidoDTO2 = mostrarProdutoDoPedidoDTOBuilder.idProduto(2L).nomeProduto("Produto 2").preco(BigDecimal.valueOf(200.00)).quantidade(2).subTotal(BigDecimal.valueOf(400.00)).primeiraImagem(bytesImagem).build();
		Long idCliente = 2L;
		MostrarPedidoDTO mostrarPedidoDTO = mostrarPedidoDTOBuilder.idPedido(UUID.randomUUID()).idCliente(idCliente).valorTotal(BigDecimal.valueOf(1200.00)).dataPedido(LocalDateTime.now()).statusPedido(StatusPedido.A_PAGAR).entrega(mostrarEntregaDTO).pagamento(mostrarPagamentoDTO).produtos(new ArrayList<>(List.of(mostrarProdutoDoPedidoDTO, mostrarProdutoDoPedidoDTO2))).build();
		when(pedidoUserService.criarPedidoPorIdToken(any())).thenReturn(mostrarPedidoDTO);
		
		//ACT
		CriarPagamentoDTO criarPagamentoDTO = criarPagamentoDTOBuilder.metodoPagamento(MetodoPagamento.BOLETO).build();
		CriarEntregaDTO criarEntregaDTO = criarEntregaDTOBuilder.tipoEntrega(TipoEntrega.RETIRADA_NA_LOJA).build();
		AdicionarItemPedidoDTO adicionarItemPedidoDTO = adicionarItemPedidoDTOBuilder.idProduto(1L).quantidade(2).build();
		CriarPedidoDTO criarPedidoDTO = criarPedidoDTOBuilder.pagamento(criarPagamentoDTO).entrega(criarEntregaDTO).veioPeloCarrinho(false).items(new ArrayList<>(List.of(adicionarItemPedidoDTO))).build();
		var response = mvc.perform(post("/shop/cliente/pedido")
								  .header("Authorization", "Bearer " + tokenUser)
								  .contentType(MediaType.APPLICATION_JSON)
								  .content(criarPedidoDTOJson.write(criarPedidoDTO).getJson()))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        var jsonEsperado = mostrarPedidoDTOJson.write(mostrarPedidoDTO).getJson();
        JSONAssert.assertEquals(jsonEsperado, response.getContentAsString(), JSONCompareMode.LENIENT);
	}
	
	@Test
	@DisplayName("Criar pedido botao comprar agora e comprar do carrinho por id token deve retornar http 403 quando token não é enviado")
	void criarPedidoPorIdToken_TokenNaoEnviado_DeveRetornarForbidden() throws IOException, Exception {
		//ACT
		var response = mvc.perform(post("/shop/cliente/pedido"))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
	}
}
