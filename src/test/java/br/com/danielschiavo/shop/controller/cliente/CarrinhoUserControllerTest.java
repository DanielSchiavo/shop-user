package br.com.danielschiavo.shop.controller.cliente;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import br.com.danielschiavo.shop.JwtUtilTest;
import br.com.danielschiavo.shop.model.cliente.carrinho.MostrarCarrinhoClienteDTO;
import br.com.danielschiavo.shop.model.cliente.carrinho.MostrarCarrinhoClienteDTO.MostrarCarrinhoClienteDTOBuilder;
import br.com.danielschiavo.shop.model.cliente.carrinho.itemcarrinho.AdicionarItemCarrinhoDTO;
import br.com.danielschiavo.shop.model.cliente.carrinho.itemcarrinho.AdicionarItemCarrinhoDTO.AdicionarItemCarrinhoDTOBuilder;
import br.com.danielschiavo.shop.model.cliente.carrinho.itemcarrinho.MostrarItemCarrinhoClienteDTO;
import br.com.danielschiavo.shop.model.cliente.carrinho.itemcarrinho.MostrarItemCarrinhoClienteDTO.MostrarItemCarrinhoClienteDTOBuilder;
import br.com.danielschiavo.shop.service.cliente.CarrinhoUserService;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ActiveProfiles("dev")
class CarrinhoUserControllerTest {
	
	@Autowired
	private MockMvc mvc;
	
	private String tokenUser = JwtUtilTest.generateTokenUser();
	
	@MockBean
	private CarrinhoUserService carrinhoService;
	
	@Autowired
	private JacksonTester<MostrarCarrinhoClienteDTO> mostrarCarrinhoClienteDTOJson;
	
	@Autowired
	private JacksonTester<AdicionarItemCarrinhoDTO> itemCarrinhoDTOJson;
	
	private MostrarItemCarrinhoClienteDTOBuilder mostrarItemCarrinhoClienteDtoBuilder = MostrarItemCarrinhoClienteDTO.builder();
	
	private MostrarCarrinhoClienteDTOBuilder mostrarCarrinhoClienteDTOBuilder = MostrarCarrinhoClienteDTO.builder();

	private AdicionarItemCarrinhoDTOBuilder adicionarItemCarrinhoDTOBuilder = AdicionarItemCarrinhoDTO.builder();
	
	@Test
	@DisplayName("Deletar produto no carrinho deve retornar http 204 quando token e idProduto válidos são enviados")
	void deletarProdutoNoCarrinhoPorIdToken_TokenValido_DeveRetornarOkNoContent() throws IOException, Exception {
		//ARRANGE
		doNothing().when(carrinhoService).deletarProdutoNoCarrinhoPorIdToken(any());
		
		//ACT
		Long idProduto = 1L;
		var response = mvc.perform(delete("/shop/cliente/carrinho/{idProduto}", idProduto)
								  .header("Authorization", "Bearer " + tokenUser))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}
	
