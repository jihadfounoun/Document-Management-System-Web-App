package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Folder;

public class FolderDAO {
	private Connection connection;
	
	public FolderDAO(Connection connection) {
		this.connection = connection;
	}
	public Folder findFolderById(int id) throws SQLException{
		Folder folder=null;
		PreparedStatement statement=null;
		ResultSet result=null;
		String query="SELECT * FROM document_management_system.folder WHERE id= ? ";
		try {
			statement=connection.prepareStatement(query);
			statement.setInt(1,id);
			result=statement.executeQuery();
			while(result.next()) {
				folder=new Folder(result.getInt("id"),result.getInt("owner_id"),result.getString("name"),
						result.getInt("parent_folder_id"),result.getDate("date"));
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
		return folder;
	}
	public List<Folder> getFolderByUserId(int ownerId) throws SQLException{
		List<Folder> folders=new ArrayList<>();
		PreparedStatement statement=null;
		ResultSet result=null;
		String query="SELECT * FROM document_management_system.folder WHERE owner_id= ? ";
		try {
			statement=connection.prepareStatement(query);
			statement.setInt(1,ownerId);
			result=statement.executeQuery();
		
			while(result.next()) {
				folders.add(new Folder(result.getInt("id"),result.getInt("owner_id"),result.getString("name"),
						result.getInt("parent_folder_id"),result.getDate("date"))
						);
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
		return folders;
		
	}
	public void createFolder(int fatherId,int ownerId,String name) throws SQLException{
		if(fatherId==0) {
			createMainFolder(ownerId,name);
		}else
			createChildFolder(fatherId,ownerId,name);
	}
	public void createChildFolder(int fatherId,int ownerId,String name) throws SQLException {
		PreparedStatement statement=null;
		String query="INSERT INTO document_management_system.folder (parent_folder_id,owner_id,name) values (?,?,?)";
		try {
			statement=connection.prepareStatement(query);
			statement.setInt(1,fatherId);
			statement.setInt(2,ownerId);
			statement.setString(3,name);
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
	public void createMainFolder(int ownerId,String name) throws SQLException {
		PreparedStatement statement=null;
		String query="INSERT INTO document_management_system.folder (owner_id,name) values (?,?)";
		try {
			statement=connection.prepareStatement(query);
			statement.setInt(1,ownerId);
			statement.setString(2,name);
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
	public List<Folder> getFolderByFatherFolderId(int id) throws SQLException {
		List<Folder> folders=new ArrayList<>();
		PreparedStatement statement=null;
		ResultSet result=null;
		String query="SELECT * FROM document_management_system.folder WHERE parent_folder_id= ? ";
		try {
			statement=connection.prepareStatement(query);
			statement.setInt(1,id);
			result=statement.executeQuery();
		
			while(result.next()) {
				folders.add(new Folder(result.getInt("id"),result.getInt("owner_id"),result.getString("name"),
						result.getInt("parent_folder_id"),result.getDate("date")));
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
		return folders;
	}
	public void moveFolder(int folderId,int fatherId) throws SQLException{
		PreparedStatement statement=null;
		//in caso si mette nella query string =null
		String query="UPDATE document_management_system.folder SET parent_folder_id=? WHERE id=?";
		try {
			statement=connection.prepareStatement(query);
			statement.setInt(1,fatherId);
			statement.setInt(2,folderId);
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
	public boolean isNameValid(String name,int parentFolderId, int userId) throws SQLException  {
		PreparedStatement statement=null;
		if(parentFolderId==0)
			return isNameValid(name,userId);
		String query="SELECT * FROM document_management_system.folder WHERE name= ? and parent_folder_id= ? and owner_id= ?";
		ResultSet result=null;
		try {
			statement=connection.prepareStatement(query);
			statement.setString(1,name);
			statement.setInt(2,parentFolderId);
			statement.setInt(3,userId);
			result=statement.executeQuery();
			if(result.next())
				return false;
			return true;
		} catch (SQLException e) {
			throw new SQLException();
		}
	}
	
	public boolean isNameValid(String name,int userId) throws SQLException  {
		PreparedStatement statement=null;
		String query="SELECT * FROM document_management_system.folder WHERE name= ? and parent_folder_id IS NULL and owner_id= ?";
		ResultSet result=null;
		try {
			statement=connection.prepareStatement(query);
			statement.setString(1,name);
			statement.setInt(2,userId);
			result=statement.executeQuery();
			if(result.next())
				return false;
			return true;
		} catch (SQLException e) {
			throw new SQLException();
		}
	}
	
	
	public boolean isFolderOwnedByUser(int folderId,int userId) throws SQLException {
		List<Folder> folders=getFolderByUserId(userId);
		
		for(Folder f: folders) {
			if(f.getId()==folderId)
				return true;
		}
		return false;
		//return getFolderByUserId(userId).stream().anyMatch((x)->x.getId()==folderId);
	}
	
}
