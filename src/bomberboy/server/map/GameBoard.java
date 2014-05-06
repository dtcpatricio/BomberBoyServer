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
    protected static int RANGE;
    protected static int TIMETOBLOW;
    protected static int TIMEOFBLOW;
    protected static boolean GAMEOVER = false;
    protected static ArrayList<Timer> timers = new ArrayList<Timer>();
    public static int MAXPLAYERS = 3;
    public static int SIZE = 19;
    
    protected Object lock = new Object();
    private Types[][] board;
    private GameSettings settings;
    private Map<Integer, Player> players;
    private Map<Integer, Robot> robots;
    private Map<Integer, String> playersURL;
    private Stack<Player> playerStack;
    private boolean gameStarted = false;

    public GameBoard()
    {
	// placeholder values.. it should look up for the correct level
	// specified in the level arg
	try {
	    FileInputStream fis = new FileInputStream("src/bomberboy/server/map/files/l1.map");
	    BufferedReader br = new BufferedReader(new InputStreamReader(fis));
	    SettingsReader.readSettings(br, this);
	    settings = SettingsReader.getSettings();
	} catch(FileNotFoundException fnfe) {
	    System.err.println("FileNotFoundException: " + fnfe.getMessage());
	    System.exit(1);
	} catch(NoSuchTypeException nste) {
	    System.err.println("NoSuchTypeException: " + nste.getMessage());
	    nste.printStackTrace();
	    System.exit(-1);
	}
	
	// initialize timer settings
	RANGE = SettingsReader.getSettings().getExplosionRange();
        TIMETOBLOW = SettingsReader.getSettings().getExplosionTimeout() * 1000;
        TIMEOFBLOW = SettingsReader.getSettings().getExplosionDuration() * 1000;

	// initialize robots (moving speed)
        for (Robot robot : robots.values()) {
            robot.initializeSettings();
        }

    }

    // called by SettingsReader after reading .map
    public void initializeGameBoard(GameSettings settings) {
	// create board view
	setMap(settings.getMap());

	// initialize data structures for robots and players
        robots = new Hashtable<Integer, Robot>();
        players = new Hashtable<Integer, Player>();
	playersURL = new Hashtable<Integer, String>();

	// register robots in the game - gotten from reading the file .map
        for (Robot robot : settings.getRobots()) {
            registerRobot(robot);
        }
        playerStack = (Stack<Player>) settings.getPlayers().clone();
        GAMEOVER = false;
    }

    private void setMap(Types[][] types) {
        board = new Types[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            board[i] = Arrays.copyOf(types[i], types[i].length);
        }
    }

    private void registerRobot(Robot robot) {
        int rID = 10 + robots.size();
        robot.setID(rID);
        robots.put(rID, robot);
    }

    public Collection<Player> getPlayers() { return players.values(); }

    public boolean isRunning() {
	return gameStarted;
    }

    public void beginGame() {
	gameStarted = true;

        for (Robot r : this.robots.values()) {
            r.start();
        }
    }

    public boolean addPlayer(String name, String url) {
	if(!playerStack.empty()) {

	    Player p = playerStack.pop();
	    int id = p.getID();
	    p.setURL(url);
	    p.setName(name);

	    players.put(id, p);
	    playersURL.put(id, url);
	    
	    // placeholder message for the server
	    System.err.println("Player " + name + " joined a new game, with ID: " + id + "\nand url " + url);
	    
	    // inform the player (ack register) we added him with pair (id, pos)
	    String msgpm = "ackReg " + id + " " + p.getX() + " " + p.getY();
	    BroadcastMessage pm = new BroadcastMessage(msgpm, url);
	    pm.start();
	    
	    if(players.size() > 1) {
		Collection<Player> playerColl = players.values();
		for(Player c : playerColl) {
		    String playeronserver = "newplayer " + c.getID() + " " + c.getX() + " " + c.getY() + " " + c.getName();
		    BroadcastMessage toplayer = new BroadcastMessage(playeronserver, url);
		    toplayer.start();
		}
		// if there are other players in game, let's inform them
		String msgbm = "newplayer " + id + " " + p.getX() + " " + p.getY() + " " + name;
		BroadcastMessage bm = new BroadcastMessage(msgbm, getPlayersURLs(url));
		bm.start();
	    }
	    return true;
	}
	
	return false;
    }

    public void smellMove(Integer id, Integer xpos, Integer ypos) {
	Player p = players.get(id);
	int oldx = p.getX();
	int oldy = p.getY();
	
	String dir = "still"; // in case the smelly moves against a wall
	if(oldx < xpos) {
	    dir = "down";
	    p.incX();
	}
	if(oldx > xpos) {
	    dir = "up";
	    p.decX();
	}
	if(oldy < ypos) {
	    dir = "right";
	    p.incY();
	}
	if(oldy > ypos) {
	    dir = "left";
	    p.decY();
	}

	String name = p.getName();
	// placeholder debug message
	System.err.println("Smelly " + name + " moved to " + xpos + ", " + ypos + "\ndirection " + dir);

	board[oldx][oldy] = Types.NULL;
	board[xpos][ypos] = Types.SMELLY1; // should use id to figure the type of smelly (1, 2 or 3)
	String url = playersURL.get(id);

	// comunicate changes to other players
	String msg = "move " + id + " " + dir;
	BroadcastMessage bm = new BroadcastMessage(msg, getPlayersURLs(url));
	bm.start();
    }

    public void bananaDump(Integer id, Integer xpos, Integer ypos) {
	
	Player p = players.get(id);
	System.err.println("Smelly " + p.getName() + " took a dump of banana on " + xpos + ", " + ypos);
	
	board[xpos][ypos] = Types.BANANA;
	String url = playersURL.get(id);

	// comunicate changes to other players
	String msg = "banana " + xpos + " " + ypos;
	BroadcastMessage bm = new BroadcastMessage(msg, getPlayersURLs(url));
	bm.start();
	
	// start server side bomb
    }

    // old method to get the player Type. Must be changed to use players Map and id as arg.
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

    /**
     * getPlayersURLs
     * @param urlToRemove
     * @return ArrayList<>
     * returns an ArrayList<String> with the playersURL. If urlToRemove
     * is not null, it removes that string from the list prior to return it.
     */
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

    /**
     * move
     * @param Movements
     * @param Integer
     * computes and updates the map for a move call from a robot thread
     */
    public boolean move(Movements e, Integer id) {
        synchronized (lock) {
            Robot r = null;
	    r = robots.get(id);

            if (!canMove(e, r)) return false;

	    moveClean(r);

            if (e.equals(Movements.DOWN)) {
                r.incrX();
            } else if (e.equals(Movements.UP)) {
                r.decrX();
            } else if (e.equals(Movements.LEFT)) {
                r.decrY();
            } else {
                r.incrY();
            }

	    // Maybe for later!!
            // if (diedInNuclearFallout(c) && c instanceof Player) {
            //     thread.smellyDied();
            //     return true;
            // }

	    movePlace(r);
	    String msg = "robot " + r.getID() + " " + r.getX() + " " + r.getY();
	    BroadcastMessage bm = new BroadcastMessage(msg, getPlayersURLs(""));
            bm.start();

            return true;
        }
    }

    private void moveClean(Robot c) {
        if (board[c.getX()][c.getY()].equals(Types.ROBOTANDBANANA)) {
            board[c.getX()][c.getY()] = Types.BANANA;
        } else {
	    board[c.getX()][c.getY()] = Types.NULL;
        }
    }

    private void movePlace(Robot c) {
        if (board[c.getX()][c.getY()].equals(Types.BANANA)) {
            board[c.getX()][c.getY()] = Types.ROBOTANDBANANA;
        } else {
            board[c.getX()][c.getY()] = Types.ROBOT;
        }
    }

    private boolean canMove(Movements e, Robot c) {
        if (e.equals(Movements.DOWN) && c.getX() < SIZE - 1 && isNotOccupied(e, c)) {
            return true;
        } else if (e.equals(Movements.UP) && c.getX() > 0 && isNotOccupied(e, c)) {
            return true;
        } else if (e.equals(Movements.LEFT) && c.getY() > 0 && isNotOccupied(e, c)) {
            return true;
        } else if (e.equals(Movements.RIGHT) && c.getY() < SIZE - 1 && isNotOccupied(e, c)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isNotOccupied(Movements e, Robot c) {
        if (e.equals(Movements.DOWN) && emptyPosition(c.getX() + 1, c.getY())) {
            return true;
        } else if (e.equals(Movements.UP) && emptyPosition(c.getX() - 1, c.getY())) {
            return true;
        } else if (e.equals(Movements.LEFT) && emptyPosition(c.getX(), c.getY() - 1)) {
            return true;
        } else if (e.equals(Movements.RIGHT) && emptyPosition(c.getX(), c.getY() + 1)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean emptyPosition(int x, int y) {
        return board[x][y].equals(Types.NULL) || board[x][y].equals(Types.EXPLOSION) || board[x][y].equals(Types.EXPLOSIONANDBANANA);
    }
    
}
