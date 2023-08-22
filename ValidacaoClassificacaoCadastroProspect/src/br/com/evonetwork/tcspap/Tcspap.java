package br.com.evonetwork.tcspap;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.sankhya.util.JdbcUtils;

import br.com.evonetwork.clacadpapcam.AtributosNucampo;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class Tcspap {
	public static Boolean tcspap(ArrayList<AtributosNucampo> nomeCampos, BigDecimal codpap, BigDecimal codclassifcadpap, Boolean atualizado) {
		System.out.println(">> tcspap");
		int validacao = 0;	
		String resultado = null;
		atualizado = false;
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
			System.out.println("nomeCampos: " + nomeCampos.size());
			
			for (int i = 0; i < nomeCampos.size(); i++) {
				sql = new NativeSql(jdbc);
				if(nomeCampos.get(i).getNometab().equals("TCSPAP")) {
					sql.appendSql("SELECT " + nomeCampos.get(i).getNomecampo() + " FROM TCSPAP WHERE CODPAP = " + codpap);
				}
				if(nomeCampos.get(i).getNometab().equals("TCSCTT")) {
					sql.appendSql("SELECT " + nomeCampos.get(i).getNomecampo() + " FROM TCSCTT WHERE CODPAP = " + codpap);
				}
						
				System.out.println(sql.toString());
				rset = sql.executeQuery();
				while (rset.next()) {
					resultado = rset.getString(nomeCampos.get(i).getNomecampo());
					String resultadoVazio = resultado.trim();
					if(resultadoVazio.equals("")) {
						resultado = null;
					}
					System.out.println("resultado: " + resultado);
				}

				if (resultado != null) {
					validacao++;
				}
				System.out.println("validacao: " + validacao);
				if (validacao == nomeCampos.size()) {
					atualizado = UpdateTcspap.update(codpap, codclassifcadpap);
					validacao = 0;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
			System.out.println("<< tcspap");
		}
		return atualizado;
	}
}
