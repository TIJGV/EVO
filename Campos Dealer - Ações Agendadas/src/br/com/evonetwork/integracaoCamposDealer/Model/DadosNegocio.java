package br.com.evonetwork.integracaoCamposDealer.Model;

import java.util.ArrayList;

public class DadosNegocio {
	private String idNegocio;
	private Double vlrNegocio;
	private int probabilidade;
	private String codEtapaNegocio;
	private String obsNegocio;
	private int idCliente;
	private String CodigoCliente;
	private String codUsuario;
	private String dthPrimeiroContato;
	private String dthFechamento;
	private int fConclusaoNegocio;
	private String codMotivoPerdaNegocio;
	private String obsMotivoPerda;
	private int fWeb;
	private String dthRegistro;
	private String codMotivoGanhoNegocio;
	private String obsMotivoGanho;
	private String codTipoNegocio;
	private String codNegocio;
	private int prioridade;
	private ArrayList<Acao> lstAcoes;
	private Pedido pedidoOrcamento;
	private ArrayList<NegocioXProduto> lstNegocioXProdutos;
	private ArrayList<NegocioxUsuario> lstNegocioXUsuarios;
	private String dthAbe;

	public String getIdNegocio() {
		return idNegocio;
	}

	public void setIdNegocio(String idNegocio) {
		this.idNegocio = idNegocio;
	}

	public Double getVlrNegocio() {
		return vlrNegocio;
	}

	public void setVlrNegocio(Double vlrNegocio) {
		this.vlrNegocio = vlrNegocio;
	}

	public int getProbabilidade() {
		return probabilidade;
	}

	public void setProbabilidade(int probabilidade) {
		this.probabilidade = probabilidade;
	}

	public String getCodEtapaNegocio() {
		return codEtapaNegocio;
	}

	public void setCodEtapaNegocio(String codEtapaNegocio) {
		this.codEtapaNegocio = codEtapaNegocio;
	}

	public String getObsNegocio() {
		return obsNegocio;
	}

	public void setObsNegocio(String obsNegocio) {
		this.obsNegocio = obsNegocio;
	}

	public int getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(int idCliente) {
		this.idCliente = idCliente;
	}

	public String getCodigoCliente() {
		return CodigoCliente;
	}

	public void setCodigoCliente(String codigoCliente) {
		CodigoCliente = codigoCliente;
	}

	public String getCodUsuario() {
		return codUsuario;
	}

	public void setCodUsuario(String codUsuario) {
		this.codUsuario = codUsuario;
	}

	public String getDthPrimeiroContato() {
		return dthPrimeiroContato;
	}

	public void setDthPrimeiroContato(String dthPrimeiroContato) {
		this.dthPrimeiroContato = dthPrimeiroContato;
	}

	public String getDthFechamento() {
		return dthFechamento;
	}

	public void setDthFechamento(String dthFechamento) {
		this.dthFechamento = dthFechamento;
	}

	public int getfConclusaoNegocio() {
		return fConclusaoNegocio;
	}

	public void setfConclusaoNegocio(int fConclusaoNegocio) {
		this.fConclusaoNegocio = fConclusaoNegocio;
	}

	public String getCodMotivoPerdaNegocio() {
		return codMotivoPerdaNegocio;
	}

	public void setCodMotivoPerdaNegocio(String codMotivoPerdaNegocio) {
		this.codMotivoPerdaNegocio = codMotivoPerdaNegocio;
	}

	public String getObsMotivoPerda() {
		return obsMotivoPerda;
	}

	public void setObsMotivoPerda(String obsMotivoPerda) {
		this.obsMotivoPerda = obsMotivoPerda;
	}

	public int getfWeb() {
		return fWeb;
	}

	public void setfWeb(int fWeb) {
		this.fWeb = fWeb;
	}

	public String getDthRegistro() {
		return dthRegistro;
	}

	public void setDthRegistro(String dthRegistro) {
		this.dthRegistro = dthRegistro;
	}

	public String getCodMotivoGanhoNegocio() {
		return codMotivoGanhoNegocio;
	}

	public void setCodMotivoGanhoNegocio(String codMotivoGanhoNegocio) {
		this.codMotivoGanhoNegocio = codMotivoGanhoNegocio;
	}

	public String getObsMotivoGanho() {
		return obsMotivoGanho;
	}

	public void setObsMotivoGanho(String obsMotivoGanho) {
		this.obsMotivoGanho = obsMotivoGanho;
	}

	public String getCodTipoNegocio() {
		return codTipoNegocio;
	}

	public void setCodTipoNegocio(String codTipoNegocio) {
		this.codTipoNegocio = codTipoNegocio;
	}

	public String getCodNegocio() {
		return codNegocio;
	}

	public void setCodNegocio(String codNegocio) {
		this.codNegocio = codNegocio;
	}

	public int getPrioridade() {
		return prioridade;
	}

	public void setPrioridade(int prioridade) {
		this.prioridade = prioridade;
	}

	public ArrayList<Acao> getLstAcoes() {
		return lstAcoes;
	}

	public void setLstAcoes(ArrayList<Acao> lstAcoes) {
		this.lstAcoes = lstAcoes;
	}

	public Pedido getPedidoOrcamento() {
		return pedidoOrcamento;
	}

	public void setPedidoOrcamento(Pedido pedidoOrcamento) {
		this.pedidoOrcamento = pedidoOrcamento;
	}

	public ArrayList<NegocioXProduto> getLstNegocioXProdutos() {
		return lstNegocioXProdutos;
	}

	public void setLstNegocioXProdutos(ArrayList<NegocioXProduto> lstNegocioXProdutos) {
		this.lstNegocioXProdutos = lstNegocioXProdutos;
	}

	public ArrayList<NegocioxUsuario> getLstNegocioXUsuarios() {
		return lstNegocioXUsuarios;
	}

	public void setLstNegocioXUsuarios(ArrayList<NegocioxUsuario> lstNegocioXUsuarios) {
		this.lstNegocioXUsuarios = lstNegocioXUsuarios;
	}

	public String getDthAbe() {
		return dthAbe;
	}

	public void setDthAbe(String dthAbe) {
		this.dthAbe = dthAbe;
	}

}
