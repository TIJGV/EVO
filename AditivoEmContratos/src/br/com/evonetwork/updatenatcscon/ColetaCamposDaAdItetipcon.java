package br.com.evonetwork.updatenatcscon;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.sankhya.util.JdbcUtils;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class ColetaCamposDaAdItetipcon {
	private int nroItensAdItetipcon = 0;
	private int contadorAdItetipcon = 0;
	ArrayList<CamposDaAdItetipcon> listaAdItetipcon = new ArrayList<CamposDaAdItetipcon>();
	UpdateNaTcscon updateNaTcscon = new UpdateNaTcscon();

	public void coletarDadosAdItetipcon(BigDecimal codAditivo, BigDecimal numContrato) {

		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		NativeSql sql2 = null; // para pegar o nro de itens da Tcspre
		ResultSet rset = null;
		ResultSet rset2 = null; // para pegar o nro de itens da Tcspre
		SessionHandle hnd = null;

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql2 = new NativeSql(jdbc);
			sql2.appendSql("SELECT COUNT(*) FROM  AD_ITEADICON WHERE CODADITIVO = " + codAditivo);
			rset2 = sql2.executeQuery();

			while (rset2.next()) {
				nroItensAdItetipcon = (rset2.getInt("COUNT(*)"));
			}

			sql = new NativeSql(jdbc);
			sql.appendSql("SELECT ITE.ALTERARPARA ,"
					+ "(SELECT NOMECAMPO FROM TDDCAM WHERE NUCAMPO = ITE.CAMPOALTER) AS CAMPOALTER, "
					+ "(SELECT CASE WHEN TIPCAMPO = 'D' THEN 'Data' "
					+ "WHEN TIPCAMPO = 'F' THEN 'BigDecimal' "
					+ "WHEN TIPCAMPO = 'I' THEN 'BigDecimal' "
					+ "WHEN TIPCAMPO = 'S' THEN 'String' "
					+ "WHEN TIPCAMPO = 'T' THEN 'String' END AS TIPCAMPO FROM TDDCAM WHERE NUCAMPO =ITE.CAMPOALTER) AS TIPOCAMPO"
					+ " FROM  AD_ITEADICON ITE WHERE CODADITIVO = " + codAditivo);
			rset = sql.executeQuery();

			while (rset.next()) {
				CamposDaAdItetipcon itensAdItetipcon = new CamposDaAdItetipcon();
				itensAdItetipcon.setCampoalter(rset.getString("CAMPOALTER"));
				itensAdItetipcon.setAlterarpara(rset.getString("ALTERARPARA"));
				itensAdItetipcon.setTipocampo(rset.getString("TIPOCAMPO"));
				listaAdItetipcon.add(contadorAdItetipcon, itensAdItetipcon);
					
				updateNaTcscon.updateNaTcscon(numContrato,
						itensAdItetipcon.getCampoalter(),
						itensAdItetipcon.getAlterarpara(),
						itensAdItetipcon.getTipocampo());
			}

		} catch (

		Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcUtils.closeResultSet(rset2);
			NativeSql.releaseResources(sql2);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
	}
}
