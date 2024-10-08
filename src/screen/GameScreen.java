package screen;

import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import engine.*;
import entity.*;

/**
 * Implements the game screen, where the action happens.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class GameScreen extends Screen {

	/** Milliseconds until the screen accepts user input. */
	private static final int INPUT_DELAY = 6000;
	/** Bonus score for each life remaining at the end of the level. */
	private static final int LIFE_SCORE = 100;
	/** Minimum time between bonus ship's appearances. */
	private static final int BONUS_SHIP_INTERVAL = 20000;
	/** Maximum variance in the time between bonus ship's appearances. */
	private static final int BONUS_SHIP_VARIANCE = 10000;
	/** Time until bonus ship explosion disappears. */
	private static final int BONUS_SHIP_EXPLOSION = 500;
	/** Time from finishing the level to screen change. */
	private static final int SCREEN_CHANGE_INTERVAL = 1500;
	/** Height of the interface separation line. */
	private static final int SEPARATION_LINE_HEIGHT = 40;

	/** Current game difficulty settings. */
	private GameSettings gameSettings;
	/** Current difficulty level number. */
	private int level;
	/** Formation of enemy ships. */
	private EnemyShipFormation enemyShipFormation;
	/** Player's ship. */
	private Ship ship;
	/** Bonus enemy ship that appears sometimes. */
	private EnemyShip enemyShipSpecial;
	/** Minimum time between bonus ship appearances. */
	private Cooldown enemyShipSpecialCooldown;
	/** Time until bonus ship explosion disappears. */
	private Cooldown enemyShipSpecialExplosionCooldown;
	/** Time from finishing the level to screen change. */
	private Cooldown screenFinishedCooldown;
	/** Set of all bullets fired by on screen ships. */
	private Set<Bullet> bullets;
	/** Current score. */
	private int score;
	/** Player lives left. */
	private int lives;
	/** Total bullets shot by the player. */
	private int bulletsShot;
	/** Total ships destroyed by the player. */
	private int shipsDestroyed;
	/** Moment the game starts. */
	private long gameStartTime;
	/** Checks if the level is finished. */
	private boolean levelFinished;
	/** Checks if a bonus life is received. */
	private boolean bonusLife;
	/** Spider webs restricting player movement */
	private List<Web> web;
	/**
	 * Obstacles preventing a player's bullet
	 */
	private List<Block> block;
	private Wallet wallet;
	/* Blocker 등장 쿨타임 */
	private Cooldown blockerCooldown;
	/* Blocker 보이는 시간 */
	private Cooldown blockerVisibleCooldown;
	/* Blocker이 보이고 있는지 */
	private boolean blockerVisible;
	private Random random;
	private List<Blocker> blockers;
	/** Singleton instance of SoundManager */
	private final SoundManager soundManager = SoundManager.getInstance();

	private int MAX_BLOCKERS = 0;
	/**
	 * Constructor, establishes the properties of the screen.
	 * 
	 * @param gameState
	 *            Current game state.
	 * @param gameSettings
	 *            Current game settings.
	 * @param bonusLife
	 *            Checks if a bonus life is awarded this level.
	 * @param width
	 *            Screen width.
	 * @param height
	 *            Screen height.
	 * @param fps
	 *            Frames per second, frame rate at which the game is run.
	 */
	public GameScreen(final GameState gameState,
			final GameSettings gameSettings, final boolean bonusLife,
			final int width, final int height, final int fps, final Wallet wallet) {
		super(width, height, fps);

		this.gameSettings = gameSettings;
		this.bonusLife = bonusLife;
		this.level = gameState.getLevel();
		this.score = gameState.getScore();
		this.lives = gameState.getLivesRemaining();
		if (this.bonusLife)
			this.lives++;
		this.bulletsShot = gameState.getBulletsShot();
		this.shipsDestroyed = gameState.getShipsDestroyed();
		this.wallet = wallet;

		this.random = new Random();
		this.blockerVisible = false;
		this.blockerCooldown = Core.getVariableCooldown(10000, 14000);
		this.blockerCooldown.reset();
		this.blockerVisibleCooldown = Core.getCooldown(20000);
		this.blockers = new ArrayList<>();
	}

	/**
	 * Initializes basic screen properties, and adds necessary elements.
	 */
	public final void initialize() {
		super.initialize();

		enemyShipFormation = new EnemyShipFormation(this.gameSettings);
		enemyShipFormation.attach(this);
		this.ship = new Ship(this.width / 2, this.height - 30);
		ship.applyItem(wallet);
		//Create random Spider Web.
		int web_count = 1 + level / 3;
		web = new ArrayList<>();
		for(int i = 0; i < web_count; i++) {
			double randomValue = Math.random();
			this.web.add(new Web((int) (randomValue * width - 12 * 2), this.height - 30));
			this.logger.info("거미줄 생성 위치 : " + web.get(i).getPositionX());
		}
		//Create random Block.
		int blockCount = level / 2;
		int playerTopY = this.height - 40;
		int enemyBottomY = 100 + (gameSettings.getFormationHeight() - 1) * 48;
		this.block = new ArrayList<Block>();
		for (int i = 0; i < blockCount; i++) {
			Block newBlock;
			boolean overlapping;
			do {
				newBlock = new Block(0,0);
				int positionX = (int) (Math.random() * (this.width - newBlock.getWidth()));
				int positionY = (int) (Math.random() * (playerTopY - enemyBottomY - newBlock.getHeight())) + enemyBottomY;
				newBlock = new Block(positionX, positionY);
				overlapping = false;
				for (Block block : block) {
					if (checkCollision(newBlock, block)) {
						overlapping = true;
						break;
					}
				}
			} while (overlapping);
			block.add(newBlock);
		}



		// Appears each 10-30 seconds.
		this.enemyShipSpecialCooldown = Core.getVariableCooldown(
				BONUS_SHIP_INTERVAL, BONUS_SHIP_VARIANCE);
		this.enemyShipSpecialCooldown.reset();
		this.enemyShipSpecialExplosionCooldown = Core
				.getCooldown(BONUS_SHIP_EXPLOSION);
		this.screenFinishedCooldown = Core.getCooldown(SCREEN_CHANGE_INTERVAL);
		this.bullets = new HashSet<Bullet>();

		// Special input delay / countdown.
		this.gameStartTime = System.currentTimeMillis();
		this.inputDelay = Core.getCooldown(INPUT_DELAY);
		this.inputDelay.reset();
		soundManager.playSound(Sound.COUNTDOWN);
	}

	/**
	 * Starts the action.
	 * 
	 * @return Next screen code.
	 */
	public final int run() {
		super.run();

		this.score += LIFE_SCORE * (this.lives - 1);
		if(this.lives == 0) this.score += 100;
		this.logger.info("Screen cleared with a score of " + this.score);

		return this.returnCode;
	}

	/**
	 * Updates the elements on screen and checks for events.
	 */
	protected final void update() {
		super.update();

		if (this.inputDelay.checkFinished() && !this.levelFinished) {

			if (!this.ship.isDestroyed()) {
				boolean moveRight = inputManager.isKeyDown(KeyEvent.VK_RIGHT)
						|| inputManager.isKeyDown(KeyEvent.VK_D);
				boolean moveLeft = inputManager.isKeyDown(KeyEvent.VK_LEFT)
						|| inputManager.isKeyDown(KeyEvent.VK_A);

				boolean isRightBorder = this.ship.getPositionX()
						+ this.ship.getWidth() + this.ship.getSpeed() > this.width - 1;
				boolean isLeftBorder = this.ship.getPositionX()
						- this.ship.getSpeed() < 1;

				if (moveRight && !isRightBorder) {
					this.ship.moveRight();
				}
				if (moveLeft && !isLeftBorder) {
					this.ship.moveLeft();
				}
				if (inputManager.isKeyDown(KeyEvent.VK_SPACE))
					if (this.ship.shoot(this.bullets))
						this.bulletsShot++;
				boolean conti;



				for(int i = 0; i < web.size(); i++) {
					//escape Spider Web
					if (ship.getPositionX() + 6 <= web.get(i).getPositionX() - 6
							|| web.get(i).getPositionX() + 6 <= ship.getPositionX() - 6) {
						this.ship.setThreadWeb(false);
					}
					//get caught in a spider's web
					else {
						this.ship.setThreadWeb(true);
						break;
					}
				}
			}

			if (this.enemyShipSpecial != null) {
				if (!this.enemyShipSpecial.isDestroyed())
					this.enemyShipSpecial.move(2, 0);
				else if (this.enemyShipSpecialExplosionCooldown.checkFinished())
					this.enemyShipSpecial = null;

			}
			if (this.enemyShipSpecial == null
					&& this.enemyShipSpecialCooldown.checkFinished()) {
				this.enemyShipSpecial = new EnemyShip();
				this.enemyShipSpecialCooldown.reset();
				this.logger.info("A special ship appears");
			}
			if (this.enemyShipSpecial != null
					&& this.enemyShipSpecial.getPositionX() > this.width) {
				this.enemyShipSpecial = null;
				this.logger.info("The special ship has escaped");
			}

			this.ship.update();
			this.enemyShipFormation.update();

			 if (level >= 3) {// 레벨 3 이후부터 시야 방해물 등장 이벤트 시작
                this.enemyShipFormation.shoot(this.bullets, this.level);
				handleBlockerAppearance();
			}
		}

		manageCollisions();
		cleanBullets();
		draw();

		if ((this.enemyShipFormation.isEmpty() || this.lives <= 0)
				&& !this.levelFinished) {
			this.levelFinished = true;
			this.screenFinishedCooldown.reset();
		}

		if (this.levelFinished && this.screenFinishedCooldown.checkFinished())
			this.isRunning = false;

	}

	/**
	 * Draws the elements associated with the screen.
	 */
	private void draw() {
		drawManager.initDrawing(this);

		drawManager.drawEntity(this.ship, this.ship.getPositionX(),
				this.ship.getPositionY());
		//draw Spider Web
		for (int i = 0; i < web.size(); i++) {
			drawManager.drawEntity(this.web.get(i), this.web.get(i).getPositionX(),
					this.web.get(i).getPositionY());
		}
		//draw Blocks
		for (Block block : block)
			drawManager.drawEntity(block, block.getPositionX(),
					block.getPositionY());

		if (this.enemyShipSpecial != null)
			drawManager.drawEntity(this.enemyShipSpecial,
					this.enemyShipSpecial.getPositionX(),
					this.enemyShipSpecial.getPositionY());

		enemyShipFormation.draw();

		for (Bullet bullet : this.bullets)
			drawManager.drawEntity(bullet, bullet.getPositionX(),
					bullet.getPositionY());

		// Interface.
		drawManager.drawScore(this, this.score);
		drawManager.drawLives(this, this.lives);
		drawManager.drawHorizontalLine(this, SEPARATION_LINE_HEIGHT - 1);

		// Countdown to game start.
		if (!this.inputDelay.checkFinished()) {
			int countdown = (int) ((INPUT_DELAY
					- (System.currentTimeMillis()
							- this.gameStartTime)) / 1000);
			drawManager.drawCountDown(this, this.level, countdown,
					this.bonusLife);
			drawManager.drawHorizontalLine(this, this.height / 2 - this.height
					/ 12);
			drawManager.drawHorizontalLine(this, this.height / 2 + this.height
					/ 12);
		}

		// Blocker 그리는 부분
		if (!blockers.isEmpty()) {
			for (Blocker blocker : blockers) {
				drawManager.drawRotatedEntity(blocker, blocker.getPositionX(), blocker.getPositionY(), blocker.getAngle());
			}
		}
		drawManager.completeDrawing(this);
	}


	// Blocker의 위치, 각도, sprite 등을 다루는 메소드 (update에서 반복적으로 호출됨.)
	private void handleBlockerAppearance() {

		if (level >= 3 && level < 6) MAX_BLOCKERS = 1;
		else if (level >= 6 && level < 11) MAX_BLOCKERS = 2;
		else if (level >= 11) MAX_BLOCKERS = 3;

		int kind = random.nextInt(2-1 + 1) +1; // 1~2
		DrawManager.SpriteType newSprite;
		switch (kind) {
			case 1:
				newSprite = DrawManager.SpriteType.Blocker1; // 인공위성
				break;
			case 2:
				newSprite = DrawManager.SpriteType.Blocker2; // 우주 비행사
				break;
			default:
				newSprite = DrawManager.SpriteType.Blocker1;
				break;
		}

		// Blocker 개수 체크, 나올 타이밍 체크
		if (blockers.size() < MAX_BLOCKERS && blockerCooldown.checkFinished()) {
			boolean moveLeft = random.nextBoolean(); // 현재 Blocker의 이동 방향 랜덤 설정
			int startY = random.nextInt(this.height - 90) + 25; // 화면 위아래 여백을 둔 랜덤 Y 위치
			int startX = moveLeft ? this.width + 300 : -300; // 왼쪽으로 움직일 거면 화면 오른쪽 바깥, 오른쪽이면 왼쪽 바깥
			// 새로운 Blocker 추가
			if (moveLeft) {
				blockers.add(new Blocker(startX, startY, newSprite, moveLeft)); // 오른쪽에서 왼쪽으로 이동
			} else {
				blockers.add(new Blocker(startX, startY, newSprite, moveLeft)); // 왼쪽에서 오른쪽으로 이동
			}
			blockerCooldown.reset();
		}

		// Blocker 리스트 중에 화면을 벗어나서 없어질 것들
		List<Blocker> toRemove = new ArrayList<>();
		for (int i = 0; i < blockers.size(); i++) {
			Blocker blocker = blockers.get(i);

			// Blocker가 화면을 벗어났을 경우 직접 리스트에서 제거
			if (blocker.getMoveLeft() && blocker.getPositionX() < -300 || !blocker.getMoveLeft() && blocker.getPositionX() > this.width + 300) {
				blockers.remove(i);
				i--; // 리스트에서 요소가 제거되면 인덱스를 한 칸 줄여줘야 함
				continue;
			}

			// Blocker 이동 및 회전 (positionX, Y값 변경)
			if (blocker.getMoveLeft()) {
				blocker.move(-1.5, 0); // 왼쪽으로 이동
			} else {
				blocker.move(1.5, 0); // 오른쪽으로 이동
			}
			blocker.rotate(0.2); // Blocker 회전
		}

		// 화면을 벗어난 Blocker 리스트에서 제거
		blockers.removeAll(toRemove);
	}

	/**
	 * Cleans bullets that go off screen.
	 */
	private void cleanBullets() {
		Set<Bullet> recyclable = new HashSet<Bullet>();
		for (Bullet bullet : this.bullets) {
			bullet.update();
			if (bullet.getPositionY() < SEPARATION_LINE_HEIGHT
					|| bullet.getPositionY() > this.height)
				recyclable.add(bullet);
		}
		this.bullets.removeAll(recyclable);
		BulletPool.recycle(recyclable);
	}

	/**
	 * Manages collisions between bullets and ships.
	 */
	private void manageCollisions() {
		Set<Bullet> recyclable = new HashSet<Bullet>();
		for (Bullet bullet : this.bullets)
			if (bullet.getSpeed() > 0) {
				if (checkCollision(bullet, this.ship) && !this.levelFinished) {
					recyclable.add(bullet);
					if (!this.ship.isDestroyed()) {
						this.ship.destroy();
						lvdamage();
						this.logger.info("Hit on player ship, " + this.lives
								+ " lives remaining.");
						}
					}
				} else {
					for (EnemyShip enemyShip : this.enemyShipFormation)
						if (!enemyShip.isDestroyed()
								&& checkCollision(bullet, enemyShip)) {
							this.enemyShipFormation.destroy(enemyShip);

							// enemyShipFormation에서 적 함선 스코어값, 파괴된함선++ 받아오도록 설정
							this.score += this.enemyShipFormation.getPoint();
							this.shipsDestroyed += this.enemyShipFormation.getDistroyedship();

							recyclable.add(bullet);
						}
					if (this.enemyShipSpecial != null
							&& !this.enemyShipSpecial.isDestroyed()
							&& checkCollision(bullet, this.enemyShipSpecial)) {
						this.score += this.enemyShipSpecial.getPointValue();
						this.shipsDestroyed++;
						this.enemyShipSpecial.destroy();
						this.enemyShipSpecialExplosionCooldown.reset();
						recyclable.add(bullet);
					}
					//check the collision between the obstacle and the bullet
					for (Block block : this.block) {
						if (checkCollision(bullet, block)) {
							recyclable.add(bullet);
							break;
						}
					}
				}
			//check the collision between the obstacle and the enemyship
			Set<Block> removableBlocks = new HashSet<>();
			for (EnemyShip enemyShip : this.enemyShipFormation) {
				if (!enemyShip.isDestroyed()) {
					for (Block block : block) {
						if (checkCollision(enemyShip, block)) {
							removableBlocks.add(block);
						}
					}
				}
			}
			// remove crashed obstacle
			block.removeAll(removableBlocks);
			this.bullets.removeAll(recyclable);
			BulletPool.recycle(recyclable);
		}

	/**
	 * Checks if two entities are colliding.
	 * 
	 * @param a
	 *            First entity, the bullet.
	 * @param b
	 *            Second entity, the ship.
	 * @return Result of the collision test.
	 */
	private boolean checkCollision(final Entity a, final Entity b) {
		// Calculate center point of the entities in both axis.
		int centerAX = a.getPositionX() + a.getWidth() / 2;
		int centerAY = a.getPositionY() + a.getHeight() / 2;
		int centerBX = b.getPositionX() + b.getWidth() / 2;
		int centerBY = b.getPositionY() + b.getHeight() / 2;
		// Calculate maximum distance without collision.
		int maxDistanceX = a.getWidth() / 2 + b.getWidth() / 2;
		int maxDistanceY = a.getHeight() / 2 + b.getHeight() / 2;
		// Calculates distance.
		int distanceX = Math.abs(centerAX - centerBX);
		int distanceY = Math.abs(centerAY - centerBY);

		return distanceX < maxDistanceX && distanceY < maxDistanceY;
	}

	/**
	 * Returns a GameState object representing the status of the game.
	 * 
	 * @return Current game state.
	 */
	public final GameState getGameState() {
		return new GameState(this.level, this.score, this.lives,
				this.bulletsShot, this.shipsDestroyed);
	}

	//스테이지 레벨에 따라 적군 bullet 데미지 증가
	public void lvdamage(){
		for(int i=0; i<= GameState.level/5;i++){
			this.lives--;
		}
		if(this.lives < 0){
			this.lives = 0;
		}
	}
}