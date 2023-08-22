package br.com.evonetwork.clacadpapcam;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.sankhya.util.JdbcUtils;

import br.com.evonetwork.tcspap.Tcspap;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class GetNucampos {

	public static void getNomecampo(ArrayList<BigDecimal> codclassifcadpap, BigDecimal codpap) {
		System.out.println(">> getNomecampo");
		System.out.println("codclassifcadpap: " + codclassifcadpap.size());
		ArrayList<AtributosNucampo> nomecampos = new ArrayList<>();
		Boolean atualizado = false;
		System.out.println("atualizado: " + false);
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			for (int i = 0; i < codclassifcadpap.size(); i++) {
				if(!atualizado) {
					sql = new NativeSql(jdbc);
					sql.appendSql("SELECT (SELECT NOMECAMPO FROM TDDCAM WHERE NUCAMPO = C.NUCAMPO) AS NOMECAMPO, "
							+ "(SELECT NOMETAB FROM TDDCAM WHERE NUCAMPO = C.NUCAMPO) AS NOMETAB FROM AD_CLACADPAPCAM "
							+ "C WHERE CODCLASSIFCADPAP = " + codclassifcadpap.get(i));
					System.out.println(sql.toString());
					rset = sql.executeQuery();
					while (rset.next()) {
						AtributosNucampo nucampo = new AtributosNucampo();
						nucampo.setNomecampo(rset.getString("NOMECAMPO"));
						System.out.println("nomecampo: " + nucampo.getNomecampo());

						nucampo.setNometab(rset.getString("NOMETAB"));
						System.out.println("nometab: " + nucampo.getNometab());
						
						nomecampos.add(nucampo);
					}
					
					if(nomecampos.get(0) != null) {
						atualizado = Tcspap.tcspap(nomecampos, codpap, codclassifcadpap.get(i), atualizado);
						System.out.println("atualizado: " + atualizado);
					}
					nomecampos.clear();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
			System.out.println("<< getNomecampo");

		}

	}
}
