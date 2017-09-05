package haptron.utils;

import haptron.events.Event;

public class Timer extends Clock {
	
	public volatile Event on_complete;
	
	public Timer(long limit) {
		super(limit);
	}

	public Timer(long limit, Event on_complete) {
		super(limit);
		this.on_complete = on_complete;
	}
	
	@Override
	public boolean tick() {
		boolean result = super.tick();
		if(!result) {
			if(on_complete != null) return on_complete.tick();
			else return false;
		} else return true;
	}
}
