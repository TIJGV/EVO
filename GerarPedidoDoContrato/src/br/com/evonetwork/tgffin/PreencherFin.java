package br.com.evonetwork.tgffin;

import java.math.BigDecimal;

import VerificaTipoContrato.CodempCodcencus;
import br.com.evonetwork.util.Utils;
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
			System.out.println("CODCENCUS" + fin.getFin().getCodcencus());
			System.out.println("CODEMP" + fin.getFin().getCodemp());
			System.out.println("CODMOEDA" + fin.getFin().getCodmoeda());
			System.out.println("CODNAT" + fin.getFin().getCodnat());
			System.out.println("CODPARC" + fin.getFin().getCodparc());
			System.out.println("CODTIPTIT" + fin.getFin().getCodtiptit());
			System.out.println("NUMCONTRATO" + fin.getFin().getNumcontrato());
			System.out.println("NUMNOTA" + fin.getFin().getNumnota());
			System.out.println("NUNOTA" + fin.getFin().getNunota());
			System.out.println("RECDESP" + fin.getFin().getRecdesp());
			System.out.println("VLRDESDOB" + fin.getFin().getVlrdesdob());
			System.out.println("DTNEG" + fin.getFin().getDtneg());
			System.out.println("DTVENC" + Utils.MudarDiaPag(Utils.somaMeses(fin.getFin().getDtvenc(), m+1), fin.getFin().getDiapag()));
			System.out.println("ORIGEM" + fin.getFin().getOrigem());
			System.out.println("PROVISAO" + fin.getFin().getProvisao());
			
			hnd = JapeSession.open();
			JapeWrapper finDAO = JapeFactory.dao("Financeiro");
				DynamicVO save = finDAO.create()
						.set("CODCENCUS", fin.getFin().getCodcencus())
						//.set("CODCENCUS", CodempCodcencus.getCencus(fin.getFin().getCodemp()))
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
