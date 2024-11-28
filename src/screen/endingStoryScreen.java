
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


public class endingStoryScreen extends Screen{
    private static final int SKIP_TIME = 1000;
    private static final int FONT_TIME = 100;

    private Cooldown skipCooldown;
    private Cooldown fontCooldown;

    private int level;

    private int speech;

    private int count;
    private static BufferedImage img_story1;
    private static BufferedImage img_story2;



    public endingStoryScreen(final GameState gameState, int width, int height, int fps) {
        super(width, height, fps);

        this.level = gameState.getLevel();
        this.skipCooldown = Core.getCooldown(SKIP_TIME);
        this.fontCooldown = Core.getCooldown(FONT_TIME);
        this.skipCooldown.reset();
        speech =0;
        count = 0;
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

        Font font = new Font("Arial", Font.PLAIN, 24);
        DrawManager.getBackBufferGraphics().setFont(font);

        if(level == 1){
            //이미지 띄우기
            DrawManager.getBackBufferGraphics().drawImage(img_story1,42, 100 , 500, 300,null);

            String[] s = {"dopamindoctor", "qqwqeewwwwwwwwwesd", "this is story 1 ending simulation"};

            if(this.skipCooldown.checkFinished() && this.inputDelay.checkFinished()){
                if(inputManager.isKeyDown(KeyEvent.VK_SPACE)){
                    if(count < s[speech].length()){
                        count = s[speech].length();
                    }else{
                        count = 0;
                        speech++;
                    }
                    this.skipCooldown.reset();

                }
            }

            if(speech < s.length){  //대사를 다 안쳤을 때
                if(count < s[speech].length()){   // 대사가 다 출력되지 않았을 때
                    DrawManager.getBackBufferGraphics().drawString(s[speech].substring(0, count), 60, 440);
                    if(this.fontCooldown.checkFinished()) {
                        count++;
                        this.fontCooldown.reset();
                    }
                }else{  // 대사가 다 출력되면 그상태 유지
                    DrawManager.getBackBufferGraphics().drawString(s[speech].substring(0,count),60, 440);
                }
            }else{   //대사가 다 나온 후 스페이스 바 누르면 스토리 화면 종료
                this.isRunning = false;
            }
        }else if(level == 3){
            //이미지 띄우기
            DrawManager.getBackBufferGraphics().drawImage(img_story2,42, 100 , 500, 300,null);

            String[] t = {"endingendingendingending", "show me", "dopamin"};

            if(this.skipCooldown.checkFinished() && this.inputDelay.checkFinished()){
                if(inputManager.isKeyDown(KeyEvent.VK_SPACE)){
                    if(count < t[speech].length()){
                        count = t[speech].length();
                    }else{
                        count = 0;
                        speech++;
                    }
                    this.skipCooldown.reset();

                }
            }
            if(speech < t.length){
                if(count < t[speech].length()){   // 대사가 다 출력되지 않았을 때
                    DrawManager.getBackBufferGraphics().drawString(t[speech].substring(0, count), 60, 440);
                    if(this.fontCooldown.checkFinished()) {
                        count++;
                        this.fontCooldown.reset();
                    }
                }else{  // 대사가 다 출력되면 그상태 유지
                    DrawManager.getBackBufferGraphics().drawString(t[speech].substring(0,count),60, 440);
                }
            }else{
                this.isRunning = false;
            }
        }

        drawManager.completeDrawing(this);
    }



}