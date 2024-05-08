package br.com.danielschiavo.shop.service.pedido;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.danielschiavo.infra.security.UsuarioAutenticadoService;
import br.com.danielschiavo.repository.pedido.PedidoRepository;
import br.com.danielschiavo.shop.model.ValidacaoException;
import br.com.danielschiavo.shop.model.cliente.Cliente;
import br.com.danielschiavo.shop.model.cliente.cartao.Cartao;
import br.com.danielschiavo.shop.model.cliente.endereco.Endereco;
import br.com.danielschiavo.shop.model.pedido.Pedido;
import br.com.danielschiavo.shop.model.pedido.Pedido.PedidoBuilder;
import br.com.danielschiavo.shop.model.pedido.dto.CriarPedidoDTO;
import br.com.danielschiavo.shop.model.pedido.dto.MostrarPedidoDTO;
import br.com.danielschiavo.shop.model.pedido.dto.MostrarProdutoDoPedidoDTO;
import br.com.danielschiavo.shop.model.pedido.itempedido.ItemPedido;
import br.com.danielschiavo.shop.model.pedido.itempedido.ItemPedido.ItemPedidoBuilder;
import br.com.danielschiavo.shop.model.pedido.pagamento.MetodoPagamento;
import br.com.danielschiavo.shop.service.pedido.feign.CarrinhoServiceClient;
import br.com.danielschiavo.shop.service.pedido.feign.CartaoServiceClient;
import br.com.danielschiavo.shop.service.pedido.feign.FileStoragePedidoService;
import br.com.danielschiavo.shop.service.pedido.feign.PedidoImagemProdutoRequest;
import br.com.danielschiavo.shop.service.pedido.feign.ProdutoPrimeiraImagemEAtivoResponse;
import br.com.danielschiavo.shop.service.pedido.feign.ProdutoServiceClient;
import br.com.danielschiavo.shop.service.pedido.feign.endereco.EnderecoServiceClient;
import br.com.danielschiavo.shop.service.pedido.validacoes.ValidadorCriarNovoPedido;
import jakarta.transaction.Transactional;
import lombok.Setter;

@Service
@Setter
public class PedidoUserService {

	@Autowired
	private PedidoRepository pedidoRepository;

	@Autowired
	private UsuarioAutenticadoService usuarioAutenticadoService;

	@Autowired
	private FileStoragePedidoService fileStoragePedidoService;

	@Autowired
	private ProdutoServiceClient produtoServiceClient;
	
	@Autowired
	private EnderecoServiceClient enderecoServiceClient;
	
	@Autowired
	private CartaoServiceClient cartaoServiceClient;
	
	@Autowired
	private CarrinhoServiceClient carrinhoServiceClient;
	
	@Autowired
	private List<ValidadorCriarNovoPedido> validador;
	
	@Autowired
	private PedidoMapper pedidoMapper;
	
	private PedidoBuilder pedidoBuilder = Pedido.builder();
	
	private ItemPedidoBuilder itemPedidoBuilder = ItemPedido.builder();

	public Page<MostrarPedidoDTO> pegarPedidosClientePorIdToken(Pageable pageable) {
		Cliente cliente = usuarioAutenticadoService.getCliente();
		Page<Pedido> pagePedidos = pedidoRepository.findAllByCliente(cliente, pageable);

		List<MostrarPedidoDTO> list = new ArrayList<>();
		for (Pedido pedido : pagePedidos) {
			List<MostrarProdutoDoPedidoDTO> listaMostrarProdutoDoPedidoDTO = pedidoMapper.pedidoParaMostrarProdutoDoPedidoDTO(pedido, fileStoragePedidoService);

			var mostrarPedidoDTO = new MostrarPedidoDTO(pedido, listaMostrarProdutoDoPedidoDTO);
			list.add(mostrarPedidoDTO);
		}
		
		return new PageImpl<>(list, pagePedidos.getPageable(),
				pagePedidos.getTotalElements());
	}
	
