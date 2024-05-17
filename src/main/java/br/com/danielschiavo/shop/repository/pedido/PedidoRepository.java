package br.com.danielschiavo.shop.repository.pedido;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.danielschiavo.shop.model.cliente.Cliente;
import br.com.danielschiavo.shop.model.pedido.Pedido;
import br.com.danielschiavo.shop.model.pedido.StatusPedido;

public interface PedidoRepository extends JpaRepository<Pedido, UUID>{

	Page<Pedido> findAllByStatusPedido(StatusPedido confirmando, Pageable pageable);

	List<Pedido> findAllByClienteIdOrderByDataPedidoAsc(Long id);

	Page<Pedido> findAllByCliente(Cliente cliente, Pageable pageable);

}
