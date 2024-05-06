package br.com.danielschiavo.shop.service.cliente;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.danielschiavo.infra.security.UsuarioAutenticadoService;
import br.com.danielschiavo.repository.cliente.ClienteRepository;
import br.com.danielschiavo.shop.model.ValidacaoException;
import br.com.danielschiavo.shop.model.cliente.Cliente;
import br.com.danielschiavo.shop.model.cliente.dto.AlterarClienteDTO;
import br.com.danielschiavo.shop.model.cliente.dto.AlterarFotoPerfilDTO;
import br.com.danielschiavo.shop.model.cliente.dto.CadastrarClienteDTO;
import br.com.danielschiavo.shop.model.cliente.dto.MostrarClienteDTO;
import br.com.danielschiavo.shop.model.filestorage.ArquivoInfoDTO;
import br.com.danielschiavo.shop.service.cliente.mapper.ClienteMapper;
import br.com.danielschiavo.shop.service.filestorage.FileStoragePerfilService;
import lombok.Setter;

@Service
@Setter
public class ClienteUserService {
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private FileStoragePerfilService fileService;
	
	@Autowired
	private ClienteMapper clienteMapper;
	
	@Autowired
	private UsuarioAutenticadoService usuarioAutenticadoService;

	@Transactional
	public void deletarFotoPerfilPorIdToken() throws IOException {
		Cliente cliente = usuarioAutenticadoService.getCliente();
		if (cliente.getFotoPerfil().equals("Padrao.jpeg")) {
			throw new ValidacaoException("O cliente não tem foto de perfil, ele já está com a foto padrão, portanto, não é possível deletar");
		}
		fileService.deletarFotoPerfilNoDisco(cliente.getFotoPerfil());
		cliente.setFotoPerfil("Padrao.jpeg");
		clienteRepository.save(cliente);
	}
	
	public MostrarClientePaginaInicialDTO detalharClientePorIdTokenPaginaInicial() {
		Cliente cliente = usuarioAutenticadoService.getCliente();
		return clienteMapper.clienteParaMostrarClientePaginaInicialDTO(cliente, fileService);
	}
	
	public MostrarClienteDTO detalharClientePorIdToken() {
		Cliente cliente = usuarioAutenticadoService.getCliente();
		return clienteMapper.clienteParaMostrarClienteDTO(cliente, fileService);
	}
	
	@Transactional
	public Map<String, Object> cadastrarCliente(CadastrarClienteDTO clienteDTO) {
		Cliente cliente = clienteMapper.cadastrarClienteDtoParaCliente(clienteDTO);
		clienteRepository.save(cliente);
		
		Map<String, Object> resposta = new HashMap<>();
		resposta.put("mensagem", "Cadastrado com sucesso!");
		resposta.put("clienteId", cliente.getId());
		return resposta;
	}
	
	@Transactional
	public String alterarClientePorIdToken(AlterarClienteDTO alterarClienteDTO) {
		Cliente cliente = usuarioAutenticadoService.getCliente();
		clienteMapper.alterarClienteDtoSetarAtributosEmCliente(alterarClienteDTO, cliente);
		return "Alterado com sucesso!";
	}
	
	@Transactional
	public ArquivoInfoDTO alterarFotoPerfilPorIdToken(AlterarFotoPerfilDTO alterarFotoPerfilDTO) {
		Cliente cliente = usuarioAutenticadoService.getCliente();
		String nomeNovaFotoPerfil = alterarFotoPerfilDTO.nomeNovaFotoPerfil();
		fileService.verificarSeExisteFotoPerfilPorNome(nomeNovaFotoPerfil);
		ArquivoInfoDTO arquivoInfoDTO = fileService.pegarFotoPerfilPorNome(nomeNovaFotoPerfil);
		
		cliente.setFotoPerfil(nomeNovaFotoPerfil);
		clienteRepository.save(cliente);
		return arquivoInfoDTO;
	}
	
	
//	------------------------------
//	------------------------------
//	METODOS UTILITARIOS
//	------------------------------
//	------------------------------

}
