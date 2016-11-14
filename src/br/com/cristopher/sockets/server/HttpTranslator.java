package br.com.cristopher.sockets.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import br.com.cristopher.sockets.data.Request;
import br.com.cristopher.sockets.utils.Logger;
import br.com.cristopher.sockets.utils.NodeUtils;

public class HttpTranslator {

	private static Logger log = new Logger(true);
	@SuppressWarnings("deprecation")
	public static Request translate(InputStream inputStream) throws IOException {
		log.infoLog("Traduzindo requisição ... " + new Date());
		DataInputStream input = new DataInputStream(inputStream);
		Request req = null;
		int contentLength = -1;
		int cont = 0;

		while (true) {
			if (input.available() > 0) {
				String line = input.readLine();
				String[] aux = line.split(" ");
				if (NodeUtils.inVerbos(aux[0])) {
					req = new Request(aux[0], NodeUtils.getRequested(aux[1]));
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
					}
				}
			}
		}

		if (req.getType().equalsIgnoreCase("POST") || req.getType().equalsIgnoreCase("PUT")) {

			if (contentLength < 0 || contentLength > 1024) {
				log.errorLog("Tamanho do conteúdo fora dos padrões determinados(0 < tamanho < 1024): "+contentLength);
				return null;
			}
			try {
				req.setContentLength(contentLength);
				byte[] conteudo = new byte[contentLength];
				while (cont < contentLength) {
					if (input.available() > 0) {
						conteudo[cont++] = input.readByte();
					}
				}
				req.setConteudo(conteudo);
			} catch (Exception e) {
				log.errorLog(e.getMessage());
			}
		}
		return req;
	}
}
