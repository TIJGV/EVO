package br.com.evonetwork.atualizarCustoDeProdutos.DAO;

import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.extensions.actionbutton.QueryExecutor;
import br.com.evonetwork.atualizarCustoDeProdutos.Utils.Utils;
import br.com.sankhya.jape.vo.DynamicVO;
import java.util.ArrayList;
import java.sql.Timestamp;
import java.math.BigDecimal;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;

public class BuscarDados {
	
    public static ArrayList<DynamicVO> buscarTodosProdutosComCusto(final ContextoAcao ca, final BigDecimal codEmp, final Timestamp periodoIni, final Timestamp periodoFim) throws Exception {
        System.out.println("Buscando todos os produtos com custo de importação...");
        final QueryExecutor query = ca.getQuery();
        final String dtIni = Utils.convertDate(periodoIni.toString());
        final String dtFim = Utils.convertDate(periodoFim.toString());
        final ArrayList<DynamicVO> produtos = new ArrayList<DynamicVO>();
        try {
            final String select = "SELECT DISTINCT CODPROD FROM TGFCUS WHERE CODEMP = " + codEmp + " AND (DTATUAL >= '" + dtIni + "' AND DTATUAL <= '" + dtFim + "') AND PROCESSO = 'br.com.sankhya.menu.adicional.AD_IMPPRO'";
            query.nativeSelect(select);
            System.out.println("SQL: " + select);
            while (query.next()) {
                final BigDecimal codProd = query.getBigDecimal("CODPROD");
                buscarEPreencherCustoImportadoDoProduto(ca, codProd, codEmp, dtIni, dtFim, produtos);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
        finally {
            query.close();
        }
        query.close();
        return produtos;
    }
    
    private static void buscarEPreencherCustoImportadoDoProduto(final ContextoAcao ca, final BigDecimal codProd, final BigDecimal codEmp, final String dtIni, final String dtFim, final ArrayList<DynamicVO> produtos) throws Exception {
        System.out.println("Buscando os custos do produto " + codProd + "...");
        final QueryExecutor query = ca.getQuery();
        try {
            final String select = "SELECT CODPROD, CODEMP, DTATUAL, CODLOCAL, CONTROLE FROM TGFCUS WHERE CODPROD = " + codProd + " AND (DTATUAL >= '" + dtIni + "' AND DTATUAL <= '" + dtFim + "') AND PROCESSO = 'br.com.sankhya.menu.adicional.AD_IMPPRO' AND CODEMP = " + codEmp;
            query.nativeSelect(select);
            System.out.println("SQL: " + select);
            while (query.next()) {
                JapeSession.SessionHandle hnd = null;
                try {
                    hnd = JapeSession.open();
                    final JapeWrapper custoDAO = JapeFactory.dao("Custo");
                    final String where = "CODPROD = " + codProd + " AND CODEMP = " + codEmp + " AND TO_DATE(TO_CHAR(DTATUAL, 'DD/MM/YYYY'), 'DD/MM/YYYY') = '" + Utils.convertDate(query.getTimestamp("DTATUAL").toString()) + "' AND CODLOCAL = " + query.getBigDecimal("CODLOCAL") + " AND CONTROLE = '" + query.getString("CONTROLE") + "' AND PROCESSO = 'br.com.sankhya.menu.adicional.AD_IMPPRO'";
                    System.out.println("Where: " + where);
                    final DynamicVO custoProdutoVO = custoDAO.findOne(where);
                    if (custoProdutoVO != null) {
                        produtos.add(custoProdutoVO);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    throw new Exception(e.getMessage());
                }
                finally {
                    JapeSession.close(hnd);
                }
                JapeSession.close(hnd);
            }
        }
        catch (Exception e2) {
            e2.printStackTrace();
            throw new Exception("Erro ao buscar e preencher custos do produto " + codProd + ": " + e2.getMessage());
        }
        finally {
            query.close();
        }
        query.close();
    }
    
    public static ArrayList<DynamicVO> buscarCustoDaImportacaoParaProduto(final ContextoAcao ca, final BigDecimal codProd, final BigDecimal codEmp, final Timestamp periodoIni, final Timestamp periodoFim) throws Exception {
        System.out.println("Buscando os dados do produto importado " + codProd + "...");
        final QueryExecutor query = ca.getQuery();
        final String dtIni = Utils.convertDate(periodoIni.toString());
        final String dtFim = Utils.convertDate(periodoFim.toString());
        final ArrayList<DynamicVO> produtos = new ArrayList<DynamicVO>();
        try {
            final String select = "SELECT CODPROD, CODEMP, DTATUAL, CODLOCAL, CONTROLE FROM TGFCUS WHERE CODPROD = " + codProd + " AND (DTATUAL >= '" + dtIni + "' AND DTATUAL <= '" + dtFim + "') AND PROCESSO = 'br.com.sankhya.menu.adicional.AD_IMPPRO' AND CODEMP = " + codEmp;
            query.nativeSelect(select);
            System.out.println("SQL: " + select);
            while (query.next()) {
                JapeSession.SessionHandle hnd = null;
                try {
                    hnd = JapeSession.open();
                    final JapeWrapper custoDAO = JapeFactory.dao("Custo");
                    final String where = "CODPROD = " + query.getBigDecimal("CODPROD") + " AND CODEMP = " + query.getBigDecimal("CODEMP") + " AND TO_DATE(TO_CHAR(DTATUAL, 'DD/MM/YYYY'), 'DD/MM/YYYY') = '" + Utils.convertDate(query.getTimestamp("DTATUAL").toString()) + "' AND CODLOCAL = " + query.getBigDecimal("CODLOCAL") + " AND CONTROLE = '" + query.getString("CONTROLE") + "' AND PROCESSO = 'br.com.sankhya.menu.adicional.AD_IMPPRO'";
                    System.out.println("Where: " + where);
                    final DynamicVO custoProdutoVO = custoDAO.findOne(where);
                    produtos.add(custoProdutoVO);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    throw new Exception(e.getMessage());
                }
                finally {
                    JapeSession.close(hnd);
                }
                JapeSession.close(hnd);
            }
        }
        catch (Exception e2) {
            e2.printStackTrace();
            throw new Exception("Erro ao buscar custo da importação do produto " + codProd + ": " + e2.getMessage());
        }
        finally {
            query.close();
        }
        query.close();
        return produtos;
    }
    
    public static ArrayList<DynamicVO> buscarTodosProdutosDoGrupoComCusto(final ContextoAcao ca, final BigDecimal codEmp, final Timestamp periodoIni, final Timestamp periodoFim, final String grupoProd) throws Exception {
        System.out.println("Buscando todos os produtos com custo de importação do grupo " + grupoProd + "...");
        final QueryExecutor query = ca.getQuery();
        final String dtIni = Utils.convertDate(periodoIni.toString());
        final String dtFim = Utils.convertDate(periodoFim.toString());
        final ArrayList<DynamicVO> produtos = new ArrayList<DynamicVO>();
        try {
            final String select = "SELECT DISTINCT PRO.CODPROD FROM TGFCUS CUS, TGFPRO PRO WHERE CUS.CODEMP = " + codEmp + " AND (CUS.DTATUAL >= '" + dtIni + "' AND CUS.DTATUAL <= '" + dtFim + "') AND CUS.PROCESSO = 'br.com.sankhya.menu.adicional.AD_IMPPRO' AND PRO.CODGRUPOPROD = " + grupoProd;
            query.nativeSelect(select);
            System.out.println("SQL: " + select);
            while (query.next()) {
                final BigDecimal codProd = query.getBigDecimal("CODPROD");
                buscarEPreencherCustoImportadoDoProduto(ca, codProd, codEmp, dtIni, dtFim, produtos);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
        finally {
            query.close();
        }
        query.close();
        return produtos;
    }
}
