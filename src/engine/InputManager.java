package engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Manages keyboard input for the provided screen.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public final class InputManager implements KeyListener {

	/** Number of recognised keys. */
	private static final int NUM_KEYS = 256;
	/** Array with the keys marked as pressed or not. */
	private static boolean[] keys;
	/** Singleton instance of the class. */
	private static InputManager instance;
	/** Save last pressed key */
	private static int lastKeyPressed;
	/** Indicates if the game is in puzzle mode */
	private static boolean isPuzzleMode = false;

	/**
	 * Private constructor.
	 */
	private InputManager() {
		keys = new boolean[NUM_KEYS];
		lastKeyPressed = -1;
	}

	/**
	 * Returns shared instance of InputManager.
	 * 
	 * @return Shared instance of InputManager.
	 */
	protected static InputManager getInstance() {
		if (instance == null)
			instance = new InputManager();
		return instance;
	}

	/**
	 * Returns true if the provided key is currently pressed.
	 * 
	 * @param keyCode
	 *            Key number to check.
	 * @return Key state.
	 */
	public boolean isKeyDown(final int keyCode) {
		return keys[keyCode];
	}

	/**
	 * return last pressed keycode
	 *
	 * @return last pressed keycode
	 */
	public int getLastKeyPressed() {
		return lastKeyPressed;
	}

	/**
	 * reset last pressed key
	 */
	public void resetLastKeyPressed() {
		lastKeyPressed = -1;
	}

	/**
	 * Set puzzle mode state
	 * @param active True if puzzle mode is active
	 */
	public void setPuzzleMode(boolean active) {
		isPuzzleMode = active;
		if (active) {
			for (int i = 0; i < NUM_KEYS; i++) {
				keys[i] = false;
			}
		}
	}

	/**
	 * Check if a key is valid for puzzle input
	 * @param keyCode Key code to check
	 * @return True if key is valid for puzzle
	 */
	public boolean isPuzzleKey(int keyCode) {
		return keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_DOWN ||
				keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_RIGHT ||
				keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_S ||
				keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_D;
	}

	/**
	 * Changes the state of the key to pressed.
	 *
	 * @param key
	 *            Key pressed.
	 */
	@Override
	public void keyPressed(final KeyEvent key) {
		if (key.getKeyCode() >= 0 && key.getKeyCode() < NUM_KEYS) {
			keys[key.getKeyCode()] = true;
			lastKeyPressed = key.getKeyCode();
			if (isPuzzleMode) {
				Core.getLogger().info("Puzzle mode key pressed: " + key.getKeyCode());
			}
		}
	}
	/**
	 * Changes the state of the key to not pressed.
	 * 
	 * @param key
	 *            Key released.
	 */
	@Override
	public void keyReleased(final KeyEvent key) {
		if (key.getKeyCode() >= 0 && key.getKeyCode() < NUM_KEYS)
			keys[key.getKeyCode()] = false;
	}

	/**
	 * Does nothing.
	 * 
	 * @param key
	 *            Key typed.
	 */
	@Override
	public void keyTyped(final KeyEvent key) {

	}
}