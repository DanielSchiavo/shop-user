package br.com.danielschiavo.shop.service.produto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

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

import br.com.danielschiavo.mapper.produto.ProdutoMapper;
import br.com.danielschiavo.repository.cliente.CarrinhoRepository;
import br.com.danielschiavo.repository.produto.ProdutoRepository;
import br.com.danielschiavo.repository.produto.SubCategoriaRepository;
import br.com.danielschiavo.service.produto.CategoriaUtilidadeService;
import br.com.danielschiavo.service.produto.ProdutoUtilidadeService;
import br.com.danielschiavo.shop.model.filestorage.ArquivoInfoDTO;
import br.com.danielschiavo.shop.model.pedido.TipoEntrega;
import br.com.danielschiavo.shop.model.produto.Produto;
import br.com.danielschiavo.shop.model.produto.Produto.ProdutoBuilder;
import br.com.danielschiavo.shop.model.produto.categoria.Categoria;
import br.com.danielschiavo.shop.model.produto.categoria.Categoria.CategoriaBuilder;
import br.com.danielschiavo.shop.model.produto.dto.DetalharProdutoDTO;
import br.com.danielschiavo.shop.model.produto.dto.MostrarProdutosDTO;
import br.com.danielschiavo.shop.services.filestorage.FileStorageProdutoService;

@ExtendWith(MockitoExtension.class)
class ProdutoUserServiceTest {
	
	@InjectMocks
	private ProdutoUserService produtoUserService;
	
	@Mock
	private ProdutoRepository produtoRepository;
	
	@Mock
	private CarrinhoRepository carrinhoRepository;
	
	@Mock
	private SubCategoriaRepository subCategoriaRepository;
	
	@Mock
	private FileStorageProdutoService fileStorageProdutoService;
	
	@Mock
	private ProdutoUtilidadeService produtoUtilidadeService;
	
	@Mock
	private CategoriaUtilidadeService categoriaUtilidadeService;
	
	@Mock
	private SubCategoriaUserService subCategoriaService;
	
	private ProdutoBuilder produtoBuilder = Produto.builder();
	
	private CategoriaBuilder categoriaBuilder = Categoria.builder();
	
	@BeforeEach
	public void beforeEach() {
		ProdutoMapper produtoMapper = Mappers.getMapper(ProdutoMapper.class);
		produtoUserService.setProdutoMapper(produtoMapper);
	}
	
