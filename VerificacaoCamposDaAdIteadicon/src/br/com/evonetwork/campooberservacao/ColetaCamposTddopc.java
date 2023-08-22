package br.com.evonetwork.campooberservacao;

import java.sql.ResultSet;
import java.util.ArrayList;

import com.sankhya.util.JdbcUtils;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class ColetaCamposTddopc {
	
	private String opcoes;
	ArrayList<String> listaOpcoes = new ArrayList<>();
	
	public void ValorAntigo(String nomeCampo, String observacao1) {

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

			sql = new NativeSql(jdbc);
	
			sql.appendSql("SELECT OPC.* FROM TDDCAM CAM "
					+ "JOIN TDDOPC OPC ON OPC.NUCAMPO = CAM.NUCAMPO "
					+ "WHERE CAM.NOMETAB = 'TCSCON' AND CAM.NOMECAMPO = '" + nomeCampo + "'");
			rset = sql.executeQuery();

			StringBuffer sb = new StringBuffer();
			sb.append(observacao1);
			while (rset.next()) {
				CamposTddopc camposDaTddopc = new CamposTddopc();
				camposDaTddopc.setValor(rset.getString("VALOR"));	
				camposDaTddopc.setOpcao(rset.getString("OPCAO"));
				listaOpcoes.add(camposDaTddopc.getValor());
				sb.append(camposDaTddopc.toString());
			}
			this.opcoes = sb.toString();
			System.out.println("sb.toString()\n" + this.opcoes);
		} catch (
		Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
	}

	public String getOpcoes() {
		return opcoes;
	}
}
