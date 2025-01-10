package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import beans.Document;
import beans.Folder;
import beans.User;

public class DocumentDAO {
	private Connection connection;
	//id,description, parent_folder_id,type,date
	//new Date(resultSet.getTimestamp("timestamp").getTime())
	public DocumentDAO(Connection connection) {
		this.connection=connection;
	}
	public Document getDocumentById(int id) throws SQLException {
		Document doc=null;
		PreparedStatement statement=null;
		ResultSet result=null;
		String query="SELECT * FROM document_management_system.document WHERE id=?";
		try {
			statement=connection.prepareStatement(query);
			statement.setInt(1, id);
			result=statement.executeQuery();
			while(result.next()) {
				doc = new Document(result.getInt("id"),
						result.getInt("parent_folder_id"),
						result.getString("name"),
						result.getString("description"),
						result.getString("type"),
						result.getDate("date"));
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
		return doc;
	}
	//ritorna la lista dei folder di un utente
	public List<Document> getDocumentByFatherFolderId(int folderId) throws SQLException {
		List<Document> docs=new ArrayList<>();
		PreparedStatement statement=null;
		ResultSet result=null;
		String query="SELECT * FROM document_management_system.document WHERE parent_folder_id= ? ";
		try {
			statement=connection.prepareStatement(query);
			statement.setInt(1,folderId);
			result=statement.executeQuery();
		
			while(result.next()) {
				docs.add(new Document(result.getInt("id"),
						result.getInt("parent_folder_id"),
						result.getString("name"),
						result.getString("description"),
						result.getString("type"),
						result.getDate("date")));}
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
		return docs;
	}
	
	//modifica la crtella padre del folder
	public void moveDocument(int documentId,int folderId) throws SQLException {
		PreparedStatement statement=null;
		//in caso si mette nella query string =null
		String query="UPDATE document_management_system.document SET parent_folder_id=? WHERE id=?";
		try {
			statement=connection.prepareStatement(query);
			statement.setInt(1,folderId);
			statement.setInt(2,documentId);
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
	public boolean isNameValid(String name,int parentFolderId) throws SQLException  {
		PreparedStatement statement=null;
		String query="SELECT * FROM document_management_system.document WHERE name= ? and parent_folder_id= ? ";
		ResultSet result=null;
		try {
			statement=connection.prepareStatement(query);
			statement.setString(1,name);
			statement.setInt(2,parentFolderId);
			result=statement.executeQuery();
			if(result.next())
				return false;
			return true;
		} catch (SQLException e) {
			throw new SQLException();
		}
	}
	public void createDocument(int fatherId,String name,String desc,String type) throws SQLException{
		PreparedStatement statement=null;
		String query="INSERT INTO document_management_system.document (name,description,parent_folder_id,type) values (?,?,?,?)";
		try {
			statement=connection.prepareStatement(query);
			statement.setString(1,name);
			statement.setString(2,desc);
			statement.setInt(3,fatherId);
			statement.setString(4,type);
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
	public void deleteDocumentsByParenteFolderId(int parentId) throws SQLException {
		PreparedStatement statement=null;
		String query=" DELETE FROM document_management_system.document WHERE parent_folder_id= ? ";
		try {
			statement=connection.prepareStatement(query);
			statement.setInt(1,parentId);
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException();
		}finally {
			if(statement!=null)
				try {
					statement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					throw new SQLException();
				}
			
		}
	}
	public void deleteDocument(int id) throws SQLException{
		PreparedStatement statement=null;
		String query=" DELETE FROM document_management_system.document WHERE id= ? ";
		try {
			statement=connection.prepareStatement(query);
			statement.setInt(1,id);
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
