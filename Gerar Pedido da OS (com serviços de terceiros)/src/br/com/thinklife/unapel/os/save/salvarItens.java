package br.com.thinklife.unapel.os.save;

import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.jape.vo.DynamicVO;
import java.math.BigDecimal;
import br.com.sankhya.jape.EntityFacade;

public class salvarItens
{
    public static BigDecimal maquinas(final EntityFacade dwf, final String chassisIte, final BigDecimal codparc, final BigDecimal marca, final BigDecimal especie, final BigDecimal categoria, final BigDecimal cor, final BigDecimal capPotCil, final BigDecimal modelo) throws Exception {
        final DynamicVO maquina = (DynamicVO)dwf.getDefaultValueObjectInstance("Veiculo");
        maquina.setProperty("CHASSIS", (Object)chassisIte);
        maquina.setProperty("PLACA", (Object)"ABC");
        maquina.setProperty("AFERICAO", (Object)"N");
        maquina.setProperty("TIPOAFERICAO", (Object)"N");
        maquina.setProperty("CODCID", (Object)new BigDecimal(0));
        maquina.setProperty("CODPARC", (Object)codparc);
        maquina.setProperty("AD_NROUNICOMODELO", (Object)modelo);
        maquina.setProperty("AD_CODIGO", (Object)marca);
        maquina.setProperty("AD_NROUNICOESPECIE", (Object)especie);
        maquina.setProperty("AD_NROUNICOCAT", (Object)categoria);
        maquina.setProperty("AD_NROUNICOCOR", (Object)cor);
        maquina.setProperty("AD_NROUNICOCPC", (Object)capPotCil);
        dwf.createEntity("Veiculo", (EntityVO)maquina);
        return maquina.asBigDecimal("CODVEICULO");
    }
}
