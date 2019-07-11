package server.model;

import common.FileDTO;
import org.hibernate.annotations.NaturalId;
import javax.persistence.*;
import java.util.Date;

@Entity
public class File implements FileDTO {

    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    private User owner;

    @NaturalId
    private String name;

    private boolean privateAccess;

    private boolean readPermission;

    private boolean writePermission;

    private long dimension;

    private Date createdAt;

    private Date updatedAt;

    public File(User owner, String name, boolean privateAccess, boolean writePermission, boolean readPermission, long dimension){
        this.owner = owner;
        this.name = name;
        this.privateAccess = privateAccess;
        this.readPermission = readPermission;
        this.writePermission = writePermission;
        this.dimension = dimension;
    }
    public File(){
    }

    @PrePersist
    private void onCreate(){
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    @PreUpdate
    private void onUpdate(){
        this.updatedAt = new Date();
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner){
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public boolean hasPrivateAccess(){
        return privateAccess;
    }

    public void setPrivateAccess(boolean privateAccess){
        this.privateAccess = privateAccess;
    }

    public boolean hasReadPermission(){
        return readPermission;
    }

    public void setReadPermission(boolean readPermission) {
        this.readPermission = readPermission;
    }

    public boolean hasWritePermission(){
        return writePermission;
    }

    public void setWritePermission(boolean writePermission) {
        this.writePermission = writePermission;
    }

    public long getDimension() {
        return dimension;
    }

    public void setDimension(long dimension) {
        this.dimension = dimension;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }
}
