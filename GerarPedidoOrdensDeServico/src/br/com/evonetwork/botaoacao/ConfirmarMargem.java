package br.com.evonetwork.botaoacao;

import java.math.BigDecimal;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.comercial.BarramentoRegra;
import br.com.sankhya.modelcore.comercial.CentralFaturamento;
import br.com.sankhya.modelcore.comercial.ConfirmacaoNotaHelper;

public class ConfirmarMargem implements AcaoRotinaJava {

	@Override
	public void doAction(ContextoAcao ca) throws Exception {
		try {
			Registro linha = ca.getLinhas()[0];

			if (ca.getLinhas().length > 1) {
				ca.setMensagemRetorno("");
			}

			BigDecimal nunota = (BigDecimal) linha.getCampo("AD_NUNOTASIMDESC");
			
			if(nunota != null) {
				BarramentoRegra barramentoConfirmacao = BarramentoRegra.build(CentralFaturamento.class,
						"regrasConfirmacaoSilenciosa.xml", AuthenticationInfo.getCurrent());
				barramentoConfirmacao.setValidarSilencioso(true);
				ConfirmacaoNotaHelper.confirmarNota(nunota, barramentoConfirmacao);
				ca.setMensagemRetorno("Pedido " + nunota + " confirmado.");
			}


		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

}
