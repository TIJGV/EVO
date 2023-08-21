package br.com.evonetwork.cadastrarPrecos.Controller;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sankhya.util.JdbcUtils;
import com.sankhya.util.TimeUtils;

import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.QueryExecutor;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class ControllerPrecos {

	public static void cadastrarPrecos(ContextoAcao ca, Registro linha) throws Exception {
		BigDecimal nroUnicoImportacao = (BigDecimal) linha.getCampo("NROUNICO");
		StringBuilder logImportacao = new StringBuilder();
		
		BigDecimal codTab = getTabelaDePrecos(nroUnicoImportacao);
		if(codTab == null)
			ca.mostraErro("Cód. da Tabela de preço não foi encontrado na configuração da importação!");
		
		//CUSMED, CUSGER, CUSSEMICM, CUSMEDICM
		BigDecimal cusVariavel = null;
		BigDecimal cusRep = null;
		BigDecimal cusMed = null;
		BigDecimal cusGer = null;
		BigDecimal cusSemIcm = null;
		BigDecimal cusMedIcm = null;
		BigDecimal vlrVenda = null;
		BigDecimal codEmp = null;
		
		int qtdPrecos = 0;
		
		BigDecimal codProImp = null;
		BigDecimal codPro = null;
		String importado = "";
		
		/* QUERY PARA COLETAR DADOS PRODUTOS */
		QueryExecutor query1 = ca.getQuery();
		query1.setParam("NROUNICO", nroUnicoImportacao);
		try {
//			query1.nativeSelect("SELECT IMPORTADO, NROUNICOPRO, CODPROIMP FROM AD_DADOSPRODUTO WHERE NROUNICO = {NROUNICO} AND NVL(PRECOIMPORTADO, 'N') = 'N' ORDER BY NROUNICOPRO");
			query1.nativeSelect("SELECT IMPORTADO, NROUNICOPRO, CODPROIMP FROM AD_DADOSPRODUTO WHERE NROUNICO = {NROUNICO} ORDER BY NROUNICOPRO");
			while(query1.next()) {
				codProImp = query1.getBigDecimal("NROUNICOPRO");
				codPro = query1.getBigDecimal("CODPROIMP");
				importado = query1.getString("IMPORTADO");
				
//				System.out.println("NROUNICOPRO: "+codProImp+" CODPROIMP: "+codPro+" IMPORTADO: "+importado);
				
				if(codPro == null && "S".equals(importado)) {
					ca.mostraErro("Importe os produtos primeiro!");
				}
				
				QueryExecutor query2 = ca.getQuery();
				try {
					query2.setParam("NROUNICOPRO", codProImp);
					query2.nativeSelect("SELECT VLRVENDA FROM AD_DADOSEXCECAO WHERE NROUNICOPRO = {NROUNICOPRO} AND NROUNICO = "+nroUnicoImportacao);
					while(query2.next()) {
						vlrVenda = query2.getBigDecimal("VLRVENDA");
						if(vlrVenda == null) {
							vlrVenda = BigDecimal.ZERO;
						}
					}
				} catch(Exception e) {
					e.printStackTrace();
					ca.mostraErro(e.getMessage());
				} finally {
					query2.close();
				}
				
				QueryExecutor query3 = ca.getQuery();
				try {
					query3.setParam("NROUNICOPRO", codProImp);
					query3.nativeSelect("SELECT CUSVARIAVEL, CUSREP, CUSMED, CUSGER, CUSSEMICM, CUSMEDICM, CODEMP FROM AD_DADOSCUSTO WHERE NROUNICOPRO = {NROUNICOPRO} AND NROUNICO = "+nroUnicoImportacao);
					while(query3.next()) {
						cusVariavel = query3.getBigDecimal("CUSVARIAVEL");
						if(cusVariavel == null) 
							cusVariavel = BigDecimal.ZERO;
						
						cusRep = query3.getBigDecimal("CUSREP");
						if(cusRep == null) 
							cusRep = BigDecimal.ZERO;
						
						cusMed = query3.getBigDecimal("CUSMED");
						if(cusMed == null) 
							cusMed = BigDecimal.ZERO;
						
						cusGer = query3.getBigDecimal("CUSGER");
						if(cusGer == null) 
							cusGer = BigDecimal.ZERO;
						
						cusSemIcm = query3.getBigDecimal("CUSSEMICM");
						if(cusSemIcm == null) 
							cusSemIcm = BigDecimal.ZERO;
						
						cusMedIcm = query3.getBigDecimal("CUSMEDICM");
						if(cusMedIcm == null) 
							cusMedIcm = BigDecimal.ZERO;
						
						codEmp =  query3.getBigDecimal("CODEMP");
						if(codEmp == null) 
							codEmp = BigDecimal.ONE;
					}
				} catch(Exception e) {
					e.printStackTrace();
					ca.mostraErro(e.getMessage());
				} finally {
					query3.close();
				}
				
//				System.out.println("Atualizando TGFCUS: "+cusVariavel+" "+cusRep+" "+codPro);
				cadastrarAtualizarPrecosTGFCUS(ca, cusVariavel, cusRep, codPro, cusMed, cusGer, cusSemIcm, cusMedIcm, codEmp, logImportacao);
				
//				System.out.println("CODTAB: "+codTab);
				BigDecimal nuTab = criarOuRetornaRegistroTGFTAB(codTab);
//				System.out.println("NUTAB: "+nuTab);
				
//				System.out.println("Atualizando TGFEXC: "+vlrVenda+" "+nuTab+" "+codPro);
				cadastrarAtualizarPrecosTGFEXC(ca, vlrVenda, codPro, nuTab, logImportacao);
				
//				System.out.println("Atualizando preço importado = 'S' para "+codProImp);
				atualizarPrecoImportado(nroUnicoImportacao, codProImp, ca);
				
				qtdPrecos++;
//				System.out.println("QTDPRECOS: "+qtdPrecos);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ca.mostraErro(e.getMessage());
		} finally {
			query1.close();
		}
		/* ------ FIM DA QUERY ------ */
		
		atualizarLogImportacao(nroUnicoImportacao, logImportacao);
		
		if(qtdPrecos == 0) {
			ca.setMensagemRetorno("Nenhum preço encontrado!");
		} else {
			ca.setMensagemRetorno(qtdPrecos+" preços cadastrados!");
		}
	}

	private static void atualizarLogImportacao(BigDecimal nroUnicoImportacao, StringBuilder logImportacao) throws Exception {
		SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeFactory.dao("AD_IMPPRO").prepareToUpdateByPK(nroUnicoImportacao)
				.set("LOGIMP", logImportacao.toString().toCharArray())
				.update();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

	private static void atualizarPrecoImportado(BigDecimal nroUnicoImportacao, BigDecimal codProImp, ContextoAcao ca) throws Exception {
		QueryExecutor queryUpdateImportacao = ca.getQuery();
		queryUpdateImportacao.setParam("NROUNICO", nroUnicoImportacao);
		queryUpdateImportacao.setParam("NROUNICOPRO", codProImp);
		try {
			queryUpdateImportacao.update("UPDATE AD_DADOSPRODUTO SET PRECOIMPORTADO = 'S' WHERE NROUNICO = {NROUNICO} AND NROUNICOPRO = {NROUNICOPRO}");
		} catch (Exception e) {
			e.printStackTrace();
			ca.mostraErro(e.getMessage());
		} finally {
			queryUpdateImportacao.close();
		}
	}

	private static BigDecimal criarOuRetornaRegistroTGFTAB(BigDecimal codTab) throws Exception {
		//Verificando se já existe esse registro
		BigDecimal nuTab = existeRegistro();
		if(nuTab != null)
			return nuTab;
		JapeSession.SessionHandle hnd = null;
		try {
			System.out.println("Não existe TGFTAB");
			hnd = JapeSession.open();
			JapeWrapper tgftabDAO = JapeFactory.dao("TabelaPreco");
			DynamicVO save = tgftabDAO.create()
				.set("CODTAB", codTab)
				.set("DTVIGOR", TimeUtils.getNow())
				.set("DTALTER", TimeUtils.getNow())
				.set("CODTABORIG", codTab)
				.save();
			nuTab = save.asBigDecimal("NUTAB");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JapeSession.close(hnd);
		}
		return nuTab;
	}

	private static BigDecimal existeRegistro() throws Exception {
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

			sql.appendSql("SELECT NUTAB FROM TGFTAB WHERE DTVIGOR >= '"+convertDate(TimeUtils.getNow().toString())+"' AND DTVIGOR < '"+convertDate(addDays(1, TimeUtils.getNow()).toString())+"'");
//			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();

			if (rset.next()) {
				return rset.getBigDecimal("NUTAB");
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
		return null;
	}
	
	private static Long dayToMiliseconds(int days){
	    Long result = Long.valueOf(days * 24 * 60 * 60 * 1000);
	    return result;
	}

	public static Timestamp addDays(int days, Timestamp t1) throws Exception{
	    if(days < 0){
	        throw new Exception("Day in wrong format.");
	    }
	    Long miliseconds = dayToMiliseconds(days);
	    return new Timestamp(t1.getTime() + miliseconds);
	}

	private static String convertDate(String mDate) {
//		System.out.println("MDATE: "+mDate);
		if(mDate == null || mDate == "" || "null".equals(mDate))
			return "";
		
	   SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
	   try {
	          Date newDate = inputFormat.parse(mDate);
	          inputFormat = new SimpleDateFormat("dd/MM/yyyy");
	          mDate = inputFormat.format(newDate);
	    } catch (ParseException e) {
	          e.printStackTrace();
	    }

	   return mDate;
	}
	
	private static String convertDateFull(String mDate) {
//		System.out.println("MDATE: "+mDate);
		if(mDate == null || mDate == "" || "null".equals(mDate))
			return "";
		
	   SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
	   try {
	          Date newDate = inputFormat.parse(mDate);
	          inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.S");
	          mDate = inputFormat.format(newDate);
	    } catch (ParseException e) {
	          e.printStackTrace();
	    }

	   return mDate;
	}

	private static BigDecimal getTabelaDePrecos(BigDecimal nroUnicoImportacao) {
		BigDecimal nuTabela = null;
		
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			
			JapeWrapper impProDAO = JapeFactory.dao("AD_IMPPRO");
			DynamicVO impProVO = impProDAO.findByPK(nroUnicoImportacao);
			
			JapeWrapper configDAO = JapeFactory.dao("AD_CONFIGIMPPRO");
			DynamicVO configVO = configDAO.findByPK(impProVO.asBigDecimal("NROUNICOCONFIG"));
			
			nuTabela = configVO.asBigDecimal("CODTAB");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JapeSession.close(hnd);
		}
		
		return nuTabela;
	}

	private static void cadastrarAtualizarPrecosTGFEXC(ContextoAcao ca, BigDecimal vlrVenda, BigDecimal codPro, BigDecimal nuTab, StringBuilder logImportacao) throws Exception {
		/* QUERY PARA VERIFICAR SE PRECO (TGFEXC) JÁ EXISTE */
		QueryExecutor query = ca.getQuery();
		try {
			query.nativeSelect("SELECT * FROM TGFEXC WHERE CODPROD = "+codPro+" AND NUTAB = "+nuTab+" AND CODLOCAL = 0 AND CONTROLE = ' '");
			if(query.next()) {
				logImportacao.append("Preço (TGFEXC) já existe para o CODPROD: "+codPro+" e Tabela: "+nuTab+" para a Data: "+convertDate(TimeUtils.getNow().toString())+"\n");
				return;
			}
		} catch (Exception e1) {
			ca.mostraErro(e1.getMessage());
		} finally {
			query.close();
		}
		/* ------ FIM DA QUERY ------ */
		
//		if(atualizado == 0) {
		// SE PRECO NÃO EXISTIR, REALIZAR CADASTRO
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper InsertEXC = JapeFactory.dao("Excecao");
			@SuppressWarnings("unused")
			DynamicVO salvar = InsertEXC.create()
					.set("CODPROD", codPro)
					.set("VLRVENDA", vlrVenda)
					.set("NUTAB", nuTab)
					.set("CODLOCAL", BigDecimal.ZERO)
					.set("CONTROLE", " ")
//					.set("AD_DHIMPORTACAO", TimeUtils.getNow())
//					.set("AD_CODUSUIMPORTACAO", ca.getUsuarioLogado())
					.save();
		} catch (Exception e) {
			e.printStackTrace();
			ca.mostraErro(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
//		}
	}

	private static void cadastrarAtualizarPrecosTGFCUS(ContextoAcao ca,	BigDecimal cusVariavel, BigDecimal cusRep, BigDecimal codPro, BigDecimal cusMed, BigDecimal cusGer, BigDecimal cusSemIcm, BigDecimal cusMedIcm, BigDecimal codEmp, StringBuilder logImportacao) throws Exception {
		/* QUERY PARA VERIFICAR SE PRECO (TGFCUS) JÁ EXISTE */
		QueryExecutor query = ca.getQuery();
		QueryExecutor queryUpdate = ca.getQuery();
		try {
			String select = "SELECT * FROM TGFCUS WHERE CODPROD = "+codPro+" AND CODEMP = "+codEmp+" AND (DTATUAL >= TO_DATE('"+convertDate(TimeUtils.getNow().toString())+" 00:00:00', 'DD/MM/YYYY HH24:MI:SS') AND DTATUAL <= TO_DATE('"+convertDate(TimeUtils.getNow().toString())+" 23:59:59', 'DD/MM/YYYY HH24:MI:SS')) AND CODLOCAL = 0 AND CONTROLE = ' '";
			query.nativeSelect(select);
			if(query.next()) {
				logImportacao.append("Custo (TGFCUS) já existe para o CODPROD: "+codPro+" e Empresa: "+codEmp+" para a Data: "+convertDateFull(TimeUtils.getNow().toString())+"\n");
				return;
			}
		} catch (Exception e1) {
			ca.mostraErro(e1.getMessage());
		} finally {
			query.close();
			queryUpdate.close();
		}
		/* ------ FIM DA QUERY ------ */
		
//		if(atualizado == 0) {
//		// SE PRECO NÃO EXISTIR, REALIZAR CADASTRO
		JapeSession.SessionHandle hnd = null;
		try {
//			System.out.println("PREÇO (TGFCUS) NÃO EXISTE PARA CODPROD: "+codPro);
			hnd = JapeSession.open();
			JapeWrapper InsertCUS = JapeFactory.dao(DynamicEntityNames.CUSTO);
			@SuppressWarnings("unused")
			DynamicVO salvar = InsertCUS.create()
					.set("CODPROD", codPro)
					.set("CODEMP", codEmp)
					.set("DTATUAL", TimeUtils.getNow())
					.set("CODLOCAL", BigDecimal.ZERO)
					.set("CONTROLE", " ")
					.set("NUNOTA", BigDecimal.ZERO)
					.set("SEQUENCIA", BigDecimal.ZERO)
					.set("CUSVARIAVEL", cusVariavel)
					.set("CUSREP", cusRep)
					.set("CUSMED", cusMed)
					.set("CUSGER", cusGer)
					.set("CUSSEMICM", cusSemIcm)
					.set("CUSMEDICM", cusMedIcm)
					.set("AUTOMATICO", "N")
					.set("CUSMEDCALC", "N")
//					.set("RECARGA", "N")
					.set("PROCESSO", "br.com.sankhya.atualizacao.de.custo")
//					.set("AD_DHIMPORTACAO", TimeUtils.getNow())
//					.set("AD_CODUSUIMPORTACAO", ca.getUsuarioLogado())
					.save();
		} catch (Exception e) {
			e.printStackTrace();
			ca.mostraErro(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
//		}
	}
}
