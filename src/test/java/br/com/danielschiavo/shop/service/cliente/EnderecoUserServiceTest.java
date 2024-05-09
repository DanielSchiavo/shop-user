package br.com.danielschiavo.shop.service.cliente;

import static org.mockito.Mockito.times;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

import br.com.danielschiavo.infra.security.UsuarioAutenticadoService;
import br.com.danielschiavo.repository.cliente.EnderecoRepository;
import br.com.danielschiavo.shop.model.ValidacaoException;
import br.com.danielschiavo.shop.model.cliente.Cliente;
import br.com.danielschiavo.shop.model.cliente.Cliente.ClienteBuilder;
import br.com.danielschiavo.shop.model.cliente.endereco.AlterarEnderecoDTO;
import br.com.danielschiavo.shop.model.cliente.endereco.AlterarEnderecoDTO.AlterarEnderecoDTOBuilder;
import br.com.danielschiavo.shop.model.cliente.endereco.CadastrarEnderecoDTO;
import br.com.danielschiavo.shop.model.cliente.endereco.CadastrarEnderecoDTO.CadastrarEnderecoDTOBuilder;
import br.com.danielschiavo.shop.model.cliente.endereco.Endereco;
import br.com.danielschiavo.shop.model.cliente.endereco.Endereco.EnderecoBuilder;
import br.com.danielschiavo.shop.model.cliente.endereco.MostrarEnderecoDTO;
import br.com.danielschiavo.shop.service.cliente.mapper.EnderecoMapperImpl;

@ExtendWith(MockitoExtension.class)
class EnderecoUserServiceTest {
	
	@Mock
	private UsuarioAutenticadoService usuarioAutenticadoService;
	
	@InjectMocks
	private EnderecoUserService enderecoUserService;
	
	@Mock
	private Cliente cliente;
	
	@Mock
	private EnderecoRepository enderecoRepository;
	
	@Captor
	private ArgumentCaptor<Endereco> enderecoCaptor;
	
	private ClienteBuilder clienteBuilder = Cliente.builder();
	
	private EnderecoBuilder enderecoBuilder = Endereco.builder();
	
	private CadastrarEnderecoDTOBuilder cadastrarEnderecoDTOBuilder = CadastrarEnderecoDTO.builder();
	
	private AlterarEnderecoDTOBuilder alterarEnderecoDTOBuilder = AlterarEnderecoDTO.builder();

    @BeforeEach
    void setUp() {
    	EnderecoMapperImpl enderecoMapper = new EnderecoMapperImpl();
    	enderecoUserService.setEnderecoMapper(enderecoMapper);
    }
	
	@Test
	@DisplayName("Deletar enedereco por id token deve funcionar normalmente quando id endereco fornecido existe")
	void deletarEnderecoPorIdToken_idEnderecoExiste_NaoDeveLancarExcecao() {
		//ARRANGE
		Endereco endereco = enderecoBuilder.id(1L).cep("29142298").rua("Divinopolis").numero("15").complemento("Sem complemento").bairro("Bela vista").cidade("Cariacica").estado("ES").enderecoPadrao(true).cliente(cliente).build();
		BDDMockito.when(usuarioAutenticadoService.getCliente()).thenReturn(cliente);
		Long idEndereco = 2L;
		BDDMockito.when(enderecoRepository.findByIdAndCliente(idEndereco, cliente)).thenReturn(Optional.of(endereco));
		
		//ACT
		enderecoUserService.deletarEnderecoPorIdToken(idEndereco);
		
		//ASSERT
		BDDMockito.then(enderecoRepository).should().delete(enderecoCaptor.capture());
	}

	@Test
	@DisplayName("Deletar enedereco por id token deve lançar exceção quando id endereco fornecido não existe")
	void deletarEnderecoPorIdToken_idEnderecoNaoExiste_DeveLancarExcecao() {
		//ARRANGE
		BDDMockito.when(usuarioAutenticadoService.getCliente()).thenReturn(cliente);
		Long idEndereco = 3L;
		BDDMockito.when(enderecoRepository.findByIdAndCliente(idEndereco, cliente)).thenReturn(Optional.empty());
		
		//ASSERT + ACT
		Assertions.assertThrows(ValidacaoException.class, () -> enderecoUserService.deletarEnderecoPorIdToken(idEndereco));
	}
	
