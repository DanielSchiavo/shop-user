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
import br.com.danielschiavo.mapper.pedido.PedidoMapper;
import br.com.danielschiavo.repository.pedido.PedidoRepository;
import br.com.danielschiavo.service.cliente.CarrinhoUtilidadeService;
import br.com.danielschiavo.service.produto.ProdutoUtilidadeService;
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
import br.com.danielschiavo.shop.model.produto.Produto;
import br.com.danielschiavo.shop.service.cliente.CartaoUserService;
import br.com.danielschiavo.shop.service.cliente.EnderecoUserService;
import br.com.danielschiavo.shop.service.pedido.validacoes.ValidadorCriarNovoPedido;
import br.com.danielschiavo.shop.services.filestorage.FileStoragePedidoService;
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
	private ProdutoUtilidadeService produtoUtilidadeService;
	
	@Autowired
	private EnderecoUserService enderecoService;
	
	@Autowired
	private CartaoUserService cartaoService;
	
	@Autowired
	private CarrinhoUtilidadeService carrinhoUtilidadeService;
	
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
		validador.forEach(v -> v.validar(pedidoDTO, cliente));
		
		Pedido pedido = criarEntidadePedidoERelacionamentos(pedidoDTO, cliente);
		if (pedidoDTO.veioPeloCarrinho()) {
			List<Long> ids = pedidoDTO.items().stream().map(item -> item.idProduto()).collect(Collectors.toList());
			carrinhoUtilidadeService.deletarItemsCarrinhoAposPedidoGerado(ids, cliente);
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
	
	
	private Pedido criarEntidadePedidoERelacionamentos(CriarPedidoDTO pedidoDTO, Cliente cliente) {
		PedidoBuilder pedidoBuilder = this.pedidoBuilder.cliente(cliente);
		criarESetarPagamento(pedidoDTO, pedidoBuilder, cliente);
		criarESetarEntrega(pedidoDTO, pedidoBuilder, cliente);
		criarESetarItemsPedido(pedidoDTO, pedidoBuilder);
		return pedidoBuilder.getPedido();
	}

	private PedidoBuilder criarESetarEntrega(CriarPedidoDTO pedidoDTO, PedidoBuilder pedidoBuilder, Cliente cliente) {
		Long idEndereco = pedidoDTO.entrega().idEndereco();

		pedidoBuilder.entregaIdTipo(null, pedidoDTO.entrega().tipoEntrega());
		if (idEndereco != null) {
			Endereco endereco = enderecoService.verificarSeEnderecoExistePorIdEnderecoECliente(idEndereco, cliente);
			pedidoBuilder.entregaEndereco(endereco);
		}
		return pedidoBuilder;
	}

	private void criarESetarItemsPedido(CriarPedidoDTO pedidoDTO, PedidoBuilder pedidoBuilder) {
		pedidoDTO.items().forEach(item -> {
			Produto produto = produtoUtilidadeService.verificarSeProdutoExistePorIdEAtivoTrue(item.idProduto());
			String first = produtoUtilidadeService.pegarNomePrimeiraImagem(produto);
			String nomeImagemPedido = fileStoragePedidoService.persistirOuRecuperarImagemPedido(first, produto.getId());
			ItemPedido itemPedido = itemPedidoBuilder.preco(produto.getPreco())
							 .quantidade(item.quantidade())
							 .nomeProduto(produto.getNome())
							 .primeiraImagem(nomeImagemPedido)
							 .subTotal(produto.getPreco().multiply(BigDecimal.valueOf(item.quantidade())))
							 .produtoId(produto.getId()).build();
			pedidoBuilder.comItemPedido(itemPedido);
		});
	}

	private PedidoBuilder criarESetarPagamento(CriarPedidoDTO pedidoDTO, PedidoBuilder pedidoBuilder, Cliente cliente) {
		MetodoPagamento metodoPagamentoDTO = pedidoDTO.pagamento().metodoPagamento();
		pedidoBuilder.pagamentoIdMetodo(null, metodoPagamentoDTO);
		
		Long idCartao = pedidoDTO.pagamento().idCartao();
		if (idCartao != null) {
			Cartao cartao = cartaoService.verificarSeCartaoExistePorIdCartaoECliente(idCartao, cliente);
			pedidoBuilder.pagamentoCartao(cartao);
			String numeroParcelas = pedidoDTO.pagamento().numeroParcelas();
			if (numeroParcelas != null) {
				pedidoBuilder.pagamentoNumeroDeParcelas(numeroParcelas);
			}
		}
		
		return pedidoBuilder;
	}
}
