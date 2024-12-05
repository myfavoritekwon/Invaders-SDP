package entity;

import engine.DrawManager;
import engine.Sound;

import java.awt.*;

public class BonusBoss extends Entity {
    private int health;
    private boolean isDestroyed;
    private int radius;

    public BonusBoss(int positionX, int positionY, int width, int height) {
        super(positionX, positionY, width, height, Color.WHITE);
        isDestroyed = false;
        health = 2;
        radius = 127;
    }

    public void HealthManageDestroy() {
        System.out.println(health);
        if(this.health <= 0) {
            this.isDestroyed = true;
            System.out.println("destroyed");
        } else {
            health--;
        }
    }

    public int getHealth() { return health; }

    public final void move(final double distanceX, final double distanceY) {
        this.positionX += distanceX;
        this.positionY += distanceY;
    }

    public final void destroy(final float balance) {
        this.isDestroyed = true;
    }

    public int getRadius() { return radius; }

    public boolean isDestroyed() { return isDestroyed; };
}
