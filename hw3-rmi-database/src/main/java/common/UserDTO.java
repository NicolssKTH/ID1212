package common;

import java.io.Serializable;


public interface UserDTO extends Serializable {

    int getId();

    String getUsername();

    String getPassword();



}
