package br.com.evonetwork.tgfcab;

import java.math.BigDecimal;

import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class AtualizaCab {
	
		public void AtualizaCabVlrtot(BigDecimal nunota, BigDecimal vlrnota) throws Exception {
			JapeSession.SessionHandle hnd = null;
			try {
				hnd = JapeSession.open();
				JapeWrapper servicoExecutadoDAO = JapeFactory.dao("CabecalhoNota");
				DynamicVO servico = servicoExecutadoDAO.findOne("NUNOTA = " + nunota);
				servicoExecutadoDAO.prepareToUpdate(servico)
					.set("VLRNOTA", vlrnota)
					.update();
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception(e.getMessage());
			} finally {
				JapeSession.close(hnd);
			}
		}
}
