package br.com.evonetwork.integracaoCamposDealer.Controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.sankhya.util.JdbcUtils;

import br.com.evonetwork.integracaoCamposDealer.DAO.CarteiraDAO;
import br.com.evonetwork.integracaoCamposDealer.DAO.RelacaoCarteirasDAO;
import br.com.evonetwork.integracaoCamposDealer.Model.CarteiraCliente;
import br.com.evonetwork.integracaoCamposDealer.Utils.Utils;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.modelcore.util.MGECoreParameter;

public class CarteiraController {

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
					
					CarteiraDAO.criarCarteira(c);
					
					if(relacaoCarteiraExiste(new BigDecimal(c.getIdCarteiraCliente()))) {
						RelacaoCarteirasDAO.atualizarRelacaoCarteira(c);
					} else {
						RelacaoCarteirasDAO.criarRelacaoCarteira(c);
					}
				}

				conn.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception(e.getMessage());
			}
			System.out.println("---Finalizando coleta de dados da carteira---");
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
	
	public static ArrayList<BigDecimal> buscarVendedores() throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;

		ArrayList<BigDecimal> vendedores = new ArrayList<BigDecimal>();

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT CODVEND FROM TGFVEN WHERE AD_IDCAMPOSDEALER IS NOT NULL AND TIPVEND = 'V'");
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			if (rset.next()) {
				vendedores.add(rset.getBigDecimal("CODVEND"));

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
	
	public static BigDecimal buscarCodigoCarteira(BigDecimal codvend) throws Exception {
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

				sql.appendSql("SELECT DISTINCT "
						+ "    CPAR.CODCARTEIRA   "
						+ "FROM      "
						+ "    AD_CARTEIRACLIENTE CPAR,      "
						+ "    AD_ADCARTEIRAVEN CVEN "
						+ "WHERE      "
						+ "    CVEN.CODCARTEIRA = CPAR.CODCARTEIRA      "
						+ "    AND CVEN.CODVEND = " + codvend );
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

	public static boolean relacaoCarteiraExiste(BigDecimal idcarteira) throws Exception {
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
	
	public static boolean parceiroExisteNaCarteira(BigDecimal codparc, BigDecimal codCarteira) throws Exception {
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

			sql.appendSql("SELECT DISTINCT "
					+ "    CPAR.CODCARTEIRA   "
					+ "FROM      "
					+ "    AD_CARTEIRACLIENTE CPAR,      "
					+ "    AD_CARTEIRACLIENTE CCLI "
					+ "WHERE      "
					+ "    CCLI.CODCARTEIRA = CPAR.CODCARTEIRA      "
					+ "    AND CCLI.CODPARC = " + codparc
					+ "    AND CPAR.CODCARTEIRA = " + codCarteira);
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

			sql.appendSql("SELECT CODPARC FROM TCSPAP WHERE CODPAP =  " + codpap);
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
