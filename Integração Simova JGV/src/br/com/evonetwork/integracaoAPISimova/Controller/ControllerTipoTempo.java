package br.com.evonetwork.integracaoAPISimova.Controller;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;

import br.com.evonetwork.integracaoAPISimova.DAO.FilialPadrao;
import br.com.evonetwork.integracaoAPISimova.Model.Autenticacao;
import br.com.evonetwork.integracaoAPISimova.Model.RetornoCadastro;
import br.com.evonetwork.integracaoAPISimova.Model.TipoTempo;
import br.com.evonetwork.integracaoAPISimova.Model.Token;
import br.com.sankhya.jape.vo.DynamicVO;

public class ControllerTipoTempo {

	public static void enviarTipoTempoPorDynamicVO (DynamicVO TipoTemposVO) {
		System.out.println("Iniciando envio de tipo tempo ao Simova");
		String filial = FilialPadrao.getFilialPadrao();
		try {
			String completarURL = "TIPO_TEMPO";
			
			TipoTempo TipoTempo = setTipoTempo(TipoTemposVO);
			
			Autenticacao auth = Controller.getParametrosSankhya(completarURL, filial);
			
			URL url = new URL(auth.getUrl());
			URL urlTipoTempo = new URL(auth.getUrlAcesso());
			
			System.out.println("Autenticando...");
			Token token = Controller.getToken(auth, url);
			
			System.out.println("Enviando tipo de tempo ao Simova...");
			String idSimova = criarTipoTempoSimova(token, urlTipoTempo, TipoTempo);
			
			if(((BigDecimal) TipoTemposVO.getProperty("IDINTEGRACAO") != null) && idSimova.equals(((BigDecimal) TipoTemposVO.getProperty("IDINTEGRACAO")).toString()))
				System.out.println("ID Simova não foi atualizado!");
			else
				Controller.atualizarIdIntegracao(idSimova, "IDINTEGRACAO", TipoTemposVO, "AD_TIPOTEMPO");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Fim envio de tipo tempo ao Simova");
	}
	
	private static String criarTipoTempoSimova(Token token, URL urlTipoTempo, TipoTempo TipoTempo) throws Exception {
		HttpURLConnection conn = (HttpURLConnection) urlTipoTempo.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("authorization", token.getToken());
		
		StringBuilder body = new StringBuilder();
		
		body.append("[\r\n");
		body.append("{\r\n"
				+ "\"Ativo\": \""+TipoTempo.getAtivo()+"\",\r\n"
				+ "\"CodigoTipoTempo\": \""+TipoTempo.getCodTipoTempo()+"\",\r\n"
				+ "\"DescricaoTipoTempo\": \""+TipoTempo.getDescTipoTempo()+"\"\r\n"
				+ "}\r\n");
		body.append("]");
		
		String data = body.toString();
        
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
        
        RetornoCadastro[] retornoTipoTempos = new Gson().fromJson(resposta, RetornoCadastro[].class); 
        
        System.out.println("RespTipoTempota HTTP: "+conn.getResponseCode()+" "+conn.getResponseMessage());
        
    	System.out.println("Retorno: "+retornoTipoTempos[0].getMsg());
        System.out.println("ID TipoTempo: "+retornoTipoTempos[0].getId());
        
        conn.disconnect();
        
        return retornoTipoTempos[0].getId();
	}

	private static TipoTempo setTipoTempo(DynamicVO TipoTemposVO) {
		TipoTempo TipoTempo = new TipoTempo();
		
		String ativoSankhya = (String) TipoTemposVO.getProperty("ATIVO");
		int ativo = 0;
		
		if("S".equals(ativoSankhya)) {
			ativo = 1;
		}
		
		TipoTempo.setAtivo(""+ativo);
		TipoTempo.setCodTipoTempo(((BigDecimal) TipoTemposVO.getProperty("NROUNICO")).toString());
		TipoTempo.setDescTipoTempo((String) TipoTemposVO.getProperty("DESCRICAO"));
		
		return TipoTempo;
	}
}
