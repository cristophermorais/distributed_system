package sockets.server.server;

import java.nio.ByteBuffer;

import br.com.cristopher.sockets.server.TrataCliente;
import sockets.server.core.RequestProcessor;
import sockets.server.core.Retorno;

public class RequestProcessorHandler implements RequestProcessor.Iface {

	public void ping() {
		Server.log.infoClient("Ping Recebido");
	}

	@Override
	public Retorno request(String request, ByteBuffer content) {
		TrataCliente trtCli = new TrataCliente(request, content);
		Retorno r = trtCli.run();
//		r.setStatus("deu certo");
//		return r;
		return r;
	}

}
