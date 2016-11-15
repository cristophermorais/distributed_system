package sockets.server.server;

import java.nio.ByteBuffer;

import br.com.cristopher.sockets.data.Request;
import br.com.cristopher.sockets.server.HttpTranslator;
import br.com.cristopher.sockets.server.TrataCliente;
import sockets.server.core.RequestProcessor;
import sockets.server.core.Retorno;

public class RequestProcessorHandler implements RequestProcessor.Iface {

	public void ping() {
		Server.log.infoClient("Ping Recebido");
	}

	public Retorno request(String stringRequest, ByteBuffer content) {
		
		Request req = HttpTranslator.translate(stringRequest, content);
		String url = req.getAbsolutePath();
		int nServer = url.hashCode() % Server.servidores.length;
		
		if(Server.port == Server.servidores[nServer]){
			//trata internamente
		}else{
			//encaminha para o servidor Server.servidores[nServer]
		}
		
		
		
		TrataCliente trtCli = new TrataCliente(req);
		Retorno r = trtCli.run();
//		r.setStatus("deu certo");
//		return r;
		return r;
	}

}
