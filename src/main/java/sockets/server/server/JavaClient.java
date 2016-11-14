package sockets.server.server;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import sockets.server.core.InvalidOperation;
import sockets.server.core.Operation;
import sockets.server.core.RequestProcessor;
import sockets.server.core.Work;

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
		System.out.println("ping()");

		int sum = client.add(1, 1);
		System.out.println("1+1=" + sum);

		Work work = new Work();

//		work.op = Operation.DIVIDE;
//		work.num1 = 1;
//		work.num2 = 0;
		try {
			client.calculate(1, work);
			System.out.println("Whoa we can divide by 0");
		} catch (InvalidOperation io) {
			System.out.println("Invalid operation: " + io.why);
		}

//		work.op = Operation.SUBTRACT;
//		work.num1 = 15;
//		work.num2 = 10;
//		try {
//			int diff = client.calculate(1, work);
//			System.out.println("15-10=" + diff);
//		} catch (InvalidOperation io) {
//			System.out.println("Invalid operation: " + io.why);
//		}
//
//		SharedStruct log = client.getStruct(1);
//		System.out.println("Check log: " + log.value);
	}
}