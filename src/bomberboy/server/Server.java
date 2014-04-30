package bomberboy.server;

import bomberboy.server.map.GameBoard;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

class Server
{
    
  private ServerSocket serverSocket;
  private Socket clientSocket;
  private InputStreamReader inputStreamReader;
  private BufferedReader bufferedReader;
  private int _port;

  // make it a list for various instances
  GameBoard game;
    
  Server(int port)
  {

    _port = port;

    try
      {
	serverSocket = new ServerSocket(_port);
      }
    catch(IOException ioe)
      {
	System.err.println("Error listening on port " + _port +
			   "\nPort is being used or is system-reserved\n" +
			   "Server shuting down...");
	System.exit(1);
      }

  }  



  void start() {
      
      String msg="";
      System.out.println("Server started. Listening on the port " + _port);
      
      while(true) {
	  try {
	      clientSocket = serverSocket.accept();
	      
	      inputStreamReader =
		  new InputStreamReader(clientSocket.getInputStream());
	      
	      bufferedReader =
		  new BufferedReader(inputStreamReader);
	      
	      msg = bufferedReader.readLine();
	      while(!msg.isEmpty()) {
		  parseMsg(msg);
		  msg = "";
	      }
	      
	      
	      inputStreamReader.close();
	      clientSocket.close();
	      
	  } catch (IOException ex) {
	      System.out.println("Problem in message reading");
	  }
      }
  }
    
  private void parseMsg(String msg)
  {
      System.err.println(msg);
    String[] tokens = msg.split(" ");
    String command = tokens[0];

    if(command.equals("register"))
      joinGame(tokens);

    if(command.equals("move"))
      updateTrashman(tokens);

    if(command.equals("bomb"))
      updateBanana(tokens);
    
  }

  private void joinGame(String[] params)
  {
    String playerName = params[1];
    String url = params[2];

    if(game == null)
	{
	    game = new GameBoard();
	}

    boolean success = game.addPlayer(playerName, url);
    if(!success)
      {
	System.err.println("Player " + playerName + " attempted to join a full game.");
	// do something... call the trashman to make this player run away with his smell
	return;
      }

  }

  private void updateTrashman(String[] params)
  {
    String id = params[1];
    String newX = params[2];
    String newY = params[3];
    
    try
      {
	game.smellPos(id, newX, newY);
      }
    catch(NullPointerException npe)
      {
	// probably game has not been initialized
	System.err.println("NullPointerException: " + npe.getMessage());
      }

  }

  private void updateBanana(String[] params)
  {
    String playerName = params[1];
    String bananaX = params[2];
    String bananaY = params[3];

    try
      {
	game.bananaPos(playerName, bananaX, bananaY);
      }
    catch(NullPointerException npe)
      {
	System.err.println("NullPointerException: " + npe.getMessage());
      }
  }

}
