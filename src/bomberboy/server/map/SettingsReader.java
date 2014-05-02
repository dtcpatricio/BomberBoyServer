package bomberboy.server.map;

import java.util.Stack;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;

import bomberboy.server.control.Player;
import bomberboy.server.control.Robot;

public class SettingsReader {
    private static ArrayList<Robot> robots;
    private static Stack<Player> players;
    private static GameSettings settings;
    private static int nextplayerid;
    private static int nextrobotid;

    public static GameSettings getSettings() {
        return settings;
    }

    public static void readSettings(BufferedReader reader, GameBoard board) throws NoSuchTypeException {

        robots = new ArrayList<Robot>();
        players = new Stack<Player>();

        ArrayList<String> mapStrings = new ArrayList<String>();
	int SIZE = GameBoard.SIZE;
	nextplayerid = 1;
	nextrobotid = 1;

        try {
            String s = reader.readLine();
            while (s != null) {
                mapStrings.add(s);
                s = reader.readLine();
            }
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }

        Types[][] map = new Types[SIZE][SIZE];

        for (int i = 9; i < mapStrings.size(); i++) {
            parseString(mapStrings.get(i), map[i - 9], i - 9, board);
        }

        SettingsReader.settings = new GameSettings(mapStrings.get(0).substring(3),
                Integer.parseInt(mapStrings.get(1).substring(3)),
                Integer.parseInt(mapStrings.get(2).substring(3)),
                Integer.parseInt(mapStrings.get(3).substring(3)),
                Integer.parseInt(mapStrings.get(4).substring(3)),
                Double.parseDouble(mapStrings.get(5).substring(3)),
                Integer.parseInt(mapStrings.get(6).substring(3)),
                Integer.parseInt(mapStrings.get(7).substring(3)),
                map, robots, players);

        board.initializeGameBoard(settings);
    }

    private static void parseString(String l, Types[] typeLine, int x, GameBoard board) throws NoSuchTypeException {
        for (int y = 0; y < l.length(); y++) {
            char p = l.charAt(y);
            if (p == '-') {
                typeLine[y] = Types.NULL;
            } else if (p == 'W') {
                typeLine[y] = Types.WALL;
            } else if (p == 'O') {
                typeLine[y] = Types.BARRIER;
            } else if (p == 'R') {
                typeLine[y] = Types.ROBOT;
                robots.add(new Robot(x, y, nextrobotid++, board));
            } else if (p == 'P') {
                typeLine[y] = Types.NULL;
                players.push(new Player(x, y, nextplayerid++));
            } else {
                throw new NoSuchTypeException(p);
            }
        }
    }
}
