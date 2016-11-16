package br.com.cristopher.sockets.server;

import java.nio.ByteBuffer;
import java.util.Date;

import br.com.cristopher.sockets.data.Request;
import br.com.cristopher.sockets.utils.Logger;
import br.com.cristopher.sockets.utils.NodeUtils;

public class HttpTranslator {

	private static Logger log = new Logger(true);

	public static Request translate(String request, ByteBuffer content) {
		log.infoLog("Traduzindo requisição ... " + new Date());
		Request req = null;
		int contentLength = -1;
		String[] lines = request.split("\n");
		for(String line : lines){
			String[] aux = line.split(" ");
			if (NodeUtils.inVerbos(aux[0])) {
				req = new Request(aux[0], NodeUtils.getRequested(aux[1]));
				req.setDeleteChild(false);
			} else {
				if (line.isEmpty())
					break;
				else {
					if (aux[0].startsWith("Content-Length:")) {
						try {
							contentLength = Integer.parseInt(aux[1]);
						} catch (NumberFormatException e) {
	
						}
					}
					
					if (aux[0].startsWith("Child-Path:")) {
						req.setChildPath(aux[1]);
					}
					
					if(aux[0].startsWith("Delete-Parent-Child:")){
						req.setDeleteChild(true);
					}
				}
			}
		}

		if (req.getType().equalsIgnoreCase("POST") || req.getType().equalsIgnoreCase("PUT")) {

			if (contentLength < 0 || contentLength > 1024) {
				log.errorLog("Tamanho do conteúdo fora dos padrões determinados(0 < tamanho < 1024): " + contentLength);
				return null;
			}
			try {
				req.setContentLength(contentLength);
				req.setConteudo(content);
			} catch (Exception e) {
				log.errorLog(e.getMessage());
			}
		}
		
		return req;
	}
}
