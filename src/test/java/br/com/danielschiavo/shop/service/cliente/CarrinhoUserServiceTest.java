package br.com.danielschiavo.shop.service.cliente;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.danielschiavo.infra.security.UsuarioAutenticadoService;
import br.com.danielschiavo.service.cliente.CarrinhoUtilidadeService;
import br.com.danielschiavo.shop.model.ValidacaoException;
import br.com.danielschiavo.shop.model.cliente.Cliente;
import br.com.danielschiavo.shop.model.cliente.carrinho.Carrinho;
import br.com.danielschiavo.shop.model.cliente.carrinho.Carrinho.CarrinhoBuilder;
import br.com.danielschiavo.shop.model.cliente.carrinho.MostrarCarrinhoClienteDTO;
import br.com.danielschiavo.shop.model.cliente.carrinho.itemcarrinho.AdicionarItemCarrinhoDTO;
import br.com.danielschiavo.shop.model.cliente.carrinho.itemcarrinho.AdicionarItemCarrinhoDTO.AdicionarItemCarrinhoDTOBuilder;
import br.com.danielschiavo.shop.model.cliente.carrinho.itemcarrinho.ItemCarrinho;
import br.com.danielschiavo.shop.model.cliente.carrinho.itemcarrinho.ItemCarrinho.ItemCarrinhoBuilder;
import br.com.danielschiavo.shop.repository.cliente.CarrinhoRepository;
import br.com.danielschiavo.shop.service.cliente.mapper.CarrinhoMapper;

@ExtendWith(MockitoExtension.class)
class CarrinhoUserServiceTest {

	@Mock
	private UsuarioAutenticadoService usuarioAutenticadoService;

	@Mock
	private CarrinhoRepository carrinhoRepository;
	
	@InjectMocks
	private CarrinhoUserService carrinhoUserService;
	
	@Mock
	private CarrinhoUtilidadeService carrinhoUtilidadeService;
	
	@Mock
	private Cliente cliente;
	
	@Mock
	private Carrinho carrinho;
	
	@Mock
	private AdicionarItemCarrinhoDTO itemCarrinhoDTO;
	
	@Captor
	private ArgumentCaptor<Carrinho> carrinhoCaptor;
	
	private ItemCarrinhoBuilder itemCarrinhoBuilder = ItemCarrinho.builder();
	
	private CarrinhoBuilder carrinhoBuilder = Carrinho.builder();
	
	private AdicionarItemCarrinhoDTOBuilder adicionarItemCarrinhoDTOBuilder = AdicionarItemCarrinhoDTO.builder();
	
	@BeforeEach
	public void beforeEach() {
	    CarrinhoMapper instanciaCarrinhoMapper = Mappers.getMapper(CarrinhoMapper.class);
	    instanciaCarrinhoMapper = Mockito.spy(instanciaCarrinhoMapper);
		carrinhoUserService.setCarrinhoMapper(instanciaCarrinhoMapper);
	}
	
	@Test
	@DisplayName("Deletar produto no carrinho por id token não deve lançar exceção quando carrinho existe e produto está no carrinho")
	void deletarProdutoNoCarrinhoPorIdToken_CarrinhoExisteEProdutoEstaNoCarrinho_NaoDeveLancarExcecao() {
		//ARRANGE
		ItemCarrinho itemCarrinho = itemCarrinhoBuilder.id(1L).quantidade(1).produtoId(1L).carrinho(carrinho).build();
		ItemCarrinho itemCarrinho2 = itemCarrinhoBuilder.id(2L).quantidade(3).produtoId(2L).carrinho(carrinho).build();
		List<ItemCarrinho> listaItemCarrinho = new ArrayList<>(List.of(itemCarrinho, itemCarrinho2));
		BDDMockito.when(usuarioAutenticadoService.getCliente()).thenReturn(cliente);
		BDDMockito.when(carrinhoUtilidadeService.pegarCarrinho(cliente)).thenReturn(carrinho);
		BDDMockito.when(carrinho.getItemsCarrinho()).thenReturn(listaItemCarrinho);
		
		//ACT
		List<Long> produtosId = new ArrayList<>(List.of(3L));
		carrinhoUserService.deletarProdutoNoCarrinhoPorIdToken(produtosId);
		
		//ASSERT
		BDDMockito.then(carrinhoRepository).should().save(carrinho);
	}
	
