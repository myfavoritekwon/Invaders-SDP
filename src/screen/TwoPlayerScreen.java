package screen;

import engine.Cooldown;
import engine.Core;
import engine.GameSettings;
import engine.GameState;
import entity.Ship;
import entity.Wallet;

import java.awt.event.KeyEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
/**
 * Implements the Two player mode screen, where the action happens.
 */
public class TwoPlayerScreen extends Screen {
    /** Thread pool executor **/
    private final ExecutorService executor;
    /** Game difficulty settings each player **/
    private final GameSettings[] gameSettings = new GameSettings[2];
    /** Current game wallet **/
    private final Wallet wallet;

    /** Game states for each player **/
    private final GameState[] gameStates = new GameState[2];

    /** Players game task **/
    private final Future<GameState>[] players = new Future[2];

    /** Player game finished flags **/
    private final boolean[] gameFinished = new boolean[2];

    /** Player 1's number**/
    private final int PLAYER1_NUMBER = 0;
    /** Player 2's number**/
    private final int PLAYER2_NUMBER = 1;

    private final PuzzleScreen[] puzzleScreens = new PuzzleScreen[2];
    private final boolean[] puzzleActive = new boolean[2];
    private final Cooldown[] webCooldowns = new Cooldown[2];

    private final Ship ship;

    /**
     * Constructor, establishes the properties of the screen.
     *
     *
     * @param gameState
     *            Initial game state
     * @param gameSettings
     *            Game settings list.
     * @param width
     *            Screen width.
     * @param height
     *            Screen height.
     * @param fps
     *            Frames per second, frame rate at which the game is run.
     * @param wallet
     *            Wallet for each game.
     */
    public TwoPlayerScreen(final GameState gameState, final GameSettings gameSettings,
                           final int width, final int height, final int fps, Wallet wallet) {
        super(width * 2, height, fps * 2);

        for (int playerNumber = 0; playerNumber < 2; playerNumber++) {
            this.gameSettings[playerNumber] = new GameSettings(gameSettings);
            this.gameStates[playerNumber] = new GameState(gameState);
            gameFinished[playerNumber] = false;
            puzzleActive[playerNumber] = false;
            puzzleScreens[playerNumber] = null;
            webCooldowns[playerNumber] = Core.getCooldown(3000);
            webCooldowns[playerNumber].reset();
        }

        this.wallet = wallet;
        this.ship = null;
        executor = Executors.newFixedThreadPool(2);
        this.returnCode = 1;
    }

    /**
     * Starts the action.
     *
     * @return Next screen code.
     */
    public int run(){
        try {
            runGameScreen(PLAYER1_NUMBER);
            runGameScreen(PLAYER2_NUMBER);
        }
        catch (Exception e) {
            // TODO handle exception
            e.printStackTrace();
        }
        super.run();
        return returnCode;
    }

    /**
     * Draws the elements associated with the screen.
     */
    private void draw() {
        drawManager.initDrawing(this);

        for (int i = 0; i < 2; i++) {
            if (puzzleActive[i] && puzzleScreens[i] != null) {
                drawManager.drawPuzzle(this,
                        puzzleScreens[i].getDirectionSequence(),
                        puzzleScreens[i].getPlayerInput(),
                        i, i,
                        this.ship != null ? this.ship.getCollisionX() : this.width / 4,
                        this.ship != null ? this.ship.getCollisionY() : this.height / 2);
            }
        }

        drawManager.mergeDrawing(this);
        drawManager.drawVerticalLine(this);
        drawManager.completeDrawing(this);
    }

    /**
     * Updates the elements on screen and checks for events.
     */
    protected final void update() {
        try {
            for (int i = 0; i < 2; i++) {
                if (puzzleActive[i] && puzzleScreens[i] != null) {
                    updatePuzzle(i);
                }
            }

            if (players[PLAYER1_NUMBER].isDone()) {
                gameStates[PLAYER1_NUMBER] = players[PLAYER1_NUMBER].get();
                gameStates[PLAYER1_NUMBER] = new GameState(gameStates[PLAYER1_NUMBER], gameStates[PLAYER1_NUMBER].getLevel() + 1);
                runGameScreen(PLAYER1_NUMBER);
            }
            if (players[PLAYER2_NUMBER].isDone()) {
                gameStates[PLAYER2_NUMBER] = players[PLAYER2_NUMBER].get();
                gameStates[PLAYER2_NUMBER] = new GameState(gameStates[PLAYER2_NUMBER], gameStates[PLAYER2_NUMBER].getLevel() + 1);
                runGameScreen(PLAYER2_NUMBER);
            }

            if (gameFinished[PLAYER1_NUMBER] && gameFinished[PLAYER2_NUMBER]) {
                isRunning = false;
                executor.shutdown();
            }

            draw();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updatePuzzle(final int playerNumber) {
        if (!webCooldowns[playerNumber].checkFinished()) {
            return;
        }

        if (puzzleScreens[playerNumber] == null && !puzzleActive[playerNumber]) {
            resetPuzzle(playerNumber);
            puzzleScreens[playerNumber] = new PuzzleScreen(playerNumber);
            puzzleActive[playerNumber] = true;
            return;
        }

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

        if (keyPressed && puzzleScreens[playerNumber] != null) {
            int keyCode = inputManager.getLastKeyPressed();
            if (puzzleScreens[playerNumber].handleInput(keyCode)) {
                completePuzzle(playerNumber);
            } else {
                resetPuzzle(playerNumber);
            }
            inputManager.resetLastKeyPressed();
        }
    }

    private void completePuzzle(int playerNumber) {
        puzzleScreens[playerNumber] = null;
        puzzleActive[playerNumber] = false;
        webCooldowns[playerNumber].reset();
    }

    private void resetPuzzle(int playerNumber) {
        if (puzzleScreens[playerNumber] == null) {
            puzzleScreens[playerNumber] = new PuzzleScreen(playerNumber);
        } else {
            puzzleScreens[playerNumber].reset();
        }
        puzzleActive[playerNumber] = true;
    }

    /**
     * Progression logic each games.
     */
    private void runGameScreen(int playerNumber){
        GameState gameState = gameStates[playerNumber];

        if (gameState.getLivesRemaining() > 0) {
            boolean bonusLife = gameState.getLevel()
                    % Core.EXTRA_LIFE_FRECUENCY == 0
                    && gameState.getLivesRemaining() < Core.MAX_LIVES;
            logger.info("difficulty is " + Core.getLevelSetting());
            gameSettings[playerNumber] = gameSettings[playerNumber].LevelSettings(
                    gameSettings[playerNumber].getFormationWidth(),
                    gameSettings[playerNumber].getFormationHeight(),
                    gameSettings[playerNumber].getBaseSpeed(),
                    gameSettings[playerNumber].getShootingFrecuency(),
                    gameState.getLevel(),
                    Core.getLevelSetting()
            );
            GameScreen gameScreen = new GameScreen(gameState, gameSettings[playerNumber],
                    bonusLife, width / 2, height, fps / 2, wallet, playerNumber);
            gameScreen.initialize();
            players[playerNumber] = executor.submit(gameScreen);
        }
        else gameFinished[playerNumber] = true;
    }

    public GameState getWinnerGameState() {
        return gameStates[getWinnerNumber() - 1];
    }

    public int getWinnerNumber() {
        return ((gameStates[PLAYER1_NUMBER].getScore() >= gameStates[PLAYER2_NUMBER].getScore()) ? PLAYER1_NUMBER : PLAYER2_NUMBER) + 1;
    }
}