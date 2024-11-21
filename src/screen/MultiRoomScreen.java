package screen;

import engine.*;
import entity.Room;

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
    private List<Room> rooms = new ArrayList<>();

    private final Cooldown selectionCooldown;
    protected int returnCode;
    private boolean checkDraw = false;

    public MultiRoomScreen(final int width, final int height, final int fps) {
        super(width, height, fps);
        rooms = Core.getRooms();
        this.difficultyLevel = 1;
        TOTAL_ROWS = 1+rooms.size();

        this.selectedRow = 0;

        this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
        this.selectionCooldown.reset();
    }

    public final int run(){
        super.run();

        return this.returnCode;
    }

    protected final void update(){
        super.update();

        if(!checkDraw){draw();}
        else{draw2();}
        if(this.inputDelay.checkFinished() && this.selectionCooldown.checkFinished()){
            if(inputManager.isKeyDown(KeyEvent.VK_DOWN)){
                this.selectedRow = (this.selectedRow + 1) % TOTAL_ROWS;
                this.selectionCooldown.reset();
                soundManager.playSound(Sound.MENU_MOVE);
            }else if(inputManager.isKeyDown(KeyEvent.VK_UP)){
                this.selectedRow = (this.selectedRow - 1+TOTAL_ROWS) % TOTAL_ROWS;
                this.selectionCooldown.reset();
                soundManager.playSound(Sound.MENU_MOVE);
            }else if(inputManager.isKeyDown(KeyEvent.VK_ESCAPE)){
                this.returnCode = 1;
                this.isRunning = false;
                soundManager.playSound(Sound.MENU_BACK);
            }
            if(this.selectedRow==0 && inputManager.isKeyDown(KeyEvent.VK_SPACE)){
                checkDraw = true;
            }
            if(checkDraw){
                if(this.inputDelay.checkFinished() && this.selectionCooldown.checkFinished()){
                    if(inputManager.isKeyDown(KeyEvent.VK_LEFT)){
                        if (this.difficultyLevel != 0) {
                            this.difficultyLevel--;
                            this.selectionCooldown.reset();
                            soundManager.playSound(Sound.MENU_MOVE);
                        }
                    }else if(inputManager.isKeyDown(KeyEvent.VK_RIGHT)){
                        if (this.difficultyLevel != 2) {
                            this.difficultyLevel++;
                            this.selectionCooldown.reset();
                            soundManager.playSound(Sound.MENU_MOVE);
                        }
                    }else if(inputManager.isKeyDown(KeyEvent.VK_ENTER)){
                        this.returnCode = 10;
                        this.isRunning = false;
                        soundManager.playSound(Sound.MENU_CLICK);
                    }else if(inputManager.isKeyDown(KeyEvent.VK_BACK_SPACE)){
                        checkDraw = false;
                    }
                }
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

        drawManager.drawNewRoom(this, this.selectedRow==0, difficultyLevel);
        for(int i = 0; i < rooms.size(); i++){
            drawManager.drawSelectRoom(this, i+1 == this.selectedRow, rooms.get(i).getDifficulty(), i);
        }

        drawManager.completeDrawing(this);

        Core.setLevelSetting(this.difficultyLevel);
    }

    private void draw2(){
        drawManager.initDrawing(this);

        drawManager.drawNewRoom(this, this.selectedRow==0, difficultyLevel);
        drawManager.drawSelectDifficulty(this, this.difficultyLevel);
        for(int i = 0; i < rooms.size(); i++){
            drawManager.drawSelectRoom(this, i+1 == this.selectedRow, rooms.get(i).getDifficulty(), i);
        }

        drawManager.completeDrawing(this);

        Core.setLevelSetting(this.difficultyLevel);
    }
}
