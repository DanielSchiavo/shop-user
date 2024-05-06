package br.com.danielschiavo.shop.service.produto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.danielschiavo.repository.produto.CategoriaRepository;
import br.com.danielschiavo.shop.model.produto.categoria.Categoria;
import br.com.danielschiavo.shop.model.produto.categoria.MostrarCategoriaComTodasSubCategoriaDTO;
import br.com.danielschiavo.shop.model.produto.subcategoria.MostrarSubCategoriaDTO;

@Service
public class CategoriaUserService {

	@Autowired
	private CategoriaRepository categoriaRepository;
	
	public Page<MostrarCategoriaComTodasSubCategoriaDTO> listarCategorias(Pageable pageable) {
		Page<Categoria> pageCategoria = categoriaRepository.findAll(pageable);
		
		return pageCategoria.map(categoria -> {
			List<MostrarSubCategoriaDTO> listaMostrarSubCategoria = new ArrayList<>();
			categoria.getSubCategorias().forEach(sc -> {
				MostrarSubCategoriaDTO mostrarSubCategoriaDTO = new MostrarSubCategoriaDTO(sc.getId(), sc.getNome());
				listaMostrarSubCategoria.add(mostrarSubCategoriaDTO);
			});
			return new MostrarCategoriaComTodasSubCategoriaDTO(categoria.getId(), categoria.getNome(), listaMostrarSubCategoria);
		});
	}

	
//	------------------------------
//	------------------------------
//	METODOS UTILITARIOS
//	------------------------------
//	------------------------------

	
}
