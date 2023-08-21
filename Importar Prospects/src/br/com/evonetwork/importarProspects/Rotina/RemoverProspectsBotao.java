package br.com.evonetwork.importarProspects.Rotina;

import java.math.BigDecimal;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.DynamicEntityNames;

public class RemoverProspectsBotao implements AcaoRotinaJava {

	//Botão na AD_IMPPROSP
	
	@Override
	public void doAction(ContextoAcao ca) throws Exception {
		System.out.println("***EVO - REMOVER IMPORTAÇÃO DE PROSPECTS - INICIO***");
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			hnd.setPriorityLevel(JapeSession.LOW_PRIORITY);
			for (int i = 0; i < ca.getLinhas().length; i++) {
	            Registro linha = ca.getLinhas()[i];
                remover(ca, linha);
	        }
		} catch(Exception e) {
			ca.setMensagemRetorno(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
		System.out.println("***EVO - REMOVER IMPORTAÇÃO DE PROSPECTS - FIM***");
	}

	private void remover(ContextoAcao ca, Registro linha) throws Exception {
		BigDecimal nroUnicoImportacao = (BigDecimal) linha.getCampo("NUNICO");
		removerProspectsDestaImportacao(nroUnicoImportacao);
		removerDataEUsuarioDaImportacao(nroUnicoImportacao);
	}

	private void removerProspectsDestaImportacao(BigDecimal nroUnicoImportacao) throws Exception {
		SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper configDao = JapeFactory.dao(DynamicEntityNames.PARCEIRO_PROSPECT);
			configDao.deleteByCriteria("AD_NROIMPORTACAO = "+nroUnicoImportacao);
		} catch (Exception e) {
			System.out.println("Erro ao remover prospects: "+e.getMessage());
			e.printStackTrace();
			throw new Exception("Erro ao remover prospects: "+e.getMessage());
		}finally {
			JapeSession.close(hnd);
		}
	}

	private void removerDataEUsuarioDaImportacao(BigDecimal nroUnicoImportacao) throws Exception {
		SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeFactory.dao("AD_IMPPROSP").prepareToUpdateByPK(nroUnicoImportacao)
				.set("CODUSU", null)
				.set("DHIMPORTACAO", null)
				.update();
		} catch (Exception e) {
			System.out.println("Erro ao remover dados da importação: "+e.getMessage());
			e.printStackTrace();
			throw new Exception("Erro ao remover dados da importação: "+e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

}
