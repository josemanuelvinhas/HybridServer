package es.uvigo.esei.dai.hybridserver.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class HTTPRequest {

	private HTTPRequestMethod method;
	private String resourceName;
	private String resourceChain;
	private String[] resourcePath;
	private Map<String, String> resourceParameters;
	private String httpVersion;
	private Map<String, String> headerParameters;
	private int contentLength;
	private String content;

	public HTTPRequest(Reader reader) throws IOException, HTTPParseException {

		BufferedReader br = new BufferedReader(reader);

		String linea = br.readLine();

		if (linea == null) {
			throw new HTTPParseException();
		}

		String[] primeraLinea = linea.split(" ");

		if (primeraLinea.length != 3) {
			throw new HTTPParseException();
		}

		this.method = HTTPRequestMethod.valueOf(primeraLinea[0]);
		this.resourceChain = primeraLinea[1];
		String[] tempResourceName = this.resourceChain.split("\\?");
		this.resourceName = tempResourceName[0].substring(1, tempResourceName[0].length());

		if (this.resourceName.isEmpty()) {
			this.resourcePath = new String[0];
		} else {
			this.resourcePath = this.resourceName.split("/");
		}

		this.resourceParameters = new LinkedHashMap<>();

		if (tempResourceName.length > 1) {
			String[] temp, resourceChain = this.resourceChain.split("\\?")[1].split("&");
			for (int i = 0; i < resourceChain.length; i++) {
				temp = resourceChain[i].split("=");
				this.resourceParameters.put(temp[0], temp[1]);
			}
		}

		this.httpVersion = primeraLinea[2];

		this.headerParameters = new LinkedHashMap<>();
		String headerParameter;
		String[] temp;
		while (!(headerParameter = br.readLine()).equals("")) {
			temp = headerParameter.split(": ");
			if (temp.length != 2) {
				throw new HTTPParseException();
			}
			this.headerParameters.put(temp[0], temp[1]);
		}

		String cL = this.headerParameters.get(HTTPHeaders.CONTENT_LENGTH.getHeader());

		if (cL != null) {

			this.contentLength = Integer.parseInt(cL);
			this.content = "";

			char tempChar;
			for (int i = 0; i < this.contentLength; i++) {
				tempChar = (char) br.read();
				this.content += tempChar;
			}

			String contentType = this.getHeaderParameters().get(HTTPHeaders.CONTENT_TYPE.getHeader());
			if (contentType != null && contentType.startsWith(MIME.FORM.getMime())) {
				this.content = URLDecoder.decode(this.content, "UTF-8");
			}

			String[] pair, tempResourceParameters = this.content.split("&");
			for (int i = 0; i < tempResourceParameters.length; i++) {
				pair = tempResourceParameters[i].split("=", 2);
				this.getResourceParameters().put(pair[0], pair[1]);
			}

		} else {
			this.contentLength = 0;
		}

	}

	public HTTPRequestMethod getMethod() {
		return this.method;
	}

	public String getResourceChain() {
		return this.resourceChain;
	}

	public String[] getResourcePath() {
		return this.resourcePath;
	}

	public String getResourceName() {
		return this.resourceName;
	}

	public Map<String, String> getResourceParameters() {
		return this.resourceParameters;
	}

	public String getHttpVersion() {
		return this.httpVersion;
	}

	public Map<String, String> getHeaderParameters() {
		return this.headerParameters;
	}

	public String getContent() {
		return this.content;
	}

	public int getContentLength() {
		return this.contentLength;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(this.getMethod().name()).append(' ').append(this.getResourceChain())
				.append(' ').append(this.getHttpVersion()).append("\r\n");

		for (Map.Entry<String, String> param : this.getHeaderParameters().entrySet()) {
			sb.append(param.getKey()).append(": ").append(param.getValue()).append("\r\n");
		}

		if (this.getContentLength() > 0) {
			sb.append("\r\n").append(this.getContent());
		}

		return sb.toString();
	}
}