	@Test
	void listarProdutos() {
		//ARRANGE
		List<Categoria> categorias = categoriaBuilder.categoria(1L, "Computadores")
												  .comSubCategoria(1L, "Teclado")
												  .comSubCategoria(2L, "Mouse")
											  .categoria(2L, "Softwares")
											  	  .comSubCategoria(3L, "Administrativo")
											  .getCategorias();
		//Produto		
		Produto produto = produtoBuilder.id(1L)
										.nome("Mouse gamer")
										.descricao("Descricao Mouse gamer")
										.preco(200.00)
										.quantidade(100)
										.tipoEntregaIdTipo(1L, TipoEntrega.CORREIOS)
										.tipoEntregaIdTipo(2L, TipoEntrega.RETIRADA_NA_LOJA)
										.tipoEntregaIdTipo(3L, TipoEntrega.ENTREGA_EXPRESSA)
										.arquivoProdutoIdNomePosicao(1l, "Padrao.jpeg", (byte) 0)
										.subCategoria(categorias.get(0).getSubCategorias().get(1))
										.getProduto();
		Produto produto2 = produtoBuilder.id(2L)
										 .nome("Sistema Digisat Administrador")
										 .descricao("Descricao sistema")
										 .preco(200.00)
										 .quantidade(100)
										 .tipoEntregaIdTipo(4L, TipoEntrega.ENTREGA_DIGITAL)
										 .arquivoProdutoIdNomePosicao(2L, "Padrao.jpeg", (byte) 0)
										 .subCategoria(categorias.get(1).getSubCategorias().get(0))
										 .getProduto();
		List<Produto> produtos = new ArrayList<>(List.of(produto, produto2));
		
		PageRequest pageable = PageRequest.of(0, 10);
		PageImpl<Produto> pageImplProduto = new PageImpl<>(produtos, pageable, produtos.size());
		ArquivoInfoDTO arquivoInfoDTO = new ArquivoInfoDTO("Padrao.jpeg", "Bytes do arquivo Padrao.jpeg".getBytes());
		//When
		when(produtoRepository.findAll(pageable)).thenReturn(pageImplProduto);
		when(produtoUtilidadeService.pegarNomePrimeiraImagem(any(Produto.class))).thenReturn("Padrao.jpeg");
		when(fileStorageProdutoService.pegarArquivoProdutoPorNome(any(String.class))).thenReturn(arquivoInfoDTO);
		when(categoriaUtilidadeService.verificarSeExisteCategoriaPorId(1L)).thenReturn(categorias.get(0));
		when(categoriaUtilidadeService.verificarSeExisteCategoriaPorId(2L)).thenReturn(categorias.get(1));
		
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
			Assertions.assertArrayEquals(arquivoInfoDTO.bytesArquivo(), listaMostrarProdutos.get(i).getPrimeiraImagem());
			//Categoria
			Assertions.assertEquals(produtos.get(i).getSubCategoria().getCategoria().getId(), listaMostrarProdutos.get(i).getCategoria().getId());
			Assertions.assertEquals(produtos.get(i).getSubCategoria().getCategoria().getNome(), listaMostrarProdutos.get(i).getCategoria().getNome());
			//SubCategoria
			Assertions.assertEquals(produtos.get(i).getSubCategoria().getId(), listaMostrarProdutos.get(i).getCategoria().getSubCategoria().id());
			Assertions.assertEquals(produtos.get(i).getSubCategoria().getNome(), listaMostrarProdutos.get(i).getCategoria().getSubCategoria().nome());
		}
	}
	
	@Test
	void detalharProdutoPorId() {
		//ARRANGE
		Categoria categoria = categoriaBuilder.categoria(1L, "Computadores")
														  .comSubCategoria(1L, "Teclado")
														  .comSubCategoria(2L, "Mouse")
													  .getCategoria();
		//Produto		
		Produto produto = produtoBuilder.id(1L)
										.nome("Mouse gamer")
										.descricao("Descricao Mouse gamer")
										.preco(200.00)
										.quantidade(100)
										.tipoEntregaIdTipo(1L, TipoEntrega.CORREIOS)
										.tipoEntregaIdTipo(2L, TipoEntrega.RETIRADA_NA_LOJA)
										.tipoEntregaIdTipo(3L, TipoEntrega.ENTREGA_EXPRESSA)
										.arquivoProdutoIdNomePosicao(1L, "Padrao.jpeg", (byte) 0)
										.arquivoProdutoIdNomePosicao(2L, "Padrao.jpeg", (byte) 1)
										.subCategoria(categoria.getSubCategorias().get(1))
										.getProduto();
		ArquivoInfoDTO arquivoInfoDTO = new ArquivoInfoDTO("Padrao.jpeg", "Bytes do arquivo Padrao.jpeg".getBytes());
		ArquivoInfoDTO arquivoInfoDTO2 = new ArquivoInfoDTO("Padrao.jpeg", "Bytes do arquivo Padrao.jpeg".getBytes());
		List<ArquivoInfoDTO> listaArquivosInfoDTO = new ArrayList<>(List.of(arquivoInfoDTO, arquivoInfoDTO2));
		//When
		Long idProduto = 1L;
		when(produtoUtilidadeService.verificarSeProdutoExistePorId(idProduto)).thenReturn(produto);
		when(produtoUtilidadeService.pegarNomeTodosArquivos(any(Produto.class))).thenReturn(List.of("Padrao.jpeg", "Padrao.jpeg"));
		when(fileStorageProdutoService.mostrarArquivoProdutoPorListaDeNomes(any())).thenReturn(listaArquivosInfoDTO);
		when(categoriaUtilidadeService.verificarSeExisteCategoriaPorId(1L)).thenReturn(categoria);
		
		//ACT
		DetalharProdutoDTO detalharProdutoDTO = produtoUserService.detalharProdutoPorId(idProduto);
		
		//ASSERT
		//Produto
//		Assertions.assertEquals(produto.getId(), detalharProdutoDTO.getId());
		Assertions.assertEquals(produto.getNome(), detalharProdutoDTO.getNome());
		Assertions.assertEquals(produto.getPreco(), detalharProdutoDTO.getPreco());
		Assertions.assertEquals(produto.getQuantidade(), detalharProdutoDTO.getQuantidade());
		Assertions.assertEquals(produto.getAtivo(), detalharProdutoDTO.getAtivo());
		detalharProdutoDTO.getArquivos().forEach(arquivo -> {
			Assertions.assertEquals(arquivoInfoDTO.nomeArquivo(), arquivo.nomeArquivo());
			Assertions.assertArrayEquals(arquivoInfoDTO.bytesArquivo(), arquivo.bytesArquivo());
		});
		// Categoria
		Assertions.assertEquals(categoria.getId(), detalharProdutoDTO.getCategoria().getId());
		Assertions.assertEquals(categoria.getNome(), detalharProdutoDTO.getCategoria().getNome());
		// SubCategoria
		Assertions.assertEquals(produto.getSubCategoria().getId(), detalharProdutoDTO.getCategoria().getSubCategoria().id());
		Assertions.assertEquals(produto.getSubCategoria().getNome(), detalharProdutoDTO.getCategoria().getSubCategoria().nome());
	}
}
