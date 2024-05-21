package br.com.evonetwork.integracaoCamposDealer.DAO;

import java.math.BigDecimal;
import java.sql.Timestamp;

import br.com.evonetwork.integracaoCamposDealer.Model.DadosCliente;
import br.com.evonetwork.integracaoCamposDealer.Utils.Utils;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.DynamicEntityNames;

public class ProspectDAO {

	public static void atualizarProspect(BigDecimal codProspect, DadosCliente dadosCliente, Timestamp dataDaAlteracao) throws Exception {
		SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeFactory.dao(DynamicEntityNames.PARCEIRO_PROSPECT).prepareToUpdateByPK(codProspect)
				.set("CGC_CPF", dadosCliente.getCNPJ_CPF())
				.set("NOMEPAP", dadosCliente.getNome())
				.set("ENDERECO", dadosCliente.getEndereco())
				.set("CEP", dadosCliente.getCEP())
				.set("TELEFONE", dadosCliente.getTelefone())
				.set("EMAIL", dadosCliente.getMailCliente())
				.set("NOMECID", dadosCliente.getCidade())
				.set("UF", Utils.buscarCodigoUF(dadosCliente.getEstado()))
				.set("AD_DHALTERACAOCD", dataDaAlteracao)
				.update();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Erro ao atualizar Prospect: "+e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

	public static void criarProspect(DadosCliente dadosCliente, Timestamp dataDaAlteracao) throws Exception {
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			hnd.setCanTimeout(false);
            hnd.setPriorityLevel(JapeSession.LOW_PRIORITY);//em casos de deadlock, esta sess o cai
			JapeWrapper empresaDAO = JapeFactory.dao(DynamicEntityNames.PARCEIRO_PROSPECT);
			DynamicVO save = empresaDAO.create()
				.set("CGC_CPF", dadosCliente.getCNPJ_CPF())
				.set("NOMEPAP", dadosCliente.getNome())
				.set("ENDERECO", dadosCliente.getEndereco())
				.set("CEP", dadosCliente.getCEP())
				.set("TELEFONE", dadosCliente.getTelefone())
				.set("EMAIL", dadosCliente.getMailCliente())
				.set("NOMECID", dadosCliente.getCidade())
				.set("CODUF", Utils.buscarCodigoUF(dadosCliente.getEstado()))
				.set("AD_DHALTERACAOCD", dataDaAlteracao)
				.save();
			System.out.println("Prospect criado: "+save.asBigDecimal("CODPAP"));
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Erro ao criar Prospect: "+e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

}
