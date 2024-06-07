package br.com.evonetwork.integracaoCamposDealer.Model;

import java.math.BigDecimal;

public class LstResponsaveis {

	private BigDecimal idCarteiraCliente;
	private BigDecimal idCliente;
	private String CodigoCliente;
	private BigDecimal idUsuario;
	private String codUsuario;
	private String dthRegistro;

	public BigDecimal getIdCarteiraCliente() {
		return idCarteiraCliente;
	}

	public void setIdCarteiraCliente(BigDecimal idCarteiraCliente) {
		this.idCarteiraCliente = idCarteiraCliente;
	}

	public BigDecimal getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(BigDecimal idCliente) {
		this.idCliente = idCliente;
	}

	public String getCodigoCliente() {
		return CodigoCliente;
	}

	public void setCodigoCliente(String codigoCliente) {
		CodigoCliente = codigoCliente;
	}

	public BigDecimal getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(BigDecimal idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getCodUsuario() {
		return codUsuario;
	}

	public void setCodUsuario(String codUsuario) {
		this.codUsuario = codUsuario;
	}

	public String getDthRegistro() {
		return dthRegistro;
	}

	public void setDthRegistro(String dthRegistro) {
		this.dthRegistro = dthRegistro;
	}

}
