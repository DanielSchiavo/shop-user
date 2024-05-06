package br.com.danielschiavo.shop.service.pedido.validacoes;


import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.danielschiavo.shop.model.ValidacaoException;
import br.com.danielschiavo.shop.model.cliente.Cliente;
import br.com.danielschiavo.shop.model.cliente.cartao.Cartao;
import br.com.danielschiavo.shop.model.cliente.cartao.TipoCartao;
import br.com.danielschiavo.shop.model.pedido.dto.CriarPagamentoDTO;
import br.com.danielschiavo.shop.model.pedido.dto.CriarPedidoDTO;
import br.com.danielschiavo.shop.model.pedido.pagamento.MetodoPagamento;
import br.com.danielschiavo.shop.service.cliente.CartaoUserService;

@ExtendWith(MockitoExtension.class)
class ValidadorMetodoPagamentoTest {

	@InjectMocks
	private ValidadorMetodoPagamento validador;
	
	@Mock
	private CriarPedidoDTO criarPedidoDTO;
	
	@Mock
	private CriarPagamentoDTO criarPagamentoDTO;
	
	@Mock
	private Cartao cartao;
	
	@Mock
	private CartaoUserService cartaoService;
	
	@Mock
	private Cliente cliente;
	
	@Test
	@DisplayName("Validador metodo pagamento não deve lançar exceção quando informações corretas para pagamento no cartão de crédito são enviadas")
	void ValidadorMetodoPagamento_InformacoesCorretasParaPagamentoNoCartaoCredito_NaoDeveLancarExcecao() {
		BDDMockito.given(criarPedidoDTO.pagamento()).willReturn(criarPagamentoDTO);
		BDDMockito.given(criarPedidoDTO.pagamento().metodoPagamento()).willReturn(MetodoPagamento.CARTAO_CREDITO);
		BDDMockito.given(criarPedidoDTO.pagamento().idCartao()).willReturn(1L);
		BDDMockito.given(criarPedidoDTO.pagamento().numeroParcelas()).willReturn("6");
		BDDMockito.given(cartaoService.verificarSeCartaoExistePorIdCartaoECliente(any(), any())).willReturn(cartao);
		BDDMockito.given(cartao.getTipoCartao()).willReturn(TipoCartao.CREDITO);
		
		Assertions.assertDoesNotThrow(() -> validador.validar(criarPedidoDTO, cliente));
	}
	
	@Test
	@DisplayName("Validador metodo pagamento deve lançar exceção quando pagamento for no crédito e não for enviado idCartao")
	void ValidadorMetodoPagamento_PagamentoNoCreditoIdCartaoNaoEnviado_DeveLancarExcecao() {
		BDDMockito.given(criarPedidoDTO.pagamento()).willReturn(criarPagamentoDTO);
		BDDMockito.given(criarPedidoDTO.pagamento().metodoPagamento()).willReturn(MetodoPagamento.CARTAO_CREDITO);
		BDDMockito.given(criarPedidoDTO.pagamento().idCartao()).willReturn(null);
		
		Assertions.assertThrows(ValidacaoException.class, () -> validador.validar(criarPedidoDTO, cliente));
	}
	
	@Test
	@DisplayName("Validador metodo pagamento deve lançar exceção quando pagamento for no crédito e não for enviado numeroParcelas")
	void ValidadorMetodoPagamento_PagamentoNoCreditoNumeroParcelasNaoEnviado_DeveLancarExcecao() {
		BDDMockito.given(criarPedidoDTO.pagamento()).willReturn(criarPagamentoDTO);
		BDDMockito.given(criarPedidoDTO.pagamento().metodoPagamento()).willReturn(MetodoPagamento.CARTAO_CREDITO);
		BDDMockito.given(criarPedidoDTO.pagamento().numeroParcelas()).willReturn(null);
		
		Assertions.assertThrows(ValidacaoException.class, () -> validador.validar(criarPedidoDTO, cliente));
	}
	
	@Test
	@DisplayName("Validador metodo pagamento não deve lançar exceção quando informações corretas para pagamento no cartão debito são enviadas")
	void ValidadorMetodoPagamento_InformacoesCorretasParaPagamentoNoCartaoDebito_NaoDeveLancarExcecao() {
		BDDMockito.given(criarPedidoDTO.pagamento()).willReturn(criarPagamentoDTO);
		BDDMockito.given(criarPedidoDTO.pagamento().metodoPagamento()).willReturn(MetodoPagamento.CARTAO_DEBITO);
		BDDMockito.given(criarPedidoDTO.pagamento().idCartao()).willReturn(1L);
		BDDMockito.given(criarPedidoDTO.pagamento().numeroParcelas()).willReturn(null);
		BDDMockito.given(cartaoService.verificarSeCartaoExistePorIdCartaoECliente(any(), any())).willReturn(cartao);
		BDDMockito.given(cartao.getTipoCartao()).willReturn(TipoCartao.DEBITO);
		
		Assertions.assertDoesNotThrow(() -> validador.validar(criarPedidoDTO, cliente));
	}
	
	@Test
	@DisplayName("Validador metodo pagamento deve lançar exceção quando pagamento for no débito e não for enviado idCartao")
	void ValidadorMetodoPagamento_PagamentoNoDebitoIdCartaoNaoEnviado_DeveLancarExcecao() {
		BDDMockito.given(criarPedidoDTO.pagamento()).willReturn(criarPagamentoDTO);
		BDDMockito.given(criarPedidoDTO.pagamento().metodoPagamento()).willReturn(MetodoPagamento.CARTAO_DEBITO);
		BDDMockito.given(criarPedidoDTO.pagamento().idCartao()).willReturn(null);
		
		Assertions.assertThrows(ValidacaoException.class, () -> validador.validar(criarPedidoDTO, cliente));
	}
	
