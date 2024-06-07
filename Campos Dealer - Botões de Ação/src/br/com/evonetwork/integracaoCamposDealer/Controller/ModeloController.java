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

import br.com.evonetwork.integracaoCamposDealer.DAO.ModeloDAO;
import br.com.evonetwork.integracaoCamposDealer.Model.EmpresaCDxEmpresaSNK;
import br.com.evonetwork.integracaoCamposDealer.Model.ProdutoModelo;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.modelcore.util.MGECoreParameter;

public class ModeloController {

	public static void integraModelo(ContextoAcao ca, ArrayList<EmpresaCDxEmpresaSNK> empresas, BigDecimal codModVei, BigDecimal codMarca) throws Exception {

		String descrModVei = buscarDescricao(codModVei);
		Timestamp dtatual = TimeUtils.getNow();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String dtFormat = format.format(dtatual);
		
		
		for (EmpresaCDxEmpresaSNK empresa : empresas) {
			
		try {
			String urlBase = MGECoreParameter.getParameterAsString("URLBASECOSD");
			int grupo = MGECoreParameter.getParameterAsInt("GRUPOCOSD");
			String strUrl = urlBase + "/grupo/" + grupo + "/empresa/" + empresa.getIdCamposDealer() + "/ProdutoModelo/" + codModVei;
			String auth = "BASIC " + MGECoreParameter.getParameterAsString("AUTHCOSD");

			URL url = new URL(strUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Authorization", auth);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept", "application/json");

			BigDecimal idCamposDealerMarca = MarcaController.buscarIdCamposDealerMarca(codMarca, empresa.getCodemp());
			
			PrintStream printStream = new PrintStream(conn.getOutputStream());

			JSONObject json = new JSONObject();
			json.put("codProdutoModelo", codModVei.toString());
			json.put("idProdutoMarca", idCamposDealerMarca);
			json.put("codProdutoMarca", codMarca.toString());
			json.put("dscModelo", descrModVei);
			json.put("dthRegistro", dtFormat);

			System.out.println("INTEGRAÇÃO MODELO PRODUTO");
			System.out.println(json);

			printStream.println(json);

			conn.connect(); // envia para o servidor
			
			if (conn.getResponseCode() == 409) {
				BigDecimal id = buscarIdCamposDealerModelo(codModVei, empresa.getCodemp());
				if (id.equals(BigDecimal.ZERO)) {
					coletarIdCamposDealer(codModVei, empresa);
				}
				continue;
			}

			Scanner scanner = new Scanner(conn.getInputStream());
			String jsonDeResposta = "";
			
			while(scanner.hasNext()) {
				jsonDeResposta = jsonDeResposta + scanner.next();
			}

			printStream.println(jsonDeResposta);
			System.out.println(jsonDeResposta);
			
			if (conn.getResponseCode() == 201) {
				ca.setMensagemRetorno("Modelo integrado com sucesso! \n" + jsonDeResposta);
			}

			if (conn.getResponseCode() != 201) {
				throw new Exception(jsonDeResposta);
			}

			JSONObject jsonResp = new JSONObject(jsonDeResposta);
			ModeloDAO.salvaIdCamposDealer(codModVei, empresa.getCodemp(), new BigDecimal(jsonResp.getInt("idProdutoModelo")));

			conn.disconnect();
			scanner.close();

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception(ex.getMessage());
		}
		}
	}
	
	public static void coletarIdCamposDealer(BigDecimal codModVei, EmpresaCDxEmpresaSNK empresa) throws Exception {
		try {
			String urlBase = MGECoreParameter.getParameterAsString("URLBASECOSD");
			int grupo = MGECoreParameter.getParameterAsInt("GRUPOCOSD");
			String strUrl = urlBase + "/grupo/" + grupo + "/empresa/" + empresa.getIdCamposDealer() + "/ProdutoModelo/"
					+ codModVei;
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
				throw new Exception("Erro na requisição: " + conn.getResponseCode() + ": " + conn.getResponseMessage());
			}

			ProdutoModelo[] dadosProduto = new Gson().fromJson(resposta, ProdutoModelo[].class);
			ModeloDAO.salvaIdCamposDealer(codModVei, empresa.getCodemp(), new BigDecimal(dadosProduto[0].getIdProdutoModelo()));

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

	
	public static BigDecimal buscarIdCamposDealerModelo(BigDecimal codModVei, BigDecimal codEmp) throws Exception {
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

			sql.appendSql("SELECT IDCAMPOSDEALER FROM AD_IDCDMOD WHERE NROUNICO = " + codModVei + " AND CODEMP = " + codEmp);
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
	
	public static String buscarDescricao(BigDecimal codMod) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		String descricao = "";
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT DESCRICAO FROM AD_MODELOVEI WHERE NROUNICO = "
					+ codMod );
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			if (rset.next()) {
				descricao = rset.getString("DESCRICAO");
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
		return descricao;
	}

}
