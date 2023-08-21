package br.com.evonetwork.integracaoAPISimova.Model;

public class Funcionario {
	
	private String filial = null;
	private String crachaFuncionario = null;
	private String nome = null;
	private String flagTipoFuncionario = null;
	private String seqEquipe = null;
	private String ativo = null;
	private String celular = null;
	private String local = null;
	private String dataDemissao = null;
	private String dataRetornoFerias = null;
	private String flagFerias = null;
	
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}
	public String getDataRetornoFerias() {
		return dataRetornoFerias;
	}
	public void setDataRetornoFerias(String dataRetornoFerias) {
		this.dataRetornoFerias = dataRetornoFerias;
	}
	public String getFlagFerias() {
		return flagFerias;
	}
	public void setFlagFerias(String flagFerias) {
		this.flagFerias = flagFerias;
	}
	public void setSeqEquipe(String seqEquipe) {
		this.seqEquipe = seqEquipe;
	}
	public String getSeqEquipe() {
		return seqEquipe;
	}
	public String getFilial() {
		return filial;
	}
	public void setFilial(String filial) {
		this.filial = filial;
	}
	public String getCrachaFuncionario() {
		return crachaFuncionario;
	}
	public void setCrachaFuncionario(String crachaFuncionario) {
		this.crachaFuncionario = crachaFuncionario;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getAtivo() {
		return ativo;
	}
	public void setAtivo(String ativo) {
		this.ativo = ativo;
	}
	public String getCelular() {
		return celular;
	}
	public void setCelular(String celular) {
		this.celular = celular;
	}
	public String getFlagTipoFuncionario() {
		return flagTipoFuncionario;
	}
	public void setFlagTipoFuncionario(String flagTipoFuncionario) {
		this.flagTipoFuncionario = flagTipoFuncionario;
	}
	public String getDataDemissao() {
		return dataDemissao;
	}
	public void setDataDemissao(String dataDemissao) {
		this.dataDemissao = dataDemissao;
	}
}
