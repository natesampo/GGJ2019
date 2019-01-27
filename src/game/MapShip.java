package game;

import java.awt.Graphics;

import game.GameObject.Animations;

public class MapShip extends GameObject {

	public MapShip(int x, int y) {
		super(x, y);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update(Game game, double dt) {
		this.sprite.animate(Animations.MAPSHIP, dt);
		xreal += Math.signum(x-xreal)*Math.max(Math.abs(x-xreal)*kspeed/5, minspeed/3);
		yreal += Math.signum(y-yreal)*Math.max(Math.abs(y-yreal)*kspeed/5, minspeed/3);
		if(Math.abs(xreal-x)<minspeed) xreal = x;
		if(Math.abs(yreal-y)<minspeed) yreal = y;
	}
	
	/**
	 * Renders the sprite
	 * @param g - the Graphics context with appropriate translation and scaling
	 */
	public void draw(Graphics g) {
		if(!visible) return;
		g.translate((int)(xreal), (int)(yreal));
		sprite.draw(g, this);
		g.translate((int)(-xreal), (int)(-yreal));
	}
}
