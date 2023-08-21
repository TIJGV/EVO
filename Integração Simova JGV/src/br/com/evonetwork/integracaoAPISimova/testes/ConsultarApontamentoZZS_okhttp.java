package br.com.evonetwork.integracaoAPISimova.testes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;

import br.com.evonetwork.integracaoAPISimova.Model.Autenticacao;
import br.com.evonetwork.integracaoAPISimova.Model.Token;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConsultarApontamentoZZS_okhttp {
	public static void main(String[] args) throws Exception {
		
		Autenticacao auth = new Autenticacao();
		
		auth.setUser("apiws@simova.com.br");
		auth.setPass("S1m0v@API");
		auth.setEmpresa("MMU");
		
		String strUrl = "https://grupojgv.h.simova.cloud/nfs/api/v1/auth";
		String strUrlFilial = "https://grupojgv.h.simova.cloud/nfs/api/v1/integration/VIEW_INTEGRACAO_ZZS";

		URL url = null;
		URL urlFilial = null;
		
		Token token = null;
		
		String codOs = "129";
		
		try {
			url = new URL(strUrl);
			urlFilial = new URL(strUrlFilial);
			
			token = getToken(auth, url);
			
			consultarApontamentoZZS(token, urlFilial, codOs);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void consultarApontamentoZZS(Token token, URL url, String codOs) throws Exception {
		
		OkHttpClient client = new OkHttpClient();

		String json = "{\r\n"
				+ "\"CodigoOs\": \""+codOs+"\"\r\n"
				+ "}";
		
		RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
		
		Request request = new Request.Builder()
		  .url(url)
		  .post(body)
		  .addHeader("authorization", token.getToken())
		  .addHeader("cache-control", "no-cache")
//		  .addHeader("postman-token", "0465578e-2915-c482-d425-8c00ee97ec6d")
		  .addHeader("Content-Type", "application/json")
		  .build();

		Response response = client.newCall(request).execute();
		
		System.out.println(response.body().string());
		
//		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//		conn.setRequestMethod("GET");
//		conn.setDoOutput(true);
//		conn.setRequestProperty("Content-Type", "application/json");
//		conn.setRequestProperty("authorization", token.getToken());
//		
//		System.out.println("URL: "+url.toString());
//		System.out.println("Request Method: "+conn.getRequestMethod());
//		System.out.println("Content-Type: "+conn.getRequestProperty("Content-Type"));
//		
//		StringBuilder body = new StringBuilder();
//		
//		body.append("{\r\n"
//				+ "\"CodigoOs\": \""+codOs+"\"\r\n"
//				+ "}");
//		
//		String data = body.toString();
//        
//        System.out.println("BODY: "+data);
//        
//        byte[] out = data.getBytes(StandardCharsets.UTF_8);
//
//        OutputStream stream = conn.getOutputStream();
//        stream.write(out);
//        
//        String resposta = null;
//        try {
//        	resposta = getResponseBody(conn);
//        } catch(Exception e) {
//        	resposta = getErrorStream(conn);
//        	
//        	RetornoCadastro[] retornoErro = new Gson().fromJson(resposta, RetornoCadastro[].class); 
//            
//        	System.out.println("Erro na requisição!");
//            System.out.println("Resposta HTTP: "+conn.getResponseCode()+" "+conn.getResponseMessage());
//        	System.out.println("Retorno: "+retornoErro[0].getMsg());
//        	
//        	throw new Exception("Erro na requisição: "+retornoErro[0].getMsg());
//        }
//        
//        ApontamentoZZS[] retornoApontamentoZZS = new Gson().fromJson(resposta, ApontamentoZZS[].class); 
//        
//        System.out.println("Resposta: "+conn.getResponseCode()+" "+conn.getResponseMessage());
////        System.out.println("Token: "+token.getToken());
//        
//        for(int i = 0; i < retornoApontamentoZZS.length; i++) {
//        	System.out.println("Filial"+retornoApontamentoZZS[i].getZzs_filial());
//            System.out.println("Local: "+retornoApontamentoZZS[i].getZzs_local());
//            System.out.println("NumOSv: "+retornoApontamentoZZS[i].getZzs_numosv());
//            System.out.println("TipoTempo: "+retornoApontamentoZZS[i].getZzs_tiptem());
//            System.out.println("TipServico: "+retornoApontamentoZZS[i].getZzs_tipser());
//            System.out.println("GrupoServico: "+retornoApontamentoZZS[i].getZzs_gruser());
//            System.out.println("CodServico: "+retornoApontamentoZZS[i].getZzs_codser());
//            System.out.println("TempoPadrao: "+retornoApontamentoZZS[i].getZzs_tempad());
//            System.out.println("CodProdutivo: "+retornoApontamentoZZS[i].getZzs_codpro());
//            System.out.println("UsuApp: "+retornoApontamentoZZS[i].getZzs_usuapp());
//            System.out.println("NomeProdutivo: "+retornoApontamentoZZS[i].getZzs_nompro());
//            System.out.println("DataInicial: "+retornoApontamentoZZS[i].getZzs_datini());
//            System.out.println("HoraInicial: "+retornoApontamentoZZS[i].getZzs_horini());
//            System.out.println("DataFinal: "+retornoApontamentoZZS[i].getZzs_datfin());
//            System.out.println("HoraFinal: "+retornoApontamentoZZS[i].getZzs_horfin());
//            System.out.println("NossoNum: "+retornoApontamentoZZS[i].getZzs_nosnum());
//            System.out.println("Kilometragem: "+retornoApontamentoZZS[i].getZzs_kilome());
//            System.out.println("HoraTrilha: "+retornoApontamentoZZS[i].getZzs_hortri());
//            System.out.println("DataLancamento: "+retornoApontamentoZZS[i].getZzs_dtlanc());
//            System.out.println("Flag: "+retornoApontamentoZZS[i].getZzs_flag());
//            System.out.println("KmInicial: "+retornoApontamentoZZS[i].getZzs_kilini());
//            System.out.println("KmFinal: "+retornoApontamentoZZS[i].getZzs_kilfin());
//            System.out.println("SeqDbOs: "+retornoApontamentoZZS[i].getSeq_db_os());
//            System.out.println("OsMan: "+retornoApontamentoZZS[i].getZzs_osman());
//            System.out.println("DataInsercao: "+retornoApontamentoZZS[i].getDatins());
//            System.out.println("xSerad: "+retornoApontamentoZZS[i].getZzs_xserad());
//            System.out.println("dtac: "+retornoApontamentoZZS[i].getZzs_dtac());
//            System.out.println("Placa: "+retornoApontamentoZZS[i].getZzs_placa());
//            System.out.println("Origem: "+retornoApontamentoZZS[i].getZzs_orig());
//            System.out.println("mPausa: "+retornoApontamentoZZS[i].getZzs_mpausa());
//            System.out.println("proc_st: "+retornoApontamentoZZS[i].getProc_st());
//        }
//        
//        conn.disconnect();
	}
	
//	private static String getErrorStream(HttpURLConnection conn) throws IOException {
//		InputStream errorstream = conn.getErrorStream();
//		String response = "";
//		String line;
//		BufferedReader br = new BufferedReader(new InputStreamReader(errorstream, "UTF-8"));
//		while ((line = br.readLine()) != null) {
//		    response += line;
//		}
//		return response;
//	}

	private static Token getToken(Autenticacao auth, URL url) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
//		conn.setRequestProperty("PHPSESSID", auth.getClientId());
        
        String data = "{\r\n"
        		+ "\"user\":\""+auth.getUser()+"\",\r\n"
        		+ "\"password\":\""+auth.getPass()+"\",\r\n"
        		+ "\"empresa\":\""+auth.getEmpresa()+"\"\r\n"
        		+ "}";
        
        System.out.println("BODY: "+data);
        
        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = conn.getOutputStream();
        stream.write(out);
        
        String resposta = getResponseBody(conn);
        
        Gson g = new Gson();
        Token token = g.fromJson(resposta, Token.class);
        
//        System.out.println("Resposta: "+conn.getResponseCode()+" "+conn.getResponseMessage());
//        
        System.out.println("Token: "+token.getToken());
        conn.disconnect();
        
        return token;
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
}
