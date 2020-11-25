package es.uvigo.esei.dai.hybridserver.controller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import es.uvigo.esei.dai.hybridserver.DB;
import es.uvigo.esei.dai.hybridserver.http.HTTPHeaders;
import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;
import es.uvigo.esei.dai.hybridserver.http.MIME;

public class Controller {

	private InputStreamReader isr;
	private OutputStreamWriter osr;
	private DB db;

	public Controller(InputStreamReader isr, OutputStreamWriter osr, DB db) {
		this.isr = isr;
		this.osr = osr;
		this.db = db;
	}

	public void printResponse() throws IOException {
		this.getResponse().print(osr);
	}

	public HTTPResponse getResponse() throws IOException {

		HTTPResponse response;
		HTTPRequest request;

		try {

			request = new HTTPRequest(this.isr);

		} catch (HTTPParseException e) { // Si la petición está malformada se responde con ERROR 400
			response = new HTTPResponse();
			response.setStatus(HTTPResponseStatus.S400);
			return response;
		}

		String resource = request.getResourceName();
		switch (resource) {
		case "":
			response = this.getResponseIndex();
			break;
		case "html":
			response = new HTMLController(db, request).getResponse();
			break;
		case "xml":
			response = new XMLController(db, request).getResponse();
			break;
		case "xsd":
			response = new XSDController(db, request).getResponse();
			break;
		case "xslt":
			response = new XSLTController(db, request).getResponse();
			break;
		default: // Si la petición se realiza a otro recurso
			response = getResponseDefault();
			break;
		}

		return response;
	}

	private HTTPResponse getResponseIndex() {
		HTTPResponse response = new HTTPResponse();

		response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());
		response.setContent(
				"<!DOCTYPE html> <html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Hybrid Server</title></head><body><h1>Hybrid Server</h1><ul><li><a href=\"/html\">HTML</a></li><li><a href=\"/xml\">XML</a></li><li><a href=\"/xsd\">XSD</a></li><li><a href=\"/xslt\">XSLT</a></li></ul><p>Authors: Yomar Costa Orellana &amp; José Manuel Viñas Cid</p></body></html>");
		response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(), MIME.TEXT_HTML.getMime());
		response.setStatus(HTTPResponseStatus.S200);

		return response;
	}

	private HTTPResponse getResponseDefault() {
		HTTPResponse response = new HTTPResponse();
		
		response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());
		response.setStatus(HTTPResponseStatus.S400);

		return response;
	}

}
