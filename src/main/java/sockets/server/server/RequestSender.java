package sockets.server.server;

import java.nio.ByteBuffer;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import sockets.server.core.RequestProcessor;
import sockets.server.core.Retorno;

public class RequestSender {
	private int port;
	private String request;
	private ByteBuffer content;
	private Retorno retorno;
	private RequestProcessor.Client client;
	TTransport transport;
	TProtocol protocol;

	public RequestSender(int port, String request, ByteBuffer content) {
		this.port = port;
		this.request = request;
		this.content = content;
	}

	public Retorno send() {
		retorno = null;
		boolean finished = false;
		int tries = 0;

		while (!finished && tries < 3) {
			sendThrift();

			if (retorno != null)
				finished = true;
			else
				tries++;
		}

		return retorno;
	}

	public void sendThrift() {
		transport = new TSocket("localhost", port);
		
		try {
			transport.open();
			protocol = new TBinaryProtocol(transport);
			client = new RequestProcessor.Client(protocol);

			try {
				retorno = client.request(request, content);
			} catch (TException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (TTransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}