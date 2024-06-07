package br.com.evonetwork.integracaoCamposDealer.DAO;

import java.math.BigDecimal;
import java.sql.Timestamp;

import br.com.evonetwork.integracaoCamposDealer.Model.DadosCliente;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class CttProspectDAO {
	public static void criarCttProspect(DadosCliente dadosCliente, Timestamp dataDaAlteracao, BigDecimal codpap)
			throws Exception {
		JapeSession.SessionHandle hnd = null;

		try {
			hnd = JapeSession.open();
			hnd.setCanTimeout(false);
			hnd.setPriorityLevel(JapeSession.LOW_PRIORITY);// em casos de deadlock, esta sess o cai
			JapeWrapper empresaDAO = JapeFactory.dao("ContatoProspect");
			empresaDAO.create().set("CODPAP", codpap).set("NOMECONTATO", dadosCliente.getNome())
					.set("TELEFONE", dadosCliente.getTelefone()).set("EMAIL", dadosCliente.getMailCliente())
					.set("CELULAR", dadosCliente.getTelefone2()).save();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Erro ao criar Contato Prospect: " + e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}
	
	public static void atualizarContato(BigDecimal codProspect, DadosCliente dadosCliente, Timestamp dataDaAlteracao) throws Exception {
		SessionHandle hnd = null;
		
		try {
			hnd = JapeSession.open();
			JapeFactory.dao("ContatoProspect").prepareToUpdateByPK("codpap = " + codProspect + " AND CODCONTATO = 1")
			.set("NOMECONTATO", dadosCliente.getNome())
			.set("TELEFONE", dadosCliente.getTelefone()).set("EMAIL", dadosCliente.getMailCliente())
			.set("CELULAR", dadosCliente.getTelefone2())
			.update();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Erro ao atualizar Prospect: "+e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

}
