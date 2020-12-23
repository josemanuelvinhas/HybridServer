package es.uvigo.esei.dai.hybridserver.ws;

import java.util.List;

import javax.jws.WebService;

import es.uvigo.esei.dai.hybridserver.model.entity.Document;
import es.uvigo.esei.dai.hybridserver.model.entity.DocumentXSLT;

@WebService
public interface HybridServerService {
	
	//HTML
	
	public Document getHTML(String UUID);

	public List<Document> listPagesHTML();

	public void deleteHTML(String UUID);
	
	//XML
	
	public Document getXML(String UUID);

	public List<Document> listPagesXML();

	public void deleteXML(String UUID);
	
	//XSD
	
	public Document getXSD(String UUID);

	public List<Document> listPagesXSD();

	public void deleteXSD(String UUID);
	
	//XSLT
	
	public DocumentXSLT getXSLT(String UUID);

	public List<DocumentXSLT> listPagesXSLT();

	public void deleteXSLT(String UUID);

}



