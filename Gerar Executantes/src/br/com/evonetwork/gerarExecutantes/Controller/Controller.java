package br.com.evonetwork.gerarExecutantes.Controller;

import java.sql.ResultSet;
import java.util.ArrayList;

import com.sankhya.util.JdbcUtils;

import br.com.evonetwork.gerarExecutantes.Model.Etapa;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class Controller {
	public static ArrayList<Etapa> buscarEtapas() throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;

		ArrayList<Etapa> codparc = new ArrayList<Etapa>();

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT  PRO.CODPROD AS CODSERV, (SELECT INTEIRO FROM TSIPAR WHERE CHAVE = 'CODPRODCRM') AS CODPROD  "
					+ "FROM TCSMOD MOD  LEFT JOIN TGFPRO PRO ON MOD.CODPROD = PRO.CODPROD  "
					+ "LEFT JOIN TCSSEM SEM ON SEM.CODPROD = PRO.CODPROD  "
					+ "WHERE  MOD.CODMETOD = (      SELECT  "
					+ "        INTEIRO       FROM          TSIPAR       WHERE  "
					+ "        CHAVE = 'SERVMETPRE'  )   " );
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			while (rset.next()) {
				Etapa etapa = new Etapa();
				etapa.setCodserv(rset.getBigDecimal("CODSERV"));
				etapa.setCodprod(rset.getBigDecimal("CODPROD"));
				codparc.add(etapa);
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
	
	public static void gerarExecutante(Etapa etapa ) throws Exception {
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

			sql.appendSql("SELECT "
					+ "USU.CODVEND, USU.CODUSU "
					+ "FROM TGFVEN VEN, TSIUSU USU " 
					+ "WHERE VEN.CODVEND = USU.CODVEND "
					+ "AND USU.CODVEND <> 0 "
					+ "AND USU.CODUSU NOT IN (SELECT  DISTINCT "
					+ "NVL(SEU.CODUSU, 0) "
					+ "FROM TCSMOD MOD  LEFT JOIN TGFPRO PRO ON MOD.CODPROD = PRO.CODPROD "
					+ "LEFT JOIN TCSSEM SEM ON SEM.CODPROD = PRO.CODPROD "
					+ "LEFT JOIN TGFSEU SEU ON SEU.CODSERV = PRO.CODPROD "
					+ "WHERE  MOD.CODMETOD = (SELECT INTEIRO FROM TSIPAR WHERE CHAVE = 'SERVMETPRE')  "
					+ "AND PRO.CODPROD = "+ etapa.getCodserv() + ")" );
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			while (rset.next()) {
				JapeWrapper cabecalhoDAO = JapeFactory.dao("ServicoProdutoExecutante");
				cabecalhoDAO.create()
						.set("CODSERV", etapa.getCodserv())
						.set("CODPROD", etapa.getCodprod())
						.set("CODUSU", rset.getBigDecimal("CODUSU"))
						.save();
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
}
