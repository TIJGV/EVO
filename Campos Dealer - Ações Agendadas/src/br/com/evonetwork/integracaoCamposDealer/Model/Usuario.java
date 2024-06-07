package br.com.evonetwork.integracaoCamposDealer.Model;

public class Usuario {
	private int idUsuario;
	private String codUsuario;
	private String nomeUsuario; 
	private String CPF;
	private String login;
	private String senhaUsuario;
	private String TipoUsuario;
	private String dscTipoUsuario;
	private String codEmpresa;
	private String Telefone;
	private String Email;
	private String idLicenca;
	private String dthRegistro;
	private String cracha;
	private int fEditaOcr;
	private int fOSE;
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
	public String getNomeUsuario() {
		return nomeUsuario;
	}
	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}
	public String getCPF() {
		return CPF;
	}
	public void setCPF(String cPF) {
		CPF = cPF;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getSenhaUsuario() {
		return senhaUsuario;
	}
	public void setSenhaUsuario(String senhaUsuario) {
		this.senhaUsuario = senhaUsuario;
	}
	public String getTipoUsuario() {
		return TipoUsuario;
	}
	public void setTipoUsuario(String tipoUsuario) {
		TipoUsuario = tipoUsuario;
	}
	public String getDscTipoUsuario() {
		return dscTipoUsuario;
	}
	public void setDscTipoUsuario(String dscTipoUsuario) {
		this.dscTipoUsuario = dscTipoUsuario;
	}
	public String getCodEmpresa() {
		return codEmpresa;
	}
	public void setCodEmpresa(String codEmpresa) {
		this.codEmpresa = codEmpresa;
	}
	public String getTelefone() {
		return Telefone;
	}
	public void setTelefone(String telefone) {
		Telefone = telefone;
	}
	public String getEmail() {
		return Email;
	}
	public void setEmail(String email) {
		Email = email;
	}
	public String getIdLicenca() {
		return idLicenca;
	}
	public void setIdLicenca(String idLicenca) {
		this.idLicenca = idLicenca;
	}
	public String getDthRegistro() {
		return dthRegistro;
	}
	public void setDthRegistro(String dthRegistro) {
		this.dthRegistro = dthRegistro;
	}
	public String getCracha() {
		return cracha;
	}
	public void setCracha(String cracha) {
		this.cracha = cracha;
	}
	public int getfEditaOcr() {
		return fEditaOcr;
	}
	public void setfEditaOcr(int fEditaOcr) {
		this.fEditaOcr = fEditaOcr;
	}
	public int getfOSE() {
		return fOSE;
	}
	public void setfOSE(int fOSE) {
		this.fOSE = fOSE;
	}
	
	@Override
	public String toString() {
		return "codUsuario: " + this.codUsuario + ", idUsuario: " + this.idUsuario;
	}
	
}
