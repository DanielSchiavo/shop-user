package br.com.danielschiavo.shop.service.pedido.validacoes;

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
import br.com.danielschiavo.shop.model.pedido.TipoEntrega;
import br.com.danielschiavo.shop.model.pedido.dto.CriarPedidoDTO;
import br.com.danielschiavo.shop.model.pedido.entrega.CriarEntregaDTO;

@ExtendWith(MockitoExtension.class)
class ValidadorTipoEntregaTest {

	@InjectMocks
	private ValidadorTipoEntrega validador;
	
	@Mock
	private CriarPedidoDTO criarPedidoDTO;
	
	@Mock
	private CriarEntregaDTO criarEntregaDTO;
	
	@Mock
	private Cliente cliente;
	
	@Test
	@DisplayName("Validador tipo entrega não deve lançar exceção quando tipo de entrega correios for enviado com idEndereco")
	void ValidadorTipoEntrega_CorreiosComIdEndereco_NaoDeveLancarExcecao() {
		BDDMockito.given(criarEntregaDTO.tipoEntrega()).willReturn(TipoEntrega.CORREIOS);
		BDDMockito.given(criarEntregaDTO.idEndereco()).willReturn(1L);
		BDDMockito.given(criarPedidoDTO.entrega()).willReturn(criarEntregaDTO);
		
		Assertions.assertDoesNotThrow(() -> validador.validar(criarPedidoDTO, cliente));
	}
	
	@Test
	@DisplayName("Validador tipo entrega deve lançar exceção quando tipo de entrega for correios e idEndereco não for enviado")
	void ValidadorTipoEntrega_CorreiosSemIdEndereco_DeveLancarExcecao() {
		BDDMockito.given(criarEntregaDTO.tipoEntrega()).willReturn(TipoEntrega.CORREIOS);
		BDDMockito.given(criarEntregaDTO.idEndereco()).willReturn(null);
		BDDMockito.given(criarPedidoDTO.entrega()).willReturn(criarEntregaDTO);
		
		Assertions.assertThrows(ValidacaoException.class, () -> validador.validar(criarPedidoDTO, cliente));
	}
	
	@Test
	@DisplayName("Validador tipo entrega não deve lançar exceção quando tipo de entrega expressa for enviado com idEndereco")
	void ValidadorTipoEntrega_EntregaExpressaComIdEndereco_NaoDeveLancarExcecao() {
		BDDMockito.given(criarEntregaDTO.tipoEntrega()).willReturn(TipoEntrega.ENTREGA_EXPRESSA);
		BDDMockito.given(criarEntregaDTO.idEndereco()).willReturn(1L);
		BDDMockito.given(criarPedidoDTO.entrega()).willReturn(criarEntregaDTO);
		
		Assertions.assertDoesNotThrow(() -> validador.validar(criarPedidoDTO, cliente));
	}
	
	@Test
	@DisplayName("Validador tipo entrega deve lançar exceção quando tipo de entrega for expressa e idEndereco não for enviado")
	void ValidadorTipoEntrega_EntregaExpressaSemIdEndereco_DeveLancarExcecao() {
		BDDMockito.given(criarEntregaDTO.tipoEntrega()).willReturn(TipoEntrega.ENTREGA_EXPRESSA);
		BDDMockito.given(criarEntregaDTO.idEndereco()).willReturn(null);
		BDDMockito.given(criarPedidoDTO.entrega()).willReturn(criarEntregaDTO);
		
		Assertions.assertThrows(ValidacaoException.class, () -> validador.validar(criarPedidoDTO, cliente));
	}
	
	@Test
	@DisplayName("Validador tipo entrega deve lançar exceção quando tipo de entrega digital for enviado com idEndereco")
	void ValidadorTipoEntrega_EntregaDigitalComIdEndereco_DeveLancarExcecao() {
		BDDMockito.given(criarEntregaDTO.tipoEntrega()).willReturn(TipoEntrega.ENTREGA_DIGITAL);
		BDDMockito.given(criarEntregaDTO.idEndereco()).willReturn(1L);
		BDDMockito.given(criarPedidoDTO.entrega()).willReturn(criarEntregaDTO);
		
		Assertions.assertThrows(ValidacaoException.class, () -> validador.validar(criarPedidoDTO, cliente));
	}
	
	@Test
	@DisplayName("Validador tipo entrega deve lançar exceção quando tipo de entrega retirada na loja for enviado com idEndereco")
	void ValidadorTipoEntrega_EntregaRetiradaNaLojaComIdEndereco_DeveLancarExcecao() {
		BDDMockito.given(criarEntregaDTO.tipoEntrega()).willReturn(TipoEntrega.RETIRADA_NA_LOJA);
		BDDMockito.given(criarEntregaDTO.idEndereco()).willReturn(1L);
		BDDMockito.given(criarPedidoDTO.entrega()).willReturn(criarEntregaDTO);
		
		Assertions.assertThrows(ValidacaoException.class, () -> validador.validar(criarPedidoDTO, cliente));
	}

}