	@Test
	@DisplayName("Deletar produto no carrinho por id token deve lançar exceção quando produto não está no carrinho")
	void deletarProdutoNoCarrinhoPorIdToken_ProdutoNaoEstaNoCarrinho_DeveLancarExcecao() {
		//ARRANGE
		ItemCarrinho itemCarrinho = itemCarrinhoBuilder.id(1L).quantidade(1).produtoId(1L).carrinho(carrinho).build();
		ItemCarrinho itemCarrinho2 = itemCarrinhoBuilder.id(2L).quantidade(3).produtoId(2L).carrinho(carrinho).build();
		List<ItemCarrinho> listaItemCarrinho = new ArrayList<>(List.of(itemCarrinho, itemCarrinho2));
		BDDMockito.when(usuarioAutenticadoService.getCliente()).thenReturn(cliente);
		BDDMockito.when(carrinhoUtilidadeService.pegarCarrinho(cliente)).thenReturn(carrinho);
		BDDMockito.when(carrinho.getItemsCarrinho()).thenReturn(listaItemCarrinho);
		List<Long> produtosId = new ArrayList<>(List.of(3L));
		
		//ASSERT + ACT
		Assertions.assertThrows(ValidacaoException.class, () -> carrinhoUserService.deletarProdutoNoCarrinhoPorIdToken(produtosId));
	}
	
	@Test
	@DisplayName("Pegar carrinho cliente por id token não deve lançar exceção quando carrinho do cliente tem produtos")
	void pegarCarrinhoClientePorIdToken_CarrinhoDoClienteTemProdutos_NaoDeveLancarExcecao() {
		//ARRANGE
		BDDMockito.when(usuarioAutenticadoService.getCliente()).thenReturn(cliente);
		ItemCarrinho itemCarrinho = itemCarrinhoBuilder.id(1L).quantidade(1).produtoId(1L).carrinho(carrinho).build();
		ItemCarrinho itemCarrinho2 = itemCarrinhoBuilder.id(2L).quantidade(3).produtoId(2L).carrinho(carrinho).build();
		List<ItemCarrinho> listaItemCarrinho = new ArrayList<>(List.of(itemCarrinho, itemCarrinho2));
		BDDMockito.when(carrinhoUtilidadeService.pegarCarrinho(cliente)).thenReturn(carrinho);
		BDDMockito.when(carrinho.getItemsCarrinho()).thenReturn(listaItemCarrinho);
		
		//ACT
		MostrarCarrinhoClienteDTO mostrarCarrinhoClienteDTO = carrinhoUserService.pegarCarrinhoClientePorIdToken();
		
		System.out.println(mostrarCarrinhoClienteDTO);
		
		//ASSERT
		Assertions.assertEquals(0, mostrarCarrinhoClienteDTO.getClienteId());
		Assertions.assertEquals(BigDecimal.valueOf(500.00), mostrarCarrinhoClienteDTO.getValorTotal());
		Assertions.assertEquals(listaItemCarrinho.size(), mostrarCarrinhoClienteDTO.getItemsCarrinho().size());
	}
	
	@Test
	@DisplayName("Pegar carrinho cliente por id token deve lançar exceção quando carrinho do cliente existe mas não tem produtos")
	void pegarCarrinhoClientePorIdToken_CarrinhoDoClienteExisteMasNaoTemProdutos_DeveLancarExcecao() {
		//ARRANGE
		BDDMockito.when(usuarioAutenticadoService.getCliente()).thenReturn(cliente);
		BDDMockito.when(carrinhoUtilidadeService.pegarCarrinho(cliente)).thenReturn(carrinho);
		BDDMockito.when(carrinho.getItemsCarrinho()).thenReturn(new ArrayList<ItemCarrinho>());
		
		//ASSERT + ACT
		Assertions.assertThrows(ValidacaoException.class, () -> carrinhoUserService.pegarCarrinhoClientePorIdToken());
	}
	
	@Test
	@DisplayName("Adicionar produtos no carrinho por id token deve funcionar normalmente quando cliente tem carrinho, nao tem mesmo produto no carrinho e o produto é válido")
	void adicionarProdutosNoCarrinhoPorIdToken_ClienteTemCarrinhoENaoTemMesmoProdutoNoCarrinho_NaoDeveLancarExcecao() {
		//ARRANGE
		BDDMockito.when(usuarioAutenticadoService.getCliente()).thenReturn(cliente);
		BDDMockito.when(carrinhoUtilidadeService.pegarCarrinho(cliente)).thenReturn(carrinho);
		BDDMockito.when(carrinho.getItemsCarrinho()).thenReturn(new ArrayList<ItemCarrinho>());
		
		//ACT
		AdicionarItemCarrinhoDTO adicionarItemCarrinhoDTO = adicionarItemCarrinhoDTOBuilder.produtoId(1L).quantidade(5).build();
		carrinhoUserService.adicionarProdutosNoCarrinhoPorIdToken(adicionarItemCarrinhoDTO);
		
		//ASSERT
		Assertions.assertEquals(1, carrinho.getItemsCarrinho().size());
		Assertions.assertEquals(5, carrinho.getItemsCarrinho().get(0).getQuantidade());
		Assertions.assertEquals(carrinho, carrinho.getItemsCarrinho().get(0).getCarrinho());
		BDDMockito.then(carrinhoRepository).should().save(carrinho);
	}
	
