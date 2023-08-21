package br.com.thinklife.unapel.os.iniciar;

import br.com.thinklife.unapel.os.processamento.atualizaStatusPeca;
import br.com.sankhya.modelcore.comercial.ContextoRegra;
import br.com.sankhya.modelcore.comercial.Regra;

@SuppressWarnings("serial")
public class iniciarAtualizaStatusPeca implements Regra {
	
    public void afterDelete(final ContextoRegra arg0) throws Exception {}
    
    public void afterInsert(final ContextoRegra arg0) throws Exception {}
    
    public void afterUpdate(final ContextoRegra arg0) throws Exception {
        atualizaStatusPeca.verificarNota(arg0);
    }
    
    public void beforeDelete(final ContextoRegra arg0) throws Exception {}
    
    public void beforeInsert(final ContextoRegra arg0) throws Exception {}
    
    public void beforeUpdate(final ContextoRegra arg0) throws Exception {}
    
}
