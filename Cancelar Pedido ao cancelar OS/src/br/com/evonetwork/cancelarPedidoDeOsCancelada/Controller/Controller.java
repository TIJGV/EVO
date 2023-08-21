package br.com.evonetwork.cancelarPedidoDeOsCancelada.Controller;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.jdom.Element;

import com.sankhya.util.XMLUtils;

import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.event.ModifingFields;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.comercial.CentralCabecalhoNota;
import br.com.sankhya.modelcore.comercial.CentralCabecalhoNota.CancelamentoNotaCtx;

public class Controller {

	public static void verificarSeOrdemServicoEstaCancelada(PersistenceEvent event) throws Exception {
		try {
			ModifingFields md = event.getModifingFields();
			if (md.isModifing("STATUS")) {
				DynamicVO ordemServicoVO = (DynamicVO) event.getVo();
				String status = ordemServicoVO.asString("STATUS");
				if("C".equals(status)) {
					BigDecimal numOs = ordemServicoVO.asBigDecimal("NUMOS");
					BigDecimal nuNotaPedidoOrigem = ordemServicoVO.asBigDecimal("NUNOTA");
					if(cancelarPedidoOrigem(nuNotaPedidoOrigem, numOs))
						System.out.println("A Nota "+nuNotaPedidoOrigem+" foi cancelada!");
					else
						System.out.println("Erro ao cancelar a nota "+nuNotaPedidoOrigem);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	private static boolean cancelarPedidoOrigem(BigDecimal nunota, BigDecimal numOs) throws Exception {
		String justificativa = "A Ordem de Serviço ("+numOs+") vinculada com esta nota foi cancelada.";

		JapeSession.putProperty("br.com.sankhya.mgecom.centralnotas.NotaSendoCancelada", "S");
		JapeSession.putProperty("br.com.sankhya.mgecom.centralnotas.validacaoSubstituicaoNfse", false);

		CentralCabecalhoNota centralCabNota = new CentralCabecalhoNota();

		CancelamentoNotaCtx contextoCancelamentoNota = new CentralCabecalhoNota.CancelamentoNotaCtx(new BigDecimal("0"), justificativa);

		ArrayList<BigDecimal> notas = new ArrayList<BigDecimal>();
		notas.add(nunota);

		Element resposta = centralCabNota.cancelarNotasBatch(notas, contextoCancelamentoNota);

		String qtdCancelada = XMLUtils.getAttributeAsString(resposta, "totalNotasCanceladas");

		if ("0".equals(qtdCancelada)) {
			return false;
		} else {
			return true;
		}
	}
	
}
