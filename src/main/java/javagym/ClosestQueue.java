package javagym;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import noedit.PathBuilder;
import noedit.Position;

import static javagym.Parameters.THREADING;
import static javagym.Util.smallestDist;

//TODO @mark: remove all syncrhonized if not parallel

/**
 * A queue that puts paths first end closer to an exit.
 *
 * This is linear in the number of targets; if there are too many, use {@link LayerQueue}.
 */
public final class ClosestQueue implements PathQueue {

	public static final class Node implements Comparable<Node> {
		@Nonnull private final PathBuilder path;
		private final int smallestDistance;

		public Node(@Nonnull PathBuilder path, int smallestDistance) {
			this.path = path;
			this.smallestDistance = smallestDistance;
		}

		@Override
		public int compareTo(@Nonnull Node other) {
			return this.smallestDistance - other.smallestDistance;
		}
	}

	@Nonnull private final Position[] targets;
	@Nonnull private final Queue<Node> priorityQueue;

	public ClosestQueue(@Nonnull Position[] targets) {
		this.targets = targets;
		if (THREADING) {
			this.priorityQueue = new PriorityBlockingQueue<>();
		} else {
			this.priorityQueue = new PriorityQueue<>();
		}
	}

	@Override
	public void add(@Nonnull PathBuilder path) {
		int dist = smallestDist(path.latest(), targets);
		Node node = new Node(path, dist);
		priorityQueue.add(node);
	}

	@Override
	@Nullable
	@CheckReturnValue
	public PathBuilder head() {
		Node node;
		node = priorityQueue.poll();
		if (node == null) {
			return null;
		}
		return node.path;
	}

	@Override
	public boolean isNotEmpty() {
		return !priorityQueue.isEmpty();
	}
}
