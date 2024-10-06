package entity;

import java.awt.*;

public class Web extends Entity{

    private static boolean threadWeb = false;

    /**
     * Constructor, establishes the entity's generic properties.
     *
     * @param positionX Initial position of the entity in the X axis.
     * @param positionY Initial position of the entity in the Y axis.
     */
    public Web(int positionX, int positionY) {
        super(positionX, positionY, 13 * 2, 8 * 2, Color.WHITE);
    }

    public void threadWeb(){
        threadWeb = true;
    }

    public static boolean isThreadWeb() {
        return threadWeb;
    }
}
