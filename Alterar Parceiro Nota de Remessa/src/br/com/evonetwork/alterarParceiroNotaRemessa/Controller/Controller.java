package br.com.evonetwork.alterarParceiroNotaRemessa.Controller;

import br.com.sankhya.jape.EntityFacade;
import java.sql.ResultSet;
import br.com.sankhya.jape.dao.JdbcWrapper;
import com.sankhya.util.JdbcUtils;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.jape.core.JapeSession;
import java.math.BigDecimal;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.event.PersistenceEvent;

public class Controller {
	
    public static void iniciarAlteracao(PersistenceEvent event) {
        try {
            DynamicVO tgfcabVO = (DynamicVO)event.getVo();
            BigDecimal nuNota = tgfcabVO.asBigDecimal("NUNOTA");
            BigDecimal codTOP = tgfcabVO.asBigDecimal("CODTIPOPER");
            System.out.println("TOP: "+codTOP+", Nota: "+nuNota);
            if (verificarSeEhTopCorreta(codTOP)) {
                System.out.println("TOP correta.");
                BigDecimal numOS = tgfcabVO.asBigDecimal("AD_NUMOSOFICINA");
                if (numOS == null)
                    throw new Exception("Número da O.S. não encontrado para a nota "+nuNota);
                BigDecimal codParc = buscarCodParceiroDoOrcamentoDaOS(numOS);
                if (codParc == null)
                    throw new Exception("Parceiro não encontrado para o orçamento da O.S. "+numOS);
                System.out.println("Alterando parceiro da nota "+nuNota+" para: "+codParc);
                tgfcabVO.setProperty("CODPARC", (Object)codParc);
            } else {
                System.out.println("TOP incorreta.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static boolean verificarSeEhTopCorreta(BigDecimal codTOP) {
        return BigDecimal.valueOf(1129L).equals(codTOP) || BigDecimal.valueOf(1131L).equals(codTOP) || BigDecimal.valueOf(1134L).equals(codTOP) || BigDecimal.valueOf(1137L).equals(codTOP);
    }
    
    private static BigDecimal buscarCodParceiroDoOrcamentoDaOS(BigDecimal numOS) throws Exception {
        JdbcWrapper jdbc = null;
        NativeSql sql = null;
        ResultSet rset = null;
        JapeSession.SessionHandle hnd = null;
        BigDecimal codParc = null;
        try {
            hnd = JapeSession.open();
            hnd.setFindersMaxRows(-1);
            EntityFacade entity = EntityFacadeFactory.getDWFFacade();
            jdbc = entity.getJdbcWrapper();
            jdbc.openSession();
            
            sql = new NativeSql(jdbc);
            
            sql.appendSql("SELECT CAB.CODPARC AS CODPARC FROM TGFCAB CAB INNER JOIN AD_TCSOSE OSE ON CAB.NUNOTA = OSE.NUNOTA WHERE OSE.NUMOS = "+numOS);
            System.out.println("SQL: "+sql.toString());
            
            rset = sql.executeQuery();
            
            if (rset.next())
                codParc = rset.getBigDecimal("CODPARC");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Erro ao buscar Parceiro do Orçamento da OS: "+e.getMessage());
        } finally {
            JdbcUtils.closeResultSet(rset);
            NativeSql.releaseResources(sql);
            JdbcWrapper.closeSession(jdbc);
            JapeSession.close(hnd);
        }
        return codParc;
    }
}
