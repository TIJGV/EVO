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
import br.com.evonetwork.integracaoCamposDealer.Model.RegistrosAlterados;
import br.com.evonetwork.integracaoCamposDealer.Model.DadosCliente;
import br.com.evonetwork.integracaoCamposDealer.Utils.Utils;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.modelcore.util.MGECoreParameter;

public class ProspectController {

	public static void iniciarBusca(ScheduledActionContext ctx) throws Exception {
		System.out.println("-----Iniciando a busca de clientes que foram alterados-----");
		ArrayList<String> codEmp = Utils.buscarCodigoEmpresa();

		for (String empresa : codEmp) {
			try {
				String urlBase = MGECoreParameter.getParameterAsString("URLBASECOSD");
				int grupo = MGECoreParameter.getParameterAsInt("GRUPOCOSD");
				String strUrl = urlBase + "/grupo/" + grupo + "/empresa/" + empresa + "/CRMretorno/1/0";
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
						System.out.println("Nenhum cliente pendente para atualizar ou requisição está incorreta.");
						ctx.info("Nenhum cliente pendente para atualizar ou requisição está incorreta.");
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

				RegistrosAlterados[] clientesAlterados = new Gson().fromJson(resposta, RegistrosAlterados[].class);
				System.out.println("Resposta HTTP: " + conn.getResponseCode() + " " + conn.getResponseMessage());

				for (RegistrosAlterados clienteAlterado : clientesAlterados) {
					String codCliente = clienteAlterado.getIdCamposDealer();
					Timestamp dataDaAlteracao = Utils.convertDate(clienteAlterado.getDthRegistro());
					System.out.println("Foi encontrado o cliente " + codCliente + " com a data de alteração: "
							+ dataDaAlteracao.toString());
					coletarDadosDoCliente(codCliente, dataDaAlteracao, clienteAlterado, empresa);
				}

				conn.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception(e.getMessage());
			}
		}
		System.out.println("-----Finalizando a busca de clientes que foram alterados-----");
	}

	private static void coletarDadosDoCliente(String codCliente, Timestamp dataDaAlteracao,
			RegistrosAlterados clienteAlterado, String empresa) throws Exception {
		System.out.println("---Iniciando coleta de dados do cliente---");
		try {
			String urlBase = MGECoreParameter.getParameterAsString("URLBASECOSD");
			int grupo = MGECoreParameter.getParameterAsInt("GRUPOCOSD");
			String strUrl = urlBase + "/grupo/" + grupo + "/empresa/" + empresa + "/cliente/0/" + codCliente;
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
				System.out.println("Erro na requisição!");
				System.out.println("Resposta HTTP: " + conn.getResponseCode() + " " + conn.getResponseMessage());
				throw new Exception(
						"Erro na requisição: " + conn.getResponseCode() + ": " + conn.getResponseMessage());
			}

			DadosCliente dadosCliente = new Gson().fromJson(resposta, DadosCliente.class);
			System.out.println("Resposta HTTP: " + conn.getResponseCode() + " " + conn.getResponseMessage());

//	        if(ehProspect(dadosCliente)) {
			System.out.println("Iniciando integração do cliente '" + dadosCliente.getNome() + "' do CPF/CNPJ: '"
					+ dadosCliente.getCNPJ_CPF() + "'");
			criarAtualizarProspectNoSankhya(dadosCliente, dataDaAlteracao);
			System.out.println("Alterando cliente para 'Processado' no CampOS Dealer");
			salvarCodPapCamposDealer(dadosCliente, empresa);
			alterarClienteParaProcessado(clienteAlterado, empresa);
//	        }

			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		System.out.println("---Finalizando coleta de dados do cliente---");
	}

