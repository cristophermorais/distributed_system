package br.com.cristopher.sockets.utils;

public class Logger {
	private boolean printable;
	private final char separator = '#';
	private final int SIZE_OF_LINE = 80;

	public Logger() {
		this.printable = true;
	}

	public Logger(boolean print) {
		this.printable = print;
	}
	

	public void infoLog(Object log) {
		if (isPrintable()) {
			System.out.println("[INFO] "+log.toString());
		}
	}
	
	public void errorLog(Object log) {
		if (isPrintable()) {
			System.out.println("[ERROR] "+log.toString());
		}
	}

	public void separatorLog()
	{
		String sep = "";
		for(int i=0;i<this.SIZE_OF_LINE;i++)
		{
			sep+=this.separator;
		}
		System.out.println(sep);
	}
	
	/**
	 * @return the printable
	 */
	public boolean isPrintable() {
		return printable;
	}

	/**
	 * @param printable
	 *            the printable to set
	 */
	public void setPrintable(boolean printable) {
		this.printable = printable;
	}

}
