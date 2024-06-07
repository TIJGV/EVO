package br.com.evonetwork.integracaoCamposDealer.Controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.activiti.engine.impl.util.json.JSONObject;

import com.google.gson.Gson;
import com.sankhya.util.JdbcUtils;
import com.sankhya.util.TimeUtils;

import br.com.evonetwork.integracaoCamposDealer.DAO.ProdutoDAO;
import br.com.evonetwork.integracaoCamposDealer.Model.EmpresaCDxEmpresaSNK;
import br.com.evonetwork.integracaoCamposDealer.Model.Produto;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.modelcore.util.MGECoreParameter;

public class ProdutoController {

	public static void integraProduto(Registro r, ContextoAcao ca) throws Exception {

		BigDecimal codProd = (BigDecimal) r.getCampo("CODPROD");
		String descrProd = (String) r.getCampo("DESCRPROD");
		BigDecimal codMarca = (BigDecimal) r.getCampo("CODMARCA") == null ? BigDecimal.ZERO
				: (BigDecimal) r.getCampo("CODMARCA");
		BigDecimal codModelo = (BigDecimal) r.getCampo("AD_CODMODVEI") == null ? BigDecimal.ZERO
				: (BigDecimal) r.getCampo("AD_CODMODVEI");
		BigDecimal codGrupoProd = (BigDecimal) r.getCampo("CODGRUPOPROD");
		String ativo = (String) r.getCampo("ATIVO");

		if (codMarca.equals(BigDecimal.ZERO)) {
			throw new Exception("Para integrar com o Campos Dealer a marca deve ser informada");
		}

		if (codModelo.equals(BigDecimal.ZERO)) {
			throw new Exception("Para integrar com o Campos Dealer o modelo deve ser informada");
		}

		BigDecimal vlrVenda = buscarValorVenda(codProd);

		int fAtivo = ativo.equals("S") ? 1 : 0;

		Timestamp dtatual = TimeUtils.getNow();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String dtFormat = format.format(dtatual);

		ArrayList<EmpresaCDxEmpresaSNK> empresas = GrupoProdutoController.buscarEmpresasSincronia(codGrupoProd);

		if (empresas.size() == 0) {
			throw new Exception(
					"Erro: É necessário cadastrar empresas para sincronia na aba \"Grupo x Empresas Campos Dealer\" da tela Grupo de Produtos.");
		}

		ArrayList<String> empresasJaSincronizadas = new ArrayList<String>();
		ArrayList<String> empresasSincronizadas = new ArrayList<String>();

		for (EmpresaCDxEmpresaSNK empresa : empresas) {

			try {

				BigDecimal idCodGrupoProd = GrupoProdutoController.buscarIdCamposDealerGrupo(codGrupoProd,
						empresa.getCodemp());
				if (idCodGrupoProd == null) {
					throw new Exception("Grupo de produto vinculado não sincronizado com Campos Dealer");
				}

				MarcaController.integraMarca(ca, empresas, codMarca);
				BigDecimal idCodMarca = MarcaController.buscarIdCamposDealerMarca(codMarca, empresa.getCodemp());

				ModeloController.integraModelo(ca, empresas, codModelo, codMarca);
				BigDecimal idCodModelo = ModeloController.buscarIdCamposDealerModelo(codModelo, empresa.getCodemp());

				String urlBase = MGECoreParameter.getParameterAsString("URLBASECOSD");
				int grupo = MGECoreParameter.getParameterAsInt("GRUPOCOSD");
				String strUrl = urlBase + "/grupo/" + grupo + "/empresa/" + empresa.getIdCamposDealer() + "/Produto/"
						+ codProd;
				String auth = "BASIC " + MGECoreParameter.getParameterAsString("AUTHCOSD");

				URL url = new URL(strUrl);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("POST");
				conn.setDoOutput(true);
				conn.setRequestProperty("Authorization", auth);
				conn.setRequestProperty("Content-Type", "application/json");
				conn.setRequestProperty("Accept", "application/json");

				JSONObject json = new JSONObject();
				json.put("codProduto", codProd);
				json.put("vlrProduto", vlrVenda);
				json.put("dscProduto", descrProd);
				json.put("idProdutoGrupo", idCodGrupoProd);
				json.put("codProdutoGrupo", codGrupoProd);
				json.put("idProdutoMarca", idCodMarca);
				json.put("codProdutoMarca", codMarca.toString());
				json.put("idProdutoModelo", idCodModelo);
				json.put("codProdutoModelo", codModelo.toString());
				json.put("dthRegistro", dtFormat);
				json.put("fAtivo", fAtivo);

				System.out.println("INTEGRAÇÃO PRODUTO");
				System.out.println(json);

				String body = json.toString();
				System.out.println("Body: " + body);
				byte[] out = body.getBytes(StandardCharsets.UTF_8);

				OutputStream stream = conn.getOutputStream();
				stream.write(out);

				String resposta = null;

				try {
					resposta = getResponseBody(conn);

					JSONObject jsonResp = new JSONObject(resposta);
					ProdutoDAO.salvaIdCamposDealer(codProd, empresa.getCodemp(),
							new BigDecimal(jsonResp.getInt("idProduto")));
					empresasSincronizadas.add(empresa.getIdCamposDealer());

				} catch (Exception e) {
					try {
						resposta = getErrorStream(conn);
					} catch (Exception e1) {
						System.out.println("Nenhum ErrorStream retornado: " + e1.getMessage());
					}
					if (conn.getResponseCode() == 409) {
						empresasJaSincronizadas.add(empresa.getIdCamposDealer());
						BigDecimal id = buscarIdCamposDealer(codProd, empresa.getCodemp());
						if (id.equals(BigDecimal.ZERO)) {
							coletarIdCamposDealer(codProd, empresa);
						}
						continue;
					}
				}
				conn.disconnect();

			} catch (Exception ex) {
				ex.printStackTrace();
				throw new Exception(ex.getMessage());
			}
		}
		StringBuffer mensagem = new StringBuffer();

		if (empresasJaSincronizadas.size() > 0) {
			mensagem.append("Produto já sincronizado para a(s) empresa(s) " + empresasJaSincronizadas.toString());
			mensagem.append(System.getProperty("line.separator"));
		}

		if (empresasSincronizadas.size() > 0) {
			mensagem.append(
					"Produto sincronizado com sucesso para a(s) empresa(s) " + empresasSincronizadas.toString());
		}

		ca.setMensagemRetorno(mensagem.toString());
	}
	
