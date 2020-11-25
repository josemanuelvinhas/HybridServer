package es.uvigo.esei.dai.hybridserver.model.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapDAO implements DAO {

	private Map<String, String> map;

	public MapDAO(Map<String, String> map) {
		this.map = map;
	}

	@Override
	public String get(String UUID) {
		return this.map.get(UUID);
	}

	@Override
	public List<String> listPages() {
		return new ArrayList<String>(map.keySet());
	}

	@Override
	public void insert(String UUID, String content) {
		map.put(UUID, content);
	}

	@Override
	public void delete(String UUID) {
		map.remove(UUID);
	}

}
