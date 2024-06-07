package br.com.evonetwork.integracaoCamposDealer.Model;

import java.math.BigDecimal;

public class CarteiraCliente {
	private int idCarteiraCliente;
	private int idCliente;
	private String CodigoCliente;//prospect
	private int idUsuario;
	private String codUsuario; //vendedor
	private String dthRegistro;

	private BigDecimal codCarteira;

	public int getIdCarteiraCliente() {
		return idCarteiraCliente;
	}

	public void setIdCarteiraCliente(int idCarteiraCliente) {
		this.idCarteiraCliente = idCarteiraCliente;
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

	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
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

	public BigDecimal getCodCarteira() {
		return codCarteira;
	}

	public void setCodCarteira(BigDecimal codCarteira) {
		this.codCarteira = codCarteira;
	}

}
