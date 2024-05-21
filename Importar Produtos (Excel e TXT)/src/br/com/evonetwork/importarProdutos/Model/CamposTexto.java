package br.com.evonetwork.importarProdutos.Model;

import java.math.BigDecimal;

public class CamposTexto {
	
	private BigDecimal sequencia;
	private BigDecimal posIni;
	private BigDecimal posFim;
	private String tabela;
	private String nomeCampo;
	private String tipoCampo;
	private int casasDecimais;
//	private boolean removerEspacos;
//	private boolean removerZerosAEsquerda;

	public CamposTexto() {}
	
	public BigDecimal getSequencia() {
		return sequencia;
	}
	public void setSequencia(BigDecimal sequencia) {
		this.sequencia = sequencia;
	}
	public BigDecimal getPosIni() {
		return posIni;
	}
	public void setPosIni(BigDecimal posIni) {
		this.posIni = posIni;
	}
	public BigDecimal getPosFim() {
		return posFim;
	}
	public void setPosFim(BigDecimal posFim) {
		this.posFim = posFim;
	}
	public String getTabela() {
		return tabela;
	}
	public void setTabela(String tabela) {
		this.tabela = tabela;
	}
	public String getNomeCampo() {
		return nomeCampo;
	}
	public void setNomeCampo(String nomeCampo) {
		this.nomeCampo = nomeCampo;
	}
	public String getTipoCampo() {
		return tipoCampo;
	}
	public void setTipoCampo(String tipoCampo) {
		this.tipoCampo = tipoCampo;
	}
	public int getCasasDecimais() {
		return casasDecimais;
	}
	public void setCasasDecimais(int casasDecimais) {
		this.casasDecimais = casasDecimais;
	}
//	public boolean isRemoverEspacos() {
//		return removerEspacos;
//	}
//	public void setRemoverEspacos(boolean removerEspacos) {
//		this.removerEspacos = removerEspacos;
//	}
//	public boolean isRemoverZerosAEsquerda() {
//		return removerZerosAEsquerda;
//	}
//	public void setRemoverZerosAEsquerda(boolean removerZerosAEsquerda) {
//		this.removerZerosAEsquerda = removerZerosAEsquerda;
//	}

}
