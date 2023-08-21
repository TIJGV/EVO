package br.com.thinklife.unapel.os.save;

import br.com.sankhya.jape.util.JapeSessionContext;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import com.sankhya.util.TimeUtils;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import br.com.thinklife.unapel.os.consulta.buscaDados;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.jape.vo.DynamicVO;
import java.math.BigDecimal;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;

public class saves
{
    private AuthenticationInfo oldAuthInfo;
    private AuthenticationInfo authInfo;
    
    public static BigDecimal cabecalho(final EntityFacade dwf, final BigDecimal empresa, final BigDecimal parceiro, final BigDecimal numnota, final BigDecimal tipooper, final BigDecimal tipvenda, final String tipMov, final BigDecimal nat, final BigDecimal centroresult, final BigDecimal parcDest) throws Exception {
        final DynamicVO nota = (DynamicVO)dwf.getDefaultValueObjectInstance("CabecalhoNota");
        nota.setProperty("CODEMP", (Object)empresa);
        nota.setProperty("CODPARC", (Object)parceiro);
        nota.setProperty("NUMNOTA", (Object)numnota);
        nota.setProperty("CODTIPOPER", (Object)tipooper);
        nota.setProperty("CODTIPVENDA", (Object)tipvenda);
        nota.setProperty("TIPMOV", (Object)tipMov);
        nota.setProperty("CODNAT", (Object)nat);
        nota.setProperty("CODCENCUS", (Object)centroresult);
        nota.setProperty("CODPARCDEST", (Object)parcDest);
        dwf.createEntity("CabecalhoNota", (EntityVO)nota);
        final BigDecimal nunota = nota.asBigDecimal("NUNOTA");
        return nunota;
    }
    
    public static BigDecimal cabecalhoReqPeca(final EntityFacade dwf, final JdbcWrapper jdbcWrapper, final BigDecimal numOs, final BigDecimal usuLogado) throws Exception {
        BigDecimal nunota = BigDecimal.ZERO;
        final ResultSet query = buscaDados.modNotaReqPeca(jdbcWrapper, numOs, usuLogado);
        while (query.next()) {
            final DynamicVO nota = (DynamicVO)dwf.getDefaultValueObjectInstance("CabecalhoNota");
            nota.setProperty("CODEMP", (Object)query.getBigDecimal("CODEMP"));
            nota.setProperty("CODPARC", (Object)query.getBigDecimal("CODPARC"));
            nota.setProperty("NUMNOTA", (Object)query.getBigDecimal("NUMNOTA"));
            nota.setProperty("CODTIPOPER", (Object)query.getBigDecimal("CODTIPOPER"));
            nota.setProperty("CODTIPVENDA", (Object)query.getBigDecimal("CODTIPVENDA"));
            nota.setProperty("TIPMOV", (Object)new StringBuilder().append(query.getString("TIPMOV")).toString());
            nota.setProperty("CODNAT", (Object)query.getBigDecimal("CODNAT"));
            nota.setProperty("CODCENCUS", (Object)query.getBigDecimal("CODCENCUS"));
            nota.setProperty("CODPARCDEST", (Object)query.getBigDecimal("PARCDEST"));
            nota.setProperty("CODUSUINC", (Object)usuLogado);
            nota.setProperty("CODVEND", (Object)query.getBigDecimal("VENDEDOR"));
            dwf.createEntity("CabecalhoNota", (EntityVO)nota);
            nunota = nota.asBigDecimal("NUNOTA");
        }
        return nunota;
    }
    
