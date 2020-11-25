package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import es.uvigo.esei.dai.hybridserver.controller.Controller;

public class HybridServerServiceThread implements Runnable {

	private Socket socket;
	private DB db;

	public HybridServerServiceThread(Socket socket, DB db) {
		this.socket = socket;
		this.db = db;
	}

	@Override
	public void run() {
		try (Socket socket = this.socket;
				InputStreamReader isr = new InputStreamReader(socket.getInputStream());
				OutputStreamWriter osr = new OutputStreamWriter(socket.getOutputStream())) {

			new Controller(isr, osr, this.db).printResponse();

		} catch (IOException e) {
			System.out.println("Error serving a client");
		}

	}

}
