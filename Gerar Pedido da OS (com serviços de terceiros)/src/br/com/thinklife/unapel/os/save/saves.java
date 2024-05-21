package br.com.thinklife.unapel.os.save;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import com.sankhya.util.TimeUtils;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.util.JapeSessionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.thinklife.unapel.os.consulta.buscaDados;

public class saves
{
    private AuthenticationInfo oldAuthInfo;
    private AuthenticationInfo authInfo;
    
    public static BigDecimal cabecalho(EntityFacade dwf, BigDecimal empresa, BigDecimal parceiro, BigDecimal numnota, BigDecimal tipooper, BigDecimal tipvenda, String tipMov, BigDecimal nat, BigDecimal centroresult, BigDecimal parcDest) throws Exception {
        DynamicVO nota = (DynamicVO)dwf.getDefaultValueObjectInstance("CabecalhoNota");
        nota.setProperty("CODEMP", empresa);
        nota.setProperty("CODPARC", parceiro);
        nota.setProperty("NUMNOTA", numnota);
        nota.setProperty("CODTIPOPER", tipooper);
        nota.setProperty("CODTIPVENDA", tipvenda);
        nota.setProperty("TIPMOV", tipMov);
        nota.setProperty("CODNAT", nat);
        nota.setProperty("CODCENCUS", centroresult);
        nota.setProperty("CODPARCDEST", parcDest);
        dwf.createEntity("CabecalhoNota", (EntityVO)nota);
        BigDecimal nunota = nota.asBigDecimal("NUNOTA");
        return nunota;
    }
    
    public static BigDecimal cabecalhoReqPeca(EntityFacade dwf, JdbcWrapper jdbcWrapper, BigDecimal numOs, BigDecimal usuLogado) throws Exception {
        BigDecimal nunota = BigDecimal.ZERO;
        ResultSet query = buscaDados.modNotaReqPeca(jdbcWrapper, numOs, usuLogado);
        while (query.next()) {
            DynamicVO nota = (DynamicVO)dwf.getDefaultValueObjectInstance("CabecalhoNota");
            nota.setProperty("CODEMP", query.getBigDecimal("CODEMP"));
            nota.setProperty("CODPARC", query.getBigDecimal("CODPARC"));
            nota.setProperty("NUMNOTA", query.getBigDecimal("NUMNOTA"));
            nota.setProperty("CODTIPOPER", query.getBigDecimal("CODTIPOPER"));
            nota.setProperty("CODTIPVENDA", query.getBigDecimal("CODTIPVENDA"));
            nota.setProperty("TIPMOV", new StringBuilder().append(query.getString("TIPMOV")).toString());
            nota.setProperty("CODNAT", query.getBigDecimal("CODNAT"));
            nota.setProperty("CODCENCUS", query.getBigDecimal("CODCENCUS"));
            nota.setProperty("CODPARCDEST", query.getBigDecimal("PARCDEST"));
            nota.setProperty("CODUSUINC", usuLogado);
            nota.setProperty("CODVEND", query.getBigDecimal("VENDEDOR"));
            dwf.createEntity("CabecalhoNota", (EntityVO)nota);
            nunota = nota.asBigDecimal("NUNOTA");
        }
        return nunota;
    }
    
    public static BigDecimal cabecalho2(EntityFacade dwf, BigDecimal empresa, BigDecimal parceiro, BigDecimal numnota, BigDecimal tipooper, BigDecimal tipvenda, String tipMov, BigDecimal nat, BigDecimal centroresult, BigDecimal notaorigem) throws Exception {
        DynamicVO nota = (DynamicVO)dwf.getDefaultValueObjectInstance("CabecalhoNota");
        nota.setProperty("CODEMP", empresa);
        nota.setProperty("CODPARC", parceiro);
        nota.setProperty("NUMNOTA", numnota);
        nota.setProperty("CODTIPOPER", tipooper);
        nota.setProperty("CODTIPVENDA", tipvenda);
        nota.setProperty("TIPMOV", tipMov);
        nota.setProperty("CODNAT", nat);
        nota.setProperty("CODCENCUS", centroresult);
        nota.setProperty("AD_NOTAORIGEM", notaorigem);
        dwf.createEntity("CabecalhoNota", (EntityVO)nota);
        BigDecimal nunota = nota.asBigDecimal("NUNOTA");
        return nunota;
    }
    
