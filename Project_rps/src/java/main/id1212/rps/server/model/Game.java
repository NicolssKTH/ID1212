package java.main.id1212.rps.server.model;

import java.main.id1212.rps.common.PlayerTable;

public class Game {

    public static String checkTurnResult(PlayerTable playerTable, Player currentPlayer){
        String result ="";

        if (playerTable.allPlayersPlayed() && currentPlayer.getCurrentChoice() != null){
            calculateScore(playerTable, currentPlayer);
            result = "Current Round Score: " + currentPlayer.getRounds() + " - Total score: " + currentPlayer.getTotalScore();
            playerTable.resetPlayersMoves();
            currentPlayer.setCurrentChoice(null);
            currentPlayer.resetRoundScore();
        }
        return result;
    }

    private static void calculateScore(PlayerTable playerTable, Player currentPlayer) {
        for (Player playerA : playerTable.getPlayers()){
            String moveA = playerA.getCurrentChoice();

            for (Player playerB : playerTable.getPlayers()){
                if (!playerB.getId().equals(playerA.getId())){
                    String moveB = playerB.getCurrentChoice();

                    if (moveA.equals("PAPER")){
                        playerA.setRoundScore(moveB.equals("ROCK") ? 1 : 0);
                    }
                    if (moveA.equals("ROCK")){
                        playerA.setRoundScore(moveB.equals("SCISSORS") ? 1 : 0);
                    }
                    if (moveA.equals("SCISSORS")){
                        playerA.setRoundScore(moveB.equals("PAPER") ? 1: 0);
                    }
                }
            }
            playerA.setTotalScore(playerA.getRoundScore());
            playerA.resetRoundScore();
        }
    }
}
