package client.view;

import client.Utility;
import common.Catalog;
import common.ClientRemote;
import common.FileDTO;
import common.UserDTO;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CatalogShell implements Runnable{
    private static final String PROMT = ">> ";
    private final SafePrint outMgr = new SafePrint();
    private final Scanner scanner = new Scanner(System.in);
    private NotificationServer outpuHandler;
    private Catalog catalog;
    private UserDTO user = null;
    private ArrayList<String> arrlist;
    private boolean running = false;

    public void start(Catalog catalog){
        this.catalog = catalog;

        try{
            this.outpuHandler = new NotificationServer();
        }catch (RemoteException e){
            e.printStackTrace();
        }

        if (running) return;
        running = true;

        new Thread(this).start();
    }

    @Override
    public void run(){
        outMgr.print("Welcome to the Catalog Shell!!");
        outMgr.println("");
        outMgr.print(PROMT);

        while (running) {
            try{
                String readLine = scanner.nextLine();
                arrlist = parseLine(readLine);
                Command cmd = checkCommand(arrlist);

                if (user == null && !cmd.equals(Command.REGISTER) &&
                        !cmd.equals(Command.UNREGISTER) &&
                        !cmd.equals(Command.LOGIN) &&
                        !cmd.equals(Command.QUIT) &&
                        !cmd.equals(Command.HELP) &&
                        !cmd.equals(Command.NOCOMMAND)
                ){
                    outMgr.println("Please login to use the file catalog");
                    outMgr.print(PROMT);
                    continue;
                }
                switch (cmd){
                    case REGISTER:
                        catalog.registerUser(arrlist.get(1), arrlist.get(2));
                        outMgr.println("User " + arrlist.get(1) + " is now registered");
                        break;
                    case UNREGISTER:
                        catalog.unregisterUser(arrlist.get(1), arrlist.get(2));
                        this.user = null;
                        outMgr.println("User" + arrlist.get(1) + " is now unregistered");
                        break;
                    case LOGIN:
                        this.user = catalog.loginUser(arrlist.get(1), arrlist.get(2));
                        outMgr.println("User" + arrlist.get(1) + " is now logged in!");
                        break;
                    case LOGOUT:
                        this.user = null;
                        outMgr.println("You are now logged out");
                        break;
                    case STOREFILE:
                        if (user != null){
                            byte[] data = Utility.readFile(arrlist.get(1));
                            catalog.storeFile(this.user, arrlist.get(1), data,
                                            Boolean.parseBoolean(arrlist.get(2)),
                                            Boolean.parseBoolean(arrlist.get(3)),
                                            Boolean.parseBoolean(arrlist.get(4)));
                            outMgr.println("File uploaded!");
                        }else{
                            outMgr.println("Failed to upload");
                        }
                        break;
                    case LISTFILE:
                        List<? extends FileDTO> list = user != null ? catalog.findAllFiles(user) : catalog.findAllFiles();
                        outMgr.println("The catalog contains the following files:");
                        outMgr.println("NAME (size) , PRIVATE-WRITE-READ");
                        for (FileDTO file : list){
                            outMgr.println(file.getName() + "(" + file.getDimension() + " Bytes) , " + file.hasPrivateAccess()
                            + "-" + file.hasWritePermission() + "-" + file.hasReadPermission());
                        }
                        break;
                    case DELETEFILE:
                        if (this.user != null){
                            catalog.deleteFile(user, arrlist.get(1));
                            outMgr.println("File deleted");
                        }else{
                            outMgr.println("You have to be logged in på delete files");
                        }
                        break;
                    case GETFILE:
                        if (this.user != null){
                            Utility.writeFile(arrlist.get(1), catalog.getFile(user, arrlist.get(1)));
                            outMgr.println("File Downloaded");
                        }else{
                            outMgr.println("You have to be logged in to download");
                        }
                        break;
                    case UPDATEFILE:
                        if (this.user != null){
                            byte[] data = Utility.readFile(arrlist.get(1));
                            catalog.updateFile(this.user, arrlist.get(1), data,
                                    Boolean.parseBoolean(arrlist.get(2)),
                                    Boolean.parseBoolean(arrlist.get(3)),
                                    Boolean.parseBoolean(arrlist.get(4)));
                            outMgr.println("File updated");
                        }else{
                            outMgr.println("You have to be logged in to update file");
                        }
                        break;
                    case NOTIFY:
                        if (this.user != null){
                            catalog.notify(this.user, arrlist.get(1), this.outpuHandler);
                        }else{
                            outMgr.println("you have to be logged in to be notified");
                        }
                        break;
                    case HELP:
                        outMgr.println("FILE CATALOG HELP MENU \n" +
                                "REGISER <username> <password>\n" +
                                "UNREGISER <username> <password>\n" +
                                "LOGIN <username> <password>\n" +
                                "LOGOUT\n" +
                                "STOREFILE <username> <private(t/f)> <read(t/f)> <write(t/f)>\n" +
                                "UPDATAFILE <username> <private(t/f)> <read(t/f)> <write(t/f)>\n" +
                                "GETFILE <filename>\n" +
                                "DELETEFILE <filename>\n" +
                                "LISTFILE\n" +
                                "NOTIFY\n" +
                                "HELP");
                        break;
                    case QUIT:
                        running = false;
                        break;
                    case NOCOMMAND:
                        break;

                }
                outMgr.print(PROMT);
            }catch (IllegalArgumentException | IOException e){
                e.printStackTrace();
                outMgr.println("Command error" + e.getMessage());
                outMgr.print(PROMT);
            }
        }
    }

    private ArrayList<String> parseLine(String input){
        ArrayList<String> list = new ArrayList<>();
        if (input.isEmpty()){
            return null;
        }
        for (String subString : input.split(" ")) {
            list.add(subString);
        }
        return list;

    }

    private Command checkCommand(ArrayList<String> list){

        String cmd = list.get(0);

        switch (cmd){
            case "REGISTER":
                if (list.size()== 3){
                    return Command.REGISTER;
                }else{
                    outMgr.println("Invalid REGISTER parameters, try REGISTER <username> <password>");
                    return Command.NOCOMMAND;
                }

            case "UNREGISTER":
                if (list.size() == 3){
                    return Command.UNREGISTER;
                }else{
                    outMgr.println("Invalid UNREGISTER parameters, try UNREGISTER <username> <password>");
                    return Command.NOCOMMAND;
                }

            case "LOGIN":
                if (list.size() == 3){
                    return Command.LOGIN;
                }else{
                    outMgr.println("Invalid LOGIN parameters, try LOGIN <username> <password>");
                    return Command.NOCOMMAND;
                }

            case "LOGOUT":
                if (list.size() == 1){
                    return Command.LOGOUT;
                }else{
                    outMgr.println("Type LOGOUT to logout");
                    return Command.NOCOMMAND;
                }

            case "STOREFILE":
                if (list.size() == 5){
                    return Command.STOREFILE;
                }else{
                    outMgr.println("Invalid STOREFILE parameters, try STOREFILE <Filename> <privateAccess(t/f)> <writePermission(t/f)> <readPermission(t/f)>");
                    return Command.NOCOMMAND;
                }

            case "GETFILE":
                if (list.size() == 2){
                    return Command.GETFILE;
                }else{
                    outMgr.println("Invalid GETFILE parameters, try GETFILE <Filename>");
                    return Command.NOCOMMAND;
                }

            case "LISTFILE":
                if (list.size() == 1){
                    return Command.LISTFILE;
                }else{
                    outMgr.println("To list files, use LISTFILE");
                    return Command.NOCOMMAND;
                }

            case "DELETEFILE":
                if (list.size() == 2){
                    return Command.DELETEFILE;
                }else{
                    outMgr.println("Invalid DELETEFILE parameters, try DELETEFILE <Filename>");
                    return Command.NOCOMMAND;
                }

            case "UPDATEFILE":
                if (list.size() == 5){
                    return Command.UPDATEFILE;
                }else{
                    outMgr.println("Invalid UPDATEFILE parameters, try UPDATEFILE <Filename> <privateAccess(t/f)> <writePermission(t/f)> <readPermission(t/f)>");
                    return Command.NOCOMMAND;
                }

            case "NOTIFY":
                if (list.size() == 2){
                    return Command.NOTIFY;
                }else{
                    outMgr.println("Invalid NOTIFY parameters, try NOTIFY <Filename>");
                    return Command.NOCOMMAND;
                }

            case "HELP":
                if (list.size() == 1){
                    return Command.HELP;
                }

            case "QUIT":
                if (list.size() == 1){
                    return Command.QUIT;
                }
            default:
                return Command.NOCOMMAND;

        }
    }


    private class NotificationServer extends UnicastRemoteObject implements ClientRemote{
        public NotificationServer() throws RemoteException{
        }

        @Override
        public void outputMassage(String message) throws RemoteException {
            outMgr.println(message);
            outMgr.print(PROMT);
        }
    }

}

