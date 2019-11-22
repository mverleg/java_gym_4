package javagym;

import java.util.TreeMap;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.Validate;

import noedit.Maze;
import noedit.Path;
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

        // Create a priority queue of items to process.
        Queue queue = new Queue(exits);
        


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
//        return path.build();
        throw new NotImplementedException("todo: ");  //TODO @mark: implement
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
