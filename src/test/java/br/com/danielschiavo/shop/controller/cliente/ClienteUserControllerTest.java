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

import br.com.danielschiavo.JwtUtilTest;
import br.com.danielschiavo.shop.model.cliente.dto.AlterarClienteDTO;
import br.com.danielschiavo.shop.model.cliente.dto.AlterarFotoPerfilDTO;
import br.com.danielschiavo.shop.model.cliente.dto.CadastrarClienteDTO;
import br.com.danielschiavo.shop.model.cliente.dto.MostrarClienteDTO;
import br.com.danielschiavo.shop.model.cliente.dto.AlterarClienteDTO.AlterarClienteDTOBuilder;
import br.com.danielschiavo.shop.model.cliente.dto.CadastrarClienteDTO.CadastrarClienteDTOBuilder;
import br.com.danielschiavo.shop.model.cliente.dto.MostrarClienteDTO.MostrarClienteDTOBuilder;
import br.com.danielschiavo.shop.model.cliente.endereco.CadastrarEnderecoDTO;
import br.com.danielschiavo.shop.model.cliente.endereco.MostrarEnderecoDTO;
import br.com.danielschiavo.shop.model.cliente.endereco.CadastrarEnderecoDTO.CadastrarEnderecoDTOBuilder;
import br.com.danielschiavo.shop.model.cliente.endereco.MostrarEnderecoDTO.MostrarEnderecoDTOBuilder;
import br.com.danielschiavo.shop.model.filestorage.ArquivoInfoDTO;
import br.com.danielschiavo.shop.model.filestorage.ArquivoInfoDTO.ArquivoInfoDTOBuilder;
import br.com.danielschiavo.shop.service.cliente.ClienteUserService;

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
	private JacksonTester<MostrarClienteDTO> mostrarClienteDTOJson;
	
	@Autowired
	private JacksonTester<AlterarClienteDTO> alterarClienteDTOJson;
	
	@Autowired
	private JacksonTester<AlterarFotoPerfilDTO> alterarFotoPerfilDTOJson;
	
	@Autowired
	private JacksonTester<ArquivoInfoDTO> arquivoInfoDTOJson;
	
    @MockBean
    private ClienteUserService clienteUserService;

	private MostrarClienteDTOBuilder mostrarClienteDTOBuilder = MostrarClienteDTO.builder();

	private MostrarEnderecoDTOBuilder mostrarEnderecoDTOBuilder = MostrarEnderecoDTO.builder();
	
	private CadastrarClienteDTOBuilder cadastrarClienteDTOBuilder = CadastrarClienteDTO.builder();
	
	private CadastrarEnderecoDTOBuilder cadastrarEnderecoDTOBuilder = CadastrarEnderecoDTO.builder();
	
	private ArquivoInfoDTOBuilder arquivoInfoDtoBuilder = ArquivoInfoDTO.builder();
	
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
		MostrarClienteDTO mostrarClienteDTO = mostrarClienteDTOBuilder.nome("Daniel").fotoPerfil(new ArquivoInfoDTO("Padrao.jpeg", "Bytes da imagem padrao".getBytes())).build();
		when(clienteUserService.detalharClientePorIdTokenPaginaInicial()).thenReturn(mostrarClienteDTO);
		
		//ACT
		var response = mvc.perform(get("/shop/cliente/pagina-inicial")
								  .header("Authorization", "Bearer " + tokenUser))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        var jsonEsperado = mostrarClienteDTOJson.write(mostrarClienteDTO).getJson();
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
		MostrarEnderecoDTO mostrarEnderecoDTO = mostrarEnderecoDTOBuilder.id(1L).cep("29142298").rua("divinopolis").numero("35").complemento(null).bairro("Bela vista").cidade("Cariacica").estado("ES").enderecoPadrao(true).build();
		MostrarClienteDTO mostrarClienteDTO = mostrarClienteDTOBuilder.nome("Daniel").fotoPerfil(new ArquivoInfoDTO("Padrao.jpeg", "Bytes da imagem padrao".getBytes())).enderecos(new ArrayList<>(List.of(mostrarEnderecoDTO))).build();
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
	void detalharCliente_ClienteInvalido_DeveRetornarForbidden() throws IOException, Exception {
		//ACT
		var response = mvc.perform(get("/shop/cliente"))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
	@Test
	@DisplayName("Cadastrar cliente deve retornar http 201 quando informacoes estão válidas sem endereço")
	void cadastrarCliente_ClienteValidoSemEndereco_DeveRetornarCreated() throws IOException, Exception {
		//ARRANGE
		MostrarClienteDTO mostrarClienteDTO = mostrarClienteDTOBuilder.cpf("12345671012").nome("Junior").sobrenome("da Silva").dataNascimento(LocalDate.of(2000, 3, 3)).email("juniordasilva@gmail.com").celular("27996101055").fotoPerfil(new ArquivoInfoDTO("Padrao.jpeg", "Bytes da imagem padrao".getBytes())).enderecos(null).build();
		when(clienteUserService.cadastrarCliente(any())).thenReturn(mostrarClienteDTO);
		
		//ACT
		CadastrarClienteDTO cadastrarClienteDTO = cadastrarClienteDTOBuilder.cpf("12345671012").nome("Junior").sobrenome("da Silva").dataNascimento(LocalDate.of(2000, 3, 3)).email("juniordasilva@gmail.com").senha("123456").celular("27996101055").fotoPerfil("Padrao.jpeg").endereco(null).build();
		var response = mvc.perform(post("/shop/publico/cadastrar/cliente")
								  .contentType(MediaType.APPLICATION_JSON)
								  .content(cadastrarClienteDTOJson.write(cadastrarClienteDTO).getJson()))
						.andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        var jsonEsperado = mostrarClienteDTOJson.write(mostrarClienteDTO).getJson();
        assertThat(response.getContentAsString()).isEqualTo(jsonEsperado);
	}
	
	@Test
	@DisplayName("Cadastrar cliente deve retornar http 201 quando informacoes estão válidas com endereço")
	void cadastrarCliente_ClienteValidoComEndereco_DeveRetornarCreated() throws IOException, Exception {
		//ARRANGE
		MostrarEnderecoDTO mostrarEnderecoDTO = mostrarEnderecoDTOBuilder.id(1L).cep("29142298").rua("divinopolis").numero("35").complemento(null).bairro("Bela vista").cidade("Cariacica").estado("ES").enderecoPadrao(true).build();
		MostrarClienteDTO mostrarClienteDTO = mostrarClienteDTOBuilder.cpf("12345671012").nome("Junior").sobrenome("da Silva").dataNascimento(LocalDate.of(2000, 3, 3)).email("juniordasilva@gmail.com").celular("27996101055").fotoPerfil(new ArquivoInfoDTO("Padrao.jpeg", "Bytes da imagem padrao".getBytes())).enderecos(new ArrayList<>(List.of(mostrarEnderecoDTO))).build();
		when(clienteUserService.cadastrarCliente(any())).thenReturn(mostrarClienteDTO);
		
		//ACT
		
		CadastrarEnderecoDTO cadastrarEnderecoDTO = cadastrarEnderecoDTOBuilder.cep("29142298").rua("divinopolis").numero("35").complemento(null).bairro("Bela vista").cidade("Cariacica").estado("ES").enderecoPadrao(true).build();
		CadastrarClienteDTO cadastrarClienteDTO = cadastrarClienteDTOBuilder.cpf("12345671012").nome("Junior").sobrenome("da Silva").dataNascimento(LocalDate.of(2000, 3, 3)).email("juniordasilva@gmail.com").senha("123456").celular("27996101055").fotoPerfil("Padrao.jpeg").endereco(cadastrarEnderecoDTO).build();
		var response = mvc.perform(post("/shop/publico/cadastrar/cliente")
								  .contentType(MediaType.APPLICATION_JSON)
								  .content(cadastrarClienteDTOJson.write(cadastrarClienteDTO).getJson()))
						.andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        var jsonEsperado = mostrarClienteDTOJson.write(mostrarClienteDTO).getJson();
        assertThat(response.getContentAsString()).isEqualTo(jsonEsperado);
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
		MostrarClienteDTO mostrarClienteDTO = mostrarClienteDTOBuilder.cpf("12345671012").nome("Junior").sobrenome("da Silva").dataNascimento(LocalDate.of(2000, 3, 3)).email("juniordasilva@gmail.com").celular("27996101055").fotoPerfil(new ArquivoInfoDTO("Padrao.jpeg", "Bytes da imagem padrao".getBytes())).build();
		when(clienteUserService.alterarClientePorIdToken(any())).thenReturn(mostrarClienteDTO);
		
		//ACT
		AlterarClienteDTO alterarClienteDTO = alterarClienteDTOBuilder.cpf("12345671012").nome("Junior").sobrenome("da Silva").dataNascimento(LocalDate.of(2000, 3, 3)).email("juniordasilva@gmail.com").senha("123456").celular("27996101055").build();
		var response = mvc.perform(put("/shop/cliente")
								  .header("Authorization", "Bearer " + tokenUser)
								  .contentType(MediaType.APPLICATION_JSON)
								  .content(alterarClienteDTOJson.write(alterarClienteDTO).getJson()))
						.andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        var jsonEsperado = mostrarClienteDTOJson.write(mostrarClienteDTO).getJson();
        assertThat(response.getContentAsString()).isEqualTo(jsonEsperado);
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
		ArquivoInfoDTO arquivoInfoDTO = arquivoInfoDtoBuilder.nomeArquivo("nomeNovaImagem.jpeg").bytesArquivo("Bytes da novaImagem.jpeg".getBytes()).build();
		when(clienteUserService.alterarFotoPerfilPorIdToken(any())).thenReturn(arquivoInfoDTO);

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
        var jsonEsperado = arquivoInfoDTOJson.write(arquivoInfoDTO).getJson();
        assertThat(response.getContentAsString()).isEqualTo(jsonEsperado);
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
