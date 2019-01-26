package game;

import game.GameObject.Animations;

public class Boom extends GameObject {

	public int heading;
	
	public Boom(int x, int y, int heading) {
		super(x, y);
		this.heading = heading;
	}

	@Override
	public void update(Game game, double dt) {
		if(this.sprite.animate(Animations.BOOM, dt)) {
			Game.sprites.remove(this);
		}
	}

}
