package br.com.cristopher.sockets.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;

import br.com.cristopher.sockets.data.Node;
import br.com.cristopher.sockets.data.NodeDao;
import br.com.cristopher.sockets.data.Request;
import br.com.cristopher.sockets.response.HttpResponse;
import br.com.cristopher.sockets.utils.Logger;

public class TrataCliente implements Runnable {
	private Socket cliente;
	DataOutputStream output = null;
	Request req;
	Node node;

	private Logger log = new Logger(true);

	public TrataCliente(Socket client) throws IOException {
		log.infoLog("Recebendo conexão de "+client.getInetAddress().getHostAddress()+" as "+new Date());
		this.cliente = client;
		output = new DataOutputStream(cliente.getOutputStream());
		req = HttpTranslator.translate(cliente.getInputStream());
		(new Thread(this)).start();
	}

	@Override
	public void run() {
		try {
			
			node = NodeDao.get(req.getPath());
						
			switch (req.getType()) {

			case "GET":
				trataGet();
				break;

			case "POST":
				trataPost();				
				break;

			case "PUT":
				trataPut();
				break;
			case "DELETE":
				trataDelete();
				break;
			case "HEAD":
				trataHead();
				break;
			default:
				break;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			
			try {
				cliente.close();
				log.separatorLog();
			} catch (IOException e) {
				log.errorLog("Falha ao fechar socket de "+cliente.getInetAddress().getHostAddress()+": "+e.getMessage());
				log.separatorLog();
			}
		}
	}
	
	private synchronized void trataGet() throws IOException {
		log.infoLog("Tratando requisição GET: "+Arrays.toString(this.req.getPath()));
		if (node == null) HttpResponse.send404(output);
		else {
			if (node.getConteudo() == null)
				HttpResponse.send204(output, node);
			else {
				HttpResponse.send200(output);
				HttpResponse.sendDefault(output, node);
				HttpResponse.sendContent(output, node.getConteudo());
			}
		}
	}
	
	private synchronized void trataPost() throws IOException {
		log.infoLog("Tratando requisição POST: "+Arrays.toString(this.req.getPath()));
		if (node == null) {
			node = NodeDao.post(req);

			if (node != null) {
				HttpResponse.send200(output);
				HttpResponse.sendDefault(output, node);
			} else {
				HttpResponse.send500(output);
			}

		} else {
			HttpResponse.send500(output);
		}
	}
	
	private synchronized void trataPut() throws IOException {
		log.infoLog("Tratando requisição PUT: "+Arrays.toString(this.req.getPath()));
		if (node != null) {
			node = NodeDao.put(req);

			if (node != null) {
				HttpResponse.send200(output);
				HttpResponse.sendDefault(output, node);
				;
			} else {
				HttpResponse.send500(output);
			}

		} else {
			HttpResponse.send404(output);
		}
	}
	
	private synchronized void trataDelete() throws IOException {
		log.infoLog("Tratando requisição DELETE: "+Arrays.toString(this.req.getPath()));
		if (node != null) {
			boolean ok = NodeDao.delete(req);

			if (ok) {
				HttpResponse.send200(output);
				HttpResponse.sendDefault(output, node);
			} else {
				HttpResponse.send500(output);
			}

		} else {
			HttpResponse.send404(output);
		}

	}
	
	private synchronized void trataHead() throws IOException {
		if (node != null) {
			if (node.getConteudo() != null) {
				HttpResponse.send200(output);
				HttpResponse.sendDefault(output, node);
				output.writeBytes("Content-length: " + node.getConteudo().length + "\r\n");
				output.writeBytes("\r\n");
			} else {
				HttpResponse.send204(output, node);
			}
		} else {
			HttpResponse.send404(output);
		}
	}
}