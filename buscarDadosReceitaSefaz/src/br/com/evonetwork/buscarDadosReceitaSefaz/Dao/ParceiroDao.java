package br.com.evonetwork.buscarDadosReceitaSefaz.Dao;

import java.math.BigDecimal;

import com.sankhya.util.TimeUtils;

import br.com.evonetwork.buscarDadosReceitaSefaz.Model.Parceiro;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.modelcore.util.DynamicEntityNames;

public class ParceiroDao {
    public static Parceiro setarParceiroUtilizandoDynamicVo(DynamicVO tgfparVO) {
        Parceiro parceiro = new Parceiro();

        parceiro.setCodParc(tgfparVO.asBigDecimalOrZero("CODPARC"));
        parceiro.setRazaoSocial(tgfparVO.asString("RAZAOSOCIAL"));
        parceiro.setCgcCpf(tgfparVO.asString("CGC_CPF"));
        parceiro.setTelefone(tgfparVO.asString("TELEFONE"));
        parceiro.setCnae(tgfparVO.asString("CNAE"));
        parceiro.setRegimeApuracao(tgfparVO.asString("REGAPUR"));
        parceiro.setInscricaoEstadual(tgfparVO.asString("IDENTINSCESTAD"));
        parceiro.setNroEndereco(tgfparVO.asString("NUMEND"));
        parceiro.setComplemento(tgfparVO.asString("COMPLEMENTO"));
        parceiro.setCep(tgfparVO.asString("CEP"));
//        parceiro.setCodEstado(tgfparVO.asBigDecimalOrZero("CODUF"));

        parceiro.setTipoServico("SF");

        return parceiro;
    }

    public static void atualizarParceiro(DynamicVO dynamicVO, Parceiro parceiro) {
        System.out.println("FINAL: "+ parceiro);
        
        String nomeParc = parceiro.getNomeParc().trim();
    	System.out.println("**NOMEPARC: "+nomeParc);
    	if(nomeParc.length() < 2) {
    		nomeParc = parceiro.getRazaoSocial();
    		System.out.println("**Alterado para: "+nomeParc);
    	}

        dynamicVO.setProperty("RAZAOSOCIAL", parceiro.getRazaoSocial());
        dynamicVO.setProperty("NOMEPARC", nomeParc);
        dynamicVO.setProperty("TELEFONE", parceiro.getTelefone());
        dynamicVO.setProperty("NUMEND", parceiro.getNroEndereco());
        dynamicVO.setProperty("COMPLEMENTO", parceiro.getComplemento());
        dynamicVO.setProperty("CEP", parceiro.getCep());
        dynamicVO.setProperty("IDENTINSCESTAD", parceiro.getInscricaoEstadual());
        dynamicVO.setProperty("CODEND", parceiro.getCodEndAdicional());
        dynamicVO.setProperty("CODCID", parceiro.getCodCidAdicional());
        dynamicVO.setProperty("CODBAI", parceiro.getCodBairroAdicional());
//        dynamicVO.setProperty("CODUF", parceiro.getCodEstado());
        
        BigDecimal sitCadRf = null;
        if(!parceiro.getSituacaoReceita().isEmpty()) {
        	sitCadRf = new BigDecimal(parceiro.getSituacaoReceita());
        	dynamicVO.setProperty("SITCADRF", sitCadRf);
        	dynamicVO.setProperty("DHCADRF", TimeUtils.getNow());
        }
        
        BigDecimal sitCadSefaz = null;
        if(!parceiro.getSituacaoSefaz().isEmpty()) {
        	sitCadSefaz = new BigDecimal(parceiro.getSituacaoSefaz());
        	dynamicVO.setProperty("SITCADSEFAZ", sitCadSefaz);
        	dynamicVO.setProperty("DHCADSEFAZ", TimeUtils.getNow());
        }
    }
    
