package br.com.evonetwork.transferirItensDeUmaOS.botao;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;

import com.sankhya.util.JdbcUtils;
import com.sankhya.util.TimeUtils;

import br.com.evonetwork.transferirItensDeUmaOS.Controller.Controller;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class BotaoTransferirFerramentasDaOS implements AcaoRotinaJava {
	
	// botão na tabela TGFITE

	@Override
	public void doAction(ContextoAcao ca) throws Exception {
		System.out.println("***EVO - TRANSFERINDO FERRAMENTAS OS - INICIO***");
		Registro[] linhas = ca.getLinhas();
		Registro line = linhas[0];
		
		BigDecimal nuNotaOriginalTransferencia = (BigDecimal) line.getCampo("NUNOTA");
		BigDecimal codProd = (BigDecimal) line.getCampo("CODPROD");
		
		try {
			validarNota(nuNotaOriginalTransferencia);
			validarSeOSEstaAberta(nuNotaOriginalTransferencia, codProd);
		} catch (Exception e) {
			ca.mostraErro(e.getMessage());
		}
		
		BigDecimal topTransferencia = BigDecimal.valueOf(700);
		BigDecimal empresa = (BigDecimal) line.getCampo("CODEMP");
		Timestamp dtHoje = TimeUtils.getNow();
		
		System.out.println("Lançando cabeçalho de transferência");
		DynamicVO notaTransferencia = Controller.lancarCabecalhoDeTransferencia(empresa, topTransferencia, dtHoje, nuNotaOriginalTransferencia);
		BigDecimal nuNotaTransferenciaGerada = (BigDecimal) notaTransferencia.getProperty("NUNOTA");
		String statusNota = (String) notaTransferencia.getProperty("STATUSNOTA");

		for (Registro linha : linhas) {
			try {
				System.out.println("Percorrendo ferramentas");
				BigDecimal sequencia = (BigDecimal) linha.getCampo("SEQUENCIA");
				if(sequencia.compareTo(BigDecimal.ZERO) == 1)
					Controller.inserirItensNaNotaDeTransferencia(linha, ca, sequencia, nuNotaTransferenciaGerada, statusNota, nuNotaOriginalTransferencia);
			}catch(Exception e) {
				e.printStackTrace();
				ca.mostraErro(e.getMessage());
			}
		}
		String url = Controller.abrirTela(nuNotaTransferenciaGerada);
		ca.setMensagemRetorno("Nota de transferência "+url+" lançada!");
		System.out.println("***EVO - TRANSFERINDO FERRAMENTAS OS - FIM***");
	}

	private void validarSeOSEstaAberta(BigDecimal nuNotaOriginalTransferencia, BigDecimal codProd) throws Exception {
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
			
			sql.appendSql("SELECT STATUS FROM AD_TCSOSE WHERE NUMOS = (SELECT NUMOS FROM AD_MAQUINASUTILIZADAS WHERE NUMREQUISICAO = (SELECT NUNOTAORIG FROM TGFVAR WHERE NUNOTA = "+nuNotaOriginalTransferencia+" AND ROWNUM = 1) AND CODPROD = "+codProd+")");
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				if(!"A".equals(rset.getString("STATUS")) && rset.getString("STATUS") != null)
					throw new Exception("A Ordem de Serviço precisa estar aberta para devolver ferramentas!");
			} else {
				throw new Exception("Ordem de serviço não encontrada para esta transferência!");
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

	private void validarNota(BigDecimal nuNota) throws Exception {
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

			sql.appendSql("SELECT STATUSNOTA, CODTIPOPER FROM TGFCAB WHERE NUNOTA = "+nuNota);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();

			if (rset.next()) {
				if(!"L".equals(rset.getString("STATUSNOTA")))
					throw new Exception("A nota precisa estar confirmada para devolver ferramentas!");
				
				if(rset.getBigDecimal("CODTIPOPER").compareTo(BigDecimal.valueOf(700)) != 0)
					throw new Exception("Tipo de operação inválido para devolução de ferramentas!");
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
