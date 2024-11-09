package edu.seg2105.edu.server.backend;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractServer {
    private int port;
    private ServerSocket serverSocket;
    private boolean isListening;
    private List<ConnectionToClient> clients;

    // Constructor
    public AbstractServer(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
    }

    // Start listening for client connections
    public void listen() throws IOException {
        if (!isListening) {
            serverSocket = new ServerSocket(port);
            isListening = true;
            System.out.println("Server listening on port " + port);

            // Start accepting clients in a new thread
            new Thread(() -> {
                while (isListening) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        ConnectionToClient client = new ConnectionToClient(clientSocket, this);
                        clients.add(client);
                        client.start(); // Start listening for messages from the client
                        clientConnected(client); // Notify that a client has connected
                    } catch (IOException e) {
                        if (isListening) {
                            System.out.println("Error accepting client connection: " + e.getMessage());
                        }
                    }
                }
            }).start();
        }
    }

    // Stop listening for new clients but keep existing connections open
    public void stopListening() throws IOException {
        isListening = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
            System.out.println("Server stopped listening for new clients.");
        }
    }

    // Close the server and disconnect all clients
    public void close() throws IOException {
        stopListening(); // Stop accepting new clients
        for (ConnectionToClient client : clients) {
            client.closeConnection();
        }
        clients.clear();
        System.out.println("Server closed and all clients disconnected.");
    }

    // Abstract method to handle messages from a client
    protected abstract void handleMessageFromClient(Object msg, ConnectionToClient client);

    // Method to send a message to all clients
    public void sendToAllClients(Object msg) {
        for (ConnectionToClient client : clients) {
            client.sendToClient(msg);
        }
    }

    // Get the port number the server is listening on
    public int getPort() {
        return port;
    }

    // Called when a client connects
    protected void clientConnected(ConnectionToClient client) {
        System.out.println("Client connected: " + client);
    }

    // Called when a client disconnects
    protected void clientDisconnected(ConnectionToClient client) {
        System.out.println("Client disconnected: " + client);
    }

	protected void serverStopped() {
		// TODO Auto-generated method stub
		
	}

	protected void serverStarted() {
		// TODO Auto-generated method stub
		
	}
}

