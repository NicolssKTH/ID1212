package java.main.id1212.rps.common;

import java.io.Serializable;
import java.main.id1212.rps.server.model.Player;
import java.util.Collection;
import java.util.HashMap;

public class PlayerTable implements Serializable {
    private HashMap<String, Player> playerTable;

    public PlayerTable(){
        this.playerTable = new HashMap<>();
    }

    public void addPlayer(Player player){
        this.playerTable.put(player.getId(), player);
    }

    public void removePlayer(String id){
        this.playerTable.remove(id);
    }

    public Collection<Player> getPlayers(){
        return this.playerTable.values();
    }

    public HashMap<String, Player> getPlayerTable() {
        return playerTable;
    }

    public int getTableSize(){
        return playerTable.size();
    }

    public void replacePlayer(Player player){
        this.playerTable.replace(player.getId(), player);
    }

    public void resetPlayersMoves(){
        for (Player player : this.playerTable.values()){
            player.setCurrentChoice(null);
        }
    }

    public boolean allPlayersPlayed(){
        boolean allPlayed = true;

        for (Player player : this.playerTable.values()){
            if (player.getCurrentChoice() == null){
                allPlayed = false;
            }
        }
        return allPlayed;
    }

    public void setPlayerMove(Player player, String move){
        playerTable.get(player.getId()).setCurrentChoice(move);
    }

    @Override
    public String toString(){
        return "PlayerTable: {" + playerTable + "}";
    }

}
