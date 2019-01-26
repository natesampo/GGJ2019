package game;

import java.awt.Graphics;
import java.awt.Image;
import java.util.HashMap;

import game.GameObject.Animations;

/**
 * Handles animation of GameObjects
 */
public class Sprite {
	
	public int framerate = Game.FRAME_RATE, width, height;
	public double duration;
	private static HashMap<GameObject.Animations, SpriteSheet[]> animations = new HashMap<GameObject.Animations, SpriteSheet[]>();
	private Image frame;
	private GameObject.Animations state;
	private double t;
	private int offset = 0;
	public boolean flipX = false, flipY = false;
	
	/**
	 * Adds a sprite sheet to the dictionary
	 * @param a - The animation to load
	 */
	public static void loadAnimation(GameObject.Animations a) {
		int i = a.filename.indexOf(".");
		String name = i>0?a.filename.substring(0, a.filename.indexOf(".")):a.filename;
		if(animations.containsKey(name)) {
			return; // duplicate detection
		}
		if(name.contains("Ship")) {
			SpriteSheet[] all = new SpriteSheet[4];
			all[0] = new SpriteSheet(i>0?a.filename:name+"Right.png", a.columns, a.frames);
			all[1] = new SpriteSheet(i>0?a.filename:name+"Up.png", a.columns, a.frames);
			all[2] = new SpriteSheet(i>0?a.filename:name+"Left.png", a.columns, a.frames);
			all[3] = new SpriteSheet(i>0?a.filename:name+"Down.png", a.columns, a.frames);
			animations.put(a, all);
			return;
		}
		if(name.contains("Enemy")) {
			SpriteSheet[] all = new SpriteSheet[4];
			all[0] = new SpriteSheet(i>0?a.filename:name+"Right.png", a.columns, a.frames);
			all[1] = new SpriteSheet(i>0?a.filename:name+"Up.png", a.columns, a.frames);
			all[2] = new SpriteSheet(i>0?a.filename:name+"Left.png", a.columns, a.frames);
			all[3] = new SpriteSheet(i>0?a.filename:name+"Down.png", a.columns, a.frames);
			animations.put(a, all);
			return;
		}
		if(name.contains("Fire")) {
			SpriteSheet[] all = new SpriteSheet[4];
			all[0] = new SpriteSheet(i>0?a.filename:name+"LeftStarboard.png", a.columns, a.frames);
			all[1] = new SpriteSheet(i>0?a.filename:name+"Up.png", a.columns, a.frames);
			all[2] = new SpriteSheet(i>0?a.filename:name+"LeftPort.png", a.columns, a.frames);
			all[3] = new SpriteSheet(i>0?a.filename:name+"Down.png", a.columns, a.frames);
			animations.put(a, all);
			return;
		}
		SpriteSheet[] all = new SpriteSheet[1];
		all[0] = new SpriteSheet(i>0?a.filename:name+".png", a.columns, a.frames);
		animations.put(a, all);
	}
	
	/**
	 * Updates the animation by a specified time step
	 * @param state - the desired animation
	 * @param dt - the time step in seconds
	 * @return true if the animation completed
	 */
	public boolean animate(GameObject.Animations state, double dt, int index) {
		if(!animations.containsKey(state)) {
			System.err.println("Animation not found: "+state);
			return false; // Error detection
		}
		t += dt; // Update elapsed time
		if(!state.equals(this.state)) {
			t = 0;
			this.state = state; // Change animations
		}
		SpriteSheet[] all = animations.get(state);
		if(all.length<=index) {
			System.err.println("Animation index not found: "+state);
			return false;
		}
		SpriteSheet animation = all[index];
		width = animation.width;
		height = animation.height;
		duration = 1.0*animation.frames/framerate;
		frame = animation.getFrame((int)(t*framerate+offset)%animation.frames);
		if(t >= duration) {
			t = 0; // Loop animation
			return true;
		} else {
			return false;
		}
	}

	public boolean animate(GameObject.Animations state, double dt) {
		return animate(state, dt, 0);
	}
	
	public void offset(int frames) {
		offset = frames;
	}
	
	/**
	 * Render the sprite centered at (0,0)
	 * @param g - the Graphics context centered on the sprite
	 * @param object - the GameObject possessing the sprite
	 */
	public void draw(Graphics g, GameObject object) {
		if(frame==null||state==null) return; // error detection
		g.drawImage(frame, -width/2*(flipX?-1:1), -height/2*(flipY?-1:1), width*(flipX?-1:1), height*(flipY?-1:1), null);
	}
}