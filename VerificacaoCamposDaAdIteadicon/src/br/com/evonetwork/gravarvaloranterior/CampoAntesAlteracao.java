package br.com.evonetwork.gravarvaloranterior;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sankhya.util.JdbcUtils;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class CampoAntesAlteracao {
	private String nomeCampo;
	private String valorAntigo;
	private BigDecimal numContrato;
	private String tipCampo;
	
	public CampoAntesAlteracao(PersistenceEvent event, String tipCampo) throws Exception {
		DynamicVO evento = (DynamicVO) event.getVo();
		this.tipCampo = tipCampo;
		BigDecimal campoAlter = evento.asBigDecimal("CAMPOALTER");
		BigDecimal codAditivo = evento.asBigDecimal("CODADITIVO");
		BigDecimal sequencia = evento.asBigDecimal("SEQUENCIA");
		
		ValorAntigo(campoAlter, codAditivo);
		GravarValorAntesDaAlteracao(codAditivo, sequencia);
	}
	
	public void ValorAntigo(BigDecimal campoAlter, BigDecimal codAditivo) {

		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		
		NativeSql sql2 = null;
		ResultSet rset2 = null;
		
		NativeSql sql3 = null;
		ResultSet rset3 = null;

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);
			sql.appendSql("SELECT NOMECAMPO FROM TDDCAM WHERE NUCAMPO = " + campoAlter);
			rset = sql.executeQuery();

			while (rset.next()) {
				this.nomeCampo = rset.getString("NOMECAMPO");	
			}
			
			sql3 = new NativeSql(jdbc);
			sql3.appendSql("SELECT NUMCONTRATO FROM AD_ADICON WHERE CODADITIVO = " + codAditivo);
			rset3 = sql3.executeQuery();

			while (rset3.next()) {
				this.numContrato = rset3.getBigDecimal("NUMCONTRATO");
			}
			
			
			sql2 = new NativeSql(jdbc);
			sql2.appendSql("SELECT " + this.nomeCampo + " FROM TCSCON WHERE NUMCONTRATO = " + this.numContrato);
			rset2 = sql2.executeQuery();

			while (rset2.next()) {
				this.valorAntigo = rset2.getString(this.nomeCampo);
			}
		} catch (
		Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
			
			JdbcUtils.closeResultSet(rset2);
			NativeSql.releaseResources(sql2);
			
			JdbcUtils.closeResultSet(rset3);
			NativeSql.releaseResources(sql3);
		}
	}
	
	public void GravarValorAntesDaAlteracao(BigDecimal codAditivo, BigDecimal sequencia) throws Exception {
		JapeSession.SessionHandle hnd = null;
		if(this.tipCampo.equals("D")) {
			this.valorAntigo = convertDate(this.valorAntigo);
		}
		try {
			hnd = JapeSession.open();
			JapeWrapper servicoExecutadoDAO = JapeFactory.dao("AD_ITEADICON");
			DynamicVO servico = servicoExecutadoDAO.findOne(" CODADITIVO = " + codAditivo + " AND SEQUENCIA = " + sequencia);
			servicoExecutadoDAO.prepareToUpdate(servico)
				.set("VALORANTIGO", this.valorAntigo)
				.update();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}
	public static String convertDate(String mDate) {
		if(mDate == null || mDate == "" || "null".equals(mDate))
			return "";
	   SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   try {
	          Date newDate = inputFormat.parse(mDate);
	          inputFormat = new SimpleDateFormat("dd/MM/yyyy");
	          mDate = inputFormat.format(newDate);
	    } catch (ParseException e) {
	          e.printStackTrace();
	    }

	   return mDate;
	}
}
