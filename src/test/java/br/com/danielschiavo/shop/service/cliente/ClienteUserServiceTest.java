package br.com.danielschiavo.shop.service.cliente;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.danielschiavo.feign.FileStoragePerfilComumServiceClient;
import br.com.danielschiavo.infra.security.UsuarioAutenticadoService;
import br.com.danielschiavo.repository.cliente.ClienteRepository;
import br.com.danielschiavo.shop.service.cliente.mapper.ClienteMapperImpl;
import br.com.danielschiavo.shop.model.ValidacaoException;
import br.com.danielschiavo.shop.model.cliente.Cliente;
import br.com.danielschiavo.shop.model.cliente.Cliente.ClienteBuilder;
import br.com.danielschiavo.shop.model.cliente.dto.AlterarClienteDTO;
import br.com.danielschiavo.shop.model.cliente.dto.AlterarClienteDTO.AlterarClienteDTOBuilder;
import br.com.danielschiavo.shop.model.cliente.dto.AlterarFotoPerfilDTO;
import br.com.danielschiavo.shop.model.cliente.dto.CadastrarClienteDTO;
import br.com.danielschiavo.shop.model.cliente.dto.CadastrarClienteDTO.CadastrarClienteDTOBuilder;
import br.com.danielschiavo.shop.model.cliente.dto.MostrarClienteDTO;
import br.com.danielschiavo.shop.model.filestorage.ArquivoInfoDTO;

@ExtendWith(MockitoExtension.class)
class ClienteUserServiceTest {

	@Mock
	private UsuarioAutenticadoService usuarioAutenticadoService;
	
	@InjectMocks
	private ClienteUserService clienteUserService;
	
	@Mock
	private Cliente cliente;
	
	@Captor
	private ArgumentCaptor<Cliente> clienteCaptor;
	
	@Mock
	private FileStoragePerfilComumServiceClient fileStorageServiceClient;
	
	@Mock
	private ClienteRepository clienteRepository;
	
	private ClienteBuilder clienteBuilder = Cliente.builder();
	
	private CadastrarClienteDTOBuilder cadastrarClienteDTOBuilder = CadastrarClienteDTO.builder();
	
	private AlterarClienteDTOBuilder alterarClienteDTOBuilder = AlterarClienteDTO.builder();
	
    @BeforeEach
    void setUp() {
    	ClienteMapperImpl clienteMapper = new ClienteMapperImpl();
    	clienteUserService.setClienteMapper(clienteMapper);
    }
	
	@Test
	@DisplayName("Deletar foto perfil por id token deve lançar exceção quando tentar excluir foto Padrão")
	void deletarFotoPerfilPorIdToken_ClienteTemFotoPadrao_DeveLancarExcecao() {
		//ARRANGE
		BDDMockito.when(usuarioAutenticadoService.getCliente()).thenReturn(cliente);
		BDDMockito.when(cliente.getFotoPerfil()).thenReturn("Padrao.jpeg");
		
		//ASSERT + ACT
		Assertions.assertThrows(ValidacaoException.class, () -> clienteUserService.deletarFotoPerfilPorIdToken());
	}
	
	@Test
	@DisplayName("Deletar foto perfil por id token deve remover a foto perfil do cliente e definir a foto do cliente como Padrao.jpeg")
	void deletarFotoPerfilPorIdToken_ClienteNaoTemFotoPadrao_NaoDeveLancarExcecao() throws IOException {
		//ARRANGE
		Cliente cliente = clienteBuilder.id(1L).cpf("12345678994").nome("Silvana").sobrenome("Pereira da silva").dataNascimento(LocalDate.of(2000, 3, 3)).dataCriacaoConta(LocalDate.now()).email("silvana.dasilva@gmail.com").senha("{noop}123456").celular("27999833653").fotoPerfil("Qualquerfoto.jpeg").getCliente();
		BDDMockito.when(usuarioAutenticadoService.getCliente()).thenReturn(cliente);
		
		//ACT
		clienteUserService.deletarFotoPerfilPorIdToken();
		
		//ASSERT
		verify(clienteRepository).save(any(Cliente.class));
		Assertions.assertEquals("Padrao.jpeg", cliente.getFotoPerfil());
	}
	
