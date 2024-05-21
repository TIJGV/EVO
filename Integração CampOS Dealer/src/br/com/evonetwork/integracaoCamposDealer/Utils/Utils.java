package br.com.evonetwork.integracaoCamposDealer.Utils;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sankhya.util.JdbcUtils;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class Utils {

	public static Timestamp convertDate(String mDate) {
		Timestamp retorno = null;
		if(mDate == null || mDate == "" || "null".equals(mDate))
			return null;
	    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S");
	    try {
	    	Date newDate = inputFormat.parse(mDate);
	        retorno = new Timestamp(newDate.getTime());
	    } catch (ParseException e) {
	    	 e.printStackTrace();
	    }
	    return retorno;
	}

	public static BigDecimal buscarCodigoUF(String estado) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal codUF = BigDecimal.ZERO;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT CODUF FROM TSIUFS WHERE UF = '"+estado+"'");
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				codUF = rset.getBigDecimal("CODUF");
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
		return codUF;
	}

}