    public static BigDecimal cabecalho2(final EntityFacade dwf, final BigDecimal empresa, final BigDecimal parceiro, final BigDecimal numnota, final BigDecimal tipooper, final BigDecimal tipvenda, final String tipMov, final BigDecimal nat, final BigDecimal centroresult, final BigDecimal notaorigem) throws Exception {
        final DynamicVO nota = (DynamicVO)dwf.getDefaultValueObjectInstance("CabecalhoNota");
        nota.setProperty("CODEMP", (Object)empresa);
        nota.setProperty("CODPARC", (Object)parceiro);
        nota.setProperty("NUMNOTA", (Object)numnota);
        nota.setProperty("CODTIPOPER", (Object)tipooper);
        nota.setProperty("CODTIPVENDA", (Object)tipvenda);
        nota.setProperty("TIPMOV", (Object)tipMov);
        nota.setProperty("CODNAT", (Object)nat);
        nota.setProperty("CODCENCUS", (Object)centroresult);
        nota.setProperty("AD_NOTAORIGEM", (Object)notaorigem);
        dwf.createEntity("CabecalhoNota", (EntityVO)nota);
        final BigDecimal nunota = nota.asBigDecimal("NUNOTA");
        return nunota;
    }
    
    public static BigDecimal item(final EntityFacade dwf, final BigDecimal nunota, final BigDecimal codProd, final String codVol, final BigDecimal qtd, final BigDecimal codlocalOrigem, final BigDecimal localDestino, final BigDecimal contador, final String lote) throws Exception {
        final DynamicVO iteOrigem = (DynamicVO)dwf.getDefaultValueObjectInstance("ItemNota");
        iteOrigem.setProperty("NUNOTA", (Object)nunota);
        iteOrigem.setProperty("SEQUENCIA", (Object)contador);
        iteOrigem.setProperty("ATUALESTOQUE", (Object)new BigDecimal(-1));
        iteOrigem.setProperty("CODPROD", (Object)codProd);
        iteOrigem.setProperty("CODVOL", (Object)codVol);
        iteOrigem.setProperty("QTDNEG", (Object)qtd);
        iteOrigem.setProperty("CODLOCALORIG", (Object)codlocalOrigem);
        iteOrigem.setProperty("CODLOCALDEST", (Object)localDestino);
        iteOrigem.setProperty("CONTROLE", (Object)lote);
        dwf.createEntity("ItemNota", (EntityVO)iteOrigem);
        return iteOrigem.asBigDecimal("SEQUENCIA");
    }
    
    public static BigDecimal item2(final EntityFacade dwf, final BigDecimal nunota, final BigDecimal codProd, final String codVol, final BigDecimal qtd, final BigDecimal codlocalOrigem, final BigDecimal localDestino, final BigDecimal contador, final String lote, final String necLote) throws Exception {
        final DynamicVO iteOrigem = (DynamicVO)dwf.getDefaultValueObjectInstance("ItemNota");
        iteOrigem.setProperty("NUNOTA", (Object)nunota);
        iteOrigem.setProperty("SEQUENCIA", (Object)contador);
        iteOrigem.setProperty("ATUALESTOQUE", (Object)new BigDecimal(1));
        iteOrigem.setProperty("CODPROD", (Object)codProd);
        iteOrigem.setProperty("RESERVA", (Object)"S");
        iteOrigem.setProperty("CODVOL", (Object)codVol);
        iteOrigem.setProperty("QTDNEG", (Object)qtd);
        iteOrigem.setProperty("CODLOCALORIG", (Object)codlocalOrigem);
        iteOrigem.setProperty("CODLOCALDEST", (Object)localDestino);
        if (!necLote.equals("N")) {
            iteOrigem.setProperty("CONTROLE", (Object)lote);
        }
        iteOrigem.setProperty("TERCEIROS", (Object)"N");
        iteOrigem.setProperty("ATUALESTTERC", (Object)"N");
        iteOrigem.setProperty("USOPROD", (Object)"M");
        iteOrigem.setProperty("STATUSLOTE", (Object)"P");
        dwf.createEntity("ItemNota", (EntityVO)iteOrigem);
        return iteOrigem.asBigDecimal("SEQUENCIA");
    }
    
