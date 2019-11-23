package javagym;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import noedit.Maze;
import noedit.MazeGenerator;
import noedit.Position;

import static javagym.SolutionTest.checkMazeSolution;

public class MarkSolutionTest {

    @Test
    void testPerformanceMark() {
        // Hint: Do not run in debug mode!
        double perfectTotal = 0.0;
        for (int i = 0; i < 500; i++) {
            Pair<Maze, Position> puzzle = MazeGenerator.generate(246_800_000 + 111 * i, 6, 150, 0.0, 1);
            perfectTotal += checkMazeSolution(puzzle.getLeft(), puzzle.getRight());
        }
        System.out.println(String.format("perfect took %.3f ms", perfectTotal));
        double porousTotal = 0.0;
        for (int i = 0; i < 1000; i++) {
            Pair<Maze, Position> puzzle = MazeGenerator.generate(135_780_000 + 111 * i, 10, 150, 0.10, 5);
            porousTotal += checkMazeSolution(puzzle.getLeft(), puzzle.getRight());
        }
        System.out.println(String.format("porous took %.3f ms", porousTotal));
        System.out.println(String.format("total took %.3f ms", perfectTotal + porousTotal));
    }
}
