package client;

import client.view.CatalogShell;
import common.Catalog;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Startup {
    public static void main(String[] args) {
        try {
            Catalog catalog = (Catalog) Naming.lookup("catalog");
            new CatalogShell().start(catalog);
        }catch (NotBoundException | MalformedURLException | RemoteException e){
            System.out.println("Catalog shell failed to start");
        }
    }
}
