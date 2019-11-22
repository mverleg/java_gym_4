package javagym;

import javax.annotation.Nonnull;

import noedit.Maze;
import noedit.Position;

/**
 * Tracks cells that are already visited or are in the {@link Queue} (which counts as visited).
 */
public class VisitGrid {

	private final int duration;
	private final int width;
	private final int height;
	private final boolean[][][] isVisited;

	public VisitGrid(@Nonnull Maze maze) {
		duration = maze.duration;
		width = maze.width;
		height = maze.height;
		isVisited = new boolean[duration][width][height];
	}

	public void mark(@Nonnull Position position) {
		isVisited[position.t][position.x][position.y] = true;
	}

	public boolean visited(@Nonnull Position position) {
		return isVisited[position.t][position.x][position.y];
	}
}
