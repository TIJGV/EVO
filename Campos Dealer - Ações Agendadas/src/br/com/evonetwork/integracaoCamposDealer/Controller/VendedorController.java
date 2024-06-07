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
import java.util.ArrayList;

import org.activiti.engine.impl.util.json.JSONObject;
import org.cuckoo.core.ScheduledActionContext;

import com.google.gson.Gson;
import com.sankhya.util.JdbcUtils;

import br.com.evonetwork.integracaoCamposDealer.DAO.ProspectDAO;
import br.com.evonetwork.integracaoCamposDealer.DAO.VendedorDAO;
import br.com.evonetwork.integracaoCamposDealer.Model.RegistrosAlterados;
import br.com.evonetwork.integracaoCamposDealer.Model.Usuario;
import br.com.evonetwork.integracaoCamposDealer.Model.DadosCliente;
import br.com.evonetwork.integracaoCamposDealer.Utils.Utils;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.modelcore.util.MGECoreParameter;

public class VendedorController {

	public static void iniciarBusca(ScheduledActionContext ctx) throws Exception {
		System.out.println("-----Iniciando a busca de usuarios-----");
		ArrayList<String> codEmp = Utils.buscarCodigoEmpresa();

		for (String empresa : codEmp) {
			try {
				String urlBase = MGECoreParameter.getParameterAsString("URLBASECOSD");
				int grupo = MGECoreParameter.getParameterAsInt("GRUPOCOSD");
				String strUrl = urlBase + "/grupo/" + grupo + "/empresa/" + empresa + "/usuario/0";
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
						System.out.println("Nenhum usuario encontrado.");
						ctx.info("Nenhum usuario encontrado.");
						continue;
					} else {
						System.out.println("Erro na requisição!");
						System.out
								.println("Resposta HTTP: " + conn.getResponseCode() + " " + conn.getResponseMessage());
					}
					throw new Exception(
							"Erro na requisição: " + conn.getResponseCode() + ": " + conn.getResponseMessage());
				}

				System.out.println("RESPOSTA: " + resposta.toString());

				Usuario[] usuarios = new Gson().fromJson(resposta, Usuario[].class);
				System.out.println("Resposta HTTP: " + conn.getResponseCode() + " " + conn.getResponseMessage());

				for (Usuario u : usuarios) {
					System.out.println(u.toString());
					System.out.println("Iniciando integração do vendedor '" + u.getCodUsuario());
					String codUsuario = u.getCodUsuario();
					BigDecimal idUsuario = new BigDecimal(u.getIdUsuario());
					
					if(codUsuario == null || vendedorExistenteNoSankhya(idUsuario, codUsuario)) {
						continue;
					}
					
					VendedorDAO.atualizarVendedor(u);
					
				}

				conn.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception(e.getMessage());
			}
		}
		System.out.println("-----Finalizando a busca de usuarios-----");
	}

	private static boolean vendedorExistenteNoSankhya(BigDecimal idUsuario, String codUsuario) throws Exception {
		System.out.println("Buscando vendedor no Sankhya");
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

			sql.appendSql("SELECT CODVEND FROM TGFVEN WHERE AD_IDCAMPOSDEALER = " + idUsuario + " AND CODVEND = " + codUsuario);
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			if (rset.next()) {
				return true;
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
		return false;
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