    public static BigDecimal item3(final EntityFacade dwf, final BigDecimal nunota, final BigDecimal codProd, final String codVol, final BigDecimal qtd, final BigDecimal codlocalOrigem, final BigDecimal localDestino, final BigDecimal contador, final String lote, final String necLote, final BigDecimal vlrUnit, final BigDecimal vlrTot) throws Exception {
        final DynamicVO iteOrigem = (DynamicVO)dwf.getDefaultValueObjectInstance("ItemNota");
        iteOrigem.setProperty("NUNOTA", (Object)nunota);
        iteOrigem.setProperty("SEQUENCIA", (Object)contador);
        iteOrigem.setProperty("ATUALESTOQUE", (Object)new BigDecimal(1));
        iteOrigem.setProperty("CODPROD", (Object)codProd);
        iteOrigem.setProperty("RESERVA", (Object)"S");
        iteOrigem.setProperty("CODVOL", (Object)codVol);
        iteOrigem.setProperty("QTDNEG", (Object)qtd);
        iteOrigem.setProperty("CODLOCALORIG", (Object)codlocalOrigem);
        iteOrigem.setProperty("CODLOCALDEST", (Object)localDestino);
        if (!necLote.equals("N")) {
            iteOrigem.setProperty("CONTROLE", (Object)lote);
        }
        iteOrigem.setProperty("TERCEIROS", (Object)"N");
        iteOrigem.setProperty("ATUALESTTERC", (Object)"N");
        iteOrigem.setProperty("USOPROD", (Object)"M");
        iteOrigem.setProperty("STATUSLOTE", (Object)"P");
        iteOrigem.setProperty("VLRTOT", (Object)vlrTot);
        iteOrigem.setProperty("VLRUNIT", (Object)vlrUnit);
        dwf.createEntity("ItemNota", (EntityVO)iteOrigem);
        return iteOrigem.asBigDecimal("SEQUENCIA");
    }
    
    public static void notasGeradas(final EntityFacade dwf, final BigDecimal codTipOper, final BigDecimal numos, final BigDecimal nunota) throws Exception {
        final DynamicVO notagerada = (DynamicVO)dwf.getDefaultValueObjectInstance("AD_TCSNOTAS");
        notagerada.setProperty("CODTIPOPER", (Object)codTipOper);
        notagerada.setProperty("NUMOS", (Object)numos);
        notagerada.setProperty("NUNOTA", (Object)nunota);
        dwf.createEntity("AD_TCSNOTAS", (EntityVO)notagerada);
    }
    
    public static BigDecimal itemPecasUtilizadas(final EntityFacade dwf, final BigDecimal nunota, final BigDecimal codProd, final String codVol, final BigDecimal qtd, final BigDecimal codlocalOrigem, final BigDecimal localDestino, final BigDecimal contador, final BigDecimal vlrtot, final String lote, final BigDecimal atualEstoque, final String reserva, final BigDecimal vlrUnit) throws Exception {
        final DynamicVO iteOrigem = (DynamicVO)dwf.getDefaultValueObjectInstance("ItemNota");
        iteOrigem.setProperty("NUNOTA", (Object)nunota);
        iteOrigem.setProperty("SEQUENCIA", (Object)contador);
        iteOrigem.setProperty("CODPROD", (Object)codProd);
        iteOrigem.setProperty("CODVOL", (Object)codVol);
        iteOrigem.setProperty("QTDNEG", (Object)qtd);
        iteOrigem.setProperty("CODLOCALORIG", (Object)codlocalOrigem);
        iteOrigem.setProperty("CODLOCALDEST", (Object)localDestino);
        iteOrigem.setProperty("VLRTOT", (Object)vlrtot);
        iteOrigem.setProperty("PENDENTE", (Object)"S");
        iteOrigem.setProperty("QTDENTREGUE", (Object)BigDecimal.ZERO);
        iteOrigem.setProperty("ATUALESTOQUE", (Object)atualEstoque);
        iteOrigem.setProperty("RESERVA", (Object)reserva);
        iteOrigem.setProperty("VLRUNIT", (Object)vlrUnit);
        if (lote != null) {
            iteOrigem.setProperty("CONTROLE", (Object)lote);
        }
        dwf.createEntity("ItemNota", (EntityVO)iteOrigem);
        return iteOrigem.asBigDecimal("SEQUENCIA");
    }
    
