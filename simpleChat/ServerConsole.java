import edu.seg2105.client.common.ChatIF;

public class ServerConsole implements ChatIF {

	@Override
	private EchoServer server;

    public ServerConsole(EchoServer server) {
        this.server = server;
    }

    @Override
    public void display(String message) {
        System.out.println(message); // Display messages on the server's console
    }

    // Method to accept input from the server's console
    public void accept() {
        try {
            BufferedReader fromConsole = new BufferedReader(new InputStreamReader(System.in));
            String message;

            while ((message = fromConsole.readLine()) != null) {
                if (message.startsWith("#")) {
                    handleServerCommand(message); // Process as a command if it starts with '#'
                } else {
                    // Broadcast message to all clients with "SERVER MSG>" prefix
                    server.sendMessageToAllClients("SERVER MSG> " + message);
                    display("SERVER MSG> " + message);
                }
            }
        } catch (Exception ex) {
            System.out.println("Unexpected error while reading from console!");
        }
    }

    // Method to handle server-specific commands
    private void handleServerCommand(String command) {
        String[] parts = command.split(" ");
        String commandType = parts[0];

        switch (commandType) {
            case "#quit":
                server.close();
                System.exit(0);
                break;

            case "#stop":
                server.stopListening();
                display("Server has stopped listening for new clients.");
                break;

            case "#close":
                server.stopListening();
                server.disconnectAllClients();
                display("Server has disconnected all clients and stopped listening.");
                break;

            case "#setport":
                if (parts.length > 1) {
                    if (!server.isListening()) {
                        int port = Integer.parseInt(parts[1]);
                        server.setPort(port);
                        display("Port set to " + port);
                    } else {
                        display("Error: Server must be stopped to change the port.");
                    }
                } else {
                    display("Error: Please provide a port number.");
                }
                break;

            case "#start":
                if (!server.isListening()) {
                    try {
                        server.listen();
                        display("Server has started listening for new clients.");
                    } catch (Exception e) {
                        display("Error starting server: " + e.getMessage());
                    }
                } else {
                    display("Server is already listening.");
                }
                break;

            case "#getport":
                display("Current port: " + server.getPort());
                break;

            default:
                display("Invalid command.");
        }
    }

}
