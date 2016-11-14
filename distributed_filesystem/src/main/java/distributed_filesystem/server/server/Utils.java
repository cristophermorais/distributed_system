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

		public Logger(String name) {
			try {
				buffWrite = new BufferedWriter(new FileWriter(name + ".log"));
			} catch (IOException e) {
			}
		}

		public void info(Object obj) {
			System.out
					.println(new SimpleDateFormat("dd/MM/YYYY HH:mm:ss").format(new Date()) + "[IC] " + obj.toString());
			try {
				buffWrite.append(
						new SimpleDateFormat("dd/MM/YYYY HH:mm:ss").format(new Date()) + "[IC] " + obj.toString());
				buffWrite.append("\n");
				buffWrite.flush();
			} catch (IOException e) {
				System.out.println(new SimpleDateFormat("dd/MM/YYYY HH:mm:ss").format(new Date())
						+ "[IC] Falha ao gerar arquivo de log");
				e.printStackTrace();
			}
		}
	}
}
