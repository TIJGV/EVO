package br.com.evonetwork.integracaoCamposDealer.Model;

public class ClientesAlterados {
	
	private int idCRMRetorno;
	private int idCRMRetornoTipo;
	private String dscCRMRetornoTipo;
	private String endPoint;
	private String idCamposDealer;
	private String CodigoIntegracao;
	private String idEmpresa;
	private String fStatus;
	private String dthRegistro; //"2023-09-28T09:27:51.89"
	private String Message;
	
	public int getIdCRMRetorno() {
		return idCRMRetorno;
	}
	
	public void setIdCRMRetorno(int idCRMRetorno) {
		this.idCRMRetorno = idCRMRetorno;
	}
	
	public int getIdCRMRetornoTipo() {
		return idCRMRetornoTipo;
	}
	
	public void setIdCRMRetornoTipo(int idCRMRetornoTipo) {
		this.idCRMRetornoTipo = idCRMRetornoTipo;
	}
	
	public String getDscCRMRetornoTipo() {
		return dscCRMRetornoTipo;
	}
	
	public void setDscCRMRetornoTipo(String dscCRMRetornoTipo) {
		this.dscCRMRetornoTipo = dscCRMRetornoTipo;
	}
	
	public String getEndPoint() {
		return endPoint;
	}
	
	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}
	
	public String getIdCamposDealer() {
		return idCamposDealer;
	}
	
	public void setIdCamposDealer(String idCamposDealer) {
		this.idCamposDealer = idCamposDealer;
	}
	
	public String getCodigoIntegracao() {
		return CodigoIntegracao;
	}
	
	public void setCodigoIntegracao(String codigoIntegracao) {
		CodigoIntegracao = codigoIntegracao;
	}
	
	public String getIdEmpresa() {
		return idEmpresa;
	}
	
	public void setIdEmpresa(String idEmpresa) {
		this.idEmpresa = idEmpresa;
	}
	
	public String getfStatus() {
		return fStatus;
	}
	
	public void setfStatus(String fStatus) {
		this.fStatus = fStatus;
	}
	
	public String getDthRegistro() {
		return dthRegistro;
	}
	
	public void setDthRegistro(String dthRegistro) {
		this.dthRegistro = dthRegistro;
	}

	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}

}
