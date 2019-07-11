package server.model;

import common.UserDTO;
import org.hibernate.annotations.NaturalId;
import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class User implements Serializable, UserDTO {

    @Id
    @GeneratedValue
    private int id;

    @NaturalId
    private String username;

    private String password;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<File> files = new ArrayList<>();

    private Date createdAt;
    private Date updatedAt;

    public User(){
    }

    public User(String username, String password){
        this.username = username;
        this.setPassword(password);
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


    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = BCrypt.hashpw(password, BCrypt.gensalt(10));;
    }

}