	@Test
	@DisplayName("Pegar enderecos cliente por id token deve funcionar normalmente quando cliente tem enderecos cadastrados")
	void pegarEnderecosClientePorIdToken_ClienteTemEnderecosCadastrados_NaoDeveLancarExcecao() {
		//ARRANGE
		Cliente cliente = clienteBuilder.id(1L).cpf("12345678994").nome("Silvana").sobrenome("Pereira da silva").dataNascimento(LocalDate.of(2000, 3, 3)).dataCriacaoConta(LocalDate.now()).email("silvana.dasilva@gmail.com").senha("{noop}123456").celular("27999833653").fotoPerfil("Qualquerfoto.jpeg").getCliente();
		Endereco endereco = enderecoBuilder.id(1L).cep("29142298").rua("Divinopolis").numero("15").complemento("Sem complemento").bairro("Bela vista").cidade("Cariacica").estado("ES").enderecoPadrao(true).cliente(cliente).build();
		Endereco endereco2 = enderecoBuilder.id(2L).cep("29152291").rua("Avenida luciano das neves").numero("3233").complemento("Apartamento 302").bairro("Praia de itaparica").cidade("Vila velha").estado("ES").enderecoPadrao(true).cliente(cliente).build();
		List<Endereco> listaEndereco = new ArrayList<>(List.of(endereco, endereco2));
		BDDMockito.when(enderecoRepository.findAllByCliente(cliente)).thenReturn(listaEndereco);
		BDDMockito.when(usuarioAutenticadoService.getCliente()).thenReturn(cliente);
		
		//ACT
		List<MostrarEnderecoDTO> listaMostrarEnderecoDTO = enderecoUserService.pegarEnderecosClientePorIdToken();
		
		//ASSERT
		Assertions.assertEquals(listaEndereco.size(), listaMostrarEnderecoDTO.size());
		for (int i = 0; i < listaMostrarEnderecoDTO.size(); i++) {
		    MostrarEnderecoDTO enderecoResultado = listaMostrarEnderecoDTO.get(i);

		    Assertions.assertEquals(listaEndereco.get(i).getCep(), enderecoResultado.cep(), "O CEP do endereço deve ser igual");
		    Assertions.assertEquals(listaEndereco.get(i).getRua(), enderecoResultado.rua(), "A rua do endereço deve ser igual");
		    Assertions.assertEquals(listaEndereco.get(i).getNumero(), enderecoResultado.numero(), "O número do endereço deve ser igual");
		    Assertions.assertEquals(listaEndereco.get(i).getComplemento(), enderecoResultado.complemento(), "O complemento do endereço deve ser igual");
		    Assertions.assertEquals(listaEndereco.get(i).getBairro(), enderecoResultado.bairro(), "O bairro do endereço deve ser igual");
		    Assertions.assertEquals(listaEndereco.get(i).getCidade(), enderecoResultado.cidade(), "A cidade do endereço deve ser igual");
		    Assertions.assertEquals(listaEndereco.get(i).getEstado(), enderecoResultado.estado(), "O estado do endereço deve ser igual");
		    Assertions.assertEquals(listaEndereco.get(i).getEnderecoPadrao(), enderecoResultado.enderecoPadrao(), "A indicação de endereço padrão deve ser igual");
		}
	}
	
	@Test
	@DisplayName("Pegar enderecos cliente por id token deve lançar exceção quando cliente não tem enderecos cadastrados")
	void pegarEnderecosClientePorIdToken_ClienteNaoTemEnderecosCadastrados_DeveLancarExcecao() {
		//ARRANGE
		Cliente cliente = clienteBuilder.id(1L).cpf("12345678994").nome("Silvana").sobrenome("Pereira da silva").dataNascimento(LocalDate.of(2000, 3, 3)).dataCriacaoConta(LocalDate.now()).email("silvana.dasilva@gmail.com").senha("{noop}123456").celular("27999833653").fotoPerfil("Qualquerfoto.jpeg").getCliente();
		BDDMockito.when(usuarioAutenticadoService.getCliente()).thenReturn(cliente);
		
		//ASSERT + ACT
		Assertions.assertThrows(ValidacaoException.class, () -> enderecoUserService.pegarEnderecosClientePorIdToken());
	}
	
