package br.com.evonetwork.integracaoAPISimova.sankhya.botoes;

import java.math.BigDecimal;

import br.com.evonetwork.integracaoAPISimova.Controller.ControllerGrupoServico;
import br.com.evonetwork.integracaoAPISimova.Controller.ControllerServico;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.DynamicEntityNames;

public class BotaoEnviarServico implements AcaoRotinaJava{

	@Override
	public void doAction(ContextoAcao ca) throws Exception {
		// botão na tabela TGFPRO
		System.out.println("***EVO - ENVIANDO SERVIÇO PARA SIMOVA - INICIO***");
		Registro[] linhas = ca.getLinhas();
		for (Registro linha : linhas) {
			try {
				DynamicVO servicoVO = getServicoVO((BigDecimal) linha.getCampo("CODPROD"));
				if("S".equals(servicoVO.asString("USOPROD"))) {
					BigDecimal codGrupo = (BigDecimal) servicoVO.getProperty("CODGRUPOPROD");
					DynamicVO grupoServicoVO = getGrupoServicoVO(codGrupo);
					ControllerGrupoServico.enviarGrupoServicoPorDynamicVO(grupoServicoVO);
					ControllerServico.enviarServicoPorDynamicVO(servicoVO);
				} else {
					ca.mostraErro("Serviço "+(BigDecimal) linha.getCampo("CODPROD")+" não possui uso de 'Serviço'.");
				}
			} catch(Exception e) {
				e.printStackTrace();
				ca.mostraErro(e.getMessage());
			}
		}
		ca.setMensagemRetorno("Integração concluída!");
		System.out.println("***EVO - ENVIANDO SERVIÇO PARA SIMOVA - FIM***");
	}

	private DynamicVO getGrupoServicoVO(BigDecimal codGrupo) throws Exception {
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper grupoServicoDAO = JapeFactory.dao(DynamicEntityNames.GRUPO_PRODUTO);
			DynamicVO grupoServicoVO = grupoServicoDAO.findOne("CODGRUPOPROD = "+codGrupo);
			return grupoServicoVO;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

	private DynamicVO getServicoVO(BigDecimal codProd) throws Exception {
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper servicoDAO = JapeFactory.dao(DynamicEntityNames.SERVICO);
			DynamicVO servicoVO = servicoDAO.findOne("CODPROD = "+codProd);
			return servicoVO;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}
}
