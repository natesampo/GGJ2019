package game;

import java.awt.Graphics;

/**
 * A game element capable of displaying animations
 */
public abstract class GameObject implements Comparable<GameObject> {
	
	protected Sprite sprite;
	public int x, y, z=1;
	public double xreal, yreal;
	public double kspeed = .2, minspeed = .02;
	static int SCALE = 64;
	static int XOFFSET = 32, YOFFSET = 64;
	public boolean visible = true;
	public boolean kill = false;
	
	/**
	 * Animations that any sprite can display
	 */
	public enum Animations {
		HYDRA("hydra", 4, 4),
		WATER("Water", 8, 8),
		SHIP("Ship", 4, 4),
		ROCK1("Rock1", 1, 1),
		ROCK2("Rock2", 1, 1),
		ROCK3("Rock3", 1, 1),
		BOOM("Fire", 12, 12),
		ENEMY1("Enemy1", 4, 4),
		ENEMY2("Enemy2", 4, 4),
		ENEMY3("Enemy3", 4, 4),
		CURRENT("Current", 4, 4),
		MINE("Mine", 8, 8);
		
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
	 */
	public GameObject(int x, int y) {
		this.sprite = new Sprite();
		this.x = x;
		this.y = y;
		this.xreal = x;
		this.yreal = y;
	}
	
	/**
	 * Updates the sprite behavior and animation
	 * @param dt - elapsed time in seconds
	 * @return An array containing horizontal and vertical displacement in pixels
	 */
	public abstract void update(Game game, double dt);
	
	/**
	 * Renders the sprite
	 * @param g - the Graphics context with appropriate translation and scaling
	 */
	public void draw(Graphics g) {
		if(!visible) return;
		g.translate((int)(xreal*SCALE)+SCALE/2+XOFFSET, (int)(yreal*SCALE)+SCALE/2+YOFFSET);
		sprite.draw(g, this);
		g.translate(-(int)(xreal*SCALE)-SCALE/2-XOFFSET, -(int)(yreal*SCALE)-SCALE/2-YOFFSET);
	}
	
	@Override
    public int compareTo(GameObject obj) {
		if(obj.z == 0) {
			return this.z>0?1:0;
		}
		if(this.z == 0) {
			return obj.z>0?-1:0;
		}
        if(this.y==obj.y) {
        	if(this.z==obj.z) {
        		return 0;
        	}
        	return this.z>obj.z?1:-1;
        }
        return this.y>obj.y?1:-1;
    }
}