package es.uvigo.esei.dai.hybridserver.controller;

import java.io.IOException;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import es.uvigo.esei.dai.hybridserver.DB;
import es.uvigo.esei.dai.hybridserver.http.HTTPHeaders;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequestMethod;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;
import es.uvigo.esei.dai.hybridserver.http.MIME;
import es.uvigo.esei.dai.hybridserver.model.dao.DAO;
import es.uvigo.esei.dai.hybridserver.model.dao.DAO_HTML;
import es.uvigo.esei.dai.hybridserver.model.dao.DAO_XML;
import es.uvigo.esei.dai.hybridserver.model.dao.DAO_XSD;
import es.uvigo.esei.dai.hybridserver.model.dao.DAO_XSLT;
import es.uvigo.esei.dai.hybridserver.model.entity.Document;
import es.uvigo.esei.dai.hybridserver.model.entity.DocumentXSLT;
import es.uvigo.esei.dai.hybridserver.parser.ParsingUtils;

public class XMLController {

	private HTTPRequest request;
	private DAO<Document> daoHTML;
	private DAO<DocumentXSLT> daoXSLT;
	private DAO<Document> daoXSD;

	public XMLController(DB db, HTTPRequest request) {
		this.request = request;
		this.daoHTML = new DAO_XML(db.getUrl(), db.getUser(), db.getPassword());
		this.daoXSLT = new DAO_XSLT(db.getUrl(), db.getUser(), db.getPassword());
		this.daoXSD = new DAO_XSD(db.getUrl(), db.getUser(), db.getPassword());
	}

	public HTTPResponse getResponse() {
		HTTPResponse response;
		HTTPRequestMethod method = request.getMethod();

		switch (method) {
		case GET:
			response = this.getResponseGet(request);
			break;
		case POST:
			response = this.getResponsePost(request);
			break;
		case DELETE:
			response = this.getResponseDelete(request);
			break;
		default: // Si la petición se realiza por cualquier otro método se responde con ERROR 400
			response = new HTTPResponse();
			response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());
			response.setStatus(HTTPResponseStatus.S400);
			break;
		}

		return response;
	}

	private HTTPResponse getResponseDelete(HTTPRequest request) {
		HTTPResponse response = new HTTPResponse();
		response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());

		String uuid;

		if ((uuid = request.getResourceParameters().get("uuid")) != null) { // Si el recurso es HTML y se la petición
																			// contiene el uuid
			try {
				if (daoHTML.get(uuid) != null) {
					daoHTML.delete(uuid);
					response.setContent(
							"<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Hybrid Server</title></head><body><h1>Hybrid Server</h1><p>La pagina ha sido eliminada</p><p>Authors: Yomar Costa Orellana &amp; José Manuel Viñas Cid</p></body></html>");
					response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(), MIME.TEXT_HTML.getMime());
					response.setStatus(HTTPResponseStatus.S200);
				} else { // Si no existe la pagina con el UUID se devuelve un error 404
					response.setStatus(HTTPResponseStatus.S404);
				}
			} catch (RuntimeException e) {// Si hay un error en la consulta/eliminación en la BD se devuelve un error
											// 500
				response.setStatus(HTTPResponseStatus.S500);
			}
		} else {// Si no se envió el UUID se devuelve un ERROR 400
			response.setStatus(HTTPResponseStatus.S400);
		}

		return response;
	}

	private HTTPResponse getResponsePost(HTTPRequest request) {
		HTTPResponse response = new HTTPResponse();
		response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());

		String xml;

		if ((xml = request.getResourceParameters().get("xml")) != null) { // Si la petición tiene el contenido
			try {
				String uuid;
				do {
					uuid = UUID.randomUUID().toString();
				} while (daoHTML.get(uuid) != null);
				daoHTML.insert(new Document(uuid, xml));

				response.setContent(new StringBuilder(
						"<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Hybrid Server</title></head><body><a href=\"xml?uuid=")
								.append(uuid).append("\">").append(uuid).append("</a></body></html>").toString());
				response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(), MIME.TEXT_HTML.getMime());
				response.setStatus(HTTPResponseStatus.S200);
			} catch (RuntimeException e) {// Si hay un error en la consulta/insercion en la BD se devuelve un error 500
				response.setStatus(HTTPResponseStatus.S500);
			}
		} else { // Si no se envió el contenido se devuelve un ERROR 400
			response.setStatus(HTTPResponseStatus.S400);
		}

		return response;
	}

	private HTTPResponse getResponseGet(HTTPRequest request) {
		HTTPResponse response = new HTTPResponse();
		response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());

		String uuid = request.getResourceParameters().get("uuid");

		if (uuid != null) { // Si se pasa el parametro uuid
			Document document;
			try {
				if ((document = daoHTML.get(uuid)) != null) {// Se solicita el contenido del UUID y si existe se
																// devuelve dicho contenido

					String xsltUUID = request.getResourceParameters().get("xslt");

					if (xsltUUID == null) { // Si no se pasa xslt se devuelve xml
						response.setContent(document.getContent());
						response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(), MIME.APPLICATION_XML.getMime());
						response.setStatus(HTTPResponseStatus.S200);

					} else {// Si se pasa xslt se obtiene

						DocumentXSLT documentoXSLT = daoXSLT.get(xsltUUID);
						Document documentoXSD;

						if (documentoXSLT != null && (documentoXSD = daoXSD.get(documentoXSLT.getXsd())) != null) {
							try {
								ParsingUtils.validateStringXMLWithStringXSD(document.getContent(),
										documentoXSD.getContent());

								String resultHTML = ParsingUtils.transformWithXSLT(document.getContent(),
										documentoXSLT.getContent());

								response.setContent(resultHTML);
								response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(), MIME.TEXT_HTML.getMime());
								response.setStatus(HTTPResponseStatus.S200);
								// En caso de error validando se devuelve ERROR 400
							} catch (ParserConfigurationException | SAXException | IOException e) {
								response.setStatus(HTTPResponseStatus.S400);
							} catch (TransformerException e) {
								// TODO Ver que error devolver en caso de MALA TRANSFORMACION
							}
						}else {
							response.setStatus(HTTPResponseStatus.S404);
						}

					}

				} else { // Si no existe se devuelve un ERROR 404
					response.setStatus(HTTPResponseStatus.S404);
				}
			} catch (RuntimeException e) { // Si hay un error en la consulta a la BD se devuelve un error 500
				response.setStatus(HTTPResponseStatus.S500);
			}

		} else { // Si no se pasa el parametro UUID se devuelve una página con la lista de
					// páginas
			StringBuilder content = new StringBuilder(
					"<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Hybrid Server</title></head><body><h1>Hybrid Server - XML</h1><h2>Pages List</h2><ul>");
			try {
				for (Document page : daoHTML.listPages()) {
					content.append("<li><a href=\"/xml?uuid=").append(page.getUUID()).append("\">")
							.append(page.getUUID()).append("</a></li>");
				}

				content.append("</ul><p>Authors: Yomar Costa Orellana &amp; José Manuel Viñas Cid</p></body></html>");
				response.setContent(content.toString());
				response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(), MIME.TEXT_HTML.getMime());
				response.setStatus(HTTPResponseStatus.S200);
			} catch (RuntimeException e) { // Si hay un error en la consulta a la BD se devuelve un error 500
				response.setStatus(HTTPResponseStatus.S500);
			}
		}

		return response;
	}

}
