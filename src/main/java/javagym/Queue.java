package javagym;

import java.util.PriorityQueue;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.Validate;

import noedit.PathBuilder;
import noedit.Position;

import static javagym.Util.smallestDist;

/**
 * A queue that puts paths first end closer to an exit.
 */
public final class Queue {

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
	@Nonnull private final PriorityQueue<Node> priorityQueue;

	public Queue(@Nonnull Position[] targets) {
		Validate.isTrue(targets.length > 0);
		this.targets = targets;
		this.priorityQueue = new PriorityQueue<>();
	}

	public void add(@Nonnull PathBuilder path) {
		int dist = smallestDist(path.latest(), targets);
		Node node = new Node(path, dist);
		priorityQueue.add(node);
	}

	@Nullable
	@CheckReturnValue
	public PathBuilder head() {
		Node node = priorityQueue.poll();
		if (node == null) {
			return null;
		}
		return node.path;
	}

	public boolean isNotEmpty() {
		return !priorityQueue.isEmpty();
	}
}
