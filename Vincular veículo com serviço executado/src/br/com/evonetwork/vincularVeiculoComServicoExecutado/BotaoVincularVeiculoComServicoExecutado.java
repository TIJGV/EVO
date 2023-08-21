package br.com.evonetwork.vincularVeiculoComServicoExecutado;

import java.math.BigDecimal;
import java.sql.ResultSet;

import com.sankhya.util.JdbcUtils;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class BotaoVincularVeiculoComServicoExecutado implements AcaoRotinaJava {

	// Botão na AD_TCSITE
	
	@Override
	public void doAction(ContextoAcao ca) throws Exception {
		System.out.println("***EVO - VINCULAR VEICULO COM SERVICO EXECUTADO - INICIO***");
		try {
			Registro linhas[] = ca.getLinhas();
			for (Registro linha : linhas) {
				vincularVeiculoComServicoExecutado(linha);
			}
			ca.setMensagemRetorno("O veículo foi vinculado com o serviço executado!");
		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		System.out.println("***EVO - VINCULAR VEICULO COM SERVICO EXECUTADO - FIM***");
	}

	private void vincularVeiculoComServicoExecutado(Registro linha) throws Exception {
		try {
			BigDecimal codServ = (BigDecimal) linha.getCampo("CODSERV");
			if(codServ == null)
				throw new Exception("Nenhum Serviço encontrado!");
			
			if(servicoEhKmRodado(codServ)) {
				BigDecimal respOficina = (BigDecimal) linha.getCampo("CODUSURESP");
				if(respOficina == null)
					throw new Exception("Responsável pela oficina não encontrado!");
				
				BigDecimal codParceiroDoRespOficina = getCodParcDoUsuario(respOficina);
				if(codParceiroDoRespOficina == null)
					throw new Exception("Nenhum parceiro encontrado para o usuário "+respOficina+"!");
				
				BigDecimal codVeiculo = getVeiculoDoResponsavelOficina(codParceiroDoRespOficina);
				if(codVeiculo == null)
					throw new Exception("Nenhum veículo encontrado para o parceiro "+codParceiroDoRespOficina+" vinculado com o usuário "+respOficina+"!");
				
				BigDecimal numOS = (BigDecimal) linha.getCampo("NUMOS");
				BigDecimal numItem = (BigDecimal) linha.getCampo("NUMITEM");
				atualizarCodVeiculoDoServicoExecutado(numOS, numItem, codVeiculo);
			} else {
				throw new Exception("Serviço não é de deslocamento!");
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	private void atualizarCodVeiculoDoServicoExecutado(BigDecimal numOS, BigDecimal numItem, BigDecimal codVeiculo) throws Exception {
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper servicoExecutadoDAO = JapeFactory.dao("TCSITE");
			DynamicVO servicoExecutado = servicoExecutadoDAO.findOne(" NUMOS = "+numOS+" AND NUMITEM = "+numItem);
			servicoExecutadoDAO.prepareToUpdate(servicoExecutado)
				.set("CODVEICULO", codVeiculo)
				.update();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
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
