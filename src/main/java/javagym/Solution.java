package javagym;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;

import noedit.Maze;
import noedit.Path;
import noedit.PathBuilder;
import noedit.Position;

import static javagym.Util.dist;
import static noedit.Cell.Exit;
import static noedit.Cell.Wall;

public class Solution {

    //TODO @mark: flamegraph
    //TODO @mark: if there are too many target points, it's better to ignore them alltogether, rather than take the first few

    @Nonnull
    @CheckReturnValue
    public Path solve(@Nonnull Maze maze, @Nonnull Position initialPosition) {
        Validate.isTrue(Wall != maze.get(initialPosition),
                "Started inside a wall; this should never happen");

        // First find the exits.
        Position[] targets = ArrayUtils.addAll(
                findExits(maze, initialPosition, 16),
                findTimeChokePoints(maze, 3)
        );
        Validate.isTrue(targets.length > 0, "No exits, IT'S A TRAP!");

        // Create a priority queue of items to process.
        Queue queue = new Queue(targets);

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
            assert path != null : "No more items to explore, even though no exit has been found";

            // Find which neighbours can be explored.
            // If there is only one (tunnel), follow it.
            Position[] neighbours = findExplorable(path.latest(), maze, grid);
            int tunnelSteps = 0;
            while (tunnelSteps < 15 && neighbours.length == 1) {
                Position neighbour = neighbours[0];
                // Pay attention that anything added to path must be 1) checked for exit and 2) marked as visited.
                path.add(neighbour);
                if (maze.get(neighbour) == Exit) {
                    return path.build();
                }
                grid.mark(neighbour);
                tunnelSteps += 1;
                neighbours = findExplorable(path.latest(), maze, grid);
            }

            // Add all the options (either a split, or a dead end, or too long tunnel).
            for (Position neighbour : neighbours) {
                PathBuilder newPath = path.clone();
                // Pay attention that anything added to path must be 1) checked for exit and 2) marked as visited.
                newPath.add(neighbour);
                if (maze.get(neighbour) == Exit) {
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
     * Find layers with few accessible open spaces, so that they can be rargeted.
     */
    @Nonnull
    @CheckReturnValue
    @SuppressWarnings("SameParameterValue")
    private Position[] findTimeChokePoints(Maze maze, int maxPerLayer) {
        if (maze.duration <= 2) {
            return new Position[0];
        }
        @SuppressWarnings("unchecked")
        List<Position>[] layersPassages = new ArrayList[maze.duration - 2];
        for (int t = 1; t < maze.duration - 1; t++) {
            ArrayList<Position> currentLayerPackages = new ArrayList<>();
            for (int x = 0; x < maze.width; x++) {
                for (int y = 0; y < maze.height; y++) {
                    boolean prevOpen = maze.getOrElse(t - 1, x, y, Wall) != Wall;
                    boolean curOpen = maze.getOrElse(t, x, y, Wall) != Wall;
                    boolean nextOpen = maze.getOrElse(t + 1, x, y, Wall) != Wall;
                    boolean isPassage = (prevOpen && curOpen) || (curOpen && nextOpen);
                    if (isPassage) {
                        currentLayerPackages.add(Position.at(t, x, y));
                    }
                }
                if (currentLayerPackages.size() > maxPerLayer) {
                    break;
                }
            }
            Validate.isTrue(!currentLayerPackages.isEmpty());
            layersPassages[t - 1] = currentLayerPackages;
        }
        return Arrays.stream(layersPassages)
                .filter(passages -> passages.size() <= maxPerLayer)
                .flatMap(passages -> passages.stream())
                .toArray(Position[]::new);
    }

    /**
     * Create an array of exits, from nearest to furthest from initial position.
     */
    @Nonnull
    @CheckReturnValue
    @SuppressWarnings("SameParameterValue")
    private Position[] findExits(@Nonnull Maze maze, @Nonnull Position initialPosition, int maxCount) {
        return maze.stream()
                .filter(cell -> cell.getRight() == Exit)
                .map(cell -> cell.getLeft())
                .sorted((pos1, pos2) -> dist(initialPosition, pos2) - dist(initialPosition, pos1))
                .limit(maxCount)
                .toArray(Position[]::new);
    }
}
