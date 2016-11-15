package br.com.cristopher.sockets.response;

import br.com.cristopher.sockets.data.Node;

public class HttpResponse {
	
	public static String send200() {
		return ("HTTP/1.1 200 OK\n");
	}
	
	public static String send204(Node node) {
		return ("HTTP/1.1 204 No Content\n");
	}
	
	public static String send400() {
		return ("HTTP/1.1 400 Bad Request\n");
	}
	
	public static String send404() {
		return "HTTP/1.1 404 Not Found\n";
	}
	
	public static String send500() {
		return "HTTP/1.1 500 InternalServerError\n";
	}
	
}
