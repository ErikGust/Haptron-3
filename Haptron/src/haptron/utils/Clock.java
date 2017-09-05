package haptron.utils;

import haptron.events.Event;

public class Clock implements Event {
	public static final long INFINITE = -1;

	private final long limit;
	private boolean stop;
	private long time;
	
	public Clock(long limit) {
		this.time = 0;
		this.stop = false;
		this.limit = limit;
	}
	
	@Override
	public synchronized boolean tick() {
		time++;
		return (limit != INFINITE ? time < limit : true) && !stop;
	}
	
	public synchronized long getTime() {
		return time;
	}
	
	public synchronized long reset() {
		long c = time;
		time = 0;
		return c;
	}
	
	public synchronized long getLimit() {
		return limit;
	}
	
	public synchronized void stop() {
		this.stop = true;
	}

	public boolean isAlive() {
		return !stop;
	}
}
