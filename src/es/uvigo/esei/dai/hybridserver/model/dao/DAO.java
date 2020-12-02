package es.uvigo.esei.dai.hybridserver.model.dao;

import java.util.List;

import es.uvigo.esei.dai.hybridserver.model.entity.Document;
import es.uvigo.esei.dai.hybridserver.model.entity.DocumentXSLT;

public interface DAO<T> {

	public T get(String UUID);

	public List<T> listPages();

	public void insert(T t);

	public void delete(String UUID);

}
