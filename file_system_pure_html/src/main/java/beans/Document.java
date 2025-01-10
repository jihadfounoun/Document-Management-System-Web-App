package beans;

import java.util.Date;

public class Document {
    private final int id,folderId;
    private final String name,description,type;
    private final Date date;
    public Document(int documentId,int folderId,String documentName,String description,String type,Date timestamp) {
        this.id=documentId;
        this.folderId=folderId;
        this.name=documentName;
        this.description=description;
        this.type=type;
        this.date=timestamp;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getFolderId() {
        return folderId;
    }

    public int getId() {
        return id;
    }
    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Document{" +
                "id=" + id +
                ", folderId=" + folderId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", timestamp=" + date +
                '}';
    }

    public String getType() {
        return type;
    }
}
