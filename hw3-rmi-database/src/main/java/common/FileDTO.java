package common;

import server.model.User;

import java.io.Serializable;
import java.util.Date;

public interface FileDTO extends Serializable {
    User getOwner();

    String getName();

    boolean hasPrivateAccess();

    boolean hasWritePermission();

    boolean hasReadPermission();

    long getDimension();
}
