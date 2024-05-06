package br.com.danielschiavo.shop.service.produto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import br.com.danielschiavo.infra.security.UsuarioAutenticadoService;
import br.com.danielschiavo.repository.produto.CategoriaRepository;
import br.com.danielschiavo.shop.model.cliente.Cliente;
import br.com.danielschiavo.shop.model.produto.categoria.Categoria;
import br.com.danielschiavo.shop.model.produto.categoria.Categoria.CategoriaBuilder;
import br.com.danielschiavo.shop.model.produto.categoria.MostrarCategoriaComTodasSubCategoriaDTO;

@ExtendWith(MockitoExtension.class)
class CategoriaUserServiceTest {
	
	@Mock
	private UsuarioAutenticadoService usuarioAutenticadoService;
	
	@InjectMocks
	private CategoriaUserService categoriaService;
	
	@Mock
	private Cliente cliente;
	
	@Mock
	private Categoria categoria;
	
	@Mock
	private CategoriaRepository categoriaRepository;
	
	@Captor
	private ArgumentCaptor<Categoria> categoriaCaptor;
	
	private CategoriaBuilder categoriaBuilder = Categoria.builder();
	
    @Test
    @DisplayName("Listar categorias deve retornar pagina de categorias normalmente")
    void listarCategorias_DeveRetornarPaginaDeCategorias() {
        // ARRANGE
		List<Categoria> categorias = 
				categoriaBuilder
				.categoria(1L, "Computadores")
							.comSubCategoria(1L, "Teclado")
							.comSubCategoria(2L, "Mouse")
							.comSubCategoria(3L, "SSD")
							.comSubCategoria(4L, "Placa de Video")
				.categoria(2L, "Softwares")
							.comSubCategoria(5L, "Sistema Administrativo")
							.comSubCategoria(6L, "Automacao")
				.getCategorias();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Categoria> pageCategoria = new PageImpl<>(categorias, pageable, categorias.size());
        BDDMockito.when(categoriaRepository.findAll(pageable)).thenReturn(pageCategoria);

        // ACT
        Page<MostrarCategoriaComTodasSubCategoriaDTO> retornoPageCategoriaListarCategorias = categoriaService.listarCategorias(pageable);

        // ASSERT
        assertNotNull(retornoPageCategoriaListarCategorias);
        assertEquals(pageCategoria.getTotalElements(), retornoPageCategoriaListarCategorias.getTotalElements());
        List<MostrarCategoriaComTodasSubCategoriaDTO> listaMostrarCategoriaComTodasSubCategorias = retornoPageCategoriaListarCategorias.getContent();
        for (int i=0; i< categorias.size(); i++) {
        	assertEquals(categorias.get(i).getId(), listaMostrarCategoriaComTodasSubCategorias.get(i).getId());
        	assertEquals(categorias.get(i).getNome(), listaMostrarCategoriaComTodasSubCategorias.get(i).getNome());
        	for (int j=0; j<categorias.get(i).getSubCategorias().size(); j++) {
        		assertEquals(categorias.get(i).getSubCategorias().get(j).getId(), listaMostrarCategoriaComTodasSubCategorias.get(i).getSubCategorias().get(j).id());
        		assertEquals(categorias.get(i).getSubCategorias().get(j).getNome(), listaMostrarCategoriaComTodasSubCategorias.get(i).getSubCategorias().get(j).nome());
        	}
        }
    }
}
