package br.com.thinklife.unapel.os.iniciar;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.sankhya.util.JdbcUtils;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.PersistenceException;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.thinklife.unapel.os.consulta.buscaDados;
import br.com.thinklife.unapel.os.consulta.buscaDadosStatusPeca;

public class fecharOS implements AcaoRotinaJava
{
    public void doAction(final ContextoAcao arg0) throws Exception {
        try {
//            final BigDecimal codUsuLogado = arg0.getUsuarioLogado();
            final Registro[] linhas = arg0.getLinhas();
            final EntityFacade dwf = EntityFacadeFactory.getDWFFacade();
            final JdbcWrapper jdbc = dwf.getJdbcWrapper();
            jdbc.openSession();
            Registro[] array;
            for (int length = (array = linhas).length, i = 0; i < length; ++i) {
                final Registro linha = array[i];
                final BigDecimal NUMOS = (BigDecimal)linha.getCampo("NUMOS");
                final String DIAGNOSTICOFECHAMENTO = new StringBuilder().append(linha.getCampo("DIAGNOSTICOFECHAMENTO")).toString();
//                final String DATAFECHAMENTO = new StringBuilder().append(linha.getCampo("DATAFECHAMENTO")).toString();
                final String STATUS = new StringBuilder().append(linha.getCampo("STATUS")).toString();
                final BigDecimal qtdmin = new BigDecimal(new StringBuilder().append(arg0.getParametroSistema("QTDMINCARACTER")).toString());
                final String sql2 = buscaDados.devolucaoMqpUtilizada(NUMOS);
                final PreparedStatement pstm = jdbc.getPreparedStatement(sql2);
                final ResultSet consulta1 = pstm.executeQuery();
                while (consulta1.next()) {
                    if (!consulta1.getString("NOTADEVOLUCAO_STATUS").equals("L")) {
                        arg0.mostraErro("<br><br>As ferramentas precisam ser devolvidas antes de encerrar a OS!!<br><br>");
                    }
                }
                if(existeServico(NUMOS)) { // Alteração a pedido do Rafael
	                final String sql3 = "SELECT COUNT(*) as COUNT \r\nFROM AD_TCSITE ITE\r\nINNER JOIN TSIUSU USU ON ITE.CODUSURESP = USU.CODUSU\r\nWHERE USU.AD_GERCOB = 'S' AND NUMOS =" + NUMOS;
	                final PreparedStatement pstmaux = jdbc.getPreparedStatement(sql3);
	                final ResultSet aux = pstmaux.executeQuery();
	                while (aux.next()) {
	                    if (aux.getBigDecimal("COUNT").compareTo(BigDecimal.ZERO) <= 0) {
	                        throw new Exception("Atenção, mecânico informado nos serviços executados não gera cobrança, favor verifique as informações!");
	                    }
                	}
                }
                final ResultSet queryVerificarStatusPeca = buscaDadosStatusPeca.VerificarStatusPeca(jdbc, NUMOS);
                while (queryVerificarStatusPeca.next()) {
                    if (queryVerificarStatusPeca.getString("STATUS").equalsIgnoreCase("P") || queryVerificarStatusPeca.getString("STATUS").equalsIgnoreCase("R")) {
                        arg0.mostraErro("Não é possível fechar OS com peças com status previsto ou requerido");
                    }
                }
                if (DIAGNOSTICOFECHAMENTO == null || DIAGNOSTICOFECHAMENTO.isEmpty() || DIAGNOSTICOFECHAMENTO.equals("null")) {
                    arg0.mostraErro("'Ação não permitida!',<br>'Para realizar o fechamento da Ordem de Serviço é obrigatório o preenchimento dos campos: Diagnostico do Fechamento!'");
                }
                if (DIAGNOSTICOFECHAMENTO.equalsIgnoreCase("")) {
                    arg0.mostraErro("'Ação não permitida!',\r\n                'Para realizar o fechamento da Ordem de Serviço é obrigatório o preenchimento dos campos: Diagnostico do Fechamento!'");
                }
                if (qtdmin.compareTo(new BigDecimal(DIAGNOSTICOFECHAMENTO.length())) > 0) {
                    arg0.mostraErro("<br><br>Favor especifique melhor o laudo de encerramento com pelo menos " + qtdmin + " caracteres <br><br>");
                }
                if (STATUS.equalsIgnoreCase("F")) {
                    arg0.mostraErro("'Ação não permitida!',\r\n                'Ordem de Serviço está Fechada, não pode ser alterada!',\r\n                ''");
                }
                validaQuantidadeDasPecas(NUMOS);
                final String sql4 = "    UPDATE AD_TCSOSE SET \r\n            STATUSSERVICO    = 'C',\r\n            STATUS            = 'F',\r\n            FECHAMENTO        = 'S',\r\n            DATAFECHAMENTO    = NVL(DATAFECHAMENTO,SYSDATE),\r\n            DTHFECHAMENTO = SYSDATE WHERE NUMOS = " + NUMOS;
                final PreparedStatement updateSQL = jdbc.getPreparedStatement(sql4);
                updateSQL.executeUpdate();
                arg0.setMensagemRetorno("Fechado com sucesso !");
            }
        }
        catch (PersistenceException e) {
            arg0.mostraErro(e.toString());
        }
    }

