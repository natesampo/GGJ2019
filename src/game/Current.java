package game;

import game.GameObject.Animations;

public class Current extends Tile {
	
	int heading;

	public Current(int x, int y, int heading) {
		super(x, y, 5);
		this.heading = heading;
		this.z = 0;
		Game.currentGrid[x][y] = heading/90+1;
	}
	
	@Override
	public void update(Game game, double dt) {
		this.sprite.animate(Animations.CURRENT, dt, heading/90);
	}
}
