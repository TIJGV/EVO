package br.com.evonetwork.tcspre;

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

public class ColetaDadosDosCamposDaTcspre {
	ArrayList<CamposDaTcspre> listaTcspre = new ArrayList<CamposDaTcspre>();
	int nroItensTcspre = 0;
	int contador = 0;
	BigDecimal numContrato;
	BigDecimal codProduto;
	
	public void coletarDadosTcspre(BigDecimal numContrato, BigDecimal codProduto) {
		this.numContrato = numContrato;
		this.codProduto = codProduto;
		
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		NativeSql sql2 = null; //para pegar o nro de itens da Tcspre
		ResultSet rset = null;
		ResultSet rset2 = null; //para pegar o nro de itens da Tcspre
		SessionHandle hnd = null;

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql2 = new NativeSql(jdbc);

			sql2.appendSql("SELECT COUNT(*) FROM TCSPRE WHERE NUMCONTRATO = " + numContrato + " AND CODPROD = " + codProduto);
			rset2 = sql2.executeQuery();

			while (rset2.next()) {
				nroItensTcspre = (rset2.getInt("COUNT(*)"));
			}
			
			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT * FROM TCSPRE WHERE NUMCONTRATO = " + numContrato + " AND CODPROD = " + codProduto);
			rset = sql.executeQuery();
			
			while (rset.next()) {
				CamposDaTcspre itensTcspre = new CamposDaTcspre();
				itensTcspre.setCodprod(rset.getBigDecimal("CODPROD"));
				itensTcspre.setCodserv(rset.getBigDecimal("CODSERV"));
				itensTcspre.setCodterrespar(rset.getBigDecimal("CODTERRESPAR"));
				itensTcspre.setNumcontrato(rset.getBigDecimal("NUMCONTRATO"));
				itensTcspre.setValor(rset.getBigDecimal("VALOR"));
				itensTcspre.setReferencia(rset.getString("REFERENCIA"));
				getListaTcspre().add(contador, itensTcspre);
				
				
				setContador(contador + 1);
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
	
	public int quantidadeDePrecosNoProduto() {
		return 	getListaTcspre().size();
	}
	public CamposDaTcspre getListaTcspre(int i) {
		return listaTcspre.get(i);
	}

	public String ValoresTcspre(int i) {
		return getListaTcspre().get(i).toString();
	}

	public ArrayList<CamposDaTcspre> getListaTcspre() {
		return listaTcspre;
	}

	public void setListaTcspre(ArrayList<CamposDaTcspre> listaTcspre) {
		this.listaTcspre = listaTcspre;
	}

	public void setContador(int contador) {
		this.contador = contador;
	}
}