    public static BigDecimal cabecalhoPecasUtilizadas(final EntityFacade dwf, final BigDecimal empresa, final BigDecimal parceiro, final BigDecimal parceiroDest, final BigDecimal numnota, final BigDecimal tipooper, final BigDecimal tipvenda, final BigDecimal nat, final BigDecimal centroresult, final BigDecimal numos, final String tipmov, final String tipoOs, final BigDecimal localVenda, final BigDecimal vendedor) throws Exception {
        final DynamicVO nota = (DynamicVO)dwf.getDefaultValueObjectInstance("CabecalhoNota");
        nota.setProperty("CODEMP", (Object)empresa);
        nota.setProperty("CODPARC", (Object)parceiro);
        nota.setProperty("CODPARCDEST", (Object)parceiroDest);
        nota.setProperty("NUMNOTA", (Object)numnota);
        nota.setProperty("CODTIPOPER", (Object)tipooper);
        nota.setProperty("CODTIPVENDA", (Object)tipvenda);
        nota.setProperty("CODNAT", (Object)nat);
        nota.setProperty("CODCENCUS", (Object)centroresult);
        nota.setProperty("AD_NUMOSOFICINA", (Object)numos);
        nota.setProperty("TIPMOV", (Object)tipmov);
        nota.setProperty("PENDENTE", (Object)"S");
        nota.setProperty("AD_CODTIPODEOS", (Object)new BigDecimal(tipoOs));
        nota.setProperty("AD_CODOAT", (Object)localVenda);
        nota.setProperty("CODVEND", (Object)vendedor);
        dwf.createEntity("CabecalhoNota", (EntityVO)nota);
        final BigDecimal nunota = nota.asBigDecimalOrZero("NUNOTA");
        return nunota;
    }
    
    public static void salvarTCSNOTAS(final EntityFacade dwf, final BigDecimal codtipoper, final BigDecimal codparc, final BigDecimal nunota, final Timestamp data, final BigDecimal numos) throws Exception {
        final DynamicVO ordemServicoTCSNOTAS = (DynamicVO)dwf.getDefaultValueObjectInstance("AD_TCSNOTAS");
        ordemServicoTCSNOTAS.setProperty("CODTIPOPER", (Object)codtipoper);
        ordemServicoTCSNOTAS.setProperty("CODPARC", (Object)codparc);
        ordemServicoTCSNOTAS.setProperty("NUNOTA", (Object)nunota);
        ordemServicoTCSNOTAS.setProperty("DTNEG", (Object)data);
        ordemServicoTCSNOTAS.setProperty("NUMOS", (Object)numos);
        dwf.createEntity("AD_TCSNOTAS", (EntityVO)ordemServicoTCSNOTAS);
    }
    
    public static void salvarTGFVAR(final EntityFacade dwf, final BigDecimal nunota, final BigDecimal sequencia, final BigDecimal nunotaorigem, final BigDecimal seqorigem, final String statusnota) throws Exception {
        final DynamicVO cadastroTGFVAR = (DynamicVO)dwf.getDefaultValueObjectInstance("CompraVendavariosPedido");
        cadastroTGFVAR.setProperty("NUNOTA", (Object)nunota);
        cadastroTGFVAR.setProperty("SEQUENCIA", (Object)sequencia);
        cadastroTGFVAR.setProperty("NUNOTAORIG", (Object)nunotaorigem);
        cadastroTGFVAR.setProperty("SEQUENCIAORIG", (Object)seqorigem);
        cadastroTGFVAR.setProperty("STATUSNOTA", (Object)statusnota);
        dwf.createEntity("CompraVendavariosPedido", (EntityVO)cadastroTGFVAR);
    }
    
