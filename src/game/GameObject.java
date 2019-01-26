package game;

import java.awt.Graphics;

/**
 * A game element capable of displaying animations
 */
public abstract class GameObject implements Comparable<GameObject> {
	
	protected Sprite sprite;
	public double x, y;
	public int z, w, h;
	public boolean visible = true;
	
	/**
	 * Animations that Sprites can display
	 */
	public enum Animations {
		HYDRA("hydra", 4, 4);
		
		public String filename;
		public int columns;
		public int frames;
		private Animations(String filename, int columns, int frames) {
			this.filename = filename;
			this.columns = columns;
			this.frames = frames;
		}
	}
	
	/**
	 * Superclass constructor
	 * @param x - initial x-coordinate in pixels
	 * @param y - initial y-coordinate in pixels
	 * @param z - relative layer to display sprite on-screen
	 * @param w - width of GameObject for collisions in pixels
	 * @param h - height of GameObject for collisions in pixels
	 */
	public GameObject(double x, double y, int z, int w, int h) {
		this.sprite = new Sprite();
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Updates the sprite behavior and animation
	 * @param dt - elapsed time in seconds
	 * @return An array containing horizontal and vertical displacement in pixels
	 */
	public abstract double[] update(Game game, double dt);

	/**
	 * Checks if another GameObject can block this GameObject's motion
	 * @param obj - GameObject being interacted with
	 * @return If the other GameObject can block this GameObject
	 */
	public boolean isCollidable(GameObject obj) {
		return false;
	}
	
	/**
	 * Checks if another GameObject can interact with this GameObject
	 * @param obj - GameObject being interacted with
	 * @return If the other GameObject can interact with this GameObject
	 */
	public boolean isInteractable(GameObject obj) {
		return isCollidable(obj);
	}
	
	/**
	 * Called when another GameObject collides with this one
	 * @param obj - The object that collided with this object
	 */
	public void collide(GameObject obj) {
		return;
	}
	
	/**
	 * Renders the sprite
	 * @param g - the Graphics context with appropriate translation and scaling
	 */
	public void draw(Graphics g) {
		if(!visible) return;
		g.translate((int)x, (int)y);
		sprite.draw(g, this);
		g.translate((int)-x, (int)-y);
	}
	
	/**
	 * Determines if a sprite can move into a given space
	 * @param dx - amount sprite is moving horizontally in pixels
	 * @param dy - amount sprite is moving vertically in pixels
	 * @param dx2 - amount other sprite is moving horizontally in pixels
	 * @param dy2 - amount other sprite is moving vertically in pixels
	 * @param obj - the other sprite to collide with
	 * @return Allowed dx, allowed dy, and 1 if a collision occurred
	 */
	public double[] checkCollision(double dx, double dy, double dx2, double dy2, GameObject obj) {
		int x1 = (int)(x+dx);
		int y1 = (int)(y+dy);
		int x2 = (int)(obj.x+dx2);
		int y2 = (int)(obj.y+dy2);
		boolean overlapx = (x1+w/2)>(x2-obj.w/2) && (x1-w/2)<(x2+obj.w/2);
		boolean overlapy = (y1+h/2)>(y2-obj.h/2) && (y1-h/2)<(y2+obj.h/2);
		if(overlapx) { // check y-collision
			if((y1+h/2)>(y2-obj.h/2) && (y1-h/2)>(y2+obj.h/2)) { // hit below
				
			}
		}
		return new double[]{dx, dy, 0};
	}
	
	/**
	 * Determines if a solid object is directly underneath this one
	 * @param game
	 * @return
	 */
	public boolean checkFloor(Game game) {
		for(GameObject sprite:game.sprites) {
			if(isCollidable(sprite)) {
				if(checkCollision(0, 1, 0, 0, sprite)[1]<1) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Move GameObject by a set distance
	 * @param dx - horizontal movement in pixels
	 * @param dy - vertical movement in pixels
	 */
	public void move(double dx, double dy) {
		x += dx;
		y += dy;
	}
	
	@Override
	public int compareTo(GameObject obj) {
		return z - obj.z;
	}
}