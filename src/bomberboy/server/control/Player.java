package bomberboy.server.control;

public class Player {

    private Integer x;
    private Integer y;
    private Integer id;
    private Integer points;
    private String url;
    private String name;
    private Boolean bomb;
    private Boolean dead;

    public Player(Integer x, Integer y, int id) {
	this.x = x;
	this.y = y;
	this.id = id;
	this.bomb = false;
	this.dead = false;
	this.points = 0;
    }

    public int getX() {
	return x;
    }
    public int getY() {
	return y;
    }
    public int getID() {
	return id;
    }
    public int getPoints() {
	return points;
    }
    public void incPoints(int val) {
	this.points += val;
    }
    public void incX() {
	x++;
    }
    public void incY() {
	y++;
    }
    public void decX() {
	x--;
    }
    public void decY() {
	y--;
    }
    public String getName() {
	return name;
    }
    public void setName(String s) {
	this.name = s;
    }
    public String getURL() {
	return url;
    }
    public void setURL(String url) {
	this.url = url;
    }
}