	@Test
	@DisplayName("Detalhar cliente por id token pagina inicial deve retornar dados normalmente quando usuario logado no sistema")
	void detalharClientePorIdTokenPaginaInicial() {
		//ARRANGE
		Cliente cliente = clienteBuilder.id(1L).cpf("12345678994").nome("Silvana").sobrenome("Pereira da silva").dataNascimento(LocalDate.of(2000, 3, 3)).dataCriacaoConta(LocalDate.now()).email("silvana.dasilva@gmail.com").senha("{noop}123456").celular("27999833653").fotoPerfil("Padrao.jpeg").getCliente();
		BDDMockito.when(usuarioAutenticadoService.getCliente()).thenReturn(cliente);
		ArquivoInfoDTO arquivoInfoDTO = new ArquivoInfoDTO("outrafoto.jpeg", "Bytesimagemdummy".getBytes());
		BDDMockito.when(fileStorageServiceClient.getFotoPerfil(any())).thenReturn(arquivoInfoDTO);
		
		//ACT
		MostrarClientePaginaInicialDTO mostrarClientePaginaInicialDTO = clienteUserService.detalharClientePorIdTokenPaginaInicial();
		
		//ASSERT
		Assertions.assertEquals(cliente.getNome(), mostrarClientePaginaInicialDTO.getNome());
		Assertions.assertEquals(arquivoInfoDTO.bytesArquivo(), mostrarClientePaginaInicialDTO.getFotoPerfil().bytesArquivo());
		Assertions.assertEquals(arquivoInfoDTO.nomeArquivo(), mostrarClientePaginaInicialDTO.getFotoPerfil().nomeArquivo());
	}
	
	@Test
	@DisplayName("Detalhar cliente por id token deve retornar dados normalmente quando usuario logado no sistema")
	void detalharClientePorIdToken() {
		//ARRANGE
		Cliente cliente = clienteBuilder.id(1L).cpf("12345678994").nome("Silvana").sobrenome("Pereira da silva")
									.dataNascimento(LocalDate.of(2000, 3, 3)).dataCriacaoConta(LocalDate.now())
									.email("silvana.dasilva@gmail.com").senha("{noop}123456").celular("27999833653")
									.fotoPerfil("Padrao.jpeg").getCliente();
		BDDMockito.when(usuarioAutenticadoService.getCliente()).thenReturn(cliente);
		ArquivoInfoDTO arquivoInfoDTO = new ArquivoInfoDTO("outrafoto.jpeg", "Bytesimagemdummy".getBytes());
		BDDMockito.when(fileStorageServiceClient.getFotoPerfil(any())).thenReturn(arquivoInfoDTO);
		
		//ACT
		MostrarClienteDTO mostrarClienteDTO = clienteUserService.detalharClientePorIdToken();
		
		//ASSERT
		Assertions.assertNotNull(mostrarClienteDTO);
		Assertions.assertEquals(cliente.getId(), mostrarClienteDTO.getId());
		Assertions.assertEquals(cliente.getCpf(), mostrarClienteDTO.getCpf());
		Assertions.assertEquals(cliente.getNome(), mostrarClienteDTO.getNome());
		Assertions.assertEquals(cliente.getSobrenome(), mostrarClienteDTO.getSobrenome());
		Assertions.assertEquals(cliente.getDataNascimento(), mostrarClienteDTO.getDataNascimento());
		Assertions.assertEquals(cliente.getDataCriacaoConta(), mostrarClienteDTO.getDataCriacaoConta());
		Assertions.assertEquals(cliente.getEmail(), mostrarClienteDTO.getEmail());
		Assertions.assertEquals(cliente.getCelular(), mostrarClienteDTO.getCelular());
		//ArquivoInfoDTO
		Assertions.assertEquals(arquivoInfoDTO.nomeArquivo(), mostrarClienteDTO.getFotoPerfil().nomeArquivo());
		Assertions.assertEquals(arquivoInfoDTO.bytesArquivo(), mostrarClienteDTO.getFotoPerfil().bytesArquivo());
	}
	
