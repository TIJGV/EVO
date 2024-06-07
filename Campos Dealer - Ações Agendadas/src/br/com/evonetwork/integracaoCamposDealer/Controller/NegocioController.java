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

import org.cuckoo.core.ScheduledActionContext;

import com.google.gson.Gson;
import com.sankhya.util.JdbcUtils;
import com.sankhya.util.TimeUtils;

import br.com.evonetwork.integracaoCamposDealer.DAO.AcaoDAO;
import br.com.evonetwork.integracaoCamposDealer.DAO.NegociacaoDAO;
import br.com.evonetwork.integracaoCamposDealer.DAO.ProdutoNegociadoDAO;
import br.com.evonetwork.integracaoCamposDealer.Model.Acao;
import br.com.evonetwork.integracaoCamposDealer.Model.DadosCliente;
import br.com.evonetwork.integracaoCamposDealer.Model.DadosNegocio;
import br.com.evonetwork.integracaoCamposDealer.Model.InfoNegoc;
import br.com.evonetwork.integracaoCamposDealer.Model.NegocioXProduto;
import br.com.evonetwork.integracaoCamposDealer.Model.RegistrosAlterados;
import br.com.evonetwork.integracaoCamposDealer.Utils.Utils;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.util.JapeSessionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.modelcore.util.MGECoreParameter;

public class NegocioController {

