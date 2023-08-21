package br.com.evonetwork.buscarDadosReceitaSefaz.Dao;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.sql.ResultSet;

public class GenericoDao {

    public static int verificaQtdLinhas(String tabela, String where) {
        EntityFacade entityFacade = EntityFacadeFactory.getDWFFacade();
        JdbcWrapper jdbc = entityFacade.getJdbcWrapper();
        int resultSet = 0;
        try {
            jdbc.openSession();
            NativeSql nativeSql = new NativeSql(jdbc);
            StringBuffer sql = new StringBuffer("");
            sql.append("SELECT COUNT(*) QTD  ");
            sql.append("FROM ").append(tabela).append(" ");
            sql.append("WHERE  ").append(where);

            System.out.println("SQL: " + sql);

            ResultSet rs = nativeSql.executeQuery(sql);
            while (rs.next()) {
                resultSet = rs.getInt("QTD");
            }
        } catch (Exception e) {
            System.out.println("Erro na verificacao da Quantidade de Linhas " +  ExceptionUtils.getStackTrace(e));
        } finally {
            jdbc.closeSession();
        }

        return resultSet;
    }
}
