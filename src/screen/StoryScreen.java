
package screen;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import engine.*;

import javax.imageio.ImageIO;


public class StoryScreen extends Screen{
    private static final int SKIP_TIME = 1000;
    private static final int FONT_TIME = 100;

    private final SoundManager soundManager = SoundManager.getInstance();

    private Cooldown skipCooldown;
    private Cooldown fontCooldown;

    private int level;

    private int speech;

    private int count;
    private int seccount;
    private static BufferedImage img_story1;
    private static BufferedImage img_story2;



    public StoryScreen(final GameState gameState, int width, int height, int fps) {
        super(width, height, fps);

        this.level = gameState.getLevel();
        this.skipCooldown = Core.getCooldown(SKIP_TIME);
        this.fontCooldown = Core.getCooldown(FONT_TIME);
        this.skipCooldown.reset();
        speech =0;
        count = 0;
        seccount = 0;
        // story image
        try{
            img_story1 = ImageIO.read(new File("res/image/story1.png"));
            img_story2 = ImageIO.read(new File("res/image/tosung.jpg"));
        } catch (IOException e){
            logger.info("Story image loading failed");
        }
    }

    public void initialize() {
        if (soundManager.isSoundPlaying(Sound.BGM_MAIN))
            soundManager.stopSound(Sound.BGM_MAIN);
        soundManager.loopSound(Sound.BGM_STORY);
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

        Font font = new Font("Arial", Font.PLAIN, 20);
        DrawManager.getBackBufferGraphics().setFont(font);

        if(level == 1){
            //이미지 띄우기
            DrawManager.getBackBufferGraphics().drawImage(img_story1,42, 100 , 500, 300,null);

            String[] s = {"You dare step into my domain? Foolish  ", "So, you think you’re worthy of challenging", "Bravery, or foolishness? You "};
            String[] m = {"mortals, prepare to face annihilation!"," me? Very well, I shall oblige!","shall soon see the cost of your arrogance." };

            if(this.skipCooldown.checkFinished() && this.inputDelay.checkFinished()){
                if(inputManager.isKeyDown(KeyEvent.VK_SPACE)){
                    if(count < s[speech].length() || seccount < m[speech].length()){
                        count = s[speech].length();
                        seccount = m[speech].length();
                    }else{
                        count = 0;
                        seccount =0;
                        speech++;
                    }
                    this.skipCooldown.reset();

                }
            }

            if(speech < s.length){  //대사들을 다 안쳤을 때
                if(count < s[speech].length()){   // 대사가 다 출력되지 않았을 때
                    DrawManager.getBackBufferGraphics().drawString(s[speech].substring(0, count), 60, 440);
                    if(this.fontCooldown.checkFinished()) { // 대사 출력속도 조절
                        count++;
                        this.fontCooldown.reset();
                    }
                }else{  // 대사가 다 출력되면 그상태 유지
                    if(seccount < m[speech].length()){   // 대사가 다 출력되지 않았을 때
                        DrawManager.getBackBufferGraphics().drawString(m[speech].substring(0, seccount), 60, 480);
                        if(this.fontCooldown.checkFinished()) { // 대사 출력속도 조절
                            seccount++;
                            this.fontCooldown.reset();
                        }
                    }else{
                        DrawManager.getBackBufferGraphics().drawString(m[speech].substring(0, seccount), 60, 480);
                    }
                    DrawManager.getBackBufferGraphics().drawString(s[speech].substring(0,count),60, 440);
                }
            }else{   //대사가 다 나온 후 스페이스 바 누르면 스토리 화면 종료
                soundManager.stopSound(Sound.BGM_STORY);
                this.isRunning = false;
            }
        }else if(level == 3){
            //이미지 띄우기
            DrawManager.getBackBufferGraphics().drawImage(img_story2,42, 100 , 500, 300,null);

            String[] t = {"Is this all you’ve got? Pathetic!", "You cannot stop me! ", "I’ve grown tired of this game. Time to end this, once and for all!"};
            String[] r = {"","You’re only delaying the inevitable."," Time to end this, once and for all!"};

            if(this.skipCooldown.checkFinished() && this.inputDelay.checkFinished()){
                if(inputManager.isKeyDown(KeyEvent.VK_SPACE)){
                    if(count < t[speech].length() || seccount < r[speech].length()){
                        count = t[speech].length();
                        seccount = r[speech].length();
                    }else{
                        count = 0;
                        seccount =0;
                        speech++;
                    }
                    this.skipCooldown.reset();

                }
            }

            if(speech < t.length){  //대사들을 다 안쳤을 때
                if(count < t[speech].length()){   // 대사가 다 출력되지 않았을 때
                    DrawManager.getBackBufferGraphics().drawString(t[speech].substring(0, count), 60, 440);
                    if(this.fontCooldown.checkFinished()) { // 대사 출력속도 조절
                        count++;
                        this.fontCooldown.reset();
                    }
                }else{  // 대사가 다 출력되면 그상태 유지
                    if(seccount < r[speech].length()){   // 대사가 다 출력되지 않았을 때
                        DrawManager.getBackBufferGraphics().drawString(r[speech].substring(0, seccount), 60, 480);
                        if(this.fontCooldown.checkFinished()) { // 대사 출력속도 조절
                            seccount++;
                            this.fontCooldown.reset();
                        }
                    }else{
                        DrawManager.getBackBufferGraphics().drawString(r[speech].substring(0, seccount), 60, 480);
                    }
                    DrawManager.getBackBufferGraphics().drawString(t[speech].substring(0,count),60, 440);
                }
            }else{
                soundManager.stopSound(Sound.BGM_STORY);
                this.isRunning = false;
            }
        }

        drawManager.completeDrawing(this);
    }



}
