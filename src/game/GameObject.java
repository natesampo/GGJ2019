package game;

import java.awt.Graphics;

/**
 * A game element capable of displaying animations
 */
public abstract class GameObject implements Comparable<GameObject> {
	
	protected Sprite sprite;
	public int x, y;
	public double xreal, yreal;
	public double kspeed = 1;
	public int SCALE = 64;
	public int XOFFSET = 32, YOFFSET = 64;
	public boolean visible = true;
	
	/**
	 * Animations that any sprite can display
	 */
	public enum Animations {
		HYDRA("hydra", 4, 4),
		WATER("Water", 1, 1),
		RIGHT("ShipRight", 1, 1);
		
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
        if(this.y==obj.y) {
        	return 0;
        }
        return this.y>obj.y?1:-1;
    }
}