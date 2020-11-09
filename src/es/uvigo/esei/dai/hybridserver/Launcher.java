package es.uvigo.esei.dai.hybridserver;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Launcher {
	public static void main(String[] args) {

		if (args.length == 0) {
			new HybridServer().start();

		} else if (args.length == 1) {

			Properties properties = new Properties();

			try (FileReader fr = new FileReader(args[0])) {

				properties.load(fr);
				new HybridServer(properties).start();

			} catch (IOException e) {
				System.out.println("Error: loading properties error");
			} catch (RuntimeException e) {
				System.out.println(e.getMessage());
			}

		}else {
			System.out.println("Error (one argument max) try: java Launcher [Properties File Path]");
		}

	}
}
