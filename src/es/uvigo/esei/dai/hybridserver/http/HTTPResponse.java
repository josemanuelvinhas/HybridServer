package es.uvigo.esei.dai.hybridserver.http;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HTTPResponse {

	private HTTPResponseStatus status;
	private String version;
	private String content;
	private Map<String, String> parameters;

	public HTTPResponse() {
		this.parameters = new HashMap<>();
		this.content = "";
	}

	public HTTPResponseStatus getStatus() {
		return this.status;
	}

	public void setStatus(HTTPResponseStatus status) {
		this.status = status;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
		this.parameters.put(HTTPHeaders.CONTENT_LENGTH.getHeader(), Integer.toString(content.getBytes().length));
	}

	public Map<String, String> getParameters() {
		return this.parameters;
	}

	public String putParameter(String name, String value) {
		return this.parameters.put(name, value);
	}

	public boolean containsParameter(String name) {
		return this.parameters.containsKey(name);
	}

	public String removeParameter(String name) {
		return this.parameters.remove(name);
	}

	public void clearParameters() {
		this.parameters.clear();
	}

	public List<String> listParameters() {
		return new ArrayList<>(this.parameters.values());
	}

	public void print(Writer writer) throws IOException {
		BufferedWriter br = new BufferedWriter(writer);
		String firstLine = this.version + " " + this.status.getCode() + " " + this.status.getStatus();
		br.write(firstLine);
		br.write("\r\n");

		for (String key : this.parameters.keySet()) {
			br.write(key + ": " + this.parameters.get(key));
			br.write("\r\n");
		}
		br.write("\r\n");
		br.write(this.content);
		br.flush();
	}

	@Override
	public String toString() {
		final StringWriter writer = new StringWriter();

		try {
			this.print(writer);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return writer.toString();
	}
}
