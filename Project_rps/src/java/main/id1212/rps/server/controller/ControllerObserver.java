package java.main.id1212.rps.server.controller;


import java.main.id1212.rps.server.model.Player;

public interface ControllerObserver {

    public void addPlayer(Player player);
    public  void removePlayer(Player player);
    public Player getPlayer();
    public void setPlayerMove(String move, Player player);

}