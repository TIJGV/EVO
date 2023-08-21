package br.com.evonetwork.importarProdutos.Controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.sankhya.util.JdbcUtils;
import com.sankhya.util.TimeUtils;

import br.com.evonetwork.importarProdutos.Model.CamposImportacao;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.QueryExecutor;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class Controller {
	
	static ArrayList<CamposImportacao> camposImp = new ArrayList<>();

	public static void importar(ContextoAcao ca, Registro linha) throws Exception {
		BigDecimal nroUnico = (BigDecimal) linha.getCampo("NROUNICO");
		BigDecimal nroUnicoCofig = (BigDecimal) linha.getCampo("NROUNICOCONFIG");
		
		DynamicVO impProVO = null;
		InputStream is = null;
		int importados = 0;
		Workbook workbook = null;
		
		JapeWrapper daoInfo = JapeFactory.dao("AD_IMPPRO");
		impProVO = daoInfo.findByPK(nroUnico);
		
		byte[] arquivo = impProVO.asBlob("ARQUIVO");
		if (arquivo == null) {
            ca.setMensagemRetorno("Arquivo Inválido! Aconteceu um problema na importação do Arquivo!");
            return;
        }
		
		is = new ByteArrayInputStream(arquivo);
        
        InputStream inArquivo = getLerArquivo(is);

		workbook = WorkbookFactory.create(inArquivo);
		
		importados = lerArquivo(ca, workbook, nroUnico, linha, nroUnicoCofig);
		
		linha.setCampo("CODUSU", ca.getUsuarioLogado());
		linha.setCampo("DTIMPORTACAO", TimeUtils.getNow());
		
		if(importados == 0) {
			ca.setMensagemRetorno("Nenhuma linha foi encontrada.");
		} else {
			ca.setMensagemRetorno("Arquivo importado! Foram criados "+importados+" registros.");
		}
		
	}
	
	private static int lerArquivo(ContextoAcao ca, Workbook workbook, BigDecimal nroUnico, 
			Registro linha, BigDecimal nroUnicoCofig) throws Exception {
		int contador = 0;
		
		Sheet sheet = workbook.getSheetAt(0);
		ArrayList<String> campo = new ArrayList<String>();
		ArrayList<String> tabela = new ArrayList<String>();
		ArrayList<String> tipoCampo = new ArrayList<String>();
		ArrayList<String> coluna = new ArrayList<String>();
		ArrayList<String> caracteres = new ArrayList<String>();
		int linhaAtual = 0;
//		int count = 0;
		
//		System.out.println("getCamposColunas");
		getCamposColunas(nroUnico, campo, tabela, coluna, tipoCampo, caracteres, ca, nroUnicoCofig);
		
		//removendo cabeçalho da planilha
//		System.out.println("getCabecalho");
		BigDecimal cabecalho = getCabecalho(nroUnicoCofig);
		if(cabecalho == null)
			cabecalho = BigDecimal.ZERO;
		
		int cabec = cabecalho.intValue();
		for(int i=0; i < cabec; i++) {
			Row row = sheet.getRow(i);
			if(row != null)
				sheet.removeRow(row);
		}
		
		//removendo cabeçalho da planilha
//		sheet.removeRow(sheet.getRow(0));
//		sheet.removeRow(sheet.getRow(1));
		
        Iterator<Row> rowIterator = sheet.iterator();
        while (rowIterator.hasNext()) {
//        	count++;
        	camposImp = new ArrayList<>();
            Row row = rowIterator.next();
            System.out.println("***LENDO LINHA: "+linhaAtual);
            
    		try {
    			for(int j=0; j < coluna.size(); j++) {
    				String conteudoString = null;
    				BigDecimal conteudoNumero = null;
    				int conteudo = 0;
    				int numLinha = 0;
    				int calcularLinha = 0;
    				
    				// pegando coluna como char
//    				System.out.println("Coluna: "+coluna.get(j));
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
    				if(cell != null) {
	    				// DEFINIR TIPO DE DADOS DA CELULA
	    				switch(tipoCampo.get(j)) {
	    				case "F":
	    					//decimal
	    					cell.setCellType(Cell.CELL_TYPE_STRING);
	    					conteudo = 1;
	    					break;
	    				case "S":
	    					//texto
	    					cell.setCellType(Cell.CELL_TYPE_STRING);
	    					conteudo = 0;
	    					break;
	    				case "I":
	    					//inteiro
	    					cell.setCellType(Cell.CELL_TYPE_STRING);
	    					conteudo = 1;
	    					break;
	    				case "H":
	    					//data e hora
	    					cell.setCellType(Cell.CELL_TYPE_STRING);
	    					conteudo = 0;
	    					break;
	    				case "C":
	    					//texto grande
	    					cell.setCellType(Cell.CELL_TYPE_STRING);
	    					conteudo = 0;
	    					break;
	    				}
	    				
	    				camposImp.add(new CamposImportacao());
	    				int i = camposImp.size() - 1;
	    				camposImp.get(i).setNomeCampo(campo.get(j));
	    				camposImp.get(i).setNomeTabela(tabela.get(j));
//	    				System.out.println("***Nome campo: "+camposImp.get(i).getNomeCampo());
//	    				System.out.println("***Nome tabela: "+camposImp.get(i).getNomeTabela());
	    				
	    				if(conteudo == 1) {
//	    					System.out.println("Valor celula: "+cell.getStringCellValue());
	    					conteudoNumero = new BigDecimal(cell.getStringCellValue());
//	    					System.out.println("***Conteudo1: "+conteudoNumero);
	    					conteudoNumero = conteudoNumero.setScale(2, BigDecimal.ROUND_HALF_EVEN);
//	    					System.out.println("***Conteudo2: "+conteudoNumero);
	    					camposImp.get(i).setConteudoNumero(conteudoNumero);
	    					camposImp.get(i).setTipoConteudo(1);
//	    					System.out.println("***Conteudo: "+camposImp.get(i).getConteudoNumero());
	    				} else {
	    					conteudoString = cell.getStringCellValue();
//	    					System.out.println("***Conteudo1: "+conteudoString);
	    					if("CUSMEDCALC".equals(camposImp.get(i).getNomeCampo()))
	    						conteudoString = cell.getStringCellValue().replace(".", ",");
	    					if("Revenda".equals(conteudoString))
	    						conteudoString = "R";
	    					else if("M. P.".equals(conteudoString))
	    						conteudoString = "M";
	    					else if("Venda de prod.".equals(conteudoString))
	    						conteudoString = "V";
	    					
	//    					conteudoString = conteudoString.replace(".", "");
	    					
	//    					System.out.println("removerCaracteres");
	    					String remover = caracteres.get(i);
	    					if(remover != null) {
		    					for(int k = 0; k < remover.length(); k++)
		    						conteudoString = conteudoString.replace(remover.charAt(k)+"", "");
	    					}
	    					
	    					camposImp.get(i).setConteudoString(conteudoString);
	    					camposImp.get(i).setTipoConteudo(2);
//	    					System.out.println("***Conteudo: "+camposImp.get(i).getConteudoString());
	    				}
    				} else {
    					System.out.println("Célula nula");
    				}
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    			ca.mostraErro("Ocorreu um erro na leitura do arquivo, verifique o arquivo e a configuração da importação.");
    		}
            
            contador = contador + importarSankhya(ca, nroUnico, linhaAtual);
    		linhaAtual++;
        }
		return contador;
	}
	
	private static BigDecimal getCabecalho(BigDecimal nroUnicoCofig) {
		BigDecimal cabecalho = null;
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

			sql.appendSql("SELECT CABECALHO FROM AD_CONFIGIMPPRO WHERE NROUNICOCONFIG = "+nroUnicoCofig);

			rset = sql.executeQuery();

			if (rset.next()) {
				cabecalho = rset.getBigDecimal("CABECALHO");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
		return cabecalho;
	}

	private static void getCamposColunas(BigDecimal nroUnico, ArrayList<String> campo, 
			ArrayList<String> tabela, ArrayList<String> coluna, ArrayList<String> tipoCampo, 
			ArrayList<String> caracteres, ContextoAcao ca, BigDecimal nroUnicoCofig) throws Exception {
		BigDecimal nuCampo = null;
		
		/* QUERY PARA COLETAR CAMPO E COLUNA */
		QueryExecutor query1 = ca.getQuery();
		QueryExecutor query2 = ca.getQuery();
		try {
			query1.setParam("NROUNICOCONFIG", nroUnicoCofig);
			query1.nativeSelect("SELECT NROUNICOCOL, COLUNA, TABELA, NUCAMPO, CARACTERES FROM AD_CONFIGCOLIMPPRO WHERE NROUNICOCONFIG = {NROUNICOCONFIG} ORDER BY NROUNICOCOL");
			while(query1.next()) {
				coluna.add(query1.getString("COLUNA").replace(" ", "").toUpperCase());
				tabela.add(query1.getString("TABELA").replace(" ", "").toUpperCase());
				caracteres.add(query1.getString("CARACTERES"));
				nuCampo = query1.getBigDecimal("NUCAMPO");
				query2.setParam("NUCAMPO", nuCampo);
				query2.nativeSelect("SELECT NOMECAMPO, TIPCAMPO FROM TDDCAM WHERE NUCAMPO = {NUCAMPO}");
				while(query2.next()) {
					campo.add(query2.getString("NOMECAMPO"));
					tipoCampo.add(query2.getString("TIPCAMPO"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			ca.mostraErro("Ocorreu um erro na leitura da configuração das colunas!");
		} finally {
			query1.close();
			query2.close();
		}
		/* ------ FIM DA QUERY ------ */
	}

	private static int importarSankhya(ContextoAcao ca, BigDecimal nroUnico, int linhaAtual) throws Exception {
		/* adicionando dados de cada linha do .xlsx no Sankhya */
		
		EntityFacade dwfFacade1 = EntityFacadeFactory.getDWFFacade();
		EntityFacade dwfFacade2 = EntityFacadeFactory.getDWFFacade();
		EntityFacade dwfFacade3 = EntityFacadeFactory.getDWFFacade();
		
		// criar VO's para as tabelas
		DynamicVO dynamicPRO = (DynamicVO) dwfFacade1.getDefaultValueObjectInstance("AD_DADOSPRODUTO");
		DynamicVO dynamicCUS = (DynamicVO) dwfFacade2.getDefaultValueObjectInstance("AD_DADOSCUSTO");
		DynamicVO dynamicEXC = (DynamicVO) dwfFacade3.getDefaultValueObjectInstance("AD_DADOSEXCECAO");
		
		dynamicPRO.setProperty("NROUNICO", nroUnico);
		dynamicCUS.setProperty("NROUNICO", nroUnico);
		dynamicEXC.setProperty("NROUNICO", nroUnico);
		
		BigDecimal conteudoNumero = null;
		String conteudoString = null;
		
		//System.out.println("**ENTRANDO NA INSERCAO");
		for(int j = 1; j <= camposImp.size(); ++j){
			try {
				String campo = camposImp.get(j-1).getNomeCampo();
//				String tabela = camposImp.get(j-1).getNomeTabela();
				
//				System.out.println("*********");
//				System.out.println("**CAMPO: "+campo);
//				System.out.println("**TABELA: "+tabela);
				
				if(camposImp.get(j-1).getTipoConteudo() == 1) {
					conteudoNumero = camposImp.get(j-1).getConteudoNumero();
					
//					System.out.println("**CONTEUDO: "+conteudoNumero);
					
					if("TGFPRO".equals(camposImp.get(j-1).getNomeTabela())) {
						dynamicPRO.setProperty(campo, conteudoNumero);
					} else if("TGFCUS".equals(camposImp.get(j-1).getNomeTabela())) {
						if("CODEMP".equals(campo))
							dynamicCUS.setProperty(campo, new BigDecimal(conteudoNumero.toBigInteger()));
						else
							dynamicCUS.setProperty(campo, conteudoNumero);
					} else if("TGFEXC".equals(camposImp.get(j-1).getNomeTabela())) {
						dynamicEXC.setProperty(campo, conteudoNumero);
					}
				} else if (camposImp.get(j-1).getTipoConteudo() == 2) {
					conteudoString = camposImp.get(j-1).getConteudoString();
					
//					System.out.println("**CONTEUDO: "+conteudoString);
					
					if("TGFPRO".equals(camposImp.get(j-1).getNomeTabela())) {
						if("NCM".equals(campo) && verificaSeNCMExisteNoSankhya(conteudoString))
							dynamicPRO.setProperty(campo, conteudoString);
						else if(!"NCM".equals(campo))
							dynamicPRO.setProperty(campo, conteudoString);
						else
							dynamicPRO.setProperty(campo, "");
					} else if("TGFCUS".equals(camposImp.get(j-1).getNomeTabela())) {
						dynamicCUS.setProperty(campo, conteudoString);
					} else if("TGFEXC".equals(camposImp.get(j-1).getNomeTabela())) {
						dynamicEXC.setProperty(campo, conteudoString);
					}
				}
				// impress�o dos campos
//				if("TGFPRO".equals(camposImp.get(j-1).getNomeTabela())) {
//					System.out.println("***"+campo+": "+dynamicPRO.getProperty(campo));
//				} else if("TGFCUS".equals(camposImp.get(j-1).getNomeTabela())) {
//					System.out.println("***"+campo+": "+dynamicCUS.getProperty(campo));
//				} else if("TGFEXC".equals(camposImp.get(j-1).getNomeTabela())) {
//					System.out.println("***"+campo+": "+dynamicEXC.getProperty(campo));
//				}
			} catch (Exception e) {
				e.printStackTrace();
				ca.mostraErro("Ocorreu um erro na importação do arquivo, verifique o arquivo e a configuração da importação.");
			}
		}
		try {
			// TODOS OS PRODUTOS ENTRAM COM IMPORTADO = S
			dynamicPRO.setProperty("IMPORTADO", "S");
			
			dwfFacade1.createEntity("AD_DADOSPRODUTO", (EntityVO) dynamicPRO);
			
			BigDecimal nroUnicoPro = (BigDecimal) dynamicPRO.getProperty("NROUNICOPRO");
	//		System.out.println("***NROUNICOPROD: "+nroUnicoPro);
			dynamicCUS.setProperty("NROUNICOPRO", nroUnicoPro);
			dynamicEXC.setProperty("NROUNICOPRO", nroUnicoPro);
			
			dwfFacade2.createEntity("AD_DADOSCUSTO", (EntityVO) dynamicCUS);
			dwfFacade3.createEntity("AD_DADOSEXCECAO", (EntityVO) dynamicEXC);
		
		} catch (Exception e) {
			e.printStackTrace();
			ca.mostraErro("Ocorreu um erro na inserção dos produtos, verifique o arquivo e a configuração da importação.");
		}
		
		return 1;
	}
	
	private static boolean verificaSeNCMExisteNoSankhya(String conteudoString) {
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

			sql.appendSql("SELECT * FROM TGFNCM WHERE CODNCM = "+conteudoString);

			rset = sql.executeQuery();

			if (rset.next())
				return true;
			else
				return false;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
		return false;
	}

	public static boolean isCellEmpty(Cell cell) {
	    if (cell == null) { // use row.getCell(x, Row.CREATE_NULL_AS_BLANK) to avoid null cells
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
	
	private static InputStream getLerArquivo(InputStream inputStream) throws IOException {
		JdbcWrapper jdbc = null;
        NativeSql query = null;

        try {
            jdbc = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            StringBuffer buf = new StringBuffer();
            InputStream in = inputStream;

            byte[] b = new byte[2048];
            int length;
            boolean hasFileInfo = false;
            boolean writeDirectly = false;
            int offset = 0;
            while ((length = in.read(b)) > 0) {
                if (writeDirectly) {
                    baos.write(b, 0, length);
                } else {
                    offset = buf.length();
                    buf.append(new String(b));
                    if (!hasFileInfo && "__start_fileinformation__".equals(buf.substring(0, 25))) {
                        hasFileInfo = true;
                    }

                    if (hasFileInfo) {
                        int i = buf.indexOf("__end_fileinformation__");
                        if (i > -1) {
                            i += 23;// tamanho do "__end_fileinformation__"
                            i -= offset; // O quanto ja havia sido lido antes
                            baos.write(b, i, length - i);
                            writeDirectly = true;
                        }
                    } else {
                        baos.write(b, 0, length);
                        writeDirectly = true;
                    }
                }
            }
            baos.flush();
            in.close();
            inputStream = new ByteArrayInputStream(baos.toByteArray());
        } finally {
            NativeSql.releaseResources(query);
            JdbcWrapper.closeSession(jdbc);
        }
        return inputStream;
    }

}
