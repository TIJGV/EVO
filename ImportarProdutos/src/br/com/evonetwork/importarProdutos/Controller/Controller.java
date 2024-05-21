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
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
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
		impProVO = daoInfo.findByPK(new Object[] { nroUnico });
		byte[] arquivo = impProVO.asBlob("ARQUIVO");
		if (arquivo == null) {
			ca.setMensagemRetorno("Arquivo Inválido! Aconteceu um problema na importação do Arquivo!");
			return;
		}
		is = new ByteArrayInputStream(arquivo);
		InputStream inArquivo = getLerArquivo(is);
		workbook = WorkbookFactory.create(inArquivo);
		importados = lerArquivo(ca, workbook, nroUnico, linha, nroUnicoCofig);
		linha.setCampo("CODUSU", (Object) ca.getUsuarioLogado());
		linha.setCampo("DTIMPORTACAO", (Object) TimeUtils.getNow());
		if (importados == 0) {
			ca.setMensagemRetorno("Nenhuma linha foi encontrada.");
		} else {
			ca.setMensagemRetorno("Arquivo importado! Foram criados " + importados + " registros.");
		}
	}

	private static int lerArquivo(ContextoAcao ca, Workbook workbook, BigDecimal nroUnico, Registro linha,
			BigDecimal nroUnicoCofig) throws Exception {
		int contador = 0;
		Sheet sheet = workbook.getSheetAt(0);
		ArrayList<String> campo = new ArrayList<String>();
		ArrayList<String> tabela = new ArrayList<String>();
		ArrayList<String> tipoCampo = new ArrayList<String>();
		ArrayList<String> coluna = new ArrayList<String>();
		ArrayList<String> caracteres = new ArrayList<String>();
		int linhaAtual = 0;
		getCamposColunas(nroUnico, campo, tabela, coluna, tipoCampo, caracteres, ca, nroUnicoCofig);
		BigDecimal cabecalho = getCabecalho(nroUnicoCofig);
		if (cabecalho == null) {
			cabecalho = BigDecimal.ZERO;
		}
		for (int cabec = cabecalho.intValue(), i = 0; i < cabec; ++i) {
			Row row = sheet.getRow(i);
			if (row != null) {
				sheet.removeRow(row);
			}
		}
		Iterator<Row> rowIterator = (Iterator<Row>) sheet.iterator();
		while (rowIterator.hasNext()) {
			Controller.camposImp = new ArrayList<CamposImportacao>();
			Row row = rowIterator.next();
			System.out.println("***LENDO LINHA: " + linhaAtual);
			try {
				for (int j = 0; j < coluna.size(); ++j) {
					String conteudoString = null;
					BigDecimal conteudoNumero = null;
					int conteudo = 0;
					int numLinha = 0;
					int calcularLinha = 0;
					if (coluna.get(j).length() == 1) {
						char ch = coluna.get(j).charAt(0);
						numLinha = ch - 'A';
					} else if (coluna.get(j).length() > 1) {
						char ch2 = coluna.get(j).charAt(0);
						int x = ch2 - '@';
						calcularLinha = 26 * x;
						char ch3 = coluna.get(j).charAt(1);
						numLinha = ch3 - 'A' + calcularLinha;
					}
					Cell cell = row.getCell(numLinha);
					if (cell != null) {
						switch (tipoCampo.get(j)) {
						case "C": {
							cell.setCellType(1);
							conteudo = 0;
							break;
						}
						case "F": {
							cell.setCellType(1);
							conteudo = 1;
							break;
						}
						case "H": {
							cell.setCellType(1);
							conteudo = 0;
							break;
						}
						case "I": {
							cell.setCellType(1);
							conteudo = 1;
							break;
						}
						case "S": {
							cell.setCellType(1);
							conteudo = 0;
							break;
						}
						default:
							break;
						}
						Controller.camposImp.add(new CamposImportacao());
						int k = Controller.camposImp.size() - 1;
						Controller.camposImp.get(k).setNomeCampo(campo.get(j));
						Controller.camposImp.get(k).setNomeTabela(tabela.get(j));
						if (conteudo == 1) {
							conteudoNumero = ((cell.getStringCellValue() != null
									&& !cell.getStringCellValue().isEmpty()) ? new BigDecimal(cell.getStringCellValue())
											: null);
							if (conteudoNumero != null) {
								conteudoNumero = conteudoNumero.setScale(2, 6);
							}
							Controller.camposImp.get(k).setConteudoNumero(conteudoNumero);
							Controller.camposImp.get(k).setTipoConteudo(1);
						} else {
							conteudoString = cell.getStringCellValue();
							if ("CUSMEDCALC".equals(Controller.camposImp.get(k).getNomeCampo())) {
								conteudoString = cell.getStringCellValue().replace(".", ",");
							}
							if ("Revenda".equals(conteudoString)) {
								conteudoString = "R";
							} else if ("M. P.".equals(conteudoString)) {
								conteudoString = "M";
							} else if ("Venda de prod.".equals(conteudoString)) {
								conteudoString = "V";
							}
							String remover = caracteres.get(k);
							if (remover != null) {
								for (int l = 0; l < remover.length(); ++l) {
									conteudoString = conteudoString.replace(
											new StringBuilder(String.valueOf(remover.charAt(l))).toString(), "");
								}
							}
							Controller.camposImp.get(k).setConteudoString(conteudoString);
							Controller.camposImp.get(k).setTipoConteudo(2);
						}
					} else {
						System.out.println("Célula nula");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				ca.mostraErro(
						"Ocorreu um erro na leitura do arquivo, verifique o arquivo e a configuração da importação.");
			}
			contador += importarSankhya(ca, nroUnico, linhaAtual);
			++linhaAtual;
		}
		return contador;
	}

	private static BigDecimal getCabecalho(BigDecimal nroUnicoCofig) {
		BigDecimal cabecalho = null;
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			sql = new NativeSql(jdbc);
			sql.appendSql("SELECT CABECALHO FROM AD_CONFIGIMPPRO WHERE NROUNICOCONFIG = " + nroUnicoCofig);
			rset = sql.executeQuery();
			if (rset.next()) {
				cabecalho = rset.getBigDecimal("CABECALHO");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return cabecalho;
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
		JdbcUtils.closeResultSet(rset);
		NativeSql.releaseResources(sql);
		JdbcWrapper.closeSession(jdbc);
		JapeSession.close(hnd);
		return cabecalho;
	}

	private static void getCamposColunas(BigDecimal nroUnico, ArrayList<String> campo, ArrayList<String> tabela,
			ArrayList<String> coluna, ArrayList<String> tipoCampo, ArrayList<String> caracteres, ContextoAcao ca,
			BigDecimal nroUnicoCofig) throws Exception {
		BigDecimal nuCampo = null;
		QueryExecutor query1 = ca.getQuery();
		QueryExecutor query2 = ca.getQuery();
		try {
			query1.setParam("NROUNICOCONFIG", (Object) nroUnicoCofig);
			query1.nativeSelect(
					"SELECT NROUNICOCOL, COLUNA, TABELA, NUCAMPO, CARACTERES FROM AD_CONFIGCOLIMPPRO WHERE NROUNICOCONFIG = {NROUNICOCONFIG} ORDER BY NROUNICOCOL");
			while (query1.next()) {
				coluna.add(query1.getString("COLUNA").replace(" ", "").toUpperCase());
				tabela.add(query1.getString("TABELA").replace(" ", "").toUpperCase());
				caracteres.add(query1.getString("CARACTERES"));
				nuCampo = query1.getBigDecimal("NUCAMPO");
				query2.setParam("NUCAMPO", (Object) nuCampo);
				query2.nativeSelect("SELECT NOMECAMPO, TIPCAMPO FROM TDDCAM WHERE NUCAMPO = {NUCAMPO}");
				while (query2.next()) {
					campo.add(query2.getString("NOMECAMPO"));
					tipoCampo.add(query2.getString("TIPCAMPO"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			ca.mostraErro("Ocorreu um erro na leitura da configuração das colunas!");
			return;
		} finally {
			query1.close();
			query2.close();
		}
		query1.close();
		query2.close();
	}

	private static int importarSankhya(ContextoAcao ca, BigDecimal nroUnico, int linhaAtual) throws Exception {
		EntityFacade dwfFacade1 = EntityFacadeFactory.getDWFFacade();
		EntityFacade dwfFacade2 = EntityFacadeFactory.getDWFFacade();
		EntityFacade dwfFacade3 = EntityFacadeFactory.getDWFFacade();
		DynamicVO dynamicPRO = (DynamicVO) dwfFacade1.getDefaultValueObjectInstance("AD_DADOSPRODUTO");
		DynamicVO dynamicCUS = (DynamicVO) dwfFacade2.getDefaultValueObjectInstance("AD_DADOSCUSTO");
		DynamicVO dynamicEXC = (DynamicVO) dwfFacade3.getDefaultValueObjectInstance("AD_DADOSEXCECAO");
		dynamicPRO.setProperty("NROUNICO", (Object) nroUnico);
		dynamicCUS.setProperty("NROUNICO", (Object) nroUnico);
		dynamicEXC.setProperty("NROUNICO", (Object) nroUnico);
		BigDecimal conteudoNumero = null;
		String conteudoString = null;
		for (int j = 1; j <= Controller.camposImp.size(); ++j) {
			try {
				String campo = Controller.camposImp.get(j - 1).getNomeCampo();
				if (Controller.camposImp.get(j - 1).getTipoConteudo() == 1) {
					conteudoNumero = Controller.camposImp.get(j - 1).getConteudoNumero();
					if ("TGFPRO".equals(Controller.camposImp.get(j - 1).getNomeTabela())) {
						dynamicPRO.setProperty(campo, (Object) conteudoNumero);
					} else if ("TGFCUS".equals(Controller.camposImp.get(j - 1).getNomeTabela())) {
						if ("CODEMP".equals(campo)) {
							dynamicCUS.setProperty(campo, (Object) new BigDecimal(conteudoNumero.toBigInteger()));
						} else {
							dynamicCUS.setProperty(campo, (Object) conteudoNumero);
						}
					} else if ("TGFEXC".equals(Controller.camposImp.get(j - 1).getNomeTabela())) {
						dynamicEXC.setProperty(campo, (Object) conteudoNumero);
					}
				} else if (Controller.camposImp.get(j - 1).getTipoConteudo() == 2) {
					conteudoString = Controller.camposImp.get(j - 1).getConteudoString();
					if ("TGFPRO".equals(Controller.camposImp.get(j - 1).getNomeTabela())) {
						if ("NCM".equals(campo) && verificaSeNCMExisteNoSankhya(conteudoString)) {
							dynamicPRO.setProperty(campo, (Object) conteudoString);
						} else if (!"NCM".equals(campo)) {
							dynamicPRO.setProperty(campo, (Object) conteudoString);
						} else {
							dynamicPRO.setProperty(campo, (Object) "");
						}
					} else if ("TGFCUS".equals(Controller.camposImp.get(j - 1).getNomeTabela())) {
						dynamicCUS.setProperty(campo, (Object) conteudoString);
					} else if ("TGFEXC".equals(Controller.camposImp.get(j - 1).getNomeTabela())) {
						dynamicEXC.setProperty(campo, (Object) conteudoString);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				ca.mostraErro(
						"Ocorreu um erro na importação do arquivo, verifique o arquivo e a configuração da importação.");
			}
		}
		try {
			dynamicPRO.setProperty("IMPORTADO", (Object) "S");
			dwfFacade1.createEntity("AD_DADOSPRODUTO", (EntityVO) dynamicPRO);
			BigDecimal nroUnicoPro = (BigDecimal) dynamicPRO.getProperty("NROUNICOPRO");
			dynamicCUS.setProperty("NROUNICOPRO", (Object) nroUnicoPro);
			dynamicEXC.setProperty("NROUNICOPRO", (Object) nroUnicoPro);
			dwfFacade2.createEntity("AD_DADOSCUSTO", (EntityVO) dynamicCUS);
			dwfFacade3.createEntity("AD_DADOSEXCECAO", (EntityVO) dynamicEXC);
		} catch (Exception e2) {
			e2.printStackTrace();
			ca.mostraErro(
					"Ocorreu um erro na inserção dos produtos, verifique o arquivo e a configuração da importação.");
		}
		return 1;
	}

	private static boolean verificaSeNCMExisteNoSankhya(String conteudoString) {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			sql = new NativeSql(jdbc);
			sql.appendSql("SELECT * FROM TGFNCM WHERE CODNCM = " + conteudoString);
			rset = sql.executeQuery();
			return rset.next();
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
		return cell == null || cell.getCellType() == 3
				|| (cell.getCellType() == 1 && cell.getStringCellValue().trim().isEmpty());
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
			boolean hasFileInfo = false;
			boolean writeDirectly = false;
			int offset = 0;
			int length;
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
						if (i <= -1) {
							continue;
						}
						i += 23;
						i -= offset;
						baos.write(b, i, length - i);
						writeDirectly = true;
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
		NativeSql.releaseResources(query);
		JdbcWrapper.closeSession(jdbc);
		return inputStream;
	}
}
