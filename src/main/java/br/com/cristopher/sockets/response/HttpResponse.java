package br.com.cristopher.sockets.response;

import java.io.IOException;

import br.com.cristopher.sockets.data.Node;

public class HttpResponse {
	
	public static String send200() throws IOException {
		return ("HTTP/1.1 200 OK\n");
	}
	
	public static String send204(Node node) throws IOException {
		return ("HTTP/1.1 204 No Content\n");
	}
	
	public static String send404() throws IOException {
		return "HTTP/1.1 404 Not Found\n";
	}
	
	public static String send500() throws IOException {
		return "HTTP/1.1 500 InternalServerError\n";
	}
	
}
