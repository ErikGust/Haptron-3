package haptron.levels;

import haptron.engine.Console;
import haptron.engine.Haptron;
import haptron.utils.Resources;

public class Levels {
	private static final LevelData[] levels;
	static {
		levels = new LevelData[7];
		
		for(int i = 0; i < levels.length; i++) {
			try {
				levels[i] = LevelData.read(Resources.getResource("haptron.levels.level" + (i+1)));
			} catch (Exception e) {
				Console.error(e);
				System.exit(-1);
			}
		}
	}
	
	public static LevelData nextLevel(LevelData data) {
		for(int i = 0; i < levels.length; i++) {
			if(levels[i] == data) return getLevel(i+1);
		}
		return null;
	}
	
	public static Level newGame(Haptron haptron) {
		return new Level(getLevel(6), 3, haptron, System.currentTimeMillis());
	}
	
	public static LevelData getLevel(int level) {
		if(level < 0 || level >= levels.length) return null;
		return levels[level];
	}
}
