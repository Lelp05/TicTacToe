package de.lelp.main;

import de.lelp.net.Client;
import de.lelp.net.Server;
import de.lelp.network.DataPackage;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Game {

    public static void drawMainMenu(){

        //draw the main menu
        System.out.println("Main Menu:");
        System.out.println("1. Join game");
        System.out.println("2. Create a Server");

        //wait for a response of the user
        try{

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input = reader.readLine();

            //check if the input was valid
            int option = Integer.parseInt(input);
            if(option == 1){
                joinGame();
            }else if(option == 2){
                createServer();
            }else{
                throw new IllegalArgumentException("Please enter a valid option");
            }

        }catch (Exception e){
            drawMainMenu();
        }


    }

    public static void createServer(){
        System.out.println("Enter the port on which the server should start (for example: 4000)");


        //wait for a response of the user
        try{

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input = reader.readLine();

            //check if the input was valid
            int port = Integer.parseInt(input);
            Main.server = new Server(port, 1000, true, false);

            //connect this user to the server
            Main.client = new Client("localhost", port, 1000, "Player", "Host", true, false);

        }catch (Exception e){
            drawMainMenu();
        }

    }

    public static void joinGame(){

        try{

            System.out.println("Enter the ip and the port of the game server ( 0.0.0.0:4000 )");

            //wait for a response of the user
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input = reader.readLine();

            //check if the input was valid
            String[] parts = input.split(":");

            if(parts.length == 2){

                int port = Integer.parseInt(parts[1]);

                //try to join the server
                Main.client = new Client(parts[0], port, 1000, "Player", "Player-1", true, false);

                if(!Main.client.isConnected()){
                    System.out.println("The server seems to be offline");
                    stopGame();
                }

            }else{
                throw new IllegalArgumentException();
            }


        }catch (Exception e){
            drawMainMenu();
        }

    }

    public static void stopGame(){

        //set the server instance to null
        if(Main.server != null){
            Main.server.stop();
            Main.server = null;
        }

        //set the client to null
        if(Main.client != null){
            Main.client.stop();
            Main.client = null;
        }

        System.out.println("The Game has been stopped");

        drawMainMenu();
    }

    public static void gameWin(DataPackage pack){
        if (pack.get(0).equals("Win")) {

            //print who won's
            if (pack.get(1).equals(Main.client.getUuid())) {
                System.out.println(" __ __                          ");
                System.out.println("|  |  |___ _ _    _ _ _ ___ ___ ");
                System.out.println("|_   _| . | | |  | | | | . |   |");
                System.out.println("  |_| |___|___|  |_____|___|_|_|");
                System.out.println("");
                System.out.println("Want to continue (Yes) (No)");

            } else {
                System.out.println(" __ __            _         _   ");
                System.out.println("|  |  |___ _ _   | |___ ___| |_ ");
                System.out.println("|_   _| . | | |  | | . |_ -|  _|");
                System.out.println("  |_| |___|___|  |_|___|___|_|  ");
                System.out.println("");
                System.out.println("Want to continue (Yes) (No)");
            }

        } else if (pack.get(0).equals("Draw")) {
            System.out.println("   _               ");
            System.out.println(" _| |___ ___ _ _ _ ");
            System.out.println("| . |  _| .'| | | |");
            System.out.println("|___|_| |__,|_____|");
            System.out.println("");
            System.out.println("Want to continue (Yes) (No)");
        }

        try{

            //wait for a input of the user
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input = reader.readLine();

            if(input.equalsIgnoreCase("Yes")){
                Main.client.sendRequest("reset");
            }else{
                System.exit(0);
            }

        }catch (Exception ignored){}
    }

    public static void waitForGameInput(int[] gameField, String playerOnTurn){


       try{

           //wait for a input of the user
           BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
           String input = reader.readLine();

           //check if the player wants to make a turn
           int field = Integer.parseInt(input);


           //check if the field exists and if the field is empty
           if(field <= 9 && gameField[field-1] == -1){

               Main.client.sendPackage(new DataPackage("makeTurn", (field-1)));

           }else{
               System.out.println("This field doesn't exist or is not empty");
               waitForGameInput(gameField, playerOnTurn);
           }


       }catch (Exception e){
           drawGameField(gameField, playerOnTurn);
       }

    }

    public static void drawGameField(int[] gameField, String playerOnTurn){

        if(playerOnTurn.equals(Main.client.getUuid())){
            System.out.println("\n It's your turn");
        }else{
            System.out.println("\n wait for the opponents turn");
        }

        System.out.println(
                "\n" +
                toPrintable(0, gameField) + " | " + toPrintable(1, gameField) + " | " + toPrintable(2, gameField) + "\n" +
                toPrintable(3, gameField) + " | " + toPrintable(4, gameField) + " | " + toPrintable(5, gameField) + "\n" +
                toPrintable(6, gameField) + " | " + toPrintable(7, gameField) + " | " + toPrintable(8, gameField) + "\n"
        );

        //let the player make a turn
        if(playerOnTurn.equals(Main.client.getUuid())) {
            System.out.println("1-9: make a turn");
            waitForGameInput(gameField, playerOnTurn);
        }

    }

    private static String toPrintable(int i, int[] gameField){

        //make the int value to a readable string
        switch (gameField[i]){
            case 0:
                return "O";
            case 1:
                return "X";
        }

        return ".";
    }

}