    public static BigDecimal item(EntityFacade dwf, BigDecimal nunota, BigDecimal codProd, String codVol, BigDecimal qtd, BigDecimal codlocalOrigem, BigDecimal localDestino, BigDecimal contador, String lote) throws Exception {
        DynamicVO iteOrigem = (DynamicVO)dwf.getDefaultValueObjectInstance("ItemNota");
        iteOrigem.setProperty("NUNOTA", nunota);
        iteOrigem.setProperty("SEQUENCIA", contador);
        iteOrigem.setProperty("ATUALESTOQUE", new BigDecimal(-1));
        iteOrigem.setProperty("CODPROD", codProd);
        iteOrigem.setProperty("CODVOL", codVol);
        iteOrigem.setProperty("QTDNEG", qtd);
        iteOrigem.setProperty("CODLOCALORIG", codlocalOrigem);
        iteOrigem.setProperty("CODLOCALDEST", localDestino);
        iteOrigem.setProperty("CONTROLE", lote);
        dwf.createEntity("ItemNota", (EntityVO)iteOrigem);
        return iteOrigem.asBigDecimal("SEQUENCIA");
    }
    
    public static BigDecimal item2(EntityFacade dwf, BigDecimal nunota, BigDecimal codProd, String codVol, BigDecimal qtd, BigDecimal codlocalOrigem, BigDecimal localDestino, BigDecimal contador, String lote, String necLote) throws Exception {
        DynamicVO iteOrigem = (DynamicVO)dwf.getDefaultValueObjectInstance("ItemNota");
        iteOrigem.setProperty("NUNOTA", nunota);
        iteOrigem.setProperty("SEQUENCIA", contador);
        iteOrigem.setProperty("ATUALESTOQUE", new BigDecimal(1));
        iteOrigem.setProperty("CODPROD", codProd);
        iteOrigem.setProperty("RESERVA", "S");
        iteOrigem.setProperty("CODVOL", codVol);
        iteOrigem.setProperty("QTDNEG", qtd);
        iteOrigem.setProperty("CODLOCALORIG", codlocalOrigem);
        iteOrigem.setProperty("CODLOCALDEST", localDestino);
        if (!necLote.equals("N")) {
            iteOrigem.setProperty("CONTROLE", lote);
        }
        iteOrigem.setProperty("TERCEIROS", "N");
        iteOrigem.setProperty("ATUALESTTERC", "N");
        iteOrigem.setProperty("USOPROD", "M");
        iteOrigem.setProperty("STATUSLOTE", "P");
        dwf.createEntity("ItemNota", (EntityVO)iteOrigem);
        return iteOrigem.asBigDecimal("SEQUENCIA");
    }
    
    public static BigDecimal item3(EntityFacade dwf, BigDecimal nunota, BigDecimal codProd, String codVol, BigDecimal qtd, BigDecimal codlocalOrigem, BigDecimal localDestino, BigDecimal contador, String lote, String necLote, BigDecimal vlrUnit, BigDecimal vlrTot) throws Exception {
        DynamicVO iteOrigem = (DynamicVO)dwf.getDefaultValueObjectInstance("ItemNota");
        iteOrigem.setProperty("NUNOTA", nunota);
        iteOrigem.setProperty("SEQUENCIA", contador);
        iteOrigem.setProperty("ATUALESTOQUE", new BigDecimal(1));
        iteOrigem.setProperty("CODPROD", codProd);
        iteOrigem.setProperty("RESERVA", "S");
        iteOrigem.setProperty("CODVOL", codVol);
        iteOrigem.setProperty("QTDNEG", qtd);
        iteOrigem.setProperty("CODLOCALORIG", codlocalOrigem);
        iteOrigem.setProperty("CODLOCALDEST", localDestino);
        if (!necLote.equals("N")) {
            iteOrigem.setProperty("CONTROLE", lote);
        }
        iteOrigem.setProperty("TERCEIROS", "N");
        iteOrigem.setProperty("ATUALESTTERC", "N");
        iteOrigem.setProperty("USOPROD", "M");
        iteOrigem.setProperty("STATUSLOTE", "P");
        iteOrigem.setProperty("VLRTOT", vlrTot);
        iteOrigem.setProperty("VLRUNIT", vlrUnit);
        dwf.createEntity("ItemNota", (EntityVO)iteOrigem);
        return iteOrigem.asBigDecimal("SEQUENCIA");
    }
    
