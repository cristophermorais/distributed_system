package distributed_filesystem.server.server;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
	public static final long TIME_REQ_TRY = 1000;

	public class Logger {
		private BufferedWriter buffWrite;
		private final String data = "["+new SimpleDateFormat("dd/MM/YYYY HH:mm:ss").format(new Date())+"] ";
		private final String cab = "["+Server.port+"] [IC]";
		private final String cabClient = cab+" [Client] " + data;
		private final String cabServer = cab+" [Server] " +data;

		public Logger(String name) {
			try {
				buffWrite = new BufferedWriter(new FileWriter(name + ".log"));
			} catch (IOException e) {
			}
		}

		public void infoServer(Object obj) {
			System.out.println(cabServer + obj.toString());
			try {
				buffWrite.append(cabServer + obj.toString());
				buffWrite.append("\n");
				buffWrite.flush();
			} catch (IOException e) {
				System.out.println(cabServer + "Falha ao gerar arquivo de log");
				e.printStackTrace();
			}
		}

		public void infoClient(Object obj) {
			System.out.println(cabClient + obj.toString());
			try {
				buffWrite.append(cabClient + obj.toString());
				buffWrite.append("\n");
				buffWrite.flush();
			} catch (IOException e) {
				System.out.println(cabClient + "Falha ao gerar arquivo de log");
				e.printStackTrace();
			}
		}
	}
}
