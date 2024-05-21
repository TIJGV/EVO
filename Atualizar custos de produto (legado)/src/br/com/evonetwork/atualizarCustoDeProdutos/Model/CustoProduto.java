package br.com.evonetwork.atualizarCustoDeProdutos.Model;

import java.sql.Timestamp;
import java.math.BigDecimal;

public class CustoProduto {
	
    private BigDecimal codProd;
    private BigDecimal codEmp;
    private Timestamp dtAtual;
    private BigDecimal codLocal;
    private String controle;
    private BigDecimal cusMedIcm;
    private BigDecimal cusSemIcm;
    private BigDecimal cusRep;
    private BigDecimal cusVariavel;
    private BigDecimal cusGer;
    private BigDecimal cusMed;
    private BigDecimal entSemIcm;
    private BigDecimal entComIcm;
	private String processo;
    
    @Override
    public String toString() {
        return "CustoProduto: {codProd:" + this.codProd + ", " + "codEmp:" + this.codEmp + ", " + "dtAtual:" + this.dtAtual + ", " + "codLocal:" + this.codLocal + ", " + "controle:" + this.controle + ", " + "cusMedIcm:" + this.cusMedIcm + ", " + "cusSemIcm:" + this.cusSemIcm + ", " + "cusRep:" + this.cusRep + ", " + "cusVariavel:" + this.cusVariavel + ", " + "cusGer:" + this.cusGer + ", " + "cusMed:" + this.cusMed + ", " + "processo:" + this.processo + "}";
    }
    
    public BigDecimal getCodProd() {
        return this.codProd;
    }
    
    public void setCodProd(final BigDecimal codProd) {
        this.codProd = codProd;
    }
    
    public BigDecimal getCodEmp() {
        return this.codEmp;
    }
    
    public void setCodEmp(final BigDecimal codEmp) {
        this.codEmp = codEmp;
    }
    
    public Timestamp getDtAtual() {
        return this.dtAtual;
    }
    
    public void setDtAtual(final Timestamp dtAtual) {
        this.dtAtual = dtAtual;
    }
    
    public BigDecimal getCodLocal() {
        return this.codLocal;
    }
    
    public void setCodLocal(final BigDecimal codLocal) {
        this.codLocal = codLocal;
    }
    
    public String getControle() {
        return this.controle;
    }
    
    public void setControle(final String controle) {
        this.controle = controle;
    }
    
    public BigDecimal getCusMedIcm() {
        return this.cusMedIcm;
    }
    
    public void setCusMedIcm(final BigDecimal cusMedIcm) {
        this.cusMedIcm = cusMedIcm;
    }
    
    public BigDecimal getCusSemIcm() {
        return this.cusSemIcm;
    }
    
    public void setCusSemIcm(final BigDecimal cusSemIcm) {
        this.cusSemIcm = cusSemIcm;
    }
    
    public BigDecimal getCusRep() {
        return this.cusRep;
    }
    
    public void setCusRep(final BigDecimal cusRep) {
        this.cusRep = cusRep;
    }
    
    public BigDecimal getCusVariavel() {
        return this.cusVariavel;
    }
    
    public void setCusVariavel(final BigDecimal cusVariavel) {
        this.cusVariavel = cusVariavel;
    }
    
    public BigDecimal getCusGer() {
        return this.cusGer;
    }
    
    public void setCusGer(final BigDecimal cusGer) {
        this.cusGer = cusGer;
    }
    
    public BigDecimal getCusMed() {
        return this.cusMed;
    }
    
    public void setCusMed(final BigDecimal cusMed) {
        this.cusMed = cusMed;
    }
    
    public String getProcesso() {
        return this.processo;
    }
    
    public void setProcesso(final String processo) {
        this.processo = processo;
    }
    
    public BigDecimal getEntSemIcm() {
		return entSemIcm;
	}

	public void setEntSemIcm(BigDecimal entSemIcm) {
		this.entSemIcm = entSemIcm;
	}

	public BigDecimal getEntComIcm() {
		return entComIcm;
	}

	public void setEntComIcm(BigDecimal entComIcm) {
		this.entComIcm = entComIcm;
	}
}
