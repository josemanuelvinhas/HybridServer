package es.uvigo.esei.dai.hybridserver.model.dao;

import java.util.List;

import es.uvigo.esei.dai.hybridserver.model.entity.Document;
import es.uvigo.esei.dai.hybridserver.model.entity.DocumentXSLT;

public interface DAO {

	public Document get(String UUID);

	public List<Document> listPages();

	public void insert(Document document);

	public void delete(String UUID);

}
