package br.com.evonetwork.verificarpedidospendentes;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import com.sankhya.util.JdbcUtils;

import br.com.evonetwork.deletarpedidos.DeletarPedidos;
import br.com.evonetwork.deletarpedidosextras.DeletarPedidosExtras;
import br.com.evonetwork.deletarpedidosextras.VerificaCab;
import br.com.evonetwork.deletarpedidosextras.VerificaVar;
import br.com.evonetwork.utils.Utils;
import br.com.evonetwork.verificatipocontrato.VerificaCodtipcontrato;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class RastreiaPedidos {
	PreencherCab cab = new PreencherCab();
	DeletarPedidos d = new DeletarPedidos();
	
	ArrayList<BigDecimal> nunotaLista = new ArrayList<>();
	ArrayList<BigDecimal> nufinLista = new ArrayList<>();
	ArrayList<Integer> dtvencLista = new ArrayList<>();
	VerificaVar varExtra = new VerificaVar();
	VerificaCab cabExtra = new VerificaCab();
	
	public void PedidosExtras(BigDecimal numContrato) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String dt = df.format(new Timestamp(c.getTimeInMillis()));

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);
			sql.appendSql("SELECT CAB.NUNOTA,CAB.CODEMP, FIN.NUFIN, FIN.DTNEG "
					+ "FROM TGFCAB CAB JOIN TGFITE ITE ON CAB.NUNOTA = ITE.NUNOTA "
					+ "JOIN TGFFIN FIN ON CAB.NUNOTA = FIN.NUNOTA WHERE CAB.NUMCONTRATO = " + numContrato
					+ " AND CAB.DTFATUR IS NULL");
			rset = sql.executeQuery();
			System.out.println();
			while (rset.next()) {
				BigDecimal nufin = rset.getBigDecimal("NUFIN");
				BigDecimal nunota = rset.getBigDecimal("NUNOTA");
				String dtvenc = rset.getString("DTNEG"); 
				
				nunotaLista.add(nunota);
				nufinLista.add(nufin);
				
				c.setTime(Utils.stringToTimestamp(dtvenc));
				dtvencLista.add(c.get(Calendar.MONTH));
				
			}
			System.out.println("Tamanho da lista: " + nunotaLista.size());
			if(nunotaLista.size() != 0) {
				Collections.sort(dtvencLista);
				System.out.println("criando registro cab");
				cab.criarRegistroTgfcab(numContrato, dtvencLista.get(0), VerificaCodtipcontrato.getTipContrato(numContrato));
				
				for (int i = 0; i < nunotaLista.size(); i++) {
					d.removerCabs(nunotaLista.get(i)); //aqui vem o metodo para deletar os nunota
				}		
				
				////////////////////deletar pedidos extras
				System.out.println("deletando pedidos 753");
				varExtra.VarExtras(numContrato);
				cabExtra.CabExtras(numContrato);
				DeletarPedidosExtras.DeletPedidoExtra(varExtra.getVarExtra(), cabExtra.getCabExtra());
			
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
	}
}
