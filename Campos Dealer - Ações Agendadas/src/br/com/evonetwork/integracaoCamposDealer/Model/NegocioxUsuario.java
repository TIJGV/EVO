package br.com.evonetwork.integracaoCamposDealer.Model;

public class NegocioxUsuario {
	private int idCRMNegocioXUsuario;
	private String idNegocio;
	private String codNegocio;
	private int idUsuario;
	private String codUsuario;
	private String dthRegistro;

	public int getIdCRMNegocioXUsuario() {
		return idCRMNegocioXUsuario;
	}

	public void setIdCRMNegocioXUsuario(int idCRMNegocioXUsuario) {
		this.idCRMNegocioXUsuario = idCRMNegocioXUsuario;
	}

	public String getIdNegocio() {
		return idNegocio;
	}

	public void setIdNegocio(String idNegocio) {
		this.idNegocio = idNegocio;
	}

	public String getCodNegocio() {
		return codNegocio;
	}

	public void setCodNegocio(String codNegocio) {
		this.codNegocio = codNegocio;
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

}
