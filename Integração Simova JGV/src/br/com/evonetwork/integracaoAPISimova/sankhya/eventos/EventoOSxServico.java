package br.com.evonetwork.integracaoAPISimova.sankhya.eventos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;

import com.google.gson.Gson;
import com.sankhya.util.JdbcUtils;

import br.com.evonetwork.integracaoAPISimova.Controller.Controller;
import br.com.evonetwork.integracaoAPISimova.Model.Autenticacao;
import br.com.evonetwork.integracaoAPISimova.Model.OSxServico;
import br.com.evonetwork.integracaoAPISimova.Model.RetornoCadastro;
import br.com.evonetwork.integracaoAPISimova.Model.Token;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class EventoOSxServico implements EventoProgramavelJava{

	@Override
	public void afterInsert(PersistenceEvent event) throws Exception {
		criarAtualizarOSxServico(event);
	}
	
	@Override
	public void afterUpdate(PersistenceEvent event) throws Exception {
		criarAtualizarOSxServico(event);
	}
	
	private void criarAtualizarOSxServico(PersistenceEvent event) {
		System.out.println("***Iniciando integração SIMOVA - OSxSERVIÇO***");
		try {
			String completarURL = "OS_SERVICO";
			
			DynamicVO OSxServicosVO = (DynamicVO) event.getVo();
			
			String filial = Controller.getFilial(getFilial((BigDecimal) OSxServicosVO.getProperty("NUMOS")));
			
			if("".equals(filial)) {
				System.out.println("Filial não encontrada! Não é possível integrar!");
				return;
			}
			
			OSxServico OSxServico = setOSxServico(OSxServicosVO, filial);
			
			Autenticacao auth = Controller.getParametrosSankhya(completarURL, filial);
			
			URL url = new URL(auth.getUrl());
			URL urlOSxServico = new URL(auth.getUrlAcesso());
			
			System.out.println("Autenticando...");
			Token token = Controller.getToken(auth, url);
			
			System.out.println("Enviando OSxServiço ao Simova...");
			String idSimova = criarOSxServicoSimova(token, urlOSxServico, OSxServico);
			
			atualizarIdIntegracao(idSimova, OSxServicosVO);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("***Fim integração SIMOVA - OSxSERVIÇO***");
	}

	private void atualizarIdIntegracao(String idSimova, DynamicVO oSxServicosVO) {
		SessionHandle hnd = null;
		BigDecimal numOs = oSxServicosVO.asBigDecimal("NUMOS");
		BigDecimal numItem = oSxServicosVO.asBigDecimal("NUMITEM");
		System.out.println("Atualizando OSxServiço "+numOs+", "+numItem+" com IDINTEGRACAO "+idSimova+"...");
		try {
			hnd = JapeSession.open();

			JapeFactory.dao("TCSITE").
			prepareToUpdate(oSxServicosVO)
				.set("IDINTEGRACAO", new BigDecimal(idSimova))
				.update();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JapeSession.close(hnd);
		}
		System.out.println("Atualizado.");
	}

	private String criarOSxServicoSimova(Token token, URL urlOSxServico, OSxServico OSxServico) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) urlOSxServico.openConnection();
		conn.setRequestMethod("POSxServicoT");
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
				+ "\"Ativo\": \""+OSxServico.getAtivo()+"\""
				+ "}\r\n");
		body.append("]");
		
		String data = body.toString();
        
        System.out.println("BODY: "+data);
        
        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = conn.getOutputStream();
        stream.write(out);
        
        String respOSxServicota = getResponseBody(conn);
        
        RetornoCadastro[] retornoOSxServicos = new Gson().fromJson(respOSxServicota, RetornoCadastro[].class); 
        
        System.out.println("RespOSxServicota HTTP: "+conn.getResponseCode()+" "+conn.getResponseMessage());
        
    	System.out.println("Retorno: "+retornoOSxServicos[0].getMsg());
        System.out.println("ID OSxServico: "+retornoOSxServicos[0].getId());
        
        conn.disconnect();
        
        return retornoOSxServicos[0].getId();
	}

	private OSxServico setOSxServico(DynamicVO OSxServicosVO, String filial) {
		OSxServico OSxServico = new OSxServico();
		
		//AD_TCSITE
		
		OSxServico.setFilial(filial);
		OSxServico.setLocal(filial);
		OSxServico.setCodOs(((BigDecimal) OSxServicosVO.getProperty("NUMOS")).toString());
		OSxServico.setCodTipoTempo(((BigDecimal) OSxServicosVO.getProperty("CODTIPOTEMPO")).toString());
		OSxServico.setNroRequisicao(((BigDecimal) OSxServicosVO.getProperty("NUMITEM")).toString());
		OSxServico.setCodTipoServico(getTipoServico((BigDecimal) OSxServicosVO.getProperty("CODSERV")));
		OSxServico.setTempoPadrao(getTempoPadrao((BigDecimal) OSxServicosVO.getProperty("CODSERV")));
		OSxServico.setTempoCobrado("0");
		OSxServico.setCodServico(((BigDecimal) OSxServicosVO.getProperty("CODSERV")).toString());
		OSxServico.setCodMarca(coletarCodMarcaServico((BigDecimal) OSxServicosVO.getProperty("CODSERV")));
		OSxServico.setAtivo("1");
		
		return OSxServico;
	}
	
	private String getTipoServico(BigDecimal codServico) {
		JapeSession.SessionHandle hnd = null;
		String retorno = "";
		try {
			hnd = JapeSession.open();
			JapeWrapper servicoDAO = JapeFactory.dao(DynamicEntityNames.SERVICO);
			DynamicVO servico = servicoDAO.findByPK(codServico);
			retorno = servico.asString("TIPO");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JapeSession.close(hnd);
		}
		return retorno;
	}

	private String getTempoPadrao(BigDecimal codServico) {
		JapeSession.SessionHandle hnd = null;
		String retorno = "";
		try {
			hnd = JapeSession.open();
			JapeWrapper servicoDAO = JapeFactory.dao(DynamicEntityNames.SERVICO);
			DynamicVO servico = servicoDAO.findByPK(codServico);
			retorno = servico.asBigDecimal("AD_TEMPPADRAO")+"";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JapeSession.close(hnd);
		}
		return retorno;
	}

//	private String convertHour(String mDate) {
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

	private String coletarCodMarcaServico(BigDecimal codGrupoProd) {
		String codMarca = null;
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
				codMarca = rset.getBigDecimal("AD_CODMARCA").toString();
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

	private BigDecimal getFilial(BigDecimal numOs) {
		JapeSession.SessionHandle hnd = null;
		BigDecimal codEmp = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper tcsoseDAO = JapeFactory.dao("AD_TCSOSE");
			DynamicVO nota = tcsoseDAO.findByPK(numOs);
			codEmp = nota.asBigDecimal("CODEMP");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JapeSession.close(hnd);
		}
		return codEmp;
	}

	private static String getResponseBody(HttpURLConnection conn) {
		BufferedReader br = null;
        StringBuilder body = null;
        String line = "";
        try {
            br = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            body = new StringBuilder();
            while ((line = br.readLine()) != null)
                body.append(line);
            return body.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}

	@Override
	public void afterDelete(PersistenceEvent event) throws Exception {}
	@Override
	public void beforeCommit(TransactionContext ctx) throws Exception {}
	@Override
	public void beforeDelete(PersistenceEvent event) throws Exception {}
	@Override
	public void beforeInsert(PersistenceEvent event) throws Exception {}
	@Override
	public void beforeUpdate(PersistenceEvent event) throws Exception {}
}