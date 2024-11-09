package edu.seg2105.server.backend;

import edu.seg2105.client.common.ChatIF;
import ocsf.server.*;
import java.io.IOException;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 */
public class EchoServer extends AbstractServer {
    // Class variables
    final public static int DEFAULT_PORT = 5555;
    private ChatIF serverUI;

    // Constructor
    public EchoServer(int port, ChatIF serverUI) {
        super(port);
        this.serverUI = serverUI;
    }

    // Handle messages from clients
    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        String message = msg.toString();

        if (message.startsWith("#login ")) {
            handleLoginCommand(message, client);
        } else {
            if (client.getInfo("loginId") == null) {
                try {
                    client.sendToClient("Error: You must login first.");
                    client.close(); 
                } catch (IOException e) {
                    System.out.println("Error closing client connection: " + e.getMessage());
                }
            } else {
                String loginId = client.getInfo("loginId").toString();
                sendToAllClients(loginId + ": " + message);
                System.out.println("Message from " + loginId + ": " + message);
            }
        }
    }

    // Handle the #login command from the client
    private void handleLoginCommand(String message, ConnectionToClient client) {
        String[] parts = message.split(" ", 2);
        if (parts.length < 2) {
            try {
                client.sendToClient("Error: Login ID is required.");
                client.close();
            } catch (IOException e) {
                System.out.println("Error closing client connection: " + e.getMessage());
            }
            return;
        }

        if (client.getInfo("loginId") != null) {
            try {
                client.sendToClient("Error: Login command can only be sent once at the start.");
                client.close();
            } catch (IOException e) {
                System.out.println("Error closing client connection: " + e.getMessage());
            }
            return;
        }

        String loginId = parts[1];
        client.setInfo("loginId", loginId);
        System.out.println("Client logged in with ID: " + loginId);

        try {
            ((Object) client).sendToClient("Login successful. Welcome, " + loginId + "!");
        } catch (IOException e) {
            System.out.println("Error sending login confirmation: " + e.getMessage());
        }
    }

    // Called when the server starts listening for connections
    @Override
    protected void serverStarted() {
        System.out.println("Server listening for connections on port " + getPort());
    }

    // Called when the server stops listening for connections
    @Override
    protected void serverStopped() {
        System.out.println("Server has stopped listening for connections.");
    }

    // Called when a client connects to the server
    @Override
    protected void clientConnected(ConnectionToClient client) {
        if (serverUI != null) {
            serverUI.display("A new client has connected.");
        }
    }

    // Called when a client disconnects from the server
    @Override
    protected void clientDisconnected(ConnectionToClient client) {
        if (serverUI != null) {
            serverUI.display("A client has disconnected.");
        }
    }

    // Main method to start the server
    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port number. Using default port: " + DEFAULT_PORT);
            }
        }

        // Instantiate the server and server console for interaction
        ChatIF console = (ChatIF) new ServerConsole();
        EchoServer server = new EchoServer(port, console);

        try {
            server.listen(); // Start listening for connections
            System.out.println("Server listening for clients on port " + port);
        } catch (Exception ex) {
            System.out.println("ERROR - Could not listen for clients!");
            ex.printStackTrace();
        }
    }

	public void listen() {
		try {
	        super.listen(); // Start listening for connections using the superclass method
	        System.out.println("Server is now listening for clients on port " + getPort());
	    } catch (Exception e) {
	        System.out.println("Error: Could not start listening for clients.");
	        e.printStackTrace();
	    }
		
	}
}

//End of EchoServer class
