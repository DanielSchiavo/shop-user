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
import java.time.LocalDate;

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

import br.com.danielschiavo.JwtUtilTest;
import br.com.danielschiavo.shop.model.cliente.dto.AlterarClienteDTO;
import br.com.danielschiavo.shop.model.cliente.dto.AlterarClienteDTO.AlterarClienteDTOBuilder;
import br.com.danielschiavo.shop.model.cliente.dto.AlterarFotoPerfilDTO;
import br.com.danielschiavo.shop.model.cliente.dto.CadastrarClienteDTO;
import br.com.danielschiavo.shop.model.cliente.dto.CadastrarClienteDTO.CadastrarClienteDTOBuilder;
import br.com.danielschiavo.shop.model.cliente.dto.MostrarClienteDTO;
import br.com.danielschiavo.shop.model.cliente.dto.MostrarClienteDTO.MostrarClienteDTOBuilder;
import br.com.danielschiavo.shop.model.filestorage.ArquivoInfoDTO;
import br.com.danielschiavo.shop.service.cliente.ClienteUserService;
import br.com.danielschiavo.shop.service.cliente.MostrarClientePaginaInicialDTO;
import br.com.danielschiavo.shop.service.cliente.MostrarClientePaginaInicialDTO.MostrarClientePaginaInicialDTOBuilder;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ActiveProfiles("dev")
class ClienteUserControllerTest {
	
	@Autowired
	private MockMvc mvc;
	
	private String tokenUser = JwtUtilTest.generateTokenUser();
	
	@Autowired
	private JacksonTester<CadastrarClienteDTO> cadastrarClienteDTOJson;
	
	@Autowired
	private JacksonTester<MostrarClientePaginaInicialDTO> mostrarClientePaginaInicialDTOJson;
	
	@Autowired
	private JacksonTester<MostrarClienteDTO> mostrarClienteDTOJson;
	
	@Autowired
	private JacksonTester<AlterarClienteDTO> alterarClienteDTOJson;
	
	@Autowired
	private JacksonTester<AlterarFotoPerfilDTO> alterarFotoPerfilDTOJson;
	
    @MockBean
    private ClienteUserService clienteUserService;

	private MostrarClientePaginaInicialDTOBuilder mostrarClientePaginaInicialDTOBuilder = MostrarClientePaginaInicialDTO.builder();
	
	private MostrarClienteDTOBuilder mostrarClienteDTOBuilder = MostrarClienteDTO.builder();

	private CadastrarClienteDTOBuilder cadastrarClienteDTOBuilder = CadastrarClienteDTO.builder();
	
	private AlterarClienteDTOBuilder alterarClienteDTOBuilder = AlterarClienteDTO.builder();
	
	@Test
	@DisplayName("Deletar foto perfil deve retornar http 204 quando token válido é enviado")
	void deletarFotoPerfil_ClienteValido_DeveRetornarOkNoContent() throws IOException, Exception {
		//ARRANGE
		doNothing().when(clienteUserService).deletarFotoPerfilPorIdToken();
		
		//ACT
		var response = mvc.perform(delete("/shop/cliente/foto-perfil")
								  .header("Authorization", "Bearer " + tokenUser))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}
	
	@Test
	@DisplayName("Deletar foto perfil deve retornar http 403 quando token não é enviado")
	void deletarFotoPerfil_ClienteInvalido_DeveRetornarForbidden() throws IOException, Exception {
		//ARRANGE
		doNothing().when(clienteUserService).deletarFotoPerfilPorIdToken();
		
		//ACT
		var response = mvc.perform(delete("/shop/cliente/foto-perfil"))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
	}
    
	@Test
	@DisplayName("Detalhar cliente página inicial deve retornar codigo http 201 quando token válido é enviado")
	void detalharClientePaginaInicial_ClienteValido_DeveRetornarOk() throws IOException, Exception {
		//ARRANGE
		MostrarClientePaginaInicialDTO mostrarClientePaginaInicialDTO = mostrarClientePaginaInicialDTOBuilder
																			.nome("Daniel")
																			.fotoPerfil(new ArquivoInfoDTO("Padrao.jpeg", "Bytes da imagem padrao".getBytes()))
																			.build();
		when(clienteUserService.detalharClientePorIdTokenPaginaInicial()).thenReturn(mostrarClientePaginaInicialDTO);
		
		//ACT
		var response = mvc.perform(get("/shop/cliente/pagina-inicial")
								  .header("Authorization", "Bearer " + tokenUser))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        var jsonEsperado = mostrarClientePaginaInicialDTOJson.write(mostrarClientePaginaInicialDTO).getJson();
        assertThat(response.getContentAsString()).isEqualTo(jsonEsperado);
	}
	
	@Test
	@DisplayName("Detalhar cliente página inicial deve retornar codigo http 403 quando token não é enviado")
	void detalharClientePaginaInicial_ClienteInvalido_DeveRetornarForbidden() throws IOException, Exception {
		//ACT
		var response = mvc.perform(get("/shop/cliente/pagina-inicial"))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
	}

