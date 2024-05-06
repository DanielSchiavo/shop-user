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
import br.com.danielschiavo.shop.model.cliente.endereco.AlterarEnderecoDTO;
import br.com.danielschiavo.shop.model.cliente.endereco.CadastrarEnderecoDTO;
import br.com.danielschiavo.shop.model.cliente.endereco.MostrarEnderecoDTO;
import br.com.danielschiavo.shop.model.cliente.endereco.AlterarEnderecoDTO.AlterarEnderecoDTOBuilder;
import br.com.danielschiavo.shop.model.cliente.endereco.CadastrarEnderecoDTO.CadastrarEnderecoDTOBuilder;
import br.com.danielschiavo.shop.model.cliente.endereco.MostrarEnderecoDTO.MostrarEnderecoDTOBuilder;
import br.com.danielschiavo.shop.service.cliente.EnderecoUserService;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ActiveProfiles("dev")
class EnderecoUserControllerTest {
	
	@Autowired
	private MockMvc mvc;
	
	private String tokenUser = JwtUtilTest.generateTokenUser();
	
    @MockBean
    private EnderecoUserService enderecoService;
    
	@Autowired
	private JacksonTester<List<MostrarEnderecoDTO>> listMostrarEnderecoDTOJson;
	
	@Autowired
	private JacksonTester<MostrarEnderecoDTO> mostrarEnderecoDTOJson;
	
	@Autowired
	private JacksonTester<CadastrarEnderecoDTO> cadastrarEnderecoDTOJson;
	
	@Autowired
	private JacksonTester<AlterarEnderecoDTO> alterarEnderecoDTOJson;
	
	private MostrarEnderecoDTOBuilder MostrarEnderecoDTOBuilder = MostrarEnderecoDTO.builder();
	
	private CadastrarEnderecoDTOBuilder cadastrarEnderecoDTOBuilder = CadastrarEnderecoDTO.builder();
	
	private AlterarEnderecoDTOBuilder alterarEnderecoDTOBuilder = AlterarEnderecoDTO.builder();

	@Test
	@DisplayName("Deletar endereco por id token deve retornar http 204 quando token e idEndereco válidos são enviados")
	void deletarEnderecoPorIdToken_TokenValido_DeveRetornarNoContent() throws IOException, Exception {
		//ARRANGE
		doNothing().when(enderecoService).deletarEnderecoPorIdToken(any());
		
		//ACT
		Long idEndereco = 1L;
		var response = mvc.perform(delete("/shop/cliente/endereco/{idEndereco}", idEndereco)
								  .header("Authorization", "Bearer " + tokenUser))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}
	
