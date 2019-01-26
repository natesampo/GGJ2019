package game;

import java.awt.Graphics;

public class Ship extends GameObject {
	public int heading, type;
	
	/**
	 * Creates a new ship. 
	 * @param x - Leftmost square = 0
	 * @param y - Topmost square = 0
	 * @param heading - Right = 0, up = 90, left = 180, down = 270
	 * @param type 0 = player, 1 = easy, 2 = medium, 3 = hard
	 */
	public Ship(int x, int y, int heading, int type) {
		super(x, y);
		this.heading = heading;
		this.type = type;
	}

	@Override
	public void update(Game game, double dt) {
		this.sprite.animate(Animations.HYDRA, dt);
	}
	
	public boolean moveLeft() {
		if(heading == 0 || x == 0) return false;
		translate(-1, 0);
		heading = 180;
		return true;
	}
	
	public boolean moveRight() {
		if(heading == 180 || x == Game.W-1) return false;
		translate(1, 0);
		heading = 0;
		return true;
	}
	
	public boolean moveUp() {
		if(heading == 270 || y == 0) return false;
		translate(0, -1);
		heading = 90;
		return true;
	}
	
	public boolean moveDown() {
		if(heading == 90 || y == Game.H-1) return false;
		translate(0,1);
		heading = 270;
		return true;
	}
	
	public void translate(int dx, int dy) {
		x += dx;
		y += dy;
	}
	
	/**
	 * Renders the sprite
	 * @param g - the Graphics context with appropriate translation and scaling
	 */
//	@Override
//	public void draw(Graphics g) {
//		Graphics2D g2d = (Graphics2D)g;
//		g2d.rotate(Math.toRadians(degrees));
//		super.draw(g);
//	}
}
