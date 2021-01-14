package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.ws.Endpoint;

import es.uvigo.esei.dai.hybridserver.ws.HybridServerServiceImpl;

public class HybridServer {

	private static final int SERVICE_PORT = 8888;
	private static final int NUM_CLIENTS = 50;
	private static final String DB_URL = "jdbc:mysql://localhost:3306/hstestdb";
	private static final String DB_USER = "hsdb";
	private static final String DB_PASSWORD = "hsdbpass";
	private static final String WEB_SERVICE_URL = null;

	private Thread serverThread;
	private ExecutorService threadPool;
	private boolean stop;
	private Endpoint endpoint;

	private Configuration configuration;

	public HybridServer() {

		this.configuration = new Configuration();

		this.configuration.setNumClients(NUM_CLIENTS);
		this.configuration.setHttpPort(SERVICE_PORT);
		this.configuration.setDbURL(DB_URL);
		this.configuration.setDbUser(DB_USER);
		this.configuration.setDbPassword(DB_PASSWORD);
		this.configuration.setServers(new LinkedList<ServerConfiguration>());
		this.configuration.setWebServiceURL(WEB_SERVICE_URL);
	}

	public HybridServer(Configuration configuration) throws Exception {
		this.configuration = configuration;
	}

	public HybridServer(Properties properties) {

		this.configuration = new Configuration();

		try {
			this.configuration.setNumClients(Integer.parseInt(properties.getProperty("numClients")));
		} catch (NumberFormatException e) {
			throw new RuntimeException("Error: loading properties error");
		}

		try {
			this.configuration.setHttpPort(Integer.parseInt(properties.getProperty("port")));
		} catch (NumberFormatException e) {
			throw new RuntimeException("Error: loading properties error");
		}

		String db_url;
		if ((db_url = properties.getProperty("db.url")) == null) {
			throw new RuntimeException("Error: loading properties error");
		} else {
			this.configuration.setDbURL(db_url);
		}

		String db_user;
		if ((db_user = properties.getProperty("db.user")) == null) {
			throw new RuntimeException("Error: loading properties error");
		} else {
			this.configuration.setDbUser(db_user);
		}

		String db_password;
		if ((db_password = properties.getProperty("db.password")) == null) {
			throw new RuntimeException("Error: loading properties error");
		} else {
			this.configuration.setDbPassword(db_password);
		}

		this.configuration.setServers(new LinkedList<ServerConfiguration>());
		this.configuration.setWebServiceURL(WEB_SERVICE_URL);
	}

	public int getPort() {
		return configuration.getHttpPort();
	}

	public void start() {

		

		this.serverThread = new Thread() {
			@Override
			public void run() {
				try (final ServerSocket serverSocket = new ServerSocket(configuration.getHttpPort())) {

					threadPool = Executors.newFixedThreadPool(configuration.getNumClients());
					
					if(configuration.getWebServiceURL() != null) {
						//Publicar los WS
						endpoint = Endpoint.publish(configuration.getWebServiceURL(),
								new HybridServerServiceImpl(configuration));
						//Utilizar el mismo pool que para las peticiones
						endpoint.setExecutor(threadPool);
					}

					while (true) {
						Socket socket = serverSocket.accept();
						if (stop)
							break;
						threadPool.execute(new HybridServerServiceThread(socket, configuration));
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

		try (Socket socket = new Socket("localhost", configuration.getHttpPort())) {
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
		
		if(this.configuration.getWebServiceURL() != null) {
			this.endpoint.stop();
		}
		
	}
}