	@Test
	@DisplayName("Validador metodo pagamento deve lançar exceção quando pagamento for no débito e não for enviado numeroParcelas")
	void ValidadorMetodoPagamento_PagamentoNoDebitoNumeroParcelasEnviado_DeveLancarExcecao() {
		BDDMockito.given(criarPedidoDTO.pagamento()).willReturn(criarPagamentoDTO);
		BDDMockito.given(criarPedidoDTO.pagamento().metodoPagamento()).willReturn(MetodoPagamento.CARTAO_DEBITO);
		BDDMockito.given(criarPedidoDTO.pagamento().numeroParcelas()).willReturn("6");
		
		Assertions.assertThrows(ValidacaoException.class, () -> validador.validar(criarPedidoDTO, cliente));
	}
	
	@Test
	@DisplayName("Validador metodo pagamento não deve lançar exceção quando informações corretas para pagamento no boleto são enviadas")
	void ValidadorMetodoPagamento_InformacoesCorretasParaPagamentoNoBoleto_NaoDeveLancarExcecao() {
		BDDMockito.given(criarPedidoDTO.pagamento()).willReturn(criarPagamentoDTO);
		BDDMockito.given(criarPedidoDTO.pagamento().metodoPagamento()).willReturn(MetodoPagamento.BOLETO);
		BDDMockito.given(criarPedidoDTO.pagamento().idCartao()).willReturn(null);
		BDDMockito.given(criarPedidoDTO.pagamento().numeroParcelas()).willReturn(null);
		
		Assertions.assertDoesNotThrow(() -> validador.validar(criarPedidoDTO, cliente));
	}
	
	@Test
	@DisplayName("Validador metodo pagamento deve lançar exceção quando pagamento no boleto e idCartao enviado")
	void ValidadorMetodoPagamento_PagamentoNoBoletoIdCartaoEnviado_DeveLancarExcecao() {
		BDDMockito.given(criarPedidoDTO.pagamento()).willReturn(criarPagamentoDTO);
		BDDMockito.given(criarPedidoDTO.pagamento().metodoPagamento()).willReturn(MetodoPagamento.BOLETO);
		BDDMockito.given(criarPedidoDTO.pagamento().idCartao()).willReturn(1L);
		
		Assertions.assertThrows(ValidacaoException.class, () -> validador.validar(criarPedidoDTO, cliente));
	}
	
	@Test
	@DisplayName("Validador metodo pagamento deve lançar exceção quando pagamento no boleto e numeroParcelas enviado")
	void ValidadorMetodoPagamento_PagamentoNoBoletoNumeroParcelasEnviado_DeveLancarExcecao() {
		BDDMockito.given(criarPedidoDTO.pagamento()).willReturn(criarPagamentoDTO);
		BDDMockito.given(criarPedidoDTO.pagamento().metodoPagamento()).willReturn(MetodoPagamento.BOLETO);
		BDDMockito.given(criarPedidoDTO.pagamento().numeroParcelas()).willReturn("6");
		
		Assertions.assertThrows(ValidacaoException.class, () -> validador.validar(criarPedidoDTO, cliente));
	}

	@Test
	@DisplayName("Validador metodo pagamento não deve lançar exceção quando informações corretas para pagamento no pix são enviadas")
	void ValidadorMetodoPagamento_InformacoesCorretasParaPagamentoNoPix_NaoDeveLancarExcecao() {
		BDDMockito.given(criarPedidoDTO.pagamento()).willReturn(criarPagamentoDTO);
		BDDMockito.given(criarPedidoDTO.pagamento().metodoPagamento()).willReturn(MetodoPagamento.PIX);
		BDDMockito.given(criarPedidoDTO.pagamento().idCartao()).willReturn(null);
		BDDMockito.given(criarPedidoDTO.pagamento().numeroParcelas()).willReturn(null);
		
		Assertions.assertDoesNotThrow(() -> validador.validar(criarPedidoDTO, cliente));
	}
	
	@Test
	@DisplayName("Validador metodo pagamento deve lançar exceção quando pagamento no pix e idCartao enviado")
	void ValidadorMetodoPagamento_PagamentoNoPixIdCartaoEnviado_DeveLancarExcecao() {
		BDDMockito.given(criarPedidoDTO.pagamento()).willReturn(criarPagamentoDTO);
		BDDMockito.given(criarPedidoDTO.pagamento().metodoPagamento()).willReturn(MetodoPagamento.PIX);
		BDDMockito.given(criarPedidoDTO.pagamento().idCartao()).willReturn(1L);
		
		Assertions.assertThrows(ValidacaoException.class, () -> validador.validar(criarPedidoDTO, cliente));
	}
	
	@Test
	@DisplayName("Validador metodo pagamento deve lançar exceção quando pagamento no pix e numeroParcelas enviado")
	void ValidadorMetodoPagamento_PagamentoNoPixNumeroParcelasEnviado_DeveLancarExcecao() {
		BDDMockito.given(criarPedidoDTO.pagamento()).willReturn(criarPagamentoDTO);
		BDDMockito.given(criarPedidoDTO.pagamento().metodoPagamento()).willReturn(MetodoPagamento.PIX);
		BDDMockito.given(criarPedidoDTO.pagamento().numeroParcelas()).willReturn("6");
		
		Assertions.assertThrows(ValidacaoException.class, () -> validador.validar(criarPedidoDTO, cliente));
	}
	
}
