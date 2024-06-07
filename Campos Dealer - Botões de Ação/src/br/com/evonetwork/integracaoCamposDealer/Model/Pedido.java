package br.com.evonetwork.integracaoCamposDealer.Model;

public class Pedido {
	private String idPedido;
	private String codPedido;
	private String dthPedido;
	private String dthValidade;
	private String IncEstadualCadProd;
	private String enderecoFaturamento;
	private String enderecoEntrega;
	private int fFrete;
	private int fAceiteTermo;
	private int fSituacaoPedido;
	private int fOrcamento;
	private String obsPedido;
	private String emailCliente;
	private Double vlrPedido;
	private String dthAbe;

	public String getIdPedido() {
		return idPedido;
	}

	public void setIdPedido(String idPedido) {
		this.idPedido = idPedido;
	}

	public String getCodPedido() {
		return codPedido;
	}

	public void setCodPedido(String codPedido) {
		this.codPedido = codPedido;
	}

	public String getDthPedido() {
		return dthPedido;
	}

	public void setDthPedido(String dthPedido) {
		this.dthPedido = dthPedido;
	}

	public String getDthValidade() {
		return dthValidade;
	}

	public void setDthValidade(String dthValidade) {
		this.dthValidade = dthValidade;
	}

	public String getIncEstadualCadProd() {
		return IncEstadualCadProd;
	}

	public void setIncEstadualCadProd(String incEstadualCadProd) {
		IncEstadualCadProd = incEstadualCadProd;
	}

	public String getEnderecoFaturamento() {
		return enderecoFaturamento;
	}

	public void setEnderecoFaturamento(String enderecoFaturamento) {
		this.enderecoFaturamento = enderecoFaturamento;
	}

	public String getEnderecoEntrega() {
		return enderecoEntrega;
	}

	public void setEnderecoEntrega(String enderecoEntrega) {
		this.enderecoEntrega = enderecoEntrega;
	}

	public int getfFrete() {
		return fFrete;
	}

	public void setfFrete(int fFrete) {
		this.fFrete = fFrete;
	}

	public int getfAceiteTermo() {
		return fAceiteTermo;
	}

	public void setfAceiteTermo(int fAceiteTermo) {
		this.fAceiteTermo = fAceiteTermo;
	}

	public int getfSituacaoPedido() {
		return fSituacaoPedido;
	}

	public void setfSituacaoPedido(int fSituacaoPedido) {
		this.fSituacaoPedido = fSituacaoPedido;
	}

	public int getfOrcamento() {
		return fOrcamento;
	}

	public void setfOrcamento(int fOrcamento) {
		this.fOrcamento = fOrcamento;
	}

	public String getObsPedido() {
		return obsPedido;
	}

	public void setObsPedido(String obsPedido) {
		this.obsPedido = obsPedido;
	}

	public String getEmailCliente() {
		return emailCliente;
	}

	public void setEmailCliente(String emailCliente) {
		this.emailCliente = emailCliente;
	}

	public Double getVlrPedido() {
		return vlrPedido;
	}

	public void setVlrPedido(Double vlrPedido) {
		this.vlrPedido = vlrPedido;
	}

	public String getDthAbe() {
		return dthAbe;
	}

	public void setDthAbe(String dthAbe) {
		this.dthAbe = dthAbe;
	}

}
