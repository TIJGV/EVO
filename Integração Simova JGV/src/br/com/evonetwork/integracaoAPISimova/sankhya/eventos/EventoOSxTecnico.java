package br.com.evonetwork.integracaoAPISimova.sankhya.eventos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;

import br.com.evonetwork.integracaoAPISimova.Controller.Controller;
import br.com.evonetwork.integracaoAPISimova.Model.Autenticacao;
import br.com.evonetwork.integracaoAPISimova.Model.OSxTecnico;
import br.com.evonetwork.integracaoAPISimova.Model.RetornoCadastro;
import br.com.evonetwork.integracaoAPISimova.Model.Token;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;

public class EventoOSxTecnico implements EventoProgramavelJava{

	@Override
	public void afterInsert(PersistenceEvent event) throws Exception {
		criarAtualizarOSxTecnico(event);
	}
	
	@Override
	public void afterUpdate(PersistenceEvent event) throws Exception {
		criarAtualizarOSxTecnico(event);
	}
	
	private void criarAtualizarOSxTecnico(PersistenceEvent event) {
		System.out.println("***Iniciando integração SIMOVA - OSxTECNICO***");
		try {
			String completarURL = "OS_TECNICO";
			
			DynamicVO OSxTecnicosVO = (DynamicVO) event.getVo();
			
			String filial = Controller.getFilial((BigDecimal) OSxTecnicosVO.getProperty("CODEMP"));
			
			if("".equals(filial)) {
				System.out.println("Filial não encontrada! Não é possível integrar!");
				return;
			}
			
			OSxTecnico OSxTecnico = setOSxTecnico(OSxTecnicosVO, filial);
			
			Autenticacao auth = Controller.getParametrosSankhya(completarURL, filial);
			
			URL url = new URL(auth.getUrl());
			URL urlOSxTecnico = new URL(auth.getUrlAcesso());
			
			System.out.println("Autenticando...");
			Token token = Controller.getToken(auth, url);
			
			System.out.println("Enviando OSxTecnico ao Simova...");
			String idSimova = criarOSxTecnicoSimova(token, urlOSxTecnico, OSxTecnico);
			
			atualizarIdIntegracao(idSimova, OSxTecnicosVO);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("***Fim integração SIMOVA - OSxTECNICO***");
	}

	private void atualizarIdIntegracao(String idSimova, DynamicVO oSxTecnicosVO) {
		SessionHandle hnd = null;
		BigDecimal numOs = oSxTecnicosVO.asBigDecimal("NUMOS");
		System.out.println("Atualizando OSxTecnico "+numOs+" com IDINTEGRACAOTECNICO "+idSimova+"...");
		try {
			hnd = JapeSession.open();

			JapeFactory.dao("TCSOSE").
			prepareToUpdate(oSxTecnicosVO)
				.set("IDINTEGRACAOTECNICO", new BigDecimal(idSimova))
				.update();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JapeSession.close(hnd);
		}
		System.out.println("Atualizado.");
	}

	private String criarOSxTecnicoSimova(Token token, URL urlOSxTecnico, OSxTecnico OSxTecnico) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) urlOSxTecnico.openConnection();
		conn.setRequestMethod("POSxTecnicoT");
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
		
		String data = body.toString();
        
        System.out.println("BODY: "+data);
        
        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = conn.getOutputStream();
        stream.write(out);
        
        String respOSxTecnicota = getResponseBody(conn);
        
        RetornoCadastro[] retornoOSxTecnicos = new Gson().fromJson(respOSxTecnicota, RetornoCadastro[].class); 
        
        System.out.println("RespOSxTecnicota HTTP: "+conn.getResponseCode()+" "+conn.getResponseMessage());
        
    	System.out.println("Retorno: "+retornoOSxTecnicos[0].getMsg());
        System.out.println("ID OSxTecnico: "+retornoOSxTecnicos[0].getId());
        
        conn.disconnect();
        
        return retornoOSxTecnicos[0].getId();
	}

	private OSxTecnico setOSxTecnico(DynamicVO OSxTecnicosVO, String filial) {
		OSxTecnico OSxTecnico = new OSxTecnico();
		
		//AD_TCSOSE
		
		OSxTecnico.setFilial(filial);
		OSxTecnico.setLocal(filial);
		OSxTecnico.setCodOS(((BigDecimal) OSxTecnicosVO.getProperty("NUMOS")).toString());
		OSxTecnico.setCodTecnico(((BigDecimal) OSxTecnicosVO.getProperty("CODPARCRESP")).toString());
		OSxTecnico.setCodStatusOS(getStatusOS((String) OSxTecnicosVO.getProperty("STATUS")));
		OSxTecnico.setAtivo("1");
		
		return OSxTecnico;
	}
	
	private String getStatusOS(String statusOS) {
		String retorno = "";
		if(statusOS != null) {
			switch(statusOS) {
				case("F"):
					retorno = "3";
					break;
				case("AV"):
					retorno = "1";
					break;
				case("A"):
					retorno = "1";
					break;
			}
		}
		return retorno;
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