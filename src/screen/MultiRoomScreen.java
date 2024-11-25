package screen;

import engine.*;
import engine.Socket.Client;
import engine.Socket.Server;

public class MultiRoomScreen extends Screen{
    private static MultiRoomScreen instance;
    private final SoundManager soundManager = SoundManager.getInstance();
    private final int width;
    private final int height;
    private final int FPS;
    private static final int SELECTION_TIME = 200;
    private static int ErrorCheck;
    private int difficultyLevel;

    private final Cooldown selectionCooldown;
    protected int returnCode;
    private Server server;
    private Client client;

    public MultiRoomScreen(final int width, final int height, final int fps, Server server, Client client) {
        super(width, height, fps);

        this.width = width;
        this.height = height;
        this.FPS = fps;
        this.server = server;
        this.client = client;

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
        if(ErrorCheck == 1){
            ErrorCheck = 0;
            this.returnCode = 1;
            this.isRunning = false;
            soundManager.playSound(Sound.MENU_MOVE);
        }else if(ErrorCheck == 2){
            ErrorCheck = 0;
            this.returnCode = 10;
            this.isRunning = false;
            soundManager.playSound(Sound.MENU_CLICK);
            soundManager.stopSound(Sound.BGM_MAIN);
        }else if(Server.getReturnCode() == 10){
            this.returnCode = 10;
            this.isRunning = false;
            soundManager.playSound(Sound.MENU_CLICK);
            soundManager.stopSound(Sound.BGM_MAIN);
        }
    }

    public MultiRoomScreen getInstance() {
        if (instance == null) {
            instance = new MultiRoomScreen(width, height, FPS, server, client);
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
