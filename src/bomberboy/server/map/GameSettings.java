package bomberboy.server.map;

import java.util.Stack;
import java.util.ArrayList;

import bomberboy.server.control.Player;
import bomberboy.server.control.Robot;

public class GameSettings {
    private String levelName;
    private Integer gameDuration;
    private Integer explosionTimeout;
    private Integer explosionDuration;
    private Integer explosionRange;
    private Double robotSpeed;
    private Integer pointsPerRobot;
    private Integer pointsPerPlayer;
    private Types[][] map;
    private ArrayList<Robot> robots;
    private Stack<Player> players;

    public GameSettings(String levelName, Integer gameDuration, Integer explosionTimeout, Integer explosionDuration, Integer explosionRange, Double robotSpeed, Integer pointsPerRobot, Integer pointsPerPlayer, Types[][] map, ArrayList<Robot> robots, Stack<Player> players) {
        this.levelName = levelName;
        this.gameDuration = gameDuration;
        this.explosionTimeout = explosionTimeout;
        this.explosionDuration = explosionDuration;
        this.explosionRange = explosionRange;
        this.robotSpeed = robotSpeed;
        this.pointsPerRobot = pointsPerRobot;
        this.pointsPerPlayer = pointsPerPlayer;
        this.map = map;
        this.robots = robots;
        this.players = players;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public Integer getGameDuration() {
        return gameDuration;
    }

    public void setGameDuration(Integer gameDuration) {
        this.gameDuration = gameDuration;
    }

    public Integer getExplosionTimeout() {
        return explosionTimeout;
    }

    public void setExplosionTimeout(Integer explosionTimeout) {
        this.explosionTimeout = explosionTimeout;
    }

    public Integer getExplosionDuration() {
        return explosionDuration;
    }

    public void setExplosionDuration(Integer explosionDuration) {
        this.explosionDuration = explosionDuration;
    }

    public Integer getExplosionRange() {
        return explosionRange;
    }

    public void setExplosionRange(Integer explosionRange) {
        this.explosionRange = explosionRange;
    }

    public Double getRobotSpeed() {
        return robotSpeed;
    }

    public void setRobotSpeed(Double robotSpeed) {
        this.robotSpeed = robotSpeed;
    }

    public Integer getPointsPerRobot() {
        return pointsPerRobot;
    }

    public void setPointsPerRobot(Integer pointsPerRobot) {
        this.pointsPerRobot = pointsPerRobot;
    }

    public Integer getPointsPerPlayer() {
        return pointsPerPlayer;
    }

    public void setPointsPerPlayer(Integer pointsPerPlayer) {
        this.pointsPerPlayer = pointsPerPlayer;
    }

    public Types[][] getMap() {
        return map;
    }

    // probably not in use...
    public void setMap(Types[][] map) {
        this.map = map;
    }

    public ArrayList<Robot> getRobots() {
        return robots;
    }

    public void setRobots(ArrayList<Robot> robots) {
        this.robots = robots;
    }

    public Stack<Player> getPlayers() {
        return players;
    }

    public void setPlayers(Stack<Player> players) {
        this.players = players;
    }
}
