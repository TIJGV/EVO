package br.com.evonetwork.adadicon;

import java.math.BigDecimal;

import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class UpdateAdAdicon {
	public static void update(BigDecimal codaditivo, String link) throws Exception {
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper servicoExecutadoDAO = JapeFactory.dao("AD_ADICON");
			DynamicVO servico = servicoExecutadoDAO.findOne("CODADITIVO = " + codaditivo);
			servicoExecutadoDAO.prepareToUpdate(servico)
				.set("CAMPOTESTEDOIS", link)
				.update();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}
}
