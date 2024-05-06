package br.com.danielschiavo.shop.service.cliente.mapper;

import java.time.LocalDateTime;

import org.mapstruct.Mapper;

import br.com.danielschiavo.shop.model.cliente.Cliente;
import br.com.danielschiavo.shop.model.cliente.role.NomeRole;
import br.com.danielschiavo.shop.model.cliente.role.Role;

@Mapper(componentModel = "spring")
public abstract class RoleMapper {

	public Role stringRoleParaRoleEntity(String nomeRole, Cliente cliente) {
		Role role = new Role();
		role.setDataAtribuicao(LocalDateTime.now());
		if (nomeRole.equals("ADMIN")) {
			role.setRole(NomeRole.ADMIN);
		}
		role.setCliente(cliente);
		return role;
	}
}
