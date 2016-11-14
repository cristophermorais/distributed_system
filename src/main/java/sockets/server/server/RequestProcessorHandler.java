package sockets.server.server;

import java.util.HashMap;

import org.apache.thrift.TException;

import sockets.server.core.InvalidOperation;
import sockets.server.core.Work;
import sockets.server.core.RequestProcessor;
import sockets.server.core.Retorno;
import sockets.server.core.SharedStruct;

public class RequestProcessorHandler implements RequestProcessor.Iface {

	private HashMap<Integer, SharedStruct> log;

	public RequestProcessorHandler() {
		log = new HashMap<Integer, SharedStruct>();
	}

	public void ping() {
		Server.log.infoClient("Ping Recebido");
	}

	public int add(int n1, int n2) {
		Server.log.infoClient("add(" + n1 + "," + n2 + ")");
		return n1 + n2;
	}

	public SharedStruct getStruct(int key) {
		Server.log.infoClient("getStruct(" + key + ")");
		return log.get(key);
	}
	
	public void zip() {
		Server.log.infoClient("zip()");
	}

	public Retorno calculate(int logid, Work w) throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return null;
	}

}