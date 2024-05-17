package br.com.danielschiavo.shop.infra.inserirdados;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.danielschiavo.shop.model.cliente.Cliente;
import br.com.danielschiavo.shop.model.cliente.Cliente.ClienteBuilder;
import br.com.danielschiavo.shop.model.cliente.cartao.Cartao;
import br.com.danielschiavo.shop.model.cliente.cartao.Cartao.CartaoBuilder;
import br.com.danielschiavo.shop.model.cliente.cartao.TipoCartao;
import br.com.danielschiavo.shop.model.cliente.endereco.Endereco;
import br.com.danielschiavo.shop.model.cliente.endereco.Endereco.EnderecoBuilder;
import br.com.danielschiavo.shop.model.cliente.role.NomeRole;
import br.com.danielschiavo.shop.model.cliente.role.Role;
import br.com.danielschiavo.shop.model.cliente.role.Role.RoleBuilder;
import br.com.danielschiavo.shop.model.pedido.Pedido;
import br.com.danielschiavo.shop.model.pedido.Pedido.PedidoBuilder;
import br.com.danielschiavo.shop.model.pedido.TipoEntrega;
import br.com.danielschiavo.shop.model.produto.Produto;
import br.com.danielschiavo.shop.model.produto.Produto.ProdutoBuilder;
import br.com.danielschiavo.shop.model.produto.arquivosproduto.ArquivoProduto;
import br.com.danielschiavo.shop.model.produto.categoria.Categoria;
import br.com.danielschiavo.shop.model.produto.categoria.Categoria.CategoriaBuilder;
import br.com.danielschiavo.shop.model.produto.tipoentregaproduto.TipoEntregaProduto;
import br.com.danielschiavo.shop.repository.cliente.ClienteRepository;
import br.com.danielschiavo.shop.repository.pedido.PedidoRepository;
import br.com.danielschiavo.shop.repository.produto.CategoriaRepository;
import br.com.danielschiavo.shop.repository.produto.ProdutoRepository;

@Profile("dev")
@Component
public class InicializacaoDevEnv implements CommandLineRunner {
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	@Autowired
	private ProdutoRepository produtoRepository;
	
	@Autowired
	private PedidoRepository pedidoRepository;
	
	@Autowired
	private LimpadorBancoDeDados limpadorBancoDeDados;
	
	private ProdutoBuilder produtoBuilder = Produto.builder();
	private CategoriaBuilder categoriaBuilder = Categoria.builder();
	
	private EnderecoBuilder enderecoBuilder = Endereco.builder();
	private CartaoBuilder cartaoBuilder = Cartao.builder();
	private ClienteBuilder clienteBuilder = Cliente.builder();
	private RoleBuilder roleBuilder = Role.builder();
	
	private PedidoBuilder pedidoBuilder = Pedido.builder();
	
	@Override
	@Transactional
	public void run(String... args) throws Exception {
		limpadorBancoDeDados.limpar();
		
		inserirDados();
	}

