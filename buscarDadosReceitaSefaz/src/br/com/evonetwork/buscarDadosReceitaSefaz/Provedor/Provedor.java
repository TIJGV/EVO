package br.com.evonetwork.buscarDadosReceitaSefaz.Provedor;

import com.google.gson.JsonArray;
import com.sankhya.util.StringUtils;

import br.com.evonetwork.buscarDadosReceitaSefaz.Model.Parceiro;

public class Provedor {


    public static Parceiro decodificarJsonResponse(JsonArray array, Parceiro prospectLinha){
        Parceiro prospect = new Parceiro();

        JsonArray result = (JsonArray) array.get(0);
        System.out.printf("Resultado => " + result.toString());
        System.out.printf("Prospect => " + prospectLinha);

        prospect.setTipoServico(prospectLinha.getTipoServico());
        prospect.setCodParc(prospectLinha.getCodParc());

        // NOMEPARC
        prospect.setNomeParc(result.get(1).getAsString());

        // RAZAO SOCIAL
        String razaoSocial = result.get(2).getAsString();
        if (razaoSocial.length() >= 60){
            prospect.setRazaoSocial(razaoSocial.substring(0, 60));
        } else {
            prospect.setRazaoSocial(razaoSocial);
        }

        // CGC_CPF
        prospect.setCgcCpf(result.get(3).getAsString());

        // ENDERECO
        if (StringUtils.getNullAsEmpty(prospectLinha.getEndereco()).isEmpty()) {
            prospect.setCodEndAdicional(result.get(4).getAsBigDecimal());
        }

        // NRO ENDERECO
        if (StringUtils.getNullAsEmpty(prospectLinha.getNroEndereco()).isEmpty()) {
            String nroEndereco = result.get(5).getAsString();
            if (nroEndereco.length() >= 6 )
                prospect.setNroEndereco(nroEndereco.substring(0, 6));
            else
                prospect.setNroEndereco(nroEndereco);
        }

        // COMPLEMENTO
        if (StringUtils.getNullAsEmpty(prospectLinha.getComplemento()).isEmpty()) {
            prospect.setComplemento(result.get(6).getAsString());
        }else {
            prospect.setComplemento(prospectLinha.getComplemento());
        }

        // BAIRRO
        if ((StringUtils.getNullAsEmpty(prospectLinha.getNomeBairro()).isEmpty())) {
            prospect.setCodBairroAdicional(result.get(7).getAsBigDecimal());
        }

        // CIDADE
        if (StringUtils.getNullAsEmpty(prospectLinha.getNomeCidade()).isEmpty()) {
            prospect.setCodCidAdicional(result.get(8).getAsBigDecimal());
        }


        // TELEFONE
        if (StringUtils.getNullAsEmpty(prospectLinha.getTelefone()).isEmpty()) {
            prospect.setTelefone(result.get(9).getAsString());
        }

        // CEP
        if (StringUtils.getNullAsEmpty(prospectLinha.getCep()).isEmpty()) {
            prospect.setCep(result.get(10).getAsString());
        }

        // INSCRICAO ESTADUAL
        if (StringUtils.getNullAsEmpty(prospectLinha.getInscricaoEstadual()).isEmpty()) {
            prospect.setInscricaoEstadual(result.get(11).getAsString());
        }

        // SITUACAO RECEITA
        prospect.setSituacaoReceita(result.get(12).getAsString());
        
        // SITUACAO SEFAZ
        prospect.setSituacaoSefaz(result.get(13).getAsString());
        
        // CODUF
//        prospect.setCodEstado(result.get(14).getAsBigDecimal());

        return prospect;
    }


    public static String retirarEspacosEmBanco(String conteudo) {
        return conteudo.replace(" " , "%");
    }

    public static String retornarTipoDaRua(String nome) {
        if (!StringUtils.getNullAsEmpty(nome).isEmpty()) {
            int indexDaPrimeiraOcorrenciaDeEspaco = nome.indexOf(" ");
            return nome.substring(0, indexDaPrimeiraOcorrenciaDeEspaco);
        }else {
            return "";
        }
    }

    public static String retornarRuaSemPrefixo(String nome) {
        if (!StringUtils.getNullAsEmpty(nome).isEmpty()) {
            int indexDaPrimeiraOcorrenciaDeEspaco = nome.indexOf(" ");
            return nome.substring(indexDaPrimeiraOcorrenciaDeEspaco+1, nome.length());
        }else {
            return "";
        }
    }


    public static String removeDoubleQuotes(String input){

        StringBuilder sb = new StringBuilder();

        char[] tab = input.toCharArray();
        for( char current : tab ){
            if( current != '"' )
                sb.append( current );
        }

        return sb.toString();
    }
}
