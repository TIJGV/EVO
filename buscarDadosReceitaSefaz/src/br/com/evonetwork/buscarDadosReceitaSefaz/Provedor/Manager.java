package br.com.evonetwork.buscarDadosReceitaSefaz.Provedor;

import java.math.BigDecimal;

public class Manager {

    private static final ThreadLocal<BigDecimal> SECURITY_VALUE__ = new ThreadLocal<>();

    public static void setCodigo(BigDecimal info) {
        SECURITY_VALUE__.set(info);
    }
    public static BigDecimal getCodigo() {
        return SECURITY_VALUE__.get();
    }
    public static void limparCodigo(){
        SECURITY_VALUE__.remove();
    }


}
