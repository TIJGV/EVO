package br.com.evonetwork.botaodeacao;

import java.math.BigDecimal;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class Delete implements AcaoRotinaJava{

	@Override
	public void doAction(ContextoAcao contexto) throws Exception {
		try {
			Registro[] linhas = contexto.getLinhas();

			for (Registro linha : linhas) {

				BigDecimal sequencia = (BigDecimal) linha.getCampo("SEQUENCIA");
				removerRegistros(sequencia);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void removerRegistros(BigDecimal sequencia) throws Exception {
		SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper configDao = JapeFactory.dao("AD_TESTE");
			configDao.deleteByCriteria("SEQUENCIA = " + sequencia);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}finally {
			JapeSession.close(hnd);
		}
	}

}
