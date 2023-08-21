package br.com.evonetwork.importarParceiros.Controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import br.com.evonetwork.importarParceiros.Model.Autenticacao;
import br.com.evonetwork.importarParceiros.Model.CamposImportacao;

public class Controller {
	
	static ArrayList<CamposImportacao> camposImp = new ArrayList<>();

	public static void importar() throws Exception {
		int importados = 0;
		XSSFWorkbook workbook = null;
        
//        FileInputStream arquivo = new FileInputStream(new File(
//                "C:\\Users\\João Paulo\\Desktop\\Desenvolvimentos\\JGV\\ImportacaoParceiros\\DADOSFORNECEDORES.xlsx"));
		
		OPCPackage pkg = OPCPackage.open(new File("C:\\Users\\João Paulo\\Desktop\\Desenvolvimentos\\JGV\\ImportacaoParceiros\\DADOSFORNECEDORES.xlsx"));
        
		workbook = new XSSFWorkbook(pkg);
		
		importados = lerArquivo(workbook);
		
		if(importados == 0) {
			System.out.println("Nenhuma linha foi encontrada.");
		} else {
			System.out.println("Arquivo importado! Foram criados "+importados+" registros.");
		}
		
	}
	
	private static int lerArquivo(XSSFWorkbook workbook) throws Exception {
		
		String servidor = "unapel.nuvemdatacom.com.br:9706";
		
		int contador = 0;
		
		Sheet sheet = workbook.getSheetAt(0);
		ArrayList<String> campo = new ArrayList<String>();
		ArrayList<String> tabela = new ArrayList<String>();
		ArrayList<String> tipoCampo = new ArrayList<String>();
		ArrayList<String> coluna = new ArrayList<String>();
		int linhaAtual = 0;
		
		getCamposColunas(campo, tabela, coluna, tipoCampo);
		
		//removendo cabeçalho da planilha
		sheet.removeRow(sheet.getRow(0));
		sheet.removeRow(sheet.getRow(1));
		
        Iterator<Row> rowIterator = sheet.iterator();
        
        //LOGAR NA API
  		System.out.println("Logando...");
  		Autenticacao auth = fazerLogin(servidor);
      		
        while (rowIterator.hasNext()) {
        	camposImp = new ArrayList<>();
            Row row = rowIterator.next();
            System.out.println("***LENDO LINHA: "+linhaAtual);
            
    		try {
    			for(int j=0; j < coluna.size(); j++) {
    				String conteudoString = null;
    				int numLinha = 0;
    				int calcularLinha = 0;
    				
    				// pegando coluna como char
    				if(coluna.get(j).length() == 1) {
	    				char ch = coluna.get(j).charAt(0);
	    				// transformado char em inteiro (A = 0, B = 1, C = 2, ...)
	    				numLinha = (int) ch - 65;
    				} else if(coluna.get(j).length() > 1) {
    					char ch1 = coluna.get(j).charAt(0);
    					int x = (int) ch1 - 64;
    					calcularLinha = 26 * x;
	    				char ch = coluna.get(j).charAt(1);
	    				// transformado char em inteiro (AA = 26, AB = 27, AC = 28, ...)
	    				numLinha = (int) ch - 65 + calcularLinha;
    				}
    				
    				Cell cell = row.getCell(numLinha);
    				
    				// DEFINIR TIPO DE DADOS DA CELULA
    				cell.setCellType(Cell.CELL_TYPE_STRING);
    				
    				camposImp.add(new CamposImportacao());
    				int i = camposImp.size() - 1;
    				camposImp.get(i).setNomeCampo(campo.get(j));
    				camposImp.get(i).setNomeTabela(tabela.get(j));
    				
    				if("CGC_CPF".equals(campo.get(j)) || "IDENTINSCESTAD".equals(campo.get(j))) {
    					conteudoString = cell.getStringCellValue()
    							.replace(".", "" )
        			            .replace(" ", "")
        			            .replace("-", "")
        			            .replace("/", "")
        			            .replace("\u00AD", "")
        			            .trim();
    					if(conteudoString.length() == 0) {
    						conteudoString = "ISENTO";
    					}
    					camposImp.get(i).setConteudoString(conteudoString);
    					camposImp.get(i).setTipoConteudo(2);
    				} else {
    					conteudoString = cell.getStringCellValue().trim();
//    					if("NOMEPARC".equals(campo.get(j))) {
//    						conteudoString = conteudoString.replaceFirst(" ", "");
//    					}
    					camposImp.get(i).setConteudoString(conteudoString);
    					camposImp.get(i).setTipoConteudo(2);
    				}
    			}
    		} catch (Exception e) {
    			System.out.println("Ocorreu um erro na leitura do arquivo, verifique o arquivo e a configuração da importação.");
    		}
            
            contador = contador + importarSankhya(linhaAtual, servidor, auth);
    		linhaAtual++;
        }
        
        System.out.println("Deslogando...");
		fazerLogout(servidor);
		return contador;
	}
	
	private static void getCamposColunas(ArrayList<String> campo, ArrayList<String> tabela, 
			ArrayList<String> coluna, ArrayList<String> tipoCampo) throws Exception {
		coluna.add("B");
		tabela.add("TGFPAR");
		campo.add("NOMEPARC");
		tipoCampo.add("S");
		
		coluna.add("M");
		tabela.add("TGFPAR");
		campo.add("EMAIL");
		tipoCampo.add("S");
		
		coluna.add("N");
		tabela.add("TGFPAR");
		campo.add("CGC_CPF");
		tipoCampo.add("S");

		coluna.add("O");
		tabela.add("TGFPAR");
		campo.add("TIPPESSOA");
		tipoCampo.add("S");
		
		coluna.add("P");
		tabela.add("TGFPAR");
		campo.add("IDENTINSCESTAD");
		tipoCampo.add("S");
	}

