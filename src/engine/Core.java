package engine;

import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import screen.GameScreen;
import screen.HighScoreScreen;
import screen.ScoreScreen;
import screen.Screen;
import screen.TitleScreen;

/**
 * Implements core game logic.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class Core {

	private static Screen currentScreen;
	private static final int width = 448;
	private static final int height = 520;
	private static int fps = 60;

	private static final Logger logger = Logger.getLogger(Core.class
			.getSimpleName());
	private static Handler fileHandler;
	private static ConsoleHandler consoleHandler;

	/**
	 * Test implementation.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			logger.setUseParentHandlers(false);

			fileHandler = new FileHandler("log");
			fileHandler.setFormatter(new MinimalFormatter());

			consoleHandler = new ConsoleHandler();
			consoleHandler.setFormatter(new MinimalFormatter());

			logger.addHandler(fileHandler);
			logger.addHandler(consoleHandler);
			logger.setLevel(Level.ALL);

		} catch (Exception e) {
			// TODO handle exception
			e.printStackTrace();
		}

		int level;
		int score;
		int livesRemaining;
		int bulletsShot;
		int shipsDestroyed;

		int returnCode = 1;
		do {
			level = 1;
			score = 0;
			livesRemaining = 3;
			bulletsShot = 0;
			shipsDestroyed = 0;

			if (currentScreen != null)
				currentScreen.dispose();

			switch (returnCode) {
			case 1:
				// Main menu.
				currentScreen = new TitleScreen(width, height, fps);
				logger.info("Starting " + width + "x" + height
						+ " title screen at " + fps + " fps.");
				currentScreen.initialize();
				returnCode = currentScreen.run();
				logger.info("Closing title screen.");
				break;
			case 2:
				// Game & score.
				do {
					currentScreen = new GameScreen(level, score,
							livesRemaining, width, height, fps);
					logger.info("Starting " + width + "x" + height
							+ " game screen at " + fps + " fps.");
					currentScreen.initialize();
					currentScreen.run();
					logger.info("Closing game screen.");

					score = ((GameScreen) currentScreen).getScore();
					livesRemaining = ((GameScreen) currentScreen).getLives();
					bulletsShot += ((GameScreen) currentScreen)
							.getBulletsShot();
					shipsDestroyed += ((GameScreen) currentScreen)
							.getShipsDestroyed();
					currentScreen.dispose();

					// One extra live every 3 levels.
					if (level % 3 == 0 && livesRemaining < 3)
						livesRemaining++;
					
					level++;
				} while (livesRemaining > 0 && level <= 15);

				logger.info("Starting " + width + "x" + height
						+ " score screen at " + fps + " fps, with a score of "
						+ score + ", " + livesRemaining + " lives remaining, "
						+ bulletsShot + " bullets shot and " + shipsDestroyed
						+ " ships destroyed.");
				currentScreen = new ScoreScreen(width, height, fps, score,
						livesRemaining, bulletsShot, shipsDestroyed);
				currentScreen.initialize();
				returnCode = currentScreen.run();
				logger.info("Closing score screen.");
				break;
			case 3:
				// High scores.
				currentScreen = new HighScoreScreen(width, height, fps);
				logger.info("Starting " + width + "x" + height
						+ " high score screen at " + fps + " fps.");
				currentScreen.initialize();
				returnCode = currentScreen.run();
				logger.info("Closing high score screen.");
				break;
			default:
				break;
			}

		} while (returnCode != 0);

		fileHandler.flush();
		fileHandler.close();
		System.exit(0);
	}

	/**
	 * Controls access to the logger.
	 * 
	 * @return Application logger.
	 */
	public static Logger getLogger() {
		return logger;
	}

	/**
	 * Controls access to the drawing manager.
	 * 
	 * @return Application draw manager.
	 */
	public static DrawManager getDrawManager() {
		return DrawManager.getInstance();
	}

	/**
	 * Controls access to the input manager.
	 * 
	 * @return Application input manager.
	 */
	public static InputManager getInputManager() {
		return InputManager.getInstance();
	}

	/**
	 * Controls access to the file manager.
	 * 
	 * @return Application file manager.
	 */
	public static FileManager getFileManager() {
		return FileManager.getInstance();
	}

	/**
	 * Controls creation of new cooldowns.
	 * 
	 * @param milliseconds
	 *            Duration of the cooldown.
	 * @return A new cooldown.
	 */
	public static Cooldown getCooldown(int milliseconds) {
		return new Cooldown(milliseconds);
	}

	/**
	 * Controls creation of new cooldowns with variance.
	 * 
	 * @param milliseconds
	 *            Duration of the cooldown.
	 * @param variance
	 *            Variation in the cooldown duration.
	 * @return A new cooldown with variance.
	 */
	public static Cooldown getVariableCooldown(int milliseconds, int variance) {
		return new Cooldown(milliseconds, variance);
	}
}