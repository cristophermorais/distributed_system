package br.com.cristopher.sockets.data;

public class Request {
	private byte[] conteudo;
	private final String type;
	private int contentLength;
	private String[] path;
	
	public Request(String type) {
		this.type = type;
	}
	public Request(String type, String[] path) {
		this.type = type;
		this.path = path;
	}
	public byte[] getConteudo() {
		return conteudo;
	}
	public void setConteudo(byte[] conteudo) {
		this.conteudo = conteudo;
	}
	public String getType() {
		return type;
	}
	public int getContentLength() {
		return contentLength;
	}
	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}
	public String[] getPath() {
		return path;
	}
	public void setPath(String[] path) {
		this.path = path;
	}
}
