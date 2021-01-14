package es.uvigo.esei.dai.hybridserver.model.entity;

public class Document {

	private String uuid;
	private String content;

	public Document() {
	}

	public Document(String uuid, String content) {
		this.uuid = uuid;
		this.content = content;
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

	public void setUUID(String uuid) {
		this.uuid = uuid;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
