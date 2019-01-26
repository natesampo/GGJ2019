package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;

import game.GameObject.Animations;

public class Game {

	private JFrame frame;
	public Camera camera;
	public static final String NAME = "GAME JAM";
	public static final int WIDTH = 896; // window width pixels
	public static final int HEIGHT = 896; // window height pixels
	public static final int W = 12; // grid width
	public static final int H = 12; // grid height
	public static final int MAX_STEP = 50000000;
	public static final int FRAME_RATE = 12;
	public int MIN_STEP = 20000000;
	public int windowWidth;
	public int windowHeight;
	public boolean isRunning;
	public Ship player;

	public boolean yourTurn = false;
	public boolean lock = false;
	public int keyLock = 0;
	public static GameObject[][] grid = new GameObject[W][H];

	public int delete_this_variable = 0;
	public double test_local_time = 0;
	
	public static ArrayList<GameObject> sprites = new ArrayList<GameObject>();
	
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
		camera.goTo(-WIDTH/2, -HEIGHT/2);
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
			public void keyTyped(KeyEvent ke) {
			}

			@Override
			public void keyReleased(KeyEvent ke) {
				if(keyLock == ke.getKeyCode()) keyLock = 0;
			}

			@Override
			public void keyPressed(KeyEvent ke) {
				if(yourTurn && keyLock!=ke.getKeyCode()) takeTurn(ke);
				keyLock = ke.getKeyCode();
			}
		});

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
		loadLevel("1.1");
		while (isRunning) {
			now = System.nanoTime();
			dt = now - then;
			then = now;
			if (dt > 0) {
				update(Math.min(MAX_STEP, dt) / 1000000000.0);
			}
			frame.repaint();
			try {
				Thread.sleep(Math.max((MIN_STEP - (System.nanoTime() - then)) / 1000000, 0));
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Update the game model
	 * 
	 * @param dt
	 *            - elapsed time in seconds
	 */
	public void update(double dt) {
		if (lock)
			return;
		lock = true;
		// Put code for user interface before camera update, so slowdowns
		// don't affect UI elements.
		dt = camera.update(dt); // dt changes values here based on camera speed
		Collections.sort(sprites); // Draw in order of y position
		// Update GameObject graphics
		for (int i = 0; i < sprites.size(); i++) {
			sprites.get(i).update(this, dt);
		}
		yourTurn = true;
		lock = false;
	}

	/**
	 * Draw the graphics
	 * 
	 * @param g
	 *            - the game's Graphics context
	 */
	public void draw(Graphics g) {
		if (lock)
			return;
		lock = true;
		Graphics2D g2 = (Graphics2D) g;
		long xfoc = (int) (Math.sin(System.nanoTime() / 1000000000.0) * (500)) - 640;
		long yfoc = (int) (Math.cos(System.nanoTime() / 1500000000.0) * (320)) - 360;
		double zoom = Math.sin(System.nanoTime() / 2500000000.0) * 0.5 + 1.0;
		xfoc = -WIDTH / 2;
		yfoc = -HEIGHT / 2;
		zoom = 1.0;
		camera.set_target_pos(xfoc, yfoc);
		camera.zoom.set_target_value(zoom);
		g2.scale(camera.get_zoom(), camera.get_zoom());
		g2.translate((int) (camera.get_x_pos() + WIDTH / (2 * camera.get_zoom())),
				(int) (camera.get_y_pos() + HEIGHT / (2 * camera.get_zoom())));

		int sq_size = 80;
		int xs[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
		int ys[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
		for (int x : xs) {
			for (int y : ys) {
				if ((x + y) % 2 == 0) {
					g.setColor(Color.lightGray);
					g.fillRect(sq_size * x, sq_size * y, sq_size, sq_size);
				} else {
					g.setColor(Color.WHITE);
					g.fillRect(sq_size * x, sq_size * y, sq_size, sq_size);
				}
			}
		}
		for (GameObject sprite : sprites) {
			sprite.draw(g);
		}
		lock = false;
	}

	public void takeTurn(KeyEvent ke) {
		switch(ke.getKeyCode()) {
			case KeyEvent.VK_W:
			case KeyEvent.VK_UP:
				yourTurn = !player.moveUp();
				break;
			case KeyEvent.VK_S:
			case KeyEvent.VK_DOWN:
				yourTurn = !player.moveDown();
				break;
			case KeyEvent.VK_A:
			case KeyEvent.VK_LEFT:
				yourTurn = !player.moveLeft();
				break;
			case KeyEvent.VK_D:
			case KeyEvent.VK_RIGHT:
				yourTurn = !player.moveRight();
				break;
			case KeyEvent.VK_SPACE:
				yourTurn = false;
				player.shoot();
				break;
		}
	}

	/**
	 * Loads all animation files
	 */
	public static void loadAllAnimations() {
		for (Animations a : Animations.values()) {
			Sprite.loadAnimation(a);
		}
	}

	public void loadLevel(String name) {
		try {
			Scanner in = new Scanner(new FileReader(name + ".txt"));
			grid = new GameObject[W][H];
			int y = 0;
			while (in.hasNext()) {
				String s = in.nextLine();
				for (int x = 0; x < s.length(); x++) {
					char c = s.charAt(x);
					switch (c) {
					case 'E':
						sprites.add(new Ship(x, y, 0, 1));
						sprites.add(new Tile(x, y, 1));
						break;
					case 'S':
						player = new Ship(x, y, 0, 0);
						sprites.add(player);
						sprites.add(new Tile(x, y, 1));
						break;
					case '~':
						sprites.add(new Tile(x, y, 1));
						break;
					case 'x':
						sprites.add(new Tile(x, y, 1));
						sprites.add(new Tile(x, y, 2));
						break;
					case 'X':
						sprites.add(new Tile(x, y, 3));
						break;
					case 'm':
						sprites.add(new Tile(x, y, 4));
						break;
					case '>':
						sprites.add(new Tile(x, y, 5));
						break;
					case '<':
						sprites.add(new Tile(x, y, 6));
						break;
					case '^':
						sprites.add(new Tile(x, y, 7));
						break;
					case '/':
						sprites.add(new Tile(x, y, 8));
						break;
					}
				}
				y++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
