package entity;

import engine.DrawManager.SpriteType;
import java.util.Random;
import java.awt.*;

public class Blocker extends Entity {

    private Random random;
    /* 각도 */
    private double angle;
    /* 왼쪽으로 움직일 것인가? */
    private boolean moveLeft;

    public Blocker(int positionX, int positionY, final SpriteType spriteType, boolean moveLeft) {
        //super(positionX, positionY, 182 * 2, 93 * 2, Color.GREEN);
        super(positionX, positionY, 82 * 2, 81 * 2, Color.GREEN);
        this.spriteType = spriteType;
        this.random = new Random();
        angle = 180 * random.nextDouble();
        this.moveLeft = moveLeft;
    }

    public final void move(final double distanceX, final double distanceY) {
        this.positionX += distanceX;
        this.positionY += distanceY;
    }

    public final void rotate(final double degree) { angle += degree; }

    public double getAngle() { return angle; }

    public boolean getMoveLeft() { return moveLeft; }
}
