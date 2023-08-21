package br.com.evonetwork.integracaoAPISimova.Model;

public class ModeloEquipamento {
	
	private String ativo = null;
	private String codModeloVeiculo = null;
	private String descModeloVeiculo = null;
	private String codTipoVeiculo = null;
	
	public String getAtivo() {
		return ativo;
	}
	public void setAtivo(String ativo) {
		this.ativo = ativo;
	}
	public String getCodModeloVeiculo() {
		return codModeloVeiculo;
	}
	public void setCodModeloVeiculo(String codModeloVeiculo) {
		this.codModeloVeiculo = codModeloVeiculo;
	}
	public String getDescModeloVeiculo() {
		return descModeloVeiculo;
	}
	public void setDescModeloVeiculo(String descModeloVeiculo) {
		this.descModeloVeiculo = descModeloVeiculo;
	}
	public String getCodTipoVeiculo() {
		return codTipoVeiculo;
	}
	public void setCodTipoVeiculo(String codTipoVeiculo) {
		this.codTipoVeiculo = codTipoVeiculo;
	}
	
}
