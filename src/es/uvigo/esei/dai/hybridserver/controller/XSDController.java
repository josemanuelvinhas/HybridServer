package es.uvigo.esei.dai.hybridserver.controller;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import javax.xml.ws.WebServiceException;

import es.uvigo.esei.dai.hybridserver.DB;
import es.uvigo.esei.dai.hybridserver.ServerConfiguration;
import es.uvigo.esei.dai.hybridserver.http.HTTPHeaders;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequestMethod;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;
import es.uvigo.esei.dai.hybridserver.http.MIME;
import es.uvigo.esei.dai.hybridserver.model.dao.DAO;
import es.uvigo.esei.dai.hybridserver.model.dao.DAO_XSD;
import es.uvigo.esei.dai.hybridserver.model.entity.Document;
import es.uvigo.esei.dai.hybridserver.ws.HybridServerService;
import es.uvigo.esei.dai.hybridserver.ws.HybridServerServiceConnection;

public class XSDController {

	private HTTPRequest request;
	private DAO<Document> daoXSD;
	private HybridServerServiceConnection hybridServerServiceConnection;

	public XSDController(DB db, HybridServerServiceConnection hybridServerServiceConnection, HTTPRequest request) {
		this.request = request;
		this.hybridServerServiceConnection = hybridServerServiceConnection;
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

		if ((uuid = request.getResourceParameters().get("uuid")) != null) { // Si la petición contiene el uuid
			try {
				boolean isDeleted = false;

				// Borrado local
				if (daoXSD.get(uuid) != null) {
					daoXSD.delete(uuid);
					isDeleted = true;
				}

				// Borrado P2P
				Map<ServerConfiguration, HybridServerService> serversConnection = this.hybridServerServiceConnection
						.getConnection();

				Iterator<ServerConfiguration> servers = this.hybridServerServiceConnection.getServers().iterator();

				HybridServerService hybridServerService;
				while (servers.hasNext()) {
					ServerConfiguration serverConfiguration = servers.next();
					try {
						if ((hybridServerService = serversConnection.get(serverConfiguration)) != null
								&& hybridServerService.getXSD(uuid) != null) {
							hybridServerService.deleteXSD(uuid);
							isDeleted = true;
						}
					} catch (WebServiceException e) {
						System.out.println("Server Connection Error: " + serverConfiguration.getName());
					}
				}

				// Si la pagina se ha borrado de algun servidor
				if (isDeleted) {

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

		String xsd;

		if ((xsd = request.getResourceParameters().get("xsd")) != null) { // Si la petición tiene el contenido
			try {
				String uuid;
				do {
					uuid = UUID.randomUUID().toString();
				} while (daoXSD.get(uuid) != null);
				daoXSD.insert(new Document(uuid, xsd));

				response.setContent(new StringBuilder(
						"<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Hybrid Server</title></head><body><a href=\"xsd?uuid=")
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
				if ((document = daoXSD.get(uuid)) != null) {// Se solicita el contenido del UUID y si existe se devuelve
															// dicho contenido
					response.setContent(document.getContent());
					response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(), MIME.APPLICATION_XML.getMime());
					response.setStatus(HTTPResponseStatus.S200);

				} else { // Se busca en el resto de servidores
					Map<ServerConfiguration, HybridServerService> serversConnection = this.hybridServerServiceConnection
							.getConnection();

					Iterator<ServerConfiguration> servers = this.hybridServerServiceConnection.getServers().iterator();

					HybridServerService hybridServerService;
					while (servers.hasNext() && document == null) {
						ServerConfiguration serverConfiguration = servers.next();
						try {
							if ((hybridServerService = serversConnection.get(serverConfiguration)) != null
									&& (document = hybridServerService.getXSD(uuid)) != null)
								;
						} catch (WebServiceException e) {
							System.out.println("Server Connection Error: " + serverConfiguration.getName());
						}
					}

					if (document != null) { // Si existe se devuelve el contenido

						daoXSD.insert(document); // Caché

						response.setContent(document.getContent());
						response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(), MIME.APPLICATION_XML.getMime());
						response.setStatus(HTTPResponseStatus.S200);

					} else { // Si no existe se devuelve un ERROR 404
						response.setStatus(HTTPResponseStatus.S404);
					}
				}
			} catch (RuntimeException e) { // Si hay un error en la consulta a la BD se devuelve un error 500
				response.setStatus(HTTPResponseStatus.S500);
			}

		} else { // Si no se pasa el parametro UUID se devuelve una página con la lista de
					// páginas
			StringBuilder content = new StringBuilder(
					"<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Hybrid Server</title></head><body><h1>Hybrid Server - XSD</h1><h2>Pages List</h2>");

			try {
				content.append("<h3>Local Host</h3>");
				content.append("<ul>");
				for (Document page : daoXSD.listPages()) {
					content.append("<li><a href=\"/xsd?uuid=").append(page.getUUID()).append("\">")
							.append(page.getUUID()).append("</a></li>");
				}
				content.append("</ul>");

				Map<ServerConfiguration, HybridServerService> serversConnection = this.hybridServerServiceConnection
						.getConnection();

				for (ServerConfiguration server : this.hybridServerServiceConnection.getServers()) {
					try {
						content.append("<h3>" + server.getName() + "</h3>");

						HybridServerService hybridServerService = serversConnection.get(server);

						if (hybridServerService != null) {
							content.append("<ul>");
							for (Document page : hybridServerService.listPagesXSD()) {
								content.append("<li><a href=\"/xsd?uuid=").append(page.getUUID()).append("\">")
										.append(page.getUUID()).append("</a></li>");
							}
							content.append("</ul>");
						}
					} catch (WebServiceException e) {
						System.out.println("Server Connection Error: " + server.getName());
					}
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
