package distributed_filesystem.server.server;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import distributed_filesystem.server.core.Calculator;

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

		try {
			transport = new TSocket("localhost", port);
			transport.open();
			protocol = new TBinaryProtocol(transport);
			client = new Calculator.Client(protocol);
		} catch (TTransportException e) {
			JavaServer.log.info("Falha ao criar conexão " + port);
			Thread.sleep(2000);
			iniciar();
		}

	}

	public void run() {
		while (true) {
			try {
				client.ping();
				JavaServer.log.info("Servidor conectado. Porta " + port);
			} catch (Exception e) {
				try {
					iniciar();
				} catch (Exception e1) {
					JavaServer.log.info("Servidor Offline. Porta " + port);
				}
			} finally {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					JavaServer.log.info("Falha. TInterruptedException na porta " + port);
				}
			}

		}

	}
}