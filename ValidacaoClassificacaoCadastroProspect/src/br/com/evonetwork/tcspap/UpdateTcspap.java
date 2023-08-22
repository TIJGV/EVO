package br.com.evonetwork.tcspap;

import java.math.BigDecimal;

import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class UpdateTcspap {
	public static Boolean update(BigDecimal codpap, BigDecimal codclassifcadpap) throws Exception {
		System.out.println(">> update");

		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper servicoExecutadoDAO = JapeFactory.dao("ParceiroProspect");
			DynamicVO servico = servicoExecutadoDAO.findOne("CODPAP = "+ codpap);
			servicoExecutadoDAO.prepareToUpdate(servico)
			.set("AD_CODCLASSIFCADPAP", codclassifcadpap)
			.update();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
			System.out.println("<< update");
		}
		return true;
		
	}
}
