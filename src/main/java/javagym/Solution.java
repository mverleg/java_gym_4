package javagym;

import java.util.Arrays;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.Validate;

import noedit.Maze;
import noedit.Path;
import noedit.PathBuilder;
import noedit.Position;

import static javagym.Util.dist;
import static noedit.Cell.Exit;
import static noedit.Cell.Wall;

public class Solution {

    @Nonnull
    @CheckReturnValue
    public Path solve(@Nonnull Maze maze, @Nonnull Position initialPosition) {
        Validate.isTrue(Wall != maze.get(initialPosition),
                "Started inside a wall; this should never happen");

        // First find the exits.
        //TODO @mark: probably just set max count to 20+
        Position[] exits = findExits(maze, initialPosition, 5);
        Validate.isTrue(exits.length > 0, "No exist, IT'S A TRAP!");

        // Create a priority queue of items to process.
        Queue queue = new Queue(exits);

        // And create a grid of visited cells.
        VisitGrid grid = new VisitGrid(maze);

        // Kick off from the initial position.
        queue.add(new PathBuilder(initialPosition));
        grid.mark(initialPosition);

        // Go through all the nodes until an exit is found.
        while (queue.isNotEmpty()) {

            PathBuilder path = queue.head();
            assert path != null: "No more items to explore, even though no exit has been found";

            // Find which neighbours can be explored.
            // If there is only one (tunnel), follow it.
            Position[] neighbours = findExplorable(path.latest(), maze, grid);
            while (neighbours.length == 1) {
                path.add(neighbours[0]);
                if (maze.get(neighbours[0]) == Exit) return path.build();
                neighbours = findExplorable(path.latest(), maze, grid);
            }

            // Add all the options (either a split, or a dead end).
            for (Position neighbour : neighbours) {
                PathBuilder newPath = path.clone();
                newPath.add(neighbour);
                if (maze.get(neighbours[0]) == Exit) return path.build();
                queue.add(newPath);
            }
        }

//        System.out.println(maze.asStringAll());
//        PathBuilder path = new PathBuilder(initialPosition);
//        while (true) {
//            Position leftPos = path.latest().left();
//            Cell leftCell = maze.getOrElse(leftPos, Wall);
//            if (leftCell == Wall) {
//                // Let's just give up here.
//                break;
//            }
//            path.left();
//            if (leftCell == Exit) {
//                break;
//            }
//        }
        throw new IllegalStateException("Sorry, I failed to find a solution");
    }

    @Nonnull
    @CheckReturnValue
    private Position[] findExplorable(@Nonnull Position position, @Nonnull Maze maze, @Nonnull VisitGrid grid) {
        Position[] neighbours = new Position[]{
                position.nextStep(),
                position.right(),
                position.down(),
                position.left(),
                position.up()
        };
        return Arrays.stream(neighbours)
                .filter(neighbour -> !grid.visited(neighbour))
                .filter(neighbour -> maze.getOrElse(position, Wall) != Wall)
                .toArray(Position[]::new);
    }

    /**
     * Create an array of exits, from nearest to furthest from initial position.
     */
    @Nonnull
    @CheckReturnValue
    private Position[] findExits(@Nonnull Maze maze, @Nonnull Position initialPosition, int maxCount) {
        return maze.stream()
                .filter(cell -> cell.getRight() == Exit)
                .map(cell -> cell.getLeft())
                .sorted((pos1, pos2) -> dist(initialPosition, pos2) - dist(initialPosition, pos1))
                .limit(maxCount)
                .toArray(Position[]::new);
    }
}
