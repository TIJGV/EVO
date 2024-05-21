package br.com.evonetwork.tgfite;

import java.math.BigDecimal;
import java.util.ArrayList;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class Tgfite {
	@SuppressWarnings("unused")
	public static void criarIte(BigDecimal numos, BigDecimal nunota, DynamicVO pro) throws Exception {
		System.out.println("criarIte >>");

		ArrayList<DadosTgfite> item = GetDadosTgfite.itensDaNota(numos);

		for (int i = 0; i < item.size(); i++) {
			String nomeDaInstancia = "ItemNota";

			EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
			EntityVO entityVO = dwfFacade.getDefaultValueObjectInstance(nomeDaInstancia);
			DynamicVO ite = (DynamicVO) entityVO;

			ite.setProperty("NUNOTA", nunota);
			ite.setProperty("VLRUNIT", item.get(i).getVlrunit());
			ite.setProperty("VLRTOT", item.get(i).getVlrtot());
			ite.setProperty("CODPROD", item.get(i).getCodprod());
			ite.setProperty("CODVOL", item.get(i).getCodvol());
			ite.setProperty("QTDNEG", item.get(i).getQtdneg());
			ite.setProperty("VLRTOT", item.get(i).getVlrtot());
			ite.setProperty("ATUALESTOQUE", new BigDecimal(0));
			ite.setProperty("ATUALESTTERC", "N");
			ite.setProperty("AD_SEQITESIMDESC", item.get(i).getSequencia());
			ite.setProperty("VLRDESC", item.get(i).getDescontoreais());
			ite.setProperty("PERCDESC", item.get(i).getDescontopctgm());

			
			PersistentLocalEntity createEntity = dwfFacade.createEntity(nomeDaInstancia, entityVO);

			DynamicVO save = (DynamicVO) createEntity.getValueObject();
			
			System.out.println("criarIte <<");
		}

	}

	public static void removerIte(BigDecimal nunota) throws Exception {
		SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper configDao = JapeFactory.dao("ItemNota");
			configDao.deleteByCriteria(" NUNOTA = " + nunota);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

	public static void criarIte2(BigDecimal numos, BigDecimal nunota) throws Exception {
		System.out.println("criarIte >>");

		ArrayList<DadosTgfite> item = GetDadosTgfite.itensDaNota(numos);

		for (int i = 0; i < item.size(); i++) {
			JapeSession.SessionHandle hnd = null;
			try {
				hnd = JapeSession.open();
				hnd.setCanTimeout(false);
				hnd.setPriorityLevel(JapeSession.LOW_PRIORITY);// em casos de deadlock, esta sess o cai
				JapeWrapper ite = JapeFactory.dao("ItemNota");
				@SuppressWarnings("unused")
				DynamicVO save = ite.create().set("NUNOTA", nunota).set("VLRUNIT", item.get(i).getVlrunit())
						.set("VLRTOT", item.get(i).getVlrtot()).set("CODPROD", item.get(i).getCodprod())
						.set("CODVOL", item.get(i).getCodvol()).set("QTDNEG", item.get(i).getQtdneg())
						.set("ATUALESTOQUE", new BigDecimal(0)).set("ATUALESTTERC", "N").save();
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception(e.getMessage());
			} finally {
				JapeSession.close(hnd);
				System.out.println("criarIte <<");
			}
		}

	}
}
