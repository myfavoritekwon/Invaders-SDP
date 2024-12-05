package Test;

import engine.Core;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import screen.PuzzleScreen;

import java.awt.event.KeyEvent;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PuzzleScreenTest {
    private PuzzleScreen puzzleScreen;

    @BeforeEach
    // 플레이어 넘버 0으로 설정
    void setUp() {
        puzzleScreen = new PuzzleScreen(0);
    }

    // 퍼즐 초기화 상태 확인
    @Test
    void initializationTest() {
        assertNotNull(puzzleScreen.getDirectionSequence());
        assertNotNull(puzzleScreen.getPlayerInput());
        assertTrue(puzzleScreen.getPlayerInput().isEmpty());
    }

    // 시퀀스 길이 확인
    @Test
    void sequenceLengthTest() {
        assertEquals(Core.PuzzleSettings.SEQUENCE_LENGTH,
                puzzleScreen.getDirectionSequence().size());
    }

    // 플레이어 입력 테스트 (WASD)
    @Test
    void player1InputTest() {
        List<Integer> sequence = puzzleScreen.getDirectionSequence();
        assertFalse(puzzleScreen.handleInput(sequence.get(0)),
                "시퀀스가 완성되지 않았으므로 false를 반환해야 함");
        assertEquals(1, puzzleScreen.getPlayerInput().size(),
                "올바른 입력이므로 playerInput에 추가되어야 함");
    }

    // 플레이어 입력 테스트 (방향키)
    @Test
    void player2InputTest() {
        puzzleScreen = new PuzzleScreen(1);
        List<Integer> sequence = puzzleScreen.getDirectionSequence();
        assertFalse(puzzleScreen.handleInput(sequence.get(0)),
                "시퀀스가 완성되지 않았으므로 false를 반환해야 함");
        assertEquals(1, puzzleScreen.getPlayerInput().size(),
                "올바른 입력이므로 playerInput에 추가되어야 함");
    }

    // 잘못된 순서로 입력 시 초기화 테스트
    @Test
    void wrongSequenceTest() {
        List<Integer> sequence = puzzleScreen.getDirectionSequence();
        puzzleScreen.handleInput(sequence.get(0));
        assertEquals(1, puzzleScreen.getPlayerInput().size());

        int wrongKey = (sequence.get(1) == KeyEvent.VK_W) ? KeyEvent.VK_S : KeyEvent.VK_W;
        assertFalse(puzzleScreen.handleInput(wrongKey),
                "잘못된 입력이므로 false를 반환해야 함");
        assertTrue(puzzleScreen.getPlayerInput().isEmpty(),
                "잘못된 입력 후 playerInput이 초기화되어야 함");
    }

    // 올바른 시퀀스 입력 시 완료 테스트
    @Test
    void completeCorrectSequenceTest() {
        List<Integer> sequence = puzzleScreen.getDirectionSequence();
        for (int i = 0; i < sequence.size() - 1; i++) {
            assertFalse(puzzleScreen.handleInput(sequence.get(i)),
                    "시퀀스 완성 전에는 false를 반환해야 함");
            assertEquals(i + 1, puzzleScreen.getPlayerInput().size(),
                    "올바른 입력은 playerInput에 추가되어야 함");
        }

        assertTrue(puzzleScreen.handleInput(sequence.get(sequence.size() - 1)),
                "시퀀스 완성 시 true를 반환해야 함");
    }

    // 퍼즐 리셋 테스트
    void resetTest() {
        // 초기 상태 저장
        List<Integer> originalSequence = puzzleScreen.getDirectionSequence();
        puzzleScreen.handleInput(originalSequence.get(0));
        assertFalse(puzzleScreen.getPlayerInput().isEmpty());

        puzzleScreen.reset();
        assertTrue(puzzleScreen.getPlayerInput().isEmpty(),
                "리셋 후 playerInput이 비어있어야 함");

        List<Integer> newSequence = puzzleScreen.getDirectionSequence();
        assertEquals(Core.PuzzleSettings.SEQUENCE_LENGTH, newSequence.size(),
                "새로운 시퀀스의 길이가 올바른지 확인");

        for (Integer key : newSequence) {
            assertTrue(
                    key == KeyEvent.VK_W || key == KeyEvent.VK_A ||
                            key == KeyEvent.VK_S || key == KeyEvent.VK_D,
                    "모든 키가 유효한 범위 내에 있어야 함"
            );
        }
    }
}