package bomberboy.server.map;

import bomberboy.server.comm.*;
import bomberboy.server.control.*;

import java.io.FileInputStream;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.io.FileNotFoundException;

public class GameBoard
{
    public static int MAXPLAYERS = 3;
    public static int SIZE = 19;
    private Types[][] board;
    private GameSettings settings;
    private Map<Integer, Player> players;
    private Map<Integer, String> playersURL;
    private Stack<Player> playerStack;

    public GameBoard()
    {
	// placeholder values.. it should look up for the correct level
	// specified in the level arg
	try {
	    FileInputStream fis = new FileInputStream("src/bomberboy/server/map/files/l1.map");
	    BufferedReader br = new BufferedReader(new InputStreamReader(fis));
	    SettingsReader.readSettings(br);
	    settings = SettingsReader.getSettings();
	} catch(FileNotFoundException fnfe) {
	    System.err.println("FileNotFoundException: " + fnfe.getMessage());
	    System.exit(1);
	} catch(NoSuchTypeException nste) {
	    System.err.println("NoSuchTypeException: " + nste.getMessage());
	    nste.printStackTrace();
	    System.exit(-1);
	}
	

	setMap(settings.getMap());
	players = new Hashtable<Integer, Player>();
	playersURL = new Hashtable<Integer, String>();
	playerStack = (Stack<Player>) settings.getPlayers().clone();
    }

    private void setMap(Types[][] types) {
        board = new Types[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            board[i] = Arrays.copyOf(types[i], types[i].length);
        }
    }

    public boolean addPlayer(String name, String url) {
	if(!playerStack.empty()) {

	    String msg = "";
	    Player p = playerStack.pop();
	    int id = p.getID();
	    p.setURL(url);
	    p.setName(name);

	    players.put(id, p);
	    playersURL.put(id, url);
	    
	    // placeholder message for the server
	    System.err.println("Player " + name + " joined a new game, with ID: " + id);
	    
	    // inform the player (ack register) we added him with pair (id, pos)
	    msg = "ackReg " + id + " " + p.getX() + " " + p.getY();
	    BroadcastMessage pm = new BroadcastMessage(msg, url);
	    pm.start();
	    
	    // if there are other players in game, let's inform them
	    msg = "newplayer " + id + " " + p.getX() + " " + p.getY() + " " + name;
	    BroadcastMessage bm = new BroadcastMessage(msg, getPlayersURLs(url));
	    bm.start();
	    return true;
	}
	
	return false;
    }

    public void smellPos(Integer id, Integer xpos, Integer ypos) {
	Player p = players.get(id);
	int oldx = p.getX();
	int oldy = p.getY();
	
	String dir = "still"; // in case the smelly moves against a wall
	if(oldx < xpos)
	    dir = "down";
	if(oldx > xpos)
	    dir = "up";
	if(oldy < ypos)
	    dir = "right";
	if(oldy > ypos)
	    dir = "left";

	String name = p.getName();
	// placeholder debug message
	System.err.println("Smelly " + name + " moved to " + xpos + ", " + ypos);

	board[oldx][oldy] = Types.NULL;
	board[xpos][ypos] = Types.SMELLY1; // should use id instead
	String url = playersURL.get(id);

	// comunicate changes to other players
	String msg = "move " + id + " " + dir;
	BroadcastMessage bm = new BroadcastMessage(msg, getPlayersURLs(url));
	bm.start();
    }

    public void bananaPos(String name, String posX, String posY)
    {
	int x = Integer.parseInt(posX);
	int y = Integer.parseInt(posY);

	System.err.println("Smelly " + name + " threw a banana on " + x + ", " + y);

	board[x][y] = Types.BANANA;
	int id = getIdByName(name);
	String url = playersURL.get(id);

	// comunicate changes to other players
	String msg = "bomb " + x + " " + y;
	BroadcastMessage bm = new BroadcastMessage(msg, getPlayersURLs(url));
	bm.start();
	// and probably start a bomb timer (server side bomb)
    }

    private Types getPlayer(String name)
    {

	int id = 0;
	for(Map.Entry<Integer, Player> entry : players.entrySet())
	    {
		if(name.equals(entry.getValue().getName()))
		    {
			id = entry.getKey();
			break;
		    }
	    }

	if(id == 1)
	    {
		return Types.SMELLY1;
	    }
	else if(id == 2)
	    {
		return Types.SMELLY2;
	    }
	else
	    {
		return Types.SMELLY3;
	    }
    }

    private ArrayList<String> getPlayersURLs(String urlToRemove)
    {
	ArrayList<String> urls = new ArrayList<String>(playersURL.values());
	if(!urlToRemove.equals(""))
	    urls.remove(urlToRemove);

	return urls;
    }

    private int getIdByName(String name) {
	for(Map.Entry<Integer, Player> entry : players.entrySet()) {
	    if(name.equals(entry.getValue().getName())) {
		int id = entry.getKey();
		return id;
	    }
	}
	return 0;
    }
}
