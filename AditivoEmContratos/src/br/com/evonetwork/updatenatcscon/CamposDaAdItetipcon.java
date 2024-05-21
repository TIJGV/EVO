package br.com.evonetwork.updatenatcscon;

public class CamposDaAdItetipcon {
	private String campoalter;
	private String alterarpara;
	private String tipocampo;
	
	public String getCampoalter() {
		return campoalter;
	}
	public void setCampoalter(String campoalter) {
		this.campoalter = campoalter;
	}
	public String getAlterarpara() {
		return alterarpara;
	}
	public void setAlterarpara(String alterarpara) {
		this.alterarpara = alterarpara;
	}
	public String getTipocampo() {
		return tipocampo;
	}
	public void setTipocampo(String tipocampo) {
		this.tipocampo = tipocampo;
	}
	@Override
	public String toString() {
		return "\nCAMPOALTER: " + campoalter +
				"\nALTERARPARA: " + alterarpara +
				"\nTIPOCAMPO: " + tipocampo;
	}
}
