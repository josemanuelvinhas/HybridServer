package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HybridServer {
	private static final int SERVICE_PORT = 8888;
	private static final int NUM_CLIENTS = 50;

	private Thread serverThread;
	private ExecutorService threadPool;
	private boolean stop;
	private DB db;
	private int numClients;
	private int port;
	private String db_url;
	private String db_user;
	private String db_password;

	public HybridServer() {
		this.numClients = NUM_CLIENTS;
		this.port = SERVICE_PORT;
		this.db_url = "jdbc:mysql://localhost:3306/hstestdb";
		this.db_user = "hsdb";
		this.db_password = "hsdbpass";
		
		this.db = new DB(this.db_url, this.db_user, this.db_password);
	}

	public HybridServer(Properties properties) {

		try {
			this.numClients = Integer.parseInt(properties.getProperty("numClients"));
		} catch (NumberFormatException e) {
			throw new RuntimeException("Error: loading properties error");
		}

		try {
			this.port = Integer.parseInt(properties.getProperty("port"));
		} catch (NumberFormatException e) {
			throw new RuntimeException("Error: loading properties error");
		}
		
		if((this.db_url = properties.getProperty("db.url")) == null) {
			throw new RuntimeException("Error: loading properties error");
		}
		
		if((this.db_user = properties.getProperty("db.user")) == null) {
			throw new RuntimeException("Error: loading properties error");
		}
		
		if((this.db_password = properties.getProperty("db.password")) == null) {
			throw new RuntimeException("Error: loading properties error");
		}

		this.db = new DB(this.db_url, this.db_user, this.db_password);
	}

	public int getPort() {
		return this.port;
	}

	public void start() {
		this.serverThread = new Thread() {
			@Override
			public void run() {
				try (final ServerSocket serverSocket = new ServerSocket(port)) {

					threadPool = Executors.newFixedThreadPool(numClients);

					while (true) {
						Socket socket = serverSocket.accept();
						if (stop)
							break;
						threadPool.execute(new HybridServerServiceThread(socket, db));
					}

				} catch (IOException e) {
					throw new RuntimeException("Server Error");
				}
			}
		};

		this.stop = false;
		this.serverThread.start();
	}

	public void stop() {
		this.stop = true;

		try (Socket socket = new Socket("localhost", this.port)) {
			// Esta conexión se hace, simplemente, para "despertar" el hilo servidor
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try {
			this.serverThread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		this.serverThread = null;

		threadPool.shutdownNow();

		try {
			threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
