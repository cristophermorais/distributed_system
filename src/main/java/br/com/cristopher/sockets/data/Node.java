package br.com.cristopher.sockets.data;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

public class Node {
	private final Date criado;
	private final String path;
	private Date modificado;
	private int versao = 0;
	private Vector<String> filhos;
	private ByteBuffer conteudo;
		
	
	public Node(String path) {
		modificado = new Date();
		criado = new Date();
		this.path = path;
		this.conteudo = null;
		this.filhos = new Vector<>();
	}
	
	public Node(String path, ByteBuffer conteudo) {
		modificado = new Date();
		criado = new Date();
		this.path = path;
		this.filhos = new Vector<>();
		this.conteudo = conteudo;
	}
	
	public ArrayList<String> getListFilhos(){
		ArrayList<String> lista = new ArrayList<>(this.filhos);
		return lista;
	}

	public int getContentLength() {
		return conteudo.array().length;
	}

	public Date getCriado() {
		return criado;
	}


	public Date getModificado() {
		return modificado;
	}

	protected void setModificado(Date modificado) {
		this.modificado = modificado;
	}

	public int getVersao() {
		return versao;
	}

	protected void setVersao(int versao) {
		this.versao = versao;
	}

	public String getPath() {
		return path;
	}

	public ByteBuffer getConteudo() {
		return conteudo;
	}

	protected void setConteudo(ByteBuffer conteudo) {
		this.conteudo = conteudo;
	}

	protected Vector<String> getFilhos() {
		return filhos;
	}
	
	protected void add(String childPath){
		this.filhos.add(childPath);;
	}
}
