package sockets.server.server;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class Utils {
	public static final long TIME_REQ_TRY = 1000;

	public class Logger {
		private BufferedWriter buffWrite;
		private final String cab = "["+Server.port+"] [IC]";
		private final String cabClient = cab+" [Client] ";
		private final String cabServer = cab+" [Server] ";

		public Logger(String name) {
			try {
				buffWrite = new BufferedWriter(new FileWriter(name + ".log"));
			} catch (IOException e) {
			}
		}

		public void infoServer(Object obj) {
			System.out.println(cabServer + getDate() + obj.toString());
			try {
				buffWrite.append(cabServer + getDate() + obj.toString());
				buffWrite.append("\n");
				buffWrite.flush();
			} catch (IOException e) {
				System.out.println(cabServer + getDate() + "Falha ao gerar arquivo de log");
				e.printStackTrace();
			}
		}

		public void infoClient(Object obj) {
			System.out.println(cabClient + getDate() + obj.toString());
			try {
				buffWrite.append(cabClient + getDate() + obj.toString());
				buffWrite.append("\n");
				buffWrite.flush();
			} catch (IOException e) {
				System.out.println(cabClient + getDate() + "Falha ao gerar arquivo de log");
				e.printStackTrace();
			}
		}
		
		private String getDate() {
			return  "["+new SimpleDateFormat("dd/MM/YYYY HH:mm:ss").format(System.currentTimeMillis())+"] ";
		}
	}
}
