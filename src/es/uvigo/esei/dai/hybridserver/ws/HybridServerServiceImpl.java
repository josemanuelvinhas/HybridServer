package es.uvigo.esei.dai.hybridserver.ws;

import java.util.List;

import javax.jws.WebService;

import es.uvigo.esei.dai.hybridserver.Configuration;
import es.uvigo.esei.dai.hybridserver.model.dao.DAO;
import es.uvigo.esei.dai.hybridserver.model.dao.DAO_HTML;
import es.uvigo.esei.dai.hybridserver.model.dao.DAO_XML;
import es.uvigo.esei.dai.hybridserver.model.dao.DAO_XSD;
import es.uvigo.esei.dai.hybridserver.model.dao.DAO_XSLT;
import es.uvigo.esei.dai.hybridserver.model.entity.Document;
import es.uvigo.esei.dai.hybridserver.model.entity.DocumentXSLT;

@WebService(endpointInterface = "es.uvigo.esei.dai.hybridserver.ws.HybridServerService", targetNamespace = "http://hybridserver.dai.esei.uvigo.es/", serviceName = "HybridServerService")
public class HybridServerServiceImpl implements HybridServerService {

	private DAO<Document> daoHTML;
	private DAO<Document> daoXML;
	private DAO<Document> daoXSD;
	private DAO<DocumentXSLT> daoXSLT;

	public HybridServerServiceImpl(Configuration configuration) {
		this.daoHTML = new DAO_HTML(configuration.getDbURL(), configuration.getDbUser(), configuration.getDbPassword());
		this.daoXML = new DAO_XML(configuration.getDbURL(), configuration.getDbUser(), configuration.getDbPassword());
		this.daoXSD = new DAO_XSD(configuration.getDbURL(), configuration.getDbUser(), configuration.getDbPassword());
		this.daoXSLT = new DAO_XSLT(configuration.getDbURL(), configuration.getDbUser(), configuration.getDbPassword());
	}
	
	//HTML
	
	@Override
	public Document getHTML(String UUID) {
		return this.daoHTML.get(UUID);
	}

	@Override
	public List<Document> listPagesHTML() {
		return this.daoHTML.listPages();
	}

	@Override
	public void deleteHTML(String UUID) {
		this.daoHTML.delete(UUID);
	}
	
	//XML

	@Override
	public Document getXML(String UUID) {
		return this.daoXML.get(UUID);
	}

	@Override
	public List<Document> listPagesXML() {
		return this.daoXML.listPages();
	}

	@Override
	public void deleteXML(String UUID) {
		this.daoXML.delete(UUID);
	}
	
	//XSD

	@Override
	public Document getXSD(String UUID) {
		return this.daoXSD.get(UUID);
	}

	@Override
	public List<Document> listPagesXSD() {
		return this.daoXSD.listPages();
	}

	@Override
	public void deleteXSD(String UUID) {
		this.daoXSD.delete(UUID);
	}
	
	//XSLT

	@Override
	public DocumentXSLT getXSLT(String UUID) {
		return this.daoXSLT.get(UUID);
	}

	@Override
	public List<DocumentXSLT> listPagesXSLT() {
		return this.daoXSLT.listPages();
	}

	@Override
	public void deleteXSLT(String UUID) {
		this.daoXSLT.delete(UUID);
	}

}