    public void criandoAgendaRecursos(final EntityFacade dwf, final JdbcWrapper jdbcWrapper, final BigDecimal codusuresp, final String hrinicial, final String hrfinal, final String descrprod, final String sincronizar, final String dhagenda, final BigDecimal NUMOS, final BigDecimal NUMITEM, final BigDecimal codparc) throws Exception {
        this.authenticate(new BigDecimal(0));
        final String horainicial = hrinicial.split(" ")[1];
        final String horafinal = hrfinal.split(" ")[1];
        final String[] datainicial1 = hrinicial.replace(horainicial, "").split("-");
        final String diadata = datainicial1[2];
        final String mesdata = datainicial1[1];
        final String anodata = datainicial1[0];
        final String data_nova_inicial = String.valueOf(diadata.replace(" ", "")) + "/" + mesdata + "/" + anodata;
        final String[] datafinal1 = hrfinal.replace(horafinal, "").split("-");
        final String dia1 = datafinal1[2];
        final String mes1data = datafinal1[1];
        final String ano1 = datafinal1[0];
        final String data_nova_final = String.valueOf(dia1.replace(" ", "")) + "/" + mes1data + "/" + ano1;
        final String sql = "select COALESCE((select max(nuevento) from TCSEAG),0)+1 as NUEVENTO from dual";
        final PreparedStatement sqlAgenda = jdbcWrapper.getPreparedStatement(sql);
        final ResultSet rsAgenda = sqlAgenda.executeQuery();
        String NUEVENTO = "";
        while (rsAgenda.next()) {
            NUEVENTO = rsAgenda.getString("NUEVENTO");
        }
        final String insertAgenda = "INSERT INTO TCSEAG (CODPARC,nuevento,DHINICIO, DHFINAL, CODUSU, DESCRABREV, SINCRONIZAR, DHLCTO,AD_NUMOSNOVO,AD_NUMITEM) VALUES(" + codparc + "," + NUEVENTO + ",cast('" + data_nova_inicial.replace(".0", "") + " " + horainicial.replace(".0", "") + "' as timestamp),cast('" + data_nova_final.replace(".0", "") + " " + horafinal.replace(".0", "") + "' as timestamp)," + codusuresp + ",'" + descrprod + "','" + sincronizar + "',sysdate ," + NUMOS + "," + NUMITEM + ")";
        final PreparedStatement prepInsert = jdbcWrapper.getPreparedStatement(insertAgenda);
        prepInsert.execute();
        final String updAgenda = "UPDATE  AD_TCSITE SET NUEVENTO= " + NUEVENTO + " WHERE NUMOS=" + NUMOS + " AND NUMITEM=" + NUMITEM;
        final PreparedStatement prepuPDt = jdbcWrapper.getPreparedStatement(updAgenda);
        prepuPDt.execute();
    }
    
    public BigDecimal salvarTCSEAG(final EntityFacade dwf, final BigDecimal codParc, final Timestamp cdhInicio, final Timestamp dhFinal, final BigDecimal codUsu, final String descr, final String sincronizar) throws Exception {
        this.authenticate(new BigDecimal(0));
        final DynamicVO eag = (DynamicVO)dwf.getDefaultValueObjectInstance("EventoAgendado");
        eag.setProperty("CODPARC", (Object)codParc);
        eag.setProperty("DHINICIO", (Object)cdhInicio);
        eag.setProperty("DHFINAL", (Object)dhFinal);
        eag.setProperty("CODUSU", (Object)codUsu);
        eag.setProperty("DESCRABREV", (Object)descr);
        eag.setProperty("SINCRONIZAR", (Object)sincronizar);
        eag.setProperty("DHLCTO", (Object)TimeUtils.getNow());
        dwf.createEntity("EventoAgendado", (EntityVO)eag);
        return eag.asBigDecimal("NUEVENTO");
    }
    
