package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import es.uvigo.esei.dai.hybridserver.dao.DAO;

public class HybridServerServiceThread implements Runnable {

	private Socket socket;
	private DAO dao;

	public HybridServerServiceThread(Socket socket, DAO dao) {
		this.socket = socket;
		this.dao = dao;
	}

	@Override
	public void run() {
		try (Socket socket = this.socket;
				InputStreamReader isr = new InputStreamReader(socket.getInputStream());
				OutputStreamWriter osr = new OutputStreamWriter(socket.getOutputStream())) {

			Controller controller = new Controller(isr, this.dao);
			controller.getResponse().print(osr);

		} catch (IOException e) {
			System.out.println("Error serving a client");
		}

	}

}