	@Test
	@DisplayName("Adicionar produtos no carrinho por id token deve funcionar normalmente quando cliente tem carrinho, tem o mesmo produto no carrinho e produto é válido")
	void adicionarProdutosNoCarrinhoPorIdToken_ClienteTemCarrinhoETemMesmoProdutoNoCarrinho_NaoDeveLancarExcecao() {
		//ARRANGE
		ItemCarrinho itemCarrinho = itemCarrinhoBuilder.id(1L).quantidade(2).produtoId(1L).carrinho(carrinho).build();
		Carrinho carrinho = carrinhoBuilder.clienteId(1L).itemsCarrinho(List.of(itemCarrinho)).build();
		BDDMockito.when(usuarioAutenticadoService.getCliente()).thenReturn(cliente);
		BDDMockito.when(carrinhoUtilidadeService.pegarCarrinho(cliente)).thenReturn(carrinho);
		
		//ACT
		AdicionarItemCarrinhoDTO adicionarItemCarrinhoDTO = adicionarItemCarrinhoDTOBuilder.produtoId(1L).quantidade(5).build();
		carrinhoUserService.adicionarProdutosNoCarrinhoPorIdToken(adicionarItemCarrinhoDTO);
		
		//ASSERT
		Assertions.assertEquals(1, carrinho.getItemsCarrinho().size());
		Assertions.assertEquals(7, carrinho.getItemsCarrinho().get(0).getQuantidade());
		BDDMockito.then(carrinhoRepository).should().save(carrinho);
	}
	
	@Test
	@DisplayName("Setar quantidade produto no carrinho por id token deve funcionar normalmente quando cliente tem carrinho e envia dto valido")
	void setarQuantidadeProdutoNoCarrinhoPorIdToken_ClienteTemCarrinhoEDtoValido_NaoDeveLancarExecao() {
		//ARRANGE
		ItemCarrinho itemCarrinho = itemCarrinhoBuilder.id(1L).quantidade(1).produtoId(1L).carrinho(carrinho).build();
		ItemCarrinho itemCarrinho2 = itemCarrinhoBuilder.id(2L).quantidade(3).produtoId(2L).carrinho(carrinho).build();
		List<ItemCarrinho> listaItemCarrinho = new ArrayList<>(List.of(itemCarrinho, itemCarrinho2));
		BDDMockito.when(usuarioAutenticadoService.getCliente()).thenReturn(cliente);
		BDDMockito.when(carrinhoUtilidadeService.pegarCarrinho(cliente)).thenReturn(carrinho);
		BDDMockito.when(carrinho.getItemsCarrinho()).thenReturn(listaItemCarrinho);
		
		//ACT
		AdicionarItemCarrinhoDTO adicionarItemCarrinhoDTO = adicionarItemCarrinhoDTOBuilder.produtoId(2L).quantidade(3).build();
		carrinhoUserService.setarQuantidadeProdutoNoCarrinhoPorIdToken(adicionarItemCarrinhoDTO);
		
		//ASSERT
		BDDMockito.then(carrinhoRepository).should().save(carrinho);
	}
	
	@Test
	@DisplayName("Setar quantidade produto no carrinho por id token deve funcionar normalmente quando envia dto valido para remover")
	void setarQuantidadeProdutoNoCarrinhoPorIdToken_DtoValidoParaRemover_NaoDeveLancarExecao() {
		//ARRANGE
		ItemCarrinho itemCarrinho = itemCarrinhoBuilder.id(1L).quantidade(1).produtoId(1L).carrinho(carrinho).build();
		ItemCarrinho itemCarrinho2 = itemCarrinhoBuilder.id(2L).quantidade(3).produtoId(2L).carrinho(carrinho).build();
		List<ItemCarrinho> listaItemCarrinho = new ArrayList<>(List.of(itemCarrinho, itemCarrinho2));
		BDDMockito.when(usuarioAutenticadoService.getCliente()).thenReturn(cliente);
		BDDMockito.when(carrinhoUtilidadeService.pegarCarrinho(cliente)).thenReturn(carrinho);
		BDDMockito.when(carrinho.getItemsCarrinho()).thenReturn(listaItemCarrinho);
		
		//ACT
		AdicionarItemCarrinhoDTO adicionarItemCarrinhoDTO = adicionarItemCarrinhoDTOBuilder.produtoId(2L).quantidade(0).build();
		carrinhoUserService.setarQuantidadeProdutoNoCarrinhoPorIdToken(adicionarItemCarrinhoDTO);
		
		//ASSERT
		BDDMockito.then(carrinhoRepository).should().save(carrinho);
	}
}
