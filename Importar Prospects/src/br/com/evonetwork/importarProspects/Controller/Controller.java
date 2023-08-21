package br.com.evonetwork.importarProspects.Controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.sankhya.util.TimeUtils;

import br.com.evonetwork.importarProspects.Model.CamposImportacao;
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
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class Controller {
	
	static ArrayList<CamposImportacao> camposImp = new ArrayList<>();

	public static void iniciarImportacao(ContextoAcao ca, Registro linha) throws Exception {
		BigDecimal nroUnicoImportacao = (BigDecimal) linha.getCampo("NUNICO");
		BigDecimal nroUnicoCofig = (BigDecimal) linha.getCampo("NUCONFIG");
		BigDecimal codUsuImportacao = (BigDecimal) linha.getCampo("CODUSU");
		Timestamp dataImportacao = (Timestamp) linha.getCampo("DHIMPORTACAO");
		if(codUsuImportacao != null || dataImportacao != null) {
			ca.mostraErro("Este registro já foi importado!");
		}
		DynamicVO impPlanilhaVO = null;
		InputStream is = null;
		Workbook workbook = null;
		
		try {
			JapeWrapper daoInfo = JapeFactory.dao("AD_IMPPROSP");
			impPlanilhaVO = daoInfo.findByPK(nroUnicoImportacao);
			
			byte[] arquivo = impPlanilhaVO.asBlob("ARQUIVO");
			if (arquivo == null) {
	            ca.setMensagemRetorno("Arquivo Inválido! Aconteceu um problema na importação do arquivo!");
	            return;
	        }
			
			is = new ByteArrayInputStream(arquivo);
	        
	        InputStream inArquivo = getLerArquivo(is);
	
			workbook = WorkbookFactory.create(inArquivo);
			
			lerArquivo(ca, workbook, nroUnicoImportacao, linha, nroUnicoCofig);
			
			linha.setCampo("CODUSU", ca.getUsuarioLogado());
			linha.setCampo("DHIMPORTACAO", TimeUtils.getNow());
		} catch(Exception e) {
			System.out.println("Erro na importação: "+e.getMessage());
			e.printStackTrace();
			ca.mostraErro("Erro na importação: "+e.getMessage());
		}
		ca.setMensagemRetorno("Planilha importada!");
	}

	private static InputStream getLerArquivo(InputStream inputStream) throws Exception {
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

	private static void lerArquivo(ContextoAcao ca, Workbook workbook, BigDecimal nroUnicoImportacao, Registro linha, BigDecimal nroUnicoCofig) throws Exception {
		Sheet sheet = workbook.getSheetAt(0);
		ArrayList<String> campo = new ArrayList<String>();
		ArrayList<String> tabela = new ArrayList<String>();
		ArrayList<String> tipoCampo = new ArrayList<String>();
		ArrayList<String> coluna = new ArrayList<String>();
		ArrayList<String> caracteres = new ArrayList<String>();
		int linhaAtual = 0;
		
		System.out.println("getCamposColunas");
		getCamposColunas(nroUnicoImportacao, campo, tabela, coluna, tipoCampo, caracteres, ca, nroUnicoCofig);
		
		//removendo cabeçalho da planilha
		System.out.println("getCabecalho");
		BigDecimal cabecalho = getCabecalho(nroUnicoCofig, ca);
		if(cabecalho == null)
			cabecalho = BigDecimal.ZERO;
		int cabec = cabecalho.intValue();
		for(int i=0; i < cabec; i++) {
			Row rowCabecalho = sheet.getRow(i);
			if(rowCabecalho != null)
				sheet.removeRow(rowCabecalho);
		}
		
        Iterator<Row> rowIterator = sheet.iterator();
        while (rowIterator.hasNext()) {
        	camposImp = new ArrayList<>();
            Row row = rowIterator.next();
            System.out.println("Lendo Linha: "+linhaAtual);
    		try {
    			for(int j=0; j < coluna.size(); j++) {
    				String conteudoString = null;
    				BigDecimal conteudoNumero = null;
    				Timestamp conteudoData = null;
    				
    				int conteudo = 0;
    				int numLinha = 0;
    				int calcularLinha = 0;
    				
    				// pegando coluna como char
    				System.out.println("Coluna: "+coluna.get(j));
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
    					System.out.println("Tipo do campo: "+tipoCampo.get(j));
	    				switch(tipoCampo.get(j)) {
	    				case "F": //decimal
	    					cell.setCellType(Cell.CELL_TYPE_NUMERIC);
	    					conteudo = 2;
	    					break;
	    				case "S": //texto
	    					cell.setCellType(Cell.CELL_TYPE_STRING);
	    					conteudo = 1;
	    					break;
	    				case "I": //inteiro
	    					cell.setCellType(Cell.CELL_TYPE_STRING);
	    					conteudo = 0;
	    					break;
	    				case "H": //data e hora
	    					cell.setCellType(Cell.CELL_TYPE_STRING);
	    					conteudo = 4;
	    					break;
	    				case "D": //data
//	    					cell.setCellType(Cell.CELL_TYPE_NUMERIC);
	    					cell.setCellType(Cell.CELL_TYPE_STRING);
	    					conteudo = 3;
	    					break;
	    				case "C": //texto grande
	    					cell.setCellType(Cell.CELL_TYPE_STRING);
	    					conteudo = 1;
	    					break;
	    				}
	    				
	    				camposImp.add(new CamposImportacao());
	    				int i = camposImp.size() - 1;
	    				camposImp.get(i).setNomeCampo(campo.get(j));
	    				camposImp.get(i).setNomeTabela(tabela.get(j));
	    				
	    				System.out.println("Nome campo adicionado: "+camposImp.get(i).getNomeCampo());
	    				System.out.println("Nome tabela adicionada: "+camposImp.get(i).getNomeTabela());
	    				
	    				if(conteudo == 0) { //INTEIRO
	    					if(camposImp.get(i).getNomeCampo().equals("CODUF")) {
	    						String UF = cell.getStringCellValue().replace(" ", "");
	    						System.out.println("Dado na planilha: "+UF);
	    						System.out.println("Procurando pela UF: "+UF);
	    						SessionHandle hnd = null;
	    						try {
	    							hnd = JapeSession.open();
	    							JapeWrapper ufDAO = JapeFactory.dao(DynamicEntityNames.UNIDADE_FEDERATIVA);
	    							Collection<DynamicVO> dynamicVOs = ufDAO.find("UF = ?", UF);
	    							DynamicVO ufs = (DynamicVO) (dynamicVOs.isEmpty() ? null : dynamicVOs.iterator().next());
	    							BigDecimal codUF = (BigDecimal) ufs.getProperty("CODUF");
	    							conteudoNumero = codUF;
	    						} finally {
	    							JapeSession.close(hnd);
	    						}
	    					} else {
		    					System.out.println("Dado na planilha: "+cell.getStringCellValue());
		    					if(cell.getStringCellValue() == null || "".equals(cell.getStringCellValue()))
		    						conteudoNumero = null;
		    					else
		    						conteudoNumero = new BigDecimal(cell.getStringCellValue());
	    					}
	    					camposImp.get(i).setConteudoNumero(conteudoNumero);
	    					camposImp.get(i).setTipoConteudo(0);
	    					System.out.println("Conteudo: "+conteudoNumero);
	    				} else if(conteudo == 1) { //STRING
	    					System.out.println("Dado na planilha: "+cell.getStringCellValue());
	    					conteudoString = cell.getStringCellValue();
	    					String remover = caracteres.get(i);
	    					if(remover != null && !remover.isEmpty()) {
		    					for(int k = 0; k < remover.length(); k++)
		    						conteudoString = conteudoString.replace(remover.charAt(k)+"", "");
	    					}
	    					if(camposImp.get(i).getNomeCampo().equals("TELEFONE") || camposImp.get(i).getNomeCampo().equals("CELULAR"))
	    						conteudoString = conteudoString.replace(" ", "");
	    					camposImp.get(i).setConteudoString(conteudoString.replace("'", ""));
	    					camposImp.get(i).setTipoConteudo(1);
	    					System.out.println("Conteudo: "+conteudoString);
	    				} else if(conteudo == 2){ //NUMERO
	    					System.out.println("Dado na planilha: "+cell.getNumericCellValue());
	    					conteudoNumero = BigDecimal.valueOf(cell.getNumericCellValue());
//		    				conteudoNumero = conteudoNumero.setScale(10, BigDecimal.ROUND_HALF_EVEN);
	    					camposImp.get(i).setConteudoNumero(conteudoNumero);
	    					camposImp.get(i).setTipoConteudo(2);
	    					System.out.println("Conteudo: "+conteudoNumero);
	    				} else if(conteudo == 3){ //DATA
	    					try{
		    					System.out.println("Dado na planilha: "+cell.getStringCellValue().replace("'", ""));
		    					conteudoData = stringToTimestamp(cell.getStringCellValue().replace("'", ""));
		    					camposImp.get(i).setConteudoData(conteudoData);
		    					camposImp.get(i).setTipoConteudo(3);
		    					System.out.println("Conteudo: "+conteudoData.toString());
	    					} catch(IllegalStateException e) {
	    						System.out.println("Tentando com Data...");
	    						cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		    					System.out.println("Dado na planilha: "+cell.getDateCellValue().toString());
		    					conteudoData = stringToTimestamp(convertData(cell.getDateCellValue().toString()));
		    					camposImp.get(i).setConteudoData(conteudoData);
		    					camposImp.get(i).setTipoConteudo(3);
		    					System.out.println("Conteudo: "+conteudoData.toString());
	    					}
	    				} else if(conteudo == 4){ //DATA E HORA
	    					System.out.println("Dado na planilha: "+cell.getStringCellValue().replace("'", ""));
	    					conteudoData = convertDataHora(cell.getStringCellValue().replace("'", ""));
	    					camposImp.get(i).setConteudoData(conteudoData);
	    					camposImp.get(i).setTipoConteudo(4);
	    					System.out.println("Conteudo: "+conteudoData.toString());
	    				}
    				} else {
    					System.out.println("Célula nula");
    				}
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    			ca.mostraErro("Ocorreu um erro na leitura do arquivo, verifique o arquivo e a configuração da importação.");
    		}
    		importarDadosSankhya(ca, nroUnicoImportacao);
    		linhaAtual++;
        }
	}

	private static void importarDadosSankhya(ContextoAcao ca, BigDecimal nroUnicoImportacao) throws Exception {
		EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
		
		DynamicVO dynamicPAP = (DynamicVO) dwfFacade.getDefaultValueObjectInstance("ParceiroProspect");
		DynamicVO dynamicCTT = (DynamicVO) dwfFacade.getDefaultValueObjectInstance("ContatoProspect");
		
		dynamicPAP.setProperty("AD_NROIMPORTACAO", nroUnicoImportacao);
		
		BigDecimal conteudoNumero = null;
		String conteudoString = null;
		Timestamp conteudoData = null;
		
		for(int j = 1; j <= camposImp.size(); ++j){
			try {
				String campo = camposImp.get(j-1).getNomeCampo();
				
				if(camposImp.get(j-1).getTipoConteudo() == 1) {
					conteudoString = camposImp.get(j-1).getConteudoString();
					if(conteudoString != null) {
						if("TCSPAP".equals(camposImp.get(j-1).getNomeTabela())) {
							dynamicPAP.setProperty(campo, conteudoString);
						} else {
							dynamicCTT.setProperty(campo, conteudoString);
						}
					}
				} else if(camposImp.get(j-1).getTipoConteudo() == 0 || camposImp.get(j-1).getTipoConteudo() == 2) {
					conteudoNumero = camposImp.get(j-1).getConteudoNumero();
					if(conteudoNumero != null) {
						if("TCSPAP".equals(camposImp.get(j-1).getNomeTabela())) {
							dynamicPAP.setProperty(campo, conteudoNumero);
						} else {
							dynamicCTT.setProperty(campo, conteudoNumero);
						}
					}
				} else if(camposImp.get(j-1).getTipoConteudo() == 3 || camposImp.get(j-1).getTipoConteudo() == 4) {
					conteudoData = camposImp.get(j-1).getConteudoData();
					if(conteudoData != null) {
						if("TCSPAP".equals(camposImp.get(j-1).getNomeTabela())) {
							dynamicPAP.setProperty(campo, conteudoData);
						} else {
							dynamicCTT.setProperty(campo, conteudoData);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				ca.mostraErro("Ocorreu um erro na geração do Prospect, verifique o arquivo e a configuração.");
			}
		}
		System.out.println("Tamanho dos campos: "+camposImp.size());
		if(camposImp.size() > 0) {
			dwfFacade.createEntity("ParceiroProspect", (EntityVO) dynamicPAP);
			BigDecimal codPap = dynamicPAP.asBigDecimal("CODPAP");
			System.out.println("Prospect criado: "+codPap);
			dynamicCTT.setProperty("CODPAP", codPap);
			dwfFacade.createEntity("ContatoProspect", (EntityVO) dynamicCTT);
			BigDecimal codContato = dynamicCTT.asBigDecimal("CODCONTATO");
			System.out.println("Contato do Prospect criado: "+codContato);
		}
	}

	private static Timestamp convertDataHora(String date) throws Exception {
		Date parsedDate;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
			parsedDate = dateFormat.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	    return new java.sql.Timestamp(parsedDate.getTime());
	}

	private static String convertData(String mDate) {
		if(mDate == null || mDate == "" || "null".equals(mDate))
			return "";
		
	    SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
	    try {
	    	Date newDate = inputFormat.parse(mDate);
	        inputFormat = new SimpleDateFormat("dd/MM/yyyy");
	        mDate = inputFormat.format(newDate);
	    } catch (ParseException e) {
	        e.printStackTrace();
	    }
	    
	    return mDate;
	}

	private static Timestamp stringToTimestamp(String data) throws Exception {
		Timestamp timestamp = null;
		try {
		    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		    Date parsedDate = dateFormat.parse(data);
		    timestamp = new java.sql.Timestamp(parsedDate.getTime());
		} catch(Exception e) {
		    e.printStackTrace();
		    throw new Exception(e.getMessage());
		}
		return timestamp;
	}

	private static BigDecimal getCabecalho(BigDecimal nroUnicoCofig, ContextoAcao ca) throws Exception {
		BigDecimal cabecalho = null;
		QueryExecutor query = ca.getQuery();
		try {
			query.nativeSelect("SELECT CABECALHO FROM AD_CONFIGIMPPLANILHA WHERE NUCONFIG = "+nroUnicoCofig);
			if(query.next())
				cabecalho = query.getBigDecimal("CABECALHO");
		} catch (Exception e) {
			e.printStackTrace();
			ca.mostraErro("Ocorreu um erro na leitura do campo \"cabeçalho\" da configuração!");
		} finally {
			query.close();
		}
		return cabecalho;
	}

	private static void getCamposColunas(BigDecimal nroUnicoImportacao, ArrayList<String> campo,
			ArrayList<String> tabela, ArrayList<String> coluna, ArrayList<String> tipoCampo,
			ArrayList<String> caracteres, ContextoAcao ca, BigDecimal nroUnicoCofig) throws Exception {
		BigDecimal nuCampo = null;
		
		/* QUERY PARA COLETAR CAMPO E COLUNA */
		QueryExecutor query1 = ca.getQuery();
		QueryExecutor query2 = ca.getQuery();
		try {
			query1.setParam("NUCONFIG", nroUnicoCofig);
			query1.nativeSelect("SELECT COLUNA, TABELA, NUCAMPO, CARACTERES FROM AD_COLUNASIMPPLANILHA WHERE NUCONFIG = {NUCONFIG} ORDER BY TABELA, COLUNA");
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

}
