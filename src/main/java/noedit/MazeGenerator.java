package noedit;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.checkerframework.checker.index.qual.Positive;

import static noedit.Cell.Exit;
import static noedit.Cell.Open;
import static noedit.Cell.Wall;

public final class MazeGenerator {

	enum Direction {
		Right(+1, 0),
		Up(0, -1),
		Left(-1, 0),
		Down(0, +1);

		private final int dx;
		private final int dy;

		Direction(int dx, int dy) {
			this.dx = dx;
			this.dy = dy;
		}
	}

	/**
	 * Generate a random maze.
	 *
	 * @param seed Random seed, for reproducible results.
	 * @param duration Time dimension of the maze.
	 * @param circumference Spatial dimension of the maze.
	 * @param porosity How many extra holes are in the maze. Zero for a perfect maze, otherwise a number much smaller than one.
	 * @param exitCount Number of exits.
	 */
	@Nonnull
	@CheckReturnValue
	public static Pair<Maze, Position> generate(
			int seed,
			@Positive int duration,
			@Positive int circumference,
			@Positive double porosity,
			@Positive int exitCount
	) {
		//noinspection ConstantConditions
		Validate.isTrue(duration >= 1);
		Validate.isTrue(circumference >= 4);
		Validate.isTrue(porosity >= 0 && porosity < 1);
		//noinspection ConstantConditions
		Validate.isTrue(exitCount > 0 && exitCount <= circumference);

		Random rand = new Random(seed);
		int width = Math.max((int) Math.ceil((rand.nextDouble() * 0.8 + 0.1) * circumference), 2);
		int height = Math.max(circumference - width, 2);

		Spatial initialPos = Spatial.at(
				rand.nextInt(width),
				rand.nextInt(height)
		);

		Cell[][][] maze = new Cell[2 * duration - 1][width][height];

		// Add the mazes
		Spatial layerPos = initialPos;
		for (int t = 0; t < duration - 1; t++) {
			maze[2 * t] = makePorous(
					generatePerfect2D(rand, layerPos, width, height),
					porosity, rand);
			layerPos = Spatial.at(
					rand.nextInt(width),
					rand.nextInt(height)
			);
			Validate.isTrue(maze[2 * t][2 * layerPos.x][2 * layerPos.y] != Wall);
			maze[2 * t + 1] = makePorous(
					generateWallLayer(width, height),
					porosity, rand);
			maze[2 * t + 1][2 * layerPos.x][2 * layerPos.y] = Open;
		}
		maze[2 * duration - 2] = makePorous(
				generatePerfect2D(rand, layerPos, width, height),
				porosity, rand);

		// Add the exits
		while (exitCount > 0) {
			int tMax = 2 * duration - 2;
			int t = tMax;
			while (rand.nextInt(4) == 0) {
				t = ((t - 1) + tMax) % tMax;
			}
			if (maze[t][2 * rand.nextInt(width)][2 * rand.nextInt(height)] != Exit) {
				maze[t][2 * rand.nextInt(width)][2 * rand.nextInt(height)] = Exit;
				exitCount -= 1;
			}
		}

		return Pair.of(
				new Maze(maze),
				Position.initial(2 * initialPos.x, 2 * initialPos.y)
		);
	}

	/**
	 * Generate a mostly-open 'maze' with random obstacles. Not guaranteed to be solvable.
	 */
	@Nonnull
	@CheckReturnValue
	public static Pair<Maze, Position> generateOpen(
			int seed,
			@Positive int duration,
			@Positive int sideLength,
			@Positive double obstacleFraction
	) {
		Random rand = new Random(seed);
		Cell[][][] maze = new Cell[duration][sideLength][sideLength];
		for (int t = 0; t < duration; t++) {
			for (int x = 0; x < sideLength; x++) {
				for (int y = 0; y < sideLength; y++) {
					if (rand.nextDouble() > obstacleFraction) {
						maze[t][x][y] = Open;
					} else {
						maze[t][x][y] = Wall;
					}
				}
			}
		}
		maze[duration - 1][rand.nextInt(sideLength)][rand.nextInt(sideLength)] = Exit;
		int initX = rand.nextInt(sideLength);
		int initY = rand.nextInt(sideLength);
		maze[0][initX][initY] = Open;
		Position initialPos = Position.initial(initX, initY);
		return Pair.of(new Maze(maze), initialPos);
	}