    public static void notasGeradas(EntityFacade dwf, BigDecimal codTipOper, BigDecimal numos, BigDecimal nunota) throws Exception {
        DynamicVO notagerada = (DynamicVO)dwf.getDefaultValueObjectInstance("AD_TCSNOTAS");
        notagerada.setProperty("CODTIPOPER", codTipOper);
        notagerada.setProperty("NUMOS", numos);
        notagerada.setProperty("NUNOTA", nunota);
        dwf.createEntity("AD_TCSNOTAS", (EntityVO)notagerada);
    }
    
    public static BigDecimal itemPecasUtilizadas(EntityFacade dwf, BigDecimal nunota, BigDecimal codProd, String codVol, BigDecimal qtd, BigDecimal codlocalOrigem, BigDecimal localDestino, BigDecimal contador, BigDecimal vlrtot, String lote, BigDecimal atualEstoque, String reserva, BigDecimal vlrUnit, String usoProd, BigDecimal descReais, BigDecimal descPorcentagem) throws Exception {
        DynamicVO iteOrigem = (DynamicVO)dwf.getDefaultValueObjectInstance("ItemNota");
        iteOrigem.setProperty("NUNOTA", nunota);
        iteOrigem.setProperty("SEQUENCIA", contador);
        iteOrigem.setProperty("CODPROD", codProd);
        iteOrigem.setProperty("CODVOL", codVol);
        iteOrigem.setProperty("QTDNEG", qtd);
        iteOrigem.setProperty("CODLOCALORIG", codlocalOrigem);
        iteOrigem.setProperty("CODLOCALDEST", localDestino);
        iteOrigem.setProperty("VLRTOT", vlrtot);
        iteOrigem.setProperty("PENDENTE", "S");
        iteOrigem.setProperty("QTDENTREGUE", BigDecimal.ZERO);
        iteOrigem.setProperty("ATUALESTOQUE", atualEstoque);
        iteOrigem.setProperty("RESERVA", reserva);
        iteOrigem.setProperty("VLRUNIT", vlrUnit);
        iteOrigem.setProperty("USOPROD", usoProd);
//    	iteOrigem.setProperty("PERCDESC", descPorcentagem);
//    	iteOrigem.setProperty("VLRDESC", descReais);
        if (lote != null) {
            iteOrigem.setProperty("CONTROLE", lote);
        }
        dwf.createEntity("ItemNota", (EntityVO)iteOrigem);
        return iteOrigem.asBigDecimal("SEQUENCIA");
    }
    
    public static BigDecimal itemServicosUtilizados(EntityFacade dwf, BigDecimal nunota, BigDecimal codProd, String codVol, BigDecimal qtd, BigDecimal codlocalOrigem, BigDecimal localDestino, BigDecimal contador, BigDecimal vlrtot, String lote, BigDecimal atualEstoque, String reserva, BigDecimal vlrUnit, String usoProd, BigDecimal codVendServico) throws Exception {
        DynamicVO iteOrigem = (DynamicVO)dwf.getDefaultValueObjectInstance("ItemNota");
        iteOrigem.setProperty("NUNOTA", nunota);
        iteOrigem.setProperty("SEQUENCIA", contador);
        iteOrigem.setProperty("CODPROD", codProd);
        iteOrigem.setProperty("CODVOL", codVol);
        iteOrigem.setProperty("QTDNEG", qtd);
        iteOrigem.setProperty("CODLOCALORIG", codlocalOrigem);
        iteOrigem.setProperty("CODLOCALDEST", localDestino);
        iteOrigem.setProperty("VLRTOT", vlrtot);
        iteOrigem.setProperty("PENDENTE", "S");
        iteOrigem.setProperty("QTDENTREGUE", BigDecimal.ZERO);
        iteOrigem.setProperty("ATUALESTOQUE", atualEstoque);
        iteOrigem.setProperty("RESERVA", reserva);
        iteOrigem.setProperty("VLRUNIT", vlrUnit);
        iteOrigem.setProperty("USOPROD", usoProd);
        iteOrigem.setProperty("CODVEND", codVendServico);
        if (lote != null) {
            iteOrigem.setProperty("CONTROLE", lote);
        }
        dwf.createEntity("ItemNota", (EntityVO)iteOrigem);
        return iteOrigem.asBigDecimal("SEQUENCIA");
    }
    
