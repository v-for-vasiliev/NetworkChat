package client;

import client.connection.IMessageService;
import client.connection.MessageService;
import client.connection.Network;
import client.ui.AuthGui;
import client.ui.ChatGui;


public class ClientApp {

    private static final String SERVER_ADDR = "localhost";
    private static final int SERVER_PORT = 8189;

    public static void main(String[] args) {
        try {
            Network network = new Network(SERVER_ADDR, SERVER_PORT);
            IMessageService messageService = new MessageService(network);
            AuthGui authGui = new AuthGui(messageService);
            authGui.showForm();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
