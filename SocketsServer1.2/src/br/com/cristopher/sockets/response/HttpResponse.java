package br.com.cristopher.sockets.response;

import java.io.DataOutputStream;
import java.io.IOException;

import br.com.cristopher.sockets.data.Node;

public class HttpResponse {
	public static void sendDefault(DataOutputStream output, Node node) throws IOException {

		output.writeBytes("Version: " + node.getVersao() + "\n");
		output.writeBytes("Creation: " + node.getCriado().getTime() + "\n");
		output.writeBytes("Modification: " + node.getModificado().getTime() + "\n");
	}

	public static void send200(DataOutputStream output) throws IOException {

		output.writeBytes("HTTP/1.1 200 OK\n");
	}
	
	public static void send204(DataOutputStream output, Node node) throws IOException {

		output.writeBytes("HTTP/1.1 204 No Content\n");
		sendDefault(output, node);
		output.writeBytes("Content-length: 0\n");
	}

	public static void sendContent(DataOutputStream output, byte[] conteudo) throws IOException {

		output.writeBytes("Content-length: " + conteudo.length + "\n");
		output.writeBytes("\n");
		output.write(conteudo);
		output.writeBytes("\n");
	}
	
	public static void send404(DataOutputStream output) throws IOException {
		output.writeBytes("HTTP/1.1 404 Not Found\n");
	}
	
	public static void send500(DataOutputStream output) throws IOException {
		output.writeBytes("HTTP/1.1 500 InternalServerError\n");
	}
	
}
