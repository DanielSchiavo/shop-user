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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.danielschiavo.infra.security.UsuarioAutenticadoService;
import br.com.danielschiavo.mapper.cliente.ClienteMapper;
import br.com.danielschiavo.repository.cliente.ClienteRepository;
import br.com.danielschiavo.shop.mapper.cliente.ClienteMapperImpl;
import br.com.danielschiavo.shop.model.ValidacaoException;
import br.com.danielschiavo.shop.model.cliente.Cliente;
import br.com.danielschiavo.shop.model.cliente.Cliente.ClienteBuilder;
import br.com.danielschiavo.shop.model.cliente.cartao.Cartao;
import br.com.danielschiavo.shop.model.cliente.cartao.Cartao.CartaoBuilder;
import br.com.danielschiavo.shop.model.cliente.cartao.TipoCartao;
import br.com.danielschiavo.shop.model.cliente.dto.AlterarClienteDTO;
import br.com.danielschiavo.shop.model.cliente.dto.AlterarClienteDTO.AlterarClienteDTOBuilder;
import br.com.danielschiavo.shop.model.cliente.dto.AlterarFotoPerfilDTO;
import br.com.danielschiavo.shop.model.cliente.dto.CadastrarClienteDTO;
import br.com.danielschiavo.shop.model.cliente.dto.CadastrarClienteDTO.CadastrarClienteDTOBuilder;
import br.com.danielschiavo.shop.model.cliente.dto.MostrarClienteDTO;
import br.com.danielschiavo.shop.model.cliente.endereco.CadastrarEnderecoDTO;
import br.com.danielschiavo.shop.model.cliente.endereco.CadastrarEnderecoDTO.CadastrarEnderecoDTOBuilder;
import br.com.danielschiavo.shop.model.cliente.endereco.Endereco;
import br.com.danielschiavo.shop.model.cliente.endereco.Endereco.EnderecoBuilder;
import br.com.danielschiavo.shop.model.filestorage.ArquivoInfoDTO;
import br.com.danielschiavo.shop.service.filestorage.FileStoragePerfilService;

@ExtendWith(MockitoExtension.class)
class ClienteUserServiceTest {

	@Mock
	private UsuarioAutenticadoService usuarioAutenticadoService;
	
	@InjectMocks
	private ClienteUserService clienteUserService;
	
	@Spy
	private EnderecoUserService enderecoService;
	
	@Mock
	private Cliente cliente;
	
	@Captor
	private ArgumentCaptor<Cliente> clienteCaptor;
	
	@Mock
	private FileStoragePerfilService filePerfilService;
	
	@Mock
	private ClienteRepository clienteRepository;
	
	private ClienteBuilder clienteBuilder = Cliente.builder();
	
	private EnderecoBuilder enderecoBuilder = Endereco.builder();
	
	private CartaoBuilder cartaoBuilder = Cartao.builder();
	
	private CadastrarClienteDTOBuilder cadastrarClienteDTOBuilder = CadastrarClienteDTO.builder();
	
	private CadastrarEnderecoDTOBuilder cadastrarEnderecoDTOBuilder = CadastrarEnderecoDTO.builder();
	
	private AlterarClienteDTOBuilder alterarClienteDTOBuilder = AlterarClienteDTO.builder();
	
    @BeforeEach
    void setUp() {
//    	clienteUserService.setEnderecoService(enderecoService);
    	ClienteMapper clienteMapper = new ClienteMapperImpl();
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
		BDDMockito.when(filePerfilService.pegarFotoPerfilPorNome(any())).thenReturn(arquivoInfoDTO);
		
		//ACT
		MostrarClienteDTO mostrarClientePaginaInicialDTO = clienteUserService.detalharClientePorIdTokenPaginaInicial();
		
		//ASSERT
		Assertions.assertEquals(cliente.getNome(), mostrarClientePaginaInicialDTO.getNome());
		Assertions.assertEquals(arquivoInfoDTO.bytesArquivo(), mostrarClientePaginaInicialDTO.getFotoPerfil().bytesArquivo());
		Assertions.assertEquals(arquivoInfoDTO.nomeArquivo(), mostrarClientePaginaInicialDTO.getFotoPerfil().nomeArquivo());
	}
	