	@Test
	@DisplayName("Detalhar cliente deve retornar http 200 quando token enviado é válido")
	void detalharCliente_ClienteValido_DeveRetornarOk() throws IOException, Exception {
		//ARRANGE
		MostrarClienteDTO mostrarClienteDTO = mostrarClienteDTOBuilder.nome("Daniel").fotoPerfil(new ArquivoInfoDTO("Padrao.jpeg", "Bytes da imagem padrao".getBytes())).build();
		when(clienteUserService.detalharClientePorIdToken()).thenReturn(mostrarClienteDTO);

		//ACT
		var response = mvc.perform(get("/shop/cliente")
								  .header("Authorization", "Bearer " + tokenUser))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        var jsonEsperado = mostrarClienteDTOJson.write(mostrarClienteDTO).getJson();
        assertThat(response.getContentAsString()).isEqualTo(jsonEsperado);
	}
	
	@Test
	@DisplayName("Detalhar cliente deve retornar http 403 quando token não é enviado")
	void detalharCliente_TokenNaoEnviado_DeveRetornarForbidden() throws IOException, Exception {
		//ACT
		var response = mvc.perform(get("/shop/cliente"))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
	@Test
	@DisplayName("Cadastrar cliente deve retornar http 201 quando informacoes estão válidas")
	void cadastrarCliente_ClienteValido_DeveRetornarCreated() throws IOException, Exception {
		//ARRANGE
		String mensagem = "Cadastrado com sucesso!";
		when(clienteUserService.cadastrarCliente(any())).thenReturn(mensagem);
		
		//ACT
		CadastrarClienteDTO cadastrarClienteDTO = cadastrarClienteDTOBuilder.cpf("12345671012").nome("Junior").sobrenome("da Silva").dataNascimento(LocalDate.of(2000, 3, 3)).email("juniordasilva@gmail.com").senha("123456").celular("27996101055").fotoPerfil("Padrao.jpeg").endereco(null).build();
		var response = mvc.perform(post("/shop/publico/cadastrar/cliente")
								  .contentType(MediaType.APPLICATION_JSON)
								  .content(cadastrarClienteDTOJson.write(cadastrarClienteDTO).getJson()))
						.andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).isEqualTo(mensagem);
	}

    @Test
    @DisplayName("Cadastrar cliente deve retornar http 400 BAD_REQUEST enviando corpo requisição nulo")
    void cadastrarCliente_ClienteInvalido_DeveRetornarBadRequest() throws Exception {
        //ACT
    	var response = mvc.perform(post("/shop/publico/cadastrar/cliente"))
                .andReturn().getResponse();

    	//ASSERT
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

	@Test
	@DisplayName("Alterar cliente deve retornar http 200 quando informacoes estão válidas")
	void alterarClientePorId_ClienteValido_DeveRetornarOk() throws IOException, Exception {
		//ARRANGE
		String mensagem = "Alterado com sucesso!";
		when(clienteUserService.alterarClientePorIdToken(any())).thenReturn(mensagem);
		
		//ACT
		AlterarClienteDTO alterarClienteDTO = alterarClienteDTOBuilder.cpf("12345671012").nome("Junior").sobrenome("da Silva").dataNascimento(LocalDate.of(2000, 3, 3)).email("juniordasilva@gmail.com").senha("123456").celular("27996101055").build();
		var response = mvc.perform(put("/shop/cliente")
								  .header("Authorization", "Bearer " + tokenUser)
								  .contentType(MediaType.APPLICATION_JSON)
								  .content(alterarClienteDTOJson.write(alterarClienteDTO).getJson()))
						.andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(mensagem);
	}
	
	@Test
	@DisplayName("Alterar cliente deve retornar http 403 quando token não é enviado")
	void alterarClientePorId_ClienteInvalido_DeveRetornarForbidden() throws IOException, Exception {
		//ACT
		var response = mvc.perform(put("/shop/cliente")
								  .contentType(MediaType.APPLICATION_JSON))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
	}

	@Test
	@DisplayName("Alterar foto perfil por id token deve retornar codigo http 200 quando token e corpo da requisição válido é enviado")
	void alterarFotoPerfilPorIdToken_ClienteValido_DeveRetornarOk() throws IOException, Exception {
		//ARRANGE
		String mensagem = "Foto alterada com sucesso!";
		when(clienteUserService.alterarFotoPerfilPorIdToken(any())).thenReturn(mensagem);

		//ACT
		var response = mvc.perform(put("/shop/cliente/foto-perfil")
								  .header("Authorization", "Bearer " + tokenUser)
								  .contentType(MediaType.APPLICATION_JSON)
								  .content(alterarFotoPerfilDTOJson.write(
										  new AlterarFotoPerfilDTO("nomeNovaImagem.jpeg"))
										  .getJson()))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(mensagem);
	}
	
	@Test
	@DisplayName("Detalhar cliente página inicial deve retornar codigo http 400 quando token válido e corpo inválido é enviado")
	void alterarFotoPerfilPorIdToken_ClienteInvalido_DeveRetornarBadRequest() throws IOException, Exception {
		//ACT
		var response = mvc.perform(put("/shop/cliente/foto-perfil")
								  .header("Authorization", "Bearer " + tokenUser)
								  .contentType(MediaType.APPLICATION_JSON)
								  .content(alterarFotoPerfilDTOJson.write(
										  new AlterarFotoPerfilDTO(""))
										  .getJson()))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
}
