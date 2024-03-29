package es.uvigo.esei.dai.hybridserver.model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import es.uvigo.esei.dai.hybridserver.model.entity.DocumentXSLT;

public class DAO_XSLT implements DAO<DocumentXSLT> {

	private String db_url;
	private String db_user;
	private String db_password;

	public DAO_XSLT(String db_url, String db_user, String db_password) {
		this.db_url = db_url;
		this.db_user = db_user;
		this.db_password = db_password;
	}

	@Override
	public DocumentXSLT get(String UUID) {
		String sql = "SELECT * FROM XSLT WHERE uuid = ?";
		DocumentXSLT toret = null;

		try {
			Connection connection = DriverManager.getConnection(db_url, db_user, db_password);
			PreparedStatement statement = connection.prepareStatement(sql);

			statement.setString(1, UUID);

			try (ResultSet result = statement.executeQuery();
					PreparedStatement statementClose = statement;
					Connection connectionClose = connection) {

				if (result.next()) {
					toret = new DocumentXSLT(UUID, result.getString("content"), result.getString("xsd"));
				}
			}

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return toret;
	}

	@Override
	public List<DocumentXSLT> listPages() {
		String sql = "SELECT uuid FROM XSLT";
		List<DocumentXSLT> toret = new LinkedList<>();

		try {

			Connection connection = DriverManager.getConnection(db_url, db_user, db_password);
			Statement statement = connection.createStatement();

			try (ResultSet result = statement.executeQuery(sql);
					Statement statementClose = statement;
					Connection connectionClose = connection) {

				while (result.next()) {
					toret.add(new DocumentXSLT(result.getString("uuid")));
				}

			}

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return toret;
	}

	public void insert(DocumentXSLT document) {
		String sql = "INSERT INTO XSLT (uuid, content, xsd)" + "VALUES (?, ?, ?)";

		try {

			Connection connection = DriverManager.getConnection(db_url, db_user, db_password);

			try (PreparedStatement statement = connection.prepareStatement(sql);
					Connection connectionClose = connection) {

				statement.setString(1, document.getUUID());
				statement.setString(2, document.getContent());
				statement.setString(3, document.getXsd());

				if (statement.executeUpdate() != 1) {
					throw new RuntimeException("Error inserting page");
				}

			}

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void delete(String UUID) {
		String sql = "DELETE FROM XSLT WHERE uuid = ?";

		try {

			Connection connection = DriverManager.getConnection(db_url, db_user, db_password);

			try (PreparedStatement statement = connection.prepareStatement(sql);
					Connection connectionClose = connection) {

				statement.setString(1, UUID);

				if (statement.executeUpdate() != 1) {
					throw new RuntimeException("Error deleting page");
				}
			}

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}