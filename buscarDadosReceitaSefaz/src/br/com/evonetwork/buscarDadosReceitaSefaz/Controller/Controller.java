package br.com.evonetwork.buscarDadosReceitaSefaz.Controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sankhya.util.StringUtils;

import br.com.evonetwork.buscarDadosReceitaSefaz.Dao.ParceiroDao;
import br.com.evonetwork.buscarDadosReceitaSefaz.Model.Parceiro;
import br.com.evonetwork.buscarDadosReceitaSefaz.Provedor.Provedor;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.facades.ParceiroSPBean;
import br.com.sankhya.ws.ServiceContext;

public class Controller {
	// EXECUTA NO BEFORE INSERT
    public static void importar(PersistenceEvent event) throws Exception {
    	System.out.println("***EVO - INICIANDO COLETA DE DADOS RECEITAWS E SEFAZ***");

        // TGFPAR - PARCEIRO
        DynamicVO tgfparVO = (DynamicVO) event.getVo();
        Parceiro parceiroAtual = ParceiroDao.setarParceiroUtilizandoDynamicVo(tgfparVO);

        realizarChamada(tgfparVO, parceiroAtual, 1);
    }

    public static void realizarChamada(DynamicVO dynamicVO, Parceiro parceiroAtual, int modo) throws Exception {

        System.out.println("***INTEGRACAO_EVENTO_PROSPECT");

        if (!StringUtils.getNullAsEmpty(parceiroAtual.getCgcCpf()).isEmpty())
            Controller.buscarDadosSefaz(dynamicVO, parceiroAtual, modo);
        else {
            System.out.println("***Não passou na validação (CPF vazio)");
            ParceiroDao.setarZeroCasoNaoEcontre(dynamicVO);
        }

    }

    public static void buscarDadosSefaz(DynamicVO dynamicVO, Parceiro model, int modo) throws Exception {
        System.out.println("***INICIO: Atualizar dados parceiro - SEFAZ");
        System.out.println(model.toString());

        Map<String, Object> requestBody = gerarCorpoDaRequisicao(model);

        JsonArray array = null;
        boolean isSucesso = true;
        try {
            ServiceContext ctx = criarJsonEGerarContextoEChamarServicoParaImportarParceiro(requestBody);
            JsonObject document = ctx.getJsonResponse();
            array = document.getAsJsonArray("result");

        }catch (Exception e) {
            isSucesso = false;
            System.out.println("ERRO: "+ e.getMessage());
        }

        if (verificaSeExisteArrayDeDados(array) && isSucesso) {
            Parceiro parceiro = Provedor.decodificarJsonResponse(array, model);
            if(modo == 1) {
            	ParceiroDao.atualizarParceiro(dynamicVO, parceiro);
            } else if(modo == 2) {
            	ParceiroDao.atualizarParceiroExistente(dynamicVO, parceiro);
            }
            
        } else {
            System.out.println("Array Vazio ou aconteceu um erro!");
            buscarDadosReceita(dynamicVO, model, modo);
        }

    }

    private static boolean verificaSeExisteArrayDeDados(JsonArray jsonArray){
        if (jsonArray != null) {
            if (jsonArray.size() > 0 )
                return true;
            else
                return false;
        } else
            return false;
    }

    private static ServiceContext criarJsonEGerarContextoEChamarServicoParaImportarParceiro(Map<String, Object> requestBody) throws MGEModelException {
        Gson g = new Gson();
        JsonObject json = (JsonObject) g.toJsonTree(requestBody);

        ServiceContext ctx = new ServiceContext(null);
        ctx.setJsonRequestBody(json);

        System.out.println("Json Request Body: " + ctx.getJsonRequestBody());

        ParceiroSPBean parceiro = new ParceiroSPBean();
        parceiro.importarDadosParceiroToJson(ctx);

        System.out.println("Response: " + ctx.getJsonResponse());

        return ctx;
    }

