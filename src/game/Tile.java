package game;

import game.GameObject.Animations;

public class Tile extends GameObject {

	public Tile(int x, int y) {
		super(x, y);
	}

	@Override
	public void update(Game game, double dt) {
		this.sprite.animate(Animations.WATER, dt);
	}

	@Override
    public int compareTo(GameObject obj) {
		if(!(obj instanceof Tile)) return -1;
		return super.compareTo(obj);
    }
}
