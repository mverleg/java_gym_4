package javagym;

import org.junit.jupiter.api.Test;

import noedit.PathBuilder;
import noedit.Position;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class QueueTest {

	@Test
	void testQueueOrder() {
		Position[] exits = new Position[]{
				Position.at(1, 10, 10),
		};
		Queue queue = new Queue(exits);
		queue.add(new PathBuilder(Position.at(0, 10, 10)));  // 80
		queue.add(new PathBuilder(Position.at(1, 3, 0)));  // 17
		queue.add(new PathBuilder(Position.at(0, 0, 0)));  // 100
		queue.add(new PathBuilder(Position.at(1, 0, 0)));  // 20
		queue.add(new PathBuilder(Position.at(1, 6, 6)));  // 10
		queue.add(new PathBuilder(Position.at(1, 8, 0)));  // 12
		assertEquals(Position.at(1, 6, 6), queue.head().latest());
		assertEquals(Position.at(1, 8, 0), queue.head().latest());
		assertEquals(Position.at(1, 3, 0), queue.head().latest());
		assertEquals(Position.at(1, 0, 0), queue.head().latest());
		assertEquals(Position.at(0, 10, 10), queue.head().latest());
		assertEquals(Position.at(0, 0, 0), queue.head().latest());
		assertNull(queue.head());
	}
}
