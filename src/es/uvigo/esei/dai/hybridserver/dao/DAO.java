package es.uvigo.esei.dai.hybridserver.dao;

import java.util.List;

public interface DAO {

	public String get(String UUID);

	public List<String> listPages();

	public void insert(String UUID, String content);

	public void delete(String UUID);

}
