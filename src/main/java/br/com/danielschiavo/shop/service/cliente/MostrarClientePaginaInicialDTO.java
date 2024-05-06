package br.com.danielschiavo.shop.service.cliente;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import br.com.danielschiavo.shop.model.filestorage.ArquivoInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class MostrarClientePaginaInicialDTO {
	
	private String nome;
	private ArquivoInfoDTO fotoPerfil;
}
