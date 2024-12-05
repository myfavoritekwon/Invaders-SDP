package screen;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.*;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.Timer;
import java.util.TimerTask;

import engine.DrawManager.SpriteType;
import engine.*;
import entity.*;


/**
 * Implements the game screen, where the action happens.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public class GameScreen extends Screen implements Callable<GameState> {

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
	/** 중력 함선 */
	private ArrayList<PhysicsEnemyShip> physicsEnemyShips;
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
	private Cooldown shootingCooldown;
	private Cooldown pauseESCCooldown;
	/** Set of all bullets fired by on screen ships. */
	private Set<Bullet> bullets;
	/** Current score. */
	private String name1;

	private int score;
	/** tempScore records the score up to the previous level. */
	private int tempScore;
	/** Current ship type. */
	private Ship.ShipType shipType;
	/** Player lives left. */
	private int lives;
	/** Total bullets shot by the player. */
	private int bulletsShot;
	/** Total ships destroyed by the player. */
	private int shipsDestroyed;
	/** Number of consecutive hits.
	 * maxCombo records the maximum value of combos in that level. */
	private int combo;
	private int maxCombo;
	/** Moment the game starts. */
	private long gameStartTime;
	/** Checks if the level is finished. */
	private boolean levelFinished;
	/** Checks if a bonus life is received. */
	private boolean bonusLife;
	/** Player number for two player mode **/
	private int playerNumber;
	/** list of highScores for find recode. */
	private List<Score>highScores;
	/** Elapsed time while playing this game.
	 * lapTime records the time to the previous level. */
	private int elapsedTime;
	private int lapTime;
	/** Keep previous timestamp. */
	private Integer prevTime;
	/** Alert Message when a special enemy appears. */
	private String alertMessage;
	/** checks if it's executed. */
	private boolean isExecuted = false;
	/** timer.. */
	private Timer timer;
	private TimerTask timerTask;
	/** Spider webs restricting player movement */
	private List<Web> web;
	/**
	 * Obstacles preventing a player's bullet
	 */
	private List<Block> block;

	private Wallet wallet;
	/* Blocker appearance cooldown */
	private Cooldown blockerCooldown;
	/* Blocker visible time */
	private Cooldown blockerVisibleCooldown;
	/* Is Blocker visible */
	private boolean blockerVisible;
	private Random random;
	private List<Blocker> blockers = new ArrayList<>();
	/** Singleton instance of SoundManager */
	private final SoundManager soundManager = SoundManager.getInstance();
	/** Singleton instance of ItemManager. */
	private ItemManager itemManager;
	/** Item boxes that dropped when kill enemy ships. */
	private Set<ItemBox> itemBoxes;
	/** Barriers appear in game screen. */
	private Set<Barrier> barriers;
	/** Sound balance for each player*/
	private float balance = 0.0f;

	private int MAX_BLOCKERS = 0;

	private GameState gameState;

	private int hitBullets;

	private PuzzleScreen puzzleScreen;
	private Cooldown puzzleRetryCooldown;
	private Cooldown webCooldown;

	private BonusBoss bonusBoss;
	private Set<Integer> bonusBossLevels = Set.of(7,4);
	private Cooldown bonusLevelCountdown;
	private int barWidth;
	private int barInitialWidth = 330;
	private int barHeight = 12;
	private int barX = 133;
	private int barY = 78;
	private Cooldown barDisappear;

	private boolean checkPause = false;
	private int checkPauseClick = 0;


	private Boss boss;
	private Set<Integer> bossLevels = Set.of(3,6);
	private boolean firstBossAppeared = false;
	private Cooldown bossPattern;
	private boolean enemyByPattern = false;
	private List<BossBullet> bossBullets;
	private Cooldown bossMovement;
	private Cooldown bossShotCool;
	private boolean hacked = false;
	private Cooldown hackedTime;
	private Cooldown hackedEffect;
	private boolean hackedVisible = true;
	private int laserHeight = 1;
	private Cooldown laserMaintainCooldown;
	private Cooldown laserCooldown;
	private boolean laserActive = false;
	private boolean laserDrawComplete = false;
	private boolean left;
	private int hpBarWidth;
	private int hpBarInitialWidth = 523;
	private int widthMinus;

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
		this.gameState = gameState;
		this.bonusLife = bonusLife;
		this.level = gameState.getLevel();
		this.score = gameState.getScore();
		this.elapsedTime = gameState.getElapsedTime();
		this.alertMessage = gameState.getAlertMessage();
		this.shipType = gameState.getShipType();
		this.lives = gameState.getLivesRemaining();
		this.hitBullets = gameState.getHitBullets();
		if (this.bonusLife)
			this.lives++;
		this.shipsDestroyed = gameState.getShipsDestroyed();
		this.playerNumber = -1;
		this.maxCombo = gameState.getMaxCombo();
		this.lapTime = gameState.getPrevTime();
		this.tempScore = gameState.getPrevScore();

		this.bulletsShot = gameState.getBulletsShot();
		try {
			this.highScores = Core.getFileManager().loadHighScores();

		} catch (IOException e) {
			logger.warning("Couldn't load high scores!");
		}

		this.wallet = wallet;


		this.random = new Random();
		this.blockerVisible = false;
		this.blockerCooldown = Core.getVariableCooldown(10000, 14000);
		this.blockerCooldown.reset();
		this.blockerVisibleCooldown = Core.getCooldown(20000);
		this.pauseESCCooldown = Core.getCooldown(10000);

		try {
			this.highScores = Core.getFileManager().loadHighScores();
		} catch (IOException e) {
			logger.warning("Couldn't load high scores!");
		}
		this.alertMessage = "";

		this.wallet = wallet;
	}

	/**
	 * Constructor, establishes the properties of the screen for two player mode.
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
	 * @param playerNumber
	 *            Player number for two player mode
	 */
	public GameScreen(final GameState gameState,
					  final GameSettings gameSettings, final boolean bonusLife,
					  final int width, final int height, final int fps, final Wallet wallet,
					  final int playerNumber) {
		this(gameState, gameSettings, bonusLife, width, height, fps, wallet);
		this.playerNumber = playerNumber;
		this.balance = switch (playerNumber) {
			case 0: yield -1.0f; // 1P
			case 1: yield 1.0f;  // 2P
			default: yield 0.0f; // default
		};
	}

	/**
	 * Initializes basic screen properties, and adds necessary elements.
	 */
	public final void initialize() {
		super.initialize();

        // Appears each 10-30 seconds.
        this.ship = ShipFactory.create(this.shipType, this.width / 2, this.height - 30);
		if (bossLevels.contains(level)) {
			bossBullets = new ArrayList<>();
		}
		if (!bonusBossLevels.contains(level) && !bossLevels.contains(level)) {
			enemyShipFormation = new EnemyShipFormation(this.gameSettings, this.gameState);
			enemyShipFormation.attach(this);
		}
		logger.info("Player ship created " + this.shipType + " at " + this.ship.getPositionX() + ", " + this.ship.getPositionY());
        ship.applyItem(wallet);

		//Create Gravity Enemy
		if (!bonusBossLevels.contains(level)) {
			int bonus = random.nextBoolean()? 0 : 1;

			if (bonus == 0) {
				physicsEnemyShips = new ArrayList<>();
				int mob_num = level + 2;
				logger.info("Create bonus gravity! num : " + mob_num);
				Random random = new Random();
				for (int i = 0; i < mob_num; i++) {

					int init_x = getWidth() - 12 * 2;
//				int x_result = random.nextBoolean() ? 0 : init_x;
					int x_result = random.nextInt(init_x + 1);

					int minY = 300; // 최소값
					int maxY = getHeight() - 100; // 최대값
					// 최소값 minY부터 최대값 maxY 사이의 랜덤 값 생성
					int y_result = minY + random.nextInt(maxY - minY + 1);

					SpriteType[] spriteTypes = {SpriteType.EnemyShipA1, SpriteType.EnemyShipB1, SpriteType.EnemyShipC1, SpriteType.EnemyShipD1, SpriteType.EnemyShipE1};
					SpriteType sprite_result = spriteTypes[random.nextInt(spriteTypes.length)];
					PhysicsEnemyShip physicsEnemyShip = new PhysicsEnemyShip(x_result, y_result, sprite_result, gameState, this);

					physicsEnemyShips.add(physicsEnemyShip);
				}
			}
		}
		//Create random Spider Web.
		if (!bonusBossLevels.contains(level) && !bossLevels.contains(level)) {
			int web_count = 1 + level / 3;
			web = new ArrayList<>();
			for(int i = 0; i < web_count; i++) {
                double randomValue = Math.random();
                int randomValueX;

                if (random.nextBoolean()) {
                    // 좌측 범위에서 랜덤 값 선택
                    randomValueX = random.nextInt(Math.max(1, (int) (this.ship.getPositionX() - 12 * 2)));
                } else {
                    // 우측 범위에서 랜덤 값 선택
                    randomValueX = (int) this.ship.getPositionX() + 12  * 2+ random.nextInt((int) (width - this.ship.getPositionX() - 12 * 2));
                }

                int randomValueY;
                int minY = 300; // 최소값
                int maxY = this.height - 30; // 최대값
                // 최소값 minY부터 최대값 maxY 사이의 랜덤 값 생성
                randomValueY = minY + random.nextInt(maxY - minY + 1);

                this.web.add(new Web(randomValueX, randomValueY));
                this.logger.info("Spider web creation location X: " + web.get(i).getPositionX() + ", Y:" + web.get(i).getPositionY());
			}
			//Create random Block.
			int blockCount = level / 2;
			int playerTopY_contain_barrier = this.height - 40 - 150;
			int enemyBottomY = 100 + (gameSettings.getFormationHeight() - 1) * 48;
			this.block = new ArrayList<Block>();
			for (int i = 0; i < blockCount; i++) {
				Block newBlock;
				boolean overlapping;
				do {
					newBlock = new Block(0,0);
					int positionX = (int) (Math.random() * (this.width - newBlock.getWidth()));
					int positionY = (int) (Math.random() * (playerTopY_contain_barrier - enemyBottomY - newBlock.getHeight())) + enemyBottomY;
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
		}
		/** initialinze images*/
		DrawManager.initializeItemImages();


		// Appears each 10-30 seconds.
		this.enemyShipSpecialCooldown = Core.getVariableCooldown(
				BONUS_SHIP_INTERVAL, BONUS_SHIP_VARIANCE);
		this.enemyShipSpecialCooldown.reset();
		this.enemyShipSpecialExplosionCooldown = Core
				.getCooldown(BONUS_SHIP_EXPLOSION);
		this.screenFinishedCooldown = Core.getCooldown(SCREEN_CHANGE_INTERVAL);
		this.bullets = new HashSet<>();
		this.barriers = new HashSet<>();
		this.itemBoxes = new HashSet<>();
		this.itemManager = new ItemManager(this.ship, this.enemyShipFormation, this.barriers, this.height, this.width, this.balance);
		this.itemBoxes = new HashSet<>();
		if (bonusBossLevels.contains(level)) { // 보너스 레벨을 2라 하자
//			bonusLevelCountdown = Core.getCooldown(30000);
//			bonusLevelCountdown.reset();
			bonusBoss = new BonusBoss(50, 50, 100, 100);
			barWidth = barInitialWidth;
			barDisappear = Core.getCooldown(1000);
			barDisappear.reset();
		}

		if (bossLevels.contains(level)) { // 보스 레벨
			switch (level) {
				case 3: // 첫번째 보스
					bossShotCool = Core.getCooldown(2000);
					boss = new Boss((this.getWidth() - 600) / 2, -300, 600, 495, 1); // 사진 크기 600x495
					hpBarInitialWidth = 523;
					hpBarWidth = hpBarInitialWidth;
					widthMinus = 523 / (boss.getHealth());
					break;
				case 6:
					bossShotCool = Core.getCooldown(2000);
					boss = new Boss((this.getWidth()-228)/2, -200, 228, 297, 2);
					hpBarInitialWidth = 523;
					hpBarWidth = hpBarInitialWidth;
					widthMinus = 523 / (boss.getHealth());
					break;
				default:
					break;
			}
		}

		// Special input delay / countdown.
		this.gameStartTime = System.currentTimeMillis();
		this.inputDelay = Core.getCooldown(INPUT_DELAY);
		this.inputDelay.reset();
		if (soundManager.isSoundPlaying(Sound.BGM_MAIN))
			soundManager.stopSound(Sound.BGM_MAIN);
		soundManager.playSound(Sound.COUNTDOWN);

		switch (this.level) {
			case 1: soundManager.loopSound(Sound.BGM_LV1); break;
			case 2: soundManager.loopSound(Sound.BGM_LV2); break;
			case 3: soundManager.loopSound(Sound.BGM_LV3); break;
			case 4: soundManager.loopSound(Sound.BGM_BONUS); break;
			case 5: soundManager.loopSound(Sound.BGM_LV5); break;
			case 6: soundManager.loopSound(Sound.BGM_LV6); break;
			case 7: soundManager.loopSound(Sound.BGM_BONUS); break;
				// From level 7 and above, it continues to play at BGM_LV7.
			default: soundManager.loopSound(Sound.BGM_LV7); break;
		}

		this.puzzleScreen = null;
		this.puzzleRetryCooldown = Core.getCooldown(Core.PuzzleSettings.RETRY_DELAY);
		this.webCooldown = Core.getCooldown(3000);
		this.webCooldown.reset();
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

		if (bossLevels.contains(level) && !checkPause) {
			if (level == 3) {
				if (!firstBossAppeared) {
					boss.move(0, 0.9);
					if (boss.getPositionY() >= -80) firstBossAppeared = true;
				} else { // 등장했으면
					if (bossPattern == null) {
						bossPattern = Core.getCooldown(3000); // 보스 패턴 처음 생성
						bossPattern.reset();
					} else {
						if (bossPattern.checkFinished() && !enemyByPattern) { // 몬스터 생성 동안에는 다른 패턴 발동 x
							// 공격 패턴 메소드 넣을 곳
							int type = random.nextInt(3) + 1; // 1~3
							System.out.println(type);
							switch (type) {
								case 1: // 패턴 1 : enemyship 군단 소환 (생각보다 많이 어려워서 이 패턴일 땐 다른 패턴 동시에 x)
									if (!enemyByPattern && enemyShipFormation == null || enemyShipFormation != null && enemyShipFormation.isEmpty()) {
										enemyShipFormation = new EnemyShipFormation(this.gameSettings, this.gameState);
										itemManager.setEnemyShipFormation(enemyShipFormation);
										enemyShipFormation.attach(this);
										enemyByPattern = true;
									}
									break;
								case 2: // 미사일 4개 발사
									int centerX = (int) boss.getPositionX() + boss.getBossShipImage().getWidth() / 2 - 37 / 2;
									int centerY = (int) boss.getPositionY() + boss.getBossShipImage().getHeight() / 2;

									bossBullets.add(new BossBullet(centerX - 35, centerY - 20, 100, 100, 1)); // width, height 고치기
									bossBullets.add(new BossBullet(centerX + 35, centerY - 20, 100, 100, 1));
									bossBullets.add(new BossBullet(centerX - 100, centerY, 100, 100, 1));
									bossBullets.add(new BossBullet(centerX + 100, centerY, 100, 100, 1));
									bossPattern.reset(6000);
									break;
								case 3:
									if (hackedTime == null) {
										hackedTime = Core.getCooldown(9000); // 9초 동안 해킹 당함 (공격 x)
										hackedTime.reset();
									} else {
										hackedTime.reset();
									}
									if (hackedEffect == null) {
										hackedEffect = Core.getCooldown(400);
									} else hackedEffect.reset();
									hacked = true;
									bossPattern.reset(6000);
									break;
								default:
									break;
							}

						}
					}
					if (bossMovement == null) {
						bossMovement = Core.getCooldown(4000); // 이때부터 움직이기 시작
						bossMovement.reset();
					}
				}
				if (boss != null && boss.isDestroyed() && !this.levelFinished) {
					this.levelFinished = true;
					soundManager.stopSound(soundManager.getCurrentBGM());
					this.screenFinishedCooldown.reset();
				}
				if (bossShotCool.checkFinished() && this.inputDelay.checkFinished()) {
					int centerX = (int) boss.getPositionX() + boss.getBossShipImage().getWidth() / 2 - 34 / 2;
					int centerY = (int) boss.getPositionY() + boss.getBossShipImage().getHeight() / 2;
					bossBullets.add(new BossBullet(centerX - 128, centerY + 155, 34, 34, 0));
					bossBullets.add(new BossBullet(centerX + 138, centerY + 155, 34, 34, 0));
					bossShotCool.reset();
				}
			} else if (level == 6) { // 두 번째 보스
				if (!firstBossAppeared) {
					boss.move(0, 0.9);
					if (boss.getPositionY() >= 30) firstBossAppeared = true;
				} else {
					if (bossPattern == null) {
						bossPattern = Core.getCooldown(3000); // 보스 패턴 처음 생성
						bossPattern.reset();
					} else {
						if (bossPattern.checkFinished()) {
							int type = random.nextInt(3) + 1;
							switch (type) {
								case 1:
									if (!laserActive) { // 왼쪽 레이저
										bossBullets.add(new BossBullet(boss.getPositionX() - 80, boss.getPositionY(), 153, 603, 3));
										bossPattern.reset(4000);
										left = true;
									}
									break;
								case 2:
									if (!laserActive) { // 오른쪽 레이저
										bossBullets.add(new BossBullet(boss.getPositionX() + boss.getWidth() + 80, boss.getPositionY(), 153, 603, 3));
										bossPattern.reset(4000);
										left = false;
									}
									break;
								case 3:
									int x = Math.random() < 0.5 ? 20 : 500;
									int y = random.nextInt(450) + 250;

									int starType;
									if (x == 20) {
										// x값이 10일 때 3, 4, 6 중에서 랜덤 선택
										int[] options = {6, 7, 9};
										starType = options[random.nextInt(options.length)];
									} else {
										// x값이 500일 때 2, 5 중에서 랜덤 선택
										int[] options = {4, 5, 8};
										starType = options[random.nextInt(options.length)];
									}

									bossBullets.add(new BossBullet(x, y, 10, 10, starType));
									bossPattern.reset(4000);
									break;
								default:
									break;
							}
						}
					}
					if (bossMovement == null) {
						bossMovement = Core.getCooldown(4000); // 이때부터 움직이기 시작
						bossMovement.reset();
					}
				}
				if (bossShotCool.checkFinished() && this.inputDelay.checkFinished()) {
					int centerX = (int) boss.getPositionX() + boss.getBossShipImage().getWidth() / 2 - 17 / 2;
					int centerY = (int) boss.getPositionY() + boss.getBossShipImage().getHeight() / 2;
					bossBullets.add(new BossBullet(centerX - 50, centerY + 80, 17, 43, 2));
					bossBullets.add(new BossBullet(centerX + 50, centerY + 80, 17, 43, 2));
					bossShotCool.reset();
				}

				if (boss != null && boss.isDestroyed() && !this.levelFinished) {
					this.levelFinished = true;
					soundManager.stopSound(soundManager.getCurrentBGM());
					this.screenFinishedCooldown.reset();
				}
			}

		}
		if(!checkPause && this.inputDelay.checkFinished()) {
			pauseESCCooldown.reset();
            //swap item N
            if (inputManager.isKeyDown(KeyEvent.VK_N)) {
                itemManager.swapItems();
            }

            // use item M
            if (inputManager.isKeyDown(KeyEvent.VK_M)) {
                ItemManager.ItemType usedItem = itemManager.useStoredItem();
                if (usedItem != null) {
                    Entry<Integer, Integer> result = itemManager.useItem(usedItem);
                    if (result != null) {
                        this.score += result.getKey();
                        this.shipsDestroyed += result.getValue();
                    }
                }
            }

            if (this.inputDelay.checkFinished() && !this.levelFinished) {
                // check web collision and activate puzzle
                if (!ship.isPuzzleActive() && webCooldown.checkFinished()) {
                    boolean webCollision = false;
                    if (!bonusBossLevels.contains(level) && this.web != null) {
                        for (int i = 0; i < web.size(); i++) {
                            // 거미줄 충돌 시 webCollision 값 true
                            if (checkCollision(ship, web.get(i))) {
                                webCollision = true;
                                logger.info("Web collision detected at position" + ship.getPositionX());
                                break;
                            }
                        }
                        if (webCollision && this.puzzleScreen == null) {
                            logger.info("Initializing puzzle...");
                            initializePuzzle();
                        }
                    }
                }

                if (ship.isPuzzleActive() && this.puzzleScreen != null) {
                    updatePuzzleState();
                }

                //update physicsEnemy
                if (physicsEnemyShips != null) {
                    for (int i = 0; i < physicsEnemyShips.size(); i++) {
                        physicsEnemyShips.get(i).update();
                    }
                }
//			//update physicsEnemy
//			for(int i = 0; i < physicsEnemyShips.size(); i++) {
//				physicsEnemyShips.get(i).update();
//			}

                if (bossLevels.contains(level) && bossBullets != null) {
                    for (int i = 0; i < bossBullets.size(); i++) {
                        if (bossBullets.get(i).getAttackType() == 1) {
                            // 화면 밖으로 안나갔으면
                            if (bossBullets.get(i).getPositionY() < this.getHeight() + bossBullets.get(i).getBulletImage().getHeight()) {
                                bossBullets.get(i).move(0, 4.2);
                            } else { // 화면 밖으로 나가면 없애기
                                bossBullets.remove(bossBullets.get(i));
                            }
                        } else if (bossBullets.get(i).getAttackType() == 0) {
                            if (bossBullets.get(i).getPositionY() < this.getHeight() + bossBullets.get(i).getBulletImage().getHeight()) {
                                bossBullets.get(i).move(0, 2);
                            } else { // 화면 밖으로 나가면 없애기
                                bossBullets.remove(bossBullets.get(i));
                            }
                        } else if (bossBullets.get(i).getAttackType() == 2) {
                            if (bossBullets.get(i).getPositionY() < this.getHeight() + bossBullets.get(i).getBulletImage().getHeight()) {
                                bossBullets.get(i).move(0, 5);
                            } else { // 화면 밖으로 나가면 없애기
                                bossBullets.remove(bossBullets.get(i));
                            }
                        } else if (bossBullets.get(i).getAttackType() >= 4 && bossBullets.get(i).getAttackType() <= 9) {
                            if (bossBullets.get(i).getAttackType() == 6 || bossBullets.get(i).getAttackType() == 7 || bossBullets.get(i).getAttackType() == 9) {
                                if (bossBullets.get(i).getPositionX() < this.getWidth()) {
                                    if (bossBullets.get(i).getAttackType() == 6) {
                                        bossBullets.get(i).move(4, 2);
                                    } else {
                                        bossBullets.get(i).move(4, 0);
                                    }
                                } else {
                                    bossBullets.remove(bossBullets.get(i));
                                }
                            } else {
                                if (bossBullets.get(i).getPositionX() > -bossBullets.get(i).getBulletImage().getWidth()) {
                                    if (bossBullets.get(i).getAttackType() == 4 || bossBullets.get(i).getAttackType() == 5) {
                                        bossBullets.get(i).move(-4, 2);
                                    } else {
                                        bossBullets.get(i).move(-4, 0);
                                    }
                                } else {
                                    bossBullets.remove(bossBullets.get(i));
                                }

                            }
                        }
                    }
                }

                if (bossLevels.contains(level) && bossMovement != null && bossMovement.checkFinished()) {
                    boss.randomMove();
                }

                if (bossLevels.contains(level) && hacked && level == 3) {
                    if (hackedTime.checkFinished()) {
                        hacked = false; // 해킹 종료
                    } else {
                        if (hackedEffect.checkFinished()) {
                            hackedEffect.reset(); // 깜빡임 간격 리셋
                            hackedVisible = !hackedVisible;
                        }
                    }
                }

                if (enemyShipFormation != null) {
                    this.enemyShipFormation.update();
                    this.enemyShipFormation.shoot(this.bullets, this.level, balance);
                }

                if (this.enemyShipSpecial != null) {
                    // special 함선돠 만나면 아래로 강제 이동
                    if (checkCollision(ship, enemyShipSpecial)) ship.moveDown(5);
                    if (!this.enemyShipSpecial.isDestroyed())
                        this.enemyShipSpecial.move(2, 0);
                    else if (this.enemyShipSpecialExplosionCooldown.checkFinished())
                        this.enemyShipSpecial = null;
                }
                if (this.enemyShipSpecial == null
                        && this.enemyShipSpecialCooldown.checkFinished()) {
                    this.enemyShipSpecial = new EnemyShip();
                    this.alertMessage = "";
                    this.enemyShipSpecialCooldown.reset();
                    soundManager.playSound(Sound.UFO_APPEAR, balance);
                    this.logger.info("A special ship appears");
                }
                if (this.enemyShipSpecial == null && !bossLevels.contains(level) && !bonusBossLevels.contains(level)
                        && this.enemyShipSpecialCooldown.checkAlert()) {
                    switch (this.enemyShipSpecialCooldown.checkAlertAnimation()) {
                        case 1:
                            this.alertMessage = "--! ALERT !--";
                            break;

                        case 2:
                            this.alertMessage = "-!! ALERT !!-";
                            break;

                        case 3:
                            this.alertMessage = "!!! ALERT !!!";
                            break;

                        default:
                            this.alertMessage = "";
                            break;
                    }
                }
                if (this.enemyShipSpecial != null
                        && this.enemyShipSpecial.getPositionX() > this.width) {
                    this.enemyShipSpecial = null;
                    this.logger.info("The special ship has escaped");
                }

                if (!ship.isPuzzleActive()) {
					boolean player1Attacking = false;
					if (!hacked) {
						player1Attacking = inputManager.isKeyDown(KeyEvent.VK_SPACE);
					}
					boolean player2Attacking = false;
                    player2Attacking = inputManager.isKeyDown(KeyEvent.VK_SHIFT);

                    if (player1Attacking && player2Attacking) {
                        // Both players are attacking
                        if (this.ship.shoot(this.bullets, this.itemManager.getShotNum()))
                            this.bulletsShot += this.itemManager.getShotNum();
                    } else {
                        switch (playerNumber) {
                            case 1:
                                if (player2Attacking) {
                                    if (this.ship.shoot(this.bullets, this.itemManager.getShotNum(), 1.0f)) // Player 1 attack
                                        this.bulletsShot += this.itemManager.getShotNum();
                                }
                                // 플레이어 2 ENTER 누르면 총알 각도 조정 모드 on
                                if (this.inputManager.isKeyDown(KeyEvent.VK_ENTER)) {
                                    if (this.inputManager.isKeyDown(KeyEvent.VK_LEFT)) this.ship.moveAngleToLeft();
                                    if (this.inputManager.isKeyDown(KeyEvent.VK_RIGHT)) this.ship.moveAngleToRight();
                                }
                                break;
                            case 0:
                                if (player1Attacking) {
                                    if (this.ship.shoot(this.bullets, this.itemManager.getShotNum(), -1.0f)) // Player 1 attack
                                        this.bulletsShot += this.itemManager.getShotNum();
                                }
                                // 플레이어 1 E키 누르면 총알 각도 조정 모드 on
                                if (this.inputManager.isKeyDown(KeyEvent.VK_E)) {
                                    if (this.inputManager.isKeyDown(KeyEvent.VK_A)) this.ship.moveAngleToLeft();
                                    if (this.inputManager.isKeyDown(KeyEvent.VK_D)) this.ship.moveAngleToRight();
                                }
                                break;
                            default: //playerNumber = -1
                                if (player1Attacking) {
                                    if (this.ship.shoot(this.bullets, this.itemManager.getShotNum(), 0.0f)) // Player 1 attack
                                        this.bulletsShot += this.itemManager.getShotNum();
                                }
                                // 1인 모드에서 SHIFT 누르면 총알 각도 조정 모드 on
                                if (this.inputManager.isKeyDown(KeyEvent.VK_SHIFT)) {
                                    if (this.inputManager.isKeyDown(KeyEvent.VK_LEFT)) this.ship.moveAngleToLeft();
                                    if (this.inputManager.isKeyDown(KeyEvent.VK_RIGHT)) this.ship.moveAngleToRight();
                                }
                                break;
                        }
                    }



                    /*Elapsed Time Update*/
                    long currentTime = System.currentTimeMillis();

                    if (this.prevTime != null)
                        this.elapsedTime += (int) (currentTime - this.prevTime);

                    this.prevTime = (int) currentTime;

                    if (!itemManager.isGhostActive())
                        this.ship.setColor(Color.GREEN);

                    if (!this.ship.isDestroyed()) {
                        // boolean 초기값 설정
                        boolean moveRight = false;
                        boolean moveLeft = false;
                        boolean moveUp = false;
                        boolean moveDown = false;
                        switch (playerNumber) {
                            case 0:
                                // 플레이어 1 E를 안눌렀을 때 이동 가능
                                if (!inputManager.isKeyDown(KeyEvent.VK_E)) {
                                    moveRight = inputManager.isKeyDown(KeyEvent.VK_D);
                                    moveLeft = inputManager.isKeyDown(KeyEvent.VK_A);
                                    moveUp = inputManager.isKeyDown(KeyEvent.VK_W);
                                    moveDown = inputManager.isKeyDown(KeyEvent.VK_S);
                                }
                                break;
                            case 1:
                                // 플레이어 2 ENTER 안눌렀을 때 이동 가능
                                if (!inputManager.isKeyDown(KeyEvent.VK_ENTER)) {
                                    moveRight = inputManager.isKeyDown(KeyEvent.VK_RIGHT);
                                    moveLeft = inputManager.isKeyDown(KeyEvent.VK_LEFT);
                                    moveUp = inputManager.isKeyDown(KeyEvent.VK_UP);
                                    moveDown = inputManager.isKeyDown(KeyEvent.VK_DOWN);
                                }
                                break;
                            default:
                                // 1인모드에서 SHIFT 안눌렀을 때 이동 가능
                                moveRight = (inputManager.isKeyDown(KeyEvent.VK_RIGHT)
                                        || inputManager.isKeyDown(KeyEvent.VK_D)) && !inputManager.isKeyDown(KeyEvent.VK_SHIFT);
                                moveLeft = (inputManager.isKeyDown(KeyEvent.VK_LEFT)
                                        || inputManager.isKeyDown(KeyEvent.VK_A)) && !inputManager.isKeyDown(KeyEvent.VK_SHIFT);
                                moveUp = (inputManager.isKeyDown(KeyEvent.VK_UP)
                                        || inputManager.isKeyDown(KeyEvent.VK_W)) && !inputManager.isKeyDown(KeyEvent.VK_SHIFT);
                                moveDown = (inputManager.isKeyDown(KeyEvent.VK_DOWN)
                                        || inputManager.isKeyDown(KeyEvent.VK_S)) && !inputManager.isKeyDown(KeyEvent.VK_SHIFT);
                        }

						boolean isRightBorder = this.ship.getPositionX()
								+ this.ship.getWidth() + this.ship.getSpeed() > this.width - 1;
						boolean isLeftBorder = this.ship.getPositionX()
								- this.ship.getSpeed() < 1;
						// 적군 아래에서만 이동 가능하도록 변경
						boolean isUpBorder = this.ship.getPositionY()
								- this.ship.getSpeed() < this.enemyShipFormation.getPositionY() + this.enemyShipFormation.getHeight() + 2;
						boolean isDownBorder = this.ship.getPositionY()
								+ this.ship.getHeight() + this.ship.getSpeed() > this.height - 1;

						// 가장 아래 적군보다 위로 올라갈 경우 강제로 아래로 이동
						if(this.ship.getPositionY() < this.enemyShipFormation.getPositionY() + this.enemyShipFormation.getHeight()){
							this.ship.moveDown(this.enemyShipFormation.getHeight());
						}

						if (moveDown && !isDownBorder
								&& !checkCollision(this.ship, this.block, "down")) {
							if (playerNumber == -1) this.ship.moveDown();
							else this.ship.moveDown(balance);
						}
						if (enemyShipFormation != null && checkCollision(this.ship, this.enemyShipFormation.getListEnemies(), "down")
								|| checkCollision(this.ship, this.barriers, "down")) ship.moveUp(5);

                        if (moveUp && !isUpBorder
                                && !checkCollision(this.ship, this.block, "up")) {
                            if (playerNumber == -1) this.ship.moveUp();
                            else this.ship.moveUp(balance);
                        }
                        if (enemyShipFormation != null && checkCollision(this.ship, this.enemyShipFormation.getListEnemies(), "up")
                                || checkCollision(this.ship, this.barriers, "up")) ship.moveDown(5);

                        if (moveRight && !isRightBorder
                                && !checkCollision(this.ship, this.block, "right")) {
                            if (playerNumber == -1) this.ship.moveRight();
                            else this.ship.moveRight(balance);
                        }
                        if (enemyShipFormation != null && checkCollision(this.ship, this.enemyShipFormation.getListEnemies(), "right")
                                || checkCollision(this.ship, this.barriers, "right")) ship.moveLeft(5);

                        if (moveLeft && !isLeftBorder
                                && !checkCollision(this.ship, this.block, "left")) {
                            if (playerNumber == -1) this.ship.moveLeft();
                            else this.ship.moveLeft(balance);
                        }
                        if (enemyShipFormation != null && checkCollision(this.ship, this.enemyShipFormation.getListEnemies(), "left")
                                || checkCollision(this.ship, this.barriers, "left")) ship.moveRight(5);
                        if (!bonusBossLevels.contains(level) && web != null) {
                            for (int i = 0; i < web.size(); i++) {
                                //escape Spider Web
                                if (ship.getPositionX() + 6 <= web.get(i).getPositionX() - 6
                                        || web.get(i).getPositionX() + 6 <= ship.getPositionX() - 6
                                        || ship.getPositionY() + 4 <= web.get(i).getPositionY() - 4
                                        || web.get(i).getPositionY() + 4 <= ship.getPositionY() - 4) {
                                    this.ship.setThreadWeb(false);
                                }
                                //get caught in a spider's web
                                else {
                                    this.ship.setThreadWeb(true);
                                    break;
                                }
                            }
                        }
                        // 플레이어 함선이 움직이던 중 배리어와 겹쳐졌을 버그 발생 시 플레이어 함선을 아래로 강제 이동시킴
                        if (!barriers.isEmpty()) {
                            for (Barrier check : barriers) {
                                if (checkCollision(ship, check)) {
                                    ship.moveDown(10);
                                }
                            }
                        }
                    }

                    this.ship.update();

                    if (level >= 3) { //Events where vision obstructions appear start from level 3 onwards.
                        handleBlockerAppearance();
                    }
                    if (bonusBossLevels.contains(level) && bonusLevelCountdown == null) {
                        bonusLevelCountdown = Core.getCooldown(20000);
                        bonusLevelCountdown.reset();
                    }
                    if (bonusBossLevels.contains(level)) {
                        if (barDisappear.checkFinished()) {
                            barWidth -= barInitialWidth / 20; // 20초 후에 사라짐
                            barDisappear.reset();
                        }
                    }
                }
            }

            // If Time-stop is active, Stop updating enemy ships' move and their shoots.
            if (!itemManager.isTimeStopActive()) {
				if (enemyShipFormation != null) {
					this.enemyShipFormation.update();
					this.enemyShipFormation.shoot(this.bullets, this.level, this.balance);
				}
			}

            manageCollisions();
            cleanBullets();
            if (playerNumber >= 0)
                drawThread();
            else
                draw();

            if ((enemyShipFormation != null && this.enemyShipFormation.isEmpty() && !bonusBossLevels.contains(level) && !bossLevels.contains(level) || this.lives <= 0)
                    && !this.levelFinished) {
                this.levelFinished = true;

                soundManager.stopSound(soundManager.getCurrentBGM());
                if (this.lives == 0)
                    soundManager.playSound(Sound.GAME_END);
                this.screenFinishedCooldown.reset();
            }


            if (enemyShipFormation != null && enemyShipFormation.isEmpty() && bossLevels.contains(level)) {
                if (enemyByPattern) {
                    enemyByPattern = false;
                    bossPattern.reset(6000);
                }
            }


            if (bonusBossLevels.contains(level)) {
                if ((bonusBoss != null && bonusBoss.isDestroyed() || bonusLevelCountdown != null && bonusLevelCountdown.checkFinished()) && !this.levelFinished) {
                    this.levelFinished = true;
                    soundManager.stopSound(soundManager.getCurrentBGM());
                    if (this.lives == 0)
                        soundManager.playSound(Sound.GAME_END);
                    this.screenFinishedCooldown.reset();
                }
            }




            if (this.levelFinished && this.screenFinishedCooldown.checkFinished()) {
                //Reset alert message when level is finished
                this.alertMessage = "";
                this.isRunning = false;
            }

            if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)) {
                checkPause = !checkPause;
                this.pauseESCCooldown.reset();
            }
        } else {
            if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)) {
                checkPause = !checkPause;
                this.pauseESCCooldown.reset();
            }
            if (inputManager.isKeyDown(KeyEvent.VK_DOWN)) {
                if (checkPauseClick == 1) checkPauseClick = 0;
                else checkPauseClick++;
                this.pauseESCCooldown.reset();
            }
            if (inputManager.isKeyDown(KeyEvent.VK_UP)) {
                if (checkPauseClick == 0) checkPauseClick = 1;
                else checkPauseClick--;
                this.pauseESCCooldown.reset();
			}
			if (playerNumber >= 0)
				drawThread();
			else
				draw();
			if(inputManager.isKeyDown((KeyEvent.VK_ENTER))){
				if(checkPauseClick == 1){
					returnCode = 1;
					this.isRunning = false;
				}else{
					checkPause = !checkPause;
				}
			}
		}
	}
	private boolean checkCollision(final Ship ship, List< ? extends Entity> wanted ,final String direction) {
		if (!bonusBossLevels.contains(level) && !bossLevels.contains(level)) {
			for (Entity entity : wanted) {
				if (direction.equals("down")) {
					if (checkCollision((int) ship.getPositionX(), (int) ship.getPositionY() + ship.getSpeed(),
							ship.getWidth(), ship.getHeight(), entity)) {
						return true; // 아래쪽 충돌
					}
				} else if (direction.equals("up")) {
					if (checkCollision((int) ship.getPositionX(), (int) ship.getPositionY() - ship.getSpeed(),
							ship.getWidth(), ship.getHeight(), entity)) {
						return true; // 위쪽 충돌
					}
				} else if (direction.equals("right")) {
					if (checkCollision((int) ship.getPositionX() + ship.getSpeed(), (int) ship.getPositionY(),
							ship.getWidth(), ship.getHeight(), entity)) {
						return true; // 오른쪽 충돌
					}
				} else if (direction.equals("left")) {
					if (checkCollision((int) ship.getPositionX() - ship.getSpeed(), (int) ship.getPositionY(),
							ship.getWidth(), ship.getHeight(), entity)) {
						return true; // 왼쪽 충돌
					}
				}
			}
		}
		return false; // 충돌 없음
	}
	private boolean checkCollision(final Ship ship, Set< ? extends Entity> wanted ,final String direction) {
		for (Entity entity : wanted) {
			if (direction.equals("down")) {
				if (checkCollision(ship, entity)) {
					return true; // 아래쪽 충돌
				}
			} else if (direction.equals("up")) {
				if (checkCollision(ship,entity)){
					return true; // 위쪽 충돌
				}
			} else if (direction.equals("right")) {
				if (checkCollision(ship, entity)) {
					return true; // 오른쪽 충돌
				}
			} else if (direction.equals("left")) {
				if (checkCollision(ship, entity)) {
					return true; // 왼쪽 충돌
				}
			}
		}
		return false; // 충돌 없음
	}
	private boolean checkCollision(final int x1, final int y1, final int width1, final int height1,
								   final Entity entity) {
		if(entity == null) return false; // 파괴된 적군함선은 listEnemy에 null로 저장되어 있기에 예외처리
		int x2 = (int)entity.getPositionX();
		int y2 = (int)entity.getPositionY();
		int width2 = entity.getWidth();
		int height2 = entity.getHeight();

		// 충돌 감지 (AABB 방식)
		return x1 < x2 + width2 && x1 + width1 > x2 &&
				y1 < y2 + height2 && y1 + height1 > y2;
	}

	private void updatePuzzleState() {
		if (ship.isPuzzleActive() && this.puzzleScreen != null) {
			boolean keyPressed = false;
			if (playerNumber == 0) {
				keyPressed = inputManager.isKeyDown(KeyEvent.VK_W) ||
						inputManager.isKeyDown(KeyEvent.VK_A) ||
						inputManager.isKeyDown(KeyEvent.VK_S) ||
						inputManager.isKeyDown(KeyEvent.VK_D);
			} else {
				keyPressed = inputManager.isKeyDown(KeyEvent.VK_UP) ||
						inputManager.isKeyDown(KeyEvent.VK_DOWN) ||
						inputManager.isKeyDown(KeyEvent.VK_LEFT) ||
						inputManager.isKeyDown(KeyEvent.VK_RIGHT);
			}
			if (keyPressed) {
				int keyCode = inputManager.getLastKeyPressed();
				if (puzzleScreen != null && puzzleScreen.handleInput(keyCode)) {
					completePuzzle();
				}
				inputManager.resetLastKeyPressed();
			}
		}
	}

	private void initializePuzzle() {
		if (!ship.isPuzzleActive() && webCooldown.checkFinished()) {
			ship.setThreadWeb(true);
			ship.setPlayerShip(true);
			ship.setPuzzleActive(true);

			this.puzzleScreen = new PuzzleScreen(playerNumber);
			inputManager.setPuzzleMode(true);

			logger.info("Ship puzzle state: " + ship.isPuzzleActive());  // 상태 확인용 로그
		}
	}

	private void resetPuzzle() {
		if (puzzleRetryCooldown.checkFinished()) {
			this.puzzleScreen = new PuzzleScreen(playerNumber);
		}
	}

	private void completePuzzle() {
		ship.setPuzzleActive(false);
		ship.setThreadWeb(false);
		this.puzzleScreen = null;
		inputManager.setPuzzleMode(false);
		webCooldown.reset();
		logger.info("Puzzle completed successfully");
	}

	/**
	 * Draws the elements associated with the screen.
	 */
	private void draw() {
		drawManager.initDrawing(this);
		drawManager.drawGameTitle(this);
		// 1인 모드 총알 경로
		drawManager.drawLaunchTrajectory( this, (int) this.ship.getPositionX(), (int) this.ship.getPositionY(), this.ship.getAngle());
		drawManager.drawEntity(this.ship, (int) this.ship.getPositionX(), (int) this.ship.getPositionY());
		drawManager.drawItemHud(this, this.height, itemManager.getStoredItems());


		if(checkPause){
			drawManager.drawPause(this, checkPauseClick);
		}

		//draw Gravity Enemy
        if (physicsEnemyShips != null) {
            for (int i = 0; i < physicsEnemyShips.size(); i++) {
                drawManager.drawEntity(this.physicsEnemyShips.get(i), (int) this.physicsEnemyShips.get(i).getPositionX(),
                        (int) this.physicsEnemyShips.get(i).getPositionY());
		    }
        }
		//draw Spider Web
        if (!bonusBossLevels.contains(level) && !bossLevels.contains(level)) {
            for (int i = 0; i < web.size(); i++) {
                drawManager.drawEntity(this.web.get(i), (int) this.web.get(i).getPositionX(), (int) this.web.get(i).getPositionY());
            }
            //draw Blocks
            for (Block block : block)
                drawManager.drawEntity(block, (int) block.getPositionX(), (int) block.getPositionY());
        }

		if (bossLevels.contains(level) && bossBullets != null) {
			for (int i = 0; i < bossBullets.size(); i++) {
				if (bossBullets.get(i).getAttackType() != 0 && bossBullets.get(i).getAttackType() != 2) { // 기본 공격은 보스 위에 그리게 하려고
					if (bossBullets.get(i).getAttackType() != 3) {
						drawManager.drawBossBullet(bossBullets.get(i), (int) bossBullets.get(i).getPositionX(), (int) bossBullets.get(i).getPositionY());
					} else {
						if (!laserActive && (laserCooldown == null || laserCooldown.checkFinished())) {
							// 레이저 시작
							laserHeight = 1;
							laserActive = true;
							laserDrawComplete = false;
							laserMaintainCooldown = null;
							laserCooldown = null;
						}

						if (laserActive) {
							if (!laserDrawComplete) {
								// 레이저가 다 그려지는 중
								if (laserHeight < bossBullets.get(i).getBulletImage().getHeight()) {
									laserHeight += 5;
								} else {
									// 레이저가 완전히 그려짐
									laserDrawComplete = true;
									laserMaintainCooldown = Core.getCooldown(5000); // 5초 동안 유지
									laserMaintainCooldown.reset();
								}
							} else if (laserMaintainCooldown != null && laserMaintainCooldown.checkFinished()) {
								// 유지 시간 종료
								laserActive = false;
								laserHeight = 1;
								laserCooldown = Core.getCooldown(6000); // 6초 쿨다운 설정
								laserCooldown.reset();
							}

							// 레이저 그리기
							drawManager.drawBossLaser(bossBullets.get(i), (int) boss.getPositionX(), (int) boss.getPositionY(), laserHeight, left);
						}
					}
				}
			}
		}


		if (this.enemyShipSpecial != null)
			drawManager.drawEntity(this.enemyShipSpecial,
					(int) this.enemyShipSpecial.getPositionX(),
					(int) this.enemyShipSpecial.getPositionY());

		if (enemyShipFormation != null) {
			enemyShipFormation.draw();
		}

		for (ItemBox itemBox : this.itemBoxes)
			drawManager.drawEntity(itemBox, (int) itemBox.getPositionX(), (int) itemBox.getPositionY());

		for (Barrier barrier : this.barriers)
			drawManager.drawEntity(barrier, (int) barrier.getPositionX(), (int) barrier.getPositionY());

		for (Bullet bullet : this.bullets)
			drawManager.drawEntity(bullet, (int) bullet.getPositionX(),
                    (int) bullet.getPositionY());


		if (bossLevels.contains(level)) {
			drawManager.drawBoss(this, boss);
		}

		if (bossLevels.contains(level) && bossBullets != null) {
			for (int i = 0; i < bossBullets.size(); i++) {
				if (bossBullets.get(i).getAttackType() == 0 || bossBullets.get(i).getAttackType() == 2) { // 기본 공격은 보스 위에 그리게 하려고
					if (bossBullets.get(i).getAttackType() != 3) { // 레이저는 따로 그려야됨
						drawManager.drawBossBullet(bossBullets.get(i), (int) bossBullets.get(i).getPositionX(), (int) bossBullets.get(i).getPositionY());
					}
				}
			}
		}

		if (bossLevels.contains(level) && hacked) {
			if (hackedVisible) {
				drawManager.drawHackedState(this); // HackedState를 그리기
			}
		}

		// Interface.
		drawManager.drawTopInterfaceBox(this);
		drawManager.drawScore(this, this.score);
		drawManager.drawElapsedTime(this, this.elapsedTime);
		drawManager.drawAlertMessage(this, this.alertMessage);
		drawManager.drawLives(this, this.lives, this.shipType);
		drawManager.drawLevel(this, this.level);
		drawManager.drawHorizontalLine(this, SEPARATION_LINE_HEIGHT - 1);
		drawManager.drawReloadTimer(this,this.ship,ship.getRemainingReloadTime());
		drawManager.drawCombo(this,this.combo);

		if(bonusBossLevels.contains(level)) drawManager.drawBonusBoss(this, level, bonusBoss, 50, 50);
		if (bonusBossLevels.contains(level)) {
			drawManager.drawTimerBar(this, barX, barY, barWidth, barHeight);
		}

		if (bossLevels.contains(level)) {
			drawManager.drawBossHPBar(this, 32, 72, hpBarWidth, 20);
		}


		// Countdown to game start.
		if (!this.inputDelay.checkFinished()) {
			int countdown = (int) ((INPUT_DELAY - (System.currentTimeMillis() - this.gameStartTime)) / 1000);
			drawManager.drawCountDown(this, this.level, countdown, this.bonusLife);
			drawManager.drawHorizontalLine(this, this.height / 2 - this.height / 12);
			drawManager.drawHorizontalLine(this, this.height / 2 + this.height / 12);

			//Intermediate aggregation
			if (this.level > 1){
				if (countdown == 0) {
					//Reset mac combo and edit temporary values
					this.lapTime = this.elapsedTime;
					this.tempScore = this.score;
					this.maxCombo = 0;
				} else {
					// Don't show it just before the game starts, i.e. when the countdown is zero.
					drawManager.interAggre(this, this.level - 1, this.maxCombo, this.elapsedTime, this.lapTime, this.score, this.tempScore);
				}
			}
		}

		//add drawRecord method for drawing
		drawManager.drawRecord(highScores,this);

		// Blocker drawing part
		if (!blockers.isEmpty() && !bossLevels.contains(level) && !bonusBossLevels.contains(level)) {
			for (Blocker blocker : blockers) {
				drawManager.drawRotatedEntity(blocker, (int) blocker.getPositionX(), (int) blocker.getPositionY(), blocker.getAngle());
			}
		}

		// draw puzzle screen
		if (this.ship.isPuzzleActive() && this.puzzleScreen != null) {
			drawManager.drawPuzzle(this, ship,
					puzzleScreen.getDirectionSequence(),
					puzzleScreen.getPlayerInput(),
					playerNumber);
		}
		drawManager.completeDrawing(this);
	}


	// Methods that handle the position, angle, sprite, etc. of the blocker (called repeatedly in update.)
	private void handleBlockerAppearance() {

		if (level >= 3 && level < 6) MAX_BLOCKERS = 1;
		else if (level >= 6 && level < 11) MAX_BLOCKERS = 2;
		else if (level >= 11) MAX_BLOCKERS = 3;

		int kind = random.nextInt(2-1 + 1) +1; // 1~2
		DrawManager.SpriteType newSprite;
		switch (kind) {
			case 1:
				newSprite = DrawManager.SpriteType.Blocker1; // artificial satellite
				break;
			case 2:
				newSprite = DrawManager.SpriteType.Blocker2; // astronaut
				break;
			default:
				newSprite = DrawManager.SpriteType.Blocker1;
				break;
		}

		// Check number of blockers, check timing of exit
		if (blockers.size() < MAX_BLOCKERS && blockerCooldown.checkFinished()) {
			boolean moveLeft = random.nextBoolean(); // Randomly sets the movement direction of the current blocker
			int startY = random.nextInt(this.height - 90) + 25; // Random Y position with margins at the top and bottom of the screen
			int startX = moveLeft ? this.width + 300 : -300; // If you want to move left, outside the right side of the screen, if you want to move right, outside the left side of the screen.
			// Add new Blocker
			if (moveLeft) {
				blockers.add(new Blocker(startX, startY, newSprite, moveLeft)); // move from right to left
			} else {
				blockers.add(new Blocker(startX, startY, newSprite, moveLeft)); // move from left to right
			}
			blockerCooldown.reset();
		}

		// Items in the blocker list that will disappear after leaving the screen
		List<Blocker> toRemove = new ArrayList<>();
		for (int i = 0; i < blockers.size(); i++) {
			Blocker blocker = blockers.get(i);

			// If the blocker leaves the screen, remove it directly from the list.
			if (blocker.getMoveLeft() && blocker.getPositionX() < -300 || !blocker.getMoveLeft() && blocker.getPositionX() > this.width + 300) {
				blockers.remove(i);
				i--; // When an element is removed from the list, the index must be decreased by one place.
				continue;
			}

			// Blocker movement and rotation (positionX, Y value change)
			if (blocker.getMoveLeft()) {
				blocker.move(-1.5, 0); // move left
			} else {
				blocker.move(1.5, 0); // move right
			}
			blocker.rotate(0.2); // Blocker rotation
		}

		// Remove from the blocker list that goes off screen
		blockers.removeAll(toRemove);
	}

	/**
	 * Draws the elements associated with the screen to thread buffer.
	 */
	private void drawThread() {
		drawManager.initThreadDrawing(this, playerNumber);
		drawManager.drawGameTitle(this, playerNumber);
		// 2인모드 총알 각도
		drawManager.drawLaunchTrajectory( this, (int)this.ship.getPositionX(), (int)this.ship.getPositionY(),playerNumber , this.ship.getAngle());

		drawManager.drawEntity(this.ship, (int) this.ship.getPositionX(),
                (int) this.ship.getPositionY(), playerNumber);

		//draw Gravity Enemy
		if (physicsEnemyShips != null) {
			for (int i = 0; i < physicsEnemyShips.size(); i++) {
				drawManager.drawEntity(this.physicsEnemyShips.get(i), (int) this.physicsEnemyShips.get(i).getPositionX(),
						(int) this.physicsEnemyShips.get(i).getPositionY(), playerNumber);
			}
		}

		//draw Spider Web
		if (!bonusBossLevels.contains(level)) {
			for (int i = 0; i < web.size(); i++) {
				drawManager.drawEntity(this.web.get(i), (int) this.web.get(i).getPositionX(),
						(int) this.web.get(i).getPositionY(), playerNumber);
			}
		}
		//draw Blocks
		if (!bonusBossLevels.contains(level)) {
			for (Block block : block)
				drawManager.drawEntity(block, (int) block.getPositionX(),
						(int) block.getPositionY(), playerNumber);
		}

		if (this.enemyShipSpecial != null)
			drawManager.drawEntity(this.enemyShipSpecial,
                    (int) this.enemyShipSpecial.getPositionX(),
                    (int) this.enemyShipSpecial.getPositionY(), playerNumber);

		enemyShipFormation.draw(playerNumber);

		if (!bonusBossLevels.contains(level) && !bossLevels.contains(level)) {
			for (ItemBox itemBox : this.itemBoxes)
				drawManager.drawEntity(itemBox, (int) itemBox.getPositionX(), (int) itemBox.getPositionY(), playerNumber);

			for (Barrier barrier : this.barriers)
				drawManager.drawEntity(barrier, (int) barrier.getPositionX(), (int) barrier.getPositionY(), playerNumber);

			for (Bullet bullet : this.bullets)
				drawManager.drawEntity(bullet, (int) bullet.getPositionX(),
                        (int) bullet.getPositionY(), playerNumber);
		}

		// Interface.
		drawManager.drawScore(this, this.score, playerNumber);
		drawManager.drawElapsedTime(this, this.elapsedTime, playerNumber);
		drawManager.drawAlertMessage(this, this.alertMessage, playerNumber);
		drawManager.drawLives(this, this.lives, this.shipType, playerNumber);
		drawManager.drawLevel(this, this.level, playerNumber);
		drawManager.drawHorizontalLine(this, SEPARATION_LINE_HEIGHT - 1, playerNumber);
		drawManager.drawReloadTimer(this,this.ship,ship.getRemainingReloadTime(), playerNumber);
		drawManager.drawCombo(this,this.combo, playerNumber);

		// Show GameOver if one player ends first
		if (this.levelFinished && this.screenFinishedCooldown.checkFinished() && this.lives <= 0) {
			drawManager.drawInGameOver(this, this.height, playerNumber);
			drawManager.drawHorizontalLine(this, this.height / 2 - this.height
					/ 12, playerNumber);
			drawManager.drawHorizontalLine(this, this.height / 2 + this.height
					/ 12, playerNumber);
		}

		// Countdown to game start.
		if (!this.inputDelay.checkFinished()) {
			int countdown = (int) ((INPUT_DELAY - (System.currentTimeMillis() - this.gameStartTime)) / 1000);
			drawManager.drawCountDown(this, this.level, countdown,
					this.bonusLife, playerNumber);
			drawManager.drawHorizontalLine(this, this.height / 2 - this.height
					/ 12, playerNumber);
			drawManager.drawHorizontalLine(this, this.height / 2 + this.height
					/ 12, playerNumber);

			//Intermediate aggregation
			if (this.level > 1){
				if (countdown == 0) {
					//Reset mac combo and edit temporary values
					this.lapTime = this.elapsedTime;
					this.tempScore = this.score;
					this.maxCombo = 0;
				} else {
					// Don't show it just before the game starts, i.e. when the countdown is zero.
					drawManager.interAggre(this, this.level - 1, this.maxCombo, this.elapsedTime, this.lapTime, this.score, this.tempScore, playerNumber);
				}
			}
		}

		//add drawRecord method for drawing
		drawManager.drawRecord(highScores,this, playerNumber);

		// Blocker drawing part
		if (!blockers.isEmpty()) {
			for (Blocker blocker : blockers) {
				drawManager.drawRotatedEntity(blocker, (int) blocker.getPositionX(), (int) blocker.getPositionY(), blocker.getAngle(), playerNumber);
			}
		}

		// draw puzzle screen
		if (this.ship.isPuzzleActive() && this.puzzleScreen != null) {
			drawManager.drawPuzzle(this,
					this.puzzleScreen.getDirectionSequence(),
					this.puzzleScreen.getPlayerInput(),
					playerNumber,
					playerNumber,
					this.ship.getCollisionX(),
					this.ship.getCollisionY());
		}
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

		if (isExecuted == false){
			isExecuted = true;
			timer = new Timer();
			timerTask = new TimerTask() {
				public void run() {
					combo = 0;
				}
			};
			timer.schedule(timerTask, 3000);
		}

		int topEnemyY = Integer.MAX_VALUE;
		if (enemyShipFormation != null) {
			for (EnemyShip enemyShip : this.enemyShipFormation) {
				if (enemyShip != null && !enemyShip.isDestroyed() && enemyShip.getPositionY() < topEnemyY) {
					topEnemyY = (int) enemyShip.getPositionY();
				}
			}
		}
		if (this.enemyShipSpecial != null && !this.enemyShipSpecial.isDestroyed() && this.enemyShipSpecial.getPositionY() < topEnemyY) {
			topEnemyY = (int) this.enemyShipSpecial.getPositionY();
		}

		for (Bullet bullet : this.bullets) {

			// Enemy ship's bullets
			if (bullet.getSpeed() > 0) {
				if (checkCollision(bullet, this.ship) && !this.levelFinished && !itemManager.isGhostActive()) {
					recyclable.add(bullet);
					if (!this.ship.isDestroyed()) {
						this.ship.destroy(balance);
						lvdamage();
						this.logger.info("Hit on player ship, " + this.lives + " lives remaining.");
					}
				}

				if (this.barriers != null) {
					Iterator<Barrier> barrierIterator = this.barriers.iterator();
					while (barrierIterator.hasNext()) {
						Barrier barrier = barrierIterator.next();
						if (checkCollision(bullet, barrier)) {
							recyclable.add(bullet);
							barrier.reduceHealth(balance);
							if (barrier.isDestroyed()) {
								barrierIterator.remove();
							}
						}
					}
				}

			} else {	// Player ship's bullets
				if (enemyShipFormation != null) {
					for (EnemyShip enemyShip : this.enemyShipFormation)
						if (enemyShip != null && !enemyShip.isDestroyed()
								&& checkCollision(bullet, enemyShip)) {
							// Decide whether to destroy according to physical strength
							this.enemyShipFormation.HealthManageDestroy(enemyShip, balance);
							// If the enemy doesn't die, the combo increases;
							// if the enemy dies, both the combo and score increase.
							this.score += Score.comboScore(this.enemyShipFormation.getPoint(), this.combo);
							this.shipsDestroyed += this.enemyShipFormation.getDistroyedship();
							this.combo++;
							this.hitBullets++;
							if (this.combo > this.maxCombo) this.maxCombo = this.combo;
							timer.cancel();
							isExecuted = false;
							recyclable.add(bullet);

							if (enemyShip.getHealth() < 0 && itemManager.dropItem() && !bossLevels.contains(level)) {
								this.itemBoxes.add(new ItemBox((int) (enemyShip.getPositionX() + 6), (int) (enemyShip.getPositionY() + 1), balance));
								logger.info("Item box dropped");
							}
						}
				}

				//중력 적들과 충돌했을 때 로직
				if(this.physicsEnemyShips != null) {
					Iterator<PhysicsEnemyShip> iterator = this.physicsEnemyShips.iterator();
					while (iterator.hasNext()) {
						PhysicsEnemyShip physicsEnemyShip = iterator.next();
						if (physicsEnemyShip != null && !physicsEnemyShip.isDestroyed() && checkCollision(bullet, physicsEnemyShip)) {
							System.out.println(this.physicsEnemyShips.size());
							iterator.remove(); // 안전하게 요소 삭제
							System.out.println(this.physicsEnemyShips.size());
							this.score += Score.comboScore(physicsEnemyShip.getPointValue(), this.combo);
							this.shipsDestroyed++;
							this.combo++;
							this.hitBullets++;
							if (this.combo > this.maxCombo) this.maxCombo = this.combo;
							physicsEnemyShip.destroy(balance);
							timer.cancel();
							isExecuted = false;
							itemManager.dropItem();
							this.itemBoxes.add(new ItemBox((int) (physicsEnemyShip.getPositionX() + 6), (int) (physicsEnemyShip.getPositionY() + 1), balance));
							logger.info("Item box dropped");
						}
					}
				}

				if (this.enemyShipSpecial != null
						&& !this.enemyShipSpecial.isDestroyed()
						&& checkCollision(bullet, this.enemyShipSpecial)) {
					this.score += Score.comboScore(this.enemyShipSpecial.getPointValue(), this.combo);
					this.shipsDestroyed++;
					this.combo++;
					this.hitBullets++;
					if (this.combo > this.maxCombo) this.maxCombo = this.combo;
					this.enemyShipSpecial.destroy(balance);
					this.enemyShipSpecialExplosionCooldown.reset();
					timer.cancel();
					isExecuted = false;

					recyclable.add(bullet);
				}

				if (this.itemManager.getShotNum() == 1 && bullet.getPositionY() < topEnemyY) {
					this.combo = 0;
					isExecuted = true;
				}

				// edit itembox collpase bullet
				Iterator<ItemBox> itemBoxIterator = this.itemBoxes.iterator();
				while (itemBoxIterator.hasNext()) {
					ItemBox itemBox = itemBoxIterator.next();
					if (checkCollision(bullet, itemBox) && !itemBox.isDroppedRightNow()) {
						this.hitBullets++;
						itemBoxIterator.remove();
						recyclable.add(bullet);
						ItemManager.ItemType itemType = itemManager.selectItemType();
						boolean added = itemManager.addItem(itemType);
						if (added) {
							logger.info(itemType + " added to storage.");
						} else {
							logger.info("Storage is full. Item not added.");
						}
					}
				}

				//check the collision between the obstacle and the bullet
				if (!bonusBossLevels.contains(level) && !bossLevels.contains(level)) {
					for (Block block : this.block) {
						if (checkCollision(bullet, block)) {
							recyclable.add(bullet);
							soundManager.playSound(Sound.BULLET_BLOCKING, balance);
							break;
						}
					}
				}
				if (bonusBoss != null && !bonusBoss.isDestroyed() && checkBonusBossCollusion(bonusBoss, bullet)) {
					bonusBoss.HealthManageDestroy();
					timer.cancel();
					isExecuted = false;
					recyclable.add(bullet);
					this.score++;
				}

				if (boss != null && !boss.isDestroyed() && checkFirstBossCollusion(boss,bullet)) {
					boss.HealthManageDestroy();
					hpBarWidth -= widthMinus;
					if (boss.getHealth() <= 0) hpBarWidth = 0;
					timer.cancel();
					isExecuted = false;
					recyclable.add(bullet);
				}
			}
		}

		//check the collision between the obstacle and the enemyship
		Set<Block> removableBlocks = new HashSet<>();
		if (enemyShipFormation != null) {
			for (EnemyShip enemyShip : this.enemyShipFormation) {
				if (enemyShip != null && !enemyShip.isDestroyed() && block != null) {
					for (Block block : block) {
						if (checkCollision(enemyShip, block)) {
							removableBlocks.add(block);
						}
					}
				}
			}
		}

		//check the collision between the obstacle and the physics enemy
		if (physicsEnemyShips != null) {
			for (PhysicsEnemyShip physicsEnemyShip : this.physicsEnemyShips) {
				if (physicsEnemyShip != null && !physicsEnemyShip.isDestroyed() && block != null) {
					for (Block block : block) {
						if (checkCollision(physicsEnemyShip, block)) {
							removableBlocks.add(block);
						}
					}
				}
			}
		}

		if (bossBullets != null && !bossBullets.isEmpty()) {
			for (int i = 0; i < bossBullets.size(); i++) {
				if (bossBullets.get(i).getAttackType() != 3) {
					if (checkBossAttackCollusion(ship, bossBullets.get(i))) {
						this.ship.destroy(balance);
						lvdamage();
						this.logger.info("Hit on player ship, " + this.lives + " lives remaining.");
						bossBullets.remove(bossBullets.get(i));
					}
				} else {
					if (laserCollision()) {
//						this.ship.destroy(balance);
//						lvdamage();
//						this.logger.info("Hit on player ship, " + this.lives + " lives remaining.");
//						bossBullets.remove(bossBullets.get(i));
						lvdamage();
						itemManager.useItem(ItemManager.ItemType.Ghost);
						this.logger.info("Hit on player ship, " + this.lives + " lives remaining.");
					}
				}
			}
		}


		// remove crashed obstacle
		if (!bonusBossLevels.contains(level) && !bossLevels.contains(level)) block.removeAll(removableBlocks);
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
		int centerAX = (int) (a.getPositionX() + a.getWidth() / 2);
		int centerAY = (int) (a.getPositionY() + a.getHeight() / 2);
		int centerBX = (int) (b.getPositionX() + b.getWidth() / 2);
		int centerBY = (int) (b.getPositionY() + b.getHeight() / 2);
		// Calculate maximum distance without collision.
		int maxDistanceX = a.getWidth() / 2 + b.getWidth() / 2;
		int maxDistanceY = a.getHeight() / 2 + b.getHeight() / 2;
		// Calculates distance.
		int distanceX = Math.abs(centerAX - centerBX);
		int distanceY = Math.abs(centerAY - centerBY);

		return distanceX <= maxDistanceX && distanceY <= maxDistanceY;
	}

	private boolean checkBonusBossCollusion(final BonusBoss entity, final Entity bullet) {
		int centerX = this.getWidth() / 2;
		int centerY = this.getHeight() / 2 - 46;

		int radius = entity.getRadius();

		int bulletCenterX = (int) (bullet.getPositionX() + bullet.getWidth() / 2);
		int bulletCenterY = (int) (bullet.getPositionY() + bullet.getHeight() / 2);

		int deltaX = bulletCenterX - centerX;
		int deltaY = bulletCenterY - centerY;

		int distanceSquared = deltaX * deltaX + deltaY * deltaY;

		return distanceSquared <= radius * radius;
	}

	private boolean checkFirstBossCollusion(final Boss entity, final Entity bullet) {
		int bulletCenterX = (int) (bullet.getPositionX() + bullet.getWidth() / 2);
		int bulletCenterY = (int) (bullet.getPositionY() + bullet.getHeight() / 2);

		int relativeX = bulletCenterX - (int) entity.getPositionX();
		int relativeY = bulletCenterY - (int) entity.getPositionY();

		if (relativeX < 0 || relativeX >= entity.getWidth() || relativeY < 0 || relativeY >= entity.getHeight()) {
			return false;
		}

		int pixel = entity.getBossShipImage().getRGB(relativeX, relativeY);
		int alpha = (pixel >> 24) & 0xff;

		return alpha > 0;
	}

	private boolean checkBossAttackCollusion(final Entity player, final BossBullet bullet) {
		if (itemManager.isGhostActive()) return false;
		int bulletStartX = (int) bullet.getPositionX();
		int bulletStartY = (int) bullet.getPositionY();

		int bulletWidth = bullet.getBulletImage().getWidth();
		int bulletHeight = bullet.getBulletImage().getHeight();

		int playerStartX = (int) player.getPositionX();
		int playerStartY = (int) player.getPositionY();
		int playerWidth = player.getWidth();
		int playerHeight = player.getHeight();

		// 총알의 모든 픽셀을 순회
		for (int x = 0; x < bulletWidth; x++) {
			for (int y = 0; y < bulletHeight; y++) {
				int pixel = bullet.getBulletImage().getRGB(x, y);
				int alpha = (pixel >> 24) & 0xff;

				if (alpha == 0) {
					continue; // 투명한 부분은 건너뜀
				}

				int actualX = bulletStartX + x;
				int actualY = bulletStartY + y;

				if (actualX >= playerStartX && actualX < playerStartX + playerWidth &&
						actualY >= playerStartY && actualY < playerStartY + playerHeight) {
					return true; // 충돌 발생
				}
			}
		}

		return false; // 충돌 없음
	}

	public boolean laserCollision() {
		if (itemManager.isGhostActive()) return false;
		if (left && boss.getPositionX() - 50  <= ship.getPositionX() && boss.getPositionX()+4 >= ship.getPositionX()
				&& boss.getPositionY() + laserHeight + 40 >= ship.getPositionY()) return true;
		else if (!left && boss.getPositionX() + boss.getWidth() + 50 >= ship.getPositionX() && boss.getPositionX() + boss.getWidth() - 4 <= ship.getPositionX()
				&& boss.getPositionY() + laserHeight + 40 >= ship.getPositionY()) return true;
		else return false;
	}



	private void bossRandomMove(Boss boss) {
		int currentX = (int) boss.getPositionX() + boss.getBossShipImage().getWidth() / 2;
		int currentY = (int) boss.getPositionY() + boss.getBossShipImage().getHeight() / 2;

		int deltaX = random.nextInt(21) - 10; // -10 ~ 10
		int deltaY = random.nextInt(9) - 4; // -4 ~ 4

		int gotoX = currentX + deltaX; // x축 도착할 곳
		int gotoY = currentY + deltaY; // y축 도착할 곳

	}

	/**
	 * Returns a GameState object representing the status of the game.
	 *
	 * @return Current game state.
	 */
	public final GameState getGameState() {
		return new GameState(this.level, this.score, this.shipType, this.lives,
				this.bulletsShot, this.shipsDestroyed, this.elapsedTime, this.alertMessage, 0, this.maxCombo, this.lapTime, this.tempScore, this.hitBullets);
	}


	/**
	 * Start the action for two player mode
	 *
	 * @return Current game state.
	 */
	@Override
	public final GameState call() {
		run();
		return getGameState();
	}
	//Enemy bullet damage increases depending on stage level
	public void lvdamage(){
		if (!bossLevels.contains(level)) {
			for(int i=0; i<=level/3;i++){
				this.lives--;
			}
		} else {
			this.lives--;
		}
		if(this.lives < 0){
			this.lives = 0;
		}
	}
}