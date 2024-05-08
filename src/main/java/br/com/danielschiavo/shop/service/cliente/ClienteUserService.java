package br.com.danielschiavo.shop.service.cliente;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.danielschiavo.infra.security.UsuarioAutenticadoService;
import br.com.danielschiavo.repository.cliente.ClienteRepository;
import br.com.danielschiavo.shop.model.MensagemErroDTO;
import br.com.danielschiavo.shop.model.ValidacaoException;
import br.com.danielschiavo.shop.model.cliente.Cliente;
import br.com.danielschiavo.shop.model.cliente.dto.AlterarClienteDTO;
import br.com.danielschiavo.shop.model.cliente.dto.AlterarFotoPerfilDTO;
import br.com.danielschiavo.shop.model.cliente.dto.CadastrarClienteDTO;
import br.com.danielschiavo.shop.model.cliente.dto.MostrarClienteDTO;
import br.com.danielschiavo.shop.model.filestorage.ArquivoInfoDTO;
import br.com.danielschiavo.shop.service.cliente.mapper.ClienteMapper;
import lombok.Setter;

@Service
@Setter
public class ClienteUserService {
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private ClienteMapper clienteMapper;
	
	@Autowired
	private UsuarioAutenticadoService usuarioAutenticadoService;
	
	@Autowired
	private FileStorageServiceClient fileStorageServiceClient;

	@Transactional
	public void deletarFotoPerfilPorIdToken() throws IOException {
		Cliente cliente = usuarioAutenticadoService.getCliente();
		MensagemErroDTO mensagemErroDTO = fileStorageServiceClient.deletarFotoPerfil(cliente.getFotoPerfil());
		if (mensagemErroDTO.erro() != null) {
			throw new ValidacaoException(mensagemErroDTO.mensagem());
		}
		cliente.setFotoPerfil("Padrao.jpeg");
		clienteRepository.save(cliente);
	}
	
	public MostrarClientePaginaInicialDTO detalharClientePorIdTokenPaginaInicial() {
		Cliente cliente = usuarioAutenticadoService.getCliente();
		ArquivoInfoDTO arquivoInfoDTO = fileStorageServiceClient.getFotoPerfil(cliente.getFotoPerfil());
		return clienteMapper.clienteParaMostrarClientePaginaInicialDTO(cliente, arquivoInfoDTO);
	}
	
	public MostrarClienteDTO detalharClientePorIdToken() {
		Cliente cliente = usuarioAutenticadoService.getCliente();
		ArquivoInfoDTO arquivoInfoDTO = fileStorageServiceClient.getFotoPerfil(cliente.getFotoPerfil());
		return clienteMapper.clienteParaMostrarClienteDTO(cliente, arquivoInfoDTO);
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
	public String alterarFotoPerfilPorIdToken(AlterarFotoPerfilDTO alterarFotoPerfilDTO) {
		Cliente cliente = usuarioAutenticadoService.getCliente();
		String nomeNovaFotoPerfil = alterarFotoPerfilDTO.nomeNovaFotoPerfil();
		ArquivoInfoDTO arquivoInfoDTO2 = fileStorageServiceClient.getFotoPerfil(nomeNovaFotoPerfil);
		if (arquivoInfoDTO2.erro() != null) {
			throw new ValidacaoException("A foto de perfil " + nomeNovaFotoPerfil + " não foi cadastrada.");
		} else {
			cliente.setFotoPerfil(nomeNovaFotoPerfil);
			clienteRepository.save(cliente);
			return "Foto de perfil alterada com sucesso!";
		}
	}
	
	
//	------------------------------
//	------------------------------
//	METODOS UTILITARIOS
//	------------------------------
//	------------------------------

}
