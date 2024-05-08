package br.com.danielschiavo.shop.service.pedido;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import br.com.danielschiavo.JwtUtilTest;
import br.com.danielschiavo.feign.CarrinhoServiceClient;
import br.com.danielschiavo.feign.CartaoServiceClient;
import br.com.danielschiavo.feign.pedido.FileStoragePedidoService;
import br.com.danielschiavo.feign.pedido.RequestPedidoImagemProduto;
import br.com.danielschiavo.infra.security.UsuarioAutenticadoService;
import br.com.danielschiavo.repository.pedido.PedidoRepository;
import br.com.danielschiavo.repository.produto.ProdutoRepository;
import br.com.danielschiavo.service.produto.ProdutoUtilidadeService;
import br.com.danielschiavo.shop.model.cliente.Cliente;
import br.com.danielschiavo.shop.model.cliente.Cliente.ClienteBuilder;
import br.com.danielschiavo.shop.model.cliente.cartao.Cartao;
import br.com.danielschiavo.shop.model.cliente.cartao.TipoCartao;
import br.com.danielschiavo.shop.model.cliente.endereco.Endereco;
import br.com.danielschiavo.shop.model.cliente.endereco.Endereco.EnderecoBuilder;
import br.com.danielschiavo.shop.model.filestorage.ArquivoInfoDTO;
import br.com.danielschiavo.shop.model.pedido.Pedido;
import br.com.danielschiavo.shop.model.pedido.Pedido.PedidoBuilder;
import br.com.danielschiavo.shop.model.pedido.StatusPedido;
import br.com.danielschiavo.shop.model.pedido.TipoEntrega;
import br.com.danielschiavo.shop.model.pedido.dto.CriarPedidoDTO;
import br.com.danielschiavo.shop.model.pedido.dto.FabricaDeCriarPedidoDTO;
import br.com.danielschiavo.shop.model.pedido.dto.MostrarPedidoDTO;
import br.com.danielschiavo.shop.model.pedido.dto.MostrarProdutoDoPedidoDTO;
import br.com.danielschiavo.shop.model.pedido.entrega.EnderecoPedido;
import br.com.danielschiavo.shop.model.pedido.entrega.Entrega;
import br.com.danielschiavo.shop.model.pedido.entrega.MostrarEnderecoPedidoDTO;
import br.com.danielschiavo.shop.model.pedido.entrega.MostrarEntregaDTO;
import br.com.danielschiavo.shop.model.pedido.itempedido.ItemPedido;
import br.com.danielschiavo.shop.model.pedido.pagamento.MetodoPagamento;
import br.com.danielschiavo.shop.model.pedido.pagamento.MostrarPagamentoDTO;
import br.com.danielschiavo.shop.model.pedido.pagamento.Pagamento;
import br.com.danielschiavo.shop.model.pedido.pagamento.StatusPagamento;
import br.com.danielschiavo.shop.model.produto.Produto;
import br.com.danielschiavo.shop.model.produto.Produto.ProdutoBuilder;
import br.com.danielschiavo.shop.model.produto.categoria.Categoria;
import br.com.danielschiavo.shop.model.produto.categoria.Categoria.CategoriaBuilder;
import br.com.danielschiavo.shop.service.pedido.feign.endereco.EnderecoServiceClient;
import br.com.danielschiavo.shop.service.pedido.validacoes.ValidadorCriarNovoPedido;

@ExtendWith(MockitoExtension.class)
class PedidoUserServiceTest {
	
	@Mock
	private UsuarioAutenticadoService usuarioAutenticadoService;
	
	@InjectMocks
	private PedidoUserService pedidoUserService;
	
	@Mock
	private Cliente cliente;
	
	@Mock
	private PedidoRepository pedidoRepository;
	
	@Mock
	private FileStoragePedidoService fileStoragePedidoService;
	
	@Mock
	private ProdutoRepository produtoRepository;
	
	@Mock
	private CarrinhoServiceClient carrinhoServiceClient;
	
	@Mock
	private EnderecoServiceClient enderecoServiceClient;
	
	@Mock
	private CartaoServiceClient cartaoServiceClient;
	
