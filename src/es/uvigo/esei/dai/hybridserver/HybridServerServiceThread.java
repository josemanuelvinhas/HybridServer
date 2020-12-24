package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import es.uvigo.esei.dai.hybridserver.controller.Controller;
import es.uvigo.esei.dai.hybridserver.ws.HybridServerServiceConnection;

public class HybridServerServiceThread implements Runnable {

	private Socket socket;
	private Configuration configuration;

	public HybridServerServiceThread(Socket socket, Configuration configuration) {
		this.socket = socket;
		this.configuration = configuration;
	}

	@Override
	public void run() {
		try (Socket socket = this.socket;
				InputStreamReader isr = new InputStreamReader(socket.getInputStream());
				OutputStreamWriter osr = new OutputStreamWriter(socket.getOutputStream())) {

			new Controller(isr, osr,
					new DB(configuration.getDbURL(), configuration.getDbUser(), configuration.getDbPassword()),
					new HybridServerServiceConnection(configuration.getServers()))
							.printResponse();

		} catch (IOException e) {
			System.out.println("Error serving a client");
		}

	}

}
