package game;

import java.awt.Graphics;
import java.awt.Graphics2D;

import game.GameObject.Animations;

public class Boom extends GameObject {

	public Ship ship;
	public int heading;
	public boolean right;
	public boolean impact = false;

	/**
	 * Fire the cannon
	 * @param heading - heading of ship
	 * @param right - side of ship
	 */
	public Boom(Ship s, int heading, boolean right) {
		super(s.x, s.y);
		this.ship = s;
		if(heading==0&&right || heading==180&&right) {
			this.z = 4;
		}
		else {
			this.z = 1;
		}
		this.heading = heading;
		this.right = right;
		this.sprite.framerate = 16;
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
		this.xreal = ship.xreal;
		this.yreal = ship.yreal;
		if(this.sprite.animate(Animations.BOOM, dt, (heading==270?2:0)+(heading/90)%2+((heading/90)%2==0?1:0)*(right?2:0))) {
			Game.sprites.remove(this);
		}
		this.sprite.flipX = heading==0 || (heading%180==90)&&right;
	}
}
