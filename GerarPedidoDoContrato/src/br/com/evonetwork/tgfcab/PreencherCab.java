package br.com.evonetwork.tgfcab;

import java.math.BigDecimal;

import VerificaTipoContrato.CodempCodcencus;
import VerificaTipoContrato.CodempParceiro;
import VerificaTipoContrato.VerificaTopIntercon;
import br.com.evonetwork.tgffin.PreencherFin;
import br.com.evonetwork.tgfite.PreencherIte;
import br.com.evonetwork.util.Utils;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class PreencherCab {
	ColetaCamposParaTgfcab cab = new ColetaCamposParaTgfcab();
	PreencherIte ite = new PreencherIte();
	PreencherFin fin = new PreencherFin();

	public void criarRegistroTgfcab(BigDecimal numContrato, BigDecimal codTipContrato) throws Exception {
		cab.getDadosParaTgfcab(numContrato);
		int qtMeses = cab.getListaCab().get(0).getQtMeses();
		JapeSession.SessionHandle hnd = null;

		try {
			hnd = JapeSession.open();
			JapeWrapper cabDAO = JapeFactory.dao("CabecalhoNota");
			if (codTipContrato.equals(new BigDecimal(3))) {
				for (int m = 0; m < qtMeses; m++) {
					for (int i = 0; i < cab.getListaCab().size(); i++) {
						for (int n = 0; n < 2; n++) {
							BigDecimal codemp;
							BigDecimal codtipoper;
							BigDecimal adCodoat;
							if (n == 0) {
								codemp = cab.getListaCab().get(i).getCodemp();
								codtipoper = cab.getListaCab().get(i).getCodtipoper();
								adCodoat = null;

							} else {
								codemp = CodempParceiro.getCodempParceiro(numContrato);
								codtipoper = VerificaTopIntercon.getTopintercon();
								adCodoat = new BigDecimal(5);
							}
							System.out.println("DADOS DA TGFCAB TESTE 1");
							System.out.println("CODCENCUS" + CodempCodcencus.getCencus(codemp));
							System.out.println("CODEMP" + codemp);
							System.out.println("CODNAT" + cab.getListaCab().get(i).getCodnat());
							System.out.println("CODPARC" + cab.getListaCab().get(i).getCodparc());
							System.out.println("CODTIPOPER" + codtipoper);
							System.out.println("CODTIPVENDA" + cab.getListaCab().get(i).getCodtipvenda());
							System.out.println("NUMNOTA" + cab.getListaCab().get(i).getNumnota());
							System.out.println("CODUSUCOMPRADOR" + cab.getListaCab().get(i).getCodusucomprador());
							System.out.println("DTNEG" + Utils.somaMeses(cab.getListaCab().get(i).getDtneg(), m));
							System.out.println("DTENTSAI" + Utils.somaMeses(cab.getListaCab().get(i).getDtentsai(), m));
							System.out.println("OBSERVACAO" + cab.getListaCab().get(i).getObservacao());
							System.out.println("SERIENOTA" + cab.getListaCab().get(i).getSerienota());
							System.out.println("NUMCONTRATO" + cab.getListaCab().get(i).getNumcontrato());
							System.out.println("AD_CODOAT" + adCodoat);
							
							DynamicVO save = cabDAO.create()
									.set("CODCENCUS", CodempCodcencus.getCencus(codemp))
									.set("CODEMP", codemp)
									.set("CODNAT", cab.getListaCab().get(i).getCodnat())
									.set("CODPARC", cab.getListaCab().get(i).getCodparc())
									.set("CODTIPOPER", codtipoper)
									.set("CODTIPVENDA", cab.getListaCab().get(i).getCodtipvenda())
									.set("NUMNOTA", cab.getListaCab().get(i).getNumnota())
									//.set("CODVEND", cab.getListaCab().get(i).getCodusucomprador())
									.set("DTNEG", Utils.somaMeses(cab.getListaCab().get(i).getDtneg(), m))
									.set("DTENTSAI", Utils.somaMeses(cab.getListaCab().get(i).getDtentsai(), m))
									.set("OBSERVACAO", cab.getListaCab().get(i).getObservacao())
									.set("SERIENOTA", cab.getListaCab().get(i).getSerienota())
									.set("NUMCONTRATO", cab.getListaCab().get(i).getNumcontrato())
									.set("AD_CODOAT", adCodoat)
									.save();
							cab.getListaCab().get(i).setNunota(save.asBigDecimal("NUNOTA"));
							ite.criarRegistroTgfite(numContrato, cab.getListaCab().get(i).getNunota(),
									cab.getListaCab().get(i).getVlrtot(), qtMeses, m);
							System.out.println("nunota criada: " + cab.getListaCab().get(i).getNunota());
							fin.criarRegistroTgffin(cab.getListaCab().get(i).getNunota(), m);
						}
					}
				}
			} else {
				for (int m = 0; m < qtMeses; m++) {
					for (int i = 0; i < cab.getListaCab().size(); i++) {
						DynamicVO save = cabDAO.create()
								.set("CODCENCUS", cab.getListaCab().get(i).getCodcencus())
								.set("CODEMP", cab.getListaCab().get(i).getCodemp())
								.set("CODNAT", cab.getListaCab().get(i).getCodnat())
								.set("CODPARC", cab.getListaCab().get(i).getCodparc())
								.set("CODTIPOPER", cab.getListaCab().get(i).getCodtipoper())
								.set("CODTIPVENDA", cab.getListaCab().get(i).getCodtipvenda())
								.set("NUMNOTA", cab.getListaCab().get(i).getNumnota())
								//.set("CODVEND", cab.getListaCab().get(i).getCodusucomprador())
								.set("DTNEG", Utils.somaMeses(cab.getListaCab().get(i).getDtneg(), m))
								.set("DTENTSAI", Utils.somaMeses(cab.getListaCab().get(i).getDtentsai(), m))
								.set("OBSERVACAO", cab.getListaCab().get(i).getObservacao())
								.set("SERIENOTA", cab.getListaCab().get(i).getSerienota())
								.set("NUMCONTRATO", cab.getListaCab().get(i).getNumcontrato()).save();
						cab.getListaCab().get(i).setNunota(save.asBigDecimal("NUNOTA"));
						ite.criarRegistroTgfite(numContrato, cab.getListaCab().get(i).getNunota(),
								cab.getListaCab().get(i).getVlrtot(), qtMeses, m);

						fin.criarRegistroTgffin(cab.getListaCab().get(i).getNunota(), m);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}
}
