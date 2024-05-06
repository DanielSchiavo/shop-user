package br.com.danielschiavo.shop.service.cliente;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.danielschiavo.infra.security.UsuarioAutenticadoService;
import br.com.danielschiavo.mapper.cliente.EnderecoMapper;
import br.com.danielschiavo.repository.cliente.EnderecoRepository;
import br.com.danielschiavo.shop.model.ValidacaoException;
import br.com.danielschiavo.shop.model.cliente.Cliente;
import br.com.danielschiavo.shop.model.cliente.endereco.AlterarEnderecoDTO;
import br.com.danielschiavo.shop.model.cliente.endereco.CadastrarEnderecoDTO;
import br.com.danielschiavo.shop.model.cliente.endereco.Endereco;
import br.com.danielschiavo.shop.model.cliente.endereco.MostrarEnderecoDTO;
import lombok.Setter;

@Service
@Setter
public class EnderecoUserService {
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Autowired
	private EnderecoMapper enderecoMapper;
	
	@Autowired
	private UsuarioAutenticadoService usuarioAutenticadoService;
	
	@Transactional
	public void deletarEnderecoPorIdToken(Long idEndereco) {
		Cliente cliente = usuarioAutenticadoService.getCliente();
		Endereco endereco = verificarSeEnderecoExistePorIdEnderecoECliente(idEndereco, cliente);
		
		enderecoRepository.delete(endereco);
	}
	
	public List<MostrarEnderecoDTO> pegarEnderecosClientePorIdToken() {
		Cliente cliente = usuarioAutenticadoService.getCliente();
		List<Endereco> enderecos = enderecoRepository.findAllByCliente(cliente);
		
		if (enderecos.isEmpty()) {
			throw new ValidacaoException("Cliente não possui nenhum endereço cadastrado");
		}
		
		return enderecoMapper.listaEnderecoParaMostrarEnderecoDTO(enderecos);
	}
	
	@Transactional
	public MostrarEnderecoDTO cadastrarNovoEnderecoPorIdToken(CadastrarEnderecoDTO novoEnderecoDTO) {
		Cliente cliente = usuarioAutenticadoService.getCliente();
		
		Endereco novoEndereco = enderecoMapper.cadastrarEnderecoDtoParaEndereco(novoEnderecoDTO);
		novoEndereco.setCliente(cliente);
		
		List<Endereco> todosEnderecosCliente = enderecoRepository.findAllByCliente(cliente);
		
		if (novoEnderecoDTO.enderecoPadrao() == true && !todosEnderecosCliente.isEmpty()) {
			todosEnderecosCliente.forEach(endereco -> {
				if (endereco.getEnderecoPadrao() == true) {
					endereco.setEnderecoPadrao(false);
					enderecoRepository.save(endereco);
				}
			});
		}
		
		if (novoEnderecoDTO.enderecoPadrao() == false && todosEnderecosCliente.isEmpty()) {
			novoEndereco.setEnderecoPadrao(true);
		}
		
		enderecoRepository.save(novoEndereco);
		return enderecoMapper.enderecoParaMostrarEnderecoDTO(novoEndereco);
	}
	
	@Transactional
	public MostrarEnderecoDTO alterarEnderecoPorIdToken(AlterarEnderecoDTO enderecoDTO, Long idEnderecoASerAlterado) {
		Cliente cliente = usuarioAutenticadoService.getCliente();
		List<Endereco> todosEnderecos = enderecoRepository.findAllByCliente(cliente);
		
		Endereco enderecoExiste = todosEnderecos.stream().filter(endereco -> endereco.getId() == idEnderecoASerAlterado).findFirst().orElseThrow(() -> new ValidacaoException("Não existe endereço de id número " + idEnderecoASerAlterado + " para o cliente de id número " + cliente.getId()));
		
		if (todosEnderecos.size() > 1 && enderecoDTO.enderecoPadrao() == true) {
			todosEnderecos.forEach(e -> {
				if (e.getEnderecoPadrao() == true) {
					e.setEnderecoPadrao(false);
					enderecoRepository.save(e);
				}
			});
		}
		
		enderecoMapper.alterarEnderecoDtoParaEndereco(enderecoDTO, enderecoExiste);
		
		if (enderecoDTO.enderecoPadrao() == false && todosEnderecos.size() == 1) {
			enderecoExiste.setEnderecoPadrao(true);
		}
		
		enderecoRepository.save(enderecoExiste);
		return enderecoMapper.enderecoParaMostrarEnderecoDTO(enderecoExiste);
	}
	
	
//	------------------------------
//	------------------------------
//	METODOS UTILITARIOS
//	------------------------------
//	------------------------------
	
	public Endereco verificarSeEnderecoExistePorIdEnderecoECliente(Long idEndereco, Cliente cliente) {
		return enderecoRepository.findByIdAndCliente(idEndereco, cliente).orElseThrow(() -> new ValidacaoException("Não existe endereço de id número " + idEndereco + " para o cliente de id número " + cliente.getId()));
	}
	
	public Endereco verificarSeIdEnderecoExiste(Long clienteId, Long enderecoId) {
		return enderecoRepository.findByClienteIdAndEnderecoId(clienteId, enderecoId).orElseThrow(() -> new ValidacaoException("ID do Endereco ou Cliente inexistente! "));
	}

}
