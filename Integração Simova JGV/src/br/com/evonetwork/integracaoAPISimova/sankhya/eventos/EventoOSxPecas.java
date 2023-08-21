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
import br.com.evonetwork.integracaoAPISimova.Model.OSxPecas;
import br.com.evonetwork.integracaoAPISimova.Model.RetornoCadastro;
import br.com.evonetwork.integracaoAPISimova.Model.Token;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class EventoOSxPecas implements EventoProgramavelJava{

	@Override
	public void afterInsert(PersistenceEvent event) throws Exception {
		criarAtualizarOSxPecas(event);
	}
	
	@Override
	public void afterUpdate(PersistenceEvent event) throws Exception {
		criarAtualizarOSxPecas(event);
	}
	
	private void criarAtualizarOSxPecas(PersistenceEvent event) {
		System.out.println("***Iniciando integração SIMOVA - OSxPEÇA***");
		try {
			String completarURL = "OS_PECA";
			
			DynamicVO OSxPecassVO = (DynamicVO) event.getVo();
			
			String filial = Controller.getFilial(getFilial((BigDecimal) OSxPecassVO.getProperty("NUMOS")));
			
			if("".equals(filial)) {
				System.out.println("Filial não encontrada! Não é possível integrar!");
				return;
			}
			
			OSxPecas OSxPecas = setOSxPecas(OSxPecassVO, filial);
			
			Autenticacao auth = Controller.getParametrosSankhya(completarURL, filial);
			
			URL url = new URL(auth.getUrl());
			URL urlOSxPecas = new URL(auth.getUrlAcesso());
			
			System.out.println("Autenticando...");
			Token token = Controller.getToken(auth, url);
			
			System.out.println("Enviando OSxPeças ao Simova...");
			String idSimova = criarOSxPecasSimova(token, urlOSxPecas, OSxPecas);
			
			atualizarIdIntegracao(idSimova, OSxPecassVO);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("***Fim integração SIMOVA - OSxPEÇA***");
	}

	private void atualizarIdIntegracao(String idSimova, DynamicVO OSxPecassVO) {
		SessionHandle hnd = null;
		BigDecimal numOs = OSxPecassVO.asBigDecimal("NUMOS");
		BigDecimal seq = OSxPecassVO.asBigDecimal("SEQUENCIA");
		System.out.println("Atualizando OSxPeça "+numOs+", "+seq+" com IDINTEGRACAO "+idSimova+"...");
		try {
			hnd = JapeSession.open();

			JapeFactory.dao("TCSPRO").
			prepareToUpdate(OSxPecassVO)
				.set("IDINTEGRACAO", new BigDecimal(idSimova))
				.update();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JapeSession.close(hnd);
		}
		System.out.println("Atualizado.");
	}

	private String criarOSxPecasSimova(Token token, URL urlOSxPecas, OSxPecas OSxPecas) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) urlOSxPecas.openConnection();
		conn.setRequestMethod("POSxPecasT");
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
		
		String data = body.toString();
        
        System.out.println("BODY: "+data);
        
        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = conn.getOutputStream();
        stream.write(out);
        
        String respOSxPecasta = getResponseBody(conn);
        
        RetornoCadastro[] retornoOSxPecass = new Gson().fromJson(respOSxPecasta, RetornoCadastro[].class); 
        
        System.out.println("RespOSxPecasta HTTP: "+conn.getResponseCode()+" "+conn.getResponseMessage());
        
    	System.out.println("Retorno: "+retornoOSxPecass[0].getMsg());
        System.out.println("ID OSxPecas: "+retornoOSxPecass[0].getId());
        
        conn.disconnect();
        
        return retornoOSxPecass[0].getId();
	}

	private OSxPecas setOSxPecas(DynamicVO OSxPecassVO, String filial) {
		OSxPecas OSxPecas = new OSxPecas();
		
		//AD_TCSPRO
		
		OSxPecas.setFilial(filial);
		OSxPecas.setLocal(filial);
		OSxPecas.setCodOS(((BigDecimal) OSxPecassVO.getProperty("NUMOS")).toString());
		OSxPecas.setCodProduto(((BigDecimal) OSxPecassVO.getProperty("CODPROD")).toString());
		OSxPecas.setQtdRequisitada(((BigDecimal) OSxPecassVO.getProperty("QUANTIDADE")).toString());
		OSxPecas.setQtdUtilizada("0");
		OSxPecas.setQtdDevolvida("0");
		OSxPecas.setAtivo("1");
		
		return OSxPecas;
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