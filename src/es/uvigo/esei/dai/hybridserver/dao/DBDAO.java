package es.uvigo.esei.dai.hybridserver.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class DBDAO implements DAO {

	private String db_url;
	private String db_user;
	private String db_password;

	public DBDAO(String db_url, String db_user, String db_password) {
		this.db_url = db_url;
		this.db_user = db_user;
		this.db_password = db_password;
	}

	@Override
	public String get(String UUID) {
		String sql = "SELECT * FROM HTML WHERE uuid = ?";
		String toret = null;

		try {
			Connection connection = DriverManager.getConnection(db_url, db_user, db_password);
			PreparedStatement statement = connection.prepareStatement(sql);

			statement.setString(1, UUID);

			try (ResultSet result = statement.executeQuery();
					PreparedStatement statementClose = statement;
					Connection connectionClose = connection) {

				if (result.next()) {
					toret = result.getString("content");
				}
			}

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return toret;
	}

	@Override
	public List<String> listPages() {
		String sql = "SELECT uuid FROM HTML";
		List<String> toret = new LinkedList<>();

		try {

			Connection connection = DriverManager.getConnection(db_url, db_user, db_password);
			Statement statement = connection.createStatement();

			try (ResultSet result = statement.executeQuery(sql);
					Statement statementClose = statement;
					Connection connectionClose = connection) {

				while (result.next()) {
					toret.add(result.getString("uuid"));
				}

			}

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return toret;
	}

	@Override
	public void insert(String UUID, String content) {
		String sql = "INSERT INTO HTML (uuid, content)" + "VALUES (?, ?)";

		try {

			Connection connection = DriverManager.getConnection(db_url, db_user, db_password);

			try (PreparedStatement statement = connection.prepareStatement(sql);
					Connection connectionClose = connection) {

				statement.setString(1, UUID);
				statement.setString(2, content);

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
		String sql = "DELETE FROM HTML WHERE uuid = ?";

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
