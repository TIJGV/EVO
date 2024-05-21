package br.com.evonetwork.integracaoAPISimova.Controller;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.google.gson.Gson;
import com.sankhya.util.JdbcUtils;

import br.com.evonetwork.integracaoAPISimova.Model.Autenticacao;
import br.com.evonetwork.integracaoAPISimova.Model.OS;
import br.com.evonetwork.integracaoAPISimova.Model.OSxPecas;
import br.com.evonetwork.integracaoAPISimova.Model.OSxServico;
import br.com.evonetwork.integracaoAPISimova.Model.OSxTecnico;
import br.com.evonetwork.integracaoAPISimova.Model.RetornoCadastro;
import br.com.evonetwork.integracaoAPISimova.Model.Token;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class ControllerEnviarOS {

	public static void coletarDadosEEnviarParaSimova(Registro linha, ContextoAcao ca) throws Exception{
		OS os = new OS();
		ArrayList<OSxPecas> osPecas = new ArrayList<OSxPecas>();
		ArrayList<OSxServico> osServico = new ArrayList<OSxServico>();
		OSxTecnico osTecnico = new OSxTecnico();
		String filial = "";
		String local = "";
		String codOs = "";
		String ativo = "";
		
		filial = Controller.getFilial((BigDecimal) linha.getCampo("CODEMP"));
		if("".equals(filial))
			ca.mostraErro("Não foi possível encontrar uma Filial na OS "+linha.getCampo("NUMOS")+"!\n"
						+ "Verifique se a Empresa Executante foi definida.");
		
		local = filial;
		ativo = "1";
		codOs = ((BigDecimal) linha.getCampo("NUMOS"))+"";
		
		os = gerarOsPelaLinha(linha, filial, local, ativo, codOs, ca);
		percorrerEGerarOsxPecas(osPecas, filial, local, ativo, codOs, ca);
		percorrerEGerarOsxServico(osServico, filial, local, ativo, codOs, ca);
		osTecnico = gerarOsTecnicoPelaLinha(linha, filial, local, ativo, codOs, ca);
		
		enviarDadosParaSimova(os, osPecas, osServico, osTecnico, filial);
	}

	private static void enviarDadosParaSimova(OS os, ArrayList<OSxPecas> osPecas, ArrayList<OSxServico> osServico,
			OSxTecnico osTecnico, String filial) throws Exception {
		System.out.println("***Enviado dados OS***");
		enviarDadosOS(os, filial);
		
		if(!osPecas.isEmpty()) {
			System.out.println("***Enviado dados OSxPecas***");
			enviarDadosOSxPecas(osPecas, filial);
		}
		
		if(!osServico.isEmpty()) {
			System.out.println("***Enviado dados OSxServico***");
			enviarDadosOSxServico(osServico, filial);
		}
		
		if(osTecnico != null) {
			System.out.println("***Enviado dados OSxTecnico***");
			enviarDadosOSxTecnico(osTecnico, filial);
		}
	}

	private static void enviarDadosOSxTecnico(OSxTecnico osTecnico, String filial) throws Exception {
		try {
			String completarURL = "OS_TECNICO";
			
			Autenticacao auth = Controller.getParametrosSankhya(completarURL, filial);
			
			URL url = new URL(auth.getUrl());
			URL urlOSxTecnico = new URL(auth.getUrlAcesso());
			
			Token token = Controller.getToken(auth, url);
			
			String idSimova = criarOSxTecnicoSimova(token, urlOSxTecnico, osTecnico);
			
			atualizarIdIntegracaoTecnico(idSimova, osTecnico);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	private static void atualizarIdIntegracaoTecnico(String idSimova, OSxTecnico osTecnico) throws Exception {
		SessionHandle hnd = null;
		String numOs = osTecnico.getCodOS();
		System.out.println("Atualizando OSxTecnico "+numOs+" com IDINTEGRACAOTECNICO "+idSimova+"...");
		try {
			hnd = JapeSession.open();

			JapeFactory.dao("TCSOSE").
			prepareToUpdateByPK(numOs)
				.set("IDINTEGRACAOTECNICO", new BigDecimal(idSimova))
				.update();

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

	private static String criarOSxTecnicoSimova(Token token, URL urlOSxTecnico, OSxTecnico OSxTecnico) throws Exception {
		HttpURLConnection conn = (HttpURLConnection) urlOSxTecnico.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("authorization", token.getToken());
		
		StringBuilder body = new StringBuilder();
		
		body.append("[\r\n");
		body.append("{\r\n"
				+ "\"Filial\": \""+OSxTecnico.getFilial()+"\",\r\n"
				+ "\"Local\": \""+OSxTecnico.getLocal()+"\",\r\n"
				+ "\"CodigoOs\": \""+OSxTecnico.getCodOS()+"\",\r\n"
				+ "\"CodigoTecnico\": \""+OSxTecnico.getCodTecnico()+"\",\r\n"
				+ "\"CodigoStatusOs\":\""+OSxTecnico.getCodStatusOS()+"\",\r\n"
				+ "\"Ativo\": \""+OSxTecnico.getAtivo()+"\""
				+ "}\r\n");
		body.append("]");
		
		String data = body+"";
        
        System.out.println("BODY: "+data);
        
        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = conn.getOutputStream();
        stream.write(out);
        
        String resposta = null;
        try {
        	resposta = Controller.getResponseBody(conn);
        } catch(Exception e) {
        	resposta = Controller.getErrorStream(conn);
        	
        	RetornoCadastro[] retornoOSxPecass = new Gson().fromJson(resposta, RetornoCadastro[].class); 
            
        	System.out.println("Erro na requisição!");
            System.out.println("Resposta HTTP: "+conn.getResponseCode()+" "+conn.getResponseMessage());
        	System.out.println("Retorno: "+retornoOSxPecass[0].getMsg());
        	
        	throw new Exception("Erro na requisição: "+retornoOSxPecass[0].getMsg());
        }
        
        RetornoCadastro[] retornoOSxTecnicos = new Gson().fromJson(resposta, RetornoCadastro[].class); 
        
        System.out.println("Resposta HTTP: "+conn.getResponseCode()+" "+conn.getResponseMessage());
    	System.out.println("Retorno: "+retornoOSxTecnicos[0].getMsg());
        System.out.println("ID OSxTecnico: "+retornoOSxTecnicos[0].getId());
        
        conn.disconnect();
		return retornoOSxTecnicos[0].getId();
	}

	private static void enviarDadosOSxServico(ArrayList<OSxServico> osServico, String filial) throws Exception {
		try {
			String completarURL = "OS_SERVICO";
			
			Autenticacao auth = Controller.getParametrosSankhya(completarURL, filial);
			
			URL url = new URL(auth.getUrl());
			URL urlOSxServico = new URL(auth.getUrlAcesso());
			
			for (OSxServico OSxServico : osServico) {
				Token token = Controller.getToken(auth, url);
				
				String idSimova = criarOSxServicoSimova(token, urlOSxServico, OSxServico);
				
				atualizarIdIntegracaoServico(idSimova, OSxServico);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	private static void atualizarIdIntegracaoServico(String idSimova, OSxServico oSxServico) throws Exception {
		SessionHandle hnd = null;
		String numOs = oSxServico.getCodOs();
		String numItem = oSxServico.getNroRequisicao();
		System.out.println("Atualizando OSxServiço "+numOs+", "+numItem+" com IDINTEGRACAO "+idSimova+"...");
		try {
			hnd = JapeSession.open();
			
			JapeWrapper servicoDAO = JapeFactory.dao("TCSITE");
			DynamicVO servicoVO = servicoDAO.findOne("NUMOS = "+numOs+" AND NUMITEM = "+numItem);

			JapeFactory.dao("TCSITE").prepareToUpdate(servicoVO)
				.set("IDINTEGRACAO", new BigDecimal(idSimova))
				.update();

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

	private static String criarOSxServicoSimova(Token token, URL urlOSxServico, OSxServico OSxServico) throws Exception {
		HttpURLConnection conn = (HttpURLConnection) urlOSxServico.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("authorization", token.getToken());
		
		StringBuilder body = new StringBuilder();
		
		body.append("[\r\n");
		body.append("{\r\n"
				+ "\"Filial\": \""+OSxServico.getFilial()+"\",\r\n"
				+ "\"Local\": \""+OSxServico.getLocal()+"\",\r\n"
				+ "\"CodigoOs\": \""+OSxServico.getCodOs()+"\",\r\n"
				+ "\"CodigoTipoTempo\": \""+OSxServico.getCodTipoTempo()+"\",\r\n"
				+ "\"NroRequisicao\":\""+OSxServico.getNroRequisicao()+"\",\r\n"
				+ "\"CodigoTipoServico\": \""+OSxServico.getCodTipoServico()+"\",\r\n"
				+ "\"TempoPadrao\": \""+OSxServico.getTempoPadrao()+"\",\r\n"
				+ "\"TempoCobrado\": \""+OSxServico.getTempoCobrado()+"\",\r\n"
				+ "\"CodigoServico\": \""+OSxServico.getCodServico()+"\",\r\n"
				+ "\"CodigoMarca\": \""+OSxServico.getCodMarca()+"\",\r\n"
				+ "\"Ativo\": \""+OSxServico.getAtivo()+"\"\r\n"
				+ "}\r\n");
		body.append("]");
		
		String data = body+"";
        
        System.out.println("BODY: "+data);
        
        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = conn.getOutputStream();
        stream.write(out);
        
        String resposta = null;
        try {
        	resposta = Controller.getResponseBody(conn);
        } catch(Exception e) {
        	resposta = Controller.getErrorStream(conn);
        	
        	RetornoCadastro[] retornoOSxPecass = new Gson().fromJson(resposta, RetornoCadastro[].class); 
            
        	System.out.println("Erro na requisição!");
            System.out.println("Resposta HTTP: "+conn.getResponseCode()+" "+conn.getResponseMessage());
        	System.out.println("Retorno: "+retornoOSxPecass[0].getMsg());
        	
        	throw new Exception("Erro na requisição: "+retornoOSxPecass[0].getMsg());
        }
        
        RetornoCadastro[] retornoOSxServicos = new Gson().fromJson(resposta, RetornoCadastro[].class); 
        
        System.out.println("Resposta HTTP: "+conn.getResponseCode()+" "+conn.getResponseMessage());
    	System.out.println("Retorno: "+retornoOSxServicos[0].getMsg());
        System.out.println("ID OSxServico: "+retornoOSxServicos[0].getId());
        
        conn.disconnect();
		return retornoOSxServicos[0].getId();
	}

	private static void enviarDadosOSxPecas(ArrayList<OSxPecas> osPecas, String filial) throws Exception {
		try {
			String completarURL = "OS_PECA";
			
			Autenticacao auth = Controller.getParametrosSankhya(completarURL, filial);
			
			URL url = new URL(auth.getUrl());
			URL urlOSxPecas = new URL(auth.getUrlAcesso());
			
			for (OSxPecas OSxPecas : osPecas) {
				Token token = Controller.getToken(auth, url);
				
				String idSimova = criarOSxPecasSimova(token, urlOSxPecas, OSxPecas);
				
				atualizarIdIntegracaoPecas(idSimova, OSxPecas);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	private static void atualizarIdIntegracaoPecas(String idSimova, OSxPecas oSxPecas) throws Exception {
		SessionHandle hnd = null;
		String numOs = oSxPecas.getCodOS();
		String sequencia = oSxPecas.getSequencia();
		System.out.println("Atualizando OSxPeça "+numOs+", "+sequencia+" com IDINTEGRACAO "+idSimova+"...");
		try {
			hnd = JapeSession.open();
			
			JapeWrapper pecasDAO = JapeFactory.dao("TCSPRO");
			DynamicVO pecasVO = pecasDAO.findOne("NUMOS = "+numOs+" AND SEQUENCIA = "+sequencia);

			JapeFactory.dao("TCSPRO").
			prepareToUpdate(pecasVO)
				.set("IDINTEGRACAO", new BigDecimal(idSimova))
				.update();

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

	private static String criarOSxPecasSimova(Token token, URL urlOSxPecas, OSxPecas OSxPecas) throws Exception {
		HttpURLConnection conn = (HttpURLConnection) urlOSxPecas.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("authorization", token.getToken());
		
		StringBuilder body = new StringBuilder();
		
		body.append("[\r\n");
		body.append("{\r\n"
				+ "\"Filial\": \""+OSxPecas.getFilial()+"\",\r\n"
				+ "\"Local\": \""+OSxPecas.getLocal()+"\",\r\n"
				+ "\"CodigoOs\": \""+OSxPecas.getCodOS()+"\",\r\n"
				+ "\"CodigoProduto\": \""+OSxPecas.getCodProduto()+"\",\r\n"
				+ "\"QtdRequisitada\":\""+OSxPecas.getQtdRequisitada()+"\",\r\n"
				+ "\"QtdUtilizada\": \""+OSxPecas.getQtdUtilizada()+"\",\r\n"
				+ "\"QtdDevolvida\": \""+OSxPecas.getQtdDevolvida()+"\",\r\n"
				+ "\"Ativo\": \""+OSxPecas.getAtivo()+"\"\r\n"
				+ "}\r\n");
		body.append("]");
		
		String data = body+"";
        
        System.out.println("BODY: "+data);
        
        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = conn.getOutputStream();
        stream.write(out);
        
        String resposta = null;
        try {
        	resposta = Controller.getResponseBody(conn);
        } catch(Exception e) {
        	resposta = Controller.getErrorStream(conn);
        	
        	RetornoCadastro[] retornoOSxPecass = new Gson().fromJson(resposta, RetornoCadastro[].class); 
            
        	System.out.println("Erro na requisição!");
            System.out.println("Resposta HTTP: "+conn.getResponseCode()+" "+conn.getResponseMessage());
        	System.out.println("Retorno: "+retornoOSxPecass[0].getMsg());
        	
        	throw new Exception("Erro na requisição: "+retornoOSxPecass[0].getMsg());
        }
        
        RetornoCadastro[] retornoOSxPecass = new Gson().fromJson(resposta, RetornoCadastro[].class); 
        
        System.out.println("Resposta HTTP: "+conn.getResponseCode()+" "+conn.getResponseMessage());
    	System.out.println("Retorno: "+retornoOSxPecass[0].getMsg());
        System.out.println("ID OSxPecas: "+retornoOSxPecass[0].getId());
        
        conn.disconnect();
		return retornoOSxPecass[0].getId();
	}

	private static void enviarDadosOS(OS os, String filial) throws Exception {
		try {
			String completarURL = "OS";
			
			Autenticacao auth = Controller.getParametrosSankhya(completarURL, filial);
			
			URL url = new URL(auth.getUrl());
			URL urlOS = new URL(auth.getUrlAcesso());
			
			Token token = Controller.getToken(auth, url);
			
			String idSimova = criarOSSimova(token, urlOS, os);
			
			atualizarIdIntegracaoServ(idSimova, os);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	private static void atualizarIdIntegracaoServ(String idSimova, OS os) throws Exception {
		SessionHandle hnd = null;
		System.out.println("Atualizando OS "+os.getCodOS()+" com IDINTEGRACAO "+idSimova+"...");
		try {
			hnd = JapeSession.open();

			JapeFactory.dao("TCSOSE").
			prepareToUpdateByPK(os.getCodOS())
				.set("IDINTEGRACAO", new BigDecimal(idSimova))
				.update();

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

	private static String criarOSSimova(Token token, URL urlOS, OS os) throws Exception {
		HttpURLConnection conn = (HttpURLConnection) urlOS.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("authorization", token.getToken());
		
		StringBuilder body = new StringBuilder();
		
		body.append("[\r\n");
		body.append("{\r\n"
				+ "\"Filial\": \""+os.getFilial()+"\",\r\n"
				+ "\"Local\": \""+os.getLocal()+"\",\r\n"
				+ "\"CodigoOS\": \""+os.getCodOS()+"\",\r\n"
				+ "\"ChassiVeiculo\": \""+os.getChassiVeiculo()+"\",\r\n"
				+ "\"CodigoMarca\": \""+os.getCodMarca()+"\",\r\n"
				+ "\"TipoAtendimento\": \""+os.getTipoAtendimento()+"\",\r\n"
				+ "\"Proprietario\": \""+os.getProprietario()+"\",\r\n"
				+ "\"LojaProprietario\": \""+os.getLojaProprietario()+"\",\r\n"
				+ "\"Observacao\": \""+os.getObservacao().replaceAll("\\n", "").replaceAll("\\p{C}", " ")+"\",\r\n"
				+ "\"DataInclusaoOS\": \""+os.getDataInclusaoOS()+"\",\r\n"
				+ "\"DataEntregaVeiculo\": \""+os.getDataEntregaVeiculo()+"\",\r\n"
				+ "\"CodigoStatusOs\": \""+os.getCodStatusOS()+"\",\r\n"
				+ "\"Ativo\": \""+os.getAtivo()+"\",\r\n"
				+ "\"CodigoTipoOs\": \""+os.getCodTipoOS()+"\"\r\n"
				+ "}\r\n");
		body.append("]");
		
		String data = body+"";
        
        System.out.println("BODY: "+data);
        
        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = conn.getOutputStream();
        stream.write(out);
        
        String resposta = null;
        try {
        	resposta = Controller.getResponseBody(conn);
        } catch(Exception e) {
        	resposta = Controller.getErrorStream(conn);
        	
        	RetornoCadastro[] retornoOSxPecass = new Gson().fromJson(resposta, RetornoCadastro[].class); 
            
        	System.out.println("Erro na requisição!");
            System.out.println("Resposta HTTP: "+conn.getResponseCode()+" "+conn.getResponseMessage());
        	System.out.println("Retorno: "+retornoOSxPecass[0].getMsg());
        	
        	throw new Exception("Erro na requisição: "+retornoOSxPecass[0].getMsg());
        }
        
        RetornoCadastro[] retornoOSs = new Gson().fromJson(resposta, RetornoCadastro[].class); 
        
        System.out.println("Resposta HTTP: "+conn.getResponseCode()+" "+conn.getResponseMessage());
    	System.out.println("Retorno: "+retornoOSs[0].getMsg());
        System.out.println("ID OS: "+retornoOSs[0].getId());
        
        conn.disconnect();
		return retornoOSs[0].getId();
	}

	private static void percorrerEGerarOsxServico(ArrayList<OSxServico> osServico, String filial, String local, String ativo,
			String codOs, ContextoAcao ca) throws Exception {
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

			sql.appendSql("SELECT CODSERV, NUMITEM, CODTIPOTEMPO, IDINTEGRACAO FROM AD_TCSITE WHERE NUMOS = "+codOs);

			rset = sql.executeQuery();

			while (rset.next()) {
				// Verifica se serviço já foi enviado para esta OS, se sim, não envia novamente
				BigDecimal idIntegracao = rset.getBigDecimal("IDINTEGRACAO");
				if(idIntegracao != null)
					continue;
				
				BigDecimal codigoTipoTempo = rset.getBigDecimal("CODTIPOTEMPO");
				String tipoTempo = "";
				if(codigoTipoTempo != null)
					tipoTempo = codigoTipoTempo.toString();
				
				String codServico = getReferenciaFornecedorDoServico(rset.getBigDecimal("CODSERV")); // Alterado de CODPROD para REFFORN à pedido do Leonardo
				String codTipoServico = "";//getTipoServico(codServico);
				String codTipoTempo = tipoTempo;
				String nroRequisicao = rset.getBigDecimal("NUMITEM")+"";
				String tempoPadrao = getTempoPadrao(rset.getBigDecimal("CODSERV").toString());
				String tempoCobrado = "0";
				
				String codMarca = "";
				BigDecimal codGrupo = Controller.getCodGrupoDoServico(rset.getBigDecimal("CODSERV"));
				if(codGrupo != null) {
					codMarca = Controller.coletarCodMarcaServico(codGrupo);
					if(codMarca == null) {
						JdbcUtils.closeResultSet(rset);
						throw new Exception("Não foi encontrada nenhuma Marca para o Grupo de Produto/Serviço "+codGrupo+", vinculado ao Serviço "+rset.getBigDecimal("CODSERV"));
					}
				} else {
					JdbcUtils.closeResultSet(rset);
					throw new Exception("Não foi encontrado nenhum Grupo de Produto/Serviço vinculado com o Serviço "+rset.getBigDecimal("CODSERV"));
				}
				
				// Verifica se serviço vinculado a OS existe no Simova, se não existir, o mesmo será criado
//				Controller.verificarSeServicoEstaCriadoNoSimova(rset.getBigDecimal("CODSERV"));
				Controller.enviarServicoAoSimova(rset.getBigDecimal("CODSERV"));
				
				osServico.add(gerarOsServico(filial, local, ativo, codOs, codTipoServico, codTipoTempo, nroRequisicao, tempoPadrao, tempoCobrado, codServico, codMarca));
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

	private static String getReferenciaFornecedorDoServico(BigDecimal codServ) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		String dado = "";
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT REFFORN FROM TGFPRO WHERE CODPROD = "+codServ);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getString("REFFORN");
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

	private static String getTempoPadrao(String codServico) throws Exception {
		JapeSession.SessionHandle hnd = null;
		String retorno = "";
		try {
			hnd = JapeSession.open();
			JapeWrapper servicoDAO = JapeFactory.dao(DynamicEntityNames.SERVICO);
			DynamicVO servico = servicoDAO.findByPK(codServico);
			BigDecimal tempoPadrao = servico.asBigDecimal("AD_TEMPPADRAO");
			if(tempoPadrao != null) {
				retorno = tempoPadrao.toString();
			} else {
				retorno = "0";
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
		return retorno;
	}

//	private static String getTipoServico(String codServico) throws Exception {
//		JapeSession.SessionHandle hnd = null;
//		String retorno = "";
//		try {
//			hnd = JapeSession.open();
//			JapeWrapper servicoDAO = JapeFactory.dao(DynamicEntityNames.SERVICO);
//			DynamicVO servico = servicoDAO.findByPK(codServico);
//			retorno = servico.asString("TIPO");
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new Exception(e.getMessage());
//		} finally {
//			JapeSession.close(hnd);
//		}
//		return retorno;
//	}

	private static void percorrerEGerarOsxPecas(ArrayList<OSxPecas> osPecas, String filial, String local, String ativo, String codOs, ContextoAcao ca) throws Exception {
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
			
			sql.appendSql("SELECT CODPROD, QUANTIDADE, SEQUENCIA, IDINTEGRACAO FROM AD_TCSPRO WHERE NUMOS = "+codOs);
			
			rset = sql.executeQuery();
			
			while (rset.next()) {
				// Se OSxPeça já tiver sido integrada, não enviar novamente
				BigDecimal idIntegracao = rset.getBigDecimal("IDINTEGRACAO");
				if(idIntegracao != null)
					continue;
				
				String codProd = getReferenciaFornecedorDoServico(rset.getBigDecimal("CODPROD")); // Alterado de CODPROD para REFFORN à pedido do Leonardo
				String qtdRequisitada = rset.getBigDecimal("QUANTIDADE")+"";
				String sequencia = rset.getBigDecimal("SEQUENCIA")+"";
				String qtdUtilizada = "0";
				String qtdDevolvida = "0";
				
				// Verifica se peça vinculada a OS existe no Simova, se não existir, a mesma será criada
//				Controller.verificarSePecaEstaCriadoNoSimova(rset.getBigDecimal("CODPROD").toString());
				Controller.enviarPecaAoSimova(rset.getBigDecimal("CODPROD").toString());
				
				osPecas.add(gerarOsPecas(filial, local, ativo, codOs, codProd, qtdRequisitada, qtdUtilizada, qtdDevolvida, sequencia));
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

	private static OSxServico gerarOsServico(String filial, String local, String ativo, String codOs,
			String codTipoServico, String codTipoTempo, String nroRequisicao, String tempoPadrao,
			String tempoCobrado, String codServico, String codMarca) {
		OSxServico osServico = new OSxServico();
		
		//AD_TCSITE
		
		osServico.setFilial(filial);
		osServico.setLocal(local);
		osServico.setAtivo(ativo);
		osServico.setCodOs(codOs);
		osServico.setCodTipoServico(codTipoServico);
		osServico.setCodTipoTempo(codTipoTempo);
		osServico.setNroRequisicao(nroRequisicao);
		osServico.setTempoPadrao(tempoPadrao);
		osServico.setTempoCobrado(tempoCobrado);
		osServico.setCodServico(codServico);
		osServico.setCodMarca(codMarca);
		
		return osServico;
	}

	private static OSxPecas gerarOsPecas(String filial, String local, String ativo, String codOs,
			String codProd, String qtdRequisitada, String qtdUtilizada, String qtdDevolvida, String sequencia) {
		OSxPecas osPecas = new OSxPecas();
		
		//AD_TCSPRO
		
		osPecas.setFilial(filial);
		osPecas.setLocal(local);
		osPecas.setAtivo(ativo);
		osPecas.setCodOS(codOs);
		osPecas.setCodProduto(codProd);
		osPecas.setQtdRequisitada(qtdRequisitada);
		osPecas.setQtdUtilizada(qtdUtilizada);
		osPecas.setQtdDevolvida(qtdDevolvida);
		osPecas.setSequencia(sequencia);
		
		return osPecas;
	}
	
	private static OSxTecnico gerarOsTecnicoPelaLinha(Registro linha, String filial, String local, String ativo,
			String codOs, ContextoAcao ca) throws Exception {
		OSxTecnico osTecnico = new OSxTecnico();
		BigDecimal codUsuRespOficina = (BigDecimal) linha.getCampo("CODUSURESPOFICINA");
		String codParc = Controller.getCodParcPeloUsuario(codUsuRespOficina)+"";
		//AD_TCSOSE
		
		if(verificaSeOSxTecnicoJaExisteIntegracao((BigDecimal) linha.getCampo("IDINTEGRACAOTECNICO")))
			return null;
		
		osTecnico.setFilial(filial);
		osTecnico.setLocal(local);
		osTecnico.setAtivo(ativo);
		osTecnico.setCodOS(codOs);
		osTecnico.setCodTecnico(codParc);
		osTecnico.setCodStatusOS(getStatusOS((String) linha.getCampo("STATUS")));
		
		// Verificar se tecnico vinculado a OS existe no Simova, se não existir, o mesmo será criado
//		Controller.verificarSeTecnicoEstaCriadoNoSimova(codParc, filial);
		BigDecimal codEmpFilial = Controller.getCodEmpDaFilial(filial);
		BigDecimal codParcBD = new BigDecimal(codParc);
		Controller.vincularFuncionarioComFilialDaOs(codParcBD, codEmpFilial);
		Controller.enviarTecnicoAoSimova(codParcBD);
		
		return osTecnico;
	}

	private static boolean verificaSeOSxTecnicoJaExisteIntegracao(BigDecimal idIntegracao) {
		if(idIntegracao != null)
			return true;
		return false;
	}

//	private static String getCodParcDoUsuario(BigDecimal codUsu) throws Exception {
//		BigDecimal codParc = null;
//		JdbcWrapper jdbc = null;
//		NativeSql sql = null;
//		ResultSet rset = null;
//		SessionHandle hnd = null;
//		try {
//			hnd = JapeSession.open();
//			hnd.setFindersMaxRows(-1);
//			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
//			jdbc = entity.getJdbcWrapper();
//			jdbc.openSession();
//			
//			sql = new NativeSql(jdbc);
//			
//			sql.appendSql("SELECT CODPARC FROM TSIUSU WHERE CODUSU = "+codUsu);
//			
//			rset = sql.executeQuery();
//			
//			if (rset.next()) {
//				codParc = rset.getBigDecimal("CODPARC");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new Exception(e.getMessage());
//		} finally {
//			JdbcUtils.closeResultSet(rset);
//			NativeSql.releaseResources(sql);
//			JdbcWrapper.closeSession(jdbc);
//			JapeSession.close(hnd);
//		}
//		
//		return codParc+"";
//	}

	private static OS gerarOsPelaLinha(Registro linha, String filial, String local, String ativo, 
			String codOs, ContextoAcao ca) throws Exception{
		OS os = new OS();
		try {
			//AD_TCSOSE
			os.setFilial(filial);
			os.setLocal(local);
			os.setAtivo(ativo);
			os.setCodOS(codOs);
			
			/* CHASSI */
			String chassi = getChassiVeiculo((BigDecimal) linha.getCampo("CODVEICULO"));
			if("".equals(chassi) || chassi == null)
				ca.mostraErro("Não foi encontrado o Chassi do veículo "+(BigDecimal) linha.getCampo("CODVEICULO"));
			else
				os.setChassiVeiculo(chassi);
			
			// Verificando se veículo existe no Simova, se não existir, o mesmo será criado
			//Controller.verificarSeEquipamentoEstaCriadoNoSimova(chassi, filial);
			Controller.enviarEquipamentoAoSimova(chassi, filial);
			
			/* CODMARCA */
			String codMarca = getMarcaVeiculo((BigDecimal) linha.getCampo("CODVEICULO"));
			if("".equals(codMarca) || codMarca == null)
				ca.mostraErro("Não foi encontrado o Modelo do veículo "+(BigDecimal) linha.getCampo("CODVEICULO"));
			else
				os.setCodMarca(codMarca);
			
			/* TIPO ATENDIMENTO */
			String tipoAtendimento = getTipoAtendimento((String) linha.getCampo("TIPOATENDIMENTO"));
			if("".equals(tipoAtendimento) || tipoAtendimento == null)
				ca.mostraErro("Não foi encontrado o Tipo de Atendimento da OS "+(BigDecimal) linha.getCampo("NUMOS"));
			else
				os.setTipoAtendimento(tipoAtendimento);
			
			/* PROPRIETARIO */
			String proprietario = "";
			if((BigDecimal) linha.getCampo("CODPARC") == null)
				ca.mostraErro("Não foi encontrado o Solicitante da OS "+(BigDecimal) linha.getCampo("NUMOS"));
			else
				proprietario = ((BigDecimal) linha.getCampo("CODPARC"))+"";
			
			// Verificando se Cliente existe no Simova, se não existir, o mesmo será criado
//			Controller.verificarSeClienteEstaCriadoNoSimova((BigDecimal) linha.getCampo("CODPARC"), filial);
			Controller.enviarParceiroAoSimova((BigDecimal) linha.getCampo("CODPARC"), filial);
			
			os.setProprietario(proprietario);
			
			// Se cliente não possuir Filial vinculada, envia a Filial da OS
			String filialCliente = Controller.getFilial(Controller.getFilialCliente(proprietario));
			System.out.println("filial: "+filialCliente);
			if("".equals(filialCliente) || "null".equals(filialCliente))
				filialCliente = filial;
			os.setLojaProprietario(filialCliente);
			
			/* OBSERVAÇÃO */
			os.setObservacao(((String) linha.getCampo("DESCRSERV")).replaceAll("\\n", "").replaceAll("\n", "").replaceAll("\r", ""));
			
			/* DH ABERTURA */
			if((Timestamp) linha.getCampo("DHABERTURA") == null)
				ca.mostraErro("Não foi encontrada a Data de Abertura da OS "+(BigDecimal) linha.getCampo("NUMOS"));
			else
				os.setDataInclusaoOS(convertDate(((Timestamp) linha.getCampo("DHABERTURA")+"")));
			
			/* DH PREVISTA */
			if((Timestamp) linha.getCampo("DHPREVISTA") == null)
				ca.mostraErro("Não foi encontrada a Data Prevista de Conclusão da OS "+(BigDecimal) linha.getCampo("NUMOS"));
			else
				os.setDataEntregaVeiculo(convertDate(((Timestamp) linha.getCampo("DHPREVISTA")+"")));
			
			/* STATUS OS */
			os.setCodStatusOS(getStatusOS((String) linha.getCampo("STATUS")));
			
			/* TIPO OS */
			BigDecimal codTipoOS = (BigDecimal) linha.getCampo("CODTIPODEOS");
			if(codTipoOS != null) {
				os.setCodTipoOS(codTipoOS.toString());
//				Controller.verificarSeTipoOsEstaCriadoNoSimova((BigDecimal) linha.getCampo("CODTIPODEOS"));
				Controller.enviarTipoDeOsAoSimova((BigDecimal) linha.getCampo("CODTIPODEOS"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		
		return os;
	}

	private static String getTipoAtendimento(String tipoAtendimento) {
		String retorno = "";
		if(tipoAtendimento == null)
			return "0";
		switch(tipoAtendimento) {
			case("P"):
				retorno = "0";
				break;
			case("T"):
				retorno = "1";
				break;
			default:
				retorno = "0";
				break;
		}
		return retorno;
	}

	private static String getStatusOS(String statusOS) {
		String retorno = "";
		if(statusOS == null)
			return "1";
		switch (statusOS) {
			case "F":
				retorno = "3";
				break;
			case "AV":
				retorno = "1";
				break;
			case "A":
				retorno = "1";
				break;
		}
		return retorno;
	}
	
//	private static String convertHour(String mDate){
//	   SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
//	   try {
//	          Date newDate = inputFormat.parse(mDate);
//	          inputFormat = new SimpleDateFormat("HH:mm:ss");
//	          mDate = inputFormat.format(newDate);
//	    } catch (ParseException e) {
//	          e.printStackTrace();
//	    }
//
//	   return mDate;
//	}

	private static String convertDate(String mDate) throws Exception{
	   SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
	   try {
	          Date newDate = inputFormat.parse(mDate);
	          inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	          mDate = inputFormat.format(newDate);
	    } catch (ParseException e) {
	          e.printStackTrace();
	          throw new Exception(e.getMessage());
	    }

	   return mDate;
	}

	private static String getMarcaVeiculo(BigDecimal codVeiculo) throws Exception {
		JapeSession.SessionHandle hnd = null;
		String codMarca = "";
		try {
			hnd = JapeSession.open();
			JapeWrapper veiculoDAO = JapeFactory.dao(DynamicEntityNames.VEICULO);
			DynamicVO veiculo = veiculoDAO.findByPK(codVeiculo);
			if(veiculo.asBigDecimal("AD_NROUNICOMODELO") != null)
				codMarca = veiculo.asBigDecimal("AD_NROUNICOMODELO")+"";
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
		return codMarca;
	}

	private static String getChassiVeiculo(BigDecimal codVeiculo) throws Exception {
		JapeSession.SessionHandle hnd = null;
		String chassi = "";
		try {
			hnd = JapeSession.open();
			JapeWrapper veiculoDAO = JapeFactory.dao(DynamicEntityNames.VEICULO);
			DynamicVO veiculo = veiculoDAO.findByPK(codVeiculo);
			chassi = veiculo.asString("CHASSIS");
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
		return chassi;
	}
}
