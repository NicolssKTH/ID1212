package server.controller;

import common.Catalog;
import common.ClientRemote;
import common.FileDTO;
import common.UserDTO;

import org.mindrot.jbcrypt.BCrypt;
import server.integration.FileDAO;
import server.integration.UserDAO;
import server.model.File;
import server.model.FileHandler;
import server.model.User;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;

public class Controller extends UnicastRemoteObject implements Catalog {
    private final UserDAO userDAO;
    private final FileDAO fileDAO;
    private HashMap<Integer, ClientRemote> notifiableUsers = new HashMap();

    public Controller() throws RemoteException{
        super();
        this.userDAO = new UserDAO();
        this.fileDAO = new FileDAO();
    }

    @Override
    public void registerUser(String username, String password) throws RemoteException{
        if (userDAO.findUser(username) == null){
            userDAO.storeUser(new User(username, password));
        }else {
            throw new RemoteException("The username " + username + " is already taken");
        }
    }

    @Override
    public void unregisterUser(String username, String password) {
        User user = userDAO.findUser(username);
        if (user != null && BCrypt.checkpw(password, user.getPassword())){
            userDAO.destroyUser(user);
        }
    }

    @Override
    public UserDTO loginUser(String username, String password) throws RemoteException {
        User user = userDAO.findUser(username);
        if (user != null && BCrypt.checkpw(password,user.getPassword())){
            return user;
        }else {
            throw new RemoteException("Invalid username or password");
        }
    }

    @Override
    public List<? extends FileDTO> findAllFiles(UserDTO owner){
        User user = owner == null ? null : userDAO.findUser(owner.getUsername());
        return fileDAO.findAllFiles(user);
    }

    @Override
    public List<? extends FileDTO> findAllFiles() {
        return findAllFiles(null);
    }

    @Override
    public void storeFile(UserDTO owner, String name, byte[] content, boolean privateAccess, boolean publicWrite, boolean publicRead) throws IOException {
        if (fileDAO.findFile(name) == null){
            fileDAO.storeFile(new File(userDAO.findUser(owner.getUsername()), name, privateAccess, publicWrite, publicRead, content.length));
            FileHandler.persistFile(name, content);
        }else {
            throw new RemoteException(name + " already exits");
        }
    }

    @Override
    public byte[] getFile(UserDTO userDTO, String name) throws IOException {
        User user = userDAO.findUser(userDTO.getUsername());
        File file = fileDAO.findFile(name);

        if (file == null){
            throw new RemoteException(name + " doesn't exist");
        }else if (file.getOwner().getId() == user.getId() || (!file.hasPrivateAccess() && file.hasReadPermission())){
            if (notifiableUsers.containsKey(file.getId())) notifyUser(file.getId(), file.getName(), "downloaded");
            return FileHandler.getFile(name);
        }else {
            throw new RemoteException("You don't have permission to download this file");
        }
    }

    @Override
    public void updateFile(UserDTO owner, String name, byte[] content, boolean privateAccess, boolean publicWrite, boolean publicRead) throws IOException {
        User user = userDAO.findUser(owner.getUsername());
        File file = fileDAO.findFile(name);

        if (file == null){
            throw new RemoteException("You don't have permission to uppdate this file");
        }else if (file.getOwner().getId() == user.getId() || (!file.hasPrivateAccess() && file.hasWritePermission())){
            fileDAO.updateFile(new File(userDAO.findUser(owner.getUsername()), name, privateAccess, publicWrite, publicRead, content.length));
            if (notifiableUsers.containsKey(file.getId())) notifyUser(file.getId(), file.getName(), "updated");
            FileHandler.persistFile(name, content);
        }else{
            throw new RemoteException("Update denied, you dont have permission");
        }
    }

    @Override
    public void deleteFile(UserDTO userDTO, String name) throws IOException {
        User user = userDAO.findUser(userDTO.getUsername());
        File file = fileDAO.findFile(name);

        if (file == null) {
            throw new RemoteException(name + " doesn't exist");
        } else if (file.getOwner().getId() == user.getId() || (!file.hasPrivateAccess() && file.hasWritePermission())) {
            fileDAO.destroyFile(file);
            if (notifiableUsers.containsKey(file.getId())) notifyUser(file.getId(), file.getName(), "deleted");
            FileHandler.deleteFile(name);
        }else{
            throw new RemoteException("You dont han permission to delete this file");
        }
    }

    private void notifyUser(int fileId, String fileName, String action)throws RemoteException {
        ClientRemote outputHandeler = notifiableUsers.get(fileId);
        outputHandeler.outputMassage("A user has " + action + " the file " + fileName);
    }

    @Override
    public void notify(UserDTO userDTO, String name, ClientRemote outputHandeler) throws RemoteException {
        System.out.println("works?");
        User user = userDAO.findUser(userDTO.getUsername());
        File file = fileDAO.findFile(name);

        if (file == null){
            throw new RemoteException(name + " dosent exist");
        }else if (file.getOwner().getId() == user.getId()){
            notifiableUsers.put(file.getId(),outputHandeler);
        }else{
            throw new RemoteException("Permission denied");
        }
    }
}
