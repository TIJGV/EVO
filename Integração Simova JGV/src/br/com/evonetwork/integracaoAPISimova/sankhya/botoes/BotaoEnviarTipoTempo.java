package br.com.evonetwork.integracaoAPISimova.sankhya.botoes;

import java.math.BigDecimal;

import br.com.evonetwork.integracaoAPISimova.Controller.ControllerTipoTempo;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class BotaoEnviarTipoTempo implements AcaoRotinaJava {

	@Override
	public void doAction(ContextoAcao ca) throws Exception {
		// botão na tabela AD_TIPOTEMPO
		System.out.println("***EVO - ENVIANDO TIPO TEMPO PARA SIMOVA - INICIO***");
		Registro[] linhas = ca.getLinhas();
		for (Registro linha : linhas) {
			try {
				DynamicVO tipoTempoVO = getTipoTempoVO((BigDecimal) linha.getCampo("NROUNICO"));
				ControllerTipoTempo.enviarTipoTempoPorDynamicVO(tipoTempoVO);
			} catch(Exception e) {
				e.printStackTrace();
				ca.mostraErro(e.getMessage());
			}
		}
		ca.setMensagemRetorno("Integração concluída!");
		System.out.println("***EVO - ENVIANDO TIPO TEMPO PARA SIMOVA - FIM***");
	}

	private DynamicVO getTipoTempoVO(BigDecimal codTipoTempo) throws Exception {
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper tipoTempoDAO = JapeFactory.dao("AD_TIPOTEMPO");
			DynamicVO tipoTempoVO = tipoTempoDAO.findOne("NROUNICO = "+codTipoTempo);
			return tipoTempoVO;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

}
