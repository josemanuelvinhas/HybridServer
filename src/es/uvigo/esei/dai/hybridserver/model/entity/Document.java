package es.uvigo.esei.dai.hybridserver.model.entity;

public class Document {

	private String uuid;
	private String content;
	private String xsd;

	public Document(String uuid, String content) {
		this.uuid = uuid;
		this.content = content;
	}

	public Document(String uuid, String content, String xsd) {
		this.uuid = uuid;
		this.content = content;
		this.xsd = xsd;
	}

	public Document(String uuid) {
		this.uuid = uuid;
	}

	public String getUUID() {
		return uuid;
	}

	public String getContent() {
		return content;
	}
	
	public String getXsd() {
		return xsd;
	}

	public void setUUID(String uuid) {
		this.uuid = uuid;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public void setXsd(String xsd) {
		this.xsd = xsd;
	}

}
