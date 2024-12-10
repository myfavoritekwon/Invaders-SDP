
package screen;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import engine.*;

import javax.imageio.ImageIO;


public class EndingStoryScreen extends Screen{
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
    private static BufferedImage img_story217;



    public EndingStoryScreen(final GameState gameState, int width, int height, int fps) {
        super(width, height, fps);

        this.level = gameState.getLevel();
        this.skipCooldown = Core.getCooldown(SKIP_TIME);
        this.fontCooldown = Core.getCooldown(FONT_TIME);
        this.imageCooldown = Core.getCooldown(IMAGE_TIME);
        this.skipCooldown.reset();
        speech =0;
        count = 0;
        seccount=0;
        num = 1;
        // story image
        try{
            img_story11 = ImageIO.read(new File("res/image/boss01/afterclear/boss1Clear.png"));


            img_story21 = ImageIO.read(new File("res/image/boss02/afterclear/보스 디자인2 (crying)1.png"));
            img_story22 = ImageIO.read(new File("res/image/boss02/afterclear/보스 디자인2 (crying)2.png"));
            img_story23 = ImageIO.read(new File("res/image/boss02/afterclear/보스 디자인2 (crying)3.png"));
            img_story24 = ImageIO.read(new File("res/image/boss02/afterclear/보스 디자인2 (crying)4.png"));
            img_story25 = ImageIO.read(new File("res/image/boss02/afterclear/보스 디자인2 (crying)5.png"));
            img_story26 = ImageIO.read(new File("res/image/boss02/afterclear/보스 디자인2 (crying)6.png"));
            img_story27 = ImageIO.read(new File("res/image/boss02/afterclear/보스 디자인2 (crying)7.png"));
            img_story28 = ImageIO.read(new File("res/image/boss02/afterclear/보스 디자인2 (crying)8.png"));
            img_story29 = ImageIO.read(new File("res/image/boss02/afterclear/보스 디자인2 (crying)9.png"));
            img_story210 = ImageIO.read(new File("res/image/boss02/afterclear/보스 디자인2 (crying)10.png"));
            img_story211 = ImageIO.read(new File("res/image/boss02/afterclear/보스 디자인2 (crying)11.png"));
            img_story212 = ImageIO.read(new File("res/image/boss02/afterclear/보스 디자인2 (crying)12.png"));
            img_story213 = ImageIO.read(new File("res/image/boss02/afterclear/보스 디자인2 (crying)13.png"));
            img_story214 = ImageIO.read(new File("res/image/boss02/afterclear/보스 디자인2 (crying)14.png"));
            img_story215 = ImageIO.read(new File("res/image/boss02/afterclear/보스 디자인2 (crying)15.png"));
            img_story216 = ImageIO.read(new File("res/image/boss02/afterclear/보스 디자인2 (crying)16.png"));
            img_story217 = ImageIO.read(new File("res/image/boss02/afterclear/보스 디자인2 (crying)17.png"));
        } catch (IOException e){
            logger.info("Story image loading failed");
        }
    }

    public void initialize() {
        if(level == 6){
            soundManager.loopSound(Sound.BGM_ENDSTORY2);
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
            DrawManager.getBackBufferGraphics().drawImage(img_story11, imagex, imagey , imagewidth, imageheight,null);

            String[] s = {"My ultimate masterpiece... ", "Without these, I can't protect her...", "(It seems they have lost consciousness.)", "(They look human. Let’s bring them aboard "};
            String[] m = {"falling apart like this...","", "","our ship and monitor their condition.)"};

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
                soundManager.stopSound(Sound.BGM_STORY2);
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
            }else if(num == 17){
                DrawManager.getBackBufferGraphics().drawImage(img_story217,imagex, imagey , imagewidth, imageheight,null);
            }
            if(this.imageCooldown.checkFinished()) { // 이미지 출력속도 조절
                if (num == 17) {
                    num = 0;
                }
                num++;
                this.imageCooldown.reset();
            }

            String[] t = {"No... I must avenge the doctor...!", "(Wait, if you’re looking for the doctor,", "What...? If that’s the case, thank you so much.","The truth is, the doctor rescued me when I was a ", "But both of us were forced to work for the Zyrathians ", "Thanks to you, we survived. I’m truly grateful!"};
            String[] r = {"", " we have him safe and sound.)", "","captive and took care of me like a father.","under threat.", ""};

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
                soundManager.stopSound(Sound.BGM_ENDSTORY2);
                this.isRunning = false;
            }
        }

        drawManager.completeDrawing(this);
    }



}