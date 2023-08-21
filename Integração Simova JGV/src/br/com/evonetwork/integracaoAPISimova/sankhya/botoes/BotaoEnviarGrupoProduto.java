package br.com.evonetwork.integracaoAPISimova.sankhya.botoes;

import java.math.BigDecimal;

import br.com.evonetwork.integracaoAPISimova.Controller.ControllerGrupoServico;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.DynamicEntityNames;

public class BotaoEnviarGrupoProduto implements AcaoRotinaJava{

	@Override
	public void doAction(ContextoAcao ca) throws Exception {
		// botão na tabela TGFGRU
		System.out.println("***EVO - ENVIANDO GRUPO PRODUTO PARA SIMOVA - INICIO***");
		Registro[] linhas = ca.getLinhas();
		for (Registro linha : linhas) {
			try {
				DynamicVO grupoProdutoVO = getGrupoProdutoVO((BigDecimal) linha.getCampo("CODGRUPOPROD"));
				if("S".equals(grupoProdutoVO.asString("AD_ENVIASIMOVA"))) {
					ControllerGrupoServico.enviarGrupoServicoPorDynamicVO(grupoProdutoVO);
				} else {
					ca.mostraErro("Grupo "+(BigDecimal) linha.getCampo("CODGRUPOPROD")+" não possui flag de envio para Simova ativa.");
				}
			} catch(Exception e) {
				e.printStackTrace();
				ca.mostraErro(e.getMessage());
			}
		}
		ca.setMensagemRetorno("Integração concluída!");
		System.out.println("***EVO - ENVIANDO GRUPO PRODUTO PARA SIMOVA - FIM***");
	}

	private DynamicVO getGrupoProdutoVO(BigDecimal codGrupoProd) throws Exception {
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper grupoProdutoDAO = JapeFactory.dao(DynamicEntityNames.GRUPO_PRODUTO);
			DynamicVO grupoProdutoVO = grupoProdutoDAO.findOne("CODGRUPOPROD = "+codGrupoProd);
			return grupoProdutoVO;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

}
