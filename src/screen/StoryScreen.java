
package screen;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import engine.Cooldown;
import engine.Core;
import engine.GameState;
import engine.DrawManager;

import javax.imageio.ImageIO;


public class StoryScreen extends Screen{
    private static final int SKIP_TIME = 1000;

    private Cooldown skipCooldown;

    private int level;

    private int speech;

    private static BufferedImage img_story1;
    private static BufferedImage img_story2;



    public StoryScreen(final GameState gameState, int width, int height, int fps) {
        super(width, height, fps);

        this.level = gameState.getLevel();
        this.skipCooldown = Core.getCooldown(SKIP_TIME);
        this.skipCooldown.reset();
        speech =0;

        // story image
        try{
            img_story1 = ImageIO.read(new File("res/image/story1.png"));
            img_story2 = ImageIO.read(new File("res/image/tosung.jpg"));
        } catch (IOException e){
            logger.info("Story image loading failed");
        }
    }


    public final int run() {
        super.run();

        return this.returnCode;
    }


    protected void update() {
        super.update();
        draw();

    }


    private void draw() {
        drawManager.initDrawing(this);
        drawManager.drawStory(this);


        //이미지 띄우기
        if(level == 1){
            DrawManager.getBackBufferGraphics().drawImage(img_story1,42, 100 , 500, 300,null);
        }else if(level == 3){
            DrawManager.getBackBufferGraphics().drawImage(img_story2,42, 100 , 500, 300,null);
        }

        Font font = new Font("Arial", Font.PLAIN, 24);
        DrawManager.getBackBufferGraphics().setFont(font);

        if(level == 1){
            String[] s = {"story 1", "hello", "this is story 1 simulation"};

            if(this.skipCooldown.checkFinished() && this.inputDelay.checkFinished()){
                if(inputManager.isKeyDown(KeyEvent.VK_SPACE)){

                    speech++;
                    this.skipCooldown.reset();

                }
            }
            if(speech < 3){
                DrawManager.getBackBufferGraphics().drawString(s[speech],60, 440);
            }else{
                this.isRunning = false;
            }
        }else if(level == 3){
            String[] s = {"suuuuuuuuuuuuuuuui", "show me the money", "sogesil---------------------------"};

            if(this.skipCooldown.checkFinished() && this.inputDelay.checkFinished()){
                if(inputManager.isKeyDown(KeyEvent.VK_SPACE)){

                    speech++;
                    this.skipCooldown.reset();

                }
            }
            if(speech < 3){
                DrawManager.getBackBufferGraphics().drawString(s[speech],60, 440);
            }else{
                this.isRunning = false;
            }
        }


        drawManager.completeDrawing(this);
    }



}
