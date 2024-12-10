
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
    private static final int IMAGE_TIME = 100;

    private final SoundManager soundManager = SoundManager.getInstance();

    private Cooldown skipCooldown;
    private Cooldown fontCooldown;
    private Cooldown imageCooldown;

    private int level;
    private int speech;
    private int count;
    private int seccount;
    private int num;
    private int imagex = 42;
    private int imagey =20;
    private int imagewidth=500;
    private int imageheight = 380;

    private static BufferedImage img_story11;
    private static BufferedImage img_story12;
    private static BufferedImage img_story13;
    private static BufferedImage img_story14;
    private static BufferedImage img_story15;
    private static BufferedImage img_story16;
    private static BufferedImage img_story17;
    private static BufferedImage img_story18;
    private static BufferedImage img_story21;
    private static BufferedImage img_story22;
    private static BufferedImage img_story23;
    private static BufferedImage img_story24;
    private static BufferedImage img_story25;
    private static BufferedImage img_story26;
    private static BufferedImage img_story27;
    private static BufferedImage img_story28;
    private static BufferedImage img_story29;
    private static BufferedImage img_story210;
    private static BufferedImage img_story211;
    private static BufferedImage img_story212;
    private static BufferedImage img_story213;
    private static BufferedImage img_story214;
    private static BufferedImage img_story215;
    private static BufferedImage img_story216;



    public StoryScreen(final GameState gameState, int width, int height, int fps) {
        super(width, height, fps);

        this.level = gameState.getLevel();
        this.skipCooldown = Core.getCooldown(SKIP_TIME);
        this.fontCooldown = Core.getCooldown(FONT_TIME);
        this.imageCooldown = Core.getCooldown(IMAGE_TIME);
        this.skipCooldown.reset();
        speech =0;
        count = 0;
        seccount = 0;
        num = 1;
        // story image
        try{
            img_story11 = ImageIO.read(new File("res/image/boss01/보스 디자인1.png"));
            img_story12 = ImageIO.read(new File("res/image/boss01/보스 디자인2.png"));
            img_story13 = ImageIO.read(new File("res/image/boss01/보스 디자인3.png"));
            img_story14 = ImageIO.read(new File("res/image/boss01/보스 디자인4.png"));
            img_story15 = ImageIO.read(new File("res/image/boss01/보스 디자인5.png"));
            img_story16 = ImageIO.read(new File("res/image/boss01/보스 디자인6.png"));
            img_story17 = ImageIO.read(new File("res/image/boss01/보스 디자인7.png"));
            img_story18 = ImageIO.read(new File("res/image/boss01/보스 디자인8.png"));

            img_story21 = ImageIO.read(new File("res/image/boss02/보스 디자인2.png"));
            img_story22 = ImageIO.read(new File("res/image/boss02/보스 디자인3.png"));
            img_story23 = ImageIO.read(new File("res/image/boss02/보스 디자인4.png"));
            img_story24 = ImageIO.read(new File("res/image/boss02/보스 디자인5.png"));
            img_story25 = ImageIO.read(new File("res/image/boss02/보스 디자인6.png"));
            img_story26 = ImageIO.read(new File("res/image/boss02/보스 디자인7.png"));
            img_story27 = ImageIO.read(new File("res/image/boss02/보스 디자인8.png"));
            img_story28 = ImageIO.read(new File("res/image/boss02/보스 디자인9.png"));
            img_story29 = ImageIO.read(new File("res/image/boss02/보스 디자인10.png"));
            img_story210 = ImageIO.read(new File("res/image/boss02/보스 디자인11.png"));
            img_story211 = ImageIO.read(new File("res/image/boss02/보스 디자인12.png"));
            img_story212 = ImageIO.read(new File("res/image/boss02/보스 디자인13.png"));
            img_story213 = ImageIO.read(new File("res/image/boss02/보스 디자인14.png"));
            img_story214 = ImageIO.read(new File("res/image/boss02/보스 디자인15.png"));
            img_story215 = ImageIO.read(new File("res/image/boss02/보스 디자인16.png"));
            img_story216 = ImageIO.read(new File("res/image/boss02/보스 디자인17.png"));
        } catch (IOException e){
            logger.info("Story image loading failed");
        }
    }

    public void initialize() {
        if (soundManager.isSoundPlaying(Sound.BGM_MAIN))
            soundManager.stopSound(Sound.BGM_MAIN);
        if(level == 3){
            soundManager.loopSound(Sound.BGM_STORY);
        }else if(level == 6){
            soundManager.loopSound(Sound.BGM_STORY2);
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

        Font font = new Font("Arial", Font.PLAIN, 20);
        DrawManager.getBackBufferGraphics().setFont(font);

        if(level == 3){
            //이미지 띄우기
            if(num == 1){
                DrawManager.getBackBufferGraphics().drawImage(img_story11, imagex, imagey , imagewidth, imageheight,null);
            }else if(num == 2){
                DrawManager.getBackBufferGraphics().drawImage(img_story12,imagex, imagey , imagewidth, imageheight,null);
            }else if(num == 3){
                DrawManager.getBackBufferGraphics().drawImage(img_story13,imagex, imagey , imagewidth, imageheight,null);
            }else if(num == 4){
                DrawManager.getBackBufferGraphics().drawImage(img_story14,imagex, imagey , imagewidth, imageheight,null);
            }else if(num == 5){
                DrawManager.getBackBufferGraphics().drawImage(img_story15,imagex, imagey , imagewidth, imageheight,null);
            }else if(num == 6){
                DrawManager.getBackBufferGraphics().drawImage(img_story16,imagex, imagey , imagewidth, imageheight,null);
            }else if(num == 7){
                DrawManager.getBackBufferGraphics().drawImage(img_story17,imagex, imagey , imagewidth, imageheight,null);
            }else if(num == 8){
                DrawManager.getBackBufferGraphics().drawImage(img_story18,imagex, imagey , imagewidth, imageheight,null);
            }
            if(this.imageCooldown.checkFinished()) { // 이미지 출력속도 조절
                if (num == 8) {
                    num = 0;
                }
                num++;
                this.imageCooldown.reset();
            }

            String[] s = {"So, we meet again, Earthlings.", "Why am I fighting on the side of the Zyrathians,", "Heheh... Well, perhaps you should be more worried ","I have no intention of letting you leave alive. Hahaha!", "This is the perfect opportunity to test my beauties."};
            String[] m = {""," you ask?","about your own situation right now.", "", " Good luck." };

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
                }else{  // 첫 줄 대사가 다 출력되면 그상태 유지
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
                this.isRunning = false;
            }
        }else if(level == 6){
            //이미지 띄우기
            if(num == 1){
                DrawManager.getBackBufferGraphics().drawImage(img_story21,imagex, imagey , imagewidth, imageheight,null);
            }else if(num == 2){
                DrawManager.getBackBufferGraphics().drawImage(img_story22,imagex, imagey , imagewidth, imageheight,null);
            }else if(num == 3){
                DrawManager.getBackBufferGraphics().drawImage(img_story23,imagex, imagey , imagewidth, imageheight,null);
            }else if(num == 4){
                DrawManager.getBackBufferGraphics().drawImage(img_story24,imagex, imagey , imagewidth, imageheight,null);
            }else if(num == 5){
                DrawManager.getBackBufferGraphics().drawImage(img_story25,imagex, imagey , imagewidth, imageheight,null);
            }else if(num == 6){
                DrawManager.getBackBufferGraphics().drawImage(img_story26,imagex, imagey , imagewidth, imageheight,null);
            }else if(num == 7){
                DrawManager.getBackBufferGraphics().drawImage(img_story27,imagex, imagey , imagewidth, imageheight,null);
            }else if(num == 8){
                DrawManager.getBackBufferGraphics().drawImage(img_story28,imagex, imagey , imagewidth, imageheight,null);
            }else if(num == 9){
                DrawManager.getBackBufferGraphics().drawImage(img_story29,imagex, imagey , imagewidth, imageheight,null);
            }else if(num == 10){
                DrawManager.getBackBufferGraphics().drawImage(img_story210,imagex, imagey , imagewidth, imageheight,null);
            }else if(num == 11){
                DrawManager.getBackBufferGraphics().drawImage(img_story211,imagex, imagey , imagewidth, imageheight,null);
            }else if(num == 12){
                DrawManager.getBackBufferGraphics().drawImage(img_story212,imagex, imagey , imagewidth, imageheight,null);
            }else if(num == 13){
                DrawManager.getBackBufferGraphics().drawImage(img_story213,imagex, imagey , imagewidth, imageheight,null);
            }else if(num == 14){
                DrawManager.getBackBufferGraphics().drawImage(img_story214,imagex, imagey , imagewidth, imageheight,null);
            }else if(num == 15){
                DrawManager.getBackBufferGraphics().drawImage(img_story215,imagex, imagey , imagewidth, imageheight,null);
            }else if(num == 16){
                DrawManager.getBackBufferGraphics().drawImage(img_story216,imagex, imagey , imagewidth, imageheight,null);
            }
            if(this.imageCooldown.checkFinished()) { // 이미지 출력속도 조절
                if (num == 16) {
                    num = 0;
                }
                num++;
                this.imageCooldown.reset();
            }

            String[] t = {"The fact that you’ve come this far… ", "I will never forgive you... "};
            String[] r = {"you’re ruthless beings.","I won’t let you go unpunished!"};

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
                this.isRunning = false;
            }
        }

        drawManager.completeDrawing(this);
    }



}
