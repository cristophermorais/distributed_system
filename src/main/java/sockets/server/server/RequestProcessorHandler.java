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
		Node node;
		String auxPath;
		Retorno retorno = new Retorno();
		Request req = HttpTranslator.translate(stringRequest, content);
		String url = req.getAbsolutePath();
		int nServer = Math.abs(url.hashCode()) % Server.servidores.length;

		if (Server.port == Server.servidores[nServer]) {
			// trata internamente

			String[] path = req.getPath();
			TrataCliente trtCli;

			switch (req.getType().toUpperCase()) {
			case "GET":
				trtCli = new TrataCliente(req);
				retorno = trtCli.run();
				// r.setStatus("deu certo");
				// return r;
				break;

			case "POST":
				String parentPath;
				node = NodeDao.get(req.getAbsolutePath());

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

					//se não for o ultimo elemento do Path[] cria uma requisicao JUST_POST
					if (i < path.length - 1) {
						String aux = "JUST_POST " + auxPath + " HTTP/1.1" + "\nContent-Length: 0";
						int respServer = Math.abs(auxPath.hashCode()) % Server.servidores.length;

						// se for o responsavel trata o JUST_POST
						if (Server.port == Server.servidores[respServer]) {
							Request auxReq = HttpTranslator.translate(aux, null);
							trtCli = new TrataCliente(auxReq);
							trtCli.run();
						} else {
							//se não for o responsavel encaminha o JUST_POST
							Retorno rPost1 = requestSend(Server.servidores[respServer], aux, null);
							
							//se JUST_POST deu errado retorna erro
							if(rPost1.getStatusCode() != 200){
								retorno.setStatus(HttpResponse.send500());
								return retorno;
							}else{
								//se JUST_POST deu certo, agora atualiza a lista de filhos do pai
								
								//se parentPath != nul entao precisa autalizar a lista de filhos
								if(parentPath != null){
									String child = "POST_CHILD " + parentPath + " HTTP/1.1"
											+"\nChild-Path: " +  auxPath
												+ "\nContent-Length: 0";
									respServer = Server.responseServer(parentPath);
									
									//se o servidor for responsavel trata requisicao POST_CHILD
									if(Server.port == Server.servidores[respServer]){
										Request childReq = HttpTranslator.translate(child, null);
										trtCli = new TrataCliente(childReq);
										rPost1 = trtCli.run();
										
										if(rPost1.getStatusCode() != 200){
											retorno.setStatus(HttpResponse.send500());
											return retorno;
										}
										
									}else{
										//se nao for responsavel envia requisicao pro responssavel
										rPost1 = requestSend(Server.servidores[respServer], child, null);
										
										if(rPost1.getStatusCode() != 200){
											retorno.setStatus(HttpResponse.send500());
											return retorno;
										}
										
									}
								}
							}
							
							
						}
					} else {
						//se for o ultimo elemento trata a requisicao POST
						String child = "POST_CHILD " + parentPath + " HTTP/1.1"
								+"\nChild-Path: " +  auxPath
									+ "\nContent-Length: 0";
						int respServer = Server.responseServer(parentPath);
						Retorno rPost2;
						
						//se for responsavel pelo POST_CHILD trata aqui
						if(Server.port == Server.servidores[respServer]){
							Request childReq = HttpTranslator.translate(child, null);
							trtCli = new TrataCliente(childReq);
							rPost2 = trtCli.run();
						}else{
							rPost2 = requestSend(Server.servidores[respServer], child, null);
						}
						
						//se resposta do POST_CHILD for diferente de 200 aborta
						if(rPost2.getStatusCode() != 200){
							retorno.setStatus(HttpResponse.send500());
							return retorno;
						}else{
							trtCli = new TrataCliente(req);
							retorno = trtCli.run();
							return retorno;
						}
					}
				}
				break;
				
			case "DELETE":
				node = NodeDao.get(req.getAbsolutePath());

				if (node == null) {
					retorno = new Retorno();
					retorno.setStatus(HttpResponse.send404());
					return retorno;
				}

				List<String> filhos = node.getListFilhos();

				if (filhos.isEmpty()) {
					trtCli = new TrataCliente(req);
					retorno = trtCli.run();
				} else {
					for (String filho : filhos) {
						int respServer = Math.abs(filho.hashCode()) % Server.servidores.length;
						String aux = "DELETE " + filho + " HTTP/1.1" + "\nContent-Length: 0";
						Retorno rDel = requestSend(Server.servidores[respServer], aux, null);
					}
					
					trtCli = new TrataCliente(req);
					retorno = trtCli.run();
				}
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
