package br.com.evonetwork.integracaoAPISimova.Model;

public class OSxServico {
	
	private String filial = null;
	private String local = null;
	private String ativo = null;
	private String codOs = null;
	private String codTipoServico = null;
	private String codTipoTempo = null;
	private String nroRequisicao = null;
	private String tempoPadrao = null;
	private String tempoCobrado = null;
	private String codServico = null;
	private String codMarca = null;
	
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
	public String getCodOs() {
		return codOs;
	}
	public void setCodOs(String codOs) {
		this.codOs = codOs;
	}
	public String getCodTipoServico() {
		return codTipoServico;
	}
	public void setCodTipoServico(String codTipoServico) {
		this.codTipoServico = codTipoServico;
	}
	public String getCodTipoTempo() {
		return codTipoTempo;
	}
	public void setCodTipoTempo(String codTipoTempo) {
		this.codTipoTempo = codTipoTempo;
	}
	public String getNroRequisicao() {
		return nroRequisicao;
	}
	public void setNroRequisicao(String nroRequisicao) {
		this.nroRequisicao = nroRequisicao;
	}
	public String getTempoPadrao() {
		return tempoPadrao;
	}
	public void setTempoPadrao(String tempoPadrao) {
		this.tempoPadrao = tempoPadrao;
	}
	public String getTempoCobrado() {
		return tempoCobrado;
	}
	public void setTempoCobrado(String tempoCobrado) {
		this.tempoCobrado = tempoCobrado;
	}
	public String getCodServico() {
		return codServico;
	}
	public void setCodServico(String codServico) {
		this.codServico = codServico;
	}
	public String getCodMarca() {
		return codMarca;
	}
	public void setCodMarca(String codMarca) {
		this.codMarca = codMarca;
	}
	
	public String toString() {
		String retorno = "Dados enviados OSxServico: "
				+ "Filial: "+this.filial+",\r\n"
				+ "Local: "+this.local+",\r\n"
				+ "CodigoOS: "+this.codOs+",\r\n"
				+ "CodigoTipoServico: "+this.codTipoServico+",\r\n"
				+ "CodigoTipoTempo: "+this.codTipoTempo+",\r\n"
				+ "NroRequisicao: "+this.nroRequisicao+",\r\n"
				+ "TempoPadrao: "+this.tempoPadrao+",\r\n"
				+ "TempoCobrado: "+this.tempoCobrado+",\r\n"
				+ "CodigoServico: "+this.codServico+",\r\n"
				+ "CodigoMarca: "+this.codMarca+",\r\n"
				+ "Ativo: "+this.ativo+"";
		return retorno;
	}
}
