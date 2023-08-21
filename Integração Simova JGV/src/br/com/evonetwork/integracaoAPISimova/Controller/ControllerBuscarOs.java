package br.com.evonetwork.integracaoAPISimova.Controller;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;
import com.sankhya.util.JdbcUtils;

import br.com.evonetwork.integracaoAPISimova.Model.ApontamentoZZS;
import br.com.evonetwork.integracaoAPISimova.Model.ApontamentoZZT;
import br.com.evonetwork.integracaoAPISimova.Model.ApontamentoZZU;
import br.com.evonetwork.integracaoAPISimova.Model.Autenticacao;
import br.com.evonetwork.integracaoAPISimova.Model.RetornoCadastro;
import br.com.evonetwork.integracaoAPISimova.Model.Token;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class ControllerBuscarOs {

	public static void receberDadosApontamento(String urlAcesso, String comp, String codOs, String filial) throws Exception {
		Autenticacao auth = Controller.getParametrosSankhya("", filial);
		auth.setUrlAcesso(urlAcesso+comp);
		URL url = new URL(auth.getUrl());
		URL urlConsulta = new URL(auth.getUrlAcesso());
		
		System.out.println("Autenticando...");
		Token token = Controller.getToken(auth, url);
		
		try {
			if("VIEW_INTEGRACAO_ZZS".equals(comp))
				consultarApontamentoZZS(token, urlConsulta, codOs);
			else if("VIEW_INTEGRACAO_ZZT".equals(comp))
				consultarApontamentoZZT(token, urlConsulta, codOs);
			else if("VIEW_INTEGRACAO_ZZU".equals(comp))
				consultarApontamentoZZU(token, urlConsulta, codOs);
		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
	private static void consultarApontamentoZZU(Token token, URL urlConsulta, String codOs) throws Exception {
		System.out.println("***Consultando ZZU***");
		HttpURLConnection conn = (HttpURLConnection) urlConsulta.openConnection();
		conn.setRequestMethod("GET");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("authorization", token.getToken());
		
		StringBuilder body = new StringBuilder();
		
		body.append("{\r\n"
				+ "\"CodigoOs\": \""+codOs+"\"\r\n"
				+ "}\r\n");
		
		String data = body.toString();
        
        System.out.println("BODY: "+data);
        
        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = conn.getOutputStream();
        stream.write(out);
        
        String resposta = null;
        try {
        	resposta = Controller.getResponseBody(conn);
        } catch(Exception e) {
        	resposta = Controller.getErrorStream(conn);
        	
        	RetornoCadastro[] retornoErro = new Gson().fromJson(resposta, RetornoCadastro[].class); 
            
        	System.out.println("Erro na requisição!");
            System.out.println("Resposta HTTP: "+conn.getResponseCode()+" "+conn.getResponseMessage());
        	System.out.println("Retorno: "+retornoErro[0].getMsg());
        	
        	throw new Exception("Erro na requisição: "+retornoErro[0].getMsg());
        }
        
        ApontamentoZZU[] retornoApontamentoZZU = new Gson().fromJson(resposta, ApontamentoZZU[].class); 
        
        System.out.println("Resposta: "+conn.getResponseCode()+" "+conn.getResponseMessage());
        
        for(int i = 0; i < retornoApontamentoZZU.length; i++) {
        	System.out.println("Filial"+retornoApontamentoZZU[i].getZzu_filial());
            System.out.println("Local: "+retornoApontamentoZZU[i].getZzu_local());
            System.out.println("NumOSv: "+retornoApontamentoZZU[i].getZzu_numosv());
            System.out.println("SeqDbOs: "+retornoApontamentoZZU[i].getSeq_db_os());
            System.out.println("DataLancamento: "+retornoApontamentoZZU[i].getZzu_dtlanc());
            System.out.println("DataIntegracao: "+retornoApontamentoZZU[i].getZzu_datint());
            System.out.println("HoraIntegracao: "+retornoApontamentoZZU[i].getZzu_horint());
            System.out.println("DataInsercao: "+retornoApontamentoZZU[i].getZzu_datins());
            System.out.println("Delete: "+retornoApontamentoZZU[i].getD_e_l_e_t_());
            System.out.println("proc_st: "+retornoApontamentoZZU[i].getProc_st());
        }
        
        conn.disconnect();
	}

	private static void consultarApontamentoZZT(Token token, URL urlConsulta, String codOs) throws Exception {
		System.out.println("***Consultando ZZT***");
		HttpURLConnection conn = (HttpURLConnection) urlConsulta.openConnection();
		conn.setRequestMethod("GET");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("authorization", token.getToken());
		
		StringBuilder body = new StringBuilder();
		
		body.append("{\r\n"
				+ "\"CodigoOs\": \""+codOs+"\"\r\n"
				+ "}\r\n");
		
		String data = body.toString();
        
        System.out.println("BODY: "+data);
        
        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = conn.getOutputStream();
        stream.write(out);
        
        String resposta = null;
        try {
        	resposta = Controller.getResponseBody(conn);
        } catch(Exception e) {
        	resposta = Controller.getErrorStream(conn);
        	
        	RetornoCadastro[] retornoErro = new Gson().fromJson(resposta, RetornoCadastro[].class); 
            
        	System.out.println("Erro na requisição!");
            System.out.println("Resposta HTTP: "+conn.getResponseCode()+" "+conn.getResponseMessage());
        	System.out.println("Retorno: "+retornoErro[0].getMsg());
        	
        	throw new Exception("Erro na requisição: "+retornoErro[0].getMsg());
        }
        
        ApontamentoZZT[] retornoApontamentoZZT = new Gson().fromJson(resposta, ApontamentoZZT[].class); 
        
        System.out.println("Resposta: "+conn.getResponseCode()+" "+conn.getResponseMessage());
        
        for(int i = 0; i < retornoApontamentoZZT.length; i++) {
        	atualizarDiagnosticoOficina(new BigDecimal (retornoApontamentoZZT[i].getZzt_numosv()), retornoApontamentoZZT[i].getZzt_texto());
        }
        
        conn.disconnect();
	}

	private static void atualizarDiagnosticoOficina(BigDecimal numOs, String diagnostico) throws Exception {
		SessionHandle hnd = null;
		String diagnosticoFormatado = diagnostico.replace(" - ", "\n");
		try {
			hnd = JapeSession.open();
			JapeWrapper adtcsoseDAO = JapeFactory.dao("TCSOSE");
			DynamicVO os = adtcsoseDAO.findOne("NUMOS = "+numOs);
			adtcsoseDAO.prepareToUpdate(os)
				.set("DIAGNOSTICOOFICINA", diagnosticoFormatado)
				.update();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

	private static void consultarApontamentoZZS(Token token, URL urlConsulta, String codOs) throws Exception {
		System.out.println("Consultando ZZS...");
		HttpURLConnection conn = (HttpURLConnection) urlConsulta.openConnection();
		conn.setRequestMethod("GET");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("authorization", token.getToken());
		
		StringBuilder body = new StringBuilder();
		
		body.append("{\r\n"
				+ "\"CodigoOs\": \""+codOs+"\"\r\n"
				+ "}\r\n");
		
		String data = body.toString();
        
		System.out.println("URL: "+urlConsulta.toString());
		System.out.println("Request Method: "+conn.getRequestMethod());
		System.out.println("Content-Type: "+conn.getRequestProperty("Content-Type"));
        System.out.println("BODY: "+data);
        
        byte[] out = data.getBytes(StandardCharsets.UTF_8);
        
        OutputStream stream = conn.getOutputStream();
        stream.write(out);
        
        String resposta = null;
        try {
        	resposta = Controller.getResponseBody(conn);
        } catch(Exception e) {
        	resposta = Controller.getErrorStream(conn);
        	
        	RetornoCadastro[] retornoErro = new Gson().fromJson(resposta, RetornoCadastro[].class); 
            
        	System.out.println("Erro na requisição!");
            System.out.println("Resposta HTTP: "+conn.getResponseCode()+" "+conn.getResponseMessage());
        	System.out.println("Retorno: "+retornoErro[0].getMsg());
        	
        	throw new Exception("Erro na requisição: "+retornoErro[0].getMsg());
        }
        
        ApontamentoZZS[] retornoApontamentoZZS = new Gson().fromJson(resposta, ApontamentoZZS[].class); 
        
        System.out.println("Resposta: "+conn.getResponseCode()+" "+conn.getResponseMessage());
        
        boolean retornouApontamentos = false;
        for(int i = 0; i < retornoApontamentoZZS.length; i++) {
        	retornouApontamentos = true;
            System.out.println("Apontamento "+i+" sendo integrado...");
            criarRegistroDeApontamento(retornoApontamentoZZS[i]);
            BigDecimal qtdKM = somarKmApontamentos(codOs, retornoApontamentoZZS[i]);
            if(qtdKM.compareTo(BigDecimal.ZERO) != 0)
            	atualizarServicoExecutadoComApontamento(retornoApontamentoZZS[i], qtdKM);
            somarEPreencherHorasDosApontamentos(codOs, retornoApontamentoZZS[i]);
        	atualizarOSComHorimetro(new BigDecimal(retornoApontamentoZZS[i].getZzs_hortri()), new BigDecimal(retornoApontamentoZZS[i].getZzs_numosv()));
        }
//      fecharOS(codOs);
        conn.disconnect();
        if(!retornouApontamentos)
        	throw new Exception("Nenhum apontamento retornado pelo Simova para a OS "+codOs+"!");
	}
	
	private static void somarEPreencherHorasDosApontamentos(String codOs, ApontamentoZZS apontamentoZZS) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal numItem = getNumItemDoServiço(new BigDecimal(codOs), apontamentoZZS.getZzs_codser());
		int total = 0;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT DHINICIAL, DHFINAL FROM AD_APONTAMENTOS WHERE NUMOS = "+codOs+" AND NUMITEM = "+numItem);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			while (rset.next()) {
				String dhIni = rset.getTimestamp("DHINICIAL").toString();
				String dhFin = rset.getTimestamp("DHFINAL").toString();
				
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
				
				Date d1 = df.parse(dhIni);
				Date d2 = df.parse(dhFin);
				
				BigDecimal diff = BigDecimal.valueOf(d2.getTime() - d1.getTime());
			    BigDecimal diffHours = diff.divide(BigDecimal.valueOf(60 * 60 * 1000), 2, RoundingMode.HALF_UP);
			    total += diffHours.multiply(BigDecimal.valueOf(60)).intValue();
			}
			atualizarServicoComHoras(codOs, getNumItemDoServiço(new BigDecimal(codOs), apontamentoZZS.getZzs_codser()), horaParaInteiro(total));
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
	
	private static int horaParaInteiro(int hr) {
		BigDecimal total = BigDecimal.valueOf(hr).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
		BigDecimal horas = BigDecimal.valueOf(total.intValue());
		BigDecimal minutos = total.subtract(horas);
		int min = minutos.multiply(BigDecimal.valueOf(60)).intValue();
		int hrs = horas.multiply(BigDecimal.valueOf(100)).intValue();
		int retorno = hrs+min;
		return retorno;
	}
	
	private static void atualizarServicoComHoras(String numOs, BigDecimal numItemDoServiço, int total) throws Exception {
		SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper adtcsoseDAO = JapeFactory.dao("TCSITE");
			DynamicVO os = adtcsoseDAO.findOne("NUMOS = "+numOs+" AND NUMITEM = "+numItemDoServiço);
			adtcsoseDAO.prepareToUpdate(os)
				.set("HORAAPONTAMENTOS", BigDecimal.valueOf(total))
				.update();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

	private static void atualizarOSComHorimetro(BigDecimal horimetro, BigDecimal numOs) throws Exception {
		SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper adtcsoseDAO = JapeFactory.dao("TCSOSE");
			DynamicVO os = adtcsoseDAO.findOne("NUMOS = "+numOs);
			adtcsoseDAO.prepareToUpdate(os)
				.set("HORIMETRO", horimetro)
				.update();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

	private static BigDecimal somarKmApontamentos(String codOs, ApontamentoZZS apontamentoZZS) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal soma = BigDecimal.ZERO;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT KMINIDESLOCAMENTO, KMFIMDESLOCAMENTO FROM AD_APONTAMENTOS WHERE NUMOS = "+codOs+" AND NUMITEM = "+getNumItemDoServiço(new BigDecimal(codOs), apontamentoZZS.getZzs_codser()));
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			while (rset.next()) {
				BigDecimal kmIni = rset.getBigDecimal("KMINIDESLOCAMENTO");
				BigDecimal kmFin = rset.getBigDecimal("KMFIMDESLOCAMENTO");
				if(kmIni != null && kmFin != null) {
					BigDecimal FinalMenosInicial = kmFin.subtract(kmIni);
					soma = soma.add(FinalMenosInicial);
				}
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
		return soma;
	}

	private static void criarRegistroDeApontamento(ApontamentoZZS apontamentoZZS) throws Exception {
		BigDecimal numOs = new BigDecimal(apontamentoZZS.getZzs_numosv());
		BigDecimal numItem = getNumItemDoServiço(numOs, apontamentoZZS.getZzs_codser());
		salvarRegistroDeApontamento(numOs, numItem, apontamentoZZS);
	}

	@SuppressWarnings("unused")
	private static void salvarRegistroDeApontamento(BigDecimal numOs, BigDecimal numItem,
			ApontamentoZZS apontamentoZZS) throws Exception {
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper apontamentosDAO = JapeFactory.dao("AD_APONTAMENTOS");
			DynamicVO save = apontamentosDAO.create()
				.set("NUMOS", numOs)
				.set("NUMITEM", numItem)
				.set("DHINICIAL", transformarEmDataEHora(apontamentoZZS.getZzs_datini(), apontamentoZZS.getZzs_horini()))
				.set("DHFINAL", transformarEmDataEHora(apontamentoZZS.getZzs_datfin(), apontamentoZZS.getZzs_horfin()))
				.set("KMINIDESLOCAMENTO", new BigDecimal(apontamentoZZS.getZzs_kilini()))
				.set("KMFIMDESLOCAMENTO", new BigDecimal(apontamentoZZS.getZzs_kilfin()))
				.set("APONTAMENTOAUTO", "S")
				.save();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

	private static BigDecimal getNumItemDoServiço(BigDecimal numOs, String refForn) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal dado = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT NUMITEM FROM AD_TCSITE WHERE NUMOS = "+numOs+" AND CODSERV = "+Controller.getCodServicoPelaRefFornecedor(refForn));
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal("NUMITEM");
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
		return dado;
	}

//	private static void fecharOS(String codOs) throws Exception {
//		SessionHandle hnd = null;
//		try {
//			hnd = JapeSession.open();
//
//			JapeFactory.dao("TCSOSE").prepareToUpdateByPK(codOs)
//				.set("STATUSSERVICO", "C")
//				.set("STATUS", "F")
//				.update();
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new Exception(e.getMessage());
//		} finally {
//			JapeSession.close(hnd);
//		}
//	}

	private static void atualizarServicoExecutadoComApontamento(ApontamentoZZS apontamentoZZS, BigDecimal qtdKM) throws Exception {
		SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper adtcsiteDAO = JapeFactory.dao("TCSITE");
			DynamicVO apontamento = adtcsiteDAO.findOne("NUMOS = "+apontamentoZZS.getZzs_numosv()+" AND CODSERV = "+Controller.getCodServicoPelaRefFornecedor(apontamentoZZS.getZzs_codser()));
			adtcsiteDAO.prepareToUpdate(apontamento)
				.set("QTDNEG", qtdKM)
				.update();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}
	
	private static Timestamp transformarEmDataEHora(String zzs_datini, String zzs_horini) {
		String dataEHora = "";
		dataEHora = zzs_datini.substring(6, 8)+"/"+zzs_datini.substring(4, 6)+"/"+zzs_datini.substring(0, 4)+" "
				+zzs_horini.substring(0, 2)+":"+zzs_horini.substring(2, 4);
		Timestamp dh = transformStringToTimestamp(dataEHora);
		return dh;
	}

	private static Timestamp transformStringToTimestamp(String dataEHora) {
		try {
	      DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	       // you can change format of date
	      Date date = formatter.parse(dataEHora);
	      Timestamp timeStampDate = new Timestamp(date.getTime());

	      return timeStampDate;
	    } catch (ParseException e) {
	      System.out.println("Exception :" + e);
	      return null;
	    }
	}

//	private static Object getCodUsuPeloParceiro(String zzs_codpro) throws Exception {
//		JdbcWrapper jdbc = null;
//		NativeSql sql = null;
//		ResultSet rset = null;
//		SessionHandle hnd = null;
//		BigDecimal retorno = null;
//		try {
//			hnd = JapeSession.open();
//			hnd.setFindersMaxRows(-1);
//			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
//			jdbc = entity.getJdbcWrapper();
//			jdbc.openSession();
//
//			sql = new NativeSql(jdbc);
//
//			sql.appendSql("SELECT CODUSU FROM TSIUSU WHERE CODPARC = "+zzs_codpro);
//			System.out.println("SQL: "+sql.toString());
//			
//			rset = sql.executeQuery();
//
//			if (rset.next()) {
//				retorno = rset.getBigDecimal("CODUSU");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new Exception(e.getMessage());
//		} finally {
//			JdbcUtils.closeResultSet(rset);
//			NativeSql.releaseResources(sql);
//			JdbcWrapper.closeSession(jdbc);
//			JapeSession.close(hnd);
//		}
//		
//		return retorno;
//	}
	
}
