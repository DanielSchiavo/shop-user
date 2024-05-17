package br.com.danielschiavo.shop.service.cliente;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.danielschiavo.infra.security.UsuarioAutenticadoService;
import br.com.danielschiavo.service.cliente.CarrinhoUtilidadeService;
import br.com.danielschiavo.shop.model.ValidacaoException;
import br.com.danielschiavo.shop.model.cliente.Cliente;
import br.com.danielschiavo.shop.model.cliente.carrinho.Carrinho;
import br.com.danielschiavo.shop.model.cliente.carrinho.CarrinhoNaoExisteException;
import br.com.danielschiavo.shop.model.cliente.carrinho.MostrarCarrinhoClienteDTO;
import br.com.danielschiavo.shop.model.cliente.carrinho.RemoverProdutoDoCarrinhoDTO;
import br.com.danielschiavo.shop.model.cliente.carrinho.itemcarrinho.AdicionarItemCarrinhoDTO;
import br.com.danielschiavo.shop.model.cliente.carrinho.itemcarrinho.ItemCarrinho;
import br.com.danielschiavo.shop.repository.cliente.CarrinhoRepository;
import br.com.danielschiavo.shop.repository.cliente.ClienteRepository;
import br.com.danielschiavo.shop.service.cliente.mapper.CarrinhoMapper;
import lombok.Setter;

@Service
@Setter
public class CarrinhoUserService {

	@Autowired
	private UsuarioAutenticadoService usuarioAutenticadoService;
	
	@Autowired
	private ClienteRepository clienteRepository;

	@Autowired
	private CarrinhoRepository carrinhoRepository;
	
	@Autowired
	private CarrinhoMapper carrinhoMapper;
	
	@Autowired
	private CarrinhoUtilidadeService carrinhoUtilidadeService;
	
	@Transactional
	public List<RemoverProdutoDoCarrinhoDTO> deletarProdutoNoCarrinhoPorIdToken(List<Long> produtosId) {
		Cliente cliente = usuarioAutenticadoService.getCliente();
		Carrinho carrinho = carrinhoUtilidadeService.pegarCarrinho(cliente);
		carrinhoUtilidadeService.verificarSeTemItemsNoCarrinho(cliente, carrinho);
		
		List<RemoverProdutoDoCarrinhoDTO> removerProdutoDoCarrinho = new ArrayList<>();
		for (Long id : produtosId) {
			boolean removeu = false;
			ListIterator<ItemCarrinho> iterator = carrinho.getItemsCarrinho().listIterator();
		    while (iterator.hasNext()) {
		        ItemCarrinho itemCarrinho = iterator.next();
		        if (itemCarrinho.getProdutoId() == id) {
		            carrinho.removerItemCarrinho(itemCarrinho);
		            removeu = true;
		            break;
		        }
		    }
		    var removerProdutoDoCarrinhoDTOBuilder = RemoverProdutoDoCarrinhoDTO.builder().produtoId(id);
		    if (removeu == false) {
		    	removerProdutoDoCarrinhoDTOBuilder.erro("Não foi possivel remover o produto porque ele não está no carrinho");
		    } else {
		    	removerProdutoDoCarrinhoDTOBuilder.mensagem("Produto removido do carrinho com sucesso!");
		    }
		    removerProdutoDoCarrinho.add(removerProdutoDoCarrinhoDTOBuilder.build());
		}
		carrinhoRepository.save(carrinho);
		return removerProdutoDoCarrinho;
	}
	
	public MostrarCarrinhoClienteDTO pegarCarrinhoClientePorIdToken() {
		Cliente cliente = usuarioAutenticadoService.getCliente();
		Carrinho carrinho = carrinhoUtilidadeService.pegarCarrinho(cliente);
		carrinhoUtilidadeService.verificarSeTemItemsNoCarrinho(cliente, carrinho);

		return carrinhoMapper.carrinhoParaMostrarCarrinhoClienteDTO(carrinho);
	}
	
	@Transactional
	public String adicionarProdutosNoCarrinhoPorIdToken(AdicionarItemCarrinhoDTO itemCarrinhoDTO) {
		if (itemCarrinhoDTO.quantidade() <= 0) {
			throw new ValidacaoException("A quantidade do produto deve ser maior ou igual a 1, o valor fornecido foi: "
					+ itemCarrinhoDTO.quantidade());
		}
		
		Cliente clienteDetached = usuarioAutenticadoService.getCliente();
		//O .save(cliente) é necessario porque cliente aqui está como detached, após o .save(cliente) ele será managed, portanto, poderá ser usado para persistencia
		Cliente clienteManaged = clienteRepository.save(clienteDetached);
		Carrinho carrinho = null;
		try {
			carrinho = carrinhoUtilidadeService.pegarCarrinho(clienteManaged);
		} catch (CarrinhoNaoExisteException e) {
			carrinho = Carrinho.builder().cliente(clienteManaged).dataEHoraAtualizacao(LocalDateTime.now()).build();
		}
		
		String mensagemSucesso = "Produto adicionado no carrinho!";
		carrinho.setDataEHoraAtualizacao(LocalDateTime.now());

	    List<ItemCarrinho> itensCarrinho = carrinho.getItemsCarrinho();
	    boolean itemEncontrado = false;
	    for (ItemCarrinho item : itensCarrinho) {
	        if (item.getProdutoId() == itemCarrinhoDTO.produtoId()) {
	            item.setQuantidade(item.getQuantidade() + itemCarrinhoDTO.quantidade());
	            itemEncontrado = true;
	            break;
	        }
	    }
	    
	    if (!itemEncontrado) {
	        ItemCarrinho itemCarrinho = ItemCarrinho.builder()
									                .id(null)
									                .quantidade(itemCarrinhoDTO.quantidade())
									                .produtoId(itemCarrinhoDTO.produtoId())
									                .subTotal(BigDecimal.ONE)
									                .dataEHoraInsercao(LocalDateTime.now())
									                .carrinho(carrinho).build();
	        carrinho.adicionarItemCarrinho(itemCarrinho);
	    }
	    
	    carrinhoRepository.save(carrinho);
	    return mensagemSucesso;
	}

	@Transactional
	public void setarQuantidadeProdutoNoCarrinhoPorIdToken(AdicionarItemCarrinhoDTO itemCarrinhoDTO) {
		Cliente cliente = usuarioAutenticadoService.getCliente();
		Carrinho carrinho = carrinhoUtilidadeService.pegarCarrinho(cliente);
		carrinhoUtilidadeService.verificarSeTemItemsNoCarrinho(cliente, carrinho);

		Iterator<ItemCarrinho> iterator = carrinho.getItemsCarrinho().iterator();
		while (iterator.hasNext()) {
			ItemCarrinho itemCarrinho = iterator.next();
			if (itemCarrinho.getProdutoId() == itemCarrinhoDTO.produtoId()) {
				if (itemCarrinhoDTO.quantidade() == 0) {
					carrinho.removerItemCarrinho(itemCarrinho);
					carrinhoRepository.save(carrinho);
					return;
				}
				itemCarrinho.setQuantidade(itemCarrinhoDTO.quantidade());
				carrinhoRepository.save(carrinho);
				return;
			}
		}
		throw new ValidacaoException("Não existe produto de id número " + itemCarrinhoDTO.produtoId() + " no carrinho");
	}
}
