package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientRemote extends Remote {
    void outputMassage(String message)throws RemoteException;
}
