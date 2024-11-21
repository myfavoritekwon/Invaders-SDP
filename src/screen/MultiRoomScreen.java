package screen;

import engine.*;
import entity.Room;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class MultiRoomScreen extends Screen {
    private static MultiRoomScreen instance;
    private final SoundManager soundManager = SoundManager.getInstance();
    private static final int SELECTION_TIME = 200;
    private static int ErrorCheck;
    private int difficultyLevel;
    private int Button;

    private final Cooldown selectionCooldown;
    protected int returnCode;
    private boolean checkDraw = false;

    public MultiRoomScreen(final int width, final int height, final int fps) {
        super(width, height, fps);
        this.difficultyLevel = 1;

        this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
        this.selectionCooldown.reset();
    }

    public final int run(){
        super.run();
        return this.returnCode;
    }

    protected final void update(){
        super.update();

        draw();
        if(ErrorCheck == 2){
            ErrorCheck = 0;
            this.returnCode = 1;
            this.isRunning = false;
            soundManager.playSound(Sound.MENU_MOVE);
        }
    }

    public static MultiRoomScreen getInstance() {
        if (instance == null) {
            instance = new MultiRoomScreen(0,0,0);
        }
        return instance;
    }

    private void draw(){
        drawManager.initDrawing(this);

        drawManager.drawMatching(this);
        drawManager.completeDrawing(this);

        Core.setLevelSetting(this.difficultyLevel);
    }

    public static void getErrorCheck(int error){
        ErrorCheck = error;
    }
}
