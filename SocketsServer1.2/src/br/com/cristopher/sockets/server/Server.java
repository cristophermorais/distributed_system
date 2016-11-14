package br.com.cristopher.sockets.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Date;

import br.com.cristopher.sockets.utils.Logger;

public class Server {
	private static ServerSocket servidor;
	static Logger log = new Logger();
	private static int port;

	public static void main(String[] args) throws NumberFormatException, IOException {
		port = Integer.parseInt(args[0]);
		log.separatorLog();
		try {
			startServer(port);
		} catch (Exception e) {
			log.errorLog("Erro ao iniciar o servidor. Veja o log!");
		}
		log.separatorLog();
		while (true) {
			try {
				new TrataCliente(servidor.accept());
			} catch (IOException e) {

			}
		}
	}

	private static void startServer(int port) {
		try {
			log.infoLog("Iniciando servidor na " + port + " as " + new Date());
			servidor = new ServerSocket(port);
		} catch (IOException e) {
			log.errorLog("Erro ao iniciar servidor em " + port + ": " + e.getMessage());
			startServer(port+1);
		}
	}
}