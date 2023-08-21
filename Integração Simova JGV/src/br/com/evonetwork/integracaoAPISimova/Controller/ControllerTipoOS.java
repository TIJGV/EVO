package br.com.evonetwork.integracaoAPISimova.Controller;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;

import br.com.evonetwork.integracaoAPISimova.Model.Autenticacao;
import br.com.evonetwork.integracaoAPISimova.Model.RetornoCadastro;
import br.com.evonetwork.integracaoAPISimova.Model.TipoOS;
import br.com.evonetwork.integracaoAPISimova.Model.Token;
import br.com.sankhya.jape.vo.DynamicVO;

public class ControllerTipoOS {

	public static void enviarTipoOSPorDynamicVO(DynamicVO TipoOSsVO) {
		try {
			String completarURL = "TIPO_OS";
			
			TipoOS TipoOS = setTipoOS(TipoOSsVO);
			
			Autenticacao auth = Controller.getParametrosSankhya(completarURL, "MMU");
			
			URL url = new URL(auth.getUrl());
			URL urlTipoOS = new URL(auth.getUrlAcesso());
			
			Token token = Controller.getToken(auth, url);
			
			String idSimova = criarTipoOSSimova(token, urlTipoOS, TipoOS);
			
			if(((BigDecimal) TipoOSsVO.getProperty("IDINTEGRACAO") != null) && idSimova.equals(((BigDecimal) TipoOSsVO.getProperty("IDINTEGRACAO")).toString()))
				System.out.println("ID Simova não foi atualizado!");
			else
				Controller.atualizarIdIntegracao(idSimova, "IDINTEGRACAO", TipoOSsVO, "AD_TIPOORDEMSERVICO");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String criarTipoOSSimova(Token token, URL urlTipoOS, TipoOS TipoOS) throws Exception {
		HttpURLConnection conn = (HttpURLConnection) urlTipoOS.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("authorization", token.getToken());
		
		StringBuilder body = new StringBuilder();
		
		body.append("[\r\n");
		body.append("{\r\n"
				+ "\"Ativo\": \""+TipoOS.getAtivo()+"\",\r\n"
				+ "\"CodigoTipoOs\": \""+TipoOS.getCodTipoOS()+"\",\r\n"
				+ "\"Descricao\": \""+TipoOS.getDescTipoOS()+"\",\r\n"
				+ "\"Atendimento\": \""+TipoOS.getAtendimento()+"\"\r\n"
				+ "}\r\n");
		body.append("]");
		
		String data = body.toString();
        
		System.out.println("URL: "+urlTipoOS.toString());
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
        
        RetornoCadastro[] retornoTipoOSs = new Gson().fromJson(resposta, RetornoCadastro[].class); 
        
        System.out.println("Resposta HTTP: "+conn.getResponseCode()+" "+conn.getResponseMessage());
        
    	System.out.println("Retorno: "+retornoTipoOSs[0].getMsg());
        System.out.println("ID TipoOS: "+retornoTipoOSs[0].getId());
        
        conn.disconnect();
        
        return retornoTipoOSs[0].getId();
	}

	private static TipoOS setTipoOS(DynamicVO TipoOSsVO) {
		TipoOS TipoOS = new TipoOS();
		
		TipoOS.setAtivo("1");
		TipoOS.setCodTipoOS((BigDecimal) TipoOSsVO.getProperty("CODTIPODEOS")+"");
		TipoOS.setDescTipoOS((String) TipoOSsVO.getProperty("TIPOOS"));
		TipoOS.setAtendimento("1");
		
		return TipoOS;
	}
}
