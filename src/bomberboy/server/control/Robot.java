package bomberboy.server.control;

import bomberboy.server.map.SettingsReader;

public class Robot extends Thread {

    private static double SLEEPTIME;
    private static int THRESHOLD = 5;

    private Integer x;
    private Integer y;
    private Boolean dead;
    private Boolean bomb;
    private Integer id;

    public Robot(Integer x, Integer y) {
        this.x = x;
        this.y = y;
        this.dead = false;
        this.bomb = false;
    }

    public void increaseScore(int points) {
    }

    public void initializeSettings() {
        SLEEPTIME = (1 / SettingsReader.getSettings().getRobotSpeed()) * 1000;
    }

    public void setID(Integer id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }

    private Double getDistance(Player p) {
        return Math.abs(Math.sqrt(Math.pow(p.getX() - getX(), 2) + Math.pow(p.getY() - getY(), 2)));
    }


    private Player getClosestPlayer() {
        // Player player = null;
        // Double currentDistance = 0d;

        // for (Player p : status.getPlayers()) {
        //     Double cD = getDistance(p);
        //     if (player == null) {
        //         player = p;
        //         currentDistance = cD;
        //     } else if (cD < currentDistance) {
        //         player = p;
        //         currentDistance = cD;
        //     }
        // }

        // return player;
	return null;
	}

    @Override
    public void run() {
        while (!dead) {
            try {
                Thread.sleep((long) SLEEPTIME);
                Player p = getClosestPlayer();
                Quadrant closest = Quadrant.getQuadrant(p, this);
                Double cD = getDistance(p);

                if (cD < THRESHOLD) dropBomb();

                if (move(closest.getMoveOne())) {
                } else if (move(closest.getMoveTwo())) {
                } else if (move(closest.getMoveThree())) {
                } else {
                    move(closest.getMoveFour());
                }
            } catch (InterruptedException e) {
                killRobot();
            }
        }
    }

    public void stopRobot() {
        dead = true;
    }

    private void killRobot() {
        this.dead = true;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void incrX() {
        x++;
    }

    public void decrX() {
        x--;
    }

    public void incrY() {
        y++;
    }

    public void decrY() {
        y--;
    }

    public boolean move(Movements e) {
        return true; //status.move(e, id);
    }

    public boolean dropBomb() {
        return true; //status.dropBomb(id);
    }

    public boolean hasBomb() {
        return bomb;
    }

    public void toggleBomb() {
        bomb = !bomb;
    }
}
