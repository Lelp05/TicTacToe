package de.lelp.net;

import de.lelp.main.Game;

public class Client extends de.lelp.network.Client {

    public Client(String hostname, int port, int timeout, String group, String uuid, boolean muted, boolean logging) {
        super(hostname, port, timeout, group, uuid, muted, logging);
    }

    @Override
    public void registerMethods() {

        //redraw the game field
        methods.put("drawGameField", (dataPackage, socket) -> Game.drawGameField((int[]) dataPackage.get(0), (String) dataPackage.get(1)));

        //check if the game ends
        methods.put("gameWin", (dataPackage, socket) -> Game.gameWin(dataPackage));
    }

    @Override
    public void onDisconnect() {
        Game.stopGame();
    }
}
