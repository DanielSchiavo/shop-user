package br.com.danielschiavo.shop.service.cliente;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.danielschiavo.infra.security.UsuarioAutenticadoService;
import br.com.danielschiavo.repository.cliente.CarrinhoRepository;
import br.com.danielschiavo.service.cliente.CarrinhoUtilidadeService;
import br.com.danielschiavo.shop.model.ValidacaoException;
import br.com.danielschiavo.shop.model.cliente.Cliente;
import br.com.danielschiavo.shop.model.cliente.carrinho.Carrinho;
import br.com.danielschiavo.shop.model.cliente.carrinho.MostrarCarrinhoClienteDTO;
import br.com.danielschiavo.shop.model.cliente.carrinho.itemcarrinho.AdicionarItemCarrinhoDTO;
import br.com.danielschiavo.shop.model.cliente.carrinho.itemcarrinho.ItemCarrinho;
import br.com.danielschiavo.shop.service.cliente.mapper.CarrinhoMapper;
import lombok.Setter;

@Service
@Setter
public class CarrinhoUserService {

	@Autowired
	private UsuarioAutenticadoService usuarioAutenticadoService;

	@Autowired
	private CarrinhoRepository carrinhoRepository;
	
	@Autowired
	private CarrinhoMapper carrinhoMapper;
	
	@Autowired
	private CarrinhoUtilidadeService carrinhoUtilidadeService;
	
	@Transactional
	public void deletarProdutoNoCarrinhoPorIdToken(Long produtoId) {
		Cliente cliente = usuarioAutenticadoService.getCliente();
		Carrinho carrinho = carrinhoUtilidadeService.pegarCarrinho(cliente);
		
		Iterator<ItemCarrinho> iterator = carrinho.getItemsCarrinho().iterator();
		while (iterator.hasNext()) {
			ItemCarrinho itemCarrinho = iterator.next();
			if (itemCarrinho.getProdutoId() == produtoId) {
				iterator.remove();
				carrinhoRepository.save(carrinho);
				return;
			}
		}
		throw new ValidacaoException("Não existe produto de id número " + produtoId + " no carrinho");
	}
	
	@Transactional
	public MostrarCarrinhoClienteDTO pegarCarrinhoClientePorIdToken() {
		Cliente cliente = usuarioAutenticadoService.getCliente();
		Carrinho carrinho = carrinhoUtilidadeService.pegarCarrinho(cliente);

		return carrinhoMapper.carrinhoParaMostrarCarrinhoClienteDTO(carrinho);
	}
	
	@Transactional
	public void adicionarProdutosNoCarrinhoPorIdToken(AdicionarItemCarrinhoDTO itemCarrinhoDTO) {
		if (itemCarrinhoDTO.quantidade() <= 0) {
			throw new ValidacaoException("A quantidade do produto deve ser maior ou igual a 1, o valor fornecido foi: "
					+ itemCarrinhoDTO.quantidade());
		}
		
		Cliente cliente = usuarioAutenticadoService.getCliente();
		Carrinho carrinho = carrinhoUtilidadeService.pegarCarrinho(cliente);

		List<ItemCarrinho> itensCarrinho = carrinho.getItemsCarrinho();
		for (ItemCarrinho item : itensCarrinho) {
			if (item.getProdutoId() == itemCarrinhoDTO.produtoId()) {
				item.setQuantidade(item.getQuantidade() + itemCarrinhoDTO.quantidade());
				carrinhoRepository.save(carrinho);
				return;
			}
		}
		ItemCarrinho itemCarrinho = ItemCarrinho.builder()
												.id(null)
												.quantidade(itemCarrinhoDTO.quantidade())
												.produtoId(itemCarrinhoDTO.produtoId())
												.subTotal(BigDecimal.ONE)
												.dataEHoraInsercao(LocalDateTime.now())
												.carrinho(carrinho).build();
		carrinho.getItemsCarrinho().add(itemCarrinho);
		carrinhoRepository.save(carrinho);
	}

	@Transactional
	public void setarQuantidadeProdutoNoCarrinhoPorIdToken(AdicionarItemCarrinhoDTO itemCarrinhoDTO) {
		Cliente cliente = usuarioAutenticadoService.getCliente();
		Carrinho carrinho = carrinhoUtilidadeService.pegarCarrinho(cliente);

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
