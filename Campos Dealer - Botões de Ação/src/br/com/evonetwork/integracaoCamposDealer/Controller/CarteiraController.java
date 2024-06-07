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
import com.sankhya.util.StringUtils;
import com.sankhya.util.TimeUtils;

import br.com.evonetwork.integracaoCamposDealer.DAO.RelacaoCarteirasDAO;
import br.com.evonetwork.integracaoCamposDealer.Model.CarteiraCliente;
import br.com.evonetwork.integracaoCamposDealer.Model.CodSankhyaXIdCamposDealer;
import br.com.evonetwork.integracaoCamposDealer.Utils.Utils;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.modelcore.util.MGECoreParameter;

public class CarteiraController {
	public static void enviaCarteira(CarteiraCliente carteira, ContextoAcao ca) throws Exception {
		ArrayList<String> codEmp = Utils.buscarCodigoEmpresa();

		for (String empresa : codEmp) {
			try {
				String urlBase = MGECoreParameter.getParameterAsString("URLBASECOSD");
				int grupo = MGECoreParameter.getParameterAsInt("GRUPOCOSD");
				String strUrl = urlBase + "/grupo/" + grupo + "/empresa/" + empresa + "/usuario/"
						+ carteira.getCodUsuario() + "/carteiracliente/create";
				String auth = "BASIC " + MGECoreParameter.getParameterAsString("AUTHCOSD");

				URL url = new URL(strUrl);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("POST");
				conn.setDoOutput(true);
				conn.setRequestProperty("Authorization", auth);
				conn.setRequestProperty("Content-Type", "application/json");
				conn.setRequestProperty("Accept", "application/json");

				Timestamp dtatual = TimeUtils.getNow();
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				String dtFormat = format.format(dtatual);

				JSONObject json = new JSONObject();
				json.put("idCliente", StringUtils.getNullAsEmpty(carteira.getIdCliente()));
				json.put("CodigoCliente", StringUtils.getNullAsEmpty(carteira.getCodigoCliente()));
				json.put("idUsuario", StringUtils.getNullAsEmpty(carteira.getIdUsuario()));
				json.put("codUsuario", StringUtils.getNullAsEmpty(carteira.getCodUsuario()));
				json.put("dthRegistro", dtFormat);

				System.out.println("INTEGRAÇÃO CARTEIRA");
				System.out.println(json);

				String body = json.toString();
				System.out.println("Body: " + body);
				byte[] out = body.getBytes(StandardCharsets.UTF_8);

				OutputStream stream = conn.getOutputStream();
				stream.write(out);

				String resposta = null;

				try {
					resposta = getResponseBody(conn);
					System.out.println("Carteira enviada com sucesso. Status de envio: " + resposta);
					ca.setMensagemRetorno("Carteira enviada com sucesso!");
				} catch (Exception e) {
					try {
						resposta = getErrorStream(conn);
					} catch (Exception e1) {
						System.out.println("Nenhum ErrorStream retornado: " + e1.getMessage());
					}
					System.out.println("Erro na requisiÃ§Ã£o!");
					System.out.println("Resposta HTTP: " + conn.getResponseCode() + " " + conn.getResponseMessage());
					throw new Exception(
							"Erro na requisiÃ§Ã£o: " + conn.getResponseCode() + ": " + conn.getResponseMessage());
				}
				System.out.println("Resposta HTTP: " + conn.getResponseCode() + " " + conn.getResponseMessage());
				System.out.println("Resposta: " + resposta);

				conn.disconnect();

			} catch (Exception ex) {
				ca.setMensagemRetorno(ex.getMessage());
			}
		}
	}

