package game;

import game.GameObject.Animations;

public class Tile extends GameObject {

	public int type;
	public Tile(int x, int y, int type) {
		super(x, y);
		this.z = 1;
		this.type = type;
		if(type == 1) {
			this.z = 0;
		}
		if(type == 2) {
			Game.grid[x][y] = this;
		}
	}

	@Override
	public void update(Game game, double dt) {
		switch(type){
			case 1:
				this.sprite.animate(Animations.WATER, dt); break;
			case 2:
				this.sprite.animate(Animations.ROCK1, dt); break;
		}	
	}
}
