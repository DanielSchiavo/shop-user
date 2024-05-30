package br.com.danielschiavo.shop.service.produto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import br.com.danielschiavo.mapper.ProdutoComumMapper;
import br.com.danielschiavo.service.produto.ProdutoUtilidadeService;
import br.com.danielschiavo.shop.model.pedido.TipoEntrega;
import br.com.danielschiavo.shop.model.produto.Produto;
import br.com.danielschiavo.shop.model.produto.Produto.ProdutoBuilder;
import br.com.danielschiavo.shop.model.produto.arquivosproduto.ArquivoProduto;
import br.com.danielschiavo.shop.model.produto.dto.DetalharProdutoDTO;
import br.com.danielschiavo.shop.model.produto.dto.MostrarProdutosDTO;
import br.com.danielschiavo.shop.model.produto.tipoentregaproduto.TipoEntregaProduto;
import br.com.danielschiavo.shop.repository.produto.ProdutoRepository;

@ExtendWith(MockitoExtension.class)
class ProdutoUserServiceTest {
	
	@InjectMocks
	private ProdutoUserService produtoUserService;
	
	@Mock
	private ProdutoRepository produtoRepository;
	
	@Mock
	private ProdutoUtilidadeService produtoUtilidadeService;
	
	private ProdutoBuilder produtoBuilder = Produto.builder();
	
	@BeforeEach
	public void beforeEach() {
		ProdutoComumMapper produtoMapper = Mappers.getMapper(ProdutoComumMapper.class);
		produtoUserService.setProdutoMapper(produtoMapper);
	}
	
	@Test
	void listarProdutos() {
		//ARRANGE
		//Produto		
		Produto produto = produtoBuilder.id(1L)
										.nome("Mouse gamer")
										.descricao("Descricao Mouse gamer")
										.preco(BigDecimal.valueOf(200.00))
										.quantidade(100)
										.tiposEntrega(Set.of(TipoEntregaProduto.builder().id(1L).tipoEntrega(TipoEntrega.CORREIOS).build(), TipoEntregaProduto.builder().id(2L).tipoEntrega(TipoEntrega.RETIRADA_NA_LOJA).build(), TipoEntregaProduto.builder().id(3L).tipoEntrega(TipoEntrega.ENTREGA_EXPRESSA).build()))
										.arquivosProduto(Set.of(ArquivoProduto.builder().id(1L).nome("Padrao.jpeg").posicao((byte) 0).build()))
										.subCategoriaId(1L)
										.build();
		Produto produto2 = produtoBuilder.id(2L)
										 .nome("Sistema Digisat Administrador")
										 .descricao("Descricao sistema")
										 .preco(BigDecimal.valueOf(200.00))
										 .quantidade(100)
										 .tiposEntrega(Set.of(TipoEntregaProduto.builder().id(1L).tipoEntrega(TipoEntrega.ENTREGA_DIGITAL).build()))
										 .arquivosProduto(Set.of(ArquivoProduto.builder().id(1L).nome("Padrao.jpeg").posicao((byte) 0).build()))
										 .subCategoriaId(2L)
										 .build();
		List<Produto> produtos = new ArrayList<>(List.of(produto, produto2));
		
		PageRequest pageable = PageRequest.of(0, 10);
		PageImpl<Produto> pageImplProduto = new PageImpl<>(produtos, pageable, produtos.size());
		String nomePrimeiraImagem = "Padrao.jpeg";
		//When
		when(produtoRepository.findAll(pageable)).thenReturn(pageImplProduto);
		when(produtoUtilidadeService.pegarNomePrimeiraImagem(any(Produto.class))).thenReturn(nomePrimeiraImagem);
		
		//ACT
		Page<MostrarProdutosDTO> pageMostrarProdutosDTO = produtoUserService.listarProdutos(pageable);
		
		//ASSERT
		List<MostrarProdutosDTO> listaMostrarProdutos = pageMostrarProdutosDTO.getContent();
		Assertions.assertEquals(produtos.size(), listaMostrarProdutos.size());
		for (int i = 0; i < listaMostrarProdutos.size(); i ++) {
			//Produto
			Assertions.assertEquals(produtos.get(i).getId(), listaMostrarProdutos.get(i).getId());
			Assertions.assertEquals(produtos.get(i).getNome(), listaMostrarProdutos.get(i).getNome());
			Assertions.assertEquals(produtos.get(i).getPreco(), listaMostrarProdutos.get(i).getPreco());
			Assertions.assertEquals(produtos.get(i).getQuantidade(), listaMostrarProdutos.get(i).getQuantidade());
			Assertions.assertEquals(produtos.get(i).getAtivo(), listaMostrarProdutos.get(i).getAtivo());
			Assertions.assertEquals(nomePrimeiraImagem, listaMostrarProdutos.get(i).getPrimeiraImagem());
		}
	}
	
	@Test
	void detalharProdutoPorId() {
		//ARRANGE
		ArquivoProduto arquivoProduto = ArquivoProduto.builder().id(0L).nome("Padrao.jpeg").posicao((byte) 0).build();
		ArquivoProduto arquivoProduto2 = ArquivoProduto.builder().id(1L).nome("Padrao.jpeg").posicao((byte) 1).build();
		Set<ArquivoProduto> setArquivosProduto = Set.of(arquivoProduto, arquivoProduto2);
		//Produto		
		Produto produto = produtoBuilder.id(1L)
										.nome("Mouse gamer")
										.descricao("Descricao Mouse gamer")
										.preco(BigDecimal.valueOf(200.00))
										.quantidade(100)
										.tiposEntrega(Set.of(TipoEntregaProduto.builder().id(1L).tipoEntrega(TipoEntrega.CORREIOS).build(), TipoEntregaProduto.builder().id(2L).tipoEntrega(TipoEntrega.RETIRADA_NA_LOJA).build(), TipoEntregaProduto.builder().id(3L).tipoEntrega(TipoEntrega.ENTREGA_EXPRESSA).build()))
										.arquivosProduto(setArquivosProduto)
										.subCategoriaId(1L)
										.build();
		//When
		Long idProduto = 1L;
		when(produtoUtilidadeService.pegarProdutoPorId(idProduto)).thenReturn(produto);
		
		//ACT
		DetalharProdutoDTO detalharProdutoDTO = produtoUserService.detalharProdutoPorId(idProduto);
		
		//ASSERT
		//Produto
		Assertions.assertEquals(produto.getId(), detalharProdutoDTO.getId());
		Assertions.assertEquals(produto.getNome(), detalharProdutoDTO.getNome());
		Assertions.assertEquals(produto.getPreco(), detalharProdutoDTO.getPreco());
		Assertions.assertEquals(produto.getQuantidade(), detalharProdutoDTO.getQuantidade());
		Assertions.assertEquals(produto.getAtivo(), detalharProdutoDTO.getAtivo());
		
		for (int i = 0; i < detalharProdutoDTO.getArquivos().size(); i++) {
			Assertions.assertEquals(arquivoProduto.getNome(), detalharProdutoDTO.getArquivos().get(i).nomeArquivo());
		}
	}
}
