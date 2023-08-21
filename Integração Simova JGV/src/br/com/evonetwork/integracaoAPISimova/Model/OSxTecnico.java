package br.com.evonetwork.integracaoAPISimova.Model;

public class OSxTecnico {
	
	private String filial = null;
	private String local = null;
	private String ativo = null;
	private String codOS = null;
	private String codTecnico = null;
	private String codStatusOS = null;
	
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
	public String getCodTecnico() {
		return codTecnico;
	}
	public void setCodTecnico(String codTecnico) {
		this.codTecnico = codTecnico;
	}
	public String getCodStatusOS() {
		return codStatusOS;
	}
	public void setCodStatusOS(String codStatusOS) {
		this.codStatusOS = codStatusOS;
	}
	
	public String toString() {
		String retorno = "Dados enviados OSxTecnico: "
				+ "Filial: "+this.filial+",\r\n"
				+ "Local: "+this.local+",\r\n"
				+ "CodigoOS: "+this.codOS+",\r\n"
				+ "CodigoTecnico: "+this.codTecnico+",\r\n"
				+ "CodigoStatusOS: "+this.codStatusOS+",\r\n"
				+ "Ativo: "+this.ativo+"";
		return retorno;
	}
}
