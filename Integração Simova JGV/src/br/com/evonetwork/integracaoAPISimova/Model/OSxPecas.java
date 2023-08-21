package br.com.evonetwork.integracaoAPISimova.Model;

public class OSxPecas {
	
	private String filial = null;
	private String local = null;
	private String ativo = null;
	private String codOS = null;
	private String sequencia = null;
	private String codProduto = null;
	private String qtdRequisitada = null;
	private String qtdUtilizada = null;
	private String qtdDevolvida = null;
	
	public String getFilial() {
		return filial;
	}
	public void setFilial(String filial) {
		this.filial = filial;
	}
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}
	public String getAtivo() {
		return ativo;
	}
	public void setAtivo(String ativo) {
		this.ativo = ativo;
	}
	public String getCodOS() {
		return codOS;
	}
	public void setCodOS(String codOS) {
		this.codOS = codOS;
	}
	public String getCodProduto() {
		return codProduto;
	}
	public void setCodProduto(String codProduto) {
		this.codProduto = codProduto;
	}
	public String getQtdRequisitada() {
		return qtdRequisitada;
	}
	public void setQtdRequisitada(String qtdRequisitada) {
		this.qtdRequisitada = qtdRequisitada;
	}
	public String getQtdUtilizada() {
		return qtdUtilizada;
	}
	public void setQtdUtilizada(String qtdUtilizada) {
		this.qtdUtilizada = qtdUtilizada;
	}
	public String getQtdDevolvida() {
		return qtdDevolvida;
	}
	public void setQtdDevolvida(String qtdDevolvida) {
		this.qtdDevolvida = qtdDevolvida;
	}
	
	public String getSequencia() {
		return sequencia;
	}
	public void setSequencia(String sequencia) {
		this.sequencia = sequencia;
	}
	public String toString() {
		String retorno = "Dados enviados OSxPecas: "
				+ "Filial: "+this.filial+",\r\n"
				+ "Local: "+this.local+",\r\n"
				+ "CodigoOS: "+this.codOS+",\r\n"
				+ "CodigoProduto: "+this.codProduto+",\r\n"
				+ "QtdRequisitada: "+this.qtdRequisitada+",\r\n"
				+ "QtdUtilizada: "+this.qtdUtilizada+",\r\n"
				+ "QtdDevolvida: "+this.qtdDevolvida+",\r\n"
				+ "Ativo: "+this.ativo+"";
		return retorno;
	}
}
