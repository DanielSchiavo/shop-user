package br.com.danielschiavo.shop.service.cliente;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.danielschiavo.infra.security.UsuarioAutenticadoService;
import br.com.danielschiavo.mapper.cliente.CartaoMapper;
import br.com.danielschiavo.repository.cliente.CartaoRepository;
import br.com.danielschiavo.shop.model.ValidacaoException;
import br.com.danielschiavo.shop.model.cliente.Cliente;
import br.com.danielschiavo.shop.model.cliente.cartao.CadastrarCartaoDTO;
import br.com.danielschiavo.shop.model.cliente.cartao.Cartao;
import br.com.danielschiavo.shop.model.cliente.cartao.MostrarCartaoDTO;
import br.com.danielschiavo.shop.service.cliente.validacoes.ValidadorCadastrarNovoCartao;
import lombok.Setter;

@Service
@Setter
public class CartaoUserService {
	
	@Autowired
	private CartaoRepository cartaoRepository;
	
	@Autowired
	private UsuarioAutenticadoService usuarioAutenticadoService;
	
	@Autowired
	private List<ValidadorCadastrarNovoCartao> validadores;
	
	@Autowired
	private CartaoMapper cartaoMapper;
	
	@Transactional
	public void deletarCartaoPorIdToken(Long id) {
		Cliente cliente = usuarioAutenticadoService.getCliente();
		
		Iterator<Cartao> iteratorCartao = cliente.getCartoes().iterator();
		while (iteratorCartao.hasNext()) {
			Cartao cartao = iteratorCartao.next();
			if (cartao.getId() == id) {
				iteratorCartao.remove();
				cartaoRepository.delete(cartao);
				return;
			}
		}
		throw new ValidacaoException("Não existe um cartão de id número " + id + " para esse cliente");
	}
	
	public List<MostrarCartaoDTO> pegarCartoesClientePorIdToken() {
		Cliente cliente = usuarioAutenticadoService.getCliente();
		
		List<Cartao> cartoes = cartaoRepository.findAllByCliente(cliente);
		
		if (cartoes.size() == 0) {
			throw new ValidacaoException("Cliente não possui nenhum cartão cadastrado");
		}

		return cartaoMapper.listaCartaoParaListaMostrarCartaoDto(cartoes);
	}

	@Transactional
	public MostrarCartaoDTO cadastrarNovoCartaoPorIdToken(CadastrarCartaoDTO cartaoDTO) {
		Cliente cliente = usuarioAutenticadoService.getCliente();

		validadores.forEach(v -> v.validar(cartaoDTO, cliente));
		
		Cartao novoCartao = cartaoMapper.cadastrarCartaoDtoParaCartao(cartaoDTO, cliente);
		
		List<Cartao> cartoes = cliente.getCartoes();
		if (cartaoDTO.cartaoPadrao() == true && !cartoes.isEmpty()) {
			cartoes.forEach(cartao -> {
				if (cartao.getCartaoPadrao() == true) {
					cartao.setCartaoPadrao(false);
					cartaoRepository.save(cartao);
				}
			});
		}
		
		if (cartaoDTO.cartaoPadrao() == false && cartoes.isEmpty()) {
			novoCartao.setCartaoPadrao(true);
		}
			
		novoCartao.setNomeBanco("Falta implementar API banco");
		cartaoRepository.save(novoCartao);
		
		return cartaoMapper.cartaoParaMostrarCartaoDto(novoCartao);
	}
	
	@Transactional
	public void alterarCartaoPadraoPorIdToken(Long id) {
		Cliente cliente = usuarioAutenticadoService.getCliente();
		List<Cartao> cartoes = cliente.getCartoes();
		
		AtomicBoolean comoCartaoEstaDepoisDeSerAlterado = new AtomicBoolean();
		AtomicBoolean foiAlterado = new AtomicBoolean(false);
		cartoes.forEach(cartao -> {
			if (cartao.getId() == id) {
				if (cartao.getCartaoPadrao() == false) {
					cartao.setCartaoPadrao(true);
					comoCartaoEstaDepoisDeSerAlterado.set(true);
					foiAlterado.set(true);
				} else if (cartao.getCartaoPadrao() == true) {
					cartao.setCartaoPadrao(false);
					foiAlterado.set(true);
				}
				cartaoRepository.save(cartao);
			}
		});
		
		if (comoCartaoEstaDepoisDeSerAlterado.get() != false) {
			cartoes.forEach(cartao -> {
				if (cartao.getId() != id && cartao.getCartaoPadrao() == true) {
					cartao.setCartaoPadrao(false);
					cartaoRepository.save(cartao);
				}
			});
		}
		
		if (foiAlterado.get() == false) {
			throw new ValidacaoException("ID do cartão de número: " + id + " não existe para esse cliente");
		}
	}
	
	
//	------------------------------
//	------------------------------
//	METODOS UTILITARIOS
//	------------------------------
//	------------------------------

	public Cartao verificarSeCartaoExistePorIdCartaoECliente(Long idCartao, Cliente cliente) {
		return cartaoRepository.findByIdAndCliente(idCartao, cliente).orElseThrow(() -> new ValidacaoException("Não existe o cartão de ID número " + idCartao + " para o cliente de ID número " + cliente.getId()));
	}
	
}
