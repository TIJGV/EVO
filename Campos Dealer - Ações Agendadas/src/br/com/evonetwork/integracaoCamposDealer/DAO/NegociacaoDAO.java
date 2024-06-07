package br.com.evonetwork.integracaoCamposDealer.DAO;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.cuckoo.core.ScheduledActionContext;

import com.sankhya.util.TimeUtils;

import br.com.evonetwork.integracaoCamposDealer.Controller.EmpresaController;
import br.com.evonetwork.integracaoCamposDealer.Controller.NegocioController;
import br.com.evonetwork.integracaoCamposDealer.Controller.ProspectController;
import br.com.evonetwork.integracaoCamposDealer.Model.DadosNegocio;
import br.com.evonetwork.integracaoCamposDealer.Model.InfoNegoc;
import br.com.evonetwork.integracaoCamposDealer.Utils.Utils;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.util.JapeSessionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
//import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.SPBeanUtils;
import br.com.sankhya.ws.ServiceContext;
//import br.com.sankhya.ws.ServiceContext;

public class NegociacaoDAO {

	public static BigDecimal criaCabecalhoNegoc(DadosNegocio dadosNegocio, Timestamp dataDaAlteracao, String empresa)
			throws Exception {
		BigDecimal usuarioLogado = BigDecimal.ZERO;
		Timestamp dtatual = TimeUtils.getNow();
		BigDecimal codnat = NegocioController.buscaCodNat();
		BigDecimal codvend;
//		if (dadosNegocio.getLstNegocioXUsuarios().size() > 0) {
//			codvend = new BigDecimal(dadosNegocio.getLstNegocioXUsuarios().get(0).getCodUsuario());
//		} else {
//			codvend = NegocioController.buscarCodVend(usuarioLogado);
//		}

		BigDecimal codctt = ProspectController.buscarCttProspect(new BigDecimal(dadosNegocio.getCodigoCliente()));
		BigDecimal codemp = EmpresaController.buscarCodigoEmpresa(empresa);
		BigDecimal codpap = ProspectController.buscarCodPapPeloID(new BigDecimal(dadosNegocio.getIdCliente()));
		BigDecimal codvendpap = ProspectController.buscarCodVendProspect(codpap);

		if (codvendpap == null) {
			codvend = NegocioController.buscarCodVend(usuarioLogado);
		} else if (codvendpap.compareTo(BigDecimal.ZERO) == 0) {
			BigDecimal codvendNegoc = new BigDecimal(dadosNegocio.getLstNegocioXUsuarios().get(0).getCodUsuario());
			codvend = codvendNegoc;

			SessionHandle hndl = null;

			try {
				hndl = JapeSession.open();
				JapeFactory.dao(DynamicEntityNames.PARCEIRO_PROSPECT).prepareToUpdateByPK(codpap)
						.set("CODVEND", codvend).update();
			} catch (Exception err) {
				err.printStackTrace();
				throw new Exception("Erro ao atualizar prospect: " + err.getMessage());
			}

			codvend = codvendNegoc;
		} else {
			codvend = codvendpap;
		}

		BigDecimal codcencus = NegocioController.buscaCenCus(codvend);

		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			hnd.setCanTimeout(false);
			hnd.setPriorityLevel(JapeSession.LOW_PRIORITY);// em casos de deadlock, esta sess o cai
			JapeWrapper empresaDAO = JapeFactory.dao(DynamicEntityNames.ORDEM_SERVICO);
			DynamicVO save = empresaDAO.create().set("CODPAP", codpap).set("AD_DHALTERACAOCD", dataDaAlteracao)
					.set("CODUSURESP", usuarioLogado).set("DHCHAMADA", dtatual).set("CODVEND", codvend)
					.set("CODCONTATOPAP", codctt).set("CODATEND", usuarioLogado).set("SITUACAO", "P").set("TIPO", "P")
					.set("CODCENCUS", codcencus).set("AD_CODNAT", codnat)
					.set("AD_IDCAMPOSDEALER", dadosNegocio.getIdNegocio()).set("AD_CODEMP", codemp).save();

			return save.asBigDecimal("NUMOS");
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Erro ao criar Negociação: " + e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

	public static void atualizaCabecalhoNegoc(BigDecimal numos, DadosNegocio dadosNegocio, Timestamp dataDaAlteracao)
			throws Exception {
		BigDecimal usuarioLogado = BigDecimal.ZERO;

		// BigDecimal codvend = NegocioController.buscarCodVend(usuarioLogado);
		BigDecimal codvend;

		BigDecimal codPap = ProspectController.buscarCodPapPeloID(new BigDecimal(dadosNegocio.getIdCliente()));
		if (codPap != null) {
			codvend = ProspectController.buscarCodVendProspect(codPap);
		} else {
			codvend = NegocioController.buscarCodVend(usuarioLogado);
		}
		BigDecimal codctt = ProspectController.buscarCttProspect(new BigDecimal(dadosNegocio.getCodigoCliente()));
		BigDecimal codpap = ProspectController.buscarCodPapPeloID(new BigDecimal(dadosNegocio.getIdCliente()));
		BigDecimal codcencus = NegocioController.buscaCenCus(codvend);

		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeFactory.dao(DynamicEntityNames.ORDEM_SERVICO).prepareToUpdateByPK(numos).set("CODPAP", codpap)
					.set("AD_DHALTERACAOCD", dataDaAlteracao).set("CODUSURESP", usuarioLogado).set("CODVEND", codvend)
					.set("CODCONTATOPAP", codctt).set("CODATEND", new BigDecimal(215)).set("CODCENCUS", codcencus)
					.set("AD_IDCAMPOSDEALER", dadosNegocio.getIdNegocio()).update();

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Erro ao criar Negociação: " + e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

	public static void criaItensNegoc(DadosNegocio dadosNegocio, Timestamp dataDaAlteracao, BigDecimal numos,
			ScheduledActionContext schctx) throws Exception {
		BigDecimal usuarioLogado = BigDecimal.ZERO;

		ServiceContext sctx = new ServiceContext(null);
		sctx.setAutentication(AuthenticationInfo.getCurrent());
		sctx.makeCurrent();
		try {
			SPBeanUtils.setupContext(sctx);
		} catch (Exception e) {
			e.printStackTrace();
			schctx.info("Error: Não foi Possivel Executar a Chamada SPBeanUtils.setupContext \n" + e.getMessage());
		}

		JapeSessionContext.putProperty("usuario_logado", usuarioLogado);
		Timestamp dtatual = TimeUtils.getNow();
		Timestamp dtfech = TimeUtils.dataAddDay(dtatual, 30);

		InfoNegoc negoc = NegocioController.buscarInfoNegoc(dadosNegocio.getCodEtapaNegocio());

		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			hnd.setCanTimeout(false);
			hnd.setPriorityLevel(JapeSession.LOW_PRIORITY);// em casos de deadlock, esta sess o cai
			JapeWrapper empresaDAO = JapeFactory.dao(DynamicEntityNames.ITEM_ORDEM_SERVICO);
			empresaDAO.create().set("CODEPV", BigDecimal.ONE).set("CODOCOROS", negoc.getCodocoros())
					.set("CODPROD", negoc.getCodprod()).set("CODSERV", negoc.getCodserv()).set("CODSIT", BigDecimal.ONE)
					.set("DHPREVISTA", dtatual).set("DTPREVFECHAMENTO", dtfech).set("NUMOS", numos)
					.set("TEMPPREVISTO", dtfech).set("VLRCOBRADO", new BigDecimal(dadosNegocio.getVlrNegocio()))
					.set("COBRAR", "S").set("CODUSU", negoc.getCodusu()).save();

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Erro ao criar Itens: " + e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

	public static void atualizaItensNegoc(DadosNegocio dadosNegocio, Timestamp dataDaAlteracao, BigDecimal numos,
			BigDecimal numitem) throws Exception {

		InfoNegoc negoc = NegocioController.buscarInfoNegoc(dadosNegocio.getCodEtapaNegocio());
		BigDecimal usuarioLogado = negoc.getCodusu();
		JapeSessionContext.putProperty("usuario_logado", usuarioLogado);
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper servicoExecutadoDAO = JapeFactory.dao(DynamicEntityNames.ITEM_ORDEM_SERVICO);
			DynamicVO servico = servicoExecutadoDAO.findOne("NUMOS = " + numos + " AND NUMITEM = " + numitem);

			servicoExecutadoDAO.prepareToUpdate(servico).set("CODOCOROS", negoc.getCodocoros())
					.set("CODPROD", negoc.getCodprod()).set("CODSERV", negoc.getCodserv()).set("CODSIT", BigDecimal.ONE)
					.set("NUMOS", numos).set("VLRCOBRADO", new BigDecimal(dadosNegocio.getVlrNegocio()))
					.set("COBRAR", "S").update();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Erro ao criar Itens: " + e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

}
