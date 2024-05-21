package br.com.evonetwork.cancelarOS.Controller;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;

import org.jdom.Element;

import com.sankhya.util.JdbcUtils;
import com.sankhya.util.XMLUtils;

import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.fluid.FluidUpdateVO;
import br.com.sankhya.modelcore.comercial.CentralCabecalhoNota;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class Controller {

	public static BigDecimal validarECancelarOS(Registro linha, String motivoCancelamento) throws Exception {
		BigDecimal numOS = (BigDecimal) linha.getCampo("NUMOS");
		String statusOS = (String) linha.getCampo("STATUS");
		BigDecimal nuNotaPedidoOrigem = (BigDecimal) linha.getCampo("NUNOTA");
		validarMotivoDoCancelamento(motivoCancelamento);
		if ("A".equals(statusOS)) {
			try {
				if (!todasAsPecasForamDevolvidasOuNaoAlocadas(numOS)) {
					throw new Exception("<b>Cancelamento não permitido.</b><br>A OS " + numOS
							+ " possui peças que não estão com o status \"Devolvido\" ou \"Previsto\".");
				}
				cancelarOS(numOS, nuNotaPedidoOrigem, motivoCancelamento);
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception(e.getMessage());
			}
			return numOS;
		}
		throw new Exception("A Ordem de Serviço só pode ser cancelada quando está no status \"Aberta\".");
	}

	private static void validarMotivoDoCancelamento(String motivoCancelamento) throws Exception {
		if (motivoCancelamento.length() < 20) {
			throw new Exception("O motivo do cancelamento precisa ter pelo menos 20 caracteres!");
		}
	}

	private static void cancelarOS(BigDecimal numOS, BigDecimal nuNotaPedidoOrigem, String motivoCancelamento)
			throws Exception {
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			hnd.execWithTX((JapeSession.TXBlock) new JapeSession.TXBlock() {
				public void doWithTx() throws Exception {
					((FluidUpdateVO) ((FluidUpdateVO) ((FluidUpdateVO) JapeFactory.dao("TCSOSE")
							.prepareToUpdateByPK(new Object[] { numOS }).set("STATUS", (Object) "C"))
							.set("NUNOTA", (Object) null))
							.set("MOTIVOCANCELAMENTOBOTAO", (Object) motivoCancelamento.toCharArray())).update();
				}
			});
			if (cancelarPedidoOrigem(nuNotaPedidoOrigem, numOS)) {
				System.out.println("A Nota " + nuNotaPedidoOrigem + " foi cancelada!");
				BigDecimal nuNotaCancelada = getNuNotaCancelada(nuNotaPedidoOrigem);
				hnd.execWithTX((JapeSession.TXBlock) new JapeSession.TXBlock() {
					public void doWithTx() throws Exception {
						((FluidUpdateVO) JapeFactory.dao("TCSOSE").prepareToUpdateByPK(new Object[] { numOS })
								.set("NUNOTACAN", (Object) nuNotaCancelada)).update();
					}
				});
			} else {
				System.out.println("Erro ao cancelar a nota " + nuNotaPedidoOrigem);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
		JapeSession.close(hnd);
		System.out.println("OS " + numOS + " foi cancelada.");
	}

	private static BigDecimal getNuNotaCancelada(BigDecimal nuNotaPedidoOrigem) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		JapeSession.SessionHandle hnd = null;
		BigDecimal dado = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			sql = new NativeSql(jdbc);
			sql.appendSql("SELECT NUMNOTA FROM TGFCAN WHERE NUNOTA = " + nuNotaPedidoOrigem);
			System.out.println("SQL: " + sql.toString());
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
		JdbcUtils.closeResultSet(rset);
		NativeSql.releaseResources(sql);
		JdbcWrapper.closeSession(jdbc);
		JapeSession.close(hnd);
		return dado;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static boolean cancelarPedidoOrigem(BigDecimal nunota, BigDecimal numOs) throws Exception {
		String justificativa = "A Ordem de Serviço (" + numOs + ") vinculada com esta nota foi cancelada.";
		JapeSession.putProperty("br.com.sankhya.mgecom.centralnotas.NotaSendoCancelada", (Object) "S");
		JapeSession.putProperty("br.com.sankhya.mgecom.centralnotas.validacaoSubstituicaoNfse", (Object) false);
		CentralCabecalhoNota centralCabNota = new CentralCabecalhoNota();
		CentralCabecalhoNota.CancelamentoNotaCtx contextoCancelamentoNota = new CentralCabecalhoNota.CancelamentoNotaCtx(
				new BigDecimal("0"), justificativa);
		ArrayList<BigDecimal> notas = new ArrayList<BigDecimal>();
		notas.add(nunota);
		Element resposta = centralCabNota.cancelarNotasBatch((Collection) notas, contextoCancelamentoNota);
		String qtdCancelada = XMLUtils.getAttributeAsString(resposta, "totalNotasCanceladas");
		return !"0".equals(qtdCancelada);
	}

	private static boolean todasAsPecasForamDevolvidasOuNaoAlocadas(BigDecimal numOS) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			sql = new NativeSql(jdbc);
			sql.appendSql("SELECT STATUS FROM AD_TCSPRO WHERE NUMOS = " + numOS);
			System.out.println("SQL: " + sql.toString());
			rset = sql.executeQuery();
			while (rset.next()) {
				String status = rset.getString("STATUS");
				if (!"D".equals(status)) {
					if ("P".equals(status)) {
						continue;
					}
					return false;
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
		JdbcUtils.closeResultSet(rset);
		NativeSql.releaseResources(sql);
		JdbcWrapper.closeSession(jdbc);
		JapeSession.close(hnd);
		return true;
	}
}
