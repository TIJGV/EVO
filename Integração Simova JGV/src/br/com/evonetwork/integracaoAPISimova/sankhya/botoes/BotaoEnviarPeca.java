package br.com.evonetwork.integracaoAPISimova.sankhya.botoes;

import java.math.BigDecimal;

import br.com.evonetwork.integracaoAPISimova.Controller.Controller;
import br.com.evonetwork.integracaoAPISimova.Controller.ControllerPeca;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.DynamicEntityNames;

public class BotaoEnviarPeca implements AcaoRotinaJava{

	@Override
	public void doAction(ContextoAcao ca) throws Exception {
		// botão na tabela TGFPRO
		System.out.println("***EVO - ENVIANDO PEÇA PARA SIMOVA - INICIO***");
		Registro[] linhas = ca.getLinhas();
		for (Registro linha : linhas) {
			try {
				DynamicVO pecaVO = getPecaVO((BigDecimal) linha.getCampo("CODPROD"));
				if("R".equals(pecaVO.asString("USOPROD"))) {
					if(Controller.verificaFlagDeEnvioParaPeca(pecaVO.asBigDecimal("CODGRUPOPROD")))
							ControllerPeca.enviarPecaPorDynamicVO(pecaVO);
					else
						ca.mostraErro("Peça "+(BigDecimal) linha.getCampo("CODPROD")+" está com a flag de envio desativada.");
				} else {
					ca.mostraErro("Peça "+(BigDecimal) linha.getCampo("CODPROD")+" não possui uso de 'Revenda'.");
				}
			} catch(Exception e) {
				e.printStackTrace();
				ca.mostraErro(e.getMessage());
			}
		}
		ca.setMensagemRetorno("Integração concluída!");
		System.out.println("***EVO - ENVIANDO PEÇA PARA SIMOVA - FIM***");
	}

	private DynamicVO getPecaVO(BigDecimal codProd) throws Exception {
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper produtoDAO = JapeFactory.dao(DynamicEntityNames.PRODUTO);
			DynamicVO produtoVO = produtoDAO.findOne("CODPROD = "+codProd);
			return produtoVO;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}
}
