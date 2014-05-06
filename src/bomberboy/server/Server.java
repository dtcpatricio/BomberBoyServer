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

    try {
	serverSocket = new ServerSocket(_port);
    } catch(IOException ioe) {
	System.err.println("Error listening on port " + _port +
			   "\nPort is being used or is system-reserved\n" +
			   "Server shuting down...");
	System.exit(1);
    }

    game = new GameBoard();

  }  



  void start() {
      
      System.out.println("Server started. Listening on the port " + _port);
      
      while(true) {
	  try {
	      clientSocket = serverSocket.accept();
	      
	      inputStreamReader =
		  new InputStreamReader(clientSocket.getInputStream());
	      
	      bufferedReader =
		  new BufferedReader(inputStreamReader);
	      
	      String msg = bufferedReader.readLine();
	      if(!msg.isEmpty()) {
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
    String[] tokens = msg.split("\\s+");
    String command = tokens[0];

    if(command.equals("register"))
      joinGame(tokens);

    if(command.equals("move"))
      updateTrashman(tokens);

    if(command.equals("banana"))
      updateBanana(tokens);
    
  }

  private void joinGame(String[] params)
  {
    String playerName = params[1];
    String url = params[2];

    if(!game.isRunning())
	{
	    game.beginGame();
	    System.err.println("Game started!");
	}

    boolean success = game.addPlayer(playerName, url);
    if(!success)
      {
	System.err.println("Player " + playerName + " attempted to join a full game.");
	// do something... call the trashman to make this player run away with his smell
	return;
      }

  }

  private void updateTrashman(String[] params) {
      Integer id = Integer.parseInt(params[1]);
      Integer xnew = Integer.parseInt(params[2]);
      Integer ynew = Integer.parseInt(params[3]);
    
    try {
	game.smellMove(id, xnew, ynew);
      }
    catch(NullPointerException npe)
      {
	// probably game has not been initialized
	System.err.println("NullPointerException: " + npe.getMessage());
      }

  }

  private void updateBanana(String[] params) {
      Integer id = Integer.parseInt(params[1]);
      Integer bananaX = Integer.parseInt(params[2]);
      Integer bananaY = Integer.parseInt(params[3]);

    try
      {
	game.bananaDump(id, bananaX, bananaY);
      }
    catch(NullPointerException npe)
      {
	System.err.println("NullPointerException: " + npe.getMessage());
      }
  }

}