    public static void atualizarParceiroExistente(DynamicVO dynamicVO, Parceiro parceiro) throws Exception {
    	System.out.println("FINAL: "+ parceiro);
    	BigDecimal codParc = (BigDecimal) dynamicVO.getProperty("CODPARC");
    	
    	if(" ".equals(parceiro.getNomeParc())) {
    		parceiro.setNomeParc(parceiro.getRazaoSocial());
    	}
    	if(parceiro.getInscricaoEstadual() == null) {
    		parceiro.setInscricaoEstadual("ISENTO");
    	}
    	
    	String nomeParc = parceiro.getNomeParc().trim();
    	System.out.println("**NOMEPARC: "+nomeParc);
    	if(nomeParc.length() < 2) {
    		nomeParc = parceiro.getRazaoSocial();
    		System.out.println("**Alterado para: "+nomeParc);
    	}
    	
    	String complemento = parceiro.getComplemento();
    	if(complemento.length() > 30) {
    		complemento = complemento.substring(0, 29);
    	}
    	
    	BigDecimal sitCadRf = null;
    	BigDecimal sitCadSefaz = null;
        if(!parceiro.getSituacaoReceita().isEmpty()) {
        	sitCadRf = new BigDecimal(parceiro.getSituacaoReceita());
        	JapeFactory.dao(DynamicEntityNames.PARCEIRO).prepareToUpdateByPK(codParc)
	    		.set("RAZAOSOCIAL", parceiro.getRazaoSocial())
	    		.set("NOMEPARC", nomeParc)
	    		.set("TELEFONE", parceiro.getTelefone())
	    		.set("NUMEND", parceiro.getNroEndereco())
	    		.set("COMPLEMENTO", complemento)
	    		.set("CEP", parceiro.getCep())
	    		.set("IDENTINSCESTAD", parceiro.getInscricaoEstadual())
	    		.set("CODEND", parceiro.getCodEndAdicional())
	    		.set("CODCID", parceiro.getCodCidAdicional())
	    		.set("CODBAI", parceiro.getCodBairroAdicional())
//	    		.set("CODUF", parceiro.getCodEstado())
	    		.set("SITCADRF", sitCadRf)
	    		.set("DHCADRF", TimeUtils.getNow())
	    		.update();
        } else if(!parceiro.getSituacaoSefaz().isEmpty()) {
        	sitCadSefaz = new BigDecimal(parceiro.getSituacaoSefaz());
        	JapeFactory.dao(DynamicEntityNames.PARCEIRO).prepareToUpdateByPK(codParc)
	    		.set("RAZAOSOCIAL", parceiro.getRazaoSocial())
	    		.set("NOMEPARC", parceiro.getNomeParc())
	    		.set("TELEFONE", parceiro.getTelefone())
	    		.set("NUMEND", parceiro.getNroEndereco())
	    		.set("COMPLEMENTO", parceiro.getComplemento())
	    		.set("CEP", parceiro.getCep())
	    		.set("IDENTINSCESTAD", parceiro.getInscricaoEstadual())
	    		.set("CODEND", parceiro.getCodEndAdicional())
	    		.set("CODCID", parceiro.getCodCidAdicional())
	    		.set("CODBAI", parceiro.getCodBairroAdicional())
//	    		.set("CODUF", parceiro.getCodEstado())
	    		.set("SITCADSEFAZ", sitCadSefaz)
	    		.set("DHCADSEFAZ", TimeUtils.getNow())
	    		.update();
        }
    }

	public static void setarZeroCasoNaoEcontre(DynamicVO dynamicVO) throws Exception {
		BigDecimal codParc = (BigDecimal) dynamicVO.getProperty("CODPARC");
		
		JapeFactory.dao(DynamicEntityNames.PARCEIRO).prepareToUpdateByPK(codParc)
			.set("SITCADRF", BigDecimal.ZERO)
			.set("SITCADSEFAZ", BigDecimal.ZERO)
			.update();
	}
}
