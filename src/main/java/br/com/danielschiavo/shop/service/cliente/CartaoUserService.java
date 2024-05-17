package br.com.danielschiavo.shop.service.cliente;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.danielschiavo.infra.security.UsuarioAutenticadoService;
import br.com.danielschiavo.shop.model.ValidacaoException;
import br.com.danielschiavo.shop.model.cliente.Cliente;
import br.com.danielschiavo.shop.model.cliente.cartao.CadastrarCartaoDTO;
import br.com.danielschiavo.shop.model.cliente.cartao.Cartao;
import br.com.danielschiavo.shop.model.cliente.cartao.MostrarCartaoDTO;
import br.com.danielschiavo.shop.repository.cliente.CartaoRepository;
import br.com.danielschiavo.shop.service.cliente.mapper.CartaoMapper;
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
	public String deletarCartaoPorId(Long id) {
		Cliente cliente = usuarioAutenticadoService.getCliente();
		
		Integer registrosExcluidos = cartaoRepository.deleteByIdAndCliente(id, cliente);
		
		if (registrosExcluidos > 0) {
			return "Cartão excluido com sucesso!";
		} else {
			throw new ValidacaoException("Cartão id número " + id + " não encontrado");
		}
	}
	
	public List<MostrarCartaoDTO> pegarCartoesClientePorIdToken() {
		Cliente cliente = usuarioAutenticadoService.getCliente();
		
		List<Cartao> cartoes = cartaoRepository.findAllByCliente(cliente);
		
		if (cartoes.size() == 0) {
			throw new ValidacaoException("Cliente não possui nenhum cartão cadastrado");
		}

		return cartaoMapper.listaCartaoParaListaMostrarCartaoDto(cartoes);
	}
	
	public MostrarCartaoDTO pegarCartao(Long cartaoId) {
		Cliente cliente = usuarioAutenticadoService.getCliente();
		
		Cartao cartao = cartaoRepository.findByIdAndCliente(cartaoId, cliente).orElseThrow(() -> {throw new ValidacaoException("Cliente não tem cartão com o ID " + cartaoId);});
		
		return cartaoMapper.cartaoParaMostrarCartaoDto(cartao);
	}

	@Transactional
	public String cadastrarNovoCartaoPorIdToken(CadastrarCartaoDTO cartaoDTO) {
		Cliente cliente = usuarioAutenticadoService.getCliente();

		validadores.forEach(v -> v.validar(cartaoDTO, cliente));
		
		Cartao novoCartao = cartaoMapper.cadastrarCartaoDtoParaCartao(cartaoDTO, cliente);
		
		List<Cartao> cartoes = cartaoRepository.findAllByCliente(cliente);
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
		
		return "Cartão cadastrado com sucesso!";
	}
	
	@Transactional
	public String alterarCartaoPadraoPorIdToken(Long id) {
		Cliente cliente = usuarioAutenticadoService.getCliente();
		List<Cartao> cartoes = cartaoRepository.findAllByCliente(cliente);
		
		AtomicBoolean comoCartaoEstaDepoisDeSerAlterado = new AtomicBoolean();
		AtomicBoolean foiAlterado = new AtomicBoolean(false);
		//Primeiro itera na lista e pega o cartão com o id recebido no parametro desse metodo
		//Se o cartão encontrado for cartaoPadrao = false, transforme em cartaoPadrao = true, faça o mesmo se for o contrário também
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
			}
		});
		
		//Se o cliente alterou o cartão recebido no parametro desse metodo para cartaoPadrao = true, então defina todos os outros cartões como cartaoPadrao = false
		if (comoCartaoEstaDepoisDeSerAlterado.get() == true) {
			cartoes.forEach(cartao -> {
				if (cartao.getId() != id && cartao.getCartaoPadrao() == true) {
					cartao.setCartaoPadrao(false);
				}
			});
		}
		
		//Se nenhum cartão foi alterado é porque o cliente não possui um cartão com esse ID
		if (foiAlterado.get() == false) {
			throw new ValidacaoException("ID do cartão de número: " + id + " não existe para esse cliente");
		}
		
		cartaoRepository.saveAll(cartoes);
		return "Cartão ID " + id + " alterado para cartão padrão " + comoCartaoEstaDepoisDeSerAlterado + " com sucesso";
	}
	
	
//	------------------------------
//	------------------------------
//	METODOS UTILITARIOS
//	------------------------------
//	------------------------------

	public Cartao pegarCartaoPorIdECliente(Long idCartao, Cliente cliente) {
		return cartaoRepository.findByIdAndCliente(idCartao, cliente).orElseThrow(() -> new ValidacaoException("Não existe o cartão de ID número " + idCartao + " para o cliente de ID número " + cliente.getId()));
	}


	
}
