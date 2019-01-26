package game;

import game.GameObject.Animations;

public class Tile extends GameObject {

	public int type;
	public int rand = 0;

	public Tile(int x, int y, int type) {
		super(x, y);
		this.z = 1;
		this.type = type;
		if (type == 1) {
			this.z = 0;
		}
		if (type == 2||type==3) {
			Game.grid[x][y] = this;
		}
		switch(type) {
		case 3: rand = (int)(Math.random()*2); break;
		}
	}

	@Override
	public void update(Game game, double dt) {
		switch (type) {
		case 1:
			this.sprite.animate(Animations.WATER, dt);
			this.sprite.framerate = 10;
			break;
		case 2:
			this.sprite.animate(Animations.ROCK2, dt);
			break;
		case 3:
			if (rand==1) {
				this.sprite.animate(Animations.ROCK1, dt);
			} else {
				this.sprite.animate(Animations.ROCK3, dt);
			}
			break;
		}
	}
}
