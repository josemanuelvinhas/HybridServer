package es.uvigo.esei.dai.hybridserver.ws;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

import es.uvigo.esei.dai.hybridserver.ServerConfiguration;

public class HybridServerServiceConnection {

	private List<ServerConfiguration> servers;

	public HybridServerServiceConnection(List<ServerConfiguration> servers) {
		this.servers = servers;
	}

	public Map<ServerConfiguration, HybridServerService> getConnection() {
		Map<ServerConfiguration, HybridServerService> toret = new HashMap<ServerConfiguration, HybridServerService>();

		for (ServerConfiguration serverConfiguration : servers) {
			try {
				URL url = new URL(serverConfiguration.getWsdl());

				QName name = new QName(serverConfiguration.getNamespace(), "HybridServerService");
				Service service = Service.create(url, name);
				HybridServerService hybridServerService = service.getPort(HybridServerService.class);
				toret.put(serverConfiguration, hybridServerService);
				
			} catch (MalformedURLException | WebServiceException  e) {
				System.out.println("Error conectando con un servidor " + serverConfiguration.getName());
			}
		}

		return toret;
	}

	public List<ServerConfiguration> getServers() {
		return servers;
	}
	
}
