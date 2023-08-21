package br.com.thinklife.unapel.os.processamento;

import java.sql.ResultSet;

import com.sankhya.util.JdbcUtils;

import java.math.BigDecimal;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.PrePersistEntityState;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.EntityFacade;
import br.com.thinklife.unapel.os.consulta.buscaDadosStatusPeca;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.modelcore.comercial.ContextoRegra;

public class atualizaStatusPeca
{
    public static void verificarNota(final ContextoRegra arg0) throws Exception {
    	System.out.println("Iniciando atualização de status da peça...");
        final EntityFacade dwf = EntityFacadeFactory.getDWFFacade();
        final JdbcWrapper jdbcWrapper = dwf.getJdbcWrapper();
        jdbcWrapper.openSession();
        final String nomeEnty = arg0.getPrePersistEntityState().getDao().getEntityName();
        if (!"CabecalhoNota".equals(nomeEnty)) {
            return;
        }
        final PrePersistEntityState state = arg0.getPrePersistEntityState();
        final DynamicVO itens = state.getNewVO();
        final boolean confirmando = JapeSession.getProperty("CabecalhoNota.confirmando.nota") != null;
        final BigDecimal nunota = itens.asBigDecimal("NUNOTA");
        System.out.println("Nota "+nunota+" confirmando: "+confirmando);
//      final String statusnota = itens.asString("STATUSNOTA");
        if (confirmando) {
            final ResultSet pecasDevolvidas = buscaDadosStatusPeca.VerificarPecaDevolvida(jdbcWrapper, nunota);
//            ResultSet pecasDevolvidas = buscaDadosStatusPeca.VerificarPecaDevolvidaNativeSQL(jdbcWrapper, nunota);
            while (pecasDevolvidas.next()) {
                final BigDecimal nunotaPedido = pecasDevolvidas.getBigDecimal("NUNOTA");
                final BigDecimal sequenciaPedido = pecasDevolvidas.getBigDecimal("SEQUENCIA");
                final BigDecimal qtdDevolvida = pecasDevolvidas.getBigDecimal("QTD_DEVOLVIDA");
                final BigDecimal qtdSolicitada = pecasDevolvidas.getBigDecimal("QTD_SOLICITADA");
                final String status = pecasDevolvidas.getString("STATUS");
                BigDecimal qtdNegociacao = getQtdNegDaOS(nunotaPedido, sequenciaPedido);
                System.out.println("Qtd devolvida: "+qtdDevolvida);
                System.out.println("Qtd negociação: "+qtdNegociacao);
                System.out.println("Qtd solicitada: "+qtdSolicitada);
                if(qtdDevolvida != null) {
	                if(qtdDevolvida.compareTo(qtdNegociacao) > 0) {
	                	throw new Exception("Quantidade devolvida maior que a quantidade da OS!\r\nQtd devolvida: "+qtdDevolvida+" - Qtd da OS: "+qtdNegociacao);
	                }
                }
                BigDecimal codProd = pecasDevolvidas.getBigDecimal("CODPROD");
                if(codProd != null) {
	                if(verificaSeEhPeca(codProd)) {
	                	System.out.println("Tem peças devolvidas.");
	                	buscaDadosStatusPeca.updateStatusPecaDevolvido(jdbcWrapper, nunotaPedido, sequenciaPedido, status, qtdDevolvida);
	                } else {
	                	System.out.println("Tem ferramentas devolvidas.");
	                	buscaDadosStatusPeca.updateStatusFerramentaDevolvida(jdbcWrapper, nunotaPedido, sequenciaPedido, status, qtdDevolvida);
	                }
                }
            }
            final ResultSet pecasEntregues = buscaDadosStatusPeca.VerificarPecaEntregue(jdbcWrapper, nunota);
            while (pecasEntregues.next()) {
        		System.out.println("Tem peças entregues.");
        		buscaDadosStatusPeca.updateStatusPecaEntregue(jdbcWrapper, pecasEntregues.getBigDecimal("NUNOTA"), pecasEntregues.getBigDecimal("SEQUENCIA"));
            }
            final ResultSet ferramentasEntregues = buscaDadosStatusPeca.VerificarFerramentaEntregue(jdbcWrapper, nunota);
            while (ferramentasEntregues.next()) {
            	System.out.println("Tem ferramentas entregues.");
            	buscaDadosStatusPeca.updateStatusFerramentaEntregue(jdbcWrapper, ferramentasEntregues.getBigDecimal("NUNOTA"), ferramentasEntregues.getBigDecimal("SEQUENCIA"));
            }
        }
        jdbcWrapper.closeSession();
        System.out.println("Fim atualização de status da peça...");
    }

	private static BigDecimal getQtdNegDaOS(BigDecimal nunotaPedido, BigDecimal sequenciaPedido) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal qtd = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT QUANTIDADE FROM AD_TCSPRO WHERE NUMREQUISICAO = "+nunotaPedido+" AND SEQREQUISICAO = "+sequenciaPedido);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				qtd = rset.getBigDecimal(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
		return qtd;
	}

	private static boolean verificaSeEhPeca(BigDecimal codProd) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		String ehFerramenta = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT AD_FERRAMENTAOFICINA FROM TGFGRU WHERE CODGRUPOPROD = (SELECT CODGRUPOPROD FROM TGFPRO WHERE CODPROD = "+codProd+")");
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				ehFerramenta = rset.getString("AD_FERRAMENTAOFICINA");
				if("S".equals(ehFerramenta)) {
					return false;
				} else {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
		return true;
	}
}
