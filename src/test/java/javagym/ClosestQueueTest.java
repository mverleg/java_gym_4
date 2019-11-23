package javagym;

import org.junit.jupiter.api.Test;

import noedit.PathBuilder;
import noedit.Position;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ClosestQueueTest {

	@Test
	void testQueueOrder() {
		Position[] exits = new Position[]{
				Position.at(1, 10, 10),
		};
		ClosestQueue closestQueue = new ClosestQueue(exits);
		closestQueue.add(new PathBuilder(Position.at(0, 10, 10)));  // 80
		closestQueue.add(new PathBuilder(Position.at(1, 3, 0)));  // 17
		closestQueue.add(new PathBuilder(Position.at(0, 0, 0)));  // 100
		closestQueue.add(new PathBuilder(Position.at(1, 0, 0)));  // 20
		closestQueue.add(new PathBuilder(Position.at(1, 6, 6)));  // 10
		closestQueue.add(new PathBuilder(Position.at(1, 8, 0)));  // 12
		assertEquals(Position.at(1, 6, 6), closestQueue.head().latest());
		assertEquals(Position.at(1, 8, 0), closestQueue.head().latest());
		assertEquals(Position.at(1, 3, 0), closestQueue.head().latest());
		assertEquals(Position.at(1, 0, 0), closestQueue.head().latest());
		assertEquals(Position.at(0, 10, 10), closestQueue.head().latest());
		assertEquals(Position.at(0, 0, 0), closestQueue.head().latest());
		assertNull(closestQueue.head());
	}
}
