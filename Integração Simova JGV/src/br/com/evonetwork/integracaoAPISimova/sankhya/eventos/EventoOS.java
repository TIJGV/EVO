package br.com.evonetwork.integracaoAPISimova.sankhya.eventos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;

import br.com.evonetwork.integracaoAPISimova.Controller.Controller;
import br.com.evonetwork.integracaoAPISimova.Model.Autenticacao;
import br.com.evonetwork.integracaoAPISimova.Model.OS;
import br.com.evonetwork.integracaoAPISimova.Model.RetornoCadastro;
import br.com.evonetwork.integracaoAPISimova.Model.Token;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.DynamicEntityNames;

public class EventoOS implements EventoProgramavelJava{

	@Override
	public void afterInsert(PersistenceEvent event) throws Exception {
		criarAtualizarOS(event);
	}
	
	@Override
	public void afterUpdate(PersistenceEvent event) throws Exception {
		criarAtualizarOS(event);
	}
	
	private void criarAtualizarOS(PersistenceEvent event) {
		System.out.println("***Iniciando integração SIMOVA - OS***");
		try {
			String completarURL = "OS";
			
			DynamicVO OSsVO = (DynamicVO) event.getVo();
			
			String filial = Controller.getFilial(OSsVO.asBigDecimal("CODEMP"));
			
			if("".equals(filial)) {
				System.out.println("Filial não encontrada! Não é possível integrar!");
				return;
			}
			
			OS OS = setOS(OSsVO, filial);
			
			Autenticacao auth = Controller.getParametrosSankhya(completarURL, filial);
			
			URL url = new URL(auth.getUrl());
			URL urlOS = new URL(auth.getUrlAcesso());
			
			System.out.println("Autenticando...");
			Token token = Controller.getToken(auth, url);
			
			System.out.println("Enviando OS ao Simova...");
			String idSimova = criarOSSimova(token, urlOS, OS);
			
			atualizarIdIntegracao(idSimova, OSsVO);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("***Fim integração SIMOVA - OS***");
	}

	private void atualizarIdIntegracao(String idSimova, DynamicVO oSsVO) {
		SessionHandle hnd = null;
		BigDecimal numOs = oSsVO.asBigDecimal("NUMOS");
		System.out.println("Atualizando OS "+numOs+" com IDINTEGRACAO "+idSimova+"...");
		try {
			hnd = JapeSession.open();

			JapeFactory.dao("TCSOSE").
			prepareToUpdate(oSsVO)
				.set("IDINTEGRACAO", new BigDecimal(idSimova))
				.update();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JapeSession.close(hnd);
		}
		System.out.println("Atualizado.");
	}

	private String criarOSSimova(Token token, URL urlOS, OS OS) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) urlOS.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("authorization", token.getToken());
		
		StringBuilder body = new StringBuilder();
		
		body.append("[\r\n");
		body.append("{\r\n"
				+ "\"Filial\": \""+OS.getFilial()+"\",\r\n"
				+ "\"Local\": \""+OS.getLocal()+"\",\r\n"
				+ "\"CodigoOS\": \""+OS.getCodOS()+"\",\r\n"
				+ "\"ChassiVeiculo\": \""+OS.getChassiVeiculo()+"\",\r\n"
				+ "\"CodigoMarca\": \""+OS.getCodMarca()+"\",\r\n"
				+ "\"TipoAtendimento\": \""+OS.getTipoAtendimento()+"\",\r\n"
				+ "\"Proprietario\": \""+OS.getProprietario()+"\",\r\n"
				+ "\"LojaProprietario\": \""+OS.getLojaProprietario()+"\",\r\n"
				+ "\"Observacao\": \""+OS.getObservacao()+"\",\r\n"
				+ "\"DataInclusaoOS\": \""+OS.getDataInclusaoOS()+"\",\r\n"
				+ "\"DataEntregaVeiculo\": \""+OS.getDataEntregaVeiculo()+"\",\r\n"
				+ "\"CodigoStatusOs\": \""+OS.getCodStatusOS()+"\",\r\n"
				+ "\"Ativo\": \""+OS.getAtivo()+"\"\r\n"
				+ "}\r\n");
		body.append("]");
		
		String data = body.toString();
        
        System.out.println("BODY: "+data);
        
        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = conn.getOutputStream();
        stream.write(out);
        
        String resposta = getResponseBody(conn);
        
        RetornoCadastro[] retornoOSs = new Gson().fromJson(resposta, RetornoCadastro[].class); 
        
        System.out.println("Resposta HTTP: "+conn.getResponseCode()+" "+conn.getResponseMessage());
        
    	System.out.println("Retorno: "+retornoOSs[0].getMsg());
        System.out.println("ID OS: "+retornoOSs[0].getId());
        
