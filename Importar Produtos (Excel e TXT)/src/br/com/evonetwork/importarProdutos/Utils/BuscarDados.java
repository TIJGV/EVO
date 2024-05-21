package br.com.evonetwork.importarProdutos.Utils;

import java.math.BigDecimal;
import java.util.ArrayList;

import br.com.evonetwork.importarProdutos.Model.CamposTexto;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.QueryExecutor;

public class BuscarDados {

	public static ArrayList<CamposTexto> preencherCamposColunasTexto(ContextoAcao ca, BigDecimal nroUnicoCofig) throws Exception {
		ArrayList<CamposTexto> camposTexto = new ArrayList<CamposTexto>();
		QueryExecutor queryColunas = ca.getQuery();
		QueryExecutor queryTipoCampo = ca.getQuery();
		try {
			queryColunas.nativeSelect("SELECT SEQUENCIA, POSINICIAL, POSFINAL, TABELA, NUCAMPO, QTDCASASDECIMAIS FROM AD_CONFIGTXTIMPPRO WHERE NROUNICOCONFIG = "+nroUnicoCofig+" ORDER BY SEQUENCIA");
			while (queryColunas.next()) {
				CamposTexto campoTexto = new CamposTexto();
				campoTexto.setPosIni(queryColunas.getBigDecimal("POSINICIAL"));
				campoTexto.setPosFim(queryColunas.getBigDecimal("POSFINAL"));
				campoTexto.setTabela(queryColunas.getString("TABELA").replace(" ", "").toUpperCase());
				BigDecimal nuCampo = queryColunas.getBigDecimal("NUCAMPO");
				campoTexto.setCasasDecimais((queryColunas.getBigDecimal("QTDCASASDECIMAIS") != null ? queryColunas.getBigDecimal("QTDCASASDECIMAIS").intValue() : 0));
				queryTipoCampo.nativeSelect("SELECT NOMECAMPO, TIPCAMPO FROM TDDCAM WHERE NUCAMPO = "+nuCampo);
				while (queryTipoCampo.next()) {
					campoTexto.setNomeCampo(queryTipoCampo.getString("NOMECAMPO"));
					campoTexto.setTipoCampo(queryTipoCampo.getString("TIPCAMPO"));
				}
				camposTexto.add(campoTexto);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Ocorreu um erro na leitura da configuração das colunas!");
		} finally {
			queryColunas.close();
			queryTipoCampo.close();
		}
		return camposTexto;
	}

	public static void preencherCamposColunasExcel(ArrayList<String> campo, ArrayList<String> tabela, ArrayList<String> coluna, ArrayList<String> tipoCampo, ArrayList<String> caracteres, ContextoAcao ca, BigDecimal nroUnicoCofig) throws Exception {
		QueryExecutor queryColunas = ca.getQuery();
		QueryExecutor queryTipoCampo = ca.getQuery();
		try {
			queryColunas.nativeSelect("SELECT NROUNICOCOL, COLUNA, TABELA, NUCAMPO, CARACTERES FROM AD_CONFIGCOLIMPPRO WHERE NROUNICOCONFIG = "+nroUnicoCofig+" ORDER BY NROUNICOCOL");
			while (queryColunas.next()) {
				coluna.add(queryColunas.getString("COLUNA").replace(" ", "").toUpperCase());
				tabela.add(queryColunas.getString("TABELA").replace(" ", "").toUpperCase());
				caracteres.add(queryColunas.getString("CARACTERES"));
				BigDecimal nuCampo = queryColunas.getBigDecimal("NUCAMPO");
				queryTipoCampo.nativeSelect("SELECT NOMECAMPO, TIPCAMPO FROM TDDCAM WHERE NUCAMPO = "+nuCampo);
				while (queryTipoCampo.next()) {
					campo.add(queryTipoCampo.getString("NOMECAMPO"));
					tipoCampo.add(queryTipoCampo.getString("TIPCAMPO"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Ocorreu um erro na leitura da configuração das colunas!");
		} finally {
			queryColunas.close();
			queryTipoCampo.close();
		}
	}

	public static BigDecimal getCabecalho(ContextoAcao ca, BigDecimal nroUnicoCofig) throws Exception {
		BigDecimal cabecalho = null;
		QueryExecutor query = ca.getQuery();
		try {
			String select = "SELECT CABECALHO FROM AD_CONFIGIMPPRO WHERE NROUNICOCONFIG = "+nroUnicoCofig;
			query.nativeSelect(select);
			System.out.println("SQL: "+select);
			while(query.next()) {
				cabecalho = query.getBigDecimal("CABECALHO");
				if (cabecalho == null)
					cabecalho = BigDecimal.ZERO;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			query.close();
		}
		return cabecalho;
	}

	public static boolean NCMExisteNoSankhya(ContextoAcao ca, String conteudoString) throws Exception {
		QueryExecutor query = ca.getQuery();
		try {
			String select = "SELECT * FROM TGFNCM WHERE CODNCM = '"+conteudoString+"'";
			query.nativeSelect(select);
			System.out.println("SQL: "+select);
			if (query.next())
				return true;
			else
				return false;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			query.close();
		}
	}

}