	public void inserirDados() {
		
		List<Categoria> categorias = 
				categoriaBuilder
				.categoria(null, "Computadores")
							.comSubCategoria(null, "Teclado")
							.comSubCategoria(null, "Mouse")
							.comSubCategoria(null, "SSD")
							.comSubCategoria(null, "Placa de Video")
				.categoria(null, "Softwares")
							.comSubCategoria(null, "Sistema Administrativo")
							.comSubCategoria(null, "Automacao")
				.getCategorias();
		categoriaRepository.saveAll(categorias);
		
		 Produto produto = produtoBuilder.id(null)
									 	  .nome("Teclado RedDragon switch vermelho")
										  .descricao("Teclado reddragon, switch vermelho, sem teclado numérico pt-br, com leds, teclas macro, switch óptico, teclas anti-desgaste")
										  .preco(BigDecimal.valueOf(200.00))
										  .quantidade(999)
										  .ativo(true)
										  .subCategoriaId(categorias.get(0).getSubCategorias().get(0).getId()).build();
		
		 TipoEntregaProduto tipoEntregaProduto = TipoEntregaProduto.builder()
																   .tipoEntrega(TipoEntrega.RETIRADA_NA_LOJA)
																   .produto(produto).build();
		 
		 produto.adicionarTipoEntrega(tipoEntregaProduto);
		 
		 ArquivoProduto arquivoProduto = ArquivoProduto.builder()
				 									   .nome("Padrao.jpeg")
				 									   .posicao((byte) 0)
				 									   .produto(produto).build();
		 
		 ArquivoProduto arquivoProduto3 = ArquivoProduto.builder()
													   .nome("teste.jpeg")
													   .posicao((byte) 1)
													   .produto(produto).build();
		 
		 produto.adicionarArquivoProduto(arquivoProduto);
		 produto.adicionarArquivoProduto(arquivoProduto3);
		 
		 
		 
		 Produto produto2 = produtoBuilder.id(null)
									 	  .nome("Mouse RedDragon")
										  .descricao("Descricao mouse reddragon")
										  .preco(BigDecimal.valueOf(200.00))
										  .quantidade(999)
										  .ativo(true)
										  .subCategoriaId(categorias.get(0).getSubCategorias().get(0).getId())
										  .build();
		 
		 TipoEntregaProduto tipoEntregaProduto2 = TipoEntregaProduto.builder()
																   .tipoEntrega(TipoEntrega.RETIRADA_NA_LOJA)
																   .produto(produto).build();

		produto2.adicionarTipoEntrega(tipoEntregaProduto2);
		
		ArquivoProduto arquivoProduto2 = ArquivoProduto.builder()
													   .nome("Padrao.jpeg")
													   .posicao((byte) 0)
													   .produto(produto2).build();
												
		produto2.adicionarArquivoProduto(arquivoProduto2);
		 
		 
		
		produtoRepository.saveAll(List.of(produto, produto2));

		Cliente cliente = clienteBuilder
						.id(1L)
						.cpf("12345678912")
						.nome("Daniel")
						.sobrenome("Schiavo Rosseto")
						.dataNascimento(LocalDate.of(2000, 3, 3))
						.dataCriacaoConta(LocalDate.now())
						.email("daniel.schiavo35@gmail.com")
						.senha("$2a$12$g/401MRFl.y7b4x5jOPjeu5d31oI9a.uI9WL1pWXR.0ocFj9J/DNu")
						.celular("27996121255")
						.fotoPerfil("Padrao.jpeg")
						.build();
		
		Role role = roleBuilder.id(null)
							   .role(NomeRole.ADMIN)
							   .dataEHoraAtribuicao(LocalDateTime.now())
							   .cliente(cliente).build();
		
		Endereco endereco = enderecoBuilder.id(null)
										  .cep("29142298")
										  .rua("NaoSeiONome")
										  .numero("15")
										  .complemento(null)
										  .bairro("Itapua")
										  .cidade("Vila Velha")
										  .estado("ES")
										  .enderecoPadrao(true)
										  .cliente(cliente).build();
		
		Cartao cartao = cartaoBuilder.id(null)
									  .nomeBanco("Santander")
									  .numeroCartao("1123444255591132")
									  .nomeNoCartao("Daniel Schiavo Rosseto")
									  .validadeCartao("03/25")
									  .cartaoPadrao(true)
									  .tipoCartao(TipoCartao.CREDITO)
									  .cliente(cliente).build();
		
		cliente.adicionarRole(role);
		cliente.adicionarEndereco(endereco);
		cliente.adicionarCartao(cartao);
		
		Cliente cliente2 = clienteBuilder.id(null)
										.cpf("12345678994")
										.nome("Silvana")
										.sobrenome("Pereira da silva")
										.dataNascimento(LocalDate.of(2000, 5, 3))
										.dataCriacaoConta(LocalDate.now())
										.email("silvana.dasilva@gmail.com")
										.senha("$2a$12$g/401MRFl.y7b4x5jOPjeu5d31oI9a.uI9WL1pWXR.0ocFj9J/DNu")
										.celular("27999833653")
										.fotoPerfil("Padrao.jpeg").build();
		
		Endereco endereco2 = enderecoBuilder.id(null)
											  .cep("29142298")
											  .rua("Avenida luciano das neves")
											  .numero("3233")
											  .complemento("Apartamento 302")
											  .bairro("Praia de itaparica")
											  .cidade("Vila Velha")
											  .estado("ES")
											  .enderecoPadrao(true)
											  .cliente(cliente2).build();
		
		Cartao cartao2 = cartaoBuilder.id(null)
									  .nomeBanco("Santander")
									  .numeroCartao("1111222244445555")
									  .nomeNoCartao("Silvana pereira da silva")
									  .validadeCartao("03/28")
									  .cartaoPadrao(true)
									  .tipoCartao(TipoCartao.CREDITO)
									  .cliente(cliente2).build();
		
		cliente2.adicionarCartao(cartao2);
		cliente2.adicionarEndereco(endereco2);
		
		
		clienteRepository.saveAll(List.of(cliente, cliente2));
//		
//		List<Pedido> pedidos = pedidoBuilder
//				.cliente(clientes.get(0))
//					 .comItemPedidoIdQuantidadeProduto(null, 2, produtos.get(0))
//					 .pagamentoIdMetodo(null, MetodoPagamento.PIX)
//					 .entregaIdTipo(null, TipoEntrega.ENTREGA_DIGITAL)
//				.cliente(clientes.get(1))
//					 .comItemPedidoIdQuantidadeProduto(null, 2, produtos.get(1))
//					 .pagamentoIdMetodo(null, MetodoPagamento.PIX)
//					 .entregaIdTipo(null, TipoEntrega.ENTREGA_DIGITAL)
//					 .getPedidos();
//		
//			
//		pedidoRepository.saveAll(pedidos);
		
	}
}
