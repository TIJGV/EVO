package br.com.evonetwork.gerarPedido.controller;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.sankhya.util.JdbcUtils;
import com.sankhya.util.StringUtils;

import br.com.evonetwork.gerarPedido.model.ProdutoNegociado;
import br.com.evonetwork.gerarPedido.model.TCSOSE;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.modelcore.util.MGECoreParameter;

public class Controller {
	public static TCSOSE buscarInfoOS(BigDecimal numos) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;

		TCSOSE tcsose = new TCSOSE();

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT * FROM TCSOSE WHERE NUMOS = " + numos);
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			if (rset.next()) {
				tcsose.setAd_codemp(rset.getBigDecimal("AD_CODEMP"));
				tcsose.setCodpap(rset.getBigDecimal("CODPAP"));
				tcsose.setCodcencus(rset.getBigDecimal("CODCENCUS"));
				tcsose.setCodoat(rset.getBigDecimal("CODOAT"));
				tcsose.setCodnat(rset.getBigDecimal("AD_CODNAT"));
				tcsose.setCodvend(rset.getBigDecimal("CODVEND"));
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
		return tcsose;
	}

	public static BigDecimal buscarLocalPadrao(BigDecimal codemp) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;

		BigDecimal local = BigDecimal.ZERO;

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT AD_CODLOCALMAQ FROM TSIEMP WHERE CODEMP = " + codemp);
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			if (rset.next()) {
				local = rset.getBigDecimal("AD_CODLOCALMAQ");
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
		return local;
	}

	public static BigDecimal buscarParceiroPorCPF_CNPJ(String cgc_cpf) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;

		BigDecimal codparc = BigDecimal.ZERO;

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT * FROM TGFPAR WHERE CGC_CPF = " + cgc_cpf);
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			if (rset.next()) {
				codparc = rset.getBigDecimal("CODPARC");

				if (!StringUtils.getNullAsEmpty(rset.getString("CLIENTE")).equals("S")) {
					JapeWrapper servicoExecutadoDAO = JapeFactory.dao("Parceiro");
					DynamicVO servico = servicoExecutadoDAO.findByPK(codparc);
					servicoExecutadoDAO.prepareToUpdate(servico).set("CLIENTE", "S").update();

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
		return codparc;
	}

	public static BigDecimal buscarGerarParceiro(BigDecimal codpap) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;

		BigDecimal codParc = null;

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT * FROM TCSPAP WHERE CODPAP = " + codpap);
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			if (rset.next()) {
				codParc = rset.getBigDecimal("CODPARC");
				String cgc_cpf = rset.getString("CGC_CPF");

				if (codParc == null || codParc.compareTo(BigDecimal.ZERO) == 0) {

					codParc = buscarParceiroPorCPF_CNPJ(cgc_cpf);

					if (codParc.compareTo(BigDecimal.ZERO) == 0) {
						codParc = new BigDecimal(MGECoreParameter.getParameterAsInt("PARCGENCP"));

					}

					JapeWrapper servicoExecutadoDAO = JapeFactory.dao("ParceiroProspect");
					DynamicVO servico = servicoExecutadoDAO.findOne("CODPAP = " + codpap);
					servicoExecutadoDAO.prepareToUpdate(servico).set("CODPARC", codParc).update();

					return codParc;
				}

				return codParc;
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
		return codParc;
	}
	
	public static BigDecimal buscarNunota(BigDecimal numos) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;

		BigDecimal nunota = BigDecimal.ZERO;

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql(
					"SELECT CAB.NUNOTA FROM TGFCAB CAB, TCSOSE OSE WHERE OSE.NUNOTA = CAB.NUNOTA AND OSE.NUMOS = CAB.NUMOS AND CAB.NUMOS = "
							+ numos);
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			if (rset.next()) {
				nunota = rset.getBigDecimal("NUNOTA");
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
		return nunota;
	}

	public static ArrayList<ProdutoNegociado> buscarProdutoNegociado(BigDecimal codpap, BigDecimal numos)
			throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;

		ArrayList<ProdutoNegociado> produtoNegociado = new ArrayList<ProdutoNegociado>();

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT " + "PRO.CODPROD, " + "PRO.QTDNEG, " + "PRO.VLRUNIT, PRO.CODLOCAL, PRO.CONTROLE, "
					+ "PROD.CODVOL, " + "nvl(MAR.AD_CODTIPVENDA, 0) AS CODTIPVENDA, "
					+ "nvl(MAR.AD_CODTIPOPER, 0) AS CODTIPOPER " + "FROM AD_TGFPRONEG PRO "
					+ "LEFT JOIN TGFPRO PROD ON PRO.CODPROD = PROD.CODPROD "
					+ "LEFT JOIN TGFMAR MAR ON MAR.CODIGO = PROD.CODMARCA " + "WHERE PRO.CODPAP = " + codpap
					+ " AND PRO.NUMOS = " + numos);
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			while (rset.next()) {
				ProdutoNegociado produto = new ProdutoNegociado();
				produto.setCodprod(rset.getBigDecimal("CODPROD"));
				produto.setCodtipvenda(rset.getBigDecimal("CODTIPVENDA"));
				produto.setQtdneg(rset.getBigDecimal("QTDNEG"));
				produto.setVlrunit(rset.getBigDecimal("VLRUNIT"));
				produto.setCodtipoper(rset.getBigDecimal("CODTIPOPER"));
				produto.setCodvol(rset.getString("CODVOL"));
				produto.setCodlocal(rset.getBigDecimal("CODLOCAL"));
				produto.setControle(rset.getString("CONTROLE"));
				produtoNegociado.add(produto);
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
		return produtoNegociado;
	}
}
