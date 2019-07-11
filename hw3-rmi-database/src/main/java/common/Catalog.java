package common;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Catalog extends Remote {

    void registerUser(String username, String password) throws RemoteException;

    void unregisterUser(String username, String password) throws RemoteException;

    UserDTO loginUser(String username, String password) throws RemoteException;

    List<? extends FileDTO> findAllFiles(UserDTO owner) throws RemoteException;

    List<? extends FileDTO> findAllFiles() throws RemoteException;

    void storeFile(UserDTO owner, String name, byte[] content, boolean privateAccess, boolean publicWrite, boolean publicRead) throws IOException;

    byte[] getFile(UserDTO user, String name) throws IOException;

    void updateFile(UserDTO owner, String name, byte[] content, boolean privateAccess, boolean publicWrite, boolean publicRead)throws IOException;

    void deleteFile(UserDTO user, String name) throws IOException;

    void notify(UserDTO user, String name, ClientRemote outputHandeler) throws RemoteException;
}
