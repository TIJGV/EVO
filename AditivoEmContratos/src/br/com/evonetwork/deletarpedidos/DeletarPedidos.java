package br.com.evonetwork.deletarpedidos;

import java.math.BigDecimal;

import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class DeletarPedidos {

	public void removerCabs(BigDecimal nunota) throws Exception {
		System.out.println("DELETANDO CABECALHO TESTE 1");
		SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper configDao = JapeFactory.dao("CabecalhoNota");
			configDao.deleteByCriteria("NUNOTA = " + nunota);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}finally {
			JapeSession.close(hnd);
		}
	}
}
