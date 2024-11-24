package screen;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import engine.Core;

public class PuzzleScreen {
    private List<Integer> directionSequence;
    private List<Integer> playerInput;
    private boolean isCompleted;
    private final int playerNumber;
    private static final Random random = new Random();

    public PuzzleScreen(int playerNumber) {
        this.playerNumber = playerNumber;
        this.directionSequence = new ArrayList<>();
        this.playerInput = new ArrayList<>();
        this.isCompleted = false;
        generateSequence();
    }

    private void generateSequence() {
        directionSequence.clear();
        if (playerNumber == 0) {
            int[] possibleKeys = {
                    KeyEvent.VK_W,
                    KeyEvent.VK_S,
                    KeyEvent.VK_A,
                    KeyEvent.VK_D
            };
            for (int i = 0; i < Core.PuzzleSettings.SEQUENCE_LENGTH; i++) {
                int key = possibleKeys[random.nextInt(possibleKeys.length)];
                directionSequence.add(key);
            }
        } else {
            int[] possibleKeys = {
                    KeyEvent.VK_UP,
                    KeyEvent.VK_DOWN,
                    KeyEvent.VK_LEFT,
                    KeyEvent.VK_RIGHT
            };
            for (int i = 0; i < Core.PuzzleSettings.SEQUENCE_LENGTH; i++) {
                int key = possibleKeys[random.nextInt(possibleKeys.length)];
                directionSequence.add(key);
            }
        }
    }

    public boolean handleInput(int KeyCode) {
        if (isCompleted) {
            playerInput.clear();
            return true;
        }

        if (isValidKey(KeyCode)) {
            playerInput.add(KeyCode);

            if (!checkSequence()) {
                playerInput.clear();
                return false;
            }

            if (playerInput.size() == directionSequence.size()) {
                isCompleted = true;
                return true;
            }
        }
        return false;
    }

    private boolean isValidKey(int KeyCode) {
        if (playerNumber == 0) {
            return KeyCode == KeyEvent.VK_W ||
                    KeyCode == KeyEvent.VK_A ||
                    KeyCode == KeyEvent.VK_S ||
                    KeyCode == KeyEvent.VK_D;
        } else {
            return KeyCode == KeyEvent.VK_UP ||
                    KeyCode == KeyEvent.VK_DOWN ||
                    KeyCode == KeyEvent.VK_LEFT ||
                    KeyCode == KeyEvent.VK_RIGHT;
        }
    }

    private boolean checkSequence() {
        int index = playerInput.size() - 1;
        return playerInput.get(index).equals(directionSequence.get(index));
    }

    public void reset() {
        playerInput.clear();
        generateSequence();
    }

    public List<Integer> getDirectionSequence() { return directionSequence; }

    public List<Integer> getPlayerInput() { return playerInput; }
}