	@Mock
	private ProdutoUtilidadeService produtoUtilidadeService;
	
	@Spy
	private List<ValidadorCriarNovoPedido> validadores = new ArrayList<>();

	@Mock
	private ValidadorCriarNovoPedido validador1;
	
	@Mock
	private ValidadorCriarNovoPedido validador2;
	
	private FabricaDeCriarPedidoDTO fabricaCriarPedidoDTO = new FabricaDeCriarPedidoDTO();
	
	private ProdutoBuilder produtoBuilder = Produto.builder();
	
	private EnderecoBuilder enderecoBuilder = Endereco.builder();
	
	private PedidoBuilder pedidoBuilder = Pedido.builder();
	
	private ClienteBuilder clienteBuilder = Cliente.builder();
	
	private CategoriaBuilder categoriaBuilder = Categoria.builder();
	
	private String tokenUser = "Bearer ".concat(JwtUtilTest.generateTokenUser());
	
    @BeforeEach
    void setUp() {
    	PedidoMapperImpl pedidoMapper = new PedidoMapperImpl();
    	pedidoUserService.setPedidoMapper(pedidoMapper);
    }
	
	@Test
	void pegarPedidosClientePorIdToken() {
		//ARRANGE
		//Cliente
		Cliente cliente = clienteBuilder.id(1L).cpf("12345678994").nome("Silvana").sobrenome("Pereira da silva").dataNascimento(LocalDate.of(2000, 3, 3)).dataCriacaoConta(LocalDate.now()).email("silvana.dasilva@gmail.com").senha("{noop}123456").celular("27999833653").fotoPerfil("Qualquerfoto.jpeg").getCliente();
		//Produto
		Produto produto = produtoBuilder.id(1L).nome("Mouse gamer").descricao("Descricao Mouse gamer").preco(200.00).quantidade(100).arquivoProdutoIdNomePosicao(1l, "Padrao.jpeg", (byte) 0).getProduto();
		Produto produto2 = produtoBuilder.id(2L).nome("Teclado gamer").descricao("Descricao Teclado gamer").preco(200.00).quantidade(100).arquivoProdutoIdNomePosicao(1l, "Padrao.jpeg", (byte) 0).getProduto();
		//Endereco
		Endereco endereco = enderecoBuilder.cep("12345678").rua("Divinopolis").numero("15").complemento("Sem complemento").bairro("Bela vista").cidade("Cariacica").estado("ES").build();
		//Pedido
		Pedido pedido = pedidoBuilder.cliente(cliente).comItemPedidoIdQuantidadeProduto(1L, 5, produto).comItemPedidoIdQuantidadeProduto(2L, 3, produto2).pagamentoIdMetodo(1L, MetodoPagamento.BOLETO).entregaIdTipo(1L, TipoEntrega.CORREIOS).entregaEndereco(endereco).getPedido();
		Pedido pedido2 = pedidoBuilder.cliente(cliente).comItemPedidoIdQuantidadeProduto(2L, 3, produto2).pagamentoIdMetodo(1L, MetodoPagamento.BOLETO).entregaIdTipo(1L, TipoEntrega.CORREIOS).entregaEndereco(endereco).getPedido();
		List<Pedido> listaPedido = new ArrayList<>(List.of(pedido, pedido2));
		Page<Pedido> pagePedido =  new PageImpl<>(listaPedido);
		
		byte[] bytesImagem = "Hello world".getBytes();
		ArquivoInfoDTO arquivoInfoDTO = new ArquivoInfoDTO("Padrao.jpeg", bytesImagem);
		when(fileStoragePedidoService.pegarImagemPedido(any())).thenReturn(arquivoInfoDTO);
		when(usuarioAutenticadoService.getCliente()).thenReturn(cliente);
		when(usuarioAutenticadoService.getTokenComBearer()).thenReturn(tokenUser);
		Pageable pageable = PageRequest.of(0, 10);
		when(pedidoRepository.findAllByCliente(cliente, pageable)).thenReturn(pagePedido);
		
		//ACT
		Page<MostrarPedidoDTO> pageMostrarPedidoDTO = pedidoUserService.pegarPedidosClientePorIdToken(pageable);
		
		//ASSERT
		Assertions.assertEquals(pagePedido.getTotalElements(), pageMostrarPedidoDTO.getTotalElements(), "O número total de elementos deve ser igual");
		List<MostrarPedidoDTO> listaMostrarPedidoDTO = pageMostrarPedidoDTO.getContent();

	    for (int i = 0; i < listaPedido.size(); i++) {
	        Pedido pedidoVerificar = listaPedido.get(i);
	        MostrarPedidoDTO dto = listaMostrarPedidoDTO.get(i);
	        
	        Assertions.assertEquals(pedidoVerificar.getCliente().getId(), dto.idCliente());
	        Assertions.assertEquals(pedidoVerificar.getValorTotal(), dto.valorTotal());
	        Assertions.assertEquals(pedidoVerificar.getDataPedido(), dto.dataPedido());
	        Assertions.assertEquals(pedidoVerificar.getStatusPedido(), dto.statusPedido());
	        
	        // Comparando entrega
	        Entrega entregaVerificar = pedidoVerificar.getEntrega();
	        MostrarEntregaDTO entregaDTO = dto.entrega();
	        Assertions.assertEquals(entregaVerificar.getTipoEntrega(), entregaDTO.tipoEntrega());
	        
	        // Comparando endereço de entrega
	        EnderecoPedido enderecoPedido = entregaVerificar.getEnderecoPedido();
	        MostrarEnderecoPedidoDTO enderecoDTO = entregaDTO.endereco();
	        if (enderecoPedido != null && enderecoDTO != null) {
	            Assertions.assertEquals(enderecoPedido.getCep(), enderecoDTO.cep());
	            Assertions.assertEquals(enderecoPedido.getRua(), enderecoDTO.rua());
	            Assertions.assertEquals(enderecoPedido.getNumero(), enderecoDTO.numero());
	            Assertions.assertEquals(enderecoPedido.getComplemento(), enderecoDTO.complemento());
	            Assertions.assertEquals(enderecoPedido.getBairro(), enderecoDTO.bairro());
	            Assertions.assertEquals(enderecoPedido.getCidade(), enderecoDTO.cidade());
	            Assertions.assertEquals(enderecoPedido.getEstado(), enderecoDTO.estado());
	        }
	        
	        // Comparando pagamento
	        Pagamento pagamentoVerificar = pedidoVerificar.getPagamento();
	        MostrarPagamentoDTO pagamentoDTO = dto.pagamento();
	        Assertions.assertEquals(pagamentoVerificar.getMetodoPagamento(), pagamentoDTO.metodoPagamento());
	        Assertions.assertEquals(pagamentoVerificar.getStatusPagamento(), pagamentoDTO.statusPagamento());
	        
	        // Comparando itens do pedido
	        List<ItemPedido> itensPedido = pedidoVerificar.getItemsPedido();
	        List<MostrarProdutoDoPedidoDTO> itensPedidoDTO = dto.produtos();
	        Assertions.assertEquals(itensPedido.size(), itensPedidoDTO.size(), "Os tamanhos das listas de itens do pedido devem ser iguais");
	        
	        for (int j = 0; j < itensPedido.size(); j++) {
	            ItemPedido item = itensPedido.get(j);
	            MostrarProdutoDoPedidoDTO itemDTO = itensPedidoDTO.get(j);
	            Assertions.assertEquals(item.getProdutoId(), itemDTO.idProduto());
	            Assertions.assertEquals(item.getNomeProduto(), itemDTO.nomeProduto());
	            Assertions.assertEquals(item.getPreco(), itemDTO.preco());
	            Assertions.assertEquals(item.getQuantidade(), itemDTO.quantidade());
	            Assertions.assertEquals(item.getSubTotal(), itemDTO.subTotal());
	            Assertions.assertArrayEquals(bytesImagem, itemDTO.primeiraImagem());
	        }
	    }
	}
	
