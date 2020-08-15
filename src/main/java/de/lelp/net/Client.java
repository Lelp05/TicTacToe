package de.lelp.net;

import de.lelp.main.Main;

public class Client extends de.lelp.network.Client {

    public Client(String hostname, int port, int timeout, String group, String uuid, boolean muted, boolean logging) {
        super(hostname, port, timeout, group, uuid, muted, logging);
    }

    @Override
    public void registerMethods() {

        //redraw the game field
        methods.put("drawGameField", (dataPackage, socket) -> Main.newTurn((int[]) dataPackage.get(0), (String) dataPackage.get(1)));

        //check if the game ends
        methods.put("gameWin", (dataPackage, socket) -> Main.gameWin(dataPackage));
    }

    @Override
    public void onDisconnect() {
        System.out.println("The server has stopped");
        System.exit(0);
    }
}
