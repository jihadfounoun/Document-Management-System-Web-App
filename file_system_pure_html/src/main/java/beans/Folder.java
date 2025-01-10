package beans;

import java.util.Date;

public class Folder {
    private final int id,ownerId,parentFolderId;
    private final String name;
    private final Date date;

    public Folder(int folderId,int ownerId,String folderName,int parentFolderId,Date date) {
        this.id=folderId;
        this.ownerId=ownerId;
        this.name=folderName;
        this.parentFolderId=parentFolderId;
        this.date=date;
    }

    public int getParentFolderId() {
        return parentFolderId;
    }

    public String getName() {
        return name;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public int getId() {
        return id;
    }
    //serviva per il debugging
    public String toString() {
        return "Folder{" +
                "id=" + id +
                ", ownerId=" + ownerId +
                ", name='" + name + '\'' +
                ", parentFolderId=" + parentFolderId +
                ", date=" + date +
                '}';
    }
    public Date getDate() {
    	return date;
    }

}
