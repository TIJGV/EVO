package br.com.evonetwork.buscarDadosReceitaSefaz.Dao;

import br.com.evonetwork.buscarDadosReceitaSefaz.Provedor.Provedor;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.math.BigDecimal;
import java.sql.ResultSet;

public class CidadeDao extends GenericoDao{

    public static BigDecimal buscarOuCriarCidade(String nome){

        BigDecimal codCidade = BigDecimal.ZERO;
        String nomeProcessado = Provedor.retirarEspacosEmBanco(nome);

        int qtdRetornada = verificaQtdLinhas("TSICID", "UPPER(NOMECID) LIKE UPPER('" + nomeProcessado + "')");

        if (qtdRetornada == 0){
            System.out.println("CRIAR CIDADE: "+ nome);
        } else if (qtdRetornada > 0){
            codCidade = buscarBairroPeloCidade(nome);
            System.out.println("CIDADE Retornou: "+ codCidade + " - Com Qtd Linhas: "+ qtdRetornada);
        }

        return codCidade;
    }

    private static BigDecimal buscarBairroPeloCidade(String nome) {
        JdbcWrapper jdbc;
        EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
        jdbc = dwfEntityFacade.getJdbcWrapper();

        NativeSql nativeSql = null;
        ResultSet rs = null;
        BigDecimal end = null;

        String nomeProcessado = Provedor.retirarEspacosEmBanco(nome);

        try {
            jdbc.openSession();
            nativeSql = new NativeSql(jdbc);
            StringBuilder sql = new StringBuilder();
            sql.append(" SELECT MIN(E.CODCID) as CODCID ");
            sql.append(" FROM TSICID E ");
            sql.append(" WHERE UPPER(NOMECID) LIKE UPPER('").append(nomeProcessado).append("')");

            rs = nativeSql.executeQuery(sql.toString());

            if (rs.next()) {
                end = rs.getBigDecimal("CODCID");
            }

        } catch (Exception e) {
            System.out.printf("Erro ao consultar Cidade: "+ e.getMessage());
            jdbc.closeSession();
        }
        finally {
            jdbc.closeSession();
        }

        return end;
    }

}
