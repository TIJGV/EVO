package br.com.evonetwork.verificarpedidospendentes;

import java.math.BigDecimal;

import br.com.evonetwork.utils.Utils;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class PreencherFin {
	ColetaCamposParaTgffin fin = new ColetaCamposParaTgffin();
	
	@SuppressWarnings("unused")
	public void criarRegistroTgffin(BigDecimal nuNota, int m) throws Exception {
		fin.getDadosParaTgffin(nuNota);
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper finDAO = JapeFactory.dao("Financeiro");
				DynamicVO save = finDAO.create()
						.set("CODCENCUS", fin.getFin().getCodcencus())
						.set("CODEMP", fin.getFin().getCodemp())
						.set("CODMOEDA", fin.getFin().getCodmoeda())
						.set("CODNAT", fin.getFin().getCodnat())
						.set("CODPARC", fin.getFin().getCodparc())
						.set("CODTIPTIT", fin.getFin().getCodtiptit())
						.set("NUMCONTRATO", fin.getFin().getNumcontrato())
						.set("NUMNOTA", fin.getFin().getNumnota())
						.set("NUNOTA", fin.getFin().getNunota())
						.set("RECDESP", fin.getFin().getRecdesp())
						.set("VLRDESDOB", fin.getFin().getVlrdesdob())
						.set("DTNEG", fin.getFin().getDtneg())
						.set("DTVENC", Utils.MudarDiaPag(Utils.somaMeses(fin.getFin().getDtvenc(), m+1), fin.getFin().getDiapag()))
						.set("ORIGEM", fin.getFin().getOrigem())
						.set("PROVISAO", fin.getFin().getProvisao())
						.save();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}
}