	private static void alterarClienteParaProcessado(RegistrosAlterados clienteAlterado, String empresa)
			throws Exception {
		System.out.println("Alterando cliente para processado");
		try {
			String urlBase = MGECoreParameter.getParameterAsString("URLBASECOSD");
			int grupo = MGECoreParameter.getParameterAsInt("GRUPOCOSD");
			String strUrl = urlBase + "/grupo/" + grupo + "/empresa/" + empresa + "/CRMretorno/1/"
					+ clienteAlterado.getIdCamposDealer();
			String auth = "BASIC " + MGECoreParameter.getParameterAsString("AUTHCOSD");
			System.out.println("URL: " + strUrl + "\n Auth: " + auth);
			URL url = new URL(strUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("PUT");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Authorization", auth);

			String body = "{\r\n" + "  \"idCRMRetorno\": " + clienteAlterado.getIdCRMRetorno() + ",\r\n"
					+ "  \"idCRMRetornoTipo\": " + clienteAlterado.getIdCRMRetornoTipo() + ",\r\n"
					+ "  \"dscCRMRetornoTipo\": \"" + clienteAlterado.getDscCRMRetornoTipo() + "\",\r\n"
					+ "  \"endPoint\": \"" + clienteAlterado.getEndPoint() + "\",\r\n" + "  \"idCamposDealer\": \""
					+ clienteAlterado.getIdCamposDealer() + "\",\r\n" + "  \"CodigoIntegracao\": \""
					+ clienteAlterado.getCodigoIntegracao() + "\",\r\n" + "  \"idEmpresa\": \""
					+ clienteAlterado.getIdEmpresa() + "\",\r\n" + "  \"fStatus\": 2,\r\n" + "  \"dthRegistro\": \""
					+ clienteAlterado.getDthRegistro() + "\"\r\n" + "}";
			System.out.println("Body: " + body);
			byte[] out = body.getBytes(StandardCharsets.UTF_8);

			OutputStream stream = conn.getOutputStream();
			stream.write(out);

			String resposta = null;
			try {
				resposta = getResponseBody(conn);
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
			System.out.println("Resposta HTTP: " + conn.getResponseCode() + " " + conn.getResponseMessage());
			System.out.println("Resposta: " + resposta);
			// TODO verificar se tem algum dado para retornar na resposta
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Erro no alteração do cliente para 'Processado': " + e.getMessage());
		}
	}

	@SuppressWarnings("unused")
	private static boolean ehProspect(DadosCliente dadosCliente) {
		if (dadosCliente.getfProspect() == 1)
			return true;
		return false;
	}

	private static void criarAtualizarProspectNoSankhya(DadosCliente dadosCliente, Timestamp dataDaAlteracao)
			throws Exception {
		try {
			BigDecimal codProspect = buscarProspectExistenteNoSankhya(dadosCliente.getIdCliente());
			if (codProspect.compareTo(BigDecimal.ZERO) > 0) {
				ProspectDAO.atualizarProspect(codProspect, dadosCliente, dataDaAlteracao);
			} else {
				ProspectDAO.criarProspect(dadosCliente, dataDaAlteracao);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	private static void salvarCodPapCamposDealer(DadosCliente dadosCliente, String empresa) throws Exception{
		try {
			String urlBase = MGECoreParameter.getParameterAsString("URLBASECOSD");
			int grupo = MGECoreParameter.getParameterAsInt("GRUPOCOSD");
			String strUrl = urlBase + "/grupo/" + grupo + "/empresa/" + empresa + "/cliente/"
					+ dadosCliente.getIdCliente() + "/updateId";
			String auth = "BASIC " + MGECoreParameter.getParameterAsString("AUTHCOSD");
			System.out.println("URL: " + strUrl + "\n Auth: " + auth);
			URL url = new URL(strUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("PUT");
			conn.setDoOutput(true);
			conn.setRequestProperty("Authorization", auth);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept", "application/json");

			BigDecimal codProspect = buscarProspectExistenteNoSankhya(dadosCliente.getIdCliente());
			dadosCliente.setCodigoCliente(codProspect.toString());
			
			JSONObject json = new JSONObject();
			json.put("idCliente", dadosCliente.getIdCliente());
			json.put("CodigoCliente", dadosCliente.getCodigoCliente());
			json.put("codTipoCliente", dadosCliente.getCodTipoCliente());
			json.put("CNPJ_CPF", dadosCliente.getCNPJ_CPF());
			json.put("RG", dadosCliente.getRG());
			json.put("Nome", dadosCliente.getNome());
			json.put("Login", dadosCliente.getLogin());
			json.put("Senha", dadosCliente.getSenha());
			json.put("Endereco", dadosCliente.getEndereco());
			json.put("CEP", dadosCliente.getCEP());
			json.put("Telefone", dadosCliente.getTelefone());
			json.put("Telefone2", dadosCliente.getTelefone2());
			json.put("MailCliente", dadosCliente.getMailCliente());
			json.put("Lat", dadosCliente.getLat());
			json.put("Lon", dadosCliente.getLon());
			json.put("dthRegistro", dadosCliente.getDthRegistro());
			json.put("dthAbe", dadosCliente.getDthAbe());
			json.put("Cidade", dadosCliente.getCidade());
			json.put("Estado", dadosCliente.getEstado());
			json.put("Pais", dadosCliente.getPais());
			json.put("fProspect", dadosCliente.getfProspect());
			
			String body = json.toString();
			System.out.println("Body: " + body);
			byte[] out = body.getBytes(StandardCharsets.UTF_8);

			OutputStream stream = conn.getOutputStream();
			stream.write(out);

			String resposta = null;
			try {
				resposta = getResponseBody(conn);
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
			System.out.println("Resposta HTTP: " + conn.getResponseCode() + " " + conn.getResponseMessage());
			System.out.println("Resposta: " + resposta);
			
			conn.disconnect();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Erro ao salvar codCliente: " + e.getMessage());
		}
	}

	private static BigDecimal buscarProspectExistenteNoSankhya(int idCliente) throws Exception {
		System.out.println("Buscando Prospect no Sankhya");
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal codPap = BigDecimal.ZERO;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT CODPAP FROM TCSPAP WHERE AD_IDCAMPOSDEALER = " + idCliente);
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			if (rset.next()) {
				codPap = rset.getBigDecimal("CODPAP");
				System.out.println("Prospect encontrado: " + codPap);
			} else {
				System.out.println("Nenhum prospect encontrado!");
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
		return codPap;
	}

	public static BigDecimal buscarCttProspect(BigDecimal codpap) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal codContato = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT CODCONTATO FROM TCSCTT WHERE CODPAP = " + codpap);
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			if (rset.next()) {
				codContato = rset.getBigDecimal("CODCONTATO");
			} else {
				
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
		return codContato;
	}
	
	public static BigDecimal buscarCodVendProspect(BigDecimal codpap) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal codvend = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT CODVEND FROM TCSPAP WHERE CODPAP = " + codpap);
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			if (rset.next()) {
				codvend = rset.getBigDecimal("CODVEND");
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
		return codvend;
	}
	
	public static BigDecimal buscarCodPapPeloID(BigDecimal idCamposDealer) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal codpap = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT CODPAP FROM TCSPAP WHERE AD_IDCAMPOSDEALER = " + idCamposDealer);
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			if (rset.next()) {
				codpap = rset.getBigDecimal("CODPAP");
			} else {
				throw new Exception("Prospect n�o integrado com o Campos Dealer");
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
		return codpap;
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
