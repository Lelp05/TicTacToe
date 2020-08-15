package de.lelp.net;

import de.lelp.main.Game;
import de.lelp.network.DataPackage;

import java.util.Timer;
import java.util.TimerTask;

public class Server extends de.lelp.network.Server {

    private Timer gameThread;

    private int[] gameField = { -1, -1, -1,
                                -1, -1, -1,
                                -1, -1, -1};

    private String playerOnTurn = "Player-1";

    public Server(int port, long pingInterval, boolean muted, boolean logging) {
        super(port, pingInterval, muted, logging);

        startGameThread();
    }

    @Override
    public void registerMethods() {

        methods.put("makeTurn", (dataPackage, networkClient) -> {
            if(networkClient.getUuid().equals(playerOnTurn)){

                //update the gameField
                if(networkClient.getUuid().equals("Host")){
                    gameField[(int) dataPackage.get(0)] = 0;

                    //set the player that's now on turn
                    playerOnTurn = "Player-1";
                }else{
                    gameField[(int) dataPackage.get(0)] = 1;

                    //set the player that's now on turn
                    playerOnTurn = "Host";
                }

                //broadcast the new game field
                broadcastPackage(new DataPackage("drawGameField", gameField, playerOnTurn));

                //check if someone won the game
                generalWin();

                networkClient.sendOk();

            }else{
                networkClient.reply(400, "it's not your turn");
            }
        });

        methods.put("reset", (dataPackage, networkClient) -> reset());

    }

    @Override
    public void onClientRegistered(NetworkClient client) {
        if(clients.size() == 2){

            //start the game
            broadcastPackage(new DataPackage("drawGameField", gameField, playerOnTurn));
        }
    }

    @Override
    public void onClientDisconnect(NetworkClient client) {
        //a client disconnected stop the game
        Game.stopGame();
    }

    private void generalWin(){

        //check if the host won
        if(checkWinner(0)){
            broadcastPackage(new DataPackage("gameWin", "Win", "Host"));
        }

        //check if the player-1 won
        if(checkWinner(1)){
            broadcastPackage(new DataPackage("gameWin", "Win", "Player-1"));
        }

        //check if it is a draw
        if(!checkWinner(0) && !checkWinner(1) && isFull()){
            broadcastPackage(new DataPackage("gameWin", "Draw", "Both"));
        }
    }

    private boolean checkWinner(int state){

        //horizontal
        if(gameField[0] == state && gameField[1] == state && gameField[2] == state) return true;
        if(gameField[3] == state && gameField[4] == state && gameField[5] == state) return true;
        if(gameField[6] == state && gameField[7] == state && gameField[8] == state) return true;

        //vertical
        if(gameField[0] == state && gameField[3] == state && gameField[6] == state) return true;
        if(gameField[1] == state && gameField[4] == state && gameField[7] == state) return true;
        if(gameField[2] == state && gameField[5] == state && gameField[8] == state) return true;

        //sideway
        if(gameField[0] == state && gameField[4] == state && gameField[8] == state) return true;
        if(gameField[2] == state && gameField[4] == state && gameField[6] == state) return true;

        return false;
    }

    private boolean isFull(){

        //check if at least one field is empty
        for (int i = 0; i < 9; i++) {
            if(gameField[i] == -1){
                return false;
            }
        }

        //if not return true
        return true;
    }

    private void reset(){
        //reset the game field
        gameField = new int[]{ -1, -1, -1, -1, -1, -1, -1, -1, -1};

        //send the new field to the players
        broadcastPackage(new DataPackage("drawGameField", gameField, playerOnTurn));
    }

    private void startGameThread(){
        gameThread = new Timer();
        gameThread.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                try{

                    if(clients.size() <= 1){
                        System.out.println("Wait for another player");
                    }

                }catch (Exception e){
                    Game.stopGame();
                }

            }
        }, 0, 10 * 1000);
    }


}
