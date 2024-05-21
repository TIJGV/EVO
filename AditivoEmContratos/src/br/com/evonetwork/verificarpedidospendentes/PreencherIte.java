package br.com.evonetwork.verificarpedidospendentes;

import java.math.BigDecimal;

import br.com.evonetwork.utils.Utils;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class PreencherIte {
	ColetaCamposParaTgfite ite = new ColetaCamposParaTgfite();
	
	@SuppressWarnings("unused")
	public void criarRegistroTgfite(BigDecimal numContrato, BigDecimal nuNota, BigDecimal vlrTot,int qtMeses, int m) throws Exception {
		ite.getDadosParaTgfite(numContrato);
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper iteDAO = JapeFactory.dao("ItemNota");
				DynamicVO save = iteDAO.create()
						.set("VLRUNIT", Utils.calculaValor(vlrTot, qtMeses, m+1))
						.set("VLRTOT", Utils.calculaValor(vlrTot, qtMeses, m+1))
						.set("NUMCONTRATO", numContrato)
						.set("CODPROD", ite.getIte().getCodprod())
						.set("NUNOTA", nuNota)
						.set("CODVOL", ite.getIte().getCodvol())
						.set("QTDNEG", ite.getIte().getQtdneg())
						.save();
				BigDecimal vlrnota = save.asBigDecimal("VLRTOT");
				AtualizaCab c = new AtualizaCab();
				c.AtualizaCabVlrtot(nuNota, vlrnota);
				
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}
}