	@Test
	@DisplayName("Deletar endereco por id token deve retornar http 403 quando token não é enviado")
	void deletarEnderecoPorIdToken_TokenNaoEnviado_DeveRetornarForbidden() throws IOException, Exception {
		//ACT
		Long idEndereco = 1L;
		var response = mvc.perform(delete("/shop/cliente/endereco/{idEndereco}", idEndereco))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
	@Test
	@DisplayName("Pegar enderecos cliente por id token deve retornar codigo http 200 quando enviado token correto")
	void pegarEnderecosClientePorIdToken_TokenValido_DeveRetornarOk() throws IOException, Exception {
		//ARRANGE
		MostrarEnderecoDTO mostrarEnderecoDTO = MostrarEnderecoDTOBuilder.id(1L).cep("29142321").rua("OnomeEesse").numero("15").bairro("Bela vista").cidade("Cariacica").estado("es").enderecoPadrao(true).build();
		MostrarEnderecoDTO mostrarEnderecoDTO2 = MostrarEnderecoDTOBuilder.id(2L).cep("29142123").rua("Luciano das neves").numero("3233").bairro("Itapua").cidade("Vila velha").estado("es").enderecoPadrao(false).build();
		List<MostrarEnderecoDTO> lista = new ArrayList<>(List.of(mostrarEnderecoDTO, mostrarEnderecoDTO2));
		when(enderecoService.pegarEnderecosClientePorIdToken()).thenReturn(lista);
		
		//ACT
		var response = mvc.perform(get("/shop/cliente/endereco")
								  .header("Authorization", "Bearer " + tokenUser))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        var jsonEsperado = listMostrarEnderecoDTOJson.write(lista).getJson();
        assertThat(response.getContentAsString()).isEqualTo(jsonEsperado);
	}
	
	@Test
	@DisplayName("Pegar enderecos cliente por id token deve retornar codigo http 403 quando token não é enviado")
	void pegarEnderecosClientePorIdToken_TokenNaoEnviado_DeveRetornarForbidden() throws IOException, Exception {
		//ACT
		var response = mvc.perform(get("/shop/cliente/endereco"))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
	@Test
	@DisplayName("Cadastrar novo endereco deve retornar http 201 quando dto e token são validos")
	void cadastrarNovoEndereco_CadastroETokenValido_DeveRetornarCreated() throws IOException, Exception {
		//ARRANGE
		MostrarEnderecoDTO mostrarEnderecoDTO = MostrarEnderecoDTOBuilder.cep("29142298").rua("divinopolis").numero("35").complemento("Ao lado da casa amarela").bairro("Bela vista").cidade("Cariacica").estado("ES").enderecoPadrao(true).build();
		when(enderecoService.cadastrarNovoEnderecoPorIdToken(any())).thenReturn(mostrarEnderecoDTO);
		
		//ACT
		CadastrarEnderecoDTO cadastrarEnderecoDTO = cadastrarEnderecoDTOBuilder.cep("29142298").rua("divinopolis").numero("35").complemento("Ao lado da casa amarela").bairro("Bela vista").cidade("Cariacica").estado("ES").enderecoPadrao(true).build();
		var response = mvc.perform(post("/shop/cliente/endereco")
								  .header("Authorization", "Bearer " + tokenUser)
								  .contentType(MediaType.APPLICATION_JSON)
								  .content(cadastrarEnderecoDTOJson.write(cadastrarEnderecoDTO).getJson()))
						.andReturn().getResponse();
		
		assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
		
        var jsonEsperado = mostrarEnderecoDTOJson.write(mostrarEnderecoDTO).getJson();

        assertThat(response.getContentAsString()).isEqualTo(jsonEsperado);
	}
	
	@Test
	@DisplayName("Cadastrar novo endereco deve retornar http 403 quando token não é enviado")
	void cadastrarNovoEndereco_TokenNaoEnviado_DeveRetornarForbidden() throws IOException, Exception {
		var response = mvc.perform(post("/shop/cliente/endereco"))
						.andReturn().getResponse();
		
		assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
	@Test
	@DisplayName("Alterar endereco cliente por id token deve retornar http 200 quando informacoes estão válidas")
	void alterarEnderecoPorIdToken_DtoETokenValido_DeveRetornarOk() throws IOException, Exception {
		//ARRANGE
		MostrarEnderecoDTO mostrarEnderecoDTO = MostrarEnderecoDTOBuilder.cep("29142298").rua("divinopolis").numero("35").complemento("Ao lado da casa amarela").bairro("Bela vista").cidade("Cariacica").estado("ES").enderecoPadrao(true).build();
		when(enderecoService.alterarEnderecoPorIdToken(any(), any())).thenReturn(mostrarEnderecoDTO);
		
		//ACT
		AlterarEnderecoDTO alterarEnderecoDTO = alterarEnderecoDTOBuilder.cep("29142298").rua("divinopolis").numero("35").complemento("Ao lado da casa amarela").bairro("Bela vista").cidade("Cariacica").estado("ES").enderecoPadrao(true).build();
		Long idEndereco = 1L;
		var response = mvc.perform(put("/shop/cliente/endereco/{idEndereco}", idEndereco)
								  .header("Authorization", "Bearer " + tokenUser)
								  .contentType(MediaType.APPLICATION_JSON)
								  .content(alterarEnderecoDTOJson.write(alterarEnderecoDTO).getJson()))
						.andReturn().getResponse();
		
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		
        var jsonEsperado = mostrarEnderecoDTOJson.write(mostrarEnderecoDTO).getJson();

        assertThat(response.getContentAsString()).isEqualTo(jsonEsperado);
	}
	
	@Test
	@DisplayName("Alterar endereco cliente por id token deve retornar http 403 quando nenhum token for enviado")
	void alterarEnderecoPorIdToken_TokenNaoEnviado_DeveRetornarForbidden() throws IOException, Exception {
		Long idEndereco = 1L;
		var response = mvc.perform(put("/shop/cliente/endereco/{idEndereco}", idEndereco)
								  .contentType(MediaType.APPLICATION_JSON))
						.andReturn().getResponse();
		
		assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
	}

}
