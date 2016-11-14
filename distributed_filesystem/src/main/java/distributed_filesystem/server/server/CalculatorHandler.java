package distributed_filesystem.server.server;

import java.util.HashMap;

import distributed_filesystem.server.core.Calculator;
import distributed_filesystem.server.core.InvalidOperation;
import distributed_filesystem.server.core.Work;
import distributed_filesystem.server.shared.SharedStruct;

public class CalculatorHandler implements Calculator.Iface {

	private HashMap<Integer, SharedStruct> log;

	public CalculatorHandler() {
		log = new HashMap<Integer, SharedStruct>();
	}

	public void ping() {
		Server.log.infoClient("ping()");
	}

	public int add(int n1, int n2) {
		Server.log.infoClient("add(" + n1 + "," + n2 + ")");
		return n1 + n2;
	}

	public int calculate(int logid, Work work) throws InvalidOperation {
		Server.log.infoClient("calculate(" + logid + ", {" + work.op + "," + work.num1 + "," + work.num2 + "})");
		int val = 0;
		switch (work.op) {
		case ADD:
			val = work.num1 + work.num2;
			break;
		case SUBTRACT:
			val = work.num1 - work.num2;
			break;
		case MULTIPLY:
			val = work.num1 * work.num2;
			break;
		case DIVIDE:
			if (work.num2 == 0) {
				InvalidOperation io = new InvalidOperation();
				io.whatOp = work.op.getValue();
				io.why = "Cannot divide by 0";
				throw io;
			}
			val = work.num1 / work.num2;
			break;
		default:
			InvalidOperation io = new InvalidOperation();
			io.whatOp = work.op.getValue();
			io.why = "Unknown operation";
			throw io;
		}

		SharedStruct entry = new SharedStruct();
		entry.key = logid;
		entry.value = Integer.toString(val);
		log.put(logid, entry);

		return val;
	}

	public SharedStruct getStruct(int key) {
		Server.log.infoClient("getStruct(" + key + ")");
		return log.get(key);
	}

	public void zip() {
		Server.log.infoClient("zip()");
	}

}
