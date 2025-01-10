package beans;

import java.util.List;

public class FoldersAndDocuments{
	private final List<Folder>  folders;
	private final List<Document> documents;
	
	public FoldersAndDocuments(List<Folder> f,List<Document> docs) {
		this.documents=docs;
		this.folders=f;
	}
	
	List<Document> getDocuments(){
		return documents;
	}
	
	List<Folder> getFolder() {
		return folders;
	}

}
