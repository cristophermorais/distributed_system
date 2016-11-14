package br.com.cristopher.sockets.data;

import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

import br.com.cristopher.sockets.utils.Logger;

public class NodeDao {
	private static Node root = null;
	private static Logger log = new Logger(true);

	public static Node get(String[] req) {
		log.infoLog("Obtendo informação: "+Arrays.toString(req));
		if (root == null) return null;
		if (req.length == 1 && req[0].equals("/")) return root;
		
		boolean encontrado = false;
		Node aux = root;
		Vector<Node> nodes; 
		for (String nodePath : req) {
			if (!nodePath.equals("/")) {
				nodes = aux.getNodes();
				encontrado = false;
				for (Node node : nodes) {
					if (node.getPath().equals(nodePath)) {
						aux = node;
						encontrado = true;
						break;
					}
				}
				if (!encontrado) return null;
			}
		}
		if (!encontrado) return null;
		log.infoLog("Informação encontrada: "+Arrays.toString(req));
		return aux;
	}

	public static Node post(Request req) {
		log.infoLog("Salvando nó: "+Arrays.toString(req.getPath()));
		Node node = null;
		Node nodePai = null;
		String[] path = req.getPath();
		if (path.length == 1 && path[0].equals("/")) {
			root = new Node(path[0], null, req.getConteudo());
			return root;
		}
		for (int i = 0; i < path.length; i++) {
			if (root == null) root = new Node(path[0]);
			node = NodeDao.get(Arrays.copyOfRange(path, 0, i+1));
			if (node == null) node = new Node(path[i], nodePai);
			if (i == (path.length - 1)) node.setConteudo(req.getConteudo());
			nodePai = node;
		}
		return node;
	}

	public static Node put(Request req) {
		log.infoLog("Atualizando nó: "+Arrays.toString(req.getPath()));
		Node node = NodeDao.get(req.getPath());

		if (node != null) {
			node.setVersao(node.getVersao() + 1);
			node.setModificado(new Date());
			node.setConteudo(req.getConteudo());
			return node;
		}

		return null;
	}

	public static boolean delete(Request req) {
		log.infoLog("Deletando nó: "+Arrays.toString(req.getPath()));
		Node node = NodeDao.get(req.getPath());
		if (node.getPath().equals("/")) {
			root = null;
		} else {
			return node.getPai().getNodes().remove(node);
		}
		return true;
	}
}