	public static void iniciarBusca(ScheduledActionContext ctx) throws Exception {
		System.out.println("-----Iniciando a busca de negociações que foram alteradas-----");
		ArrayList<String> codEmp = Utils.buscarCodigoEmpresa();

		for (String empresa : codEmp) {
			try {
				String urlBase = MGECoreParameter.getParameterAsString("URLBASECOSD");
				int grupo = MGECoreParameter.getParameterAsInt("GRUPOCOSD");
				String strUrl = urlBase + "/grupo/" + grupo + "/empresa/" + empresa + "/CRMretorno/3/0";
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
						System.out.println("Nenhuma negociação pendente para atualizar ou requisição está incorreta.");
						ctx.info("Nenhuma negociação pendente para atualizar ou requisição está incorreta.");
						continue;
					} else {
						System.out.println("Erro na requisição!");
						System.out
								.println("Resposta HTTP: " + conn.getResponseCode() + " " + conn.getResponseMessage());
					}
//					throw new Exception(
//							"Erro na requisição: " + conn.getResponseCode() + ": " + conn.getResponseMessage());
				}

				RegistrosAlterados[] negociosAlterados = new Gson().fromJson(resposta, RegistrosAlterados[].class);
				System.out.println("Resposta HTTP: " + conn.getResponseCode() + " " + conn.getResponseMessage());

				for (RegistrosAlterados negocioAlterado : negociosAlterados) {
					String idNegocio = negocioAlterado.getIdCamposDealer();
					Timestamp dataDaAlteracao = Utils.convertDate(negocioAlterado.getDthRegistro());
					coletarDadosDaNegociacao(idNegocio, dataDaAlteracao, negocioAlterado, empresa, ctx);
				}

				conn.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception(e.getMessage());
			}
		}
		System.out.println("-----Finalizando a busca de negociações que foram alterados-----");
	}

	private static void coletarDadosDaNegociacao(String idNegocio, Timestamp dataDaAlteracao,
			RegistrosAlterados negocioAlterado, String empresa, ScheduledActionContext ctx) throws Exception {
		System.out.println("---Iniciando coleta de dados da negociação---");
		try {
			String urlBase = MGECoreParameter.getParameterAsString("URLBASECOSD");
			int grupo = MGECoreParameter.getParameterAsInt("GRUPOCOSD");
			String strUrl = urlBase + "/grupo/" + grupo + "/empresa/" + empresa + "/negocio/0/" + idNegocio;
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
//				throw new Exception("Erro na requisição: " + conn.getResponseCode() + ": " + conn.getResponseMessage());
			}

			DadosNegocio[] dadosNegocio = new Gson().fromJson(resposta, DadosNegocio[].class);
			System.out.println("Resposta HTTP: " + conn.getResponseCode() + " " + conn.getResponseMessage());

			String hasError = criarAtualizarNegocioNoSankhya(dadosNegocio[0], dataDaAlteracao, empresa, ctx);
			if (hasError != "") {
				throw new Exception("Erro ao criar/atualizar negociação: " + hasError);
			}
			alterarNegocioParaProcessado(negocioAlterado, empresa);

			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		System.out.println("---Finalizando coleta de dados da negociação---");
	}

	private static void alterarNegocioParaProcessado(RegistrosAlterados negocioAlterado, String empresa)
			throws Exception {
		System.out.println("Alterando cliente para processado");
		try {
			String urlBase = MGECoreParameter.getParameterAsString("URLBASECOSD");
			int grupo = MGECoreParameter.getParameterAsInt("GRUPOCOSD");
			String strUrl = urlBase + "/grupo/" + grupo + "/empresa/" + empresa + "/CRMretorno/3/"
					+ negocioAlterado.getIdCamposDealer();
			String auth = "BASIC " + MGECoreParameter.getParameterAsString("AUTHCOSD");
			System.out.println("URL: " + strUrl + "\n Auth: " + auth);
			URL url = new URL(strUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("PUT");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Authorization", auth);

			String body = "{ " + "  \"idCRMRetorno\": " + negocioAlterado.getIdCRMRetorno() + ", "
					+ "  \"idCRMRetornoTipo\": " + negocioAlterado.getIdCRMRetornoTipo() + ", "
					+ "  \"dscCRMRetornoTipo\": \"" + negocioAlterado.getDscCRMRetornoTipo() + "\", "
					+ "  \"endPoint\": \"" + negocioAlterado.getEndPoint() + "\", " + "  \"idCamposDealer\": \""
					+ negocioAlterado.getIdCamposDealer() + "\", " + "  \"CodigoIntegracao\": \""
					+ negocioAlterado.getCodigoIntegracao() + "\", " + "  \"idEmpresa\": \""
					+ negocioAlterado.getIdEmpresa() + "\", " + "  \"fStatus\": 2, " + "  \"dthRegistro\": \""
					+ negocioAlterado.getDthRegistro() + "\" " + "}";
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
				System.out.println("Erro na requisiÃ§Ã£o!");
				System.out.println("Resposta HTTP: " + conn.getResponseCode() + " " + conn.getResponseMessage());
//				throw new Exception(
//						"Erro na requisiÃ§Ã£o: " + conn.getResponseCode() + ": " + conn.getResponseMessage());
			}
			System.out.println("Resposta HTTP: " + conn.getResponseCode() + " " + conn.getResponseMessage());
			System.out.println("Resposta: " + resposta);
			// TODO verificar se tem algum dado para retornar na resposta
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Erro na alteração da negociação para 'Processado': " + e.getMessage());
		}
	}

	private static String criarAtualizarNegocioNoSankhya(DadosNegocio dadosnegocio, Timestamp dataDaAlteracao,
			String empresa, ScheduledActionContext ctx) throws Exception {
		try {
			BigDecimal numos = negociacaoJaIntegrada(dadosnegocio.getIdNegocio());
			BigDecimal nuitem = buscarItem(numos, dadosnegocio.getCodEtapaNegocio());
			BigDecimal codpap = ProspectController.buscarCodPapPeloID(new BigDecimal(dadosnegocio.getIdCliente()));
			
			
			if (numos.compareTo(BigDecimal.ZERO) > 0) {
				NegociacaoDAO.atualizaCabecalhoNegoc(numos, dadosnegocio, dataDaAlteracao);
				
				for (NegocioXProduto p : dadosnegocio.getLstNegocioXProdutos()) {
					if (produtoNegociadoJaIntegrado(p.getIdNegocioXProduto())) {
						ProdutoNegociadoDAO.atualizarProdutoNegociado(p, numos, codpap);
					} else {
						ProdutoNegociadoDAO.salvarProdutoNegociado(p, numos, codpap);
					}
				}

				if (nuitem.compareTo(BigDecimal.ZERO) > 0) {
					NegociacaoDAO.atualizaItensNegoc(dadosnegocio, dataDaAlteracao, numos, nuitem);
				} else {
					BigDecimal codvend = ProspectController
							.buscarCodVendProspect(codpap);
					fecharUltimaEtapa(numos, codvend );
					NegociacaoDAO.criaItensNegoc(dadosnegocio, dataDaAlteracao, numos, ctx);
				}

				for (Acao a : dadosnegocio.getLstAcoes()) {
					if (acaoJaIntegrada(a.getIdAcao())) {
						AcaoDAO.atualizarAcao(a, numos);
					} else {
						AcaoDAO.salvarAcao(a, numos, codpap);
					}

				}


			} else {
				
				BigDecimal codvend = ProspectController
						.buscarCodVendProspect(codpap);
				BigDecimal codctt = ProspectController
						.buscarCttProspect(codpap);
				BigDecimal codemp = EmpresaController.buscarCodigoEmpresa(empresa);

				if (codvend == null) {
					return ("Erro na execução: Prospect não está vinculado a um vendedor");
					 
				}

				if (codctt == null) {
					return ("Erro na execução: Prospect "+dadosnegocio.getCodigoCliente()+" precisa de pelo menos 1 contato para abrir negociação");
					
				}
				if (codemp == null) {
					return ("Erro na execução: Empresa não encontrada no Sankhya, favor validar cadastro");
					
				}

				BigDecimal numos2 = NegociacaoDAO.criaCabecalhoNegoc(dadosnegocio, dataDaAlteracao, empresa);
				
				for (NegocioXProduto p : dadosnegocio.getLstNegocioXProdutos()) {
					if (produtoNegociadoJaIntegrado(p.getIdNegocioXProduto())) {
						ProdutoNegociadoDAO.atualizarProdutoNegociado(p, numos2, codpap);
					} else {
						ProdutoNegociadoDAO.salvarProdutoNegociado(p, numos2, codpap);
					}
				}
				
				NegociacaoDAO.criaItensNegoc(dadosnegocio, dataDaAlteracao, numos2, ctx);
				for (Acao a : dadosnegocio.getLstAcoes()) {
					AcaoDAO.salvarAcao(a, numos2, codpap);
				}
				for (NegocioXProduto p : dadosnegocio.getLstNegocioXProdutos()) {
					ProdutoNegociadoDAO.salvarProdutoNegociado(p, numos2, codpap);
				}
			}
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}

	}

	@SuppressWarnings("unused")
	private static boolean ehProspect(DadosCliente dadosCliente) {
		if (dadosCliente.getfProspect() == 1)
			return true;
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

	public static BigDecimal buscarItem(BigDecimal numos, String etapa) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal nuitem = BigDecimal.ZERO;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql(
					"SELECT " + "ITE.NUMITEM " + "FROM TCSITE ITE, TCSMOD MOD " + "WHERE ITE.CODSERV = MOD.CODPROD "
							+ "AND ITE.NUMOS = " + numos + "AND MOD.AD_CODCAMPOSDEALER = '" + etapa + "'");
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			if (rset.next()) {
				nuitem = rset.getBigDecimal("NUMITEM");
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
		return nuitem;
	}
	
	public static void fecharUltimaEtapa(BigDecimal numos, BigDecimal codvend) throws Exception {
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

			sql.appendSql(
					"SELECT MAX(NUMITEM) AS NUMITEM FROM TCSITE WHERE NUMOS = " + numos );
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			if (rset.next()) {
				BigDecimal nuitem = rset.getBigDecimal("NUMITEM");
				
				BigDecimal usuarioLogado = buscarCodusu(numos, nuitem);
				JapeSessionContext.putProperty("usuario_logado", usuarioLogado);
				
				Timestamp dtatual = TimeUtils.getNow();
				
				JapeWrapper servicoExecutadoDAO = JapeFactory.dao("ItemOrdemServico");
				DynamicVO servico = servicoExecutadoDAO.findOne("NUMOS = " + numos + " AND NUMITEM = " + nuitem);
				servicoExecutadoDAO.prepareToUpdate(servico)
					.set("CODSIT", new BigDecimal(2))
					.set("INICEXEC", dtatual )
					.set("HRINICIAL", TimeUtils.getHoraDecimal(dtatual))
					.set("HRFINAL", TimeUtils.getHoraDecimal(dtatual))
					.update();
				
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
	}

	public static boolean acaoJaIntegrada(String id) throws Exception {
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

			sql.appendSql("SELECT * FROM AD_ACAOPAP WHERE IDCAMPOSDEALER = '" + id + "'");
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

	public static boolean produtoNegociadoJaIntegrado(String id) throws Exception {
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

			sql.appendSql("SELECT * FROM AD_TGFPRONEG WHERE IDCAMPOSDEALER = '" + id + "'");
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
	
	public static BigDecimal buscarCodusu(BigDecimal numos, BigDecimal numitem) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal codusu = BigDecimal.ZERO;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT ITE.CODUSU FROM TCSITE ITE WHERE ITE.NUMOS = " + numos + " AND ITE.NUMITEM = " + numitem);
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			if (rset.next()) {
				codusu = rset.getBigDecimal("CODUSU");
			} else {
				throw new Exception("Usuário logado não vinculado a um vendedor");
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
		return codusu;
	}

	public static BigDecimal buscarCodVend(BigDecimal codusu) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal codVend = BigDecimal.ZERO;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT CODVEND FROM TSIUSU WHERE CODUSU = " + codusu);
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			if (rset.next()) {
				codVend = rset.getBigDecimal("CODVEND");
			} else {
				throw new Exception("Usuário logado não vinculado a um vendedor");
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
		return codVend;
	}

	public static InfoNegoc buscarInfoNegoc(String codCamposDealer) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		InfoNegoc infoNegoc = new InfoNegoc();
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT " + "PRO.CODPROD AS CODSERV, " + "PRO.DESCRPROD, " + "SEM.CODOCOROS, "
					+ "SEU.CODUSU, " + "(SELECT INTEIRO FROM TSIPAR WHERE CHAVE = 'CODPRODCRM') AS CODPROD "
					+ "FROM TCSMOD MOD " + "LEFT JOIN TGFPRO PRO ON MOD.CODPROD = PRO.CODPROD "
					+ "LEFT JOIN TCSSEM SEM ON SEM.CODPROD = PRO.CODPROD "
					+ "LEFT JOIN TGFSEU SEU ON SEU.CODSERV = PRO.CODPROD " + "WHERE  MOD.CODMETOD = ( " + "    SELECT "
					+ "        INTEIRO  " + "    FROM " + "        TSIPAR  " + "    WHERE "
					+ "        CHAVE = 'SERVMETPRE' " + ") " + "AND MOD.AD_CODCAMPOSDEALER = '" + codCamposDealer
					+ "'");
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			if (rset.next()) {
				infoNegoc.setCodserv(rset.getBigDecimal("CODSERV"));
				infoNegoc.setDescrprod(rset.getString("DESCRPROD"));
				infoNegoc.setCodocoros(rset.getBigDecimal("CODOCOROS"));
				infoNegoc.setCodprod(rset.getBigDecimal("CODPROD"));
				infoNegoc.setCodusu(rset.getBigDecimal("CODUSU"));
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
		return infoNegoc;
	}

	public static BigDecimal negociacaoJaIntegrada(String idNegoc) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal numos = BigDecimal.ZERO;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT NUMOS FROM TCSOSE WHERE AD_IDCAMPOSDEALER = '" + idNegoc + "'");
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			if (rset.next()) {
				numos = rset.getBigDecimal("NUMOS");
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
		return numos;
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

	public static BigDecimal buscaCenCus(BigDecimal codvend) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal codcencus = BigDecimal.ZERO;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT CODCENCUSPAD FROM TSIUSU WHERE CODVEND = " + codvend);
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			if (rset.next()) {
				codcencus = rset.getBigDecimal("CODCENCUSPAD");
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
		return codcencus;
	}

	public static BigDecimal buscaCodNat() throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal codnat = BigDecimal.ZERO;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT INTEIRO FROM TSIPAR WHERE CHAVE = 'CODNATPADCRM' " );
			System.out.println("SQL: " + sql.toString());

			rset = sql.executeQuery();

			if (rset.next()) {
				codnat = rset.getBigDecimal("INTEIRO");
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
		return codnat;
	}
}
