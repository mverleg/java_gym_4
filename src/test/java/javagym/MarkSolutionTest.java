package javagym;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import noedit.Maze;
import noedit.MazeGenerator;
import noedit.Path;
import noedit.Position;

import static javagym.SolutionTest.checkMazeSolution;

public class MarkSolutionTest {

    @Test
    void testShowPath() {
        // There are no asserts, you get this one for free if you don't throw errors.
        // It will plot the maze including the path that your algorithm took.
        Pair<Maze, Position> puzzle = MazeGenerator.generateOpen(55_555_555, 6, 100, 0.10);
        Maze maze = puzzle.getLeft();
        Position initialPosition = puzzle.getRight();
        Solution solution = new Solution();
        Path path = solution.solve(maze, initialPosition);
        String text = path.ontoMazeAsText(maze);
        System.out.println(text);
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
        double openTotal = 0.0;
        for (int i = 0; i < 100; i++) {
            Pair<Maze, Position> puzzle = MazeGenerator.generateOpen(147_258_369 + 111 * i, 50, 150, 0.10);
            openTotal += checkMazeSolution(puzzle.getLeft(), puzzle.getRight());
        }
        System.out.println(String.format("open took %.3f ms", openTotal));
        System.out.println(String.format("total took %.3f ms", perfectTotal + porousTotal + openTotal));
    }

    @Test
    void testBenchmark() {
        // Hint: Do not run in debug mode!
        double perfectTotal = 0.0;
        for (int i = 0; i < 50; i++) {
            Pair<Maze, Position> puzzle = MazeGenerator.generate(246_800_000 + 111 * i, 6, 150, 0.0, 1);
            perfectTotal += checkMazeSolution(puzzle.getLeft(), puzzle.getRight());
        }
        System.out.println(String.format("perfect took %.3f ms", perfectTotal));
        double porousTotal = 0.0;
        for (int i = 0; i < 100; i++) {
            Pair<Maze, Position> puzzle = MazeGenerator.generate(135_780_000 + 111 * i, 10, 150, 0.10, 5);
            porousTotal += checkMazeSolution(puzzle.getLeft(), puzzle.getRight());
        }
        System.out.println(String.format("porous took %.3f ms", porousTotal));
        System.out.println(String.format("total took %.3f ms", perfectTotal + porousTotal));
    }
}