    public static BigDecimal salvarOrdemServico(final EntityFacade dwf, final String nunota, final BigDecimal codusulogado, final BigDecimal codemp, final BigDecimal codcencus, final BigDecimal codparc, final BigDecimal codproj, final BigDecimal codveiculo, final Timestamp dataentrada, final BigDecimal horimetro, final BigDecimal tipoos, final BigDecimal tipofat, final BigDecimal tipoatendimento, final BigDecimal codnat, final String obs, final BigDecimal mecanico) throws Exception {
        final DynamicVO ordemServicoEntity = (DynamicVO)dwf.getDefaultValueObjectInstance("TCSOSE");
        ordemServicoEntity.setProperty("NUNOTA", (Object)new BigDecimal(nunota));
        ordemServicoEntity.setProperty("CODEMP", (Object)codemp);
        ordemServicoEntity.setProperty("CODNAT", (Object)codnat);
        ordemServicoEntity.setProperty("CODCENCUS", (Object)codcencus);
        ordemServicoEntity.setProperty("CODPARC", (Object)codparc);
        ordemServicoEntity.setProperty("CODPROJE", (Object)codproj);
        ordemServicoEntity.setProperty("CODUSU", (Object)codusulogado);
        ordemServicoEntity.setProperty("DHABERTURA", (Object)dataentrada);
        ordemServicoEntity.setProperty("HORIMETRO", (Object)horimetro);
        ordemServicoEntity.setProperty("CODVEICULO", (Object)codveiculo);
        ordemServicoEntity.setProperty("CODTIPODEOS", (Object)tipoos);
        ordemServicoEntity.setProperty("CODFATUR", (Object)tipofat);
        ordemServicoEntity.setProperty("CODATENDIMENTO", (Object)tipoatendimento);
        ordemServicoEntity.setProperty("DESCRSERV", (Object)obs);
        ordemServicoEntity.setProperty("CODUSURESPOFICINA", (Object)mecanico);
        ordemServicoEntity.setProperty("STATUSSERVICO", (Object)"N");
        dwf.createEntity("TCSOSE", (EntityVO)ordemServicoEntity);
        final BigDecimal numos = ordemServicoEntity.asBigDecimal("NUMOS");
        return numos;
    }
    
    public static void salvarTCSNOTAS(final EntityFacade dwf, final BigDecimal codtipoper, final BigDecimal codparc, final BigDecimal nunota, final Timestamp dtneg, final String numos) throws Exception {
        final DynamicVO ordemServicoTCSNOTAS = (DynamicVO)dwf.getDefaultValueObjectInstance("AD_TCSNOTAS");
        ordemServicoTCSNOTAS.setProperty("NUNOTA", (Object)nunota);
        ordemServicoTCSNOTAS.setProperty("NUMOS", (Object)new BigDecimal(numos));
        ordemServicoTCSNOTAS.setProperty("DTNEG", (Object)dtneg);
        ordemServicoTCSNOTAS.setProperty("CODTIPOPER", (Object)codtipoper);
        ordemServicoTCSNOTAS.setProperty("CODPARC", (Object)codparc);
        dwf.createEntity("AD_TCSNOTAS", (EntityVO)ordemServicoTCSNOTAS);
    }
    
    public static void salvarTCSITE(final EntityFacade dwf, final BigDecimal codprod, final Timestamp data, final BigDecimal vlrtot, final BigDecimal codemp2, final BigDecimal numosNovo, final BigDecimal codMecanico) throws Exception {
        final DynamicVO ordemServicoTCSITE = (DynamicVO)dwf.getDefaultValueObjectInstance("TCSITE");
        ordemServicoTCSITE.setProperty("NUMOS", (Object)numosNovo);
        ordemServicoTCSITE.setProperty("CODSERV", (Object)codprod);
        ordemServicoTCSITE.setProperty("DATA", (Object)data);
        ordemServicoTCSITE.setProperty("VALORSERVICO", (Object)vlrtot);
        ordemServicoTCSITE.setProperty("CODEMP", (Object)codemp2);
        ordemServicoTCSITE.setProperty("CODUSURESP", (Object)codMecanico);
        dwf.createEntity("TCSITE", (EntityVO)ordemServicoTCSITE);
    }
    
