package br.com.cristopher.sockets.data;

import java.nio.ByteBuffer;

public class Request {
	private ByteBuffer conteudo;
	private final String type;
	private int contentLength;
	private int version;
	private String[] path;
	private String childPath; //somente para requisicoes POST_CHILD
	private boolean deleteChild; //somente para DELETE do n� no n�vel mais alto (endereco da requisicao)
	
	public String getAbsolutePath(){
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < path.length; i++){
			if(i > 1){
				sb.append("/");
			}
			
			sb.append(path[i]);
		}
		
		return sb.toString();
	}
	
	public static String getAbsolutePath(String[] arrayPath){
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < arrayPath.length; i++){
			if(i > 1){
				sb.append("/");
			}
			
			sb.append(arrayPath[i]);
		}
		
		return sb.toString();
	}
	
	public Request(String type) {
		this.type = type;
	}
	public Request(String type, String[] path) {
		this.type = type;
		this.path = path;
	}
	
	public int getVersion(){
		return version;
	}
	public void setVersion(int version){
		this.version = version;
	}
	public boolean getDeleteChild(){
		return deleteChild;
	}
	public void setDeleteChild(boolean deleteChild){
		this.deleteChild = deleteChild;
	}
	public String getChildPath(){
		return childPath;
	}
	public void setChildPath(String childPath){
		this.childPath = childPath;
	}
	public ByteBuffer getConteudo() {
		return conteudo;
	}
	public void setConteudo(ByteBuffer conteudo) {
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
