package br.com.cristopher.sockets.data;

import java.util.Date;
import java.util.Vector;

public class Node {
	private final Date criado; 
	private final String path;
	private Node pai = null;
	private Date modificado;
	private int versao = 0;
	private Vector<Node> nodes;
	private byte[] conteudo;
		
	
	public Node(String path) {
		modificado = new Date();
		criado =new Date();
		this.path = path;
		this.conteudo = null;
		this.pai = null;
		this.nodes = new Vector<>();
	}
	
	public Node(String path, Node pai) {
		this(path);
		this.pai = pai;
		if(this.pai != null){
			this.pai.add(this);
		}
	}
	public Node(String path, Node pai, byte[] conteudo) {
		this(path, pai);
		this.conteudo = conteudo;
	}

	public int getContentLength() {
		return conteudo.length;
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

	public byte[] getConteudo() {
		return conteudo;
	}

	protected void setConteudo(byte[] conteudo) {
		this.conteudo = conteudo;
	}

	protected Node getPai() {
		return pai;
	}

	protected Vector<Node> getNodes() {
		return nodes;
	}
	
	protected void add(Node node){
		this.nodes.add(node);
	}
}
