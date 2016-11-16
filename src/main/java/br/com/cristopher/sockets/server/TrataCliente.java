package br.com.cristopher.sockets.server;

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

	public TrataCliente(Request req) {
		this.req = req;
	}

	public Retorno run() {

		node = NodeDao.get(req.getAbsolutePath());

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
		case "JUST_POST":
			return trataJustPost();
		case "POST_CHILD":
			return trataPostChild();
		case "DELETE_CHILD":
			return trataDeleteChild();

		}

		return null;
	}
	
	private synchronized Retorno trataDeleteChild(){
		Retorno retorno = new Retorno();
		log.infoLog("Tratando requisição DELETE_CHILD: " + Arrays.toString(this.req.getPath()));
		
		if(node != null){
			if(NodeDao.deleteChild(req)){
				retorno.setStatus(HttpResponse.send200());
			}else{
				retorno.setStatus(HttpResponse.send500());
			}
		}else{
			retorno.setStatus(HttpResponse.send500());
		}
		
		return retorno;
	}

	private synchronized Retorno trataPostChild() {
		Retorno retorno = new Retorno();
		log.infoLog("Tratando requisição POST_CHILD: " + Arrays.toString(this.req.getPath()));

		if (node != null) {
			NodeDao.addChild(req);
			retorno.setStatus(HttpResponse.send200());
		} else {
			retorno.setStatus(HttpResponse.send500());
		}
		
		return retorno;
	}

	private synchronized Retorno trataJustPost() {
		Retorno retorno = new Retorno();
		log.infoLog("Tratando requisição JUST_POST: " + Arrays.toString(this.req.getPath()));

		if (node == null) {
			node = NodeDao.justPost(req);

			if (node != null) {
				retorno.setStatus(HttpResponse.send200());
				retorno.setCriado(node.getCriado().getTime());
				retorno.setModificado(node.getModificado().getTime());
				retorno.setVersao(node.getVersao());
			} else {
				retorno.setStatus(HttpResponse.send500());
			}

		} else {
			retorno.setStatus(HttpResponse.send200());
		}
		return retorno;
	}

	private synchronized Retorno trataGet() {
		Retorno retorno = new Retorno();
		log.infoLog("Tratando requisição GET: " + Arrays.toString(this.req.getPath()));
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

	private synchronized Retorno trataPost() {
		Retorno retorno = new Retorno();
		log.infoLog("Tratando requisição POST: " + Arrays.toString(this.req.getPath()));
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
			retorno.setStatus(HttpResponse.send400());
		}
		return retorno;
	}

	private synchronized Retorno trataPut() {
		Retorno retorno = new Retorno();
		log.infoLog("Tratando requisição PUT: " + Arrays.toString(this.req.getPath()));
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
			retorno.setStatus(HttpResponse.send404());
		}
		return retorno;
	}

	private synchronized Retorno trataDelete() {
		Retorno retorno = new Retorno();
		log.infoLog("Tratando requisição DELETE: " + Arrays.toString(this.req.getPath()));
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
			retorno.setStatus(HttpResponse.send404());
		}
		return retorno;
	}

	private synchronized Retorno trataHead() {
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