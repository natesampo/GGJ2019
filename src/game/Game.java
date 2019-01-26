package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JFrame;
import javax.swing.JPanel;

import game.GameObject.Animations;

public class Game {
	
	private JFrame frame;
	public Camera camera;
	public static final String NAME = "GAME JAM";
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 755;
	public static final int MAX_STEP = 50000000;
	public static final int FRAME_RATE = 12;
	public int MIN_STEP = 20000000;
	public int windowWidth;
	public int windowHeight;
	public boolean isRunning;
	public PlatformingObject player;
	
	public boolean leftPressed, rightPressed, upPressed, downPressed = false;
	
	public int delete_this_variable = 0;
	public double test_local_time = 0;
	
	public ArrayList<GameObject> sprites = new ArrayList<GameObject>();
	
	public static void main(String[] args) {
		Game game = new Game();
		game.start();
	}
	
	public Game() {
		this(WIDTH, HEIGHT);
	}
	
	@SuppressWarnings("serial")
	public Game(int windowWidth, int windowHeight) {
		loadAllAnimations();
		frame = new JFrame(NAME);
		camera = new Camera();
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;
		frame.setSize(windowWidth, windowHeight);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setResizable(false);
		frame.add(new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				draw(g);
			}
		});
		frame.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent ke) {
				switch(ke.getKeyCode()) {
				case KeyEvent.VK_UP: upPressed = true; break;
				case KeyEvent.VK_DOWN: downPressed = true; break;
				case KeyEvent.VK_LEFT: leftPressed = true; break;
				case KeyEvent.VK_RIGHT: rightPressed = true; break;
				}
			}

			@Override
			public void keyReleased(KeyEvent ke) {
				switch(ke.getKeyCode()) {
				case KeyEvent.VK_UP: upPressed = false; break;
				case KeyEvent.VK_DOWN: downPressed = false; break;
				case KeyEvent.VK_LEFT: leftPressed = false; break;
				case KeyEvent.VK_RIGHT: rightPressed = false; break;
				}
			}

			@Override
			public void keyTyped(KeyEvent ke) {
				// TODO Auto-generated method stub
				
			}
			
		});
		player = new PlatformingObject(640, 640);
		sprites.add(player);
//		sprites.add(new PlatformingObject(640, 640));
		sprites.add(new PlatformingObject(640+320, 640));
		sprites.add(new PlatformingObject(640+160, 640));
//		sprites.add(new PlatformingObject(640, 640+320));
//		sprites.add(new PlatformingObject(640+320, 640+160));
//		sprites.add(new PlatformingObject(640, 640+160));
		AudioPlayer music = new AudioPlayer("OpenSource.wav");
		music.play();
	}
	
	/**
	 * Start the timer
	 */
	public void start() {
		isRunning = true;
		long then = System.nanoTime();
		long now = then;
		long dt;
		while (isRunning) {
			now = System.nanoTime();
			dt = now - then;
			then = now;
			if (dt > 0) {
				update(Math.min(MAX_STEP, dt)/1000000000.0);
			}
			frame.repaint();
			try{
				Thread.sleep(Math.max((MIN_STEP - (System.nanoTime()-then))/1000000, 0));
			} catch (Exception e) {}
		}
	}

	/**
	 * Update the game model
	 * @param dt - elapsed time in seconds
	 */
	public void update(double dt) {
		// Put code for user interface before camera update, so slowdowns
		// don't affect UI elements.
		
		dt = camera.update(dt);	//	dt changes values here based on camera speed
		Collections.sort(sprites);
		// Update GameObjects and store desired movements
		double[] dx = new double[sprites.size()];
		double[] dy = new double[sprites.size()];
//		if(upPressed) player.up();
//		if(downPressed) player.down();
//		if(leftPressed) player.left();
//		if(rightPressed) player.right();
		for(int i=0; i<sprites.size(); i++) {
			double[] delta = sprites.get(i).update(this, dt);
			dx[i] = delta[0];
			dy[i] = delta[1];
		}
		// Collide GameObjects and carry out movements
		for(int i=0; i<sprites.size()-1; i++) {
			GameObject s1 = sprites.get(i);
			for(int j=i+1; j<sprites.size(); j++) {
				GameObject s2 = sprites.get(j);
				double[] delta = new double[]{0,0,0};
				if(s1.isCollidable(s2)) { // s2 can block s1
					delta = s1.checkCollision(dx[i], dy[i], dx[j], dy[j], s2);
					dx[i] = delta[0];
					dy[i] = delta[1];
				} else if(s2.isCollidable(s1)) { // s1 can block s2
					delta = s2.checkCollision(dx[j], dy[j], dx[i], dy[i], s1);
					dx[j] = delta[0];
					dy[j] = delta[1];
				} else if(s1.isInteractable(s2)||s2.isInteractable(s1)) { // no blocking
					delta = s2.checkCollision(dx[j], dy[j], dx[i], dy[i], s1);
				}
				if(delta[2]>0) { // collision occurred
					s2.collide(s1);
					s1.collide(s2);
				}
			}
			s1.move(dx[i], dy[i]);
		}
		sprites.get(sprites.size()-1).move(dx[sprites.size()-1], dy[sprites.size()-1]);
	}

	/**
	 * Draw the graphics
	 * @param g - the game's Graphics context
	 */
	public void draw(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		long xfoc = (int)(Math.sin(System.nanoTime()/1000000000.0)*(500)) - 640;
		long yfoc = (int)(Math.cos(System.nanoTime()/1500000000.0)*(320)) - 360;
		double zoom = Math.sin(System.nanoTime()/2500000000.0)*0.5 + 1.0;
		xfoc = -WIDTH/2;
		yfoc = -HEIGHT/2;
		zoom = 1.0;
		camera.set_target_pos(xfoc, yfoc);
		camera.zoom.set_target_value(zoom);
		g2.scale(camera.get_zoom(), camera.get_zoom());
		g2.translate((int)(camera.get_x_pos() + WIDTH/(2*camera.get_zoom())), 
				(int)(camera.get_y_pos() + HEIGHT/(2*camera.get_zoom())));
		
		int sq_size = 80;
		int xs[] = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
		int ys[] = {0, 1, 2, 3, 4, 5, 6, 7, 8};
		for (int x : xs) {
			for (int y : ys) {
				if ((x+y)%2 == 0) {
					g.setColor(Color.lightGray);
					g.fillRect(sq_size*x, sq_size*y, sq_size, sq_size);
				} else {
					g.setColor(Color.WHITE);
					g.fillRect(sq_size*x, sq_size*y, sq_size, sq_size);
				}
			}
		}
		for (GameObject sprite:sprites) {
			sprite.draw(g);
		}
	}
	
	/**
	 * Loads all animation files
	 */
	public static void loadAllAnimations() {
		for(Animations a:Animations.values()) {
			Sprite.loadAnimation(a);
		}
	}
}
