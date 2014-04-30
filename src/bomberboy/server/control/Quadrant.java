package bomberboy.server.control;

public enum Quadrant {
    TOPLEFT(Movements.LEFT, Movements.UP, Movements.RIGHT, Movements.DOWN),
    TOPRIGHT(Movements.RIGHT, Movements.UP, Movements.LEFT, Movements.DOWN),
    BOTTOMLEFT(Movements.LEFT, Movements.DOWN, Movements.RIGHT, Movements.UP),
    BOTTOMRIGHT(Movements.RIGHT, Movements.DOWN, Movements.LEFT, Movements.UP);

    private final Movements moveOne;
    private final Movements moveTwo;
    private final Movements moveThree;
    private final Movements moveFour;

    Quadrant(Movements moveOne, Movements moveTwo, Movements moveThree, Movements moveFour) {
        this.moveOne = moveOne;
        this.moveTwo = moveTwo;
        this.moveThree = moveThree;
        this.moveFour = moveFour;
    }

    public Movements getMoveOne() {
        return moveOne;
    }

    public Movements getMoveTwo() {
        return moveTwo;
    }

    public Movements getMoveThree() {
        return moveThree;
    }

    public Movements getMoveFour() {
        return moveFour;
    }

    public static Quadrant getQuadrant(Player p, Robot r) {
        if (p.getX() <= r.getX()) {
            if (p.getY() <= r.getY()) {
                return TOPLEFT;
            } else {
                return TOPRIGHT;
            }
        } else {
            if (p.getY() <= r.getY()) {
                return BOTTOMLEFT;
            } else {
                return BOTTOMRIGHT;
            }
        }
    }
}
