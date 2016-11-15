package br.com.cristopher.sockets.data;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import br.com.cristopher.sockets.utils.Logger;

public class NodeDao {
	private static HashMap<String, Node> dados = new HashMap<>();
	private static Logger log = new Logger(true);

	public static Node get(String absolutePath) {
		log.infoLog("Obtendo informação: " + absolutePath);
		return dados.get(absolutePath);
	}

	public static Node post(Request request) {
		log.infoLog("Salvando nó: " + request.getAbsolutePath());

		Node node = dados.get(request.getAbsolutePath());

		if (node == null) {
			node = new Node(request.getAbsolutePath(), request.getConteudo());
			dados.put(request.getAbsolutePath(), node);
			return node;
		} else {
			return null;
		}
	}

	public static Node justPost(Request request) {
		log.infoLog("Salvando nó: " + request.getAbsolutePath());

		Node node = dados.get(request.getAbsolutePath());

		if (node == null) {
			node = new Node(request.getAbsolutePath(), request.getConteudo());
			dados.put(request.getAbsolutePath(), node);
			return node;
		} else {
			return node;
		}
	}

	public static Node put(Request req) {
		log.infoLog("Atualizando nó: " + Arrays.toString(req.getPath()));
		Node node = dados.get(req.getAbsolutePath());

		if (node != null) {
			node.setVersao(node.getVersao() + 1);
			node.setModificado(new Date());
			node.setConteudo(req.getConteudo());
			return node;
		}

		return null;
	}

	public static boolean delete(Request req) {
		log.infoLog("Deletando nó: " + req.getAbsolutePath());

		Node node = dados.get(req.getAbsolutePath());

		if (node == null)
			return false;

		dados.remove(req.getAbsolutePath());
		return true;
	}

	public static boolean addChild(Request req) {
		Node node = dados.get(req.getAbsolutePath());
		
		if (node != null) {
			node.add(req.getChildPath());
			return true;
		}
		
		return false;
	}
}