	private void validaQuantidadeDasPecas(BigDecimal NUMOS) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT I.NUMOS,\n"
					+ "I.NUMITEM,\n"
					+ "(TRUNC((\n"
					+ "SELECT (((TO_DATE('01/01/2022'|| ' ' ||SUBSTR(LPAD(COALESCE(HRFINAL,0),4,0),0,2) || ':' || SUBSTR(LPAD(COALESCE(HRFINAL,0),4,0),3,4)||':00' ,'dd/mm/yyyy HH24:MI:SS')-\n"
					+ "TO_DATE('01/01/2022'|| ' ' ||SUBSTR(LPAD(COALESCE(HRINICIAL,0),4,0),0,2) || ':' || SUBSTR(LPAD(COALESCE(HRINICIAL,0),4,0),3,4)||':00','dd/mm/yyyy HH24:MI:SS'))-\n"
					+ "(TO_DATE('01/01/2022'|| ' ' ||SUBSTR(LPAD(COALESCE(INTERVALO,0),4,0),0,2) || ':' || SUBSTR(LPAD(COALESCE(INTERVALO,0),4,0),3,4)||':00','dd/mm/yyyy HH24:MI:SS')-\n"
					+ "TO_DATE('01/01/2022 00:00:00','dd/mm/yyyy HH24:MI:SS')))*1440)/60 AS MINUTOS\n"
					+ "FROM AD_TCSITE ITE\n"
					+ "WHERE ITE.NUMOS =I.NUMOS AND ITE.NUMITEM = I.NUMITEM),2)) AS HSTRABALHADAS,\n"
					+ "(TRUNC((SELECT\n"
					+ "((TO_DATE('01/01/2022'|| ' ' ||SUBSTR(LPAD(COALESCE(HORAAPONTAMENTOS,0),4,0),0,2) || ':' || SUBSTR(LPAD(COALESCE(HORAAPONTAMENTOS,0),4,0),3,4)||':00' ,'dd/mm/yyyy HH24:MI:SS')-\n"
					+ "TO_DATE('01/01/2022 00:00:00','dd/mm/yyyy HH24:MI:SS'))*1440)/60 AS MINUTOS\n"
					+ "FROM AD_TCSITE ITE\n"
					+ "WHERE ITE.NUMOS = I.NUMOS AND ITE.NUMITEM = I.NUMITEM),2)) AS APONTAMENTOS, \n"
					+ "COALESCE((select COALESCE(pro.AD_TEMPPADRAO,0)/100 from tgfpro pro where pro.codprod = I.codserv),0) AS TEMPOPADRAO, \n"
					+ "I.QTDNEG, \n"
					+ "I.CODSERV \n"
					+ "FROM AD_TCSITE I \n"
					+ "WHERE NUMOS = "+NUMOS);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			while (rset.next()) {
				BigDecimal quantidade = rset.getBigDecimal("QTDNEG");
				BigDecimal codprod = rset.getBigDecimal("CODSERV");
				
				String tipoCalculo = getTipoCalculo(codprod);
        		if(tipoCalculo == null || "#".equals(tipoCalculo) || "".equals(tipoCalculo)) {
        			JdbcUtils.closeResultSet(rset);
        			throw new Exception("Tipo de cálculo de preço não preenchido para o serviço "+codprod);
        		}
        		if("V".equals(tipoCalculo)) { // Horas Vendidas x Valor Hora
    				BigDecimal hrsTrabalhadas = rset.getBigDecimal("HSTRABALHADAS");
					if(hrsTrabalhadas == null || hrsTrabalhadas.compareTo(BigDecimal.ZERO) == 0) {
        				BigDecimal hrsApontamentos = rset.getBigDecimal("APONTAMENTOS");
        				if(hrsApontamentos == null || hrsApontamentos.compareTo(BigDecimal.ZERO) == 0) {
        					JdbcUtils.closeResultSet(rset);
    						throw new Exception("Horas não preenchidas para o serviço "+codprod);
    					}
        		} else if("F".equals(tipoCalculo)) { // Preço Fixo
    				return;
        		} else if("N".equals(tipoCalculo)) { // Preço Manual
    				return;
        		} else if("P".equals(tipoCalculo)) { // Tempo Padrão x Valor
    				quantidade = rset.getBigDecimal("TEMPOPADRAO");
    				if(quantidade == null || quantidade.compareTo(BigDecimal.ZERO) == 0) {
    					JdbcUtils.closeResultSet(rset);
						throw new Exception("Tempo padrão não preenchido para o serviço "+codprod);
					}
        		} else if("Q".equals(tipoCalculo)) { // Quantidade x Valor
    				if(quantidade == null || quantidade.compareTo(BigDecimal.ZERO) == 0) {
    					JdbcUtils.closeResultSet(rset);
						throw new Exception("Quantidade não preenchida para o serviço "+codprod);
					}
        		}
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
	}

	private String getTipoCalculo(BigDecimal codprod) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		String dado = "";
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT AD_TIPOCALCPRECO FROM TGFPRO WHERE CODPROD = "+codprod);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getString("AD_TIPOCALCPRECO");
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
		return dado;
	}

	private boolean existeServico(BigDecimal numOS) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT * FROM AD_TCSITE WHERE NUMOS = "+numOS);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				return true;
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
		return false;
	}
}
