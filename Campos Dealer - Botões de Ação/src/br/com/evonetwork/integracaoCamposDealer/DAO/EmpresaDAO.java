package br.com.evonetwork.integracaoCamposDealer.DAO;

import java.math.BigDecimal;

import br.com.evonetwork.integracaoCamposDealer.Model.*;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
public class EmpresaDAO {

	public static void atualizarProspect(Empresa e, BigDecimal codemp) throws Exception {
		
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			hnd.setPriorityLevel(JapeSession.LOW_PRIORITY);// em casos de deadlock, esta sess o cai
			JapeWrapper empresaDAO = JapeFactory.dao("Empresa");
			DynamicVO servico = empresaDAO.findByPK(codemp);
			empresaDAO.prepareToUpdate(servico)
				.set("AD_IDCAMPOSDEALER", e.getCodEmpresa() )
				.update();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception("Erro ao integrar empresa: " + ex.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}


}
