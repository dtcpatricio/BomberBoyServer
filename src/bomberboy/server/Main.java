package bomberboy.server;

public class Main
{
  
  public static void main(String[] args)
  {

    if(args.length != 1)
      {
	System.err.println("Error starting server.\nReason: Incorrect number of arguments.\n" +
			   "Usage: java bomberboy.server.Main <port>");
	System.exit(1);
      }
    Server server = new Server(Integer.parseInt(args[0]));
    server.start();

  }

}
