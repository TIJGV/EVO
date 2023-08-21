package br.com.evonetwork.integracaoAPISimova.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.sankhya.util.JdbcUtils;

import br.com.evonetwork.integracaoAPISimova.Model.Autenticacao;
import br.com.evonetwork.integracaoAPISimova.Model.RetornoCadastro;
import br.com.evonetwork.integracaoAPISimova.Model.Token;
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

public class Controller {

	public static void atualizarIdIntegracao(String idSimova, String campoIntegracao, DynamicVO registroVO, String tabela) throws Exception {
		System.out.println("Atualizando "+campoIntegracao+" com ID "+idSimova+"...");
		registroVO.setProperty(campoIntegracao, new BigDecimal(idSimova));
		if(campoIntegracaoNaoFoiAtualizado(registroVO, idSimova, campoIntegracao, tabela)) {
			System.out.println("Não foi atualizado");
			JapeSession.SessionHandle hnd = null;
			try {
				hnd = JapeSession.open();
				JapeWrapper tabelaDAO = JapeFactory.dao(tabela);
				tabelaDAO.prepareToUpdate(registroVO)
					.set(campoIntegracao, new BigDecimal(idSimova))
					.update();
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception(e.getMessage());
			} finally {
				JapeSession.close(hnd);
			}
		}
	}
	
	private static boolean campoIntegracaoNaoFoiAtualizado(DynamicVO oldRegistroVO, String idSimova, String campoIntegracao, String tabela) throws Exception {
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper registroDAO = JapeFactory.dao(tabela);
			DynamicVO registroVO = registroDAO.findByPK(oldRegistroVO.getPrimaryKey());
			System.out.println("Campo integracao: "+registroVO.getProperty(campoIntegracao)+" ID: "+idSimova);
			if(((BigDecimal)registroVO.getProperty(campoIntegracao)) == null || ((BigDecimal)registroVO.getProperty(campoIntegracao)).compareTo(new BigDecimal(idSimova)) != 0)
				return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
		return false;
	}

	public static Autenticacao getParametrosSankhya(String completarURL, String filial) {
		Autenticacao auth = new Autenticacao();
		
		String user = null;
		String pass = null;
		String url = null;
		String urlAcesso = null;
		
		JdbcWrapper jdbc = null;
		
		NativeSql sql1 = null;
		NativeSql sql2 = null;
		NativeSql sql3 = null;
		NativeSql sql4 = null;
		
		ResultSet rset1 = null;
		ResultSet rset2 = null;
		ResultSet rset3 = null;
		ResultSet rset4 = null;
		
		SessionHandle hnd = null;

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql1 = new NativeSql(jdbc);
			sql2 = new NativeSql(jdbc);
			sql3 = new NativeSql(jdbc);
			sql4 = new NativeSql(jdbc);

			sql1.appendSql("SELECT TEXTO FROM TSIPAR WHERE CHAVE = 'SIMOVAUSER'");
			sql2.appendSql("SELECT TEXTO FROM TSIPAR WHERE CHAVE = 'SIMOVAPASS'");
			sql3.appendSql("SELECT TEXTO FROM TSIPAR WHERE CHAVE = 'SIMOVAURLAUTH'");
			sql4.appendSql("SELECT TEXTO FROM TSIPAR WHERE CHAVE = 'SIMOVAURLSYNC'");

			rset1 = sql1.executeQuery();
			rset2 = sql2.executeQuery();
			rset3 = sql3.executeQuery();
			rset4 = sql4.executeQuery();

			if (rset1.next()) {
				user = rset1.getString("TEXTO");
			}
			if (rset2.next()) {
				pass = rset2.getString("TEXTO");
			}
			if (rset3.next()) {
				url = rset3.getString("TEXTO");
			}
			if (rset4.next()) {
				urlAcesso = rset4.getString("TEXTO");
			}

			auth.setUser(user);
			auth.setPass(pass);
			auth.setEmpresa(filial);
			auth.setUrl(url);
			auth.setUrlAcesso(urlAcesso+completarURL);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rset1);
			JdbcUtils.closeResultSet(rset2);
			JdbcUtils.closeResultSet(rset3);
			JdbcUtils.closeResultSet(rset4);
			NativeSql.releaseResources(sql1);
			NativeSql.releaseResources(sql2);
			NativeSql.releaseResources(sql3);
			NativeSql.releaseResources(sql4);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
		