	private static int importarSankhya(int linhaAtual, String servidor, Autenticacao auth) throws Exception {
		/* adicionando dados de cada linha do .xlsx no Sankhya */
		
		StringBuilder campos = new StringBuilder();
		
		for(int j = 1; j <= camposImp.size(); ++j){
			try {
				String campo = camposImp.get(j-1).getNomeCampo();
				String conteudoString = camposImp.get(j-1).getConteudoString().trim();
				
				campos.append("\""+campo+"\":{\"$\":\""+conteudoString+"\"},\r\n");
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		campos.append("\"CODCID\":{\"$\":\"1\"},\r\n");
		campos.append("\"FORNECEDOR\":{\"$\":\"S\"}");
		
		System.out.println("\nCriando Parceiro...");
//		System.out.println(campos.toString());
		criarParceiro(servidor, auth.getJsessionid(), campos.toString());
		
		return 1;
	}
	
	private static void criarParceiro(String servidor, String jsessionid, String campos) {
		String retorno = null;
		String cookie = "JSESSIONID="+jsessionid+".master";
		try {
			String strUrl = "http://"+servidor+"/mge/service.sbr?serviceName=CRUDServiceProvider.saveRecord&outputType=json&mgeSession="+jsessionid;
//			System.out.println(strUrl);
			URL url = new URL(strUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Cookie", cookie);
	        
			String data = "{\r\n"
	        		+ "  \"serviceName\": \"CRUDServiceProvider.saveRecord\",\r\n"
	        		+ "  \"requestBody\": {\r\n"
	        		+ "      \"dataSet\": {\r\n"
	        		+ "          \"rootEntity\": \"Parceiro\",\r\n"
	        		+ "          \"includePresentationFields\": \"N\",\r\n"
	        		+ "          \"dataRow\": {\r\n"
	        		+ "              \"localFields\": {\r\n"
	        		+ "					 "+campos+"\r\n"
	        		+ "              }\r\n"
	        		+ "          },\r\n"
	        		+ "          \"entity\": {\r\n"
	        		+ "              \"fieldset\": {\r\n"
	        		+ "                  \"list\": \"*\"\r\n"
	        		+ "              }\r\n"
	        		+ "          }\r\n"
	        		+ "      }\r\n"
	        		+ "  }\r\n"
	        		+ "}";
	        
//	        System.out.println(data);
	        
	        byte[] out = data.getBytes(StandardCharsets.UTF_8);
	        OutputStream stream;
			stream = conn.getOutputStream();
			stream.write(out);
			
			retorno = getResponseBody(conn);
			
			System.out.println("\nResposta requisição: "+retorno);
			
	        conn.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean isCellEmpty(Cell cell) {
	    if (cell == null) {
	        return true;
	    }
	    if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
	        return true;
	    }
	    if (cell.getCellType() == Cell.CELL_TYPE_STRING && cell.getStringCellValue().trim().isEmpty()) {
	        return true;
	    }
	    return false;
	}
	
	private static void fazerLogout(String servidor) {
//		String retorno = null;
		try {
			String strUrl = "http://"+servidor+"/mge/service.sbr?serviceName=MobileLoginSP.logout&outputType=json";
			URL url = new URL(strUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/json");
	        
	        String data = "{\r\n"
	        		+ "     \"serviceName\":\"MobileLoginSP.logout\",\r\n"
	        		+ "     \"status\":\"1\",\r\n"
	        		+ "     \"pendingPrinting\":\"false\",\r\n"
	        		+ "  }";
	        
	        byte[] out = data.getBytes(StandardCharsets.UTF_8);
	        OutputStream stream;
			stream = conn.getOutputStream();
			stream.write(out);
			
	        conn.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Autenticacao fazerLogin(String servidor) {
		Autenticacao auth = new Autenticacao();
		
		getJsessionID(auth, servidor);
		
		return auth;
	}
	
	private static void getJsessionID(Autenticacao auth, String servidor) {
		String retorno = null;
		String jsessionid = null;
		
		// login/senha
		String username = "SUP";
		String pass = "@Grupojgv2022*@";
		
		try {
			String strUrl = "http://"+servidor+"/mge/service.sbr?serviceName=MobileLoginSP.login&outputType=json";
			URL url = new URL(strUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/json");
	        
	        String data = "{\r\n"
	        		+ "   \"serviceName\": \"MobileLoginSP.login\",\r\n"
	        		+ "      \"requestBody\": {\r\n"
	        		+ "           \"NOMUSU\": {\r\n"
	        		+ "               \"$\": \""+username+"\"\r\n"
	        		+ "           },\r\n"
	        		+ "           \"INTERNO\":{\r\n"
	        		+ "              \"$\":\""+pass+"\"\r\n"
	        		+ "           },\r\n"
	        		+ "          \"KEEPCONNECTED\": {\r\n"
	        		+ "              \"$\": \"S\"\r\n"
	        		+ "          }\r\n"
	        		+ "      }\r\n"
	        		+ "  }";
	        
	        //System.out.println(data);
	        
	        byte[] out = data.getBytes(StandardCharsets.UTF_8);
	
	        OutputStream stream;
		
			stream = conn.getOutputStream();
			stream.write(out);
			
			retorno = getResponseBody(conn);
			
			jsessionid = retorno.substring(212, 252);
			
//			System.out.println("JSESSIONID: "+jsessionid);
			
	        conn.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
        auth.setJsessionid(jsessionid);
	}
	
	public static String getResponseBody(HttpURLConnection conn) {
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
