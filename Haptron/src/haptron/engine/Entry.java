package haptron.engine;

import haptron.levels.LevelBuilder;
import haptron.menus.MainMenu;

public class Entry {
	
	public static void entry(String... args) {
		Haptron haptron = Haptron.create();
		haptron.setScreen(new MainMenu(haptron));
	}
	
	public static void main(String... args) {
		try {
			
			if(args.length < 1) {
				entry(args);
				return;
			}
			
			String[] _args = new String[args.length - 1];
			
			for(int i = 0; i < _args.length; i++) {
				_args[i] = args[i+1];
			}
			
			switch(args[0]) {
			case "-levelbuilder":
				LevelBuilder.entry(_args);
				break;
			default:
				entry(_args);
			}
		} catch (Exception e) {
			Console.error(e);
		}
	}
}
