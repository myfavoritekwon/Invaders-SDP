package screen;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import engine.*;
import entity.Wallet;

/**
 * Implements the score screen.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class ScoreScreen extends Screen {

	/** Milliseconds between changes in user selection. */
	private static final int SELECTION_TIME = 200;
	/** Maximum number of high scores. */
	private static final int MAX_HIGH_SCORE_NUM = 3;
	/** Code of first mayus character. */
	private static final int FIRST_CHAR = 65;
	/** Code of last mayus character. */
	private static final int LAST_CHAR = 90;

	/** Singleton instance of SoundManager */
	private final SoundManager soundManager = SoundManager.getInstance();


	/** Current score. */
	private int score;
	/** Player lives left. */
	private int livesRemaining;
	/** Total bullets shot by the player. */
	private int bulletsShot;
	/** Total ships destroyed by the player. */
	private int shipsDestroyed;
	/** List of past high scores. */
	private List<Score> highScores;
	/** Checks if current score is a new high score. */
	private double accuracy;
	/** Checks if current score is a new high score. */
	private boolean isNewRecord;
	/** Player name for record input. */
	private char[] name;
	/** Character of players name selected for change. */
	private int nameCharSelected;
	/** Time between changes in user selection. */
	private Cooldown selectionCooldown;
	/** Number of coins earned in the game */
	private int coinsEarned;
	//선택
	private int menuOptionSelected = 0;

	// Set ratios for each coin_lv - placed in an array in the order of lv1, lv2, lv3, lv4, and will be used accordingly,
	// e.g., lv1; score 100 * 0.1
	private static final double[] COIN_RATIOS = {0.1, 0.13, 0.16, 0.19};

	/**
	 * Constructor, establishes the properties of the screen.
	 *
	 * @param width
	 *            Screen width.
	 * @param height
	 *            Screen height.
	 * @param fps
	 *            Frames per second, frame rate at which the game is run.
	 * @param gameState
	 *            Current game state.
	 */
	public ScoreScreen(final int width, final int height, final int fps,
			final GameState gameState, final Wallet wallet, final AchievementManager achievementManager) {

		super(width, height, fps);

		//record
		this.isNewRecord = false;
		this.name = "AAA".toCharArray();
		this.nameCharSelected = 0;
		this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
		this.selectionCooldown.reset();

		this.score = gameState.getScore();
		this.livesRemaining = gameState.getLivesRemaining();
		this.bulletsShot = gameState.getBulletsShot();
		this.shipsDestroyed = gameState.getShipsDestroyed();

		// Get the user's coin_lv
		int coin_lv = wallet.getCoin_lv();

		// Apply different ratios based on coin_lv
		double coin_ratio = COIN_RATIOS[coin_lv-1];

		// Adjust coin earning ratios based on the game level upgrade stage score
		// Since coins are in integer units, round the decimal points and convert to int
		this.coinsEarned = (int)Math.round(gameState.getScore() * coin_ratio);
		this.coinsEarned += achievementManager.getAchievementReward();

		// deposit the earned coins to wallet
		this.accuracy = gameState.getAccuracy();
		wallet.deposit(coinsEarned);

		soundManager.loopSound(Sound.BGM_GAMEOVER);

		try {
			this.highScores = Core.getFileManager().loadHighScores();
			if (highScores.size() < MAX_HIGH_SCORE_NUM
					|| highScores.get(highScores.size() - 1).getScore()
					< this.score)
				this.isNewRecord = true;
		} catch (IOException e) {
			logger.warning("Couldn't load high scores!");
		}
	}

	/**
	 * Starts the action.
	 *
	 * @return Next screen code.
	 */
	public final int run() {
		super.run();

		return this.returnCode;
	}

	/**
	 * Updates the elements on screen and checks for events.
	 */
	protected final void update() {
		super.update();

		draw();

		// 입력이 가능할 때 실행
		if (this.inputDelay.checkFinished() && this.selectionCooldown.checkFinished()) {

			if (this.isNewRecord) {
				// 최고 기록을 세웠을 때 닉네임 편집
				if (inputManager.isKeyDown(KeyEvent.VK_RIGHT)) {
					// 선택된 문자를 오른쪽으로 이동
					this.nameCharSelected = (this.nameCharSelected + 1) % this.name.length;
					this.selectionCooldown.reset();
				} else if (inputManager.isKeyDown(KeyEvent.VK_LEFT)) {
					// 선택된 문자를 왼쪽으로 이동
					this.nameCharSelected = (this.nameCharSelected - 1 + this.name.length) % this.name.length;
					this.selectionCooldown.reset();
				}

				// 선택된 문자의 값을 위/아래로 변경
				if (inputManager.isKeyDown(KeyEvent.VK_UP)) {
					this.name[this.nameCharSelected] =
							(char) (this.name[this.nameCharSelected] == LAST_CHAR ? FIRST_CHAR : this.name[this.nameCharSelected] + 1);
					this.selectionCooldown.reset();
				} else if (inputManager.isKeyDown(KeyEvent.VK_DOWN)) {
					this.name[this.nameCharSelected] =
							(char) (this.name[this.nameCharSelected] == FIRST_CHAR ? LAST_CHAR : this.name[this.nameCharSelected] - 1);
					this.selectionCooldown.reset();
				}

				// 스페이스 바를 눌러 편집 완료하고 메뉴로 이동
				if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
					this.isNewRecord = false; // 닉네임 입력을 완료하고 일반 메뉴 모드로 전환
					this.selectionCooldown.reset();
					soundManager.playSound(Sound.MENU_CLICK);
				}

			} else {
				// 메뉴 선택 전환 (위/아래 방향 키로 선택)
				if (inputManager.isKeyDown(KeyEvent.VK_UP)) {
					// 선택된 행을 위로 이동 (총 두 가지 메뉴 선택 가능: Continue, Exit)
					this.menuOptionSelected = (this.menuOptionSelected - 1 + 2) % 2;
					this.selectionCooldown.reset();
					soundManager.playSound(Sound.MENU_MOVE);
				} else if (inputManager.isKeyDown(KeyEvent.VK_DOWN)) {
					// 선택된 행을 아래로 이동
					this.menuOptionSelected = (this.menuOptionSelected + 1) % 2;
					this.selectionCooldown.reset();
					soundManager.playSound(Sound.MENU_MOVE);
				}

				// 현재 선택된 메뉴 처리
				if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
					if (this.menuOptionSelected == 0) { // Continue 선택
						// 스페이스 바를 눌렀을 때 게임을 다시 시작
						this.returnCode = 2;
						this.isRunning = false;
						soundManager.stopSound(Sound.BGM_GAMEOVER);
						soundManager.playSound(Sound.MENU_CLICK);
						saveScore(); // 새로운 최고 점수일 경우 기록 저장
					} else if (this.menuOptionSelected == 1) { // Exit 선택
						// 스페이스 바를 눌렀을 때 메인 메뉴로 돌아가기
						this.returnCode = 1; // 메인 메뉴로 이동
						this.isRunning = false;
						soundManager.stopSound(Sound.BGM_GAMEOVER);
						soundManager.playSound(Sound.MENU_BACK);
						saveScore(); // 새로운 최고 점수일 경우 기록 저장
					}
				}
			}
		}
	}


	/**
	 * Saves the score as a high score.
	 */
	private void saveScore() {
		highScores.add(new Score(new String(this.name), score));
		Collections.sort(highScores);
		if (highScores.size() > MAX_HIGH_SCORE_NUM)
			highScores.remove(highScores.size() - 1);

		try {
			Core.getFileManager().saveHighScores(highScores);
		} catch (IOException e) {
			logger.warning("Couldn't load high scores!");
		}
	}

	/**
	 * Draws the elements associated with the screen.
	 */
	private void draw() {
		drawManager.initDrawing(this);

		drawManager.drawGameOver(this, this.inputDelay.checkFinished(),
				this.isNewRecord,this.menuOptionSelected);
		drawManager.drawResults(this, this.score, this.livesRemaining,
				this.shipsDestroyed, this.accuracy, this.isNewRecord, this.coinsEarned);

		if (this.isNewRecord)
			drawManager.drawNameInput(this, this.name, this.nameCharSelected);

		drawManager.completeDrawing(this);
	}
}

