package javagym;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import noedit.Cell;
import noedit.Maze;
import noedit.MazeGenerator;
import noedit.Path;
import noedit.Position;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MarkSolutionTest {

    private double checkMazeSolution(
            @Nonnull Maze maze,
            @Nonnull Position initialPosition
    ) {
        Solution solution = new Solution();
        long t0 = System.nanoTime();
        Path path = solution.solve(maze, initialPosition);
        long duration = System.nanoTime() - t0;
        assertEquals(path.first(), initialPosition,
                "The solution path does not start at the initial position");
        assertTrue(path.isPhysical(),
                "The solution contains impossible moves, like jumping multiple squares, or going back in time.");
        assertEquals(Cell.Exit, maze.get(path.last()),
                "The solution does not solve the maze (it does not end at an exit).");
        assertTrue(path.isSolution(maze),
                "The solution is not valid for the maze — it may cross walls or leave the maze area.");
        return duration / 1e6;
    }

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
