package br.com.evonetwork.importarProdutos.Controller;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import br.com.evonetwork.importarProdutos.Model.CamposImportacao;
import br.com.evonetwork.importarProdutos.Model.CamposTexto;
import br.com.evonetwork.importarProdutos.Model.FileInformation;
import br.com.evonetwork.importarProdutos.Utils.BuscarDados;
import br.com.evonetwork.importarProdutos.Utils.Utils;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class Controller {

	static ArrayList<CamposImportacao> camposImp = new ArrayList<CamposImportacao>();

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
		String contentInfo = new String(arquivo, StandardCharsets.ISO_8859_1);
        FileInformation fileInformation = Utils.extrairJson(contentInfo);
        String partes[] = fileInformation.getName().split("\\.");
        String extensaoArquivo = partes[partes.length-1].toLowerCase();
        System.out.println("Nome arquivo: "+fileInformation.getName());
        System.out.println("Extensão: "+extensaoArquivo);
        if("txt".equals(extensaoArquivo)) {
        	System.out.println("É arquivo TXT");
        	importados = lerArquivoTexto(ca, arquivo, nroUnico, nroUnicoCofig);
        } else if("xlsx".equals(extensaoArquivo) || "xls".equals(extensaoArquivo)){
        	System.out.println("É arquivo Excel");
//        	is = new ByteArrayInputStream(arquivo);
//    		InputStream inArquivo = Utils.lerArquivo(is);
//    		workbook = WorkbookFactory.create(inArquivo);
        	is = new ByteArrayInputStream(arquivo);
        	InputStream inArquivo = Utils.lerArquivo(is);
            is.close();
    		workbook = WorkbookFactory.create(inArquivo);
    		inArquivo.close();
    		importados = lerArquivoExcel(ca, workbook, nroUnico, nroUnicoCofig);
        } else {
        	throw new Exception("Formato de arquivo não suportado!");
        }
		Utils.preencherUsuarioEDataImprotacao(linha, ca);
		if (importados == 0)
			ca.setMensagemRetorno("Nenhuma linha foi encontrada.");
		else
			ca.setMensagemRetorno("Arquivo importado! Foram criados "+importados+" registros.");
	}

	private static int lerArquivoTexto(ContextoAcao ca, byte[] arquivo, BigDecimal nroUnico, BigDecimal nroUnicoCofig) throws Exception {
		int contador = 0;
		int linhaAtual = 0;
		ArrayList<CamposTexto> camposTexto = BuscarDados.preencherCamposColunasTexto(ca, nroUnicoCofig);
		InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(arquivo), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        String line;
        while((line = br.readLine()) != null) {
        	if(linhaAtual == 0) {
        		line = Utils.removerCabecalhoDoArquivo(line);
        		if(line.length() > 99)
        			line = line.substring(1, line.length());
        	}
        	camposImp = new ArrayList<CamposImportacao>();
        	System.out.println("Linha atual: "+linhaAtual);
            System.out.println("Linha completa: "+line);
            for (CamposTexto campoTexto : camposTexto) {
            	int conteudo = 0;
            	String conteudoString = null;
				BigDecimal conteudoNumero = null;
				int posicaoInicial = campoTexto.getPosIni().intValue()-1;
				int posicaoFinal = campoTexto.getPosFim().intValue()-1;
				System.out.println("Campo: "+campoTexto.getNomeCampo());
				System.out.println("Início: "+posicaoInicial+", Fim: "+posicaoFinal);
				System.out.println("Conteúdo: "+line.substring(posicaoInicial, posicaoFinal));
				System.out.println("Tabela: "+campoTexto.getTabela());
				System.out.println("Tipo Campo: "+campoTexto.getTipoCampo());
				System.out.println("Casas decimais: "+campoTexto.getCasasDecimais());
//				System.out.println("Remover espaços: "+campoTexto.isRemoverEspacos());
//				System.out.println("Remover zeros: "+campoTexto.isRemoverZerosAEsquerda());
				switch (campoTexto.getTipoCampo()) {
					case "F": //decimal
						conteudo = 1;
						break;
					case "S": //texto
						conteudo = 0;
						break;
					case "I": //inteiro
						conteudo = 1;
						break;
					case "H": //data e hora
						conteudo = 0;
						break;
					case "C": //texto grande
						conteudo = 0;
						break;
				}
				camposImp.add(new CamposImportacao());
				int i = camposImp.size() - 1;
				camposImp.get(i).setNomeCampo(campoTexto.getNomeCampo());
				camposImp.get(i).setNomeTabela(campoTexto.getTabela());
				String dado = line.substring(posicaoInicial, posicaoFinal).trim();
				if(dado.isEmpty())
					continue;
//				if(campoTexto.isRemoverEspacos())
//					dado = Utils.removerEspacoEmBranco(dado);
//				if(campoTexto.isRemoverZerosAEsquerda())
//					dado = Utils.removerZerosAEsquerda(dado);
				if(conteudo == 1) {
					if(campoTexto.getCasasDecimais() > 0 && dado.length() >= campoTexto.getCasasDecimais()+1)
						dado = Utils.adicionarCasasDecimais(campoTexto.getCasasDecimais(), dado);
					conteudoNumero = new BigDecimal(dado);
					conteudoNumero = conteudoNumero.setScale(2, BigDecimal.ROUND_HALF_EVEN);
					camposImp.get(i).setConteudoNumero(conteudoNumero);
					camposImp.get(i).setTipoConteudo(1);
					System.out.println("Conteudo formatado: "+camposImp.get(i).getConteudoNumero());
				} else {
					conteudoString = dado;
					if("CUSMEDCALC".equals(camposImp.get(i).getNomeCampo()))
						conteudoString = line.substring(posicaoInicial, posicaoFinal).replace(".", ",");
					if("Revenda".equals(conteudoString))
						conteudoString = "R";
					else if("M. P.".equals(conteudoString))
						conteudoString = "M";
					else if("Venda de prod.".equals(conteudoString))
						conteudoString = "V";
//					String remover = caracteres.get(i);
//					conteudoString = Utils.removerCaracteres(remover, conteudoString);
					camposImp.get(i).setConteudoString(conteudoString);
					camposImp.get(i).setTipoConteudo(2);
					System.out.println("Conteudo formatado: "+camposImp.get(i).getConteudoString());
				}
			}
            contador += importarSankhya(ca, nroUnico, linhaAtual);
            linhaAtual++;
        }
        br.close();
		return contador;
	}

	private static int lerArquivoExcel(ContextoAcao ca, Workbook workbook, BigDecimal nroUnico, BigDecimal nroUnicoCofig) throws Exception {
		int contador = 0;
		Sheet sheet = workbook.getSheetAt(0);
		ArrayList<String> campo = new ArrayList<String>();
		ArrayList<String> tabela = new ArrayList<String>();
		ArrayList<String> tipoCampo = new ArrayList<String>();
		ArrayList<String> coluna = new ArrayList<String>();
		ArrayList<String> caracteres = new ArrayList<String>();
		int linhaAtual = 0;
		BuscarDados.preencherCamposColunasExcel(campo, tabela, coluna, tipoCampo, caracteres, ca, nroUnicoCofig);
		BigDecimal cabecalho = BuscarDados.getCabecalho(ca, nroUnicoCofig);
		Utils.removerCabecalho(cabecalho, sheet);
		Iterator<Row> rowIterator = (Iterator<Row>) sheet.iterator();
		while (rowIterator.hasNext()) {
			camposImp = new ArrayList<CamposImportacao>();
			Row row = rowIterator.next();
			System.out.println("***LENDO LINHA: "+linhaAtual);
			try {
				for (int j = 0; j < coluna.size(); ++j) {
					String conteudoString = null;
					BigDecimal conteudoNumero = null;
					int conteudo = 0;
					int numLinha = 0;
					int calcularLinha = 0;
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
					if (cell != null) {
						switch (tipoCampo.get(j)) {
							case "F": //decimal
		    					cell.setCellType(Cell.CELL_TYPE_STRING);
		    					conteudo = 1;
		    					break;
		    				case "S": //texto
		    					cell.setCellType(Cell.CELL_TYPE_STRING);
		    					conteudo = 0;
		    					break;
		    				case "I": //inteiro
		    					cell.setCellType(Cell.CELL_TYPE_STRING);
		    					conteudo = 1;
		    					break;
		    				case "H": //data e hora
		    					cell.setCellType(Cell.CELL_TYPE_STRING);
		    					conteudo = 0;
		    					break;
		    				case "C": //texto grande
		    					cell.setCellType(Cell.CELL_TYPE_STRING);
		    					conteudo = 0;
		    					break;
						}
						camposImp.add(new CamposImportacao());
						int i = camposImp.size() - 1;
						camposImp.get(i).setNomeCampo(campo.get(j));
						camposImp.get(i).setNomeTabela(tabela.get(j));
						System.out.println("Campo: "+campo.get(j));
						System.out.println("Tabela: "+tabela.get(j));
						if(conteudo == 1) {
//	    					System.out.println("Valor celula: "+cell.getStringCellValue());/
	    					conteudoNumero = new BigDecimal(cell.getStringCellValue().replace(",", "."));
	    					System.out.println("***Conteudo1: "+conteudoNumero);
	    					if("F".equals(tipoCampo.get(j)))
	    						conteudoNumero = conteudoNumero.setScale(2, BigDecimal.ROUND_HALF_EVEN);
	    					System.out.println("***Conteudo2: "+conteudoNumero);
	    					camposImp.get(i).setConteudoNumero(conteudoNumero);
	    					camposImp.get(i).setTipoConteudo(1);
//	    					System.out.println("***Conteudo: "+camposImp.get(i).getConteudoNumero());
	    				} else {
	    					conteudoString = cell.getStringCellValue();
	    					System.out.println("***Conteudo1: "+conteudoString);
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
	    					conteudoString = Utils.removerCaracteres(remover, conteudoString);
	    					System.out.println("***Conteudo1: "+conteudoString);
	    					camposImp.get(i).setConteudoString(conteudoString);
	    					camposImp.get(i).setTipoConteudo(2);
	    					System.out.println("***Conteudo: "+camposImp.get(i).getConteudoString());
	    				}
					} else {
						System.out.println("Célula nula");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				ca.mostraErro("Ocorreu um erro na leitura do arquivo, verifique o arquivo e a configuração da importação.");
			}
			contador += importarSankhya(ca, nroUnico, linhaAtual);
//			camposImp.clear();
			++linhaAtual;
		}
		return contador;
	}

	private static int importarSankhya(ContextoAcao ca, BigDecimal nroUnico, int linhaAtual) throws Exception {
		SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
//			hnd.setCanTimeout(false); //instrução para evitar o Timeout
			hnd.execWithTX( new JapeSession.TXBlock() {
				public void doWithTx() throws Exception {
					boolean importaDadosProduto = false;
					boolean importaDadosCusto = false;
					boolean importaDadosPreco = false;
					EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
					for(int j = 1; j <= camposImp.size(); ++j){
						String tabela = camposImp.get(j-1).getNomeTabela();
						if("TGFPRO".equals(tabela))
							importaDadosProduto = true;
						if("TGFCUS".equals(tabela))
							importaDadosCusto = true;
						if("TGFEXC".equals(tabela))
							importaDadosPreco = true;
					}
					DynamicVO dynamicPRO = null;
					DynamicVO dynamicCUS = null;
					DynamicVO dynamicEXC = null;
					System.out.println("Importar Produtos: "+importaDadosProduto);
					System.out.println("Importar Custo: "+importaDadosCusto);
					System.out.println("Importar Preço: "+importaDadosPreco);
					if(importaDadosProduto) {
						dynamicPRO = (DynamicVO) dwfFacade.getDefaultValueObjectInstance("AD_DADOSPRODUTO");
						dynamicPRO.setProperty("NROUNICO", nroUnico);
					}
					if(importaDadosCusto) {
						dynamicCUS = (DynamicVO) dwfFacade.getDefaultValueObjectInstance("AD_DADOSCUSTO");
						dynamicCUS.setProperty("NROUNICO", nroUnico);
					}
					if(importaDadosPreco) {
						dynamicEXC = (DynamicVO) dwfFacade.getDefaultValueObjectInstance("AD_DADOSEXCECAO");
						dynamicEXC.setProperty("NROUNICO", nroUnico);
					}
					BigDecimal conteudoNumero = null;
					String conteudoString = null;
					//System.out.println("**ENTRANDO NA INSERCAO");
					for(int j = 1; j <= camposImp.size(); ++j) {
						try {
							String campo = camposImp.get(j-1).getNomeCampo();
							String tabela = camposImp.get(j-1).getNomeTabela();
							
							System.out.println("*********");
							System.out.println("**CAMPO: "+campo);
							System.out.println("**TABELA: "+tabela);
							System.out.println("**j: "+j);
							System.out.println("**size: "+camposImp.size());
							
							if(camposImp.get(j-1).getTipoConteudo() == 1) {
								conteudoNumero = camposImp.get(j-1).getConteudoNumero();
//								System.out.println("**CONTEUDO: "+conteudoNumero);
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
//								System.out.println("**CONTEUDO: "+conteudoString);
								if("TGFPRO".equals(camposImp.get(j-1).getNomeTabela())) {
									if("NCM".equals(campo)) {
										if(conteudoString.length() > 8)
											conteudoString = conteudoString.substring(0, 8); //4013900001
										if(BuscarDados.NCMExisteNoSankhya(ca, conteudoString))
										dynamicPRO.setProperty(campo, conteudoString);
									} else if(!"NCM".equals(campo))
										dynamicPRO.setProperty(campo, conteudoString);
									else
										dynamicPRO.setProperty(campo, "");
								} else if("TGFCUS".equals(camposImp.get(j-1).getNomeTabela())) {
									dynamicCUS.setProperty(campo, conteudoString);
								} else if("TGFEXC".equals(camposImp.get(j-1).getNomeTabela())) {
									dynamicEXC.setProperty(campo, conteudoString);
								}
							}
//							if("TGFPRO".equals(camposImp.get(j-1).getNomeTabela())) {
//								System.out.println("***"+campo+": "+dynamicPRO.getProperty(campo));
//							} else if("TGFCUS".equals(camposImp.get(j-1).getNomeTabela())) {
//								System.out.println("***"+campo+": "+dynamicCUS.getProperty(campo));
//							} else if("TGFEXC".equals(camposImp.get(j-1).getNomeTabela())) {
//								System.out.println("***"+campo+": "+dynamicEXC.getProperty(campo));
//							}
						} catch (Exception e) {
							e.printStackTrace();
							throw new Exception("Ocorreu um erro na importação do arquivo, verifique o arquivo e a configuração da importação: "+e.getMessage());
						}
					}
					try {
						BigDecimal nroUnicoPro = null;
						if(importaDadosProduto) {
							dynamicPRO.setProperty("IMPORTADO", "S");
							dwfFacade.createEntity("AD_DADOSPRODUTO", (EntityVO) dynamicPRO);
							nroUnicoPro = (BigDecimal) dynamicPRO.getProperty("NROUNICOPRO");
						}
						if(importaDadosCusto) {
							dynamicCUS.setProperty("NROUNICOPRO", nroUnicoPro);
							dwfFacade.createEntity("AD_DADOSCUSTO", (EntityVO) dynamicCUS);
						}
						if(importaDadosPreco) {
							dynamicEXC.setProperty("NROUNICOPRO", nroUnicoPro);
							dwfFacade.createEntity("AD_DADOSEXCECAO", (EntityVO) dynamicEXC);
						}
					} catch (Exception e) {
						e.printStackTrace();
						throw new Exception("Ocorreu um erro na inserção dos produtos, verifique o arquivo e a configuração da importação: "+e.getMessage());
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
		return 1;
	}

}