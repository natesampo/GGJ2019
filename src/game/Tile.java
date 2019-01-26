package game;

import game.GameObject.Animations;

public class Tile extends GameObject {

	public int type;
	public Tile(int x, int y, int type) {
		super(x, y);
		this.type = type;
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

	@Override
    public int compareTo(GameObject obj) {
		if(!(obj instanceof Tile)) return -1;
		return super.compareTo(obj);
    }
}