	@Test
	@DisplayName("Cadastrar cliente deve funcionar normalmente quando CadastrarClienteDTO valido é enviado")
	void cadastrarCliente_DtoEnviadoValido() {
		//ACT
		CadastrarClienteDTO cadastrarClienteDTO = cadastrarClienteDTOBuilder.cpf("12345678994").nome("Silvana").sobrenome("Pereira da silva").dataNascimento(LocalDate.of(2000, 3, 3)).email("silvana.dasilva@gmail.com").senha("{noop}123456").celular("27999833653").fotoPerfil("outrafoto.jpeg").build();
		String mensagem = clienteUserService.cadastrarCliente(cadastrarClienteDTO);
		
		//ASSERT
		BDDMockito.then(clienteRepository).should().save(clienteCaptor.capture());
		Cliente clienteSalvo = clienteCaptor.getValue();
		Assertions.assertNotNull(mensagem);
		Assertions.assertEquals(clienteSalvo.getCpf(), cadastrarClienteDTO.cpf());
		Assertions.assertEquals(clienteSalvo.getNome(), cadastrarClienteDTO.nome());
		Assertions.assertEquals(clienteSalvo.getSobrenome(), cadastrarClienteDTO.sobrenome());
		Assertions.assertEquals(clienteSalvo.getDataNascimento(), cadastrarClienteDTO.dataNascimento());
		Assertions.assertNotNull(clienteSalvo.getDataCriacaoConta());
		Assertions.assertEquals(clienteSalvo.getEmail(), cadastrarClienteDTO.email());
		Assertions.assertEquals(clienteSalvo.getCelular(), cadastrarClienteDTO.celular());
	}
	
	@Test
	@DisplayName("Alterar cliente por id token deve funcionar normalmente se dto enviado está correto")
	void alterarClientePorIdToken() {
		//ARRANGE
		Cliente cliente = clienteBuilder.id(1L).cpf("12345678994").nome("Silvana").sobrenome("Pereira da silva").dataNascimento(LocalDate.of(2000, 3, 3)).dataCriacaoConta(LocalDate.now()).email("silvana.dasilva@gmail.com").senha("{noop}123456").celular("27999833653").fotoPerfil("Qualquerfoto.jpeg").getCliente();
		BDDMockito.when(usuarioAutenticadoService.getCliente()).thenReturn(cliente);
		ArquivoInfoDTO arquivoInfoDTO = new ArquivoInfoDTO("outrafoto.jpeg", "Bytesimagemdummy".getBytes());
		BDDMockito.when(fileStorageServiceClient.getFotoPerfil(any())).thenReturn(arquivoInfoDTO);
		
		//ACT
		AlterarClienteDTO alterarClienteDTO = alterarClienteDTOBuilder.cpf("12345612321").nome("Silvana").sobrenome("Silva Santana").dataNascimento(LocalDate.of(1999, 3, 3)).email("silvanasantana@gmail.com").senha("654321").celular("27998321332").build();
		String mensagem = clienteUserService.alterarClientePorIdToken(alterarClienteDTO);
		
		//ASSERT
		Assertions.assertNotNull(mensagem);
	}
	
	@Test
	@DisplayName("Alterar foto perfil por id token deve executar normalmente quando dto enviado é valido")
	void alterarFotoPerfilPorIdToken() {
		//ARRANGE
		BDDMockito.when(usuarioAutenticadoService.getCliente()).thenReturn(cliente);
		ArquivoInfoDTO arquivoInfoDTO = new ArquivoInfoDTO("Novaimagem.jpeg", "Bytesimagemdummy".getBytes());
		BDDMockito.when(fileStorageServiceClient.getFotoPerfil(any())).thenReturn(arquivoInfoDTO);
		
		//ACT
		AlterarFotoPerfilDTO alterarFotoPerfilDTO = new AlterarFotoPerfilDTO("Novaimagem.jpeg");
		String mensagem = clienteUserService.alterarFotoPerfilPorIdToken(alterarFotoPerfilDTO);
		
		//ASSERT
		BDDMockito.then(cliente).should().setFotoPerfil(alterarFotoPerfilDTO.nomeNovaFotoPerfil());
		BDDMockito.then(clienteRepository).should().save(cliente);
		Assertions.assertNotNull(mensagem);
	}

}
