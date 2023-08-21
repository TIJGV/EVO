package br.com.evonetwork.integracaoAPISimova.sankhya.botoes;

import java.math.BigDecimal;
import java.util.ArrayList;

import br.com.evonetwork.integracaoAPISimova.Controller.Controller;
import br.com.evonetwork.integracaoAPISimova.Controller.ControllerClientes;
import br.com.evonetwork.integracaoAPISimova.Controller.ControllerFuncionario;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.DynamicEntityNames;

public class BotaoEnviarClienteFuncionario implements AcaoRotinaJava{

	@Override
	public void doAction(ContextoAcao ca) throws Exception {
		// botão na tabela TGFPAR
		System.out.println("***EVO - ENVIANDO CLIENTE/FUNCIONARIO PARA SIMOVA - INICIO***");
		Registro[] linhas = ca.getLinhas();
		for (Registro linha : linhas) {
			try {
				DynamicVO parceiroVO = getParceiroVO((BigDecimal) linha.getCampo("CODPARC"));
				if("S".equals(parceiroVO.asString("CLIENTE"))) {
					String filial = Controller.getFilial(parceiroVO.asBigDecimal("AD_FILIAL"));
					if("".equals(filial)) {
						ca.mostraErro("Empresa/Filial não encontrada para o parceiro "+parceiroVO.asBigDecimal("CODPARC")+", não é possível integrar.");
					}
					ControllerClientes.enviarClientePorDynamicVO(parceiroVO);
				} else if("S".equals(parceiroVO.asString("AD_FUNCIONARIO"))) {
					ArrayList<BigDecimal> filiaisFuncionario = Controller.getFiliaisFuncionario(parceiroVO.asBigDecimal("CODPARC"));
					if(filiaisFuncionario.isEmpty()){
						ca.mostraErro("Empresa/Filial não encontrada para o parceiro "+parceiroVO.asBigDecimal("CODPARC")+", não é possível integrar.");
					}
					ControllerFuncionario.enviarFuncionarioPorDynamicVO(parceiroVO);
				} else {
					ca.mostraErro("Parceiro "+parceiroVO.asBigDecimal("CODPARC")+" não é cliente nem funcionário.");
				}
			} catch(Exception e) {
				e.printStackTrace();
				ca.mostraErro(e.getMessage());
			}
		}
		ca.setMensagemRetorno("Integração concluída!");
		System.out.println("***EVO - ENVIANDO CLIENTE/FUNCIONARIO PARA SIMOVA - FIM***");
	}

	private DynamicVO getParceiroVO(BigDecimal codParc) throws Exception {
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper parceiroDAO = JapeFactory.dao(DynamicEntityNames.PARCEIRO);
			DynamicVO parceiroVO = parceiroDAO.findOne("CODPARC = "+codParc);
			return parceiroVO;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

}