	@Transactional
	public MostrarPedidoDTO criarPedidoPorIdToken(CriarPedidoDTO pedidoDTO) {
		Cliente cliente = usuarioAutenticadoService.getCliente();
		String tokenComBearer = usuarioAutenticadoService.getTokenComBearer();
		validador.forEach(v -> v.validar(pedidoDTO, cliente));
		
		Pedido pedido = criarEntidadePedidoERelacionamentos(pedidoDTO, cliente, tokenComBearer);
		if (pedidoDTO.veioPeloCarrinho()) {
			List<Long> ids = pedidoDTO.items().stream().map(item -> item.idProduto()).collect(Collectors.toList());
			carrinhoServiceClient.deletarProdutosNoCarrinho(ids, tokenComBearer);
		}
		
		pedidoDTO.pagamento().metodoPagamento().getProcessador(pedidoDTO, cliente).executa();
		pedidoDTO.entrega().tipoEntrega().getProcessador(pedidoDTO, cliente).executa();
		
		pedidoRepository.save(pedido);
		
		List<MostrarProdutoDoPedidoDTO> listaMostrarProdutoDoPedidoDTO = pedidoMapper.pedidoParaMostrarProdutoDoPedidoDTO(pedido, fileStoragePedidoService);
		return new MostrarPedidoDTO(pedido, listaMostrarProdutoDoPedidoDTO);
	}
	
	
//	------------------------------
//	------------------------------
//	METODOS UTILITÁRIOS
//	------------------------------
//	------------------------------
	
	
	private Pedido criarEntidadePedidoERelacionamentos(CriarPedidoDTO pedidoDTO, Cliente cliente, String tokenComBearer) {
		PedidoBuilder pedidoBuilder = this.pedidoBuilder.cliente(cliente);
		criarESetarPagamento(pedidoDTO, pedidoBuilder, tokenComBearer);
		criarESetarEntrega(pedidoDTO, pedidoBuilder, tokenComBearer);
		criarESetarItemsPedido(pedidoDTO, pedidoBuilder);
		return pedidoBuilder.getPedido();
	}

	private PedidoBuilder criarESetarEntrega(CriarPedidoDTO pedidoDTO, PedidoBuilder pedidoBuilder, String tokenComBearer) {
		Long idEndereco = pedidoDTO.entrega().idEndereco();

		pedidoBuilder.entregaIdTipo(null, pedidoDTO.entrega().tipoEntrega());
		if (idEndereco != null) {
			Endereco endereco = enderecoServiceClient.pegarEnderecoDoClientePorId(idEndereco, tokenComBearer);
			pedidoBuilder.entregaEndereco(endereco);
		}
		return pedidoBuilder;
	}

	private void criarESetarItemsPedido(CriarPedidoDTO pedidoDTO, PedidoBuilder pedidoBuilder) {
		List<Long> produtosId = pedidoDTO.items().stream().map(item -> item.idProduto()).collect(Collectors.toList());
		List<ProdutoPrimeiraImagemEAtivoResponse> listaDadosProdutos = produtoServiceClient.pegarPrimeiraImagemEVerificarSeEstaAtivo(produtosId);
		
		listaDadosProdutos.forEach(produto -> {
			if (produto.ativo() == null || produto.primeiraImagem() == null) {
				throw new ValidacaoException("Algum produto enviado para fazer um pedido não existe");
			}
			if (produto.ativo() == false) {
				throw new ValidacaoException("Não foi possivel prosseguir com o pedido porque o produto de id número " + produto.produtoId() + " não está ativo");
			}
			String nomeImagemPedido = fileStoragePedidoService.persistirOuRecuperarImagemPedido(new PedidoImagemProdutoRequest(produto.primeiraImagem(), produto.produtoId()));
			
			Integer quantidade = pedidoDTO.items().stream().filter(item -> item.idProduto() == produto.produtoId()).findFirst().get().quantidade();
			
			ItemPedido itemPedido = itemPedidoBuilder.preco(produto.preco())
													 .quantidade(quantidade)
													 .nomeProduto(produto.nome())
													 .primeiraImagem(nomeImagemPedido)
													 .subTotal(produto.preco().multiply(BigDecimal.valueOf(quantidade)))
													 .produtoId(produto.produtoId()).build();
			pedidoBuilder.comItemPedido(itemPedido);
		});
	}

	private PedidoBuilder criarESetarPagamento(CriarPedidoDTO pedidoDTO, PedidoBuilder pedidoBuilder, String tokenComBearer) {
		MetodoPagamento metodoPagamentoDTO = pedidoDTO.pagamento().metodoPagamento();
		pedidoBuilder.pagamentoIdMetodo(null, metodoPagamentoDTO);
		
		Long idCartao = pedidoDTO.pagamento().idCartao();
		if (idCartao != null) {
			Cartao cartao = cartaoServiceClient.pegarCartao(idCartao, tokenComBearer);
			pedidoBuilder.pagamentoCartao(cartao);
			String numeroParcelas = pedidoDTO.pagamento().numeroParcelas();
			if (numeroParcelas != null) {
				pedidoBuilder.pagamentoNumeroDeParcelas(numeroParcelas);
			}
		}
		
		return pedidoBuilder;
	}
}
