/**
 *  HybridServer
 *  Copyright (C) 2020 Miguel Reboiro-Jato
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.uvigo.esei.dai.hybridserver;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class XMLConfigurationLoader {

	public Configuration load(File xmlFile) throws Exception {

		ConfigurationContentHandler contendHandler = new ConfigurationContentHandler();
		
		try {
			
			//Funciona con validacion Externa pero no con Interna
			parseAndValidateWithExternalXSD(xmlFile,"configuration.xsd", contendHandler);
			
			//parseAndValidateWithInternalXSD(xmlFile, contendHandler);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new Exception("Configuration parsing error", e);
		}
		
		return contendHandler.getConfiguration();
	}

	private static class ConfigurationContentHandler extends DefaultHandler {

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

	private static void parseAndValidateWithInternalXSD(File xmlFile, ContentHandler handler)
			throws ParserConfigurationException, SAXException, IOException {
		// Construcción del parser del documento. Se activa
		// la validación y comprobación de namespaces
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setValidating(true);
		parserFactory.setNamespaceAware(true);

		// Se añade el manejador de errores y se activa la validación por schema
		SAXParser parser = parserFactory.newSAXParser();
		parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
				XMLConstants.W3C_XML_SCHEMA_NS_URI);
		XMLReader xmlReader = parser.getXMLReader();
		xmlReader.setContentHandler(handler);
		xmlReader.setErrorHandler(new ErrorHandler() {
			
			@Override
			public void warning(SAXParseException exception) throws SAXException {
				exception.printStackTrace();
			}
			
			@Override
			public void fatalError(SAXParseException exception) throws SAXException {
				throw exception;
				
			}
			
			@Override
			public void error(SAXParseException exception) throws SAXException {
				throw exception;
			}
		});

		// Parsing
		try (FileReader fileReader = new FileReader(xmlFile)) {
			xmlReader.parse(new InputSource(fileReader));
		}
	}
	
	public static void parseAndValidateWithExternalXSD(
			File xmlFile, String schemaPath, ContentHandler handler
		) throws ParserConfigurationException, SAXException, IOException {
			// Construcción del schema
			SchemaFactory schemaFactory = 
				SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(new File(schemaPath));
			
			// Construcción del parser del documento. Se establece el esquema y se activa
			// la validación y comprobación de namespaces
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			parserFactory.setValidating(false);
			parserFactory.setNamespaceAware(true);
			parserFactory.setSchema(schema);

			// Se añade el manejador de errores
			SAXParser parser = parserFactory.newSAXParser();
			XMLReader xmlReader = parser.getXMLReader();
			xmlReader.setContentHandler(handler);
			xmlReader.setErrorHandler(new ErrorHandler() {
				
				@Override
				public void warning(SAXParseException exception) throws SAXException {
					exception.printStackTrace();
				}
				
				@Override
				public void fatalError(SAXParseException exception) throws SAXException {
					throw exception;
					
				}
				
				@Override
				public void error(SAXParseException exception) throws SAXException {
					throw exception;
				}
			});

			// Parsing
			try (FileReader fileReader = new FileReader(xmlFile)) {
				xmlReader.parse(new InputSource(fileReader));
			}
		}

}
