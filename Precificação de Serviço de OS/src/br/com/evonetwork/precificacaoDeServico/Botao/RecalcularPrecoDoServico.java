package br.com.evonetwork.precificacaoDeServico.Botao;

import java.math.BigDecimal;

import br.com.evonetwork.precificacaoDeServico.Controller.Controller;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class RecalcularPrecoDoServico implements AcaoRotinaJava {

	// Botão na AD_TCSITE (Serviços Executados)
	
	@Override
	public void doAction(ContextoAcao ca) throws Exception {
		System.out.println("***EVO - RECALCULAR PRECIFICAÇÃO DO SERVIÇO - INICIO***");
		StringBuilder str = new StringBuilder();
		Registro[] linhas = ca.getLinhas();
		for (Registro linha : linhas) {
			DynamicVO servicoExecutadoVO = getVO(linha);
			String retorno = Controller.prepararParaCalcularPrecificacao(servicoExecutadoVO);
			str.append(retorno);
		}
		ca.setMensagemRetorno(str.toString());
		System.out.println("***EVO - RECALCULAR PRECIFICAÇÃO DO SERVIÇO - FIM***");
	}

	private DynamicVO getVO(Registro linha) throws Exception {
		JapeSession.SessionHandle hnd = null;
		DynamicVO servicoExecutadoVO = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper empresaDAO = JapeFactory.dao("TCSITE");
			servicoExecutadoVO = empresaDAO.findOne(" NUMITEM = "+(BigDecimal) linha.getCampo("NUMITEM")+" AND NUMOS = "+(BigDecimal) linha.getCampo("NUMOS"));
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
		return servicoExecutadoVO;
	}

}
