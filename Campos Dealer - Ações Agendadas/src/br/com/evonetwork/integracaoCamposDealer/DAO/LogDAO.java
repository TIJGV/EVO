package br.com.evonetwork.integracaoCamposDealer.DAO;

import java.sql.Timestamp;

import com.sankhya.util.TimeUtils;

import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class LogDAO {
	public static void criarLog(String origem, String log)
			throws Exception {
		JapeSession.SessionHandle hnd = null;
		Timestamp dtatual = TimeUtils.getNow();
		try {
			hnd = JapeSession.open();
			hnd.setCanTimeout(false);
			hnd.setPriorityLevel(JapeSession.LOW_PRIORITY);// em casos de deadlock, esta sess o cai
			JapeWrapper empresaDAO = JapeFactory.dao("AD_LOGCAMPOSDEALER");
			empresaDAO.create()
			.set("LOG", log.toCharArray())
			.set("ORIGEM", origem)
			.set("DHLOG", dtatual)
			.save();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Erro ao gerar log: " + e.getMessage());
		} finally {
			JapeSession.close(hnd);
		}
	}
}
