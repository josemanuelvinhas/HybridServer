package es.uvigo.esei.dai.hybridserver.parser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class ParsingUtils {

	public static void parseAndValidateWithExternalXSD(File xmlFile, String schemaPath, ContentHandler handler)
			throws ParserConfigurationException, SAXException, IOException {
		// Construcción del schema
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
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
		xmlReader.setErrorHandler(new SimpleErrorHandler());

		// Parsing
		try (FileReader fileReader = new FileReader(xmlFile)) {
			xmlReader.parse(new InputSource(fileReader));
		}
	}

	public static void validateStringXMLWithStringXSD(String XMLString, String schemaString)
			throws ParserConfigurationException, SAXException, IOException {

		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		
		Schema schema;
		try (StringReader schemaStringReader = new StringReader(schemaString)) {
			schema = schemaFactory.newSchema(new StreamSource(schemaStringReader));
		}
	
		DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();
		parserFactory.setValidating(false);
		parserFactory.setNamespaceAware(true);
		parserFactory.setSchema(schema);

		DocumentBuilder builder = parserFactory.newDocumentBuilder();
		builder.setErrorHandler(new SimpleErrorHandler());

		// Parsing
		try (StringReader XMLStringReader = new StringReader(XMLString)) {
			builder.parse(new InputSource(XMLStringReader));
		}
	}

	public static String transformWithXSLT(String XMLString, String XSLTString) throws TransformerException {
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer(new StreamSource(new StringReader(XSLTString)));
		//TODO ver cerrar StringWriter
		StringWriter writer = new StringWriter();
		transformer.transform(new StreamSource(new StringReader(XMLString)), new StreamResult(writer));

		return writer.toString();
	}

}