    public static BigDecimal cabecalhoPecasUtilizadas(EntityFacade dwf, BigDecimal empresa, BigDecimal parceiro, BigDecimal parceiroDest, BigDecimal numnota, BigDecimal tipooper, BigDecimal tipvenda, BigDecimal nat, BigDecimal centroresult, BigDecimal numos, String tipmov, String tipoOs, BigDecimal localVenda, BigDecimal vendedor, BigDecimal codVeiculo) throws Exception {
        DynamicVO nota = (DynamicVO)dwf.getDefaultValueObjectInstance("CabecalhoNota");
        nota.setProperty("CODEMP", empresa);
        nota.setProperty("CODPARC", parceiro);
        nota.setProperty("CODPARCDEST", parceiroDest);
        nota.setProperty("NUMNOTA", numnota);
        nota.setProperty("CODTIPOPER", tipooper);
        nota.setProperty("CODTIPVENDA", tipvenda);
        nota.setProperty("CODNAT", nat);
        nota.setProperty("CODCENCUS", centroresult);
        nota.setProperty("AD_NUMOSOFICINA", numos);
        nota.setProperty("TIPMOV", tipmov);
        nota.setProperty("PENDENTE", "S");
        nota.setProperty("AD_CODTIPODEOS", new BigDecimal(tipoOs));
        nota.setProperty("AD_CODOAT", localVenda);
        nota.setProperty("CODVEND", vendedor);
        nota.setProperty("CODVEICULO", codVeiculo);
        dwf.createEntity("CabecalhoNota", (EntityVO)nota);
        BigDecimal nunota = nota.asBigDecimalOrZero("NUNOTA");
        return nunota;
    }
    
    public static void salvarTCSNOTAS(EntityFacade dwf, BigDecimal codtipoper, BigDecimal codparc, BigDecimal nunota, Timestamp data, BigDecimal numos) throws Exception {
        DynamicVO ordemServicoTCSNOTAS = (DynamicVO)dwf.getDefaultValueObjectInstance("AD_TCSNOTAS");
        ordemServicoTCSNOTAS.setProperty("CODTIPOPER", codtipoper);
        ordemServicoTCSNOTAS.setProperty("CODPARC", codparc);
        ordemServicoTCSNOTAS.setProperty("NUNOTA", nunota);
        ordemServicoTCSNOTAS.setProperty("DTNEG", data);
        ordemServicoTCSNOTAS.setProperty("NUMOS", numos);
        dwf.createEntity("AD_TCSNOTAS", (EntityVO)ordemServicoTCSNOTAS);
    }
    
    public static void salvarTGFVAR(EntityFacade dwf, BigDecimal nunota, BigDecimal sequencia, BigDecimal nunotaorigem, BigDecimal seqorigem, String statusnota) throws Exception {
        DynamicVO cadastroTGFVAR = (DynamicVO)dwf.getDefaultValueObjectInstance("CompraVendavariosPedido");
        cadastroTGFVAR.setProperty("NUNOTA", nunota);
        cadastroTGFVAR.setProperty("SEQUENCIA", sequencia);
        cadastroTGFVAR.setProperty("NUNOTAORIG", nunotaorigem);
        cadastroTGFVAR.setProperty("SEQUENCIAORIG", seqorigem);
        cadastroTGFVAR.setProperty("STATUSNOTA", statusnota);
        dwf.createEntity("CompraVendavariosPedido", (EntityVO)cadastroTGFVAR);
    }
    