	@Test
	@DisplayName("Cadastrar novo endereço por id token deve executar normalmente quando enviado CadastrarEnderecoDTO correto")
	void cadastrarNovoEnderecoPorIdToken() {
		//ARRANGE
		Cliente cliente = clienteBuilder.id(1L).cpf("12345678994").nome("Silvana").sobrenome("Pereira da silva").dataNascimento(LocalDate.of(2000, 3, 3)).dataCriacaoConta(LocalDate.now()).email("silvana.dasilva@gmail.com").senha("{noop}123456").celular("27999833653").fotoPerfil("Qualquerfoto.jpeg").getCliente();
		Endereco endereco = enderecoBuilder.id(1L).cep("29142298").rua("Divinopolis").numero("15").complemento("Sem complemento").bairro("Bela vista").cidade("Cariacica").estado("ES").enderecoPadrao(true).cliente(cliente).build();
		Endereco endereco2 = enderecoBuilder.id(2L).cep("29152291").rua("Avenida luciano das neves").numero("3233").complemento("Apartamento 302").bairro("Praia de itaparica").cidade("Vila velha").estado("ES").enderecoPadrao(true).cliente(cliente).build();
		BDDMockito.when(usuarioAutenticadoService.getCliente()).thenReturn(cliente);
		BDDMockito.when(enderecoRepository.findAllByCliente(cliente)).thenReturn(new ArrayList<>(List.of(endereco, endereco2)));
		
		//ACT
		CadastrarEnderecoDTO cadastrarEnderecoDTO = cadastrarEnderecoDTOBuilder.cep("21452872").rua("Dummy").numero("21").complemento("Sem complemento").bairro("Campo grande").cidade("Cariacica").estado("ES").enderecoPadrao(true).build();
		MostrarEnderecoDTO mostrarEnderecoDTO = enderecoUserService.cadastrarNovoEnderecoPorIdToken(cadastrarEnderecoDTO);
		
		//ASSERT
		BDDMockito.then(enderecoRepository).should(times(3)).save(enderecoCaptor.capture());
		Assertions.assertEquals(cadastrarEnderecoDTO.cep(), mostrarEnderecoDTO.cep(), "O CEP do endereço deve ser igual");
		Assertions.assertEquals(cadastrarEnderecoDTO.rua(), mostrarEnderecoDTO.rua(), "A rua do endereço deve ser igual");
		Assertions.assertEquals(cadastrarEnderecoDTO.numero(), mostrarEnderecoDTO.numero(), "O número do endereço deve ser igual");
		Assertions.assertEquals(cadastrarEnderecoDTO.complemento(), mostrarEnderecoDTO.complemento(), "O complemento do endereço deve ser igual");
		Assertions.assertEquals(cadastrarEnderecoDTO.bairro(), mostrarEnderecoDTO.bairro(), "O bairro do endereço deve ser igual");
		Assertions.assertEquals(cadastrarEnderecoDTO.cidade(), mostrarEnderecoDTO.cidade(), "A cidade do endereço deve ser igual");
		Assertions.assertEquals(cadastrarEnderecoDTO.estado(), mostrarEnderecoDTO.estado(), "O estado do endereço deve ser igual");
		Assertions.assertEquals(cadastrarEnderecoDTO.enderecoPadrao(), mostrarEnderecoDTO.enderecoPadrao(), "A indicação de endereço padrão deve ser igual");
	}
	
	@Test
	@DisplayName("Alterar endereco por id token deve executar normalmente quando id fornecido existe, é do cliente e AlterarEnderecoDTO é válido")
	void alterarEnderecoPorIdToken() {
		//ARRANGE
		Cliente cliente = clienteBuilder.id(1L).cpf("12345678994").nome("Silvana").sobrenome("Pereira da silva").dataNascimento(LocalDate.of(2000, 3, 3)).dataCriacaoConta(LocalDate.now()).email("silvana.dasilva@gmail.com").senha("{noop}123456").celular("27999833653").fotoPerfil("Qualquerfoto.jpeg").getCliente();
		Endereco endereco = enderecoBuilder.id(1L).cep("29142298").rua("Divinopolis").numero("15").complemento("Sem complemento").bairro("Bela vista").cidade("Cariacica").estado("ES").enderecoPadrao(true).cliente(cliente).build();
		Endereco endereco2 = enderecoBuilder.id(2L).cep("29152291").rua("Avenida luciano das neves").numero("3233").complemento("Apartamento 302").bairro("Praia de itaparica").cidade("Vila velha").estado("ES").enderecoPadrao(true).cliente(cliente).build();
		BDDMockito.when(usuarioAutenticadoService.getCliente()).thenReturn(cliente);
		Long idEnderecoASerAlterado = 1L;
		List<Endereco> listaEndereco = new ArrayList<>(List.of(endereco, endereco2));
		BDDMockito.when(enderecoRepository.findAllByCliente(cliente)).thenReturn(listaEndereco);
		
		//ACT
		AlterarEnderecoDTO alterarEnderecoDTO = alterarEnderecoDTOBuilder.cep("12345678").rua("Itapua").numero("35").complemento("com complemento").bairro("aquele bairro").cidade("serra").estado("ES").enderecoPadrao(true).build();
		MostrarEnderecoDTO mostrarEnderecoDTO = enderecoUserService.alterarEnderecoPorIdToken(alterarEnderecoDTO, idEnderecoASerAlterado);
		
		//ASSERT
		Assertions.assertNotEquals(listaEndereco.get(0).getCep(), mostrarEnderecoDTO.cep());
		Assertions.assertNotEquals(listaEndereco.get(0).getRua(), mostrarEnderecoDTO.rua());
		Assertions.assertNotEquals(listaEndereco.get(0).getNumero(), mostrarEnderecoDTO.numero());
		Assertions.assertNotEquals(listaEndereco.get(0).getComplemento(), mostrarEnderecoDTO.complemento());
		Assertions.assertNotEquals(listaEndereco.get(0).getBairro(), mostrarEnderecoDTO.bairro());
		Assertions.assertNotEquals(listaEndereco.get(0).getCidade(), mostrarEnderecoDTO.cidade());
		Assertions.assertEquals(listaEndereco.get(0).getEstado(), mostrarEnderecoDTO.estado());
		Assertions.assertEquals(listaEndereco.get(0).getEnderecoPadrao(), mostrarEnderecoDTO.enderecoPadrao());
	}
}
