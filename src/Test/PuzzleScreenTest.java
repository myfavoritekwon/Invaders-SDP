package Test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import screen.PuzzleScreen;

import java.util.List;

class PuzzleScreenTest {
    private PuzzleScreen puzzleScreen;

    @BeforeEach
    public void setUp(){
        puzzleScreen = new PuzzleScreen(0);
    }

    //모든 값이 정확하게 들어갔을 때
    @org.junit.jupiter.api.Test
    public void handleInputTrue() {
        List<Integer> Sequence = puzzleScreen.getDirectionSequence();
        boolean Check = false;
        for(int i : Sequence){
            Check = puzzleScreen.handleInput(i);
        }

        Assertions.assertTrue(Check);
    }

    //모든 값이 정확하게 들어가지 않았을 때
    @org.junit.jupiter.api.Test
    public void handleInputFalse() {
        List<Integer> Sequence = puzzleScreen.getDirectionSequence();
        boolean Check = false;
        for(int i : Sequence){
            Check = puzzleScreen.handleInput(0);
        }

        Assertions.assertFalse(Check);
    }
}