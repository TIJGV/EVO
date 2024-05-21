package br.com.evonetwork.url;

import java.sql.ResultSet;

import com.sankhya.util.JdbcUtils;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class GetLink {
	
	public static String getLink(String linkDaBase, String linkDaTela, String pkJason, String mensagem) {
		
		//Exemplos
//		String linkDaBase = "'http://unapel2.nuvemdatacom.com.br:9707/";
//		String linkDaTela = "br.com.sankhya.menu.adicional.nuDsb.99.1";
//		String pkJason = "{\"P_CODPARC\":"+codparc+"}";
//		String mensagem = "Acessa aqui";
		//******************************
		
		String link = null;
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
			sql.appendSql("SELECT \r\n"
					+ "\r\n"
					+ " '<a target=\"_top\" href=\"'||linkBase||'mge/system.jsp#app/'||encodedString||'/'||pkEncoded||'\">Clique aqui</a>' AS LINK\r\n"
					+ "\r\n"
					+ "FROM(\r\n"
					+ "SELECT \r\n"
					+ "\r\n"
					+ " utl_raw.cast_to_varchar2(utl_encode.base64_encode(utl_raw.cast_to_raw(A.idTela))) as encodedString,\r\n"
					+ " utl_raw.cast_to_varchar2(utl_encode.base64_encode(utl_raw.cast_to_raw(A.jsonPk))) as pkEncoded,\r\n"
					+ "a.*\r\n"
					+ "\r\n"
					+ "FROM \r\n"
					+ "(SELECT\r\n"
					+ "\r\n"
					+ "'"+pkJason+"' AS jsonPk,\r\n"
					+ "'"+linkDaTela+"' AS idTela,\r\n"
					+ "'"+linkDaBase+"' AS linkBase,\r\n"
					+ "'"+mensagem+"' AS mensagem\r\n"
					+ "FROM DUAL)  A) B");
			rset = sql.executeQuery();

			while (rset.next()) {
				link = rset.getString("LINK");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
		return link;
	}
}
