package entity;

import engine.DrawManager.SpriteType;
import engine.GameState;
import screen.GameScreen;

import java.util.Random;

public class PhysicsEnemyShip extends EnemyShip {

    private double velocityX;
    private double velocityY;
    //중력 상수
    private static final double GRAVITY = 0.5;
    //반발 계수
    private static final double COEFFICIENT_OF_RESTITUTION = 0.8;

    private final GameScreen gameScreen;

    public PhysicsEnemyShip(final int positionX, final int positionY, final SpriteType spriteType, final GameState gameState, final GameScreen gameScreen) {
        super(positionX, positionY, spriteType, gameState);
        Random random = new Random();
        this.velocityX = random.nextDouble(2); // 초기 X 속도 (조정 가능)
        this.velocityY = 0.0; // 초기 Y 속도
        this.gameScreen = gameScreen;
    }

    @Override
    public void update() {
        if (this.animationCooldown.checkFinished()) {
            this.animationCooldown.reset();

            switch (this.spriteType) {
                case EnemyShipA1:
                    this.spriteType = SpriteType.EnemyShipA2;
                    break;
                case EnemyShipA2:
                    this.spriteType = SpriteType.EnemyShipA1;
                    break;
                case EnemyShipB1:
                    this.spriteType = SpriteType.EnemyShipB2;
                    break;
                case EnemyShipB2:
                    this.spriteType = SpriteType.EnemyShipB1;
                    break;
                case EnemyShipC1:
                    this.spriteType = SpriteType.EnemyShipC2;
                    break;
                case EnemyShipC2:
                    this.spriteType = SpriteType.EnemyShipC1;
                    break;
                case EnemyShipD1:
                    this.spriteType = SpriteType.EnemyShipD2;
                    break;
                case EnemyShipD2:
                    this.spriteType = SpriteType.EnemyShipD1;
                    break;
                case EnemyShipE1:
                    this.spriteType = SpriteType.EnemyShipE2;
                    break;
                case EnemyShipE2:
                    this.spriteType = SpriteType.EnemyShipE1;
                    break;
                default:
                    break;
            }
        }

        // 중력 적용
        this.velocityY += GRAVITY;

        // 위치 업데이트
        this.positionX += velocityX;
        this.positionY += velocityY;

        System.out.println("PositionX: " + this.positionX + ", PositionY: " + this.positionY);

        // 화면 경계와의 충돌 처리
        if (this.positionX <= 0 || this.positionX + this.getWidth() >= gameScreen.getWidth()) {
            this.velocityX *= -COEFFICIENT_OF_RESTITUTION; // X축 속도 반전
            if (this.positionX < 0) {
                this.positionX = 0; // 화면 밖으로 나가는 것을 방지
            } else if (this.positionX + this.getWidth() > gameScreen.getWidth()) {
                this.positionX = gameScreen.getWidth() - this.getWidth();
            }
        }
        //100 > 마진
        if (this.positionY + this.getHeight() >= gameScreen.getHeight() - 100) {
            // 바닥에 닿으면 더 이상 떨어지지 않도록 처리
            this.velocityY *= -COEFFICIENT_OF_RESTITUTION; // 약간 튕겨오르게 설정
            this.positionY = gameScreen.getHeight() - this.getHeight() - 100;
        }
    }
}
