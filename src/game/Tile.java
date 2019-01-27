package game;

import game.GameObject.Animations;

public class Tile extends GameObject {

	public int type, damage;
	public int rand = 0;

	public Tile(int x, int y, int type) {
		super(x, y);
		this.z = 1;
		this.type = type;
		this.damage = 0;
		if (type == 1) {
			this.z = 0;
			this.sprite.offset((int)(Math.random()*8));
		}
		if (type == 2||type==3||type==4) {
			Game.grid[x][y] = this;
			this.damage = 1;
		}
		switch(type) {
			case 3: rand = (int)(Math.random()*2); break;
			case 4: sprite.framerate = 3;
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
		case 4:
			this.sprite.animate(Animations.MINE, dt);
		}
	}
}
