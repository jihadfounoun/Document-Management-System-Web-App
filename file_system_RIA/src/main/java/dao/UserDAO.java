package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import beans.User;

public class UserDAO {
	
	private Connection connection;

	public UserDAO(Connection connection) {
		this.connection = connection;
	}

	public User findUser(String username, String password) throws SQLException {
		User user=null;
		PreparedStatement statement=null;
		ResultSet result=null;
		String query="SELECT * FROM document_management_system.user WHERE username = ? and password = ? ;";
		try {
			statement=this.connection.prepareStatement(query);
			statement.setString(1, username);
			statement.setString(2, password);
			result=statement.executeQuery();
			while(result.next()) {
				user = new User(result.getInt("id"),result.getString("email"),
						result.getString("username"),result.getString("name"));
			}
			
		} catch (SQLException e) {
			// errore nell'accesso al db
			throw new SQLException();
		}finally {
			if(statement!=null)
				try {
					statement.close();
				} catch (SQLException e) {
					throw new SQLException();
				}
			if(result!=null)
				try {
					result.close();
				} catch (SQLException e) {
					throw new SQLException();
				}
			
		}
		return user;
	}
	public User getUserByEmail(String email) throws SQLException {
		User user=null;
		PreparedStatement statement=null;
		ResultSet result=null;
		String query="SELECT * FROM document_management_system.user WHERE email= ? ";
		try {
			statement=connection.prepareStatement(query);
			statement.setString(1, email);
			result=statement.executeQuery();
			while(result.next()) {
				user = new User(result.getInt("id"),result.getString("email"),
						result.getString("username"),result.getString("name"));
			}
		} catch (SQLException e) {
			throw new SQLException();
		}finally {
			if(statement!=null)
				try {
					statement.close();
				} catch (SQLException e) {
					throw new SQLException();
				}
			if(result!=null)
				try {
					result.close();
				} catch (SQLException e) {
					throw new SQLException();
				}
			
		}
		
		return user;
	}
	
	public User getUserById(int id) throws SQLException {
		User user=null;
		PreparedStatement statement=null;
		ResultSet result=null;
		String query="SELECT * FROM document_management_system.user WHERE id=?";
		try {
			statement=connection.prepareStatement(query);
			statement.setInt(1, id);
			result=statement.executeQuery();
			while(result.next()) {
				user = new User(result.getInt("id"),result.getString("email"),
						result.getString("username"),result.getString("name"));
			}
		} catch (SQLException e) {
			throw new SQLException();
		}finally {
			if(statement!=null)
				try {
					statement.close();
				} catch (SQLException e) {
					throw new SQLException();
				}
			if(result!=null)
				try {
					result.close();
				} catch (SQLException e) {
					throw new SQLException();
				}
			
		}
		
		return user;
	}
	public User getUserByUsername(String username) throws SQLException {
		User user=null;
		PreparedStatement statement=null;
		ResultSet result=null;
		String query="SELECT * FROM document_management_system.user WHERE username=?";
		try {
			statement=connection.prepareStatement(query);
			statement.setString(1, username);
			result=statement.executeQuery();
			while(result.next()) {
				user = new User(result.getInt("id"),result.getString("email"),
						result.getString("username"),result.getString("name"));
			}
		} catch (SQLException e) {
			throw new SQLException();
		}finally {
			if(statement!=null)
				try {
					statement.close();
				} catch (SQLException e) {
					throw new SQLException();
				}
			if(result!=null)
				try {
					result.close();
				} catch (SQLException e) {
					throw new SQLException();
				}
			
		}
		
		return user;
	}
	public void registerUser(String name,String email,String username,String password) throws SQLException{
		PreparedStatement statement=null;
		String query="INSERT INTO document_management_system.user (name,username,password,email) values (?,?,?,?)";
		try {
			statement=connection.prepareStatement(query);
			statement.setString(1,name);
			statement.setString(2,username);
			statement.setString(3,password);
			statement.setString(4,email);
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException();
		}finally {
			if(statement!=null)
				try {
					statement.close();
				} catch (SQLException e) {
					throw new SQLException();
				}
			
		}
	}
	

}
