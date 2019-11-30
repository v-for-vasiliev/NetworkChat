package client;

import client.connection.MessageService;
import client.connection.MessageServiceImpl;
import client.connection.Network;
import client.ui.AuthGui;


public class ClientApp {

    private static final String SERVER_ADDR = "localhost";
    private static final int SERVER_PORT = 8189;

    public static void main(String[] args) {
        try {
            Network network = new Network(SERVER_ADDR, SERVER_PORT);
            MessageService messageService = new MessageServiceImpl(network);
            AuthGui authGui = new AuthGui(messageService);
            authGui.showForm();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
