package br.com.evonetwork.buscarDadosReceitaSefaz;

import br.com.evonetwork.buscarDadosReceitaSefaz.Controller.Controller;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;

public class AtualizarDadosParceiroEvent implements EventoProgramavelJava {

    @Override
    public void afterDelete(PersistenceEvent arg0) throws Exception {

    }

    @Override
    public void afterInsert(PersistenceEvent arg0) throws Exception {

    }

    @Override
    public void afterUpdate(PersistenceEvent arg0) throws Exception {

    }

    @Override
    public void beforeCommit(TransactionContext arg0) throws Exception {

    }

    @Override
    public void beforeDelete(PersistenceEvent arg0) throws Exception {

    }

    @Override
    public void beforeInsert(PersistenceEvent arg0) throws Exception {
        Controller.importar(arg0);
    }

    @Override
    public void beforeUpdate(PersistenceEvent arg0) throws Exception {

    }


}