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
    private Types[][] map;
    private GameSettings settings;
    private Map<Integer, Player> players;
    private Map<Integer, String> playersURL;

    public GameBoard()
    {
	// placeholder values.. it should look up for the correct level
	// specified in the level arg
	try {
	    FileInputStream fis = new FileInputStream("src/bomberboy/server/map/files/l1.map");
	    BufferedReader br = new BufferedReader(new InputStreamReader(fis));
	    SettingsReader.readSettings(br);
	} catch(FileNotFoundException fnfe) {
	    System.err.println("FileNotFoundException: " + fnfe.getMessage());
	    System.exit(1);
	}
	
	settings = SettingsReader.getSettings();
	setMap(settings.getMap());
	players = new Hashtable<Integer, Player>();
	playersURL = new Hashtable<Integer, String>();
    }

    private void setMap(Types[][] types) {
        board = new Types[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            board[i] = Arrays.copyOf(types[i], types[i].length);
        }
    }

    public boolean addPlayer(String name, String url, Integer x, Integer y) {
	if(nextID <= MAXPLAYERS) {

	    Player p = new Player(x, y, nextID, name, url);	    
	    players.put(nextID, p);
	    playersURL.put(nextID, url);
	    
	    // placeholder message for the server
	    System.err.println("Player " + name + " joined a new game.");
	    
	    // if there is another player in game, let's inform him
	    String msg = "newplayer " + nextID + " " + name;
	    BroadcastMessage bm = new BroadcastMessage(msg, getPlayersURLs(""));
	    bm.start();
	    return true;
	}
	
	return false;
    }

    public void smellPos(String serverID, String posX, String posY)
    {
	int id = Integer.parseInt(serverID);
	int x = Integer.parseInt(posX);
	int y = Integer.parseInt(posY);

	Player p = players.get(id);
	int oldx = p.getX();
	int oldy = p.getY();

	String dir = "";
	if(oldx < x)
	    dir = "down";
	if(oldx > x)
	    dir = "up";
	if(oldy < y)
	    dir = "right";
	if(oldy > y)
	    dir = "left";

	String name = p.getName();
	// placeholder debug message
	System.err.println("Smelly " + name + " moved to " + x + ", " + y);

	map[oldx][oldy] = Types.NULL;
	map[x][y] = getPlayer(name); // should use id instead
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

	map[x][y] = Types.BANANA;
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
