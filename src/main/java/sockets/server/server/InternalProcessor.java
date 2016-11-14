package sockets.server.server;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import sockets.server.core.Calculator;

public class InternalProcessor implements Runnable {
	private int port;
	private Calculator.Client client;
	TTransport transport;
	TProtocol protocol;

	public InternalProcessor(int port) throws InterruptedException {
		this.port = port;
		iniciar();
	}

	private void iniciar() throws InterruptedException {
		transport = new TSocket("localhost", port);
		try {
			transport.open();
			protocol = new TBinaryProtocol(transport);
			client = new Calculator.Client(protocol);
		} catch (TTransportException e) {
			Server.log.infoServer("Servidor Offline. Porta " + port);
			Thread.sleep(Utils.TIME_REQ_TRY);
			Runnable r = new Runnable() {
				public void run(){
					try {
						iniciar();
					} catch (InterruptedException e) {
					}
				}
			};
			new Thread(r).start();
		}
		Server.log.infoServer("Finalizando Thread para porta "+port);
	}

	public void run() {
		while (true) {
			try {
				client.ping();
				Server.log.infoServer("Servidor conectado. Porta " + port);
			} catch (Exception e) {
				try {
					iniciar();
				} catch (Exception e1) {
					Server.log.infoServer("Servidor Offline. Porta " + port);
				}
			} finally {
				try {
					Thread.sleep(Utils.TIME_REQ_TRY);
				} catch (InterruptedException e) {
					Server.log.infoServer("Falha. TInterruptedException na porta " + port);
				}
			}

		}

	}
}