	@Test
	@DisplayName("Deletar produto no carrinho deve retornar http 403 quando token não é enviado")
	void deletarProdutoNoCarrinhoPorIdToken_TokenNaoEnviado_DeveRetornarForbidden() throws IOException, Exception {
		//ACT
		Long idProduto = 1L;
		var response = mvc.perform(delete("/shop/cliente/carrinho/{idProduto}", idProduto))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
	@Test
	@DisplayName("Pegar carrinho cliente por id token deve retornar codigo http 200 quando token é valido")
	void pegarCarrinhoClientePorIdToken_TokenValido_DeveRetornarOk() throws IOException, Exception {
		//ARRANGE
		MostrarItemCarrinhoClienteDTO mostrarItemCarrinhoClienteDTO = mostrarItemCarrinhoClienteDtoBuilder.idProduto(1L).nomeProduto("Produto1").quantidade(2).preco(BigDecimal.valueOf(200.00)).imagemProduto(null).build();
		MostrarItemCarrinhoClienteDTO mostrarItemCarrinhoClienteDTO2 = mostrarItemCarrinhoClienteDtoBuilder.idProduto(2L).nomeProduto("Produto2").quantidade(2).preco(BigDecimal.valueOf(300.00)).imagemProduto(null).build();
		
		//ACT
		List<MostrarItemCarrinhoClienteDTO> lista = new ArrayList<>();
		lista.addAll(List.of(mostrarItemCarrinhoClienteDTO, mostrarItemCarrinhoClienteDTO2));
		BigDecimal valorTotal = BigDecimal.ZERO;
		lista.forEach(item -> valorTotal.add(item.preco()));
		MostrarCarrinhoClienteDTO mostrarCarrinhoClienteDTO = mostrarCarrinhoClienteDTOBuilder.id(2L).itemsCarrinho(lista).valorTotal(valorTotal).build();
		when(carrinhoService.pegarCarrinhoClientePorIdToken()).thenReturn(mostrarCarrinhoClienteDTO);
		var response = mvc.perform(get("/shop/cliente/carrinho")
				  				.header("Authorization", "Bearer " + tokenUser))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        var jsonEsperado = mostrarCarrinhoClienteDTOJson.write(mostrarCarrinhoClienteDTO).getJson();
        assertThat(response.getContentAsString()).isEqualTo(jsonEsperado);
	}
	
	@Test
	@DisplayName("Pegar carrinho cliente por id token deve retornar codigo http 403 quando token não é enviado")
	void pegarCarrinhoClientePorIdToken_TokenNaoEnviado_DeveRetornarForbidden() throws IOException, Exception {
		//ACT
		var response = mvc.perform(get("/shop/cliente/carrinho"))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
	@Test
	@DisplayName("Adicionar produtos no carrinho por id token deve retornar http 200 quando token e dto são válidos")
	void adicionarProdutosNoCarrinhoPorIdToken_TokenValido_DeveRetornarOk() throws IOException, Exception {
		//ACT
		AdicionarItemCarrinhoDTO adicionarItemCarrinhoDTO = adicionarItemCarrinhoDTOBuilder.idProduto(1L).quantidade(2).build();
		var response = mvc.perform(post("/shop/cliente/carrinho")
								  .header("Authorization", "Bearer " + tokenUser)
								  .contentType(MediaType.APPLICATION_JSON)
								  .content(itemCarrinhoDTOJson.write(adicionarItemCarrinhoDTO).getJson()))
						.andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
	}
	
	@Test
	@DisplayName("Adicionar produtos no carrinho por id token deve retornar http 403 quando token não é enviado")
	void adicionarProdutosNoCarrinhoPorIdToken_TokenNaoEnviado_DeveRetornarForbidden() throws IOException, Exception {
		//ACT
		var response = mvc.perform(post("/shop/cliente/carrinho"))
						.andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
	@Test
	@DisplayName("Setar quantidade produto no carrinho por id token deve retornar http 200 quando token e corpo validos são informado")
	void setarQuantidadeProdutoNoCarrinhoPorIdToken_TokenEBodyValido_DeveRetornarOk() throws IOException, Exception {
		//ARRANGE
		doNothing().when(carrinhoService).setarQuantidadeProdutoNoCarrinhoPorIdToken(any());
		
		//ACT
		AdicionarItemCarrinhoDTO adicionarItemCarrinhoDTO = adicionarItemCarrinhoDTOBuilder.idProduto(1L).quantidade(2).build();
		var response = mvc.perform(put("/shop/cliente/carrinho")
								  .header("Authorization", "Bearer " + tokenUser)
								  .contentType(MediaType.APPLICATION_JSON)
								  .content(itemCarrinhoDTOJson.write(adicionarItemCarrinhoDTO).getJson()))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
	}
	
	@Test
	@DisplayName("Setar quantidade produto no carrinho por id token deve retornar http 403 quando token não é informado")
	void setarQuantidadeProdutoNoCarrinhoPorIdToken_TokenNaoEnviado_DeveRetornarForbidden() throws IOException, Exception {
		//ACT
		var response = mvc.perform(put("/shop/cliente/carrinho"))
								.andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
	}

}
