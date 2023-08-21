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

public class BairroDao  extends GenericoDao{


    public static BigDecimal buscarOuCriarBairro(String nome){

        BigDecimal codBairro = BigDecimal.ZERO;
        String nomeProcessado = Provedor.retirarEspacosEmBanco(nome);

        int qtdRetornada = verificaQtdLinhas("TSIBAI", "UPPER(NOMEBAI) LIKE UPPER('" + nomeProcessado + "')");

        if (qtdRetornada == 0){
            System.out.println("CRIAR BAIRRO: "+ nome);
            BigDecimal registro = inserirRegistro(nome);
            System.out.println("Registro cadastrado: "+ registro.toString());
        } else if (qtdRetornada > 0){
            codBairro = buscarBairroPeloNome(nome);
            System.out.println("BAIRRO Retornou: "+ codBairro + " - Com Qtd Linhas: "+ qtdRetornada);
        }

        return codBairro;
    }

    private static BigDecimal buscarBairroPeloNome(String nome) {
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
            sql.append(" SELECT MAX(E.CODBAI) as CODBAIRRO ");
            sql.append(" FROM TSIBAI E ");
            sql.append(" WHERE UPPER(NOMEBAI) LIKE UPPER('").append(nomeProcessado).append("')");

            rs = nativeSql.executeQuery(sql.toString());

            if (rs.next()) {
                end = rs.getBigDecimal("CODBAIRRO");
            }

        } catch (Exception e) {
            System.out.printf("Erro ao consultar bairro: "+ e.getMessage());
            jdbc.closeSession();
        }
        finally {
            jdbc.closeSession();
        }

        return end;
    }

    public static BigDecimal inserirRegistro(final String nome) {
        BigDecimal codbairro = BigDecimal.ZERO;
        JapeSession.SessionHandle hnd = null;
        try {
            hnd = JapeSession.open();
            JapeWrapper apontamentoDao = JapeFactory.dao("Bairro");
            DynamicVO save = apontamentoDao.create()
                    .set("NOMEBAI", nome)
                    .set("CODREG", BigDecimal.ZERO)
                    .set("DTALTER", TimeUtils.getNow())
                    .save();

            codbairro = save.asBigDecimal("CODBAI");
        } catch (Exception e) {
            System.out.println("Erro na Inserção do bairro: " +  ExceptionUtils.getStackTrace(e));
        } finally {
            JapeSession.close(hnd);
        }
        return codbairro;
    }

}