    public void criandoAgendaRecursos(EntityFacade dwf, JdbcWrapper jdbcWrapper, BigDecimal codusuresp, String hrinicial, String hrfinal, String descrprod, String sincronizar, String dhagenda, BigDecimal NUMOS, BigDecimal NUMITEM, BigDecimal codparc) throws Exception {
        this.authenticate(new BigDecimal(0));
        String horainicial = hrinicial.split(" ")[1];
        String horafinal = hrfinal.split(" ")[1];
        String[] datainicial1 = hrinicial.replace(horainicial, "").split("-");
        String diadata = datainicial1[2];
        String mesdata = datainicial1[1];
        String anodata = datainicial1[0];
        String data_nova_inicial = String.valueOf(diadata.replace(" ", "")) + "/" + mesdata + "/" + anodata;
        String[] datafinal1 = hrfinal.replace(horafinal, "").split("-");
        String dia1 = datafinal1[2];
        String mes1data = datafinal1[1];
        String ano1 = datafinal1[0];
        String data_nova_final = String.valueOf(dia1.replace(" ", "")) + "/" + mes1data + "/" + ano1;
        String sql = "select COALESCE((select max(nuevento) from TCSEAG),0)+1 as NUEVENTO from dual";
        PreparedStatement sqlAgenda = jdbcWrapper.getPreparedStatement(sql);
        ResultSet rsAgenda = sqlAgenda.executeQuery();
        String NUEVENTO = "";
        while (rsAgenda.next()) {
            NUEVENTO = rsAgenda.getString("NUEVENTO");
        }
        String insertAgenda = "INSERT INTO TCSEAG (CODPARC,nuevento,DHINICIO, DHFINAL, CODUSU, DESCRABREV, SINCRONIZAR, DHLCTO,AD_NUMOSNOVO,AD_NUMITEM) VALUES(" + codparc + "," + NUEVENTO + ",cast('" + data_nova_inicial.replace(".0", "") + " " + horainicial.replace(".0", "") + "' as timestamp),cast('" + data_nova_final.replace(".0", "") + " " + horafinal.replace(".0", "") + "' as timestamp)," + codusuresp + ",'" + descrprod + "','" + sincronizar + "',sysdate ," + NUMOS + "," + NUMITEM + ")";
        PreparedStatement prepInsert = jdbcWrapper.getPreparedStatement(insertAgenda);
        prepInsert.execute();
        String updAgenda = "UPDATE  AD_TCSITE SET NUEVENTO= " + NUEVENTO + " WHERE NUMOS=" + NUMOS + " AND NUMITEM=" + NUMITEM;
        PreparedStatement prepuPDt = jdbcWrapper.getPreparedStatement(updAgenda);
        prepuPDt.execute();
    }
    
    public BigDecimal salvarTCSEAG(EntityFacade dwf, BigDecimal codParc, Timestamp cdhInicio, Timestamp dhFinal, BigDecimal codUsu, String descr, String sincronizar) throws Exception {
        this.authenticate(new BigDecimal(0));
        DynamicVO eag = (DynamicVO)dwf.getDefaultValueObjectInstance("EventoAgendado");
        eag.setProperty("CODPARC", codParc);
        eag.setProperty("DHINICIO", cdhInicio);
        eag.setProperty("DHFINAL", dhFinal);
        eag.setProperty("CODUSU", codUsu);
        eag.setProperty("DESCRABREV", descr);
        eag.setProperty("SINCRONIZAR", sincronizar);
        eag.setProperty("DHLCTO", TimeUtils.getNow());
        dwf.createEntity("EventoAgendado", (EntityVO)eag);
        return eag.asBigDecimal("NUEVENTO");
    }
    
    public static BigDecimal salvarOrdemServico(EntityFacade dwf, String nunota, BigDecimal codusulogado, BigDecimal codemp, BigDecimal codcencus, BigDecimal codparc, BigDecimal codproj, BigDecimal codveiculo, Timestamp dataentrada, BigDecimal horimetro, BigDecimal tipoos, BigDecimal tipofat, BigDecimal tipoatendimento, BigDecimal codnat, String obs, BigDecimal mecanico) throws Exception {
        DynamicVO ordemServicoEntity = (DynamicVO)dwf.getDefaultValueObjectInstance("TCSOSE");
        ordemServicoEntity.setProperty("NUNOTA", new BigDecimal(nunota));
        ordemServicoEntity.setProperty("CODEMP", codemp);
        ordemServicoEntity.setProperty("CODNAT", codnat);
        ordemServicoEntity.setProperty("CODCENCUS", codcencus);
        ordemServicoEntity.setProperty("CODPARC", codparc);
        ordemServicoEntity.setProperty("CODPROJE", codproj);
        ordemServicoEntity.setProperty("CODUSU", codusulogado);
        ordemServicoEntity.setProperty("DHABERTURA", dataentrada);
        ordemServicoEntity.setProperty("HORIMETRO", horimetro);
        ordemServicoEntity.setProperty("CODVEICULO", codveiculo);
        ordemServicoEntity.setProperty("CODTIPODEOS", tipoos);
        ordemServicoEntity.setProperty("CODFATUR", tipofat);
        ordemServicoEntity.setProperty("CODATENDIMENTO", tipoatendimento);
        ordemServicoEntity.setProperty("DESCRSERV", obs);
        ordemServicoEntity.setProperty("CODUSURESPOFICINA", mecanico);
        ordemServicoEntity.setProperty("STATUSSERVICO", "N");
        dwf.createEntity("TCSOSE", (EntityVO)ordemServicoEntity);
        BigDecimal numos = ordemServicoEntity.asBigDecimal("NUMOS");
        return numos;
    }
    
