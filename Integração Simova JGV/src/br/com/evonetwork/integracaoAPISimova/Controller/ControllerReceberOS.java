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
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class ControllerReceberOS {

	public static void receberOS(BigDecimal numOs, BigDecimal codEmp, ContextoAcao ca) throws Exception {
		String urlAcesso = Controller.getUrlAcesso();
		String filial = Controller.getFilial(codEmp);
		receberDadosApontamento(urlAcesso, "VIEW_INTEGRACAO_ZZU", numOs, filial);
		receberDadosApontamento(urlAcesso, "VIEW_INTEGRACAO_ZZT", numOs, filial);
		receberDadosApontamento(urlAcesso, "VIEW_INTEGRACAO_ZZS", numOs, filial);
	}

	private static void receberDadosApontamento(String urlAcesso, String comp, BigDecimal numOs, String filial) throws Exception {
		Autenticacao auth = Controller.getParametrosSankhya("", filial);
		auth.setUrlAcesso(urlAcesso+comp);
		URL url = new URL(auth.getUrl());
		URL urlConsulta = new URL(auth.getUrlAcesso());
		
		System.out.println("Autenticando...");
		Token token = Controller.getToken(auth, url);
		
		try {
			if("VIEW_INTEGRACAO_ZZS".equals(comp))
				consultarApontamentoZZS(token, urlConsulta, numOs);
			else if("VIEW_INTEGRACAO_ZZT".equals(comp))
				consultarApontamentoZZT(token, urlConsulta, numOs);
			else if("VIEW_INTEGRACAO_ZZU".equals(comp))
				consultarApontamentoZZU(token, urlConsulta, numOs);
		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	private static void consultarApontamentoZZS(Token token, URL urlConsulta, BigDecimal numOs) throws Exception {
		System.out.println("Consultando ZZS...");
		try {
			HttpURLConnection conn = (HttpURLConnection) urlConsulta.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("authorization", token.getToken());
			
			StringBuilder body = new StringBuilder();
			
			body.append("{\r\n"
					+ "\"CodigoOs\": \""+numOs+"\"\r\n"
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
	        
	        for(int i = 0; i < retornoApontamentoZZS.length; i++) {
	            System.out.println("Serviço/Apontamento "+i+" sendo integrado...");
	            criarServicoEApontamento(retornoApontamentoZZS[i], numOs);
	            BigDecimal qtdKM = somarKmApontamentos(numOs, retornoApontamentoZZS[i]);
	            if(qtdKM.compareTo(BigDecimal.ZERO) != 0)
	            	atualizarServicoExecutadoComApontamento(retornoApontamentoZZS[i], qtdKM);
	            somarEPreencherHorasDosApontamentos(numOs, retornoApontamentoZZS[i]);
	        	atualizarOSComHorimetro(new BigDecimal(retornoApontamentoZZS[i].getZzs_hortri()), numOs);
	        	atualizarSolicitanteDaOS(retornoApontamentoZZS[i].getZzs_cliente(), numOs);
	        	atualizarVeiculoEquipamentoDaOS(retornoApontamentoZZS[i].getZzs_placa(), numOs);
	        }
	        conn.disconnect();
		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	private static void atualizarVeiculoEquipamentoDaOS(String zzs_placa, BigDecimal numOs) throws Exception {
		SessionHandle hnd = null;
		try {
			BigDecimal codVeiculo = getCodVeiculo(zzs_placa);
			hnd = JapeSession.open();
			JapeWrapper adtcsoseDAO = JapeFactory.dao("TCSOSE");
			DynamicVO os = adtcsoseDAO.findOne("NUMOS = "+numOs);
			adtcsoseDAO.prepareToUpdate(os)
				.set("CODVEICULO", codVeiculo)
				.update();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
		
	}

	private static BigDecimal getCodVeiculo(String zzs_placa) throws Exception {
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
			
			sql.appendSql("SELECT CODVEICULO FROM TGFVEI WHERE PLACA = '"+zzs_placa+"'");
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal("CODVEICULO");
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

	private static void atualizarSolicitanteDaOS(String zzs_cliente, BigDecimal numOs) throws Exception {
		BigDecimal codCliente = getCodCliente(zzs_cliente);
		SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper adtcsoseDAO = JapeFactory.dao("TCSOSE");
			DynamicVO os = adtcsoseDAO.findOne("NUMOS = "+numOs);
			adtcsoseDAO.prepareToUpdate(os)
				.set("CODPARC", codCliente)
				.update();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

	private static BigDecimal getCodCliente(String zzs_cliente) {
		String partes[] = zzs_cliente.split(":");
		BigDecimal codCliente = new BigDecimal(partes[0]);
		return codCliente;
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

	private static void somarEPreencherHorasDosApontamentos(BigDecimal numOs, ApontamentoZZS apontamentoZZS) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal numItem = getNumItemDoServiço(numOs, new BigDecimal(apontamentoZZS.getZzs_codser()));
		int total = 0;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT DHINICIAL, DHFINAL FROM AD_APONTAMENTOS WHERE NUMOS = "+numOs+" AND NUMITEM = "+numItem);
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
			atualizarServicoComHoras(numOs, getNumItemDoServiço(numOs, new BigDecimal(apontamentoZZS.getZzs_codser())), horaParaInteiro(total));
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

	private static void atualizarServicoComHoras(BigDecimal numOs, BigDecimal numItemDoServiço, int total) throws Exception {
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

	private static int horaParaInteiro(int hr) {
		BigDecimal total = BigDecimal.valueOf(hr).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
		BigDecimal horas = BigDecimal.valueOf(total.intValue());
		BigDecimal minutos = total.subtract(horas);
		int min = minutos.multiply(BigDecimal.valueOf(60)).intValue();
		int hrs = horas.multiply(BigDecimal.valueOf(100)).intValue();
		int retorno = hrs+min;
		return retorno;
	}

	private static void atualizarServicoExecutadoComApontamento(ApontamentoZZS apontamentoZZS, BigDecimal qtdKM) throws Exception {
		SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper adtcsiteDAO = JapeFactory.dao("TCSITE");
			DynamicVO apontamento = adtcsiteDAO.findOne("NUMOS = "+apontamentoZZS.getZzs_numosv()+" AND CODSERV = "+apontamentoZZS.getZzs_codser());
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

	private static BigDecimal somarKmApontamentos(BigDecimal numOs, ApontamentoZZS apontamentoZZS) throws Exception {
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
			
			sql.appendSql("SELECT KMINIDESLOCAMENTO, KMFIMDESLOCAMENTO FROM AD_APONTAMENTOS WHERE NUMOS = "+numOs+" AND NUMITEM = "+getNumItemDoServiço(numOs, new BigDecimal(apontamentoZZS.getZzs_codser())));
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

	private static BigDecimal getNumItemDoServiço(BigDecimal numOs, BigDecimal codServ) throws Exception {
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
			
			sql.appendSql("SELECT NUMITEM FROM AD_TCSITE WHERE NUMOS = "+numOs+" AND CODSERV = "+codServ);
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

	private static void criarServicoEApontamento(ApontamentoZZS apontamentoZZS, BigDecimal numOs) throws Exception {
		BigDecimal numItem = getNumItemOuCriaServiço(numOs, apontamentoZZS);
		salvarRegistroDeApontamento(numOs, numItem, apontamentoZZS);
	}

	private static BigDecimal getNumItemOuCriaServiço(BigDecimal numOs, ApontamentoZZS apontamentoZZS) throws Exception {
		BigDecimal codServ = Controller.getCodServicoPelaRefFornecedor(apontamentoZZS.getZzs_codser());
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
			
			sql.appendSql("SELECT NUMITEM FROM AD_TCSITE WHERE NUMOS = "+numOs+" AND CODSERV = "+codServ);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal("NUMITEM");
			} else {
				dado = criarServicoExecutadoNaOs(apontamentoZZS, numOs, codServ);
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

	private static BigDecimal criarServicoExecutadoNaOs(ApontamentoZZS apontamentoZZS, BigDecimal numOs, BigDecimal codServ) throws Exception {
		JapeSession.SessionHandle hnd = null;
		BigDecimal numItem = null;
		BigDecimal codUsu = getCodUsuComEsteParceiro(apontamentoZZS.getZzs_codpro());
		try {
			hnd = JapeSession.open();
			JapeWrapper notaDAO = JapeFactory.dao("TCSITE");
			DynamicVO save = notaDAO.create()
				.set("NUMOS", numOs)
				.set("CODSERV", codServ)
				.set("CODEMP", new BigDecimal(apontamentoZZS.getZzs_local()))
				.set("CODUSURESP", codUsu)
				.set("DATA", formatDate(apontamentoZZS.getDatins()))
				.save();
			numItem = save.asBigDecimal("NUMITEM");
			atualizarOScomUsuario(numOs, codUsu);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
		return numItem;
	}

	private static void atualizarOScomUsuario(BigDecimal numOs, BigDecimal codUsu) throws Exception {
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper cabecalhoDAO = JapeFactory.dao("TCSOSE");
			DynamicVO cab = cabecalhoDAO.findOne("NUMOS = "+numOs);
			cabecalhoDAO.prepareToUpdate(cab)
				.set("CODUSURESPOFICINA", codUsu)
				.update();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

	private static Timestamp formatDate(String mDate) throws Exception {
		if(mDate == null || mDate == "" || "null".equals(mDate))
			return null;
		
	   SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   try {
	          Date newDate = inputFormat.parse(mDate);
	          inputFormat = new SimpleDateFormat("dd/MM/yyyy");
	          mDate = inputFormat.format(newDate);
	    } catch (ParseException e) {
	          e.printStackTrace();
	    }

	   return stringToTimestampDateOnly(mDate);
	}

	private static BigDecimal getCodUsuComEsteParceiro(String codParc) throws Exception {
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
			
			sql.appendSql("SELECT MAX(CODUSU) FROM TSIUSU WHERE CODPARC = "+codParc);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal(1);
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

	private static Object transformarEmDataEHora(String zzs_datini, String zzs_horini) {
		String dataEHora = "";
		dataEHora = zzs_datini.substring(6, 8)+"/"+zzs_datini.substring(4, 6)+"/"+zzs_datini.substring(0, 4)+" "
				+zzs_horini.substring(0, 2)+":"+zzs_horini.substring(2, 4);
		Timestamp dh = transformStringToTimestamp(dataEHora);
		return dh;
	}

	private static Timestamp transformStringToTimestamp(String dataEHora) {
		try {
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			Date date = formatter.parse(dataEHora);
			Timestamp timeStampDate = new Timestamp(date.getTime());
			return timeStampDate;
	    } catch (ParseException e) {
	    	System.out.println("Exception :" + e);
	    	return null;
	    }
	}

	private static void consultarApontamentoZZT(Token token, URL urlConsulta, BigDecimal numOs) throws Exception {
		System.out.println("Consultando ZZT...");
		try {
			HttpURLConnection conn = (HttpURLConnection) urlConsulta.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("authorization", token.getToken());
			
			StringBuilder body = new StringBuilder();
			
			body.append("{\r\n"
					+ "\"CodigoOs\": \""+numOs+"\"\r\n"
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
	        	atualizarDiagnosticoOficina(numOs, retornoApontamentoZZT[i].getZzt_texto());
	        }
	        
	        conn.disconnect();
		}catch(Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	private static void atualizarDiagnosticoOficina(BigDecimal numOs, String diagnostico) throws Exception {
		SessionHandle hnd = null;
		String diagnosticoFormatado = diagnostico.replace(" - ", "\n");
		try {
			hnd = JapeSession.open();
			JapeWrapper adtcsoseDAO = JapeFactory.dao("TCSOSE");
			DynamicVO os = adtcsoseDAO.findOne("NUMOS = "+numOs);
			adtcsoseDAO.prepareToUpdate(os)
				.set("DESCRSERV", diagnosticoFormatado)
				.update();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

	private static void consultarApontamentoZZU(Token token, URL urlConsulta, BigDecimal numOs) throws Exception {
		System.out.println("Consultando ZZU...");
		try {
			HttpURLConnection conn = (HttpURLConnection) urlConsulta.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("authorization", token.getToken());
			
			StringBuilder body = new StringBuilder();
			
			body.append("{\r\n"
					+ "\"CodigoOs\": \""+numOs+"\"\r\n"
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
	        
	        boolean retornouApontamentos = false;
	        for(int i = 0; i < retornoApontamentoZZU.length; i++) {
	        	//AD_TCSOSE
	        	retornouApontamentos = true;
	            BigDecimal codEmp = new BigDecimal(retornoApontamentoZZU[i].getZzu_filial());
	            BigDecimal idIntegracao = new BigDecimal(retornoApontamentoZZU[i].getSeq_db_os());
	            Timestamp dtEntrada = stringToTimestamp(retornoApontamentoZZU[i].getZzu_datins());
	            criarNotaNoSankhya(numOs, codEmp, idIntegracao, dtEntrada);
	        }
	        
	        conn.disconnect();
	        
	        if(!retornouApontamentos)
	        	throw new Exception("Nenhum apontamento retornado pelo Simova para a OS "+numOs+"!");
		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	private static void criarNotaNoSankhya(BigDecimal numOs, BigDecimal codEmp, BigDecimal idIntegracao,
			Timestamp dtEntrada) throws Exception {
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper notaDAO = JapeFactory.dao("TCSOSE");
			@SuppressWarnings("unused")
			DynamicVO save = notaDAO.create()
				.set("NUMOS", numOs)
				.set("CODEMP", codEmp)
				.set("IDINTEGRACAO", idIntegracao)
				.set("DHABERTURA", dtEntrada)
				.set("CODTIPODEOS", BigDecimal.valueOf(99))
				.set("CODATENDIMENTO", getCodAtendimento(BigDecimal.valueOf(99)))
				.set("CODFATUR", getCodFaturamento(BigDecimal.valueOf(99)))
				.set("CODCENCUS", getCodCentroResultado(BigDecimal.valueOf(99)))
				.set("CODNAT", getCodNatureza(BigDecimal.valueOf(99)))
				.save();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}
	
	private static BigDecimal getCodNatureza(BigDecimal codTipoOs) throws Exception {
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
			
			sql.appendSql("SELECT CODNAT FROM AD_TIPOORDEMSERVICO WHERE CODTIPODEOS = "+codTipoOs);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal("CODNAT");
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
	
	private static BigDecimal getCodCentroResultado(BigDecimal codTipoOs) throws Exception {
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
			
			sql.appendSql("SELECT CODCENCUS FROM AD_TIPOORDEMSERVICO WHERE CODTIPODEOS = "+codTipoOs);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal("CODCENCUS");
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
	
	private static BigDecimal getCodFaturamento(BigDecimal codTipoOs) throws Exception {
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
			
			sql.appendSql("SELECT CODFATUR FROM AD_TIPOORDEMSERVICO WHERE CODTIPODEOS = "+codTipoOs);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal("CODFATUR");
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
	
	private static BigDecimal getCodAtendimento(BigDecimal codTipoOs) throws Exception {
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
			
			sql.appendSql("SELECT CODATENDIMENTO FROM AD_TIPOORDEMSERVICO WHERE CODTIPODEOS = "+codTipoOs);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal("CODATENDIMENTO");
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

	private static Timestamp stringToTimestampDateOnly(String data) throws Exception {
		Timestamp timestamp = null;
		try {
		    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		    Date parsedDate = dateFormat.parse(data);
		    timestamp = new java.sql.Timestamp(parsedDate.getTime());
		} catch(Exception e) {
		    e.printStackTrace();
		    throw new Exception(e.getMessage());
		}
		return timestamp;
	}

	private static Timestamp stringToTimestamp(String data) throws Exception {
		Timestamp timestamp = null;
		try {
		    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    Date parsedDate = dateFormat.parse(data);
		    timestamp = new java.sql.Timestamp(parsedDate.getTime());
		} catch(Exception e) {
		    e.printStackTrace();
		    throw new Exception(e.getMessage());
		}
		return timestamp;
	}

}
