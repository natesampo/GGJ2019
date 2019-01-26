package game;

import java.awt.Graphics;
import java.awt.Graphics2D;

import game.GameObject.Animations;

public class Boom extends GameObject {

	public int heading;
	public boolean right;
	public boolean impact = false;

	/**
	 * Fire the cannon
	 * @param heading - heading of ship
	 * @param right - side of ship
	 */
	public Boom(int x, int y, int heading, boolean right) {
		super(x, y);
		if(heading==0&&!right || heading==180&&right) {
			this.z = 1;
		}
		else {
			this.z = 4;
		}
		this.heading = heading;
		this.right = right;
	}

	/**
	 * Something was hit by a cannon
	 */
	public Boom(int x, int y) {
		super(x, y);
		this.z = 4;
		this.impact = true;
	}

	@Override
	public void update(Game game, double dt) {
		if(this.sprite.animate(Animations.BOOM, dt, heading/90)) {
			Game.sprites.remove(this);
		}
		this.sprite.flipX = false;
		this.sprite.flipY = false;
	}
}