	public static void coletarIdCamposDealer(BigDecimal codprod, EmpresaCDxEmpresaSNK empresa) throws Exception {
		try {
			String urlBase = MGECoreParameter.getParameterAsString("URLBASECOSD");
			int grupo = MGECoreParameter.getParameterAsInt("GRUPOCOSD");
			String strUrl = urlBase + "/grupo/" + grupo + "/empresa/" + empresa.getIdCamposDealer() + "/Produto/"
					+ codprod;
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

			Produto[] dadosProduto = new Gson().fromJson(resposta, Produto[].class);
			ProdutoDAO.salvaIdCamposDealer(codprod, empresa.getCodemp(), new BigDecimal(dadosProduto[0].getIdProduto()));

			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	public static BigDecimal buscarValorVenda(BigDecimal codProd) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal vlrVenda = BigDecimal.ZERO;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT VLRVENDA FROM TGFTAB TAB, TGFEXC EXC " + "WHERE TAB.NUTAB = EXC.NUTAB "
					+ "AND TAB.DTVIGOR = (SELECT MAX(DTVIGOR) FROM TGFTAB T, TGFEXC E WHERE E.NUTAB = T.NUTAB AND E.CODPROD = EXC.CODPROD) "
					+ "AND EXC.CODPROD = " + codProd);
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			if (rset.next()) {
				vlrVenda = rset.getBigDecimal("VLRVENDA");
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
		return vlrVenda;
	}

	public static BigDecimal buscarIdCamposDealer(BigDecimal codProd, BigDecimal codEmp) throws Exception {
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

			sql.appendSql("SELECT IDCAMPOSDEALER FROM AD_IDCDPRO WHERE CODPROD = " + codProd + " AND CODEMP = " + codEmp);
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