    private static Map<String, Object> gerarCorpoDaRequisicao(Parceiro model){

        Collection<Map<String, Object>> fieldsFilter = new ArrayList<Map<String, Object>>();
        ArrayList<String> fields = new ArrayList<String>();

        if (model.getTipoServico().equals("SF")) {
            Map<String, Object> fonte = new LinkedHashMap<String, Object>();
            fonte.put("FONTE_DADOS", "SF");
            fieldsFilter.add(fonte);

            Map<String, Object> cgccpf = new LinkedHashMap<String, Object>();
            cgccpf.put("CGC_CPF", model.getCgcCpf());
            fieldsFilter.add(cgccpf);

            Map<String, Object> codpap = new LinkedHashMap<String, Object>();
            codpap.put("CODPARC", model.getCodParc().toString());
            fieldsFilter.add(codpap);

            // N�O TEM ESTADO NO PARCEIRO
//            Map<String, Object> uf = new LinkedHashMap<String, Object>();
//            uf.put("UF", model.getEstado());
//            fieldsFilter.add(uf);
        } else {
            Map<String, Object> fonte = new LinkedHashMap<String, Object>();
            fonte.put("FONTE_DADOS", "RF");
            fieldsFilter.add(fonte);

            Map<String, Object> cgccpf = new LinkedHashMap<String, Object>();
            cgccpf.put("CGC_CPF", model.getCgcCpf());
            fieldsFilter.add(cgccpf);

            Map<String, Object> codparc = new LinkedHashMap<String, Object>();
            codparc.put("CODPARC", model.getCodParc().toString());
            fieldsFilter.add(codparc);
        }
        
        fields.add("CODPARC");
        fields.add("NOMEPARC");
        fields.add("RAZAOSOCIAL");
        fields.add("CGC_CPF");
        fields.add("CODEND"); //
        fields.add("NUMEND");
        fields.add("COMPLEMENTO");
        fields.add("CODBAI"); //
        fields.add("CODCID"); //
        fields.add("TELEFONE");
        fields.add("CEP");
        fields.add("IDENTINSCESTAD");
        fields.add("SITCADRF");
        fields.add("SITCADSEFAZ");
//        fields.add("CODUF"); //
        //CNAE,INDCREDNFE,INDCREDCTE,DTINIATIV,DTULTSIT,DTBAIXA,REGAPUR,SITCADSEFAZ,DHCADSEFAZ

        Map<String, Object> loadRecordsRequest = new LinkedHashMap<String, Object>();
        loadRecordsRequest.put("entityName", "Parceiro");
        loadRecordsRequest.put("standAlone", "true");
        loadRecordsRequest.put("fields", fields);


        Map<String, Object> requestBody = new LinkedHashMap<String, Object>();
        requestBody.put("fieldsFilter", fieldsFilter);
        requestBody.put("loadRecordsRequest", loadRecordsRequest);

        return requestBody;
    }

    private static void buscarDadosReceita(DynamicVO dynamicVO, Parceiro model, int modo) throws Exception{

        System.out.println("INICIO: Atualizar dados parceiro - RECEITA");
        model.setTipoServico("RF");
        Map<String, Object> requestBody = gerarCorpoDaRequisicao(model);

        JsonArray array = null;
        boolean isSucesso = true;
        try {
            ServiceContext ctx = criarJsonEGerarContextoEChamarServicoParaImportarParceiro(requestBody);
            JsonObject document = ctx.getJsonResponse();
            array = document.getAsJsonArray("result");

        } catch (Exception e) {
            isSucesso = false;
            System.out.println("Erro na Receita: "+ e.getMessage());
        }

        if (verificaSeExisteArrayDeDados(array) && isSucesso) {
            Parceiro parceiro = Provedor.decodificarJsonResponse(array, model);
            if(modo == 1) {
            	ParceiroDao.atualizarParceiro(dynamicVO, parceiro);
            } else if(modo == 2) {
            	ParceiroDao.atualizarParceiroExistente(dynamicVO, parceiro);
            }
        } else {
            System.out.println("Array Vazio ou aconteceu um erro!");
            ParceiroDao.setarZeroCasoNaoEcontre(dynamicVO);
        }
    }
}
