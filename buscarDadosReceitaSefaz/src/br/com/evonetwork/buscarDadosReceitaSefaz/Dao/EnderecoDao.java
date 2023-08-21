package br.com.evonetwork.buscarDadosReceitaSefaz.Dao;

import br.com.evonetwork.buscarDadosReceitaSefaz.Provedor.Provedor;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import com.sankhya.util.TimeUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.math.BigDecimal;
import java.sql.ResultSet;

public class EnderecoDao extends GenericoDao {


    public static BigDecimal buscarOuCriarEndereco(String nome){

        BigDecimal codEndereco = BigDecimal.ZERO;
        String nomeProcessado = Provedor.retirarEspacosEmBanco(nome);

        int qtdRetornada = verificaQtdLinhas("TSIEND", "UPPER(TIPO + NOMEEND) LIKE UPPER('" + nomeProcessado + "')");

        if (qtdRetornada == 0){
            System.out.println("CRIAR ENDERECO: "+ nome);
            BigDecimal registro = inserirRegistro(nome);
            System.out.println("Registro cadastrado: "+ registro.toString());
        } else if (qtdRetornada > 0){
            codEndereco = buscarEnderecoPeloNome(nome);
            System.out.println("ENDERECO Retornou: "+ codEndereco + " - Com Qtd Linhas: "+ qtdRetornada);
        }

        return codEndereco;
    }

    private static BigDecimal buscarEnderecoPeloNome(String nome) {
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
            sql.append(" SELECT MAX(E.CODEND) as CODEND ");
            sql.append(" FROM TSIEND E ");
            sql.append(" WHERE UPPER(TIPO + E.NOMEEND) LIKE UPPER('").append(nomeProcessado).append("')");

            rs = nativeSql.executeQuery(sql.toString());

            if (rs.next()) {
                end = rs.getBigDecimal("CODEND");
            }

        } catch (Exception e) {
            System.out.printf("Erro ao consultar Estado: "+ e.getMessage());
            jdbc.closeSession();
        }
        finally {
            jdbc.closeSession();
        }

        return end;
    }

    public static BigDecimal inserirRegistro(final String nome) {
        BigDecimal codend = BigDecimal.ZERO;
        JapeSession.SessionHandle hnd = null;
        try {
            hnd = JapeSession.open();
            hnd.setCanTimeout(false);

            JapeWrapper apontamentoDao = JapeFactory.dao("Endereco");
            DynamicVO save = apontamentoDao.create()
                    .set("NOMEEND", Provedor.retornarRuaSemPrefixo(nome))
                    .set("TIPO", Provedor.retornarTipoDaRua(nome))
                    .set("DTALTER", TimeUtils.getNow())
                    .save();

            codend = save.asBigDecimal("CODEND");

        } catch (Exception e) {
            System.out.println("Erro na Inserção do endereço: " +  ExceptionUtils.getStackTrace(e));
        } finally {
            JapeSession.close(hnd);
        }
        return codend;
    }



}