    public static void salvarTCSNOTAS(EntityFacade dwf, BigDecimal codtipoper, BigDecimal codparc, BigDecimal nunota, Timestamp dtneg, String numos) throws Exception {
        DynamicVO ordemServicoTCSNOTAS = (DynamicVO)dwf.getDefaultValueObjectInstance("AD_TCSNOTAS");
        ordemServicoTCSNOTAS.setProperty("NUNOTA", nunota);
        ordemServicoTCSNOTAS.setProperty("NUMOS", new BigDecimal(numos));
        ordemServicoTCSNOTAS.setProperty("DTNEG", dtneg);
        ordemServicoTCSNOTAS.setProperty("CODTIPOPER", codtipoper);
        ordemServicoTCSNOTAS.setProperty("CODPARC", codparc);
        dwf.createEntity("AD_TCSNOTAS", (EntityVO)ordemServicoTCSNOTAS);
    }
    
    public static void salvarTCSITE(EntityFacade dwf, BigDecimal codprod, Timestamp data, BigDecimal vlrtot, BigDecimal codemp2, BigDecimal numosNovo, BigDecimal codMecanico) throws Exception {
        DynamicVO ordemServicoTCSITE = (DynamicVO)dwf.getDefaultValueObjectInstance("TCSITE");
        ordemServicoTCSITE.setProperty("NUMOS", numosNovo);
        ordemServicoTCSITE.setProperty("CODSERV", codprod);
        ordemServicoTCSITE.setProperty("DATA", data);
        ordemServicoTCSITE.setProperty("VALORSERVICO", vlrtot);
        ordemServicoTCSITE.setProperty("CODEMP", codemp2);
        ordemServicoTCSITE.setProperty("CODUSURESP", codMecanico);
        dwf.createEntity("TCSITE", (EntityVO)ordemServicoTCSITE);
    }
    
    public static void salvarTCSPRO(EntityFacade dwf, BigDecimal numosNovo, BigDecimal codprod, BigDecimal vlrunit, String nunota, BigDecimal codparc, BigDecimal quantidade, String lote) throws Exception {
        DynamicVO ordemServicoTCSPRO = (DynamicVO)dwf.getDefaultValueObjectInstance("TCSPRO");
        ordemServicoTCSPRO.setProperty("NUMOS", numosNovo);
        ordemServicoTCSPRO.setProperty("CODPROD", codprod);
        ordemServicoTCSPRO.setProperty("VLRUNIT", vlrunit);
        ordemServicoTCSPRO.setProperty("NUNOTA", new BigDecimal(nunota));
        ordemServicoTCSPRO.setProperty("QUANTIDADE", quantidade);
        ordemServicoTCSPRO.setProperty("CODPARC", codparc);
        if (!lote.equalsIgnoreCase("null")) {
            ordemServicoTCSPRO.setProperty("LOTE", lote);
        }
        ordemServicoTCSPRO.setProperty("TIPOMOVIMENTO", "P");
        dwf.createEntity("TCSPRO", (EntityVO)ordemServicoTCSPRO);
    }
    
