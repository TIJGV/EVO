package br.com.evonetwork.atualizaPedido.DAO;

//import java.io.BufferedReader;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.nio.charset.StandardCharsets;
//
//import br.com.sankhya.modelcore.util.MGECoreParameter;

public class NegocioXProdutoDAO {
//	private static void alterarNegocioXProduto(String empresa)
//			throws Exception {
//		System.out.println("Alterando cliente para processado");
//		try {
//			String urlBase = MGECoreParameter.getParameterAsString("URLBASECOSD");
//			int grupo = MGECoreParameter.getParameterAsInt("GRUPOCOSD");
//			String strUrl = urlBase + "/grupo/" + grupo + "/empresa/" + empresa + "/Negocio/"
//					+ codNegocio + "/NegocioXProduto/ " + codNegocioXProduto + "/" + idNegocioXProduto;
//			String auth = "BASIC " + MGECoreParameter.getParameterAsString("AUTHCOSD");
//			System.out.println("URL: " + strUrl + "\n Auth: " + auth);
//			URL url = new URL(strUrl);
//			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//			conn.setRequestMethod("PUT");
//			conn.setDoOutput(true);
//			conn.setRequestProperty("Content-Type", "application/json");
//			conn.setRequestProperty("Authorization", auth);
//
//			String body = "";
//			System.out.println("Body: " + body);
//			byte[] out = body.getBytes(StandardCharsets.UTF_8);
//
//			OutputStream stream = conn.getOutputStream();
//			stream.write(out);
//
//			String resposta = null;
//			try {
//				resposta = getResponseBody(conn);
//			} catch (Exception e) {
//				try {
//					resposta = getErrorStream(conn);
//				} catch (Exception e1) {
//					System.out.println("Nenhum ErrorStream retornado: " + e1.getMessage());
//				}
//				System.out.println("Erro na requisição!");
//				System.out.println("Resposta HTTP: " + conn.getResponseCode() + " " + conn.getResponseMessage());
//				throw new Exception(
//						"Erro na requisição: " + conn.getResponseCode() + ": " + conn.getResponseMessage());
//			}
//			System.out.println("Resposta HTTP: " + conn.getResponseCode() + " " + conn.getResponseMessage());
//			System.out.println("Resposta: " + resposta);
//			// TODO verificar se tem algum dado para retornar na resposta
//			conn.disconnect();
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new Exception("Erro no alteração do cliente para 'Processado': " + e.getMessage());
//		}
//	}
//	
//	private static String getResponseBody(HttpURLConnection conn) {
//		BufferedReader br = null;
//		StringBuilder body = null;
//		String line = "";
//		try {
//			br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
//			body = new StringBuilder();
//			while ((line = br.readLine()) != null)
//				body.append(line);
//			return body.toString();
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//	}
//
//	private static String getErrorStream(HttpURLConnection conn) throws Exception {
//		InputStream errorstream = conn.getErrorStream();
//		String response = "";
//		String line;
//		BufferedReader br = new BufferedReader(new InputStreamReader(errorstream, "UTF-8"));
//		while ((line = br.readLine()) != null) {
//			response += line;
//		}
//		return response;
//	}
}
