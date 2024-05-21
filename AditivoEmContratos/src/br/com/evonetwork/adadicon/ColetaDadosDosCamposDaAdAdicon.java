package br.com.evonetwork.adadicon;

import java.math.BigDecimal;
import java.sql.ResultSet;

import com.sankhya.util.JdbcUtils;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class ColetaDadosDosCamposDaAdAdicon {
	
	CamposDaAdAdicon adAdicon = new CamposDaAdAdicon();
	
	public void coletarDadosAdAdicon(BigDecimal codigoDoAditivo) {
		
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
			
			sql.appendSql("SELECT * FROM AD_ADICON WHERE CODADITIVO = " + codigoDoAditivo);
			rset = sql.executeQuery();

			while (rset.next()) {
				getAdAdicon().setCodtipadi(rset.getBigDecimal("CODTIPADI"));
				getAdAdicon().setCodmotadi(rset.getBigDecimal("CODMOTADI"));
				getAdAdicon().setConcopia(rset.getBigDecimal("CONCOPIA"));
				getAdAdicon().setCodaditivo(rset.getBigDecimal("CODADITIVO"));
				getAdAdicon().setNumcontrato(rset.getBigDecimal("NUMCONTRATO"));
				getAdAdicon().setCodusu(rset.getBigDecimal("CODUSU"));
				getAdAdicon().setDhalter(rset.getString("DHALTER"));

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
	}
	public CamposDaAdAdicon getAdAdicon() {
		return adAdicon;
	}
	@Override
	public String toString() {
		return getAdAdicon().toString();
	}
}
