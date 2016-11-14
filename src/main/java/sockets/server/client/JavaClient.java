package sockets.server.client;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import sockets.server.core.RequestProcessor;
import sockets.server.core.Retorno;

public class JavaClient {
	public static void main(String[] args) {

		try {
			TTransport transport;
			transport = new TSocket("localhost", Integer.parseInt(args[0]));
			transport.open();
			TProtocol protocol = new TBinaryProtocol(transport);
			RequestProcessor.Client client = new RequestProcessor.Client(protocol);

			perform(client);

			transport.close();
		} catch (TException x) {
			x.printStackTrace();
		}
	}

	private static void perform(RequestProcessor.Client client) throws TException {
		client.ping();
		Retorno r = client.request("nada");
		System.out.println(r.getCriado());
		System.out.println(r.getModificado());
		System.out.println(r.getVersao());
	}
}