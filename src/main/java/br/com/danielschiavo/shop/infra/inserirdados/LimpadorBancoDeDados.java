package br.com.danielschiavo.shop.infra.inserirdados;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class LimpadorBancoDeDados {

	@Autowired
    private JdbcTemplate jdbcTemplate;

    public void limpar() {
        String[] tables = new String[]{"pedidos_items", 
                                       "pedidos", 
                                       "pedidos_entrega", 
                                       "pedidos_pagamento", 
                                       "clientes_carrinhos_items", 
                                       "clientes_carrinhos", 
                                       "produtos_tipo_entrega", 
                                       "produtos_arquivos", 
                                       "produtos", 
                                       "sub_categorias", 
                                       "categorias", 
                                       "clientes_enderecos", 
                                       "clientes_cartoes", 
                                       "clientes"};
        
        for (String table : tables) {
            if (tabelaExiste(table)) {
                jdbcTemplate.execute("DELETE FROM " + table + ";");

                String sequenceName = null;
                if (table.contentEquals("clientes_carrinhos")) {
                	sequenceName = jdbcTemplate.queryForObject(
                			"SELECT pg_get_serial_sequence('" + table + "', 'cliente_id')", String.class);
                } else {
                	sequenceName = jdbcTemplate.queryForObject(
                			"SELECT pg_get_serial_sequence('" + table + "', 'id')", String.class);
                }

                if(sequenceName != null) {
                    jdbcTemplate.execute("ALTER SEQUENCE " + sequenceName + " RESTART WITH 1;");
                }
            }
        }
    }

    private boolean tabelaExiste(String tabela) {
        String sql = "SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, tabela);
    }

}