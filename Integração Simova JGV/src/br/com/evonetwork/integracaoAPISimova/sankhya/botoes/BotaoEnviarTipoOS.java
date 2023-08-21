package br.com.evonetwork.integracaoAPISimova.sankhya.botoes;

import java.math.BigDecimal;

import br.com.evonetwork.integracaoAPISimova.Controller.ControllerTipoOS;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class BotaoEnviarTipoOS implements AcaoRotinaJava {

	@Override
	public void doAction(ContextoAcao ca) throws Exception {
		// botão na tabela AD_TIPOORDEMSERVICO
		System.out.println("***EVO - ENVIANDO TIPO OS PARA SIMOVA - INICIO***");
		Registro[] linhas = ca.getLinhas();
		for (Registro linha : linhas) {
			try {
				DynamicVO tipoOSVO = getTipoOSVO((BigDecimal) linha.getCampo("CODTIPODEOS"));
				ControllerTipoOS.enviarTipoOSPorDynamicVO(tipoOSVO);
			} catch(Exception e) {
				e.printStackTrace();
				ca.mostraErro(e.getMessage());
			}
		}
		ca.setMensagemRetorno("Integração concluída!");
		System.out.println("***EVO - ENVIANDO TIPO OS PARA SIMOVA - FIM***");
	}

	private DynamicVO getTipoOSVO(BigDecimal codTipoOS) throws Exception {
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper tipoOSDAO = JapeFactory.dao("AD_TIPOORDEMSERVICO");
			DynamicVO tipoOSVO = tipoOSDAO.findOne("CODTIPODEOS = "+codTipoOS);
			return tipoOSVO;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

}
