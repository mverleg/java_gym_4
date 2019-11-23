package javagym;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import noedit.Cell;
import noedit.Maze;
import noedit.MazeGenerator;
import noedit.Path;
import noedit.Position;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SolutionTest {

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
    void testShowPath() {
        // There are no asserts, you get this one for free if you don't throw errors.
        // It will plot the maze including the path that your algorithm took.
        Pair<Maze, Position> puzzle = MazeGenerator.generate(55_555_555, 3, 25, 0.05, 2);
        Maze maze = puzzle.getLeft();
        Position initialPosition = puzzle.getRight();
        Solution solution = new Solution();
        Path path = solution.solve(maze, initialPosition);
        String text = path.ontoMazeAsText(maze);
        System.out.println(text);
    }

    @Test
    void testCompletesWithoutErrors() {
        // Hint: Just step to the left.
        checkMazeSolution(
                Maze.fromString("# "),
                Position.initial(1, 0)
        );
    }

    @Test
    void testStartOnExit() {
        // Hint: Maybe the only exit is under your feet!
        checkMazeSolution(
                Maze.fromStrings(new String[]{
                        "   \n" +
                        " # \n" +
                        "   \n",
                        // next step
                        "   \n" +
                        "   \n" +
                        "   \n",
                }),
                Position.initial(1, 1)
        );
    }

    @Test
    void testLongCorridor() {
        // Hint: Just keep walking right.
        checkMazeSolution(
                Maze.fromString("                          #"),
                Position.initial(0, 0)
        );
    }

    @Test
    void testSmallConstant() {
        checkMazeSolution(
                Maze.fromString(
                        "   \n" +
                        " * \n" +
                        " * \n" +
                        "#* \n"
                ),
                Position.initial(2, 3)
        );
    }

    @Test
    void testSmallTwoSteps() {
        checkMazeSolution(
                Maze.fromStrings(new String[]{
                        "*  \n" +
                                "** \n" +
                                "** \n" +
                                "#* \n",
                        // next step
                        "  *\n" +
                                " **\n" +
                                " **\n" +
                                "#**\n",
                }),
                Position.initial(2, 3)
        );
    }

    @Test
    void testStaticRoom() {
        // Hint: Walk to the exit, just a few small obstacles.
        checkMazeSolution(
                Maze.fromStrings(new String[]{
                        " *                        \n" +
                        "                 *        \n" +
                        "                          \n" +
                        "                       *  \n" +
                        "       *                  \n" +
                        "               *          \n" +
                        "       *                 #",
                }),
                Position.initial(0, 0)
        );
    }

    @Test
    void testLongWayToExit() {
        // Hint: Walk to the exit, just a few small obstacles.
        checkMazeSolution(
                Maze.fromStrings(new String[]{
                        " *   *             \n" +
                        " * * * *********** \n" +
                        " * * * *           \n" +
                        "   * * * **********\n" +
                        " * * * *           \n" +
                        " * *   *********** \n" +
                        " * *   *#          ",
                }),
                Position.initial(0, 0)
        );
    }

    @Test
    void testSomeExitsUnreachable() {
        // Hint: Walk to the exit, just a few small obstacles.
        checkMazeSolution(
                Maze.fromStrings(new String[]{
                        "                     \n" +
                        "**** * * * * * * * **\n" +
                        "** *** * * * * * *** \n" +
                        "** * *** * * * *** * \n" +
                        "** * * *** * *** * * \n" +
                        "** * * * * *** * * * \n" +
                        "#*#*#*#*#*#*#*#*#*#*#",
                }),
                Position.initial(0, 0)
        );
    }

    @Test
    void testNearbyExitsUnreachable() {
        // Hint: Walk to the exit, just a few small obstacles.
        checkMazeSolution(
                Maze.fromStrings(new String[]{
                        " *# #*   *   * \n" +
                        " ***** * * * * \n" +
                        " *   * * * * * \n" +
                        " * * * * * * * \n" +
                        " * * * * * * * \n" +
                        "   * * * * * *#\n" +
                        "**** * * * * * \n" +
                        "  #*   *   *   ",
                }),
                Position.initial(0, 0)
        );
    }

    @Test
    void testStandaloneExit() {
        // Hint: Don't just follow the walls.
        checkMazeSolution(
                Maze.fromStrings(new String[]{
                        "     \n" +
                        "     \n" +
                        "  #  \n" +
                        "     \n" +
                        "     "
                }),
                Position.initial(0, 0)
        );
    }

    @Test
    void testExitAppearsLater() {
        // Hint: Just stand still until the exit appears under you.
        checkMazeSolution(
                Maze.fromStrings(new String[]{
                        " ",
                        " ",
                        " ",
                        " ",
                        " ",
                        " ",
                        " ",
                        "#",
                }),
                Position.initial(0, 0)
        );
    }

    @Test
    void testSlidingCorridor() {
        // Hint: Take one step per turn, and don't try to escape the sliding window.
        checkMazeSolution(
                Maze.fromStrings(new String[]{
                        "  *                       #",
                        "*  *                      #",
                        " *  *                     #",
                        "  *  *                    #",
                        "   *  *                   #",
                        "    *  *                  #",
                        "     *  *                 #",
                        "      *  *                #",
                        "       *  *               #",
                        "        *  *              #",
                        "         *  *             #",
                        "          *  *            #",
                        "           *  *           #",
                        "            *  *          #",
                        "             *  *         #",
                        "              *  *        #",
                        "               *  *       #",
                        "                *  *      #",
                        "                 *  *     #",
                        "                  *  *    #",
                        "                   *  *   #",
                        "                    *  *  #",
                        "                     *  * #",
                        "                      *  *#",
                        "                         *#",
                        "                         *#",
                        "                         *#",
                        "                      *  *#",
                        "                     *  * #",
                        "                    *  *  #",
                        "                   *  *   #",
                        "                  *  *    #",
                        "                 *  *     #",
                        "                *  *      #",
                        "               *  *       #",
                        "              *  *        #",
                        "               *  *       #",
                        "                *  *      #",
                        "                 *  *     #",
                        "                  *  *    #",
                        "                   *  *   #",
                        "                    *  *  #",
                        "                     *  * #",
                        "                      *  *#",
                        "                       *  #",
                }),
                Position.initial(0, 0)
        );
    }

    @Test
    void testExitMovedAround() {
        // Hint: Don't rely on the exit position, look into the future.
        checkMazeSolution(
                Maze.fromStrings(new String[]{
                        "     \n" +
                        "**** \n" +
                        "     \n" +
                        " ****\n" +
                        "     ",
                        // next step
                        " #   \n" +
                        "**** \n" +
                        "     \n" +
                        " ****\n" +
                        "     ",
                        // next step
                        "     \n" +
                        "**** \n" +
                        "     \n" +
                        " ****\n" +
                        "  #  ",
                }),
                Position.initial(4, 4)
        );
    }

    @Test
    void testBlinkingWalls() {
        // Hint: Don't rely on the exit position, look into the future.
        checkMazeSolution(
                Maze.fromStrings(new String[]{
                        " *   *   \n" +
                        " * * * * \n" +
                        " * * * * \n" +
                        " * * * * \n" +
                        " * * * * \n" +
                        "   *   * \n",
                        // next step
                        "*********\n" +
                        "*********\n" +
                        "*********\n" +
                        "*********\n" +
                        "*********\n" +
                        "******** \n",
                        // next step
                        " *   *   \n" +
                        " * * * * \n" +
                        " * * * * \n" +
                        " * * * * \n" +
                        " * * * * \n" +
                        "   *   * \n",
                        // next step
                        " ********\n" +
                        "*********\n" +
                        "*********\n" +
                        "*********\n" +
                        "*********\n" +
                        "*********\n",
                        // next step
                        " *   *   \n" +
                        " * * * * \n" +
                        " * * * * \n" +
                        " * * * * \n" +
                        " * * * * \n" +
                        "   *   * \n",
                        // next step
                        "*********\n" +
                        "*********\n" +
                        "*********\n" +
                        "*********\n" +
                        "*********\n" +
                        "***** ***\n",
                        // next step
                        "#*   *   \n" +
                        " * * * * \n" +
                        " * * * * \n" +
                        " * * * * \n" +
                        " * * * * \n" +
                        "   *   * \n",
                }),
                Position.initial(0, 0)
        );
    }

    @Test
    void testExitOnlyInNonlastStep() {
        // Hint: Don't only look for exits at the end of time.
        checkMazeSolution(
                Maze.fromStrings(new String[]{
                        "     \n" +
                        "*****\n" +
                        "     \n" +
                        " ****\n" +
                        "     ",
                        // next step
                        "     \n" +
                        "**** \n" +
                        "     \n" +
                        "*****\n" +
                        "     ",
                        // next step
                        "#    \n" +
                        "**** \n" +
                        "     \n" +
                        " ****\n" +
                        "     ",
                        // next step
                        "     \n" +
                        "**** \n" +
                        "     \n" +
                        " ****\n" +
                        "     ",
                }),
                Position.initial(4, 4)
        );
    }

    @Test
    void testStartNotInFirstStep() {
        // Hint: Sometimes you don't start at the first timestep. This makes earlier steps useless, but it happens.
        checkMazeSolution(
                Maze.fromStrings(new String[]{
                        "#   #\n" +
                        "     \n" +
                        "  #  \n" +
                        "     \n" +
                        "#   #\n",
                        // next step
                        "     \n" +
                        " *** \n" +
                        " * * \n" +
                        " ****\n" +
                        "     \n",
                        // next step
                        "     \n" +
                        " *** \n" +
                        " * * \n" +
                        " ****\n" +
                        "   # ",
                }),
                Position.at(1, 4, 0)
        );
    }

    @Test
    void testBigMill() {
        // Hint: you have to run a circle around the center to reach the end.
        checkMazeSolution(
                Maze.fromStrings(new String[]{
                        "    *  \n" +
                        "    *  \n" +
                        "*****  \n" +
                        "  * *  \n" +
                        "  *****\n" +
                        "  *    \n" +
                        "  *    \n",
                        // next step
                        "       \n" +
                        "       \n" +
                        "*****  \n" +
                        "  * *  \n" +
                        "  *****\n" +
                        "  *    \n" +
                        "  *    \n",
                        // next step
                        "    *  \n" +
                        "    *  \n" +
                        "*****  \n" +
                        "  * *  \n" +
                        "  ***  \n" +
                        "  *    \n" +
                        "  *    \n",
                        // next step
                        "    *  \n" +
                        "    *  \n" +
                        "*****  \n" +
                        "  * *  \n" +
                        "  *****\n" +
                        "       \n" +
                        "       \n",
                        // next step (clear out starting room)
                        "*****  \n" +
                        "*****  \n" +
                        "*****  \n" +
                        "  * *  \n" +
                        "  *****\n" +
                        "  *    \n" +
                        "  *    \n",
                        // next step
                        "    *  \n" +
                        "    *  \n" +
                        "  ***  \n" +
                        "  * *  \n" +
                        "  *****\n" +
                        "  *    \n" +
                        "  *    \n",
                        // next step
                        "   #*  \n" +
                        "   #*  \n" +
                        "*****  \n" +
                        "  * *  \n" +
                        "  *****\n" +
                        "  *    \n" +
                        "  *    \n",
                }),
                Position.initial(0, 0)
        );
    }

    @Test
    void testTMP() { // from testBigMill
        Maze maze = Maze.fromStrings(new String[]{
                "    *  \n" +
                "    *  \n" +
                "*****  \n" +
                "  * *  \n" +
                "  *****\n" +
                "  *    \n" +
                "  *    \n",
                // next step
                "       \n" +
                "       \n" +
                "*****  \n" +
                "  * *  \n" +
                "  *****\n" +
                "  *    \n" +
                "  *    \n",
                // next step
                "    *  \n" +
                "    *  \n" +
                "*****  \n" +
                "  * *  \n" +
                "  ***  \n" +
                "  *    \n" +
                "  *#   \n",
//                // next step
//                "    *  \n" +
//                "    *  \n" +
//                "*****  \n" +
//                "  * *  \n" +
//                "  *****\n" +
//                "       \n" +
//                "       \n",
//                // next step (clear out starting room)
//                "*****  \n" +
//                "*****  \n" +
//                "*****  \n" +
//                "  * *  \n" +
//                "  *****\n" +
//                "  *    \n" +
//                "  *    \n",
//                // next step
//                "    *  \n" +
//                "    *  \n" +
//                "  ***  \n" +
//                "  * *  \n" +
//                "  *****\n" +
//                "  *    \n" +
//                "  *    \n",
//                // next step
//                "   #*  \n" +
//                "   #*  \n" +
//                "*****  \n" +
//                "  * *  \n" +
//                "  *****\n" +
//                "  *    \n" +
//                "  *    \n",
        });
        Position initialPosition = Position.initial(0, 0);
        Solution solution = new Solution();
        Path path = solution.solve(maze, initialPosition);
        System.out.println(path.ontoMazeAsText(maze));
        assertEquals(path.first(), initialPosition,
                "The solution path does not start at the initial position");
        assertTrue(path.isPhysical(),
                "The solution contains impossible moves, like jumping multiple squares, or going back in time.");
        assertEquals(Cell.Exit, maze.get(path.last()),
                "The solution does not solve the maze (it does not end at an exit).");
        assertTrue(path.isSolution(maze),
                "The solution is not valid for the maze — it may cross walls or leave the maze area.");
    }

    @Test
    void testGeneratedPerfect001() {
        Pair<Maze, Position> puzzle = MazeGenerator.generate(123_456_111, 1, 20, 0.0, 1);
        checkMazeSolution(puzzle.getLeft(), puzzle.getRight());
    }

    @Test
    void testGeneratedPerfect002() {
        Pair<Maze, Position> puzzle = MazeGenerator.generate(123_456_222, 2, 30, 0.0, 1);
        checkMazeSolution(puzzle.getLeft(), puzzle.getRight());
    }

    @Test
    void testGeneratedPerfect003() {
        Pair<Maze, Position> puzzle = MazeGenerator.generate(123_456_333, 3, 45, 0.0, 1);
        checkMazeSolution(puzzle.getLeft(), puzzle.getRight());
    }

    @Disabled
    @Test
    void testGeneratedPerfect004() {
        Pair<Maze, Position> puzzle = MazeGenerator.generate(123_456_444, 3, 60, 0.0, 1);
        checkMazeSolution(puzzle.getLeft(), puzzle.getRight());
    }

    @Disabled
    @Test
    void testGeneratedPerfect005() {
        Pair<Maze, Position> puzzle = MazeGenerator.generate(123_456_555, 4, 80, 0.0, 1);
        checkMazeSolution(puzzle.getLeft(), puzzle.getRight());
    }

    @Disabled
    @Test
    void testGeneratedPerfect006() {
        Pair<Maze, Position> puzzle = MazeGenerator.generate(123_456_666, 3, 100, 0.0, 1);
        checkMazeSolution(puzzle.getLeft(), puzzle.getRight());
    }

    @Disabled
    @Test
    void testGeneratedPerfect007() {
        Pair<Maze, Position> puzzle = MazeGenerator.generate(123_456_777, 4, 120, 0.0, 1);
        checkMazeSolution(puzzle.getLeft(), puzzle.getRight());
    }

    @Disabled
    @Test
    void testGeneratedPerfect008() {
        Pair<Maze, Position> puzzle = MazeGenerator.generate(123_456_888, 5, 140, 0.0, 1);
        checkMazeSolution(puzzle.getLeft(), puzzle.getRight());
    }

    @Disabled
    @Test
    void testGeneratedPerfect009() {
        Pair<Maze, Position> puzzle = MazeGenerator.generate(123_456_999, 6, 160, 0.0, 1);
        checkMazeSolution(puzzle.getLeft(), puzzle.getRight());
    }

    @Disabled
    @Test
    void testGeneratedPerfect010() {
        Pair<Maze, Position> puzzle = MazeGenerator.generate(123_456_000, 7, 200, 0.0, 1);
        checkMazeSolution(puzzle.getLeft(), puzzle.getRight());
    }

    @Test
    void testGeneratedPorous001() {
        Pair<Maze, Position> puzzle = MazeGenerator.generate(456_789_111, 1, 20, 0.10, 2);
        checkMazeSolution(puzzle.getLeft(), puzzle.getRight());
    }

    @Test
    void testGeneratedPorous002() {
        Pair<Maze, Position> puzzle = MazeGenerator.generate(456_789_222, 2, 30, 0.15, 3);
        checkMazeSolution(puzzle.getLeft(), puzzle.getRight());
    }

    @Test
    void testGeneratedPorous003() {
        Pair<Maze, Position> puzzle = MazeGenerator.generate(456_789_333, 3, 45, 0.20, 4);
        checkMazeSolution(puzzle.getLeft(), puzzle.getRight());
    }

    @Disabled
    @Test
    void testGeneratedPorous004() {
        Pair<Maze, Position> puzzle = MazeGenerator.generate(456_789_444, 3, 60, 0.10, 3);
        checkMazeSolution(puzzle.getLeft(), puzzle.getRight());
    }

    @Disabled
    @Test
    void testGeneratedPorous005() {
        Pair<Maze, Position> puzzle = MazeGenerator.generate(456_789_555, 4, 80, 0.14, 2);
        checkMazeSolution(puzzle.getLeft(), puzzle.getRight());
    }

    @Disabled
    @Test
    void testGeneratedPorous006() {
        Pair<Maze, Position> puzzle = MazeGenerator.generate(456_789_666, 3, 100, 0.08, 1);
        checkMazeSolution(puzzle.getLeft(), puzzle.getRight());
    }

    @Disabled
    @Test
    void testGeneratedPorous007() {
        Pair<Maze, Position> puzzle = MazeGenerator.generate(456_789_777, 4, 120, 0.18, 5);
        checkMazeSolution(puzzle.getLeft(), puzzle.getRight());
    }

    @Disabled
    @Test
    void testGeneratedPorous008() {
        Pair<Maze, Position> puzzle = MazeGenerator.generate(456_789_888, 5, 140, 0.10, 10);
        checkMazeSolution(puzzle.getLeft(), puzzle.getRight());
    }

    @Disabled
    @Test
    void testGeneratedPorous009() {
        Pair<Maze, Position> puzzle = MazeGenerator.generate(456_789_999, 6, 160, 0.13, 7);
        checkMazeSolution(puzzle.getLeft(), puzzle.getRight());
    }

    @Disabled
    @Test
    void testGeneratedPorous010() {
        Pair<Maze, Position> puzzle = MazeGenerator.generate(456_789_000, 7, 200, 0.16, 4);
        checkMazeSolution(puzzle.getLeft(), puzzle.getRight());
    }

    @Disabled
    @Test
    void testPerformance() {
        double total = 0;
        for (int i = 0; i < 50; i++) {
            System.out.println("perfect i = " + i);
            Pair<Maze, Position> puzzle = MazeGenerator.generate(246_800_000 + 111 * i, 5, 150, 0.0, 1);
            total += checkMazeSolution(puzzle.getLeft(), puzzle.getRight());
        }
        for (int i = 0; i < 100; i++) {
            System.out.println("porous i = " + i);
            Pair<Maze, Position> puzzle = MazeGenerator.generate(135_780_000 + 111 * i, 6, 150, 0.10, 5);
            total += checkMazeSolution(puzzle.getLeft(), puzzle.getRight());
        }
        System.out.println(String.format("took %.3f ms", total));
    }
}
