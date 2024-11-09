// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import java.io.IOException;

import edu.seg2105.client.common.ChatIF;
import edu.seg2105.client.ui.ClientConsole;
import ocsf.client.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
	private static final int DEFAULT_PORT= 7777;
	private String loginId;
	//Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String loginId, String host, int port, ChatIF clientUI)
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.loginId = loginId;
    this.clientUI = clientUI;
    openConnection();
  }
  
 

  
  //Instance methods ************************************************
    
  public ChatClient(String host, int port, ClientConsole clientConsole) {
	// TODO Auto-generated constructor stub
}




/**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
    
    
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try {
      if (message.startsWith("#")) {
    	  handleClientCommand(message);
      } else {
      sendToServer(message);
      }
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }

  public void handleUserInput (String message) {
	  if (message.startsWith(("#")){
		  switch (message.split("")[0]) {
		  case "#quit":
			  client.closeConnection();
			  System.exit(0);
			  break;
		  case "#logoff":
			  client.closeConnection();
			  break;
		  case "#sethost":
			  if (!client.isConnected()) {
				  client.setHost(message.split(" "[1]));
			  } else {
				  System.out.println("Error: Already connected.");
				  
			  }
			  break;
		  case "#setport":
			  if (!client.isConnected()) {
                  client.setPort(Integer.parseInt(message.split(" ")[1]));
              } else {
                  System.out.println("Error: Already connected.");
              }
              break;
		  case "#gethost#":
			  System.out.println("Current host: " + client.getHost());
			  break;
		  case "getport":
			  System.out.println("Current port: " + client.getPort());
			  break;
			  
	  }
	  
  protected void connectionEstablished() {
      try {
          sendToServer("#login " + loginId); // Send login ID to the server
      } catch (IOException e) {
          clientUI.display("Error sending login ID to server.");
          quit();
      }
  }
  
  protected void connectionClosed() {
	  clientUI.display("Server has shut down. The client will exit");
	  System.exit(0);;
	  
  }
  protected void connectionException( Exception exception) {
	  clientUI.display("Connection error: " + exception.getMessage());
	  clientUI.display("Server shutdown unexpectedly.");
	  System.exit(0);
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  
  
}
//End of ChatClient class