    public static void salvarMaquinas(EntityFacade dwf, BigDecimal nunota, BigDecimal codprod, BigDecimal codparc, String chassis, String tipo, BigDecimal notaorigem, String status, Timestamp dtalteracao, BigDecimal codusulogado) throws Exception {
        DynamicVO maquinasEntity = (DynamicVO)dwf.getDefaultValueObjectInstance("AD_PARQUEDEMAQUINAS");
        maquinasEntity.setProperty("NUNOTA", nunota);
        maquinasEntity.setProperty("CODPROD", codprod);
        maquinasEntity.setProperty("CODPARC", codparc);
        maquinasEntity.setProperty("NROCHASSIS", chassis);
        maquinasEntity.setProperty("TIPO", tipo);
        maquinasEntity.setProperty("NOTAORIGEM", notaorigem);
        maquinasEntity.setProperty("STATUS", status);
        maquinasEntity.setProperty("DTAQUISICAO", dtalteracao);
        maquinasEntity.setProperty("DTALTERACAO", dtalteracao);
        maquinasEntity.setProperty("DTINCLUSAO", dtalteracao);
        maquinasEntity.setProperty("CODUSU", codusulogado);
        maquinasEntity.setProperty("CODUSUALT", codusulogado);
        dwf.createEntity("AD_PARQUEDEMAQUINAS", (EntityVO)maquinasEntity);
    }
    
    private void authenticate(BigDecimal codigoUsuario) throws Exception {
        this.oldAuthInfo = AuthenticationInfo.getCurrentOrNull();
        if (this.oldAuthInfo != null) {
            AuthenticationInfo.unregistry();
        }
        DynamicVO usuarioVO = (DynamicVO)EntityFacadeFactory.getDWFFacade().findEntityByPrimaryKeyAsVO("Usuario", new Object[] { codigoUsuario });
        StringBuffer authID = new StringBuffer();
        authID.append(System.currentTimeMillis()).append(':').append(usuarioVO.asBigDecimal("CODUSU")).append(':').append(this.hashCode());
        (this.authInfo = new AuthenticationInfo(usuarioVO.asString("NOMEUSU"), usuarioVO.asBigDecimalOrZero("CODUSU"), usuarioVO.asBigDecimalOrZero("CODGRUPO"), new Integer(authID.toString().hashCode()))).makeCurrent();
        System.out.println(authID.toString().hashCode());
        JapeSessionContext.putProperty("usuario_logado", this.authInfo.getUserID());
        JapeSessionContext.putProperty("dh_atual", new Timestamp(System.currentTimeMillis()));
        JapeSessionContext.putProperty("usuarioVO", usuarioVO);
        JapeSessionContext.putProperty("authInfo", this.authInfo);
    }

	public static BigDecimal criarServicoDeTerceiroNaOS(BigDecimal nunota, BigDecimal codProd, String codVol, BigDecimal qtd, BigDecimal codlocalOrigem, BigDecimal localDestino, BigDecimal contador, BigDecimal vlrtot, String lote, BigDecimal atualEstoque, String reserva, BigDecimal vlrUnit, String usoprod) throws Exception {
		JapeSession.SessionHandle hnd = null;
		BigDecimal sequencia = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper tgfiteDAO = JapeFactory.dao(DynamicEntityNames.ITEM_NOTA);
			DynamicVO save = tgfiteDAO.create()
				.set("NUNOTA", nunota)
				.set("SEQUENCIA", contador)
				.set("CODPROD", codProd)
				.set("CODVOL", codVol)
				.set("QTDNEG", qtd)
				.set("CODLOCALORIG", codlocalOrigem)
				.set("CODLOCALDEST", localDestino)
				.set("VLRTOT", vlrtot)
				.set("PENDENTE", "S")
				.set("QTDENTREGUE", BigDecimal.ZERO)
				.set("ATUALESTOQUE", atualEstoque)
				.set("RESERVA", reserva)
				.set("VLRUNIT", vlrUnit)
				.set("USOPROD", usoprod)
				.save();
			sequencia = save.asBigDecimal("SEQUENCIA");
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
		return sequencia;
	}

	public static void criarRegistroNaTGFVAR(BigDecimal nunota, BigDecimal sequencia, BigDecimal nunotaorigem,
			BigDecimal seqorigem, String statusnota) throws Exception {
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper tgfvarDAO = JapeFactory.dao(DynamicEntityNames.COMPRA_VENDA_VARIOS_PEDIDO);
			@SuppressWarnings("unused")
			DynamicVO save = (DynamicVO) tgfvarDAO.create()
				.set("NUNOTA", nunota)
				.set("SEQUENCIA", sequencia)
				.set("NUNOTAORIG", nunotaorigem)
				.set("SEQUENCIAORIG", seqorigem)
				.set("STATUSNOTA", statusnota)
				.save();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}
}
