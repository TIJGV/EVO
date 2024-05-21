package br.com.evonetwork.tgfite;

import java.math.BigDecimal;

import br.com.evonetwork.tgfcab.AtualizaCab;
import br.com.evonetwork.util.Utils;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class PreencherIte {
	ColetaCamposParaTgfite ite = new ColetaCamposParaTgfite();

	@SuppressWarnings("unused")
	public void criarRegistroTgfite(BigDecimal numContrato, BigDecimal nuNota, BigDecimal vlrTot, int qtMeses, int m)
			throws Exception {
		ite.getDadosParaTgfite(numContrato);
		JapeSession.SessionHandle hnd = null;
		try {
			System.out.println("DADOS DA TGFITE TESTE 1");
			System.out.println("VLRUNIT" + Utils.calculaValor(vlrTot, qtMeses, m+1));
			System.out.println("VLRTOT" + Utils.calculaValor(vlrTot, qtMeses, m+1));
			System.out.println("NUMCONTRATO" + numContrato);
			System.out.println("CODPROD" + ite.getIte().getCodprod());
			System.out.println("NUNOTA" + nuNota);
			System.out.println("CODVOL" + ite.getIte().getCodvol());
			System.out.println("QTDNEG" + ite.getIte().getQtdneg());
			
			hnd = JapeSession.open();
			JapeWrapper iteDAO = JapeFactory.dao("ItemNota");
			DynamicVO save = iteDAO.create()
					.set("VLRUNIT", Utils.calculaValor(vlrTot, qtMeses, m + 1))
					.set("VLRTOT", Utils.calculaValor(vlrTot, qtMeses, m + 1))
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
