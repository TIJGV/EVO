package br.com.evonetwork.integracaoAPISimova.Model;

public class Autenticacao {
	
		private String user = null;
		private String url = null;
		private String urlAcesso = null;
		private String pass = null;
		private String empresa = null;
		private String token = null;
		
		public Autenticacao(){}

		public String getUser() {
			return user;
		}

		public void setUser(String user) {
			this.user = user;
		}

		public String getPass() {
			return pass;
		}

		public void setPass(String pass) {
			this.pass = pass;
		}

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public String getEmpresa() {
			return empresa;
		}

		public void setEmpresa(String empresa) {
			this.empresa = empresa;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getUrlAcesso() {
			return urlAcesso;
		}

		public void setUrlAcesso(String urlAcesso) {
			this.urlAcesso = urlAcesso;
		}
}
