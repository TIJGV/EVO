package br.com.evonetwork.importarProdutos.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sankhya.util.TimeUtils;

import br.com.evonetwork.importarProdutos.Model.FileInformation;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.wrapper.JapeFactory;

public class Utils {

	public static void preencherUsuarioEDataImprotacao(Registro linha, ContextoAcao ca) throws Exception {
		SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
//			hnd.setCanTimeout(false); //instrução para evitar o Timeout
			hnd.execWithTX( new JapeSession.TXBlock(){
				public void doWithTx() throws Exception{
					SessionHandle hnd = null;
					try {
						hnd = JapeSession.open();
						JapeFactory.dao("AD_IMPPRO").prepareToUpdateByPK(linha.getCampo("NROUNICO"))
							.set("CODUSU", (Object) ca.getUsuarioLogado())
							.set("DTIMPORTACAO", (Object) TimeUtils.getNow())
							.update();
					} catch (Exception e) {
						e.printStackTrace();
						throw new Exception(e.getMessage());
					} finally {
						JapeSession.close(hnd);
					}
				}
			});
		} finally {
			JapeSession.close(hnd); // fechamento da sessão
		}
		
	}

	public static FileInformation extrairJson(String contentInfo) throws JsonParseException, JsonMappingException, IOException {
        String patternString = "__start_fileinformation__(.*?)__end_fileinformation__";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(contentInfo);
        if (matcher.find()) {
            String extractedString = matcher.group(1);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(extractedString, FileInformation.class);
        }
        return null;
    }

	public static void removerCabecalho(BigDecimal cabecalho, Sheet sheet) {
		for (int cabec = cabecalho.intValue(), i = 0; i < cabec; ++i) {
			Row row = sheet.getRow(i);
			if (row != null) {
				sheet.removeRow(row);
			}
		}
	}

	public static String removerCaracteres(String remover, String conteudoString) {
		if(remover != null) {
			for(int k = 0; k < remover.length(); k++)
				conteudoString = conteudoString.replace(remover.charAt(k)+"", "");
		}
		return conteudoString;
	}

	public static InputStream lerArquivo(InputStream inputStream) throws Exception {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            StringBuffer buf = new StringBuffer();
            InputStream in = inputStream;
            byte[] b = new byte[2048];
            int length;
            boolean hasFileInfo = false;
            boolean writeDirectly = false;
            int offset = 0;
            while ((length = in.read(b)) > 0) {
                if (writeDirectly) {
                    baos.write(b, 0, length);
                } else {
                    offset = buf.length();
                    buf.append(new String(b));
                    if (!hasFileInfo && "__start_fileinformation__".equals(buf.substring(0, 25))) {
                        hasFileInfo = true;
                    }

                    if (hasFileInfo) {
                        int i = buf.indexOf("__end_fileinformation__");
                        if (i > -1) {
                            i += 23;// tamanho do "__end_fileinformation__"
                            i -= offset; // O quanto ja havia sido lido antes
                            baos.write(b, i, length - i);
                            writeDirectly = true;
                        }
                    } else {
                        baos.write(b, 0, length);
                        writeDirectly = true;
                    }
                }
            }
            inputStream = new ByteArrayInputStream(baos.toByteArray());
            baos.flush();
            in.close();
        } catch(Exception e) {
        	e.printStackTrace();
        	throw new Exception(e.getMessage());
        }
        return inputStream;
    }

	public static String removerCabecalhoDoArquivo(String line) {
		return line = line.substring(line.indexOf("__end_fileinformation__")+23, line.length());
	}

    public static String removerEspacoEmBranco(String valor) {
        if (valor == null){
            return "";
        } else {
            return valor.replace(" ", "");
        }
    }

    public static String removerZerosAEsquerda(String valor) {
        if (valor == null){
            return "";
        } else {
            char[] charArray = valor.toCharArray();
            for (char c : charArray) {
				if(c == '0')
					valor = valor.replaceFirst("0", "");
				else
					break;
			}
        }
        return valor;
    }

	public static String adicionarCasasDecimais(int casasDecimais, String dado) {
		return dado.substring(0, dado.length()-(casasDecimais))+"."+dado.substring(dado.length()-(casasDecimais));
	}

}
