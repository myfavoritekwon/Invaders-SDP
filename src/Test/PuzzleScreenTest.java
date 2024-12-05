package Test;

import org.junit.Before;
import org.junit.Test;
import screen.PuzzleScreen;

import java.util.List;

import static org.junit.Assert.*;

public class PuzzleScreenTest {
    private PuzzleScreen puzzleScreen;

    @Before
    public void setUp() throws Exception {
        puzzleScreen = new PuzzleScreen(0);
    }

    //모든 값이 정확하게 들어갔을 때
    @Test
    public void handleInputTrue() {
        List<Integer> Sequence = puzzleScreen.getDirectionSequence();
        boolean Check = false;
        for(int i : Sequence){
            Check = puzzleScreen.handleInput(i);
        }

        assertTrue(Check);
    }

    //모든 값이 정확하게 들어가지 않았을 때
    @Test
    public void handleInputFalse() {
        List<Integer> Sequence = puzzleScreen.getDirectionSequence();
        boolean Check = false;
        for(int i : Sequence){
            Check = puzzleScreen.handleInput(0);
        }

        assertFalse(Check);
    }

    @Test
    public void reset() {
    }

    @Test
    public void getDirectionSequence() {
    }

    @Test
    public void getPlayerInput() {
    }
}