		return auth;
	}
	
	public static Token getToken(Autenticacao auth, URL url) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
        
        String data = "{\r\n"
        		+ "\"user\":\""+auth.getUser()+"\",\r\n"
        		+ "\"password\":\""+auth.getPass()+"\",\r\n"
        		+ "\"empresa\":\""+auth.getEmpresa()+"\"\r\n"
        		+ "}";
        
        System.out.println("URL: "+url.toString());
        System.out.println("BODY: "+data);
        
        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = conn.getOutputStream();
        stream.write(out);
        
        String resposta = getResponseBody(conn);
        
        Gson g = new Gson();
        Token token = g.fromJson(resposta, Token.class);
        
        conn.disconnect();
        
        return token;
	}
	
	public static String getResponseBody(HttpURLConnection conn) {
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

	public static String getFilial(BigDecimal codEmp) {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		String filial = "";
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT AD_CODFILIAL FROM TSIEMP WHERE CODEMP = "+codEmp);
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				filial = rset.getString("AD_CODFILIAL");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
		return filial;
	}
	
	public static String getErrorStream(HttpURLConnection conn) throws IOException {
		InputStream errorstream = conn.getErrorStream();
		String response = "";
		String line;
		BufferedReader br = new BufferedReader(new InputStreamReader(errorstream, "UTF-8"));
		while ((line = br.readLine()) != null) {
		    response += line;
		}
		return response;
	}
	
	public static BigDecimal getFilialCliente(String codParc) throws Exception {
		BigDecimal codFilial = null;
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

			sql.appendSql("SELECT AD_FILIAL FROM TGFPAR WHERE CODPARC = "+codParc);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();

			if (rset.next()) {
				codFilial = rset.getBigDecimal("AD_FILIAL");
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
		
		return codFilial;
	}

	public static ArrayList<BigDecimal> getFiliaisFuncionario(BigDecimal codParc) throws Exception {
		ArrayList<BigDecimal> filiais = new ArrayList<BigDecimal>();
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

			sql.appendSql("SELECT CODEMP FROM AD_FILIAISPARC WHERE CODPARC = "+codParc);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();

			while (rset.next()) {
				filiais.add(rset.getBigDecimal("CODEMP"));
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
		return filiais;
	}

	public static void verificarSeClienteEstaCriadoNoSimova(BigDecimal codParc, String filial) throws Exception {
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
			
			sql.appendSql("SELECT AD_IDINTEGRACAO FROM TGFPAR WHERE CODPARC = "+codParc);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			// Se query retornar, significa que cliente já foi enviado
			if (rset.next() && rset.getBigDecimal("AD_IDINTEGRACAO") != null) {
				System.out.println("Cliente já existe no Simova: "+rset.getBigDecimal("AD_IDINTEGRACAO"));
				return;
			} else {
				System.out.println("Cliente não existe no Simova. Cadastrando...");
				enviarParceiroAoSimova(codParc, filial);
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

	public static void enviarParceiroAoSimova(BigDecimal codParc, String filialOS) throws Exception {
		DynamicVO clientesVO = null;
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper parceiroDAO = JapeFactory.dao(DynamicEntityNames.PARCEIRO);
			clientesVO = parceiroDAO.findByPK(codParc);
			
			BigDecimal codEmpFilial = getCodEmpDaFilial(filialOS);
			//se cliente tem Filial vinculada, apenas envia ao simova. Se não houver, vincula o mesmo a filial da OS
			System.out.println("Filial: "+(BigDecimal) clientesVO.getProperty("AD_FILIAL"));
			if(((BigDecimal) clientesVO.getProperty("AD_FILIAL")) != null)
				ControllerClientes.enviarClientePorDynamicVO(clientesVO);
			else
				atualizarFilialDoClienteParaFilialAtual(clientesVO, codEmpFilial);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}
	
	private static void atualizarFilialDoClienteParaFilialAtual(DynamicVO clienteVO, BigDecimal filial) throws Exception {
		SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeFactory.dao(DynamicEntityNames.PARCEIRO).prepareToUpdate(clienteVO)
				.set("AD_FILIAL", filial)
				.update();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

	public static void verificarSeModeloVeiculoEstaCriadoNoSimova(BigDecimal codModeloVeiculo) throws Exception {
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
			
			sql.appendSql("SELECT IDINTEGRACAO FROM AD_MODELOVEI WHERE NROUNICO = "+codModeloVeiculo);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			// Se query retornar, significa que modelo veículo já foi enviado
			if (rset.next() && rset.getBigDecimal("IDINTEGRACAO") != null) {
				System.out.println("Modelo veículo já existe no Simova: "+rset.getBigDecimal("IDINTEGRACAO"));
				return;
			} else {
				System.out.println("Modelo veículo não existe no Simova. Cadastrando...");
				enviarModeloVeiculoAoSimova(codModeloVeiculo);
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

	private static void enviarModeloVeiculoAoSimova(BigDecimal codModeloVeiculo) throws Exception {
		DynamicVO modeloVeiculoVO = null;
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper modeloVeiculoDAO = JapeFactory.dao("AD_MODELOVEI");
			modeloVeiculoVO = modeloVeiculoDAO.findByPK(codModeloVeiculo);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
		
		ControllerModeloEquipamento.enviarModeloEquipamentoPorDynamicVO(modeloVeiculoVO);
	}

	public static void verificarSeTipoVeiculoEstaCriadoNoSimova(BigDecimal codTipoVeiculo) throws Exception {
		if(codTipoVeiculo == null)
			throw new Exception("Espécie/Tipo Veículo não está vinculado com o Modelo do veículo!");
		
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
			
			sql.appendSql("SELECT IDINTEGRACAO FROM AD_ESPECIETIPOVEI WHERE NROUNICO = "+codTipoVeiculo);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			// Se query retornar, significa que tipo veiculo já foi enviado
			if (rset.next() && rset.getBigDecimal("IDINTEGRACAO") != null) {
				System.out.println("Tipo veículo já existe no Simova: "+rset.getBigDecimal("IDINTEGRACAO"));
				return;
			} else {
				System.out.println("Tipo veículo não existe no Simova. Cadastrando...");
				enviarTipoVeiculoAoSimova(codTipoVeiculo);
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

	private static void enviarTipoVeiculoAoSimova(BigDecimal codTipoVeiculo) throws Exception {
		DynamicVO tipoVeiculoVO = null;
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper tipoVeiculoDAO = JapeFactory.dao("AD_ESPECIETIPOVEI");
			tipoVeiculoVO = tipoVeiculoDAO.findByPK(codTipoVeiculo);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
		
		ControllerTipoEquipamento.enviarTipoEquipamentoPorDynamicVO(tipoVeiculoVO);
	}

	public static void verificarSeGrupoServiçoEstaCriadoNoSimova(BigDecimal codGrupoServico) throws Exception {
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
			
			sql.appendSql("SELECT AD_IDINTEGRACAO FROM TGFGRU WHERE CODGRUPOPROD = "+codGrupoServico);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			// Se query retornar, significa que grupo serviço já foi enviado
			if (rset.next() && rset.getBigDecimal("AD_IDINTEGRACAO") != null) {
				System.out.println("Grupo serviço já existe no Simova: "+rset.getBigDecimal("AD_IDINTEGRACAO"));
				return;
			} else {
				System.out.println("Grupo serviço não existe no Simova. Cadastrando...");
				enviarGrupoServicoAoSimova(codGrupoServico);
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

	private static void enviarGrupoServicoAoSimova(BigDecimal codGrupoServico) throws Exception {
		DynamicVO grupoProdutoVO = null;
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper grupoProdutoDAO = JapeFactory.dao(DynamicEntityNames.GRUPO_PRODUTO);
			grupoProdutoVO = grupoProdutoDAO.findByPK(codGrupoServico);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
		ControllerGrupoServico.enviarGrupoServicoPorDynamicVO(grupoProdutoVO);
	}

	public static String coletarCodMarcaServico(BigDecimal codGrupoProd) {
		BigDecimal codMarca = getCodMarca(codGrupoProd);
		if(codMarca == null)
			return "";
		
		String nomeMarca = getNomeMarca(codMarca);
		if(nomeMarca.length() > 3)
			nomeMarca = nomeMarca.substring(0, 3);
		
		return nomeMarca;
	}

	private static String getNomeMarca(BigDecimal codMarca) {
		String nomeMarca = "";
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

			sql.appendSql("SELECT DESCRICAO FROM TGFMAR WHERE CODIGO = "+codMarca);

			rset = sql.executeQuery();

			if (rset.next()) {
				if(rset.getString("DESCRICAO") != null)
					nomeMarca = rset.getString("DESCRICAO");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
		return nomeMarca;
	}

	private static BigDecimal getCodMarca(BigDecimal codGrupoProd) {
		BigDecimal codMarca = null;
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

			sql.appendSql("SELECT AD_CODMARCA FROM TGFGRU WHERE CODGRUPOPROD = "+codGrupoProd);

			rset = sql.executeQuery();

			if (rset.next()) {
				if(rset.getBigDecimal("AD_CODMARCA") != null)
					codMarca = rset.getBigDecimal("AD_CODMARCA");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
		return codMarca;
	}

	public static void verificarSeEquipamentoEstaCriadoNoSimova(String chassi, String filial) throws Exception {
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
			
			sql.appendSql("SELECT AD_INTEGRACAO FROM TGFVEI WHERE CHASSIS = '"+chassi+"'");
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			// Se query retornar, significa que equipamento já foi enviado
			if (rset.next() && rset.getBigDecimal("AD_INTEGRACAO") != null) {
				System.out.println("Equipamento já existe no Simova: "+rset.getBigDecimal("AD_INTEGRACAO"));
				return;
			} else {
				System.out.println("Equipamento não existe no Simova. Cadastrando...");
				enviarEquipamentoAoSimova(chassi, filial);
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

	public static void enviarEquipamentoAoSimova(String chassi, String filial) throws Exception {
		BigDecimal codVeiculo = getCodVeiculoPeloChassi(chassi);
		DynamicVO equipamentoVO = null;
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper veiculoDAO = JapeFactory.dao(DynamicEntityNames.VEICULO);
			equipamentoVO = veiculoDAO.findByPK(codVeiculo);

			BigDecimal codEmpFilial = getCodEmpDaFilial(filial);
			//se equipamento tiver Filial vinculada, apenas envia ao simova, se não houver, vincula o mesmo a filial da OS
			if(((BigDecimal) equipamentoVO.getProperty("AD_FILIAL")) != null)
				ControllerEquipamento.enviarEquipamentoPorDynamicVO(equipamentoVO);
			else
				atualizarFilialDoEquipamentoParaFilialAtual(equipamentoVO, codEmpFilial);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

	private static void atualizarFilialDoEquipamentoParaFilialAtual(DynamicVO equipamentoVO, BigDecimal filial) throws Exception {
		SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeFactory.dao(DynamicEntityNames.VEICULO).prepareToUpdate(equipamentoVO)
				.set("AD_FILIAL", filial)
				.update();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

	public static BigDecimal getCodEmpDaFilial(String filial) throws Exception {
		BigDecimal codEmpFilial = null;
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
			
			sql.appendSql("SELECT CODEMP FROM TSIEMP WHERE AD_CODFILIAL = '"+filial+"'");
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				codEmpFilial = rset.getBigDecimal("CODEMP");
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
		return codEmpFilial;
	}

	private static BigDecimal getCodVeiculoPeloChassi(String chassi) throws Exception {
		BigDecimal codVeiculo = null;
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

			sql.appendSql("SELECT CODVEICULO FROM TGFVEI WHERE CHASSIS = '"+chassi+"'");
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();

			if (rset.next()) {
				codVeiculo = rset.getBigDecimal("CODVEICULO");
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
		
		return codVeiculo;
	}

	public static void verificarSePecaEstaCriadoNoSimova(String codProd) throws Exception {
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
			
			sql.appendSql("SELECT AD_IDINTEGRACAO FROM TGFPRO WHERE CODPROD = "+codProd);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			// Se query retornar, significa que peça já foi enviado
			if (rset.next() && rset.getBigDecimal("AD_IDINTEGRACAO") != null) {
				System.out.println("Peça já existe no Simova: "+rset.getBigDecimal("AD_IDINTEGRACAO"));
				return;
			} else {
				System.out.println("Peça não existe no Simova. Cadastrando...");
				enviarPecaAoSimova(codProd);
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

	public static void enviarPecaAoSimova(String codProd) throws Exception {
		DynamicVO pecasVO = null;
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper parceiroDAO = JapeFactory.dao(DynamicEntityNames.PRODUTO);
			pecasVO = parceiroDAO.findByPK(codProd);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
		
		ControllerPeca.enviarPecaPorDynamicVO(pecasVO);
	}

	public static void verificarSeServicoEstaCriadoNoSimova(BigDecimal codServ) throws Exception {
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
			
			sql.appendSql("SELECT AD_IDINTEGRACAO FROM TGFPRO WHERE CODPROD = "+codServ);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			// Se query retornar, significa que serviço já foi enviado
			if (rset.next() && rset.getBigDecimal("AD_IDINTEGRACAO") != null) {
				System.out.println("Serviço já existe no Simova: "+rset.getBigDecimal("AD_IDINTEGRACAO"));
				return;
			} else {
				System.out.println("Serviço não existe no Simova. Cadastrando...");
				enviarServicoAoSimova(codServ);
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

	public static void enviarServicoAoSimova(BigDecimal codServ) throws Exception {
		DynamicVO ServicosVO = null;
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper servicoDAO = JapeFactory.dao(DynamicEntityNames.SERVICO);
			ServicosVO = servicoDAO.findByPK(codServ);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
		
		ControllerServico.enviarServicoPorDynamicVO(ServicosVO);
	}

	public static void verificarSeTecnicoEstaCriadoNoSimova(String codParcStr, String filial) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal codEmpFilial = getCodEmpDaFilial(filial);
		BigDecimal codParc = new BigDecimal(codParcStr);
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT IDINTEGRACAO FROM AD_FILIAISPARC WHERE CODPARC = "+codParc+" AND CODEMP = "+codEmpFilial);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			// Se query retornar, significa que funcionario já foi enviado
			if (rset.next() && rset.getBigDecimal("IDINTEGRACAO") != null) {
				System.out.println("Funcionário já existe no Simova: "+rset.getBigDecimal("IDINTEGRACAO"));
				return;
			} else {
				System.out.println("Funcionário não existe no Simova. Cadastrando...");
				vincularFuncionarioComFilialDaOs(codParc, codEmpFilial);
				enviarTecnicoAoSimova(codParc);
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

	public static void vincularFuncionarioComFilialDaOs(BigDecimal codParc, BigDecimal filial) throws Exception {
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper filialFuncionarioDAO = JapeFactory.dao("AD_FILIAISPARC");
			@SuppressWarnings("unused")
			DynamicVO save = filialFuncionarioDAO.create()
				.set("CODPARC", codParc)
				.set("CODEMP", filial)
				.save();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

	public static void enviarTecnicoAoSimova(BigDecimal codParc) throws Exception {
		DynamicVO funcionarioVO = null;
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper parceiroDAO = JapeFactory.dao(DynamicEntityNames.PARCEIRO);
			funcionarioVO = parceiroDAO.findByPK(codParc);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
		
		ControllerFuncionario.enviarFuncionarioPorDynamicVO(funcionarioVO);
	}

	public static void verificarSeTipoOsEstaCriadoNoSimova(BigDecimal codTipoOs) throws Exception {
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
			
			sql.appendSql("SELECT IDINTEGRACAO FROM AD_TIPOORDEMSERVICO WHERE CODTIPODEOS = "+codTipoOs);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			// Se query retornar, significa que tipo de os já foi enviado
			if (rset.next() && rset.getBigDecimal("IDINTEGRACAO") != null) {
				System.out.println("Tipo de OS já existe no Simova: "+rset.getBigDecimal("IDINTEGRACAO"));
				return;
			} else {
				System.out.println("Tipo de OS não existe no Simova. Cadastrando...");
				enviarTipoDeOsAoSimova(codTipoOs);
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

	public static void enviarTipoDeOsAoSimova(BigDecimal codTipoOs) throws Exception {
		DynamicVO tipoOsVO = null;
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper tipoOsDAO = JapeFactory.dao("AD_TIPOORDEMSERVICO");
			tipoOsVO = tipoOsDAO.findByPK(codTipoOs);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
		
		ControllerTipoOS.enviarTipoOSPorDynamicVO(tipoOsVO);
	}

	public static BigDecimal getCodParcPeloUsuario(BigDecimal codUsuRespOficina) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal dado = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT CODPARC FROM TSIUSU WHERE CODUSU = "+codUsuRespOficina);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal("CODPARC");
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

	public static BigDecimal getCodGrupoDoServico(BigDecimal codServ) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal dado = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT CODGRUPOPROD FROM TGFPRO WHERE CODPROD = "+codServ);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal("CODGRUPOPROD");
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

	public static String getUrlAcesso() throws Exception {
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
			
			sql.appendSql("SELECT TEXTO FROM TSIPAR WHERE CHAVE = 'SIMOVAURLINT'");
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getString(1);
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

	public static BigDecimal getCodServicoPelaRefFornecedor(String refForn) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		BigDecimal dado = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			sql = new NativeSql(jdbc);
			
			sql.appendSql("SELECT CODPROD FROM TGFPRO WHERE REFFORN = '"+refForn+"'");
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getBigDecimal("CODPROD");
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

	public static String getUrlIntegrarOS() throws Exception {
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
			
			sql.appendSql("SELECT TEXTO FROM TSIPAR WHERE CHAVE = 'SIMOVAURLINTOS'");
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getString(1);
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

	public static void integrarOS(String urlIntegrar, BigDecimal numOs, String filial) throws Exception {
		Autenticacao auth = Controller.getParametrosSankhya("", filial);
		URL url = new URL(auth.getUrl());
		URL urlIntegrarOS = new URL(urlIntegrar);
		
		System.out.println("Autenticando...");
		Token token = Controller.getToken(auth, url);
		
		System.out.println("Integrando a OS...");
		HttpURLConnection conn = (HttpURLConnection) urlIntegrarOS.openConnection();
		conn.setRequestMethod("GET");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("authorization", token.getToken());
		
		StringBuilder body = new StringBuilder();
		
		body.append("{\r\n"
				+ "\"CodigoOs\": \""+numOs+"\"\r\n"
				+ "}\r\n");
		
		String data = body.toString();
        
		System.out.println("URL: "+urlIntegrarOS.toString());
		System.out.println("Request Method: "+conn.getRequestMethod());
		System.out.println("Content-Type: "+conn.getRequestProperty("Content-Type"));
        System.out.println("BODY: "+data);
        
        byte[] out = data.getBytes(StandardCharsets.UTF_8);
        
        OutputStream stream = conn.getOutputStream();
        stream.write(out);
        
        String resposta = null;
        try {
        	resposta = Controller.getResponseBody(conn);
        } catch(Exception e) {
        	resposta = Controller.getErrorStream(conn);
        	
        	RetornoCadastro[] retornoErro = new Gson().fromJson(resposta, RetornoCadastro[].class); 
            
        	System.out.println("Erro na requisição!");
            System.out.println("Resposta HTTP: "+conn.getResponseCode()+" "+conn.getResponseMessage());
        	System.out.println("Retorno: "+retornoErro[0].getMsg());
        	
        	throw new Exception("Erro na requisição: "+retornoErro[0].getMsg());
        }
        
        System.out.println("Resposta: "+conn.getResponseCode()+" "+conn.getResponseMessage());
        
//      fecharOS(codOs);
        conn.disconnect();
	}

	public static boolean verificaFlagDeEnvioParaPeca(BigDecimal codGrupoProduto) throws Exception {
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
			
			sql.appendSql("SELECT AD_ENVIASIMOVA FROM TGFGRU WHERE CODGRUPOPROD = "+codGrupoProduto);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getString("AD_ENVIASIMOVA");
				if("S".equals(dado))
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

	public static boolean verificaFlagDeEnvioParaVeiculo(BigDecimal nroUnicoModelo) throws Exception {
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
			
			sql.appendSql("SELECT ENVIASIMOVA FROM AD_MODELOVEI WHERE NROUNICO = "+nroUnicoModelo);
			System.out.println("SQL: "+sql.toString());
			
			rset = sql.executeQuery();
			
			if (rset.next()) {
				dado = rset.getString("ENVIASIMOVA");
				if("S".equals(dado))
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

}
