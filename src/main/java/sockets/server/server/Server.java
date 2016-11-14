package sockets.server.server;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

import sockets.server.core.RequestProcessor;

public class Server {

	public static RequestProcessorHandler handler;
	public static RequestProcessor.Processor<RequestProcessorHandler> processor;
	public static int port;
	public static Utils.Logger log;

	/**
	 uso : java -jar Server.jar -p [PORTA] -t [DELAY DE INICIALIZAÇÃO] [PORTA DO OUTRO SERVER] [PORTA DO OUTRO SERVER]...
	 
	 Ex.: java -jar /home/cristopher/Desktop/Server.jar -p 8080 -t 5 8081 8082 8083
	 	
	 	porta do servidor local:8080
	 	iniciará em 5 segundos
	 	Servidor remoto 1: 8081
	 	Servidor remoto 2: 8082
	 	Servidor remoto 3: 8083
	 	
	 */
	public static void main(final String[] args) throws InterruptedException {
		long time = Long.parseLong(args[3]);
		port = Integer.parseInt(args[1]);
		log = new Utils(). new Logger("log" + port);
		Integer[] servidores = new Integer[args.length - 4];
		for (int i = 0; i < (args.length - 4); i++) {
			servidores[i] = Integer.parseInt(args[i + 4]);
		}
		try {
			handler = new RequestProcessorHandler();
			processor = new RequestProcessor.Processor<RequestProcessorHandler>(handler);

			Runnable simple = new Runnable() {
				public void run() {
					simple(processor, port);
				}
			};
			new Thread(simple).start();
			log.infoServer("Esperando " + time + " segundos para iniciar ...");
			Thread.sleep(time * 1000);
			for (Integer i : servidores) {
				new Thread(new InternalProcessor(i)).start();
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	public static void simple(RequestProcessor.Processor<RequestProcessorHandler> processor, Integer port) {
		try {
			TServerTransport serverTransport = new TServerSocket(port);
			TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));
			server.serve();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}