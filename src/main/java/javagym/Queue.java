package javagym;

import java.util.Optional;
import java.util.PriorityQueue;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.Validate;

import noedit.Position;

import static javagym.Util.smallestDist;

/**
 * A queue that puts positions first that are closer to an exit.
 */
public final class Queue {

	public static final class Node implements Comparable<Node> {
		@Nonnull private final Position position;
		private final int smallestDistance;

		public Node(@Nonnull Position position, int smallestDistance) {
			this.position = position;
			this.smallestDistance = smallestDistance;
		}

		@Override
		public int compareTo(@Nonnull Node other) {
			return other.smallestDistance - this.smallestDistance;
		}
	}

	@Nonnull private final Position[] targets;
	@Nonnull private final PriorityQueue<Node> priorityQueue;

	public Queue(@Nonnull Position[] targets) {
		Validate.isTrue(targets.length > 0);
		this.targets = targets;
		this.priorityQueue = new PriorityQueue<>();
	}

	public void add(@Nonnull Position position) {
		int dist = smallestDist(position, targets);
		Node node = new Node(position, dist);
		priorityQueue.add(node);
	}

	@Nullable
	@CheckReturnValue
	public Position head() {
		Node node = priorityQueue.poll();
		if (node == null) {
			return null;
		}
		return node.position;
	}
}
