package es.uvigo.esei.dai.hybridserver.controller;

import java.util.UUID;

import es.uvigo.esei.dai.hybridserver.DB;
import es.uvigo.esei.dai.hybridserver.http.HTTPHeaders;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequestMethod;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;
import es.uvigo.esei.dai.hybridserver.http.MIME;
import es.uvigo.esei.dai.hybridserver.model.dao.DAO;
import es.uvigo.esei.dai.hybridserver.model.dao.DAO_XSD;
import es.uvigo.esei.dai.hybridserver.model.entity.Document;

public class XSDController {

	private HTTPRequest request;
	private DAO<Document> dao;

	public XSDController(DB db, HTTPRequest request) {
		this.request = request;
		this.dao = new DAO_XSD(db.getUrl(), db.getUser(), db.getPassword());
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
		default: //Si la petición se realiza por cualquier otro método se responde con ERROR 400
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

		if ((uuid = request.getResourceParameters().get("uuid")) != null) { //Si la petición contiene el uuid
			try {
				if (dao.get(uuid) != null) {
					dao.delete(uuid);
					response.setContent(
							"<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Hybrid Server</title></head><body><h1>Hybrid Server</h1><p>La pagina ha sido eliminada</p><p>Authors: Yomar Costa Orellana &amp; José Manuel Viñas Cid</p></body></html>");
					response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(), MIME.TEXT_HTML.getMime());
					response.setStatus(HTTPResponseStatus.S200);
				} else { //Si no existe la pagina con el UUID se devuelve un error 404
					response.setStatus(HTTPResponseStatus.S404);
				}
			} catch (RuntimeException e) {//Si hay un error en la consulta/eliminación en la BD se devuelve un error 500
				response.setStatus(HTTPResponseStatus.S500);
			}
		} else {//Si no se envió el UUID se devuelve un ERROR 400
			response.setStatus(HTTPResponseStatus.S400);
		}

		return response;
	}

	private HTTPResponse getResponsePost(HTTPRequest request) {
		HTTPResponse response = new HTTPResponse();
		response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());
		
		String xsd;

		if ((xsd = request.getResourceParameters().get("xsd")) != null) { //Si la petición tiene el contenido
			try {
				String uuid;
				do {
					uuid = UUID.randomUUID().toString();
				} while (dao.get(uuid) != null);
				dao.insert(new Document(uuid, xsd));

				response.setContent(new StringBuilder(
						"<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Hybrid Server</title></head><body><a href=\"xsd?uuid=")
								.append(uuid).append("\">").append(uuid).append("</a></body></html>").toString());
				response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(), MIME.TEXT_HTML.getMime());
				response.setStatus(HTTPResponseStatus.S200);
			} catch (RuntimeException e) {//Si hay un error en la consulta/insercion en la BD se devuelve un error 500
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

		if (uuid != null) { //Si se pasa el parametro uuid
			Document document;
			try {
				if ((document = dao.get(uuid)) != null) {//Se solicita el contenido del UUID y si existe se devuelve dicho contenido
					response.setContent(document.getContent());
					response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(), MIME.APPLICATION_XML.getMime());
					response.setStatus(HTTPResponseStatus.S200);
				} else { //Si no existe se devuelve un ERROR 404
					response.setStatus(HTTPResponseStatus.S404);
				}
			} catch (RuntimeException e) { //Si hay un error en la consulta a la BD se devuelve un error 500
				response.setStatus(HTTPResponseStatus.S500);
			}
			
		} else { //Si no se pasa el parametro UUID se devuelve una página con la lista de páginas
			StringBuilder content = new StringBuilder(
					"<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Hybrid Server</title></head><body><h1>Hybrid Server - XSD</h1><h2>Pages List</h2><ul>");

			try {
				for (Document page : dao.listPages()) {
					content.append("<li><a href=\"/xsd?uuid=").append(page.getUUID()).append("\">").append(page.getUUID())
							.append("</a></li>");
				}

				content.append("</ul><p>Authors: Yomar Costa Orellana &amp; José Manuel Viñas Cid</p></body></html>");
				response.setContent(content.toString());
				response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(), MIME.TEXT_HTML.getMime());
				response.setStatus(HTTPResponseStatus.S200);
			} catch (RuntimeException e) { //Si hay un error en la consulta a la BD se devuelve un error 500
				response.setStatus(HTTPResponseStatus.S500);
			}
		}
		
		return response;
	}

}
