package br.com.evonetwork.updatenatcscon;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class UpdateNaTcscon {
	
	public void updateNaTcscon(BigDecimal numContrato, String campoAlter, String alteracao, String tipoCampo) throws Exception {
		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper servicoExecutadoDAO = JapeFactory.dao("Contrato");
			DynamicVO servico = servicoExecutadoDAO.findOne("NUMCONTRATO = " + numContrato);
			if(tipoCampo.equals("BigDecimal")) {
				String alteracao2;
				if (alteracao.contains(",")) {
					alteracao2 = alteracao.replace(",", ".");
				}else {
					alteracao2 = alteracao;
				}
				BigDecimal alteracao3 = new BigDecimal(alteracao2.trim());
	
				servicoExecutadoDAO.prepareToUpdate(servico)
				.set(campoAlter, alteracao3)
				.update();
				return;
			}
			if(tipoCampo.equals("String")) {
			
				servicoExecutadoDAO.prepareToUpdate(servico)
				.set(campoAlter, alteracao.trim())
				.update();
				return;
			}
			if(tipoCampo.equals("Data")) {
			
				servicoExecutadoDAO.prepareToUpdate(servico)
				.set(campoAlter, stringToTimestamp(alteracao.trim()))
				.update();
				return;
			}
			else {
				throw new Exception("Campo Inválido!!!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}

	public static Timestamp stringToTimestamp(String data) throws Exception {
		Timestamp timestamp = null;
		try {
		    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		    Date parsedDate = dateFormat.parse(data);
		    timestamp = new java.sql.Timestamp(parsedDate.getTime());
		} catch(Exception e) {
		    e.printStackTrace();
		    throw new Exception(e.getMessage());
		}
		return timestamp;
	}
}