	public static void coletarDadosDaCarteira(BigDecimal codvend) throws Exception {
		System.out.println("---Iniciando coleta de dados da carteira---");
		ArrayList<String> codEmp = Utils.buscarCodigoEmpresa();

		for (String empresa : codEmp) {
			try {
				String urlBase = MGECoreParameter.getParameterAsString("URLBASECOSD");
				int grupo = MGECoreParameter.getParameterAsInt("GRUPOCOSD");
				String strUrl = urlBase + "/grupo/" + grupo + "/empresa/" + empresa + "/usuario/" + codvend
						+ "/carteiracliente";
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
					System.out.println("RETORNO CARTEIRA: \n" + resposta);
				} catch (Exception e) {
					try {
						resposta = getErrorStream(conn);
					} catch (Exception e1) {
						System.out.println("Nenhum ErrorStream retornado: " + e1.getMessage());
					}
					System.out.println("Erro na requisição!");
					System.out.println("Resposta HTTP: " + conn.getResponseCode() + " " + conn.getResponseMessage());
					throw new Exception(
							"Erro na requisição: " + conn.getResponseCode() + ": " + conn.getResponseMessage());
				}

				CarteiraCliente[] dadosCarteira = new Gson().fromJson(resposta, CarteiraCliente[].class);

				for (CarteiraCliente c : dadosCarteira) {
					RelacaoCarteirasDAO.criarRelacaoCarteira(c);
				}

				conn.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception(e.getMessage());
			}
			System.out.println("---Finalizando coleta de dados da carteira---");
		}
	}

	public static ArrayList<CodSankhyaXIdCamposDealer> buscarClientesCarteira(BigDecimal codCarteira) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;

		ArrayList<CodSankhyaXIdCamposDealer> clientes = new ArrayList<CodSankhyaXIdCamposDealer>();

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT" + "    PAP.AD_IDCAMPOSDEALER," + "    PAP.CODPAP," + "    CPAR.CODPARC " + "FROM"
					+ "    AD_CARTEIRACLIENTE CPAR," + "    TCSPAP PAP " + "WHERE" + "    PAP.CODPARC = CPAR.CODPARC"
					+ "    AND CPAR.CODCARTEIRA = " + codCarteira);
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			while (rset.next()) {

				BigDecimal idCamposDealer = rset.getBigDecimal("AD_IDCAMPOSDEALER");
				BigDecimal codpap = rset.getBigDecimal("CODPAP");

				if (idCamposDealer == null) {
					JdbcUtils.closeResultSet(rset);
					NativeSql.releaseResources(sql);
					JdbcWrapper.closeSession(jdbc);
					JapeSession.close(hnd);
					throw new Exception("Parceiro " + codpap + " não integrado ao Campos Dealer");
				}

				CodSankhyaXIdCamposDealer cliente = new CodSankhyaXIdCamposDealer();
				cliente.setIdCamposDealer(idCamposDealer);
				cliente.setCodSankhya(codpap);

				clientes.add(cliente);
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
		return clientes;
	}

	public static BigDecimal buscarCodigoCarteira(BigDecimal codvend, BigDecimal codparc) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;

		BigDecimal carteira = BigDecimal.ZERO;

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT " + "    CCLI.CODCARTEIRA  " + "FROM " + "    AD_CARTEIRACLIENTE CPAR, "
					+ "    AD_ADCARTEIRAVEN CVEN, " + "    AD_CARTEIRAPAR CCLI " + "     " + "WHERE "
					+ "    CVEN.CODCARTEIRA = CCLI.CODCARTEIRA " + "    AND CCLI.CODCARTEIRA = CPAR.CODCARTEIRA "
					+ "    AND CVEN.CODVEND =  " + codvend + "    AND CPAR.CODPARC = " + codparc);
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			if (rset.next()) {
				carteira = rset.getBigDecimal("CODCARTEIRA");

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
		return carteira;
	}
	
	public static boolean carteiraExiste(BigDecimal idcarteira) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;

		boolean existe = false;

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT * FROM AD_RELACAOCARTEIRA WHERE IDCARTEIRA = " + idcarteira);
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			if (rset.next()) {
				existe = true;

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
		return existe;
	}

	public static boolean vendedorExiste(BigDecimal codvend) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;

		boolean existe = false;

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT * FROM TGFVEN WHERE CODVEND =  " + codvend);
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			if (rset.next()) {
				existe = true;

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
		return existe;
	}

	public static boolean parceiroExiste(BigDecimal codparc) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;

		boolean existe = false;

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT * FROM TGFPAR WHERE CODPARC = " + codparc);
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			if (rset.next()) {
				existe = true;

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
		return existe;
	}

	public static ArrayList<CodSankhyaXIdCamposDealer> buscarVendedoresCarteira(BigDecimal codCarteira)
			throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;

		ArrayList<CodSankhyaXIdCamposDealer> vendedores = new ArrayList<CodSankhyaXIdCamposDealer>();

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT" + "    VEN.AD_IDCAMPOSDEALER," + "    VEN.CODVEND " + "FROM"
					+ "    AD_ADCARTEIRAVEN CVEN," + "    TGFVEN VEN " + "WHERE" + "    VEN.CODVEND = CVEN.CODVEND"
					+ "    AND VEN.TIPVEND = 'V'" + "    AND CVEN.CODCARTEIRA = " + codCarteira);
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			while (rset.next()) {

				BigDecimal idCamposDealer = rset.getBigDecimal("AD_IDCAMPOSDEALER");
				BigDecimal codvend = rset.getBigDecimal("CODVEND");

				if (idCamposDealer == null) {
					JdbcUtils.closeResultSet(rset);
					NativeSql.releaseResources(sql);
					JdbcWrapper.closeSession(jdbc);
					JapeSession.close(hnd);
					throw new Exception("Vendedor " + codvend + " não integrado ao Campos Dealer");
				}

				CodSankhyaXIdCamposDealer vendedor = new CodSankhyaXIdCamposDealer();
				vendedor.setIdCamposDealer(idCamposDealer);
				vendedor.setCodSankhya(codvend);

				vendedores.add(vendedor);
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
		return vendedores;
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

	public static BigDecimal buscarCodParc(BigDecimal codpap) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;

		BigDecimal codparc = BigDecimal.ZERO;

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT CODPARC FROM TCSPAP WHERE CODPAP =  " + codpap );
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			if (rset.next()) {
				codparc = rset.getBigDecimal("CODPARC");

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
		return codparc;
	}
}
