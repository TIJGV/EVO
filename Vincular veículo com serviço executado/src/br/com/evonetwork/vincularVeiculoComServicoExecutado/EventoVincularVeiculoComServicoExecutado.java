package br.com.evonetwork.vincularVeiculoComServicoExecutado;

import java.math.BigDecimal;
import java.sql.ResultSet;

import com.sankhya.util.JdbcUtils;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.ModifingFields;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class EventoVincularVeiculoComServicoExecutado implements EventoProgramavelJava {

	// Evento na AD_TCSITE
	
	@Override
	public void afterDelete(PersistenceEvent arg0) throws Exception {}

	@Override
	public void afterInsert(PersistenceEvent arg0) throws Exception {}

	@Override
	public void afterUpdate(PersistenceEvent arg0) throws Exception {}

	@Override
	public void beforeCommit(TransactionContext arg0) throws Exception {}

	@Override
	public void beforeDelete(PersistenceEvent arg0) throws Exception {}

	@Override
	public void beforeInsert(PersistenceEvent event) throws Exception {
		vincularVeiculoComServicoExecutado(event, false);
	}

	@Override
	public void beforeUpdate(PersistenceEvent event) throws Exception {
		vincularVeiculoComServicoExecutado(event, true);
	}
	
	private void vincularVeiculoComServicoExecutado(PersistenceEvent event, boolean isUpdate) throws Exception {
		System.out.println("***EVO - VINCULAR VEICULO COM SERVICO EXECUTADO - INICIO***");
		try {
			if(isUpdate) {
				ModifingFields md = event.getModifingFields();
				if (md.isModifing("CODVEICULO")) {
					System.out.println("Alteração manual do veículo, cancelando evento...");
					System.out.println("***EVO - VINCULAR VEICULO COM SERVICO EXECUTADO - FIM***");
					return;
				}
			}
			DynamicVO servicoExecutadoVO = (DynamicVO) event.getVo();
			BigDecimal codServ = servicoExecutadoVO.asBigDecimal("CODSERV");
			if(servicoEhKmRodado(codServ)) {
				BigDecimal respOficina = servicoExecutadoVO.asBigDecimal("CODUSURESP");
				BigDecimal codParceiroDoRespOficina = getCodParcDoUsuario(respOficina);
				BigDecimal codVeiculo = getVeiculoDoResponsavelOficina(codParceiroDoRespOficina);
				atualizarCodVeiculoDoServicoExecutado(servicoExecutadoVO, codVeiculo);
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		System.out.println("***EVO - VINCULAR VEICULO COM SERVICO EXECUTADO - FIM***");
	}
	
	private void atualizarCodVeiculoDoServicoExecutado(DynamicVO servicoExecutadoVO, BigDecimal codVeiculo) {
		servicoExecutadoVO.setProperty("CODVEICULO", codVeiculo);
	}
	
	private BigDecimal getCodParcDoUsuario(BigDecimal respOficina) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal dado = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT CODPARC FROM TSIUSU WHERE CODUSU = "+respOficina);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal("CODPARC");
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
		return dado;
	}
	
	private BigDecimal getVeiculoDoResponsavelOficina(BigDecimal respOficina) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal dado = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT CODVEICULO FROM TGFVEI WHERE CODMOTORISTA = "+respOficina);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal("CODVEICULO");
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
		return dado;
	}
	
	private boolean servicoEhKmRodado(BigDecimal codServ) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		String dado = "";
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT CODVOL FROM TGFPRO WHERE CODPROD = "+codServ);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getString("CODVOL");
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
		if("KM".equals(dado))
			return true;
		else
			return false;
	}
	
}
