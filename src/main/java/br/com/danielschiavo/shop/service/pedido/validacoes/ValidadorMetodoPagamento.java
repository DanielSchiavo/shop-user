package br.com.danielschiavo.shop.service.pedido.validacoes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.danielschiavo.shop.model.ValidacaoException;
import br.com.danielschiavo.shop.model.cliente.Cliente;
import br.com.danielschiavo.shop.model.cliente.cartao.Cartao;
import br.com.danielschiavo.shop.model.pedido.dto.CriarPedidoDTO;
import br.com.danielschiavo.shop.model.pedido.pagamento.MetodoPagamento;
import br.com.danielschiavo.shop.service.cliente.CartaoUserService;

@Service
public class ValidadorMetodoPagamento implements ValidadorCriarNovoPedido {
	
	@Autowired
	private CartaoUserService cartaoService;
	
	@Override
	public void validar(CriarPedidoDTO pedidoDTO, Cliente cliente) {
		MetodoPagamento metodoPagamentoDTO = pedidoDTO.pagamento().metodoPagamento();
		Long idCartao = pedidoDTO.pagamento().idCartao();
		String numeroParcelas = pedidoDTO.pagamento().numeroParcelas();
		
		if ((metodoPagamentoDTO.precisaDeCartao() == true && idCartao == null) || (metodoPagamentoDTO.podeParcelar() == true && numeroParcelas == null)) {
			throw new ValidacaoException("O método de pagamento escolhido foi " + metodoPagamentoDTO + ", portanto, é necessário enviar o idCartao do cliente e o numeroParcelas juntamente.");
		}
		if ((metodoPagamentoDTO.precisaDeCartao() == false && idCartao != null) || (metodoPagamentoDTO.podeParcelar() == false && numeroParcelas != null)) {
			throw new ValidacaoException("O método de pagamento escolhido foi " + metodoPagamentoDTO + ", portanto, você não deve enviar um idCartao nem o numeroParcelas junto.");
		}
		
		if (idCartao != null) {
			Cartao cartao = cartaoService.pegarCartaoPorIdECliente(idCartao, cliente);
			if (!metodoPagamentoDTO.toString().endsWith(cartao.getTipoCartao().toString())) {
				throw new ValidacaoException("O cartão cadastrado de id número " + cartao.getId() + ", foi cadastrado como um cartão de " + cartao.getTipoCartao().toString() + ", não condiz com o método de pagamento fornecido, que é: " + metodoPagamentoDTO.toString());
			}
		}
	}
}
