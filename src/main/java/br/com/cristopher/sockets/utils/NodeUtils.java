package br.com.cristopher.sockets.utils;

public class NodeUtils {
	
	private static final String[] verbos = { "GET", "POST", "PUT", "DELETE", "HEAD" };
	
	public static String[] getRequested(String requested) {
		String[] aux = requested.split("/");
		if (requested.startsWith("/")) {
			if (aux.length == 0) {
				aux = new String[1];
			}
			aux[0] = "/";
		} else {

		}
		return aux;
	}
	

	public static boolean inVerbos(String type) {

		for (String aux : verbos) {
			if (type.equalsIgnoreCase(aux)) {
				return true;
			}
		}
		return false;
	}
}