	@Test
	void criarPedidoPorIdToken_CompraPeloBotaoComprarAgora() {
		//ARRANGE
		validadores.addAll(List.of(validador1, validador2));
		//Categoria
		Categoria categoria = categoriaBuilder
						.categoria(1L, "Softwares")
								.comSubCategoria(1L, "Sistema Administrativo")
						.getCategoria();
		//Produto
		Produto produto = produtoBuilder
				  .id(1L)
			 	  .nome("Teclado RedDragon switch vermelho")
				  .descricao("Teclado reddragon, switch vermelho, sem teclado numérico pt-br, com leds, teclas macro, switch óptico, teclas anti-desgaste")
				  .preco(200.00)
				  .quantidade(999)
				  .ativo(true)
				  .tipoEntregaIdTipo(1L, TipoEntrega.ENTREGA_DIGITAL)
				  .arquivoProdutoIdNomePosicao(1L, "Padrao.jpeg", (byte) 0)
				  .subCategoria(categoria.getSubCategorias().get(0))
				  .getProduto();
		//When
		ArquivoInfoDTO arquivoInfoDTO = new ArquivoInfoDTO("Padrao.jpeg", "Qualquercoisa".getBytes());
		when(produtoUtilidadeService.pegarProdutoPorIdEAtivoTrue(1L)).thenReturn(produto);
		when(usuarioAutenticadoService.getCliente()).thenReturn(cliente);
		when(usuarioAutenticadoService.getTokenComBearer()).thenReturn(tokenUser);
		when(produtoUtilidadeService.pegarNomePrimeiraImagem(produto)).thenReturn(arquivoInfoDTO.nomeArquivo());
		when(fileStoragePedidoService.persistirOuRecuperarImagemPedido(new RequestPedidoImagemProduto(produto.getArquivosProduto().get(0).getNome(), produto.getId()))).thenReturn("Padrao.jpeg");
		when(fileStoragePedidoService.pegarImagemPedido(any())).thenReturn(arquivoInfoDTO);
		
		//ACT
		CriarPedidoDTO criarPedidoDTO = fabricaCriarPedidoDTO
				.iniciarNovoPedido()
						.comMetodoPagamento(MetodoPagamento.BOLETO)
						.comTipoEntrega(TipoEntrega.ENTREGA_DIGITAL)
						.veioPeloCarrinho(false)
						.comItem(1L, 3)
				.getPedido();
		MostrarPedidoDTO mostrarPedidoDTO = pedidoUserService.criarPedidoPorIdToken(criarPedidoDTO);
		
		//ASSERT
		BDDMockito.then(validador1).should().validar(criarPedidoDTO, cliente);
		BDDMockito.then(validador2).should().validar(criarPedidoDTO, cliente);
		
		//Comparando MostrarPedidoDTO
		Assertions.assertEquals(cliente.getId(), mostrarPedidoDTO.idCliente());
		Assertions.assertEquals(BigDecimal.valueOf(600.00), mostrarPedidoDTO.valorTotal());
		Assertions.assertNotNull(mostrarPedidoDTO.dataPedido());
		Assertions.assertEquals(StatusPedido.A_PAGAR, mostrarPedidoDTO.statusPedido());
		
		//Comparando MostrarEntregaDTO
		MostrarEntregaDTO mostrarEntregaDTO = mostrarPedidoDTO.entrega();
		Assertions.assertEquals(criarPedidoDTO.entrega().tipoEntrega(), mostrarEntregaDTO.tipoEntrega());
		Assertions.assertNull(mostrarEntregaDTO.endereco());
	
		//Comparando MostrarPagamentoDTO
		MostrarPagamentoDTO mostrarPagamentoDTO = mostrarPedidoDTO.pagamento();
		Assertions.assertEquals(MetodoPagamento.BOLETO, mostrarPagamentoDTO.metodoPagamento());
		Assertions.assertEquals(StatusPagamento.PENDENTE, mostrarPagamentoDTO.statusPagamento());
		
		//Comparando MostrarProdutosDoPedidoDTO
		Assertions.assertEquals(produto.getId(), mostrarPedidoDTO.produtos().get(0).idProduto());
		Assertions.assertEquals(produto.getNome(), mostrarPedidoDTO.produtos().get(0).nomeProduto());
		Assertions.assertEquals(produto.getPreco(), mostrarPedidoDTO.produtos().get(0).preco());
		Assertions.assertEquals(criarPedidoDTO.items().get(0).quantidade(), mostrarPedidoDTO.produtos().get(0).quantidade());
		mostrarPedidoDTO.produtos().forEach(p -> Assertions.assertNotNull(p.subTotal()));
		Assertions.assertEquals(arquivoInfoDTO.bytesArquivo(), mostrarPedidoDTO.produtos().get(0).primeiraImagem());
	}
	
