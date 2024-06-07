package br.com.evonetwork.atualizaPedido.Controller;

import java.math.BigDecimal;
import java.sql.ResultSet;

import com.sankhya.util.JdbcUtils;

import br.com.evonetwork.atualizaPedido.Model.InfoProduto;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class ProdutoController {

	public static InfoProduto buscaInfoProduto(BigDecimal codprod) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;

		InfoProduto infoProduto = new InfoProduto();

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT  PROD.CODVOL, "
					+ "	nvl(MAR.AD_CODTIPVENDA, 0) AS CODTIPVENDA, "
					+ "	nvl(MAR.AD_CODTIPOPER, 0) AS CODTIPOPER "
					+ "FROM  TGFPRO PROD "
					+ "LEFT JOIN TGFMAR MAR ON MAR.CODIGO = PROD.CODMARCA "
					+ "WHERE PROD.CODPROD = " + codprod);
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			if (rset.next()) {
				infoProduto.setCodtipoper(rset.getBigDecimal("CODTIPOPER"));
				infoProduto.setCodtipvenda(rset.getBigDecimal("CODTIPVENDA"));
				infoProduto.setCodvol(rset.getString("CODVOL"));
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
		return infoProduto;
	}

}
