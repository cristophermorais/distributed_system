package br.com.cristopher.sockets.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import br.com.cristopher.sockets.data.Node;
import br.com.cristopher.sockets.data.NodeDao;
import br.com.cristopher.sockets.data.Request;
import br.com.cristopher.sockets.response.HttpResponse;
import br.com.cristopher.sockets.utils.Logger;
import sockets.server.core.Retorno;

public class TrataCliente {
	Request req;
	Node node;

	private Logger log = new Logger(true);

	public TrataCliente(String request, ByteBuffer content) {
		req = HttpTranslator.translate(request, content);
	}

	public Retorno run() {
		try {
			
			node = NodeDao.get(req.getPath());
						
			switch (req.getType()) {

			case "GET":
				return trataGet();
			case "POST":
				return trataPost();
			case "PUT":
				return trataPut();
			case "DELETE":
				return trataDelete();
			case "HEAD":
				return trataHead();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private synchronized Retorno trataGet() throws IOException {
		Retorno retorno = new Retorno();
		log.infoLog("Tratando requisição GET: "+Arrays.toString(this.req.getPath()));
		if (node == null)
			retorno.setStatus(HttpResponse.send404());
		else {
			if (node.getConteudo() == null)
				retorno.setStatus(HttpResponse.send204(node));
			else {
				retorno.setStatus(HttpResponse.send200());
				retorno.setCriado(node.getCriado().getTime());
				retorno.setModificado(node.getModificado().getTime());
				retorno.setVersao(node.getVersao());
				retorno.setConteudo(node.getConteudo());
			}
		}
		return retorno;
	}
	
	private synchronized Retorno trataPost() throws IOException {
		Retorno retorno = new Retorno();
		log.infoLog("Tratando requisição POST: "+Arrays.toString(this.req.getPath()));
		if (node == null) {
			node = NodeDao.post(req);

			if (node != null) {
				retorno.setStatus(HttpResponse.send200());
				retorno.setCriado(node.getCriado().getTime());
				retorno.setModificado(node.getModificado().getTime());
				retorno.setVersao(node.getVersao());
			} else {
				retorno.setStatus(HttpResponse.send500());
			}

		} else {
			retorno.setStatus(HttpResponse.send500());
		}
		return retorno;
	}
	
	private synchronized Retorno trataPut() throws IOException {
		Retorno retorno = new Retorno();
		log.infoLog("Tratando requisição PUT: "+Arrays.toString(this.req.getPath()));
		if (node != null) {
			node = NodeDao.put(req);

			if (node != null) {
				retorno.setStatus(HttpResponse.send200());
				retorno.setCriado(node.getCriado().getTime());
				retorno.setModificado(node.getModificado().getTime());
				retorno.setVersao(node.getVersao());
			} else {
				retorno.setStatus(HttpResponse.send500());
			}

		} else {
			retorno.setStatus(HttpResponse.send500());
		}
		return retorno;
	}
	
	private synchronized Retorno trataDelete() throws IOException {
		Retorno retorno = new Retorno();
		log.infoLog("Tratando requisição DELETE: "+Arrays.toString(this.req.getPath()));
		if (node != null) {
			boolean ok = NodeDao.delete(req);

			if (ok) {
				retorno.setStatus(HttpResponse.send200());
				retorno.setCriado(node.getCriado().getTime());
				retorno.setModificado(node.getModificado().getTime());
				retorno.setVersao(node.getVersao());
			} else {
				retorno.setStatus(HttpResponse.send500());
			}

		} else {
			retorno.setStatus(HttpResponse.send500());
		}
		return retorno;
	}
	
	private synchronized Retorno trataHead() throws IOException {
		Retorno retorno = new Retorno();
		if (node != null) {
			if (node.getConteudo() != null) {
				retorno.setStatus(HttpResponse.send200());
				retorno.setCriado(node.getCriado().getTime());
				retorno.setModificado(node.getModificado().getTime());
				retorno.setVersao(node.getVersao());
				retorno.setContent_length(node.getContentLength());
			} else {
				retorno.setStatus(HttpResponse.send204(node));
				retorno.setCriado(node.getCriado().getTime());
				retorno.setModificado(node.getModificado().getTime());
				retorno.setVersao(node.getVersao());
				retorno.setContent_length(0);
			}
		} else {
			retorno.setStatus(HttpResponse.send404());
		}
		return retorno;
	}
}