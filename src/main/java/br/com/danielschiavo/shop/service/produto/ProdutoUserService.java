package br.com.danielschiavo.shop.service.produto;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.danielschiavo.mapper.ProdutoComumMapper;
import br.com.danielschiavo.service.produto.ProdutoUtilidadeService;
import br.com.danielschiavo.shop.model.pedido.dto.ProdutoPedidoDTO;
import br.com.danielschiavo.shop.model.produto.Produto;
import br.com.danielschiavo.shop.model.produto.dto.DetalharProdutoDTO;
import br.com.danielschiavo.shop.model.produto.dto.MostrarProdutosDTO;
import br.com.danielschiavo.shop.repository.produto.ProdutoRepository;
import lombok.Setter;

@Service
@Setter
public class ProdutoUserService {

	@Autowired
	private ProdutoRepository produtoRepository;

	@Autowired
	private ProdutoComumMapper produtoMapper;
	
	@Autowired
	private ProdutoUtilidadeService produtoUtilidadeService;
	
	public Page<MostrarProdutosDTO> listarProdutos(Pageable pageable) {
		Page<Produto> pageProdutos = produtoRepository.findAll(pageable);
		
	    List<MostrarProdutosDTO> listaMostrarProdutosDTO = pageProdutos.getContent().stream()
	            .map(produto -> produtoMapper.produtoParaMostrarProdutosDTO(produto, produtoUtilidadeService))
	            .collect(Collectors.toList());
	    
	    return new PageImpl<>(listaMostrarProdutosDTO, pageable, pageProdutos.getTotalElements());
	}
	
	public DetalharProdutoDTO detalharProdutoPorId(Long id) {
		Produto produto = produtoUtilidadeService.pegarProdutoPorId(id);
		return produtoMapper.produtoParaDetalharProdutoDTO(produto, produtoUtilidadeService);
	}

	public List<ProdutoPedidoDTO> detalharProdutosPorIdParaFazerPedido(List<Long> produtosId) {
		List<Produto> produto = produtoRepository.findAllByIdIn(produtosId);
		return produtoMapper.listaProdutosParaListaProdutosPedidoDTO(produto, produtoUtilidadeService);
	}
	
	
//	------------------------------
//	------------------------------
//	METODOS UTILITARIOS
//	------------------------------
//	------------------------------
	
}
