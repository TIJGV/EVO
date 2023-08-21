package br.com.evonetwork.integracaoAPISimova.Model;

public class OS {
	
	private String filial = null;
	private String local = null;
	private String ativo = null;
	private String codOS = null;
	private String chassiVeiculo = null;
	private String codMarca = null;
	private String tipoAtendimento = null;
	private String proprietario = null;
	private String lojaProprietario = null;
	private String observacao = null;
	private String dataInclusaoOS = null;
	private String dataEntregaVeiculo = null;
	private String codStatusOS = null;
	private String codTipoOS = null;
	
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
	public String getChassiVeiculo() {
		return chassiVeiculo;
	}
	public void setChassiVeiculo(String chassiVeiculo) {
		this.chassiVeiculo = chassiVeiculo;
	}
	public String getCodMarca() {
		return codMarca;
	}
	public void setCodMarca(String codMarca) {
		this.codMarca = codMarca;
	}
	public String getTipoAtendimento() {
		return tipoAtendimento;
	}
	public void setTipoAtendimento(String tipoAtendimento) {
		this.tipoAtendimento = tipoAtendimento;
	}
	public String getProprietario() {
		return proprietario;
	}
	public void setProprietario(String proprietario) {
		this.proprietario = proprietario;
	}
	public String getLojaProprietario() {
		return lojaProprietario;
	}
	public void setLojaProprietario(String lojaProprietario) {
		this.lojaProprietario = lojaProprietario;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	public String getDataInclusaoOS() {
		return dataInclusaoOS;
	}
	public void setDataInclusaoOS(String dataInclusaoOS) {
		this.dataInclusaoOS = dataInclusaoOS;
	}
	public String getDataEntregaVeiculo() {
		return dataEntregaVeiculo;
	}
	public void setDataEntregaVeiculo(String dataEntregaVeiculo) {
		this.dataEntregaVeiculo = dataEntregaVeiculo;
	}
	public String getCodStatusOS() {
		return codStatusOS;
	}
	public void setCodStatusOS(String codStatusOS) {
		this.codStatusOS = codStatusOS;
	}
	
	public String getCodTipoOS() {
		return codTipoOS;
	}
	
	public void setCodTipoOS(String codTipoOS) {
		this.codTipoOS = codTipoOS;
	}
	
	public String toString() {
		String retorno = "Dados enviados OS: "
				+ "Filial: "+this.filial+",\r\n"
				+ "Local: "+this.local+",\r\n"
				+ "CodigoOS: "+this.codOS+",\r\n"
				+ "ChassiVeiculo: "+this.chassiVeiculo+",\r\n"
				+ "CodigoMarca: "+this.codMarca+",\r\n"
				+ "TipoAtendimento: "+this.tipoAtendimento+",\r\n"
				+ "Proprietario: "+this.proprietario+",\r\n"
				+ "LojaProprietario: "+this.lojaProprietario+",\r\n"
				+ "Observacao: "+this.observacao+",\r\n"
				+ "DataInclusaoOS: "+this.dataInclusaoOS+",\r\n"
				+ "DataEntregaVeiculo: "+this.dataEntregaVeiculo+",\r\n"
				+ "CodigoStatusOs: "+this.codStatusOS+",\r\n"
				+ "Ativo: "+this.ativo+"";
		return retorno;
	}
	
}
