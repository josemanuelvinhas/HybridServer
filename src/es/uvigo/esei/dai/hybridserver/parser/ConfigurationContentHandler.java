package es.uvigo.esei.dai.hybridserver.parser;

import java.util.LinkedList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import es.uvigo.esei.dai.hybridserver.Configuration;
import es.uvigo.esei.dai.hybridserver.ServerConfiguration;

public class ConfigurationContentHandler extends DefaultHandler {
	
	// Atributos auxiliares
	private boolean zonaHTTP;
	private boolean zonaWebService;
	private boolean zonaNumClients;

	private boolean zonaUser;
	private boolean zonaPassword;
	private boolean zonaUrl;

	// Atributos de configuration
	private int httpPort;
	private int numClients;
	private String webServiceURL;

	private String dbUser;
	private String dbPassword;
	private String dbURL;

	private List<ServerConfiguration> servers;

	// Atributo de configuración (contiene los anteriores)
	private Configuration configuration;

	public Configuration getConfiguration() {
		return configuration;
	}

	@Override
	public void startDocument() throws SAXException {
		this.zonaHTTP = false;
		this.zonaWebService = false;
		this.zonaNumClients = false;

		this.zonaUser = false;
		this.zonaPassword = false;
		this.zonaUrl = false;
		
		this.servers = new LinkedList<ServerConfiguration>();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes)
			throws SAXException {

		if ("http".equals(localName)) {
			this.zonaHTTP = true;

		} else if ("webservice".equals(localName)) {
			this.zonaWebService = true;

		} else if ("numClients".equals(localName)) {
			this.zonaNumClients = true;

		} else if ("user".equals(localName)) {
			this.zonaUser = true;

		} else if ("password".equals(localName)) {
			this.zonaPassword = true;

		} else if ("url".equals(localName)) {
			this.zonaUrl = true;

		} else if ("server".equals(localName)) {
			servers.add(new ServerConfiguration(attributes.getValue("name"), attributes.getValue("wsdl"),
					attributes.getValue("namespace"), attributes.getValue("service"),
					attributes.getValue("httpAddress")));
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (this.zonaHTTP) {
			this.httpPort = Integer.parseInt(new String(ch, start, length));
			this.zonaHTTP = false;

		} else if (this.zonaWebService) {
			this.webServiceURL = new String(ch, start, length);
			this.zonaWebService = false;

		} else if (this.zonaNumClients) {
			this.numClients = Integer.parseInt(new String(ch, start, length));
			this.zonaNumClients = false;

		} else if (this.zonaUser) {
			this.dbUser = new String(ch, start, length);
			this.zonaUser = false;

		} else if (this.zonaPassword) {
			this.dbPassword = new String(ch, start, length);
			this.zonaPassword = false;

		} else if (this.zonaUrl) {
			this.dbURL = new String(ch, start, length);
			this.zonaUrl = false;
		}
	}

	@Override
	public void endDocument() throws SAXException {
		this.configuration = new Configuration(this.httpPort, this.numClients, this.webServiceURL, this.dbUser,
				this.dbPassword, this.dbURL, this.servers);
	}

}
