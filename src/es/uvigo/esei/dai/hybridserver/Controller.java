package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

import es.uvigo.esei.dai.hybridserver.dao.DAO;
import es.uvigo.esei.dai.hybridserver.http.HTTPHeaders;
import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequestMethod;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class Controller {

	private InputStreamReader isr;
	private DAO dao;

	public Controller(InputStreamReader isr, DAO dao) {
		this.isr = isr;
		this.dao = dao;
	}

	public HTTPResponse getResponse() throws IOException {

		HTTPResponse response;
		HTTPRequest request;

		try {

			request = new HTTPRequest(this.isr);

		} catch (HTTPParseException e) { //Si la petición está malformada se responde con ERROR 400
			response = new HTTPResponse();
			response.setStatus(HTTPResponseStatus.S400);
			return response;
		}

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
			response.setStatus(HTTPResponseStatus.S400);
			break;
		}

		return response;
	}

	private HTTPResponse getResponseGet(HTTPRequest request) {
		HTTPResponse response = new HTTPResponse();

		response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());
		String resourceName = request.getResourceName();
		
		if (resourceName.equals("")) { //Si no se pasa un nombre del recurso se devuelve la página principal
			response.setContent(
					"<!DOCTYPE html> <html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Hybrid Server</title></head><body><h1>Hybrid Server</h1><ul><li><a href=\"/html\">HTML</a></li></ul><p>Authors: Yomar Costa Orellana &amp; José Manuel Viñas Cid</p></body></html>");
			response.setStatus(HTTPResponseStatus.S200);
			
		} else if (resourceName.equals("html")) { //Si el recurso solicitado es html
			String uuid = request.getResourceParameters().get("uuid");

			if (uuid != null) { //Si se pasa el parametro uuid
				String content;
				try {
					if ((content = dao.get(uuid)) != null) {//Se solicita el contenido del UUID y si existe se devuelve dicho contenido
						response.setContent(content);
						response.setStatus(HTTPResponseStatus.S200);
					} else { //Si no existe se devuelve un ERROR 404
						response.setStatus(HTTPResponseStatus.S404);
					}
				} catch (RuntimeException e) { //Si hay un error en la consulta a la BD se devuelve un error 500
					response.setStatus(HTTPResponseStatus.S500);
				}
				
			} else { //Si no se pasa el parametro UUID se devuelve una página con la lista de páginas
				StringBuilder content = new StringBuilder(
						"<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Hybrid Server</title></head><body><h1>Hybrid Server - HTML</h1><h2>Pages List</h2><ul>");

				try {
					for (String page : dao.listPages()) {
						content.append("<li><a href=\"/html?uuid=").append(page).append("\">").append(page)
								.append("</a></li>");
					}

					content.append("</ul><p>Authors: Yomar Costa Orellana & José Manuel Viñas Cid</p></body></html>");
					response.setContent(content.toString());
					response.setStatus(HTTPResponseStatus.S200);
				} catch (RuntimeException e) { //Si hay un error en la consulta a la BD se devuelve un error 500
					response.setStatus(HTTPResponseStatus.S500);
				}
			}
		} else { //Si el recurso solicitado es cualquier otra cosa se devuelve un ERROR 400
			response.setStatus(HTTPResponseStatus.S400);
		}

		return response;
	}

	private HTTPResponse getResponsePost(HTTPRequest request) {
		HTTPResponse response = new HTTPResponse();

		response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());
		String resourceName = request.getResourceName();
		String html;

		if (resourceName.equals("html") && (html = request.getResourceParameters().get("html")) != null) { //Si el recurso es HTML y se la petición tiene el contenido
			try {
				String uuid;
				do {
					uuid = UUID.randomUUID().toString();
				} while (dao.get(uuid) != null);
				dao.insert(uuid, html);

				response.setContent(new StringBuilder(
						"<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Hybrid Server</title></head><body><a href=\"html?uuid=")
								.append(uuid).append("\">").append(uuid).append("</a></body></html>").toString());

				response.setStatus(HTTPResponseStatus.S200);
			} catch (RuntimeException e) {//Si hay un error en la consulta/insercion en la BD se devuelve un error 500
				response.setStatus(HTTPResponseStatus.S500);
			}
		} else { // Si el recurso es cualquier otra cosa o no se envió el contenido se devuelve un ERROR 400
			response.setStatus(HTTPResponseStatus.S400);
		}

		return response;
	}

	private HTTPResponse getResponseDelete(HTTPRequest request) {

		HTTPResponse response = new HTTPResponse();

		response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());

		String uuid, resourceName = request.getResourceName();

		if (resourceName.equals("html") && (uuid = request.getResourceParameters().get("uuid")) != null) { //Si el recurso es HTML y se la petición contiene el uuid
			try {
				if (dao.get(uuid) != null) {
					dao.delete(uuid);
					response.setContent(
							"<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Hybrid Server</title></head><body><h1>Hybrid Server</h1><p>La pagina ha sido eliminada</p><p>Authors: Yomar Costa Orellana & José Manuel Viñas Cid</p></body></html>");
					response.setStatus(HTTPResponseStatus.S200);
				} else { //Si no existe la pagina con el UUID se devuelve un error 404
					response.setStatus(HTTPResponseStatus.S404);
				}
			} catch (RuntimeException e) {//Si hay un error en la consulta/eliminación en la BD se devuelve un error 500
				response.setStatus(HTTPResponseStatus.S500);
			}
		} else {//Si el recurso es cualquier otra cosa o no se envió el UUID se devuelve un ERROR 400
			response.setStatus(HTTPResponseStatus.S400);
		}

		return response;
	}
}
