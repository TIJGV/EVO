package br.com.evonetwork.integracaoCamposDealer.Controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;

import org.activiti.engine.impl.util.json.JSONObject;

import com.google.gson.Gson;
import com.sankhya.util.JdbcUtils;
import com.sankhya.util.TimeUtils;

import br.com.evonetwork.integracaoCamposDealer.DAO.GrupoProdutoDAO;
import br.com.evonetwork.integracaoCamposDealer.Model.EmpresaCDxEmpresaSNK;
import br.com.evonetwork.integracaoCamposDealer.Model.ProdutoGrupo;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.modelcore.util.MGECoreParameter;

public class GrupoProdutoController {

	public static void integraGrupo(Registro r, ContextoAcao ca) throws Exception {

		BigDecimal codGrupoProd = (BigDecimal) r.getCampo("CODGRUPOPROD");
		String descrGrupoProd = (String) r.getCampo("DESCRGRUPOPROD");
		Timestamp dtatual = TimeUtils.getNow();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String dtFormat = format.format(dtatual);

		ArrayList<EmpresaCDxEmpresaSNK> empresas = buscarEmpresasSincronia(codGrupoProd);

		for (EmpresaCDxEmpresaSNK empresa : empresas) {

			try {
				String urlBase = MGECoreParameter.getParameterAsString("URLBASECOSD");
				int grupo = MGECoreParameter.getParameterAsInt("GRUPOCOSD");
				String strUrl = urlBase + "/grupo/" + grupo + "/empresa/" + empresa.getIdCamposDealer() + "/ProdutoGrupo/" + codGrupoProd;
				String auth = "BASIC " + MGECoreParameter.getParameterAsString("AUTHCOSD");

				URL url = new URL(strUrl);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("POST");
				conn.setDoOutput(true);
				conn.setRequestProperty("Authorization", auth);
				conn.setRequestProperty("Content-Type", "application/json");
				conn.setRequestProperty("Accept", "application/json");

				PrintStream printStream = new PrintStream(conn.getOutputStream());

				JSONObject json = new JSONObject();
				json.put("codProdutoGrupo", codGrupoProd.toString());
				json.put("dscProdutoGrupo", descrGrupoProd);
				json.put("dthRegistro", dtFormat);

				System.out.println("INTEGRAÇÃO GRUPO PRODUTO");
				System.out.println(json);

				printStream.println(json);

				conn.connect(); // envia para o servidor
				
				if (conn.getResponseCode() == 409) {
					BigDecimal id = buscarIdSalvo( codGrupoProd,  new BigDecimal(empresa.getIdCamposDealer()));
					if(id.equals(BigDecimal.ZERO)) {
						coletarIdCamposDealer(codGrupoProd,  empresa);
						ca.setMensagemRetorno("Grupo de produto integrado com sucesso!");
					} else {
						ca.setMensagemRetorno("Grupo de produto já integrado!");
					}
					continue;
				}

				Scanner scanner = new Scanner(conn.getInputStream());
				String jsonDeResposta = "";

				while (scanner.hasNext()) {
					jsonDeResposta = jsonDeResposta + scanner.next();
				}

				printStream.println(jsonDeResposta);
				
				
				if (conn.getResponseCode() == 201) {
					ca.setMensagemRetorno("Grupo de produto integrado com sucesso! \n" + jsonDeResposta);
				}

				if (conn.getResponseCode() != 201) {
					throw new Exception(jsonDeResposta);
				}

				JSONObject jsonResp = new JSONObject(jsonDeResposta);
				GrupoProdutoDAO.salvaIdCamposDealer(codGrupoProd, empresa.getCodemp(), new BigDecimal(jsonResp.getInt("idProdutoGrupo")));

				conn.disconnect();
				scanner.close();

			} catch (Exception ex) {
				throw new Exception(ex.getMessage());
			}
		}
	}
	
	public static void coletarIdCamposDealer(BigDecimal codGrupoProd, EmpresaCDxEmpresaSNK empresa) throws Exception {
		try {
			String urlBase = MGECoreParameter.getParameterAsString("URLBASECOSD");
			int grupo = MGECoreParameter.getParameterAsInt("GRUPOCOSD");
			String strUrl = urlBase + "/grupo/" + grupo + "/empresa/" + empresa.getIdCamposDealer() + "/ProdutoGrupo/" + codGrupoProd;
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
				throw new Exception(
						"Erro na requisição: " + conn.getResponseCode() + ": " + conn.getResponseMessage());
			}

			ProdutoGrupo[] dadosProduto = new Gson().fromJson(resposta, ProdutoGrupo[].class);
			GrupoProdutoDAO.salvaIdCamposDealer(codGrupoProd, empresa.getCodemp(), new BigDecimal(dadosProduto[0].getIdProdutoGrupo()));

			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
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


	public static ArrayList<EmpresaCDxEmpresaSNK> buscarEmpresasSincronia(BigDecimal codGrupoProd) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		ArrayList<EmpresaCDxEmpresaSNK> ids = new ArrayList<EmpresaCDxEmpresaSNK>();
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql(
					"SELECT EMP.AD_IDCAMPOSDEALER, EMP.CODEMP FROM AD_GRUEMPCD GRU, TSIEMP EMP WHERE EMP.CODEMP = GRU.CODEMP AND CODGRUPOPROD = "
							+ codGrupoProd);
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			while (rset.next()) {
				EmpresaCDxEmpresaSNK emp = new EmpresaCDxEmpresaSNK();
				emp.setIdCamposDealer(rset.getString("AD_IDCAMPOSDEALER"));
				emp.setCodemp(rset.getBigDecimal("CODEMP"));
				ids.add(emp);
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
		return ids;
	}
	
	public static BigDecimal buscarIdSalvo(BigDecimal codGrupoProd, BigDecimal codemp) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal id = BigDecimal.ZERO;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT IDCAMPOSDEALER FROM AD_IDCDGRU WHERE CODGRUPOPROD = "
					+ codGrupoProd + " AND CODEMP = " + codemp);
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			if (rset.next()) {
				id = rset.getBigDecimal("IDCAMPOSDEALER");
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
		return id;
	}

	public static BigDecimal buscarIdCamposDealerGrupo(BigDecimal codGrupoProd, BigDecimal codemp) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal id = BigDecimal.ZERO;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT IDCAMPOSDEALER FROM AD_IDCDGRU WHERE CODGRUPOPROD = "
					+ codGrupoProd + " AND CODEMP = " + codemp);
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			if (rset.next()) {
				id = rset.getBigDecimal("IDCAMPOSDEALER");
			} else {
				throw new Exception("Erro: Grupo de Produto vinculado não está integrado ao CampOS Dealer!");
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
		return id;
	}
}
