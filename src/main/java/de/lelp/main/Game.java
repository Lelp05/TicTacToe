package de.lelp.main;

import de.lelp.api.UsefulHelper;
import de.lelp.net.Client;
import de.lelp.net.Server;
import de.lelp.network.DataPackage;

import java.util.Scanner;

public class Game {

    public static void drawMainMenu(){

        //draw the main menu

        System.out.println(
                "  _______     ______          ______              ____        ___          \n" +
                " /_  __(_)___/_  __/___ _____/_  __/___  ___     / __ \\____  / (_)___  ___ \n" +
                "  / / / / ___// / / __ `/ ___// / / __ \\/ _ \\   / / / / __ \\/ / / __ \\/ _ \\ \n" +
                " / / / / /__ / / / /_/ / /__ / / / /_/ /  __/  / /_/ / / / / / / / / /  __/ \n" +
                "/_/ /_/\\___//_/  \\__,_/\\___//_/  \\____/\\___/   \\____/_/ /_/_/_/_/ /_/\\___/ \n" +
                "\n"+
                "By Lelp05 " + "(c)" + UsefulHelper.getYear() + "\n");

        System.out.println("1. Join a game");
        System.out.println("2. Create a server");

        //wait for a response of the user
        try{

            System.out.print("> ");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.next();

            //check if the input was valid
            int option = Integer.parseInt(input);
            if(option == 1){
                joinServer();
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

        UsefulHelper.separate(100);

        System.out.println(
                "   ______                __                                                 \n" +
                "  / ____/_______  ____ _/ /____     ____ _   ________  ______   _____  _____\n" +
                " / /   / ___/ _ \\/ __ `/ __/ _ \\   / __ `/  / ___/ _ \\/ ___/ | / / _ \\/ ___/\n" +
                "/ /___/ /  /  __/ /_/ / /_/  __/  / /_/ /  (__  )  __/ /   | |/ /  __/ /    \n" +
                "\\____/_/   \\___/\\__,_/\\__/\\___/   \\__,_/  /____/\\___/_/    |___/\\___/_/     \n" +
                "\n" +
                "Enter the port on which the server should start (for example: 4000) \n" +
                "(make sure the port is free and approved on your router or on the virtual machine)");

        //wait for a response of the user
        try{

            System.out.print("> ");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.next();

            //check if the input was valid
            int port = Integer.parseInt(input);
            Main.server = new Server(port, 1000, true, false);

            //connect this user to the server
            Main.client = new Client("localhost", port, 1000, "Player", "Host", true, false);

        }catch (Exception e){
            drawMainMenu();
        }

    }

    public static void joinServer(){

        try{

            UsefulHelper.separate(100);

            System.out.println(
                    "       __      _                                                 \n" +
                    "      / /___  (_)___     ____ _   ________  ______   _____  _____\n" +
                    " __  / / __ \\/ / __ \\   / __ `/  / ___/ _ \\/ ___/ | / / _ \\/ ___/\n" +
                    "/ /_/ / /_/ / / / / /  / /_/ /  (__  )  __/ /   | |/ /  __/ /    \n" +
                    "\\____/\\____/_/_/ /_/   \\__,_/  /____/\\___/_/    |___/\\___/_/     \n" +
                    "\n" +
                    "Enter the ip and the port of the game server ( 127.0.0.1:4000 )");

            //wait for a response of the user
            System.out.print("> ");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.next();

            //check if the input was valid
            String[] parts = input.split(":");

            if(parts.length == 2){

                int port = Integer.parseInt(parts[1]);

                //try to join the server
                Main.client = new Client(parts[0], port, 1000, "Player", "Player-1", true, false);

                if(!Main.client.isConnected()){
                    System.out.println("The server seems to be offline");
                    System.exit(0);
                }

            }else{
                throw new IllegalArgumentException();
            }


        }catch (Exception e){
            drawMainMenu();
        }

    }

    public static void gameWin(DataPackage pack){

        UsefulHelper.separate(100);

        if (pack.get(0).equals("Win")) {

            //print who won's
            if (pack.get(1).equals(Main.client.getUuid())) {

                System.out.println(
                        "__  __                                  \n" +
                        "\\ \\/ /___  __  __   _      ______  ____ \n" +
                        " \\  / __ \\/ / / /  | | /| / / __ \\/ __ \\\n" +
                        " / / /_/ / /_/ /   | |/ |/ / /_/ / / / /\n" +
                        "/_/\\____/\\__,_/    |__/|__/\\____/_/ /_/ \n" +
                        "\n" +
                        "Want to continue (Yes) (No)\n");


            } else {
                System.out.println(
                        "__  __               __           __ \n" +
                        "\\ \\/ /___  __  __   / /___  _____/ /_\n" +
                        " \\  / __ \\/ / / /  / / __ \\/ ___/ __/\n" +
                        " / / /_/ / /_/ /  / / /_/ (__  ) /_  \n" +
                        "/_/\\____/\\__,_/  /_/\\____/____/\\__/  \n" +
                        "\n" +
                        "Want to continue (Yes) (No)\n");
            }

        } else if (pack.get(0).equals("Draw")) {
            System.out.println(
                    "    ____                     \n" +
                    "   / __ \\_________ __      __\n" +
                    "  / / / / ___/ __ `/ | /| / /\n" +
                    " / /_/ / /  / /_/ /| |/ |/ / \n" +
                    "/_____/_/   \\__,_/ |__/|__/  \n" +
                    "\n" +
                    "Want to continue (Yes) (No)\n");
        }

        //draw the game field
        drawGameField((int[]) pack.get(2));

        try{

            //wait for a input of the user
            System.out.print("> ");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.next();

            if(input.contains("n") || input.contains("N")){
                System.exit(0);
            }else{
                DataPackage reply = Main.client.sendRequest("reset");
                System.out.println(reply.get(0));
            }

        }catch (Exception ignored){}
    }

    public static void waitForGameInput(int[] gameField, String playerOnTurn){


       try{

           //wait for a input of the user
           System.out.print("> ");
           Scanner scanner = new Scanner(System.in);
           String input = scanner.next();

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
           newTurn(gameField, playerOnTurn);
       }

    }

    public static void newTurn(int[] gameField, String playerOnTurn){

        UsefulHelper.separate(100);

        if(playerOnTurn.equals(Main.client.getUuid())){
            System.out.println("\n It's your turn");
        }else{
            System.out.println("\n wait for the opponents turn");
        }

        drawGameField(gameField);

        //let the player make a turn
        if(playerOnTurn.equals(Main.client.getUuid())) {
            System.out.println("1-9: make a turn");
            waitForGameInput(gameField, playerOnTurn);
        }

    }

    public static void drawGameField(int[] gameField){
        System.out.println(
                "\n" +
                toPrintable(0, gameField) + " | " + toPrintable(1, gameField) + " | " + toPrintable(2, gameField) + "\n" +
                toPrintable(3, gameField) + " | " + toPrintable(4, gameField) + " | " + toPrintable(5, gameField) + "\n" +
                toPrintable(6, gameField) + " | " + toPrintable(7, gameField) + " | " + toPrintable(8, gameField) + "\n"
        );
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
