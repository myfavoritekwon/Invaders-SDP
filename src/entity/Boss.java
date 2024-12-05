package entity;

import engine.Cooldown;
import engine.Core;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class Boss extends Entity {
    private int health;
    private boolean isDestroyed;
    private BufferedImage bossShipImage;
    private static Logger logger;
    private int who; // 1: 박사, 2: 귀욤
    private int gotoX;
    private int gotoY;
    private boolean randomMoveDone = true;
    private boolean left;
    private boolean up;
    private Cooldown nextMoveCool;

    public Boss(int positionX, int positionY, int width, int height, int who) {
        super(positionX, positionY, width, height, Color.WHITE);
        isDestroyed = false;
        this.who = who;
        nextMoveCool = Core.getCooldown(1000);
        switch (who) {
            case 1:
                health = 2;
                try {
                    bossShipImage = ImageIO.read(new File("res/image/bossShip1.png"));
                    this.width = bossShipImage.getWidth();
                    this.height = bossShipImage.getHeight();
                } catch (IOException e) {
                    logger.info("Boss image loading failed.");
                }
                break;
            case 2:
                health = 100;
                try {
                    bossShipImage = ImageIO.read(new File("res/image/bossShip2.png"));
                    this.width = bossShipImage.getWidth();
                    this.height = bossShipImage.getHeight();
                } catch (IOException e) {
                    logger.info("Boss image loading failed.");
                }
                break;
        }

    }

    public void HealthManageDestroy() {
        if(this.health <= 0) {
            this.isDestroyed = true;
        } else {
            health--;
        }
    }

    public BufferedImage getBossShipImage() { return bossShipImage; }

    public int getHealth() { return health; }

    public final void move(final double distanceX, final double distanceY) {
        this.positionX += distanceX;
        this.positionY += distanceY;
    }

    public void randomMove() {
        if (randomMoveDone) {
            // 이동이 끝났고 쿨다운이 완료되었는지 확인
            if (nextMoveCool.checkFinished()) {
                // 새로운 목표 위치 생성
                do {
                    // 새로운 목표 위치 생성
                    if (who == 1) {
                        gotoX = (int) (Math.random() * 211) - 110; // X 좌표: -90 ~ 100
                        gotoY = (int) (Math.random() * 31) - 100; // Y 좌표: -100 ~ -70
                    } else {
                        gotoX = (int) (Math.random() * 351); // X 좌표
                        gotoY = (int) (Math.random() * 51); // Y 좌표: -100 ~ -70
                    }
                } while (Math.abs(gotoX - this.getPositionX()) < 50); // X축 최소 이동 거리 50 설정

                randomMoveDone = false;
            }
            return; // 아직 쿨다운 중이거나 이동이 끝난 상태라면 실행 종료
        }

        // 목표 위치와의 차이 계산
        int deltaX = (int) (gotoX - this.getPositionX());
        int deltaY = (int) (gotoY - this.getPositionY());

        // 목표 위치에 도달했는지 확인
        if (Math.abs(deltaX) <= 1 && Math.abs(deltaY) <= 1) {
            // 목표에 도달하면 위치를 정확히 설정하고 완료 플래그를 설정
            this.setPositionX(gotoX);
            this.setPositionY(gotoY);
            randomMoveDone = true;
            nextMoveCool.reset();
            return; // 이동 종료
        }

        // 이동 속도 설정
        double speed = 1.0;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        double ratio = speed / distance;

        // 목표 방향으로 이동
        move(deltaX * ratio, deltaY * ratio);
    }

    public void setRandomMoveDone(boolean bool) { this.randomMoveDone = bool; }
    public boolean isRandomMoveDone() { return randomMoveDone; }
    public final void destroy(final float balance) {
        this.isDestroyed = true;
    }

    public boolean isDestroyed() { return isDestroyed; };

    public int getWho() { return who; }
}
