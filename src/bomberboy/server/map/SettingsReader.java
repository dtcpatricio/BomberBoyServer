package bomberboy.server.map;

import java.util.Stack;
import java.util.ArrayList;

import bomberboy.server.control.Player;
import bomberboy.server.control.Robot;

class SettingsReader {
    private static ArrayList<Robot> robots;
    private static Stack<Player> players;
    private static GameSettings settings;
    private int nextid;

    public static GameSettings getSettings() {
        return settings;
    }

    public static void readSettings(BufferedReader reader) throws NoSuchTypeException {

        robots = new ArrayList<Robot>();
        players = new Stack<Player>();

        ArrayList<String> mapStrings = new ArrayList<String>();

	nextid = 1;

        try {
            String s = reader.readLine();
            while (s != null) {
                mapStrings.add(s);
                s = reader.readLine();
            }
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }

        Types[][] map = new Types[status.SIZE][status.SIZE];

        for (int i = 9; i < mapStrings.size(); i++) {
            parseString(mapStrings.get(i), map[i - 9], i - 9, main);
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

        status.initializeGameStatus(settings);
    }

    private static void parseString(String l, Types[] typeLine, int x) throws NoSuchTypeException {
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
                robots.add(new Robot(x, y));
            } else if (p == 'P') {
                typeLine[y] = Types.NULL;
                players.push(new Player(x, y, nextid++));
            } else {
                throw new NoSuchTypeException(p);
            }
        }
    }
}
