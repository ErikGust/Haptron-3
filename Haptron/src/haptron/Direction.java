package haptron;

public enum Direction {
	LEFT,RIGHT,UP,DOWN, NONE;
	
	public Direction opposite() {
		switch(this) {
		case LEFT:
			return RIGHT;
		case RIGHT:
			return LEFT;
		case UP:
			return DOWN;
		case DOWN:
			return UP;
		default:
			return NONE;
		}
	}
}
