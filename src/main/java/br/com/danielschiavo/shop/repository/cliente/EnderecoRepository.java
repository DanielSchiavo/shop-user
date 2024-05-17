package br.com.danielschiavo.shop.repository.cliente;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.danielschiavo.shop.model.cliente.Cliente;
import br.com.danielschiavo.shop.model.cliente.endereco.Endereco;

public interface EnderecoRepository extends JpaRepository<Endereco, Long>{
	
	@Query("SELECT e FROM Endereco e WHERE e.id = :enderecoId AND e.cliente = :clienteId")
	Optional<Endereco> findByClienteIdAndEnderecoId(Long clienteId, Long enderecoId);

	@Query("SELECT e FROM Endereco e WHERE e.enderecoPadrao = true AND e.cliente = :cliente")
	Optional<Endereco> findByClienteAndEnderecoPadraoTrue(Cliente cliente);

	List<Endereco> findAllByCliente(Cliente cliente);

	Optional<Endereco> findByIdAndCliente(Long idEndereco, Cliente cliente);

}
