package br.com.evonetwork.integracaoCamposDealer.Controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;

import com.google.gson.Gson;
import com.sankhya.util.JdbcUtils;

import br.com.evonetwork.integracaoCamposDealer.DAO.EmpresaDAO;
import br.com.evonetwork.integracaoCamposDealer.Model.Empresa;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.modelcore.util.MGECoreParameter;

public class EmpresaController {

	public static void iniciarBusca(ContextoAcao ca, Registro linha) throws Exception {
		System.out.println("-----Iniciando a busca de empresas-----");
		try {
			String urlBase = MGECoreParameter.getParameterAsString("URLBASECOSD");
			int grupo = MGECoreParameter.getParameterAsInt("GRUPOCOSD");
			String strUrl = urlBase + "/grupo/" + grupo + "/empresa/0/detalhes";
			String auth = "BASIC " + MGECoreParameter.getParameterAsString("AUTHCOSD");
			System.out.println("URL: " + strUrl + "\n Auth: " + auth);
			URL url = new URL(strUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setRequestProperty("Authorization", auth);

			String resposta = null;
			try {
				resposta = getResponseBody(conn);
			} catch (Exception e) {
				try {
					resposta = getErrorStream(conn);
				} catch (Exception e1) {
					System.out.println("Nenhum ErrorStream retornado: " + e1.getMessage());
				}
				if ((conn.getResponseCode()) == 404) {
					System.out.println("Nenhuma empresa encontrada ou requisição está incorreta.");
					ca.setMensagemRetorno("Nenhuma empresa encontrada ou requisição está incorreta.");
					return;
				} else {
					System.out.println("Erro na requisiÃ§Ã£o!");
					System.out.println("Resposta HTTP: " + conn.getResponseCode() + " " + conn.getResponseMessage());
				}
				throw new Exception("Erro na requisição: " + conn.getResponseCode() + ": " + conn.getResponseMessage());
			}

			Empresa[] empresaBusca = new Gson().fromJson(resposta, Empresa[].class);
			System.out.println("Resposta HTTP: " + conn.getResponseCode() + " " + conn.getResponseMessage());

			boolean find = false;

			for (Empresa e : empresaBusca) {
				String cpf_cnpj = e.getCNPJ_CPF();
				BigDecimal codemp = (BigDecimal) linha.getCampo("CODEMP");
				String CGC_CPF = (String) linha.getCampo("CGC");

				if (CGC_CPF.equals(cpf_cnpj)) {
					EmpresaDAO.atualizarProspect(e, codemp);
					ca.setMensagemRetorno("Sucesso ao integrar empresa");
					find = true;
				}
			}

			if (!find) {
				throw new Exception("Empresa não encontrada no CampOS Dealer");
			}

			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		System.out.println("-----Finalizando a busca de Empresas-----");
	}
	
	public static BigDecimal buscarCodigoEmpresa(String codCamposDealer) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		
		BigDecimal codEmp = null;
		
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT CODEMP FROM TSIEMP WHERE AD_IDCAMPOSDEALER = '" + codCamposDealer + "'");
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				codEmp = rset.getBigDecimal("CODEMP");
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
		return codEmp;
	}

	private static String getResponseBody(HttpURLConnection conn) {
		BufferedReader br = null;
		StringBuilder body = null;
		String line = "";
		try {
			br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			body = new StringBuilder();
			while ((line = br.readLine()) != null)
				body.append(line);
			return body.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static String getErrorStream(HttpURLConnection conn) throws Exception {
		InputStream errorstream = conn.getErrorStream();
		String response = "";
		String line;
		BufferedReader br = new BufferedReader(new InputStreamReader(errorstream, "UTF-8"));
		while ((line = br.readLine()) != null) {
			response += line;
		}
		return response;
	}
}