        conn.disconnect();
        
        return retornoOSs[0].getId();
	}

	private OS setOS(DynamicVO OSsVO, String filial) {
		OS OS = new OS();
		
		//AD_TCSOSE
		
		OS.setFilial(filial);
		OS.setLocal(filial);
		OS.setCodOS(((BigDecimal) OSsVO.getProperty("NUMOS"))+"");
		OS.setChassiVeiculo(getChassiVeiculo((BigDecimal) OSsVO.getProperty("CODVEICULO")));
		OS.setCodMarca(getMarcaVeiculo((BigDecimal) OSsVO.getProperty("CODVEICULO")));
		OS.setTipoAtendimento(getTipoAtendimento((String) OSsVO.getProperty("TIPOATENDIMENTO")));
		OS.setProprietario(((BigDecimal) OSsVO.getProperty("CODPARC"))+"");
		OS.setLojaProprietario(((BigDecimal) OSsVO.getProperty("CODPARC"))+"");
		OS.setObservacao((String) OSsVO.getProperty("DESCRSERV"));
		
		if((Timestamp) OSsVO.getProperty("DHABERTURA") == null)
			OS.setDataInclusaoOS("");
		else
			OS.setDataInclusaoOS(convertDate(((Timestamp) OSsVO.getProperty("DHABERTURA")+"")));
		
		if((Timestamp) OSsVO.getProperty("DHPREVISTA") == null)
			OS.setDataEntregaVeiculo("");
		else
			OS.setDataEntregaVeiculo(convertDate(((Timestamp) OSsVO.getProperty("DHPREVISTA")+"")));
		
		OS.setCodStatusOS(getStatusOS((String) OSsVO.getProperty("STATUS")));
		OS.setAtivo("1");
		
		return OS;
	}
	
	private String getStatusOS(String status) {
		String retorno = "";
		if(status == null)
			return "1";
		switch (status) {
		case "F":
			retorno = "3";
			break;
		case "AV":
			retorno = "1";
			break;
		case "A":
			retorno = "1";
			break;
		}
		return retorno;
	}

	private String convertDate(String mDate) {
		System.out.println("MDATE: "+mDate);
		if(mDate == null || mDate == "" || "null".equals(mDate))
			return "";
		
	   SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
	   try {
	          Date newDate = inputFormat.parse(mDate);
	          inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	          mDate = inputFormat.format(newDate);
	    } catch (ParseException e) {
	          e.printStackTrace();
	    }

	   return mDate;
	}

	private String getChassiVeiculo(BigDecimal codVeiculo) {
		JapeSession.SessionHandle hnd = null;
		String chassi = "";
		try {
			hnd = JapeSession.open();
			JapeWrapper veiculoDAO = JapeFactory.dao(DynamicEntityNames.VEICULO);
			DynamicVO veiculo = veiculoDAO.findByPK(codVeiculo);
			chassi = veiculo.asString("CHASSIS");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JapeSession.close(hnd);
		}
		return chassi;
	}

	private String getMarcaVeiculo(BigDecimal codVeiculo) {
		JapeSession.SessionHandle hnd = null;
		String codMarca = "";
		try {
			hnd = JapeSession.open();
			JapeWrapper veiculoDAO = JapeFactory.dao(DynamicEntityNames.VEICULO);
			DynamicVO veiculo = veiculoDAO.findByPK(codVeiculo);
			if(veiculo.asBigDecimal("AD_NROUNICOMODELO") != null)
				codMarca = veiculo.asBigDecimal("AD_NROUNICOMODELO").toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JapeSession.close(hnd);
		}
		return codMarca;
	}

	private String getTipoAtendimento(String tipoAtendimento) {
		String retorno = "";
		
		if(tipoAtendimento == null)
			return "0";
		
		switch(tipoAtendimento) {
			case("P"):
				retorno = "0";
				break;
			case("T"):
				retorno = "1";
				break;
		}
		return retorno;
	}

	private static String getResponseBody(HttpURLConnection conn) {
		BufferedReader br = null;
        StringBuilder body = null;
        String line = "";
        try {
            br = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            body = new StringBuilder();
            while ((line = br.readLine()) != null)
                body.append(line);
            return body.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}

	@Override
	public void afterDelete(PersistenceEvent event) throws Exception {}
	@Override
	public void beforeCommit(TransactionContext ctx) throws Exception {}
	@Override
	public void beforeDelete(PersistenceEvent event) throws Exception {}
	@Override
	public void beforeInsert(PersistenceEvent event) throws Exception {}
	@Override
	public void beforeUpdate(PersistenceEvent event) throws Exception {}
}