    public static void salvarTCSPRO(final EntityFacade dwf, final BigDecimal numosNovo, final BigDecimal codprod, final BigDecimal vlrunit, final String nunota, final BigDecimal codparc, final BigDecimal quantidade, final String lote) throws Exception {
        final DynamicVO ordemServicoTCSPRO = (DynamicVO)dwf.getDefaultValueObjectInstance("TCSPRO");
        ordemServicoTCSPRO.setProperty("NUMOS", (Object)numosNovo);
        ordemServicoTCSPRO.setProperty("CODPROD", (Object)codprod);
        ordemServicoTCSPRO.setProperty("VLRUNIT", (Object)vlrunit);
        ordemServicoTCSPRO.setProperty("NUNOTA", (Object)new BigDecimal(nunota));
        ordemServicoTCSPRO.setProperty("QUANTIDADE", (Object)quantidade);
        ordemServicoTCSPRO.setProperty("CODPARC", (Object)codparc);
        if (!lote.equalsIgnoreCase("null")) {
            ordemServicoTCSPRO.setProperty("LOTE", (Object)lote);
        }
        ordemServicoTCSPRO.setProperty("TIPOMOVIMENTO", (Object)"P");
        dwf.createEntity("TCSPRO", (EntityVO)ordemServicoTCSPRO);
    }
    
    public static void salvarMaquinas(final EntityFacade dwf, final BigDecimal nunota, final BigDecimal codprod, final BigDecimal codparc, final String chassis, final String tipo, final BigDecimal notaorigem, final String status, final Timestamp dtalteracao, final BigDecimal codusulogado) throws Exception {
        final DynamicVO maquinasEntity = (DynamicVO)dwf.getDefaultValueObjectInstance("AD_PARQUEDEMAQUINAS");
        maquinasEntity.setProperty("NUNOTA", (Object)nunota);
        maquinasEntity.setProperty("CODPROD", (Object)codprod);
        maquinasEntity.setProperty("CODPARC", (Object)codparc);
        maquinasEntity.setProperty("NROCHASSIS", (Object)chassis);
        maquinasEntity.setProperty("TIPO", (Object)tipo);
        maquinasEntity.setProperty("NOTAORIGEM", (Object)notaorigem);
        maquinasEntity.setProperty("STATUS", (Object)status);
        maquinasEntity.setProperty("DTAQUISICAO", (Object)dtalteracao);
        maquinasEntity.setProperty("DTALTERACAO", (Object)dtalteracao);
        maquinasEntity.setProperty("DTINCLUSAO", (Object)dtalteracao);
        maquinasEntity.setProperty("CODUSU", (Object)codusulogado);
        maquinasEntity.setProperty("CODUSUALT", (Object)codusulogado);
        dwf.createEntity("AD_PARQUEDEMAQUINAS", (EntityVO)maquinasEntity);
    }
    
    private void authenticate(final BigDecimal codigoUsuario) throws Exception {
        this.oldAuthInfo = AuthenticationInfo.getCurrentOrNull();
        if (this.oldAuthInfo != null) {
            AuthenticationInfo.unregistry();
        }
        final DynamicVO usuarioVO = (DynamicVO)EntityFacadeFactory.getDWFFacade().findEntityByPrimaryKeyAsVO("Usuario", new Object[] { codigoUsuario });
        final StringBuffer authID = new StringBuffer();
        authID.append(System.currentTimeMillis()).append(':').append(usuarioVO.asBigDecimal("CODUSU")).append(':').append(this.hashCode());
        (this.authInfo = new AuthenticationInfo(usuarioVO.asString("NOMEUSU"), usuarioVO.asBigDecimalOrZero("CODUSU"), usuarioVO.asBigDecimalOrZero("CODGRUPO"), new Integer(authID.toString().hashCode()))).makeCurrent();
        System.out.println(authID.toString().hashCode());
        JapeSessionContext.putProperty("usuario_logado", (Object)this.authInfo.getUserID());
        JapeSessionContext.putProperty("dh_atual", (Object)new Timestamp(System.currentTimeMillis()));
        JapeSessionContext.putProperty("usuarioVO", (Object)usuarioVO);
        JapeSessionContext.putProperty("authInfo", (Object)this.authInfo);
    }
}
