package br.com.danielschiavo.shop.controller.produto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.io.IOException;
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

import br.com.danielschiavo.shop.model.produto.categoria.MostrarCategoriaComTodasSubCategoriaDTO;
import br.com.danielschiavo.shop.model.produto.categoria.MostrarCategoriaComTodasSubCategoriaDTO.MostrarCategoriaComTodasSubCategoriaDTOBuilder;
import br.com.danielschiavo.shop.service.produto.CategoriaUserService;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ActiveProfiles("dev")
class CategoriaUserControllerTest {
	
	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private JacksonTester<Page<MostrarCategoriaComTodasSubCategoriaDTO>> pageMostrarCategoriaComTodasSubCategoriaDTOJson;
	
	@MockBean
	private CategoriaUserService categoriaService;
	
	private MostrarCategoriaComTodasSubCategoriaDTOBuilder categoriaBuilder = MostrarCategoriaComTodasSubCategoriaDTO.builder();
	
	@Test
	@DisplayName("Listar categorias deve retornar codigo http 200 quando qualquer usuário solicitar o endpoint")
	void listarCategorias_QualquerUsuario_DeveRetornarOk() throws IOException, Exception {
		//ARRANGE
		List<MostrarCategoriaComTodasSubCategoriaDTO> categorias = categoriaBuilder
				.categoria(1L, "Computadores")
							.comSubCategoria(1L, "Teclado")
							.comSubCategoria(2L, "Mouse")
							.comSubCategoria(3L, "SSD")
							.comSubCategoria(4L, "Placa de Video")
				.categoria(2L, "Softwares")
							.comSubCategoria(5L, "Sistema Administrativo")
							.comSubCategoria(6L, "Automacao")
				.getCategorias();
		Page<MostrarCategoriaComTodasSubCategoriaDTO> pageCategorias = new PageImpl<>(categorias);
		when(categoriaService.listarCategorias(any())).thenReturn(pageCategorias);
		
		//ACT
		var response = mvc.perform(get("/shop/publico/categoria"))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        var jsonEsperado = pageMostrarCategoriaComTodasSubCategoriaDTOJson.write(pageCategorias).getJson();
        JSONAssert.assertEquals(jsonEsperado, response.getContentAsString(), JSONCompareMode.LENIENT);
	}
}
