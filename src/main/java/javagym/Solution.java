package javagym;

import java.util.Arrays;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;

import noedit.Maze;
import noedit.Path;
import noedit.PathBuilder;
import noedit.Position;

import static javagym.Util.dist;
import static noedit.Cell.Exit;
import static noedit.Cell.Wall;

public class Solution {

    //TODO @mark: focus more on time holes
    //TODO @mark: flamegraph
    //TODO @mark: don't follow tunnels too long

    @Nonnull
    @CheckReturnValue
    public Path solve(@Nonnull Maze maze, @Nonnull Position initialPosition) {
        Validate.isTrue(Wall != maze.get(initialPosition),
                "Started inside a wall; this should never happen");

        // First find the exits.
        Position[] exits = findExits(maze, initialPosition, 16);
        Validate.isTrue(exits.length > 0, "No exits, IT'S A TRAP!");

        //TODO @mark: TEMPORARY! REMOVE THIS!
        // System.out.println(maze.asStringAll());
        // System.out.println(costGridAsText(maze, exits));

        // Create a priority queue of items to process.
        Queue queue = new Queue(exits);

        // And create a grid of visited cells.
        VisitGrid grid = new VisitGrid(maze);

        // Kick off from the initial position.
        // Pay attention that anything added to path must be 1) checked for exit and 2) marked as visited.
        if (maze.get(initialPosition) == Exit) {
            //System.out.println(grid.asText());
            return new PathBuilder(initialPosition).build();
        }
        grid.mark(initialPosition);
        queue.add(new PathBuilder(initialPosition));

        // Go through all the nodes until an exit is found.
        while (queue.isNotEmpty()) {

            PathBuilder path = queue.head();
            assert path != null: "No more items to explore, even though no exit has been found";
            // System.out.println("taking " + path.latest() + " cost " + smallestDist(path.latest(), exits));  //TODO @mark: TEMPORARY! REMOVE THIS!

            // Find which neighbours can be explored.
            // If there is only one (tunnel), follow it.
            Position[] neighbours = findExplorable(path.latest(), maze, grid);
            while (neighbours.length == 1) {
                Position neighbour = neighbours[0];
                // Pay attention that anything added to path must be 1) checked for exit and 2) marked as visited.
                path.add(neighbour);
                // System.out.println(path.latest().t + ": tunnel: " + neighbour + " (" + smallestDist(neighbour, exits) + ")");  //TODO @mark: TEMPORARY! REMOVE THIS!
                if (maze.get(neighbour) == Exit) {
                    // System.out.println(path.build().ontoMazeAsText(maze));  //TODO @mark: TEMPORARY! REMOVE THIS!
                    // System.out.println(grid.asText());  //TODO @mark: TEMPORARY! REMOVE THIS!
                    return path.build();
                }
                grid.mark(neighbour);
                neighbours = findExplorable(path.latest(), maze, grid);
            }

            // Add all the options (either a split, or a dead end).
            for (Position neighbour : neighbours) {
                PathBuilder newPath = path.clone();
                // Pay attention that anything added to path must be 1) checked for exit and 2) marked as visited.
                newPath.add(neighbour);
                // System.out.println(path.latest().t + ": corner: " + neighbour + " (" + smallestDist(neighbour, exits) + ")");  //TODO @mark: TEMPORARY! REMOVE THIS!
                if (maze.get(neighbour) == Exit) {
                    // System.out.println(newPath.build().ontoMazeAsText(maze));  //TODO @mark: TEMPORARY! REMOVE THIS!
                    // System.out.println(grid.asText());  //TODO @mark: TEMPORARY! REMOVE THIS!
                    return newPath.build();
                }
                grid.mark(neighbour);
                queue.add(newPath);
            }
        }

        throw new IllegalStateException("Sorry, I failed to find a solution, I thought I checked everything");
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
                .filter(neighbour -> maze.getOrElse(neighbour, Wall) != Wall)
                .filter(neighbour -> !grid.visited(neighbour))
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
