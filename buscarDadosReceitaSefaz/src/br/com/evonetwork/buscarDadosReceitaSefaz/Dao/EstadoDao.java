package br.com.evonetwork.buscarDadosReceitaSefaz.Dao;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.math.BigDecimal;
import java.sql.ResultSet;

public class EstadoDao {
    public static String buscarEstadoPeloCodigo(BigDecimal coduf) {

        JdbcWrapper jdbc;
        EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
        jdbc = dwfEntityFacade.getJdbcWrapper();

        NativeSql nativeSql = null;
        ResultSet rs = null;
        String uf = null;

        try {
            jdbc.openSession();
            nativeSql = new NativeSql(jdbc);

            StringBuilder sql = new StringBuilder();
            sql.append(" SELECT UF ");
            sql.append(" FROM TSIUFS UFS ");
            sql.append(" WHERE UFS.CODUF = ").append(coduf);

            rs = nativeSql.executeQuery(sql.toString());

            System.out.printf("SQL: "+ sql.toString());

            while (rs.next()) {
                uf = rs.getString("UF");
            }

        } catch (Exception e) {
            System.out.printf("Erro ao consultar Estado: "+ e.getMessage());
            jdbc.closeSession();
        }
        finally {
            jdbc.closeSession();
        }

        return uf;
    }
}
