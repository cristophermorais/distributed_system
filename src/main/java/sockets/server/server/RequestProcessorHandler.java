package sockets.server.server;

import java.nio.ByteBuffer;
import java.util.List;

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
		Retorno retorno = new Retorno();
		Request req = HttpTranslator.translate(stringRequest, content);
		String url = req.getAbsolutePath();
		int nServer = Math.abs(url.hashCode()) % Server.servidores.length;

		if (Server.port == Server.servidores[nServer]) {
			// trata internamente

			TrataCliente trtCli;

			switch (req.getType().toUpperCase()) {
			case "GET":
				retorno = get(req);
				break;

			case "POST":
				retorno = post(req);
				break;

			case "DELETE":
				retorno = delete(req);
				break;

			case "LIST":
				retorno = list(req);
				break;

			case "PUT":
				trtCli = new TrataCliente(req);
				retorno = trtCli.run();
				break;
			case "HEAD":
				trtCli = new TrataCliente(req);
				retorno = trtCli.run();
				break;
			case "JUST_POST":
				trtCli = new TrataCliente(req);
				retorno = trtCli.run();
				break;
			case "POST_CHILD":
				trtCli = new TrataCliente(req);
				retorno = trtCli.run();
				break;
			case "DELETE_CHILD":
				trtCli = new TrataCliente(req);
				retorno = trtCli.run();
				break;
			default:
				retorno.setStatus(HttpResponse.send500());
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

	public Retorno get(Request req) {
		Retorno retorno = new Retorno();
		TrataCliente trtCli = new TrataCliente(req);
		retorno = trtCli.run();
		return retorno;
	}

	public Retorno post(Request req) {
		Retorno retorno = new Retorno();
		Node node;
		String auxPath;
		String parentPath;
		TrataCliente trtCli;
		node = NodeDao.get(req.getAbsolutePath());
		String[] path = req.getPath();

		if (node != null) {
			retorno = new Retorno();
			retorno.setStatus(HttpResponse.send400());
			return retorno;
		}

		auxPath = "";

		for (int i = 0; i < path.length; i++) {
			if (i == 0) {
				auxPath = "/";
				parentPath = null;
			} else {
				parentPath = auxPath;

				if (auxPath.endsWith("/")) {
					auxPath += path[i];
				} else {
					auxPath += "/" + path[i];
				}
			}

			// se não for o ultimo elemento do Path[] cria uma
			// requisicao JUST_POST
			if (i < path.length - 1) {
				String aux = "JUST_POST " + auxPath + " HTTP/1.1" + "\nContent-Length: 0";
				int respServer = Math.abs(auxPath.hashCode()) % Server.servidores.length;

				// se for o responsavel trata o JUST_POST
				if (Server.port == Server.servidores[respServer]) {
					Request auxReq = HttpTranslator.translate(aux, null);
					trtCli = new TrataCliente(auxReq);
					trtCli.run();
				} else {
					// se não for o responsavel encaminha o JUST_POST
					Retorno rPost1 = requestSend(Server.servidores[respServer], aux, null);

					// se JUST_POST deu errado retorna erro
					if (rPost1.getStatusCode() != 200) {
						retorno.setStatus(HttpResponse.send500());
						return retorno;
					} else {
						// se JUST_POST deu certo, agora atualiza a
						// lista de filhos do pai

						// se parentPath != nul entao precisa autalizar
						// a lista de filhos
						if (parentPath != null) {
							String child = "POST_CHILD " + parentPath + " HTTP/1.1" + "\nChild-Path: " + auxPath
									+ "\nContent-Length: 0";
							respServer = Server.responseServer(parentPath);

							// se o servidor for responsavel trata
							// requisicao POST_CHILD
							if (Server.port == Server.servidores[respServer]) {
								Request childReq = HttpTranslator.translate(child, null);
								trtCli = new TrataCliente(childReq);
								rPost1 = trtCli.run();

								if (rPost1.getStatusCode() != 200) {
									retorno.setStatus(HttpResponse.send500());
									return retorno;
								}

							} else {
								// se nao for responsavel envia
								// requisicao pro responssavel
								rPost1 = requestSend(Server.servidores[respServer], child, null);

								if (rPost1.getStatusCode() != 200) {
									retorno.setStatus(HttpResponse.send500());
									return retorno;
								}

							}
						}
					}

				}
			} else {
				// se for o ultimo elemento trata a requisicao POST

				// se NAO for raiz faz o POST_CHILD
				if (parentPath != null) {
					String child = "POST_CHILD " + parentPath + " HTTP/1.1" + "\nChild-Path: " + auxPath
							+ "\nContent-Length: 0";
					int respServer = Server.responseServer(parentPath);
					Retorno rPost2;

					// se for responsavel pelo POST_CHILD trata aqui
					if (Server.port == Server.servidores[respServer]) {
						Request childReq = HttpTranslator.translate(child, null);
						trtCli = new TrataCliente(childReq);
						rPost2 = trtCli.run();
					} else {
						rPost2 = requestSend(Server.servidores[respServer], child, null);
					}

					if (rPost2.getStatusCode() != 200) {
						retorno.setStatus(HttpResponse.send500());
						return retorno;
					}
				}

				trtCli = new TrataCliente(req);
				retorno = trtCli.run();
				return retorno;
			}
		}
		return retorno;
	}

	public Retorno delete(Request req) {
		Retorno retorno = new Retorno();
		List<String> filhos;
		TrataCliente trtCli;
		Node node;
		node = NodeDao.get(req.getAbsolutePath());

		if (node == null) {
			retorno = new Retorno();
			retorno.setStatus(HttpResponse.send404());
			return retorno;
		}

		filhos = node.getListFilhos();

		if (filhos.isEmpty()) {
			trtCli = new TrataCliente(req);
			retorno = trtCli.run();
		} else {
			for (String filho : filhos) {
				int respServer = Math.abs(filho.hashCode()) % Server.servidores.length;
				String aux = "DELETE " + filho + " HTTP/1.1" + "\nContent-Length: 0";
				Retorno rDel;

				if (Server.port == Server.servidores[respServer]) {
					Request delReq = HttpTranslator.translate(aux, null);
					trtCli = new TrataCliente(delReq);
					rDel = trtCli.run();
				} else {
					rDel = requestSend(Server.servidores[respServer], aux, null);
				}

				if (rDel.getStatusCode() != 200) {
					retorno.setStatus(HttpResponse.send500());
					return retorno;
				}
			}

			trtCli = new TrataCliente(req);
			retorno = trtCli.run();

		}

		// se nao for a raiz requisita DELETE_CHILD para o pai
		if (!node.getPath().equals("/") && req.getDeleteChild()) {
			String[] split = node.getPath().split("/");
			String parentPath = "";

			if (split.length > 2) {
				for (int i = 1; i < (split.length - 1); i++) {
					parentPath += "/" + split[i];
				}
			} else if(split.length == 2) {
				parentPath = "/";
			}

			String auxStrReq = "DELETE_CHILD " + parentPath + " HTTP/1.1" + "\nChild-Path: " + node.getPath()
					+ "\nContent-Length: 0";

			int respServer = Server.responseServer(parentPath);

			Retorno rDel;

			// se for o responsavel pelo parentPath trata requisicao, senao
			// encaminha
			if (Server.port == Server.servidores[respServer]) {
				Request auxReq = HttpTranslator.translate(auxStrReq, null);
				trtCli = new TrataCliente(auxReq);
				rDel = trtCli.run();
			} else {
				rDel = requestSend(Server.servidores[respServer], auxStrReq, null);
			}

			if (rDel.getStatusCode() != 200) {
				retorno.setStatus(HttpResponse.send500());
				return retorno;
			}
		}

		return retorno;
	}

	public Retorno list(Request req) {
		Retorno retorno = new Retorno();
		List<String> filhos;
		Node node;
		node = NodeDao.get(req.getAbsolutePath());

		if (node == null) {
			retorno.setStatus(HttpResponse.send404());
		} else {
			StringBuilder sb = new StringBuilder();
			filhos = node.getListFilhos();

			for (String filho : filhos) {
				sb.append(filho + "\n");
			}

			String stringCont = sb.toString();
			retorno.setContent_length(stringCont.length());
			retorno.setConteudo(stringCont.getBytes());
			retorno.setStatus(HttpResponse.send200());
		}
		return retorno;
	}
}
