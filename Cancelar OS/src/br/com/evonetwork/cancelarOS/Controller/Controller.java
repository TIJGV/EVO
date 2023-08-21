package br.com.evonetwork.cancelarOS.Controller;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.jdom.Element;

import com.sankhya.util.JdbcUtils;
import com.sankhya.util.XMLUtils;

import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.modelcore.comercial.CentralCabecalhoNota;
import br.com.sankhya.modelcore.comercial.CentralCabecalhoNota.CancelamentoNotaCtx;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class Controller {

	public static BigDecimal validarECancelarOS(Registro linha) throws Exception {
		BigDecimal numOS = (BigDecimal) linha.getCampo("NUMOS");
		String statusOS = (String) linha.getCampo("STATUS");
		BigDecimal nuNotaPedidoOrigem = (BigDecimal) linha.getCampo("NUNOTA");
		if("A".equals(statusOS)) {
			try {
				if(todasAsPecasForamDevolvidasOuNaoAlocadas(numOS))
					cancelarOS(numOS, nuNotaPedidoOrigem);
				else
					throw new Exception("<b>Cancelamento não permitido.</b><br>A OS "+numOS+" possui peças que não estão com o status \"Devolvido\" ou \"Previsto\".");
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception(e.getMessage());
			}
			return numOS;
		} else {
			throw new Exception("A Ordem de Serviço só pode ser cancelada quando está no status \"Aberta\".");
		}
	}

	private static void cancelarOS(BigDecimal numOS, BigDecimal nuNotaPedidoOrigem) throws Exception {
		SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			hnd.execWithTX(new JapeSession.TXBlock() {
				public void doWithTx() throws Exception {
					JapeFactory.dao("TCSOSE").prepareToUpdateByPK(numOS)
						.set("STATUS", "C")
						.set("NUNOTA", null)
						.update();
				}
			});
			if(cancelarPedidoOrigem(nuNotaPedidoOrigem, numOS)) {
				System.out.println("A Nota "+nuNotaPedidoOrigem+" foi cancelada!");
				BigDecimal nuNotaCancelada = getNuNotaCancelada(nuNotaPedidoOrigem);
				hnd.execWithTX(new JapeSession.TXBlock() {
					public void doWithTx() throws Exception {
						JapeFactory.dao("TCSOSE").prepareToUpdateByPK(numOS)
							.set("NUNOTACAN", nuNotaCancelada)
							.update();
					}
				});
			} else
				System.out.println("Erro ao cancelar a nota "+nuNotaPedidoOrigem);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
		System.out.println("OS "+numOS+" foi cancelada.");
	}

	private static BigDecimal getNuNotaCancelada(BigDecimal nuNotaPedidoOrigem) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal dado = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT NUMNOTA FROM TGFCAN WHERE NUNOTA = "+nuNotaPedidoOrigem);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal(1);
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

	private static boolean cancelarPedidoOrigem(BigDecimal nunota, BigDecimal numOs) throws Exception {
		String justificativa = "A Ordem de Serviço ("+numOs+") vinculada com esta nota foi cancelada.";

		JapeSession.putProperty("br.com.sankhya.mgecom.centralnotas.NotaSendoCancelada", "S");
		JapeSession.putProperty("br.com.sankhya.mgecom.centralnotas.validacaoSubstituicaoNfse", false);

		CentralCabecalhoNota centralCabNota = new CentralCabecalhoNota();

		CancelamentoNotaCtx contextoCancelamentoNota = new CentralCabecalhoNota.CancelamentoNotaCtx(new BigDecimal("0"), justificativa);

		ArrayList<BigDecimal> notas = new ArrayList<BigDecimal>();
		notas.add(nunota);

		Element resposta = centralCabNota.cancelarNotasBatch(notas, contextoCancelamentoNota);

		String qtdCancelada = XMLUtils.getAttributeAsString(resposta, "totalNotasCanceladas");

		if ("0".equals(qtdCancelada)) {
			return false;
		} else {
			return true;
		}
	}

	private static boolean todasAsPecasForamDevolvidasOuNaoAlocadas(BigDecimal numOS) throws Exception {
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
			
			sql.appendSql("SELECT STATUS FROM AD_TCSPRO WHERE NUMOS = "+numOS);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			while (rset.next()) {
				String status = rset.getString("STATUS");
				if("D".equals(status) || "P".equals(status)) //Adicionado status "Prevista" em 07/07/2023 a pedido do Leonardo (para peças que ainda não foram alocadas)
					continue;
				else
					return false;
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
