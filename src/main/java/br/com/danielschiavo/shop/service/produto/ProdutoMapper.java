package br.com.danielschiavo.shop.service.produto;

import java.util.List;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import br.com.danielschiavo.service.produto.ProdutoUtilidadeService;
import br.com.danielschiavo.shop.model.filestorage.ArquivoInfoDTO;
import br.com.danielschiavo.shop.model.produto.Produto;
import br.com.danielschiavo.shop.model.produto.dto.DetalharProdutoDTO;
import br.com.danielschiavo.shop.model.produto.dto.MostrarProdutosDTO;

@Mapper(componentModel = "spring")
public abstract class ProdutoMapper {
	
	@Mapping(target = "primeiraImagem", ignore = true)
	public abstract MostrarProdutosDTO produtoParaMostrarProdutosDTO(Produto produto, @Context FileStorageProdutoService fileStorageProdutoService, @Context ProdutoUtilidadeService produtoUtilidadeService);
	
    public void primeiraImagem(@MappingTarget MostrarProdutosDTO.MostrarProdutosDTOBuilder dto, Produto produto, @Context FileStorageProdutoService fileStorageProdutoService, @Context ProdutoUtilidadeService produtoUtilidadeService) {
        String primeiraImagem = produtoUtilidadeService.pegarNomePrimeiraImagem(produto);
		byte[] bytesArquivo = fileStorageProdutoService.pegarArquivoProduto(primeiraImagem).bytesArquivo();
		dto.primeiraImagem(bytesArquivo);
    }
    
    @Mapping(target = "subCategoria", source = "produto.subCategoria.id")
    @Mapping(target = "arquivos", ignore = true)
    public abstract DetalharProdutoDTO produtoParaDetalharProdutoDTO(Produto produto, @Context FileStorageProdutoService fileStorageProdutoService, @Context ProdutoUtilidadeService produtoUtilidadeService);
    
    public void arquivosProduto(@MappingTarget DetalharProdutoDTO.DetalharProdutoDTOBuilder dto, Produto produto, @Context FileStorageProdutoService fileStorageProdutoService, @Context ProdutoUtilidadeService produtoUtilidadeService) {
        List<String> nomeTodosArquivos = produtoUtilidadeService.pegarNomeTodosArquivos(produto);
    	List<ArquivoInfoDTO> todosArquivos = fileStorageProdutoService.pegarArquivosProduto(nomeTodosArquivos);
    	dto.arquivos(todosArquivos);
    }
}