	@Test
	void criarPedidoBotaoComprarAgoraEComprarDoCarrinhoPorIdToken_CompraPeloCarrinho() {
		//ARRANGE
		validadores.addAll(List.of(validador1, validador2));
		//Cartao
		Cartao cartao = new Cartao(1L, "Santander", "1123444255591132", "Daniel schiavo rosseto", "03/25", true, TipoCartao.CREDITO, cliente);
		//Endereco
		Endereco endereco = new Endereco(1L, "12345621", "Divinopolis", "15", "Sem complemento", "Bela vista", "Cariacica", "ES", true, cliente);
		//Categoria
		Categoria categoria = categoriaBuilder
						.categoria(1L, "Softwares")
								.comSubCategoria(1L, "Sistema Administrativo")
						.getCategoria();
		//Produto
		Produto produto = produtoBuilder.id(1L).nome("Mouse gamer").descricao("Descricao Mouse gamer").preco(200.00).quantidade(100).arquivoProdutoIdNomePosicao(1l, "Padrao.jpeg", (byte) 0).getProduto();
		Produto produto2 = produtoBuilder.id(2L).nome("Teclado RedDragon switch vermelho").descricao("Teclado reddragon descricao").preco(200.00).quantidade(999).ativo(true).tipoEntregaIdTipo(null, TipoEntrega.RETIRADA_NA_LOJA).arquivoProdutoIdNomePosicao(null, "Padrao.jpeg", (byte) 0).subCategoria(categoria.getSubCategorias().get(0)).getProduto();
		List<Produto> produtos = new ArrayList<>(List.of(produto, produto2));
		//When
		ArquivoInfoDTO arquivoInfoDTO = new ArquivoInfoDTO("Padrao.jpeg", "Bytes arquivo padrao.jpeg".getBytes());
		when(produtoUtilidadeService.pegarProdutoPorIdEAtivoTrue(1L)).thenReturn(produto);
		when(produtoUtilidadeService.pegarProdutoPorIdEAtivoTrue(2L)).thenReturn(produto2);
		when(enderecoServiceClient.pegarEnderecoDoClientePorId(1L, tokenUser)).thenReturn(endereco);
		when(cartaoServiceClient.pegarCartao(1L, tokenUser)).thenReturn(cartao);
		when(usuarioAutenticadoService.getCliente()).thenReturn(cliente);
		when(usuarioAutenticadoService.getTokenComBearer()).thenReturn(tokenUser);
		when(produtoUtilidadeService.pegarNomePrimeiraImagem(produto)).thenReturn(arquivoInfoDTO.nomeArquivo());
		when(fileStoragePedidoService.persistirOuRecuperarImagemPedido(new RequestPedidoImagemProduto(produto.getArquivosProduto().get(0).getNome(), produto.getId()))).thenReturn("Padrao.jpeg");
		when(fileStoragePedidoService.pegarImagemPedido(any())).thenReturn(arquivoInfoDTO);
		
		//ACT
		CriarPedidoDTO criarPedidoDTO = fabricaCriarPedidoDTO
				.iniciarNovoPedido()
						.comMetodoPagamento(MetodoPagamento.CARTAO_CREDITO)
						.comCartaoENumeroParcelas(cartao.getId(), "10")
						.comTipoEntrega(TipoEntrega.CORREIOS)
						.comEndereco(endereco.getId())
						.veioPeloCarrinho(true)
						.comItem(1L, 3)
						.comItem(2L, 1)
				.getPedido();
		MostrarPedidoDTO mostrarPedidoDTO = pedidoUserService.criarPedidoPorIdToken(criarPedidoDTO);
		
		//ASSERT
		BDDMockito.then(validador1).should().validar(criarPedidoDTO, cliente);
		BDDMockito.then(validador2).should().validar(criarPedidoDTO, cliente);
		BDDMockito.then(carrinhoServiceClient).should(times(1)).deletarProdutosNoCarrinho(any(), any());
		
		//Comparando MostrarPedidoDTO
		Assertions.assertEquals(cliente.getId(), mostrarPedidoDTO.idCliente());
		Assertions.assertEquals(BigDecimal.valueOf(800.00), mostrarPedidoDTO.valorTotal());
		Assertions.assertNotNull(mostrarPedidoDTO.dataPedido());
		Assertions.assertEquals(StatusPedido.A_PAGAR, mostrarPedidoDTO.statusPedido());
		
		//Comparando MostrarEntregaDTO
		MostrarEntregaDTO mostrarEntregaDTO = mostrarPedidoDTO.entrega();
		Assertions.assertEquals(criarPedidoDTO.entrega().tipoEntrega(), mostrarEntregaDTO.tipoEntrega());
		Assertions.assertEquals(endereco.getCep(), mostrarEntregaDTO.endereco().cep());
		Assertions.assertEquals(endereco.getRua(), mostrarEntregaDTO.endereco().rua());
		Assertions.assertEquals(endereco.getNumero(), mostrarEntregaDTO.endereco().numero());
		Assertions.assertEquals(endereco.getComplemento(), mostrarEntregaDTO.endereco().complemento());
		Assertions.assertEquals(endereco.getBairro(), mostrarEntregaDTO.endereco().bairro());
		Assertions.assertEquals(endereco.getCidade(), mostrarEntregaDTO.endereco().cidade());
		Assertions.assertEquals(endereco.getEstado(), mostrarEntregaDTO.endereco().estado());
	
		//Comparando MostrarPagamentoDTO
		MostrarPagamentoDTO mostrarPagamentoDTO = mostrarPedidoDTO.pagamento();
		Assertions.assertEquals(MetodoPagamento.CARTAO_CREDITO, mostrarPagamentoDTO.metodoPagamento());
		Assertions.assertEquals(StatusPagamento.EM_PROCESSAMENTO, mostrarPagamentoDTO.statusPagamento());
		Assertions.assertEquals(cartao.getNomeBanco(), mostrarPagamentoDTO.cartaoPedido().nomeBanco());
		Assertions.assertEquals(cartao.getNumeroCartao(), mostrarPagamentoDTO.cartaoPedido().numeroCartao());
		Assertions.assertEquals(cartao.getNomeNoCartao(), mostrarPagamentoDTO.cartaoPedido().nomeNoCartao());
		Assertions.assertEquals(criarPedidoDTO.pagamento().numeroParcelas(), mostrarPagamentoDTO.cartaoPedido().numeroDeParcelas());
		Assertions.assertEquals(cartao.getTipoCartao(), mostrarPagamentoDTO.cartaoPedido().tipoCartao());
		
		//Comparando MostrarProdutoDoPedidoDTO
		List<MostrarProdutoDoPedidoDTO> listaMostrarProdutoDoPedido = mostrarPedidoDTO.produtos();
		for (int i = 0; i < listaMostrarProdutoDoPedido.size(); i++) {
			Assertions.assertEquals(produtos.get(i).getId(), listaMostrarProdutoDoPedido.get(i).idProduto());
			Assertions.assertEquals(produtos.get(i).getNome(), listaMostrarProdutoDoPedido.get(i).nomeProduto());
			Assertions.assertEquals(produtos.get(i).getPreco(), listaMostrarProdutoDoPedido.get(i).preco());
			Assertions.assertEquals(criarPedidoDTO.items().get(i).quantidade(), listaMostrarProdutoDoPedido.get(i).quantidade());
			Assertions.assertNotNull(listaMostrarProdutoDoPedido.get(i).subTotal());	
			Assertions.assertEquals(arquivoInfoDTO.bytesArquivo(), listaMostrarProdutoDoPedido.get(i).primeiraImagem());
		}
	}
}
