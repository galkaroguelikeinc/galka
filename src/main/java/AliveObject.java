public abstract class AliveObject extends GameObject {
    private int xPos;
    private int yPos;

    AliveObject(int x, int y) {
        xPos = x;
        yPos = y;
    }

    void move(int x, int y) {
        xPos = x;
        yPos = y;
    }

    int getxPos() {
        return xPos;
    }

    int getyPos() {
        return yPos;
    }
}
