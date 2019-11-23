package javagym;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;

import noedit.Maze;
import noedit.Path;
import noedit.PathBuilder;
import noedit.Position;

import static javagym.Parameters.CHOKEPOINT_LAYER_LIMIT;
import static javagym.Parameters.EXIT_TRACK_LIMIT;
import static javagym.Parameters.THREADING;
import static javagym.Parameters.TUNNEL_FOLLOW_LENGTH;
import static noedit.Cell.Exit;
import static noedit.Cell.Wall;

public class Solution {

    @Nonnull
    private final int threadCount = Runtime.getRuntime().availableProcessors();
    @Nullable
    private final ExecutorService threadPool = THREADING ? Executors.newFixedThreadPool(threadCount) : null;

    //TODO @mark: flamegraph

    @Nonnull
    @CheckReturnValue
    public Path solve(@Nonnull Maze maze, @Nonnull Position initialPosition) {

        Validate.isTrue(Wall != maze.get(initialPosition),
                "Started inside a wall; this should never happen");

        // First find the exits, if there aren't too many.
        Position[] targets = new Position[0];
        Position[] exits = findExits(maze, EXIT_TRACK_LIMIT + 1);
        if (exits.length <= EXIT_TRACK_LIMIT) {
            Position[] chokepoints = CHOKEPOINT_LAYER_LIMIT > 0 ? findTimeChokePoints(maze, CHOKEPOINT_LAYER_LIMIT) : new Position[0];
            targets = ArrayUtils.addAll(exits, chokepoints);
        }

        // Create a priority queue of items to process.
        PathQueue queue;
        if (targets.length == 0) {
            queue = new LayerQueue(maze.duration);
        } else {
            queue = new ClosestQueue(targets);
        }

        // And create a grid of visited cells.
        VisitGrid grid = new VisitGrid(maze);

        // Kick off from the initial position.
        // Pay attention that anything added to path must be 1) checked for exit and 2) marked as visited.
        if (maze.get(initialPosition) == Exit) {
            return new PathBuilder(initialPosition).build();
        }
        grid.mark(initialPosition);
        queue.add(new PathBuilder(initialPosition));

        // Go through all the nodes until an exit is found.
        Path solutionPath;
        if (THREADING) {
            solutionPath = solveThreaded(maze, grid, queue);
        } else {
            solutionPath = work(maze, grid, queue);
        }
        return solutionPath;
    }

    private Path solveThreaded(@Nonnull Maze maze, @Nonnull VisitGrid grid, @Nonnull PathQueue queue) {
        Validate.notNull(threadPool);

        @SuppressWarnings("unchecked")
        Future<Path>[] solutions = new Future[threadCount];
        for (int w = 0; w < threadCount; w++) {
            solutions[w] = threadPool.submit(() -> work(maze, grid, queue));
        }
        try {
            for (int w = 0; w < threadCount; w++) {
                Path solutionPath = null;
                solutionPath = solutions[w].get();
                if (solutionPath != null) {
                    return solutionPath;
                }
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }
        return null;
    }

    @Nullable
    @CheckReturnValue
    private Path work(@Nonnull Maze maze, @Nonnull VisitGrid grid, @Nonnull PathQueue queue) {

        // Go through all the nodes until an exit is found.
        while (queue.isNotEmpty()) {

            PathBuilder path = queue.head();
            if (path == null) {
                // Probably some other thread finished the work.
                return null;
            }

            // Find which neighbours can be explored.
            // If there is only one (tunnel), follow it.
            Position[] neighbours = findExplorable(path.latest(), maze, grid);
            int tunnelSteps = 0;
            while (tunnelSteps < TUNNEL_FOLLOW_LENGTH && neighbours.length == 1) {
                Position neighbour = neighbours[0];
                // Pay attention that anything added to path must be 1) checked for exit and 2) marked as visited.
                path.add(neighbour);
                if (maze.get(neighbour) == Exit) {
                    //System.out.println(grid.asText());  //TODO @mark: TEMPORARY! REMOVE THIS!
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
                    //System.out.println(grid.asText());  //TODO @mark: TEMPORARY! REMOVE THIS!
                    return newPath.build();
                }
                grid.mark(neighbour);
                queue.add(newPath);
            }
        }

        return null;
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
    private Position[] findExits(@Nonnull Maze maze, int maxCount) {
        return maze.stream()
                .filter(cell -> cell.getRight() == Exit)
                .map(cell -> cell.getLeft())
                .limit(maxCount)
                .toArray(Position[]::new);
    }
}
