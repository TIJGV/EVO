package br.com.evonetwork.integracaoAPISimova.DAO;

import java.math.BigDecimal;
import java.sql.ResultSet;

import com.sankhya.util.JdbcUtils;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class EnderecoDAO {
	
	public static String getEndereco(BigDecimal codEnd) {
		String endereco = null;
		
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

			sql.appendSql("SELECT TIPO, NOMEEND FROM TSIEND WHERE CODEND = "+codEnd);

			rset = sql.executeQuery();

			if (rset.next()) {
				endereco = rset.getString("TIPO")+" "+rset.getString("NOMEEND");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
		
		return endereco;
	}

	public static String getCidade(BigDecimal codCid) {
		String cidade = null;
		
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

			sql.appendSql("SELECT NOMECID FROM TSICID WHERE CODCID = "+codCid);

			rset = sql.executeQuery();

			if (rset.next()) {
				cidade = rset.getString("NOMECID");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
		
		return cidade;
	}

	public static String getUFpelaCidade(BigDecimal codCid) {
		String uf = null;
		BigDecimal codUf = null;
		
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		NativeSql sql2 = null;
		ResultSet rset = null;
		ResultSet rset2 = null;
		SessionHandle hnd = null;

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			
			//Pegando CODUF
			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT UF FROM TSICID WHERE CODCID = "+codCid);

			rset = sql.executeQuery();

			if (rset.next()) {
				codUf = rset.getBigDecimal("UF");
			}
			
			//Pegando sigla do UF
			sql2 = new NativeSql(jdbc);

			sql2.appendSql("SELECT UF FROM TSIUFS WHERE CODUF = "+codUf);

			rset2 = sql2.executeQuery();
			
			if (rset2.next()) {
				uf = rset2.getString("UF");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rset);
			JdbcUtils.closeResultSet(rset2);
			NativeSql.releaseResources(sql);
			NativeSql.releaseResources(sql2);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
		
		return uf;
	}

	public static String getBairro(BigDecimal codBai) {
		String bairro = null;
		
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

			sql.appendSql("SELECT NOMEBAI FROM TSIBAI WHERE CODBAI = "+codBai);

			rset = sql.executeQuery();

			if (rset.next()) {
				bairro = rset.getString("NOMEBAI");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
		
		return bairro;
	}

}
