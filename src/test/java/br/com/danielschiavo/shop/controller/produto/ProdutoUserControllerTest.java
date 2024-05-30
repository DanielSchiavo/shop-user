package br.com.danielschiavo.shop.controller.produto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import br.com.danielschiavo.shop.model.filestorage.ArquivoInfoDTO;
import br.com.danielschiavo.shop.model.produto.dto.DetalharProdutoDTO;
import br.com.danielschiavo.shop.model.produto.dto.DetalharProdutoDTO.DetalharProdutoDTOBuilder;
import br.com.danielschiavo.shop.model.produto.dto.MostrarProdutosDTO;
import br.com.danielschiavo.shop.model.produto.dto.MostrarProdutosDTO.MostrarProdutosDTOBuilder;
import br.com.danielschiavo.shop.service.produto.ProdutoUserService;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ActiveProfiles("dev")
class ProdutoUserControllerTest {
	
	@Autowired
	private MockMvc mvc;
	
    @MockBean
    private ProdutoUserService produtoUserService;
    
	@Autowired
	private JacksonTester<Page<MostrarProdutosDTO>> pageMostrarProdutosDTOJson;
	
	@Autowired
	private JacksonTester<DetalharProdutoDTO> detalharProdutoDTOJson;
	
	private MostrarProdutosDTOBuilder mostrarProdutosDTOBuilder = MostrarProdutosDTO.builder();
	
	private DetalharProdutoDTOBuilder detalharProdutoDTOBuilderBuilder = DetalharProdutoDTO.builder();;
	
	@Test
	@DisplayName("Listar produtos deve retornar codigo http 200")
	void listarProdutos_DeveRetornarOk() throws IOException, Exception {
		//ARRANGE
		MostrarProdutosDTO mostrarProdutosDTO = mostrarProdutosDTOBuilder
													.id(1L)
													.nome("Produto1")
													.preco(BigDecimal.valueOf(200.00))
													.quantidade(5)
													.ativo(true)
													.primeiraImagem("Padrao.jpeg").build();
		Page<MostrarProdutosDTO> pageProdutos = new PageImpl<>(List.of(mostrarProdutosDTO));
		when(produtoUserService.listarProdutos(any())).thenReturn(pageProdutos);
		
		//ACT
		var response = mvc.perform(get("/shop/publico/produto"))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        var jsonEsperado = pageMostrarProdutosDTOJson.write(pageProdutos).getJson();
        JSONAssert.assertEquals(jsonEsperado, response.getContentAsString(), JSONCompareMode.LENIENT);
	}
	
	@Test
	@DisplayName("Detalhar produto deve retornar codigo http 200")
	void detalharProduto_DeveRetornarOk() throws IOException, Exception {
		//ARRANGE
		byte[] bytes = "Hello world".getBytes();
		ArquivoInfoDTO arquivoInfoDTO = new ArquivoInfoDTO("NomeArquivo.jpeg", bytes);
		ArquivoInfoDTO arquivoInfoDTO2 = new ArquivoInfoDTO("NomeVideo.avi", bytes);
		List<ArquivoInfoDTO> listaArquivoInfoDTO = new ArrayList<>(List.of(arquivoInfoDTO, arquivoInfoDTO2));
		DetalharProdutoDTO detalharProdutoDTO = detalharProdutoDTOBuilderBuilder
															.id(1L)
															.nome("Nome produto")
															.descricao("descricao")
															.preco(BigDecimal.valueOf(200,00))
															.quantidade(5)
															.ativo(true)
															.subCategoria(1L)
															.arquivos(listaArquivoInfoDTO).build();
		when(produtoUserService.detalharProdutoPorId(any())).thenReturn(detalharProdutoDTO);
		
		//ACT
		Long idProduto = 1L;
		var response = mvc.perform(get("/shop/publico/produto/{idProduto}", idProduto))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        var jsonEsperado = detalharProdutoDTOJson.write(detalharProdutoDTO).getJson();
        JSONAssert.assertEquals(jsonEsperado, response.getContentAsString(), JSONCompareMode.LENIENT);
	}
}
