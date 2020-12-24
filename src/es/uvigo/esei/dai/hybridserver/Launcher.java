package es.uvigo.esei.dai.hybridserver;

import java.io.File;

public class Launcher {
	public static void main(String[] args) {

		if (args.length == 0) {
			new HybridServer().start();

		} else if (args.length == 1) {

			File configuration = new File(args[0]);
			try {
				Configuration conf = new XMLConfigurationLoader().load(configuration);
				new HybridServer(conf).start();
			} catch (RuntimeException e) {
				System.out.println(e.getMessage());
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

		} else {
			System.out.println("Error (one argument max) try: java Launcher [Configuration File Path]");
		}

	}
}
