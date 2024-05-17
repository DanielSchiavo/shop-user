package br.com.danielschiavo.shop.repository.cliente;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.danielschiavo.shop.model.cliente.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long>{
	
	Page<Cliente> findAll(Pageable pageable);
	
	@Query("SELECT c FROM Cliente c WHERE c.id = :id")
	UserDetails buscarPorId(Long id);

	@Query("SELECT c FROM Cliente c WHERE c.email = :login OR c.celular = :login OR c.cpf = :login")
	Optional<UserDetails> findByEmailOrCelularOrCpf(String login);

	Optional<Cliente> findByCpf(String cpf);

}