	@Test
	@DisplayName("Detalhar cliente por id token deve retornar dados normalmente quando usuario logado no sistema")
	void detalharClientePorIdToken() {
		//ARRANGE
		Endereco endereco = enderecoBuilder.id(1L).cep("29142298").rua("Divinopolis").numero("15").complemento("Sem complemento").bairro("Bela vista").cidade("Cariacica").estado("ES").enderecoPadrao(true).cliente(cliente).build();
		Endereco endereco2 = enderecoBuilder.id(2L).cep("29152291").rua("Avenida luciano das neves").numero("3233").complemento("Apartamento 302").bairro("Praia de itaparica").cidade("Vila velha").estado("ES").enderecoPadrao(true).cliente(cliente).build();
		Cartao cartao = cartaoBuilder.id(1L).nomeBanco("Santander").numeroCartao("1234567812345678").nomeNoCartao("Daniel schiavo rosseto").validadeCartao("03/28").cartaoPadrao(true).tipoCartao(TipoCartao.CREDITO).cliente(cliente).build();
		Cliente cliente = clienteBuilder.id(1L).cpf("12345678994").nome("Silvana").sobrenome("Pereira da silva")
									.dataNascimento(LocalDate.of(2000, 3, 3)).dataCriacaoConta(LocalDate.now())
									.email("silvana.dasilva@gmail.com").senha("{noop}123456").celular("27999833653")
									.fotoPerfil("Padrao.jpeg").adicionarEndereco(endereco).adicionarEndereco(endereco2).adicionarCartao(cartao).getCliente();
		BDDMockito.when(usuarioAutenticadoService.getCliente()).thenReturn(cliente);
		ArquivoInfoDTO arquivoInfoDTO = new ArquivoInfoDTO("outrafoto.jpeg", "Bytesimagemdummy".getBytes());
		BDDMockito.when(filePerfilService.pegarFotoPerfilPorNome(any())).thenReturn(arquivoInfoDTO);
		
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
	@DisplayName("Cadastrar cliente deve funcionar normalmente quando CadastrarClienteDTO valido é enviado com endereço")
	void cadastrarCliente_DtoEnviadoValidoEComEndereco() {
		//ACT
		CadastrarEnderecoDTO cadastrarEnderecoDTO = cadastrarEnderecoDTOBuilder.cep("29142298").rua("Divinopolis").numero("15").complemento("Sem complemento").bairro("Bela vista").cidade("Cariacica").estado("ES").enderecoPadrao(true).build();
		CadastrarClienteDTO cadastrarClienteDTO = cadastrarClienteDTOBuilder.cpf("12345678994").nome("Silvana").sobrenome("Pereira da silva").dataNascimento(LocalDate.of(2000, 3, 3)).email("silvana.dasilva@gmail.com").senha("{noop}123456").celular("27999833653").fotoPerfil("outrafoto.jpeg").endereco(cadastrarEnderecoDTO).build();
		MostrarClienteDTO mostrarClienteDTO = clienteUserService.cadastrarCliente(cadastrarClienteDTO);
		
		//ASSERT
		BDDMockito.then(clienteRepository).should().save(clienteCaptor.capture());
		Cliente clienteSalvo = clienteCaptor.getValue();
		Assertions.assertNotNull(mostrarClienteDTO);
		Assertions.assertEquals(clienteSalvo.getCpf(), mostrarClienteDTO.getCpf());
		Assertions.assertEquals(clienteSalvo.getNome(), mostrarClienteDTO.getNome());
		Assertions.assertEquals(clienteSalvo.getSobrenome(), mostrarClienteDTO.getSobrenome());
		Assertions.assertEquals(clienteSalvo.getDataNascimento(), mostrarClienteDTO.getDataNascimento());
		Assertions.assertEquals(clienteSalvo.getDataCriacaoConta(), mostrarClienteDTO.getDataCriacaoConta());
		Assertions.assertEquals(clienteSalvo.getEmail(), mostrarClienteDTO.getEmail());
		Assertions.assertEquals(clienteSalvo.getCelular(), mostrarClienteDTO.getCelular());
	}
	
	@Test
	@DisplayName("Cadastrar cliente deve funcionar normalmente quando CadastrarClienteDTO valido é enviado sem endereço")
	void cadastrarCliente_DtoEnviadoValidoESemEndereco() {
		//ARRANGE
		CadastrarClienteDTO cadastrarClienteDTO = cadastrarClienteDTOBuilder.cpf("12345678994").nome("Silvana").sobrenome("Pereira da silva").dataNascimento(LocalDate.of(2000, 3, 3)).email("silvana.dasilva@gmail.com").senha("{noop}123456").celular("27999833653").fotoPerfil("outrafoto.jpeg").build();
		
		//ACT
		MostrarClienteDTO mostrarClienteDTO = clienteUserService.cadastrarCliente(cadastrarClienteDTO);
		
		//ASSERT
		BDDMockito.then(clienteRepository).should().save(clienteCaptor.capture());
		Cliente clienteSalvo = clienteCaptor.getValue();
		Assertions.assertNotNull(mostrarClienteDTO);
		Assertions.assertEquals(clienteSalvo.getCpf(), mostrarClienteDTO.getCpf());
		Assertions.assertEquals(clienteSalvo.getNome(), mostrarClienteDTO.getNome());
		Assertions.assertEquals(clienteSalvo.getSobrenome(), mostrarClienteDTO.getSobrenome());
		Assertions.assertEquals(clienteSalvo.getDataNascimento(), mostrarClienteDTO.getDataNascimento());
		Assertions.assertEquals(clienteSalvo.getDataCriacaoConta(), mostrarClienteDTO.getDataCriacaoConta());
		Assertions.assertEquals(clienteSalvo.getEmail(), mostrarClienteDTO.getEmail());
		Assertions.assertEquals(clienteSalvo.getCelular(), mostrarClienteDTO.getCelular());
	}
	
	@Test
	@DisplayName("Alterar cliente por id token deve funcionar normalmente se dto enviado está correto")
	void alterarClientePorIdToken() {
		//ARRANGE
		Cliente cliente = clienteBuilder.id(1L).cpf("12345678994").nome("Silvana").sobrenome("Pereira da silva").dataNascimento(LocalDate.of(2000, 3, 3)).dataCriacaoConta(LocalDate.now()).email("silvana.dasilva@gmail.com").senha("{noop}123456").celular("27999833653").fotoPerfil("Qualquerfoto.jpeg").getCliente();
		BDDMockito.when(usuarioAutenticadoService.getCliente()).thenReturn(cliente);
		ArquivoInfoDTO arquivoInfoDTO = new ArquivoInfoDTO("outrafoto.jpeg", "Bytesimagemdummy".getBytes());
		BDDMockito.when(filePerfilService.pegarFotoPerfilPorNome(any())).thenReturn(arquivoInfoDTO);
		
		//ACT
		AlterarClienteDTO alterarClienteDTO = alterarClienteDTOBuilder.cpf("12345612321").nome("Silvana").sobrenome("Silva Santana").dataNascimento(LocalDate.of(1999, 3, 3)).email("silvanasantana@gmail.com").senha("654321").celular("27998321332").build();
		MostrarClienteDTO mostrarClienteDTO = clienteUserService.alterarClientePorIdToken(alterarClienteDTO);
		
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
	@DisplayName("Alterar foto perfil por id token deve executar normalmente quando dto enviado é valido")
	void alterarFotoPerfilPorIdToken() {
		//ARRANGE
		BDDMockito.when(usuarioAutenticadoService.getCliente()).thenReturn(cliente);
		ArquivoInfoDTO arquivoInfoDTO = new ArquivoInfoDTO("Novaimagem.jpeg", "Bytesimagemdummy".getBytes());
		BDDMockito.when(filePerfilService.pegarFotoPerfilPorNome(any())).thenReturn(arquivoInfoDTO);
		
		//ACT
		AlterarFotoPerfilDTO alterarFotoPerfilDTO = new AlterarFotoPerfilDTO("Novaimagem.jpeg");
		ArquivoInfoDTO retornoMetodoArquivoInfoDTO = clienteUserService.alterarFotoPerfilPorIdToken(alterarFotoPerfilDTO);
		
		//ASSERT
		BDDMockito.then(cliente).should().setFotoPerfil(alterarFotoPerfilDTO.nomeNovaFotoPerfil());
		BDDMockito.then(clienteRepository).should().save(cliente);
		Assertions.assertEquals(arquivoInfoDTO.nomeArquivo(), retornoMetodoArquivoInfoDTO.nomeArquivo());
		Assertions.assertEquals(arquivoInfoDTO.bytesArquivo(), retornoMetodoArquivoInfoDTO.bytesArquivo());
	}

}
