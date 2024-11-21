package screen;

import engine.*;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class MultiRoomScreen extends Screen {
    private static MultiRoomScreen instance;
    private final SoundManager soundManager = SoundManager.getInstance();
    private static final int SELECTION_TIME = 200;
    private int selectedRow;
    private static int TOTAL_ROWS;
    private int difficultyLevel;

    private final Cooldown selectionCooldown;
    protected int returnCode;
    private List<String> string = new ArrayList<String>();

    public MultiRoomScreen(final int width, final int height, final int fps) {
        super(width, height, fps);
        TOTAL_ROWS = 1+string.size();
        selectedRow = 0;
        this.difficultyLevel = 1;
        string.add("s");
        string.add("t");
        string.add("s");
        string.add("t");

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
        if(this.inputDelay.checkFinished() && this.selectionCooldown.checkFinished()){
            if(inputManager.isKeyDown(KeyEvent.VK_DOWN)){
                this.selectedRow = (this.selectedRow + 1) % TOTAL_ROWS;
                this.selectionCooldown.reset();
                soundManager.playSound(Sound.MENU_MOVE);
            }else if(inputManager.isKeyDown(KeyEvent.VK_UP)){
                this.selectedRow = (this.selectedRow - 1) % TOTAL_ROWS;
                this.selectionCooldown.reset();
                soundManager.playSound(Sound.MENU_MOVE);
            }else if(inputManager.isKeyDown(KeyEvent.VK_ESCAPE)){
                this.returnCode = 1;
                this.isRunning = false;
                soundManager.playSound(Sound.MENU_BACK);
            }
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

        drawManager.drawNewRoom(this, true, difficultyLevel);
        for(int i = 0; i < string.size(); i++){
            drawManager.drawSelectRoom(this, true, string.get(i), i, string.size());
        }

        drawManager.completeDrawing(this);

        Core.setLevelSetting(this.difficultyLevel);
    }
}
