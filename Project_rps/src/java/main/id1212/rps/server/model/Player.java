package java.main.id1212.rps.server.model;

import java.io.Serializable;

public class Player implements Serializable {
    private String id;
    private String host;
    private int port;

    private String currentChoice;
    private int rounds;
    private int roundScore;
    private int totalScore;

    public Player(String id, String host, int port){
        this.id = id;
        this.host = host;
        this.port = port;

        this.currentChoice = null;
        this.rounds = 0;
        this.roundScore = 0;
        this.totalScore = 0;
    }

    public Player(String host, int port){
        this(host + ":" + port, host, port);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getCurrentChoice() {
        return currentChoice;
    }

    public void setCurrentChoice(String currentChoice) {
        this.currentChoice = currentChoice;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds += rounds;
    }

    public int getRoundScore() {
        return roundScore;
    }

    public void setRoundScore(int roundScore) {
        this.roundScore = roundScore;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore += totalScore;
    }

   public void resetRoundScore(){
        this.roundScore = 0;
   }

   public void resetTotalScore(){
        this.totalScore = 0;
   }

    @Override
    public String toString(){
        return id;
    }
}
