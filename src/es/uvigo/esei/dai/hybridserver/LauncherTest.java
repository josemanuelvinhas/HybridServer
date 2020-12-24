package es.uvigo.esei.dai.hybridserver;

import java.io.File;

public class LauncherTest {
	public static void main(String[] args) {

		try {
			System.out.println("Servidor 1");
			Configuration conf = new XMLConfigurationLoader().load(new File("configuration.xml"));
			System.out.println("Servidor 2");
			Configuration conf1 = new XMLConfigurationLoader().load(new File("configuration1.xml"));
			System.out.println("Servidor 3");
			Configuration conf2 = new XMLConfigurationLoader().load(new File("configuration2.xml"));
			
			new HybridServer(conf).start();
			new HybridServer(conf1).start();
			new HybridServer(conf2).start();
			
		} catch (RuntimeException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}
}
