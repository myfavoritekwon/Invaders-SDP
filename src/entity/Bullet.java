package entity;

import java.awt.Color;

import engine.DrawManager.SpriteType;

/**
 * Implements a bullet that moves vertically up or down.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class Bullet extends Entity {

	/**
	 * Speed of the bullet, positive or negative depending on direction -
	 * positive is down.
	 */
	private int speed;
	private int cos_speed; // 각속도 요소 추가

	/**
	 * Constructor, establishes the bullet's properties.
	 * 
	 * @param positionX
	 *            Initial position of the bullet in the X axis.
	 * @param positionY
	 *            Initial position of the bullet in the Y axis.
	 * @param speed
	 *            Speed of the bullet, positive or negative depending on
	 *            direction - positive is down.
	 */
	public Bullet(final int positionX, final int positionY, final int speed) {
		super(positionX, positionY, 3 * 2, 5 * 2, Color.WHITE);

		this.speed = speed;
		this.cos_speed = 0; // 적군 총알은 직선으로만 쏨
		setSprite();
	}
	// 총알의 속도를 각속도로 세분화하여 생성
	public Bullet(final int positionX, final int positionY, final int cos_speed, final int sin_speed) {
		super(positionX, positionY, 3 * 2, 5 * 2, Color.WHITE);

		this.speed = sin_speed;
		this.cos_speed = cos_speed;
		setSprite();
	}

	/**
	 * Sets correct sprite for the bullet, based on speed.
	 */
	public final void setSprite() {
		if (speed < 0)
			this.spriteType = SpriteType.Bullet;
		else
			this.spriteType = SpriteType.EnemyBullet;
	}

	/**
	 * Updates the bullet's position.
	 */
	public final void update() {
		// 각속도 진행
		this.positionY += this.speed;
		this.positionX += this.cos_speed;
		// 총알이 벽에 부딪히면 반사
		if(this.positionX <= 0 || this.positionX >= 598){
			this.cos_speed = -this.cos_speed;
		}
	}

	/**
	 * Setter of the speed of the bullet.
	 * 
	 * @param speed
	 *            New speed of the bullet.
	 */
	public final void setSpeed(final int speed) {
		this.speed = speed;
	}

	// BulletPool에서 cos_speed값 정의
	public final void setCosSpeed(final int cos_speed) { this.cos_speed = cos_speed; }
	/**
	 * Getter for the speed of the bullet.
	 * 
	 * @return Speed of the bullet.
	 */
	public int getSpeed() {
		return this.speed;
	}
}