	@Nonnull
	@CheckReturnValue
	private static Cell[][] generateWallLayer(int width, int height) {
		Cell[][] layer = new Cell[2 * width - 1][2 * height - 1];
		for (int x = 0; x < 2 * width - 1; x++) {
			for (int y = 0; y < 2 * height - 1; y++) {
				layer[x][y] = Wall;
			}
		}
		return layer;
	}

	@Nonnull
	@CheckReturnValue
	private static Cell[][] generateCheckeredLayer(int width, int height) {
		Cell[][] layer = new Cell[2 * width - 1][2 * height - 1];
		for (int x = 0; x < 2 * width - 1; x++) {
			for (int y = 0; y < 2 * height - 1; y++) {
				if (x % 2 == 0 && y % 2 == 0) {
					layer[x][y] = Open;
				} else {
					layer[x][y] = Wall;
				}
			}
		}
		return layer;
	}

	/**
	 * Create extra open spaces in the layer.
	 *
	 * @param porosity The fraction of extra open spaces. Spaces may already be empty, or may be cleared twice.
	 */
	@Nonnull
	@CheckReturnValue
	private static Cell[][] makePorous(@Nonnull Cell[][] layer, double porosity, Random rand) {
		int extraHoles = (int) Math.ceil(layer.length * layer[0].length * porosity);
		for (int h = 0; h < extraHoles; h++) {
			int x = rand.nextInt(layer.length);
			int y = rand.nextInt(layer[0].length);
			layer[x][y] = Open;
		}
		return layer;
	}

	@Nonnull
	@CheckReturnValue
	private static Cell[][] generatePerfect2D(
			@Nonnull Random rand,
			@Nonnull Spatial initialPos,
			@Positive int width,
			@Positive int height
	) {
		Cell[][] isOpen = generateCheckeredLayer(width, height);
		boolean[][] isVisited = new boolean[width][height];
		Stack<Spatial> toVisit = new Stack<>();
		toVisit.push(initialPos);

		// This object is cached, cleared each loop.
		List<Direction> adjecentWalls = new ArrayList<>(4);

		while (!toVisit.isEmpty()) {
			Spatial current = toVisit.pop();
			isVisited[current.x][current.y] = true;

			// Find unvisited neighbours (which are still walls, as walls are removed when visited).
			adjecentWalls.clear();
			if (current.x > 0 && !isVisited[current.x - 1][current.y]) {
				adjecentWalls.add(Direction.Left);
			}
			if (current.y > 0 && !isVisited[current.x][current.y - 1]) {
				adjecentWalls.add(Direction.Up);
			}
			if (current.x < width - 1 && !isVisited[current.x + 1][current.y]) {
				adjecentWalls.add(Direction.Right);
			}
			if (current.y < height - 1 && !isVisited[current.x][current.y + 1]) {
				adjecentWalls.add(Direction.Down);
			}

			Direction choice;
			if (adjecentWalls.size() > 1) {
				// We have to come back to this tile later.
				toVisit.push(current);
				choice = adjecentWalls.get(rand.nextInt(adjecentWalls.size()));
			} else if (adjecentWalls.size() == 1) {
				choice = adjecentWalls.get(0);
			} else {
				// This is a dead end.
				continue;
			}

			// Clear the chosen direction.
			isOpen[2 * current.x + choice.dx][2 * current.y + choice.dy] = Open;
			Spatial next = Spatial.at(current.x + choice.dx, current.y + choice.dy);
			toVisit.push(next);
		}

		return isOpen;
	}
}
