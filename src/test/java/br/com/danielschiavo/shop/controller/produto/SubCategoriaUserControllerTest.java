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

import br.com.danielschiavo.shop.model.produto.categoria.MostrarCategoriaDTO;
import br.com.danielschiavo.shop.model.produto.subcategoria.MostrarSubCategoriaComCategoriaDTO;
import br.com.danielschiavo.shop.service.produto.SubCategoriaUserService;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ActiveProfiles("dev")
class SubCategoriaUserControllerTest {

	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private JacksonTester<Page<MostrarSubCategoriaComCategoriaDTO>> pageMostrarSubCategoriaComCategoriaDTOJson;
	
	@MockBean
	private SubCategoriaUserService subCategoriaUserService;
	
	@Test
	@DisplayName("Listar categorias deve retornar codigo http 200 quando qualquer usuário solicitar o endpoint")
	void listarSubCategorias_DeveRetornarOk() throws IOException, Exception {
		var categoria = new MostrarCategoriaDTO(null, "Computadores");
		var mostrarSubCategoriaComCategoriaDTO = new MostrarSubCategoriaComCategoriaDTO(null, "Mouses", categoria);
		var categoria2 = new MostrarCategoriaDTO(null, "Celulares");
		var mostrarSubCategoriaComCategoriaDTO2 = new MostrarSubCategoriaComCategoriaDTO(null, "Peliculas", categoria2);
		var pageMostrarSubCategoriaComCategoriaDTO = new PageImpl<>(List.of(mostrarSubCategoriaComCategoriaDTO, mostrarSubCategoriaComCategoriaDTO2));
		when(subCategoriaUserService.listarSubCategorias(any())).thenReturn(pageMostrarSubCategoriaComCategoriaDTO);
		
		var response = mvc.perform(get("/shop/publico/sub-categoria"))
								  		.andReturn().getResponse();
		
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		
        var jsonEsperado = pageMostrarSubCategoriaComCategoriaDTOJson.write(pageMostrarSubCategoriaComCategoriaDTO).getJson();

        JSONAssert.assertEquals(jsonEsperado, response.getContentAsString(), JSONCompareMode.LENIENT);
	}
}
