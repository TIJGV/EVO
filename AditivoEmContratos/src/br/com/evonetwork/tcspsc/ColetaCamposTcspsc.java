package br.com.evonetwork.tcspsc;

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

public class ColetaCamposTcspsc {
	ArrayList<CamposDaTcspsc> listaTcspsc = new ArrayList<CamposDaTcspsc>();
	int nroItensTcspsc = 0;
	int contador = 0;
	
	public void coletarDadosTcspsc(BigDecimal numContrato) {

		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		NativeSql sql2 = null; //para pegar o nro de itens da Tcspsc
		ResultSet rset = null;
		ResultSet rset2 = null; //para pegar o nro de itens da Tcspsc
		SessionHandle hnd = null;

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql2 = new NativeSql(jdbc);

			sql2.appendSql("SELECT COUNT(*) FROM TCSPSC WHERE NUMCONTRATO = " + numContrato);
			rset2 = sql2.executeQuery();

			while (rset2.next()) {
				nroItensTcspsc = (rset2.getInt("COUNT(*)"));
			}
			
			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT * FROM TCSPSC WHERE NUMCONTRATO = " + numContrato);
			rset = sql.executeQuery();

			while (rset.next()) {
				CamposDaTcspsc itensTcspsc = new CamposDaTcspsc();
				//bigdecimal
				itensTcspsc.setTopfaturcon(rset.getBigDecimal("TOPFATURCON"));
				itensTcspsc.setVlrunit(rset.getBigDecimal("VLRUNIT"));
				itensTcspsc.setQtdmeses(rset.getBigDecimal("QTDMESES"));
				itensTcspsc.setQtdeprevista(rset.getBigDecimal("QTDEPREVISTA"));
				itensTcspsc.setNumusuarios(rset.getBigDecimal("NUMUSUARIOS"));
				itensTcspsc.setNumcontrato(rset.getBigDecimal("NUMCONTRATO"));
				itensTcspsc.setGrupimpressao(rset.getBigDecimal("GRUPIMPRESSAO"));
				itensTcspsc.setFrequencia(rset.getBigDecimal("FREQUENCIA"));
				itensTcspsc.setCodprod(rset.getBigDecimal("CODPROD"));
				itensTcspsc.setCodparcpref(rset.getBigDecimal("CODPARCPREF"));
				itensTcspsc.setQtdusu(rset.getBigDecimal("QTDUSU"));

				//STRING
				itensTcspsc.setLimitante(rset.getString("LIMITANTE"));
				itensTcspsc.setImpros(rset.getString("IMPROS"));
				itensTcspsc.setImprnota(rset.getString("IMPRNOTA"));
				itensTcspsc.setVersao(rset.getString("VERSAO"));
				itensTcspsc.setSerfaturcon(rset.getString("SERFATURCON"));
				itensTcspsc.setObservacao(rset.getString("OBSERVACAO"));
				itensTcspsc.setSitprod(rset.getString("SITPROD"));
				itensTcspsc.setNumserie(rset.getString("NUMSERIE"));
				itensTcspsc.setAd_codbem(rset.getString("AD_CODBEM"));
				itensTcspsc.setProdprinc(rset.getString("PRODPRINC"));
				itensTcspsc.setDtversao(rset.getString("DTVERSAO"));
				listaTcspsc.add(contador, itensTcspsc);
				contador++;
			}
		} catch (Exception e) {
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

	public int quantidadeDeProdutosNoContrato() {
		return 	listaTcspsc.size();
	}
	public CamposDaTcspsc getListaTcspsc(int i) {
		return listaTcspsc.get(i);
	}

	@Override
	public String toString() {
		return "nroItensTcspsc: " + nroItensTcspsc;
	}
}
