package sockets.server.server;

import java.nio.ByteBuffer;

import br.com.cristopher.sockets.data.Node;
import br.com.cristopher.sockets.data.NodeDao;
import br.com.cristopher.sockets.data.Request;
import br.com.cristopher.sockets.response.HttpResponse;
import br.com.cristopher.sockets.server.HttpTranslator;
import br.com.cristopher.sockets.server.TrataCliente;
import sockets.server.core.RequestProcessor;
import sockets.server.core.Retorno;

public class RequestProcessorHandler implements RequestProcessor.Iface {

	public int ping(int port) {
		Server.log.infoClient("Ping Recebido de " + port);
		return 3;
	}

	public Retorno request(String stringRequest, ByteBuffer content) {
		Retorno retorno = null;
		Request req = HttpTranslator.translate(stringRequest, content);
		String url = req.getAbsolutePath();
		int nServer = Math.abs(url.hashCode()) % Server.servidores.length;

		if (Server.port == Server.servidores[nServer]) {
			// trata internamente

			String[] path = req.getPath();
			String absPath = req.getAbsolutePath();
			TrataCliente trtCli;

			switch (req.getType().toUpperCase()) {
			case "GET":
				trtCli = new TrataCliente(req);
				retorno = trtCli.run();
				// r.setStatus("deu certo");
				// return r;
				break;

			case "POST":
				Node node = NodeDao.get(req.getAbsolutePath());
				
				if(node != null){
					retorno = new Retorno();
					retorno.setStatus(HttpResponse.send400());
					return retorno;
				}
				
				String auxPath = "";

				for (int i = 0; i < path.length; i++) {
					if (i == 0) {
						auxPath = "/";
					} else {
						if (auxPath.endsWith("/")) {
							auxPath += path[i];
						} else {
							auxPath += "/" + path[i];
						}
					}

					if (i < path.length - 1) {
						String aux = "JUST_POST " + auxPath + " HTTP/1.1" + "\nContent-Length: 0";
						int respons = Math.abs(auxPath.hashCode()) % Server.servidores.length;

						if (Server.port == Server.servidores[respons]) {
							Request auxReq = HttpTranslator.translate(aux, null);
							trtCli = new TrataCliente(auxReq);
							retorno = trtCli.run();
						} else {
							retorno = requestSend(Server.servidores[respons], aux, null);
						}
					} else {
						trtCli = new TrataCliente(req);
						retorno = trtCli.run();
					}
				}

				break;
			case "DELETE":
				retorno = null;
				break;
			case "PUT":
				trtCli = new TrataCliente(req);
				retorno = trtCli.run();
				break;
			case "HEAD":
				trtCli = new TrataCliente(req);
				retorno = trtCli.run();
				break;
			case "JUST_DELETE":
				retorno = null;
				break;
			case "JUST_POST":
				trtCli = new TrataCliente(req);
				retorno = trtCli.run();
				break;
			default:
				retorno = null;
				break;
			}

		} else {
			// encaminha para o servidor Server.servidores[nServer]
			retorno = requestSend(Server.servidores[nServer], stringRequest, content);
		}

		return retorno;
	}

	private Retorno requestSend(int port, String stringRequest, ByteBuffer content) {
		RequestSender rs = new RequestSender(port, stringRequest, content);
		return rs.send();
	}
}
