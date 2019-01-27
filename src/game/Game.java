package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;

import game.GameObject;
import game.GameObject.Animations;

public class Game {

	private JFrame frame;
	public Camera camera;
	public static final String NAME = "Lost Treasure";
	public static final int WIDTH = 1200; // window width pixels
	public static final int HEIGHT = 900; // window height pixels
	public static final int W = 12; // grid width
	public static final int H = 12; // grid height
	public static final int MAX_STEP = 50000000;
	public static final int FRAME_RATE = 12;
	public int MIN_STEP = 20000000;
	public int windowWidth;
	public int windowHeight;
	public boolean isRunning;
	public static Ship player;

	public boolean yourTurn = true;
	public boolean lock = false;
	public int keyLock = 0;
	public static GameObject[][] grid = new GameObject[W][H];

	public int delete_this_variable = 0;
	public double test_local_time = 0;

	public static ArrayList<GameObject> sprites = new ArrayList<GameObject>();
	public static ArrayList<Button> buttons = new ArrayList<Button>();

	public double startBars, loadTime;
	public String levelText = "";
	public String levelText2 = "";
	public Font pirateFont;

	public int progress = 1;

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
		camera.goTo(-WIDTH / 2, -HEIGHT / 2);
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
				if (keyLock == ke.getKeyCode())
					keyLock = 0;
			}

			@Override
			public void keyPressed(KeyEvent ke) {
				if (yourTurn && keyLock != ke.getKeyCode())
					takeTurn(ke);
				keyLock = ke.getKeyCode();
			}
		});
		frame.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent me) {
			}

			@Override
			public void mouseMoved(MouseEvent me) {
			}
		});
		frame.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent me) {
				int mouseX = me.getX();
				int mouseY = me.getY() - 25;
				for (Button button : buttons) {
					if (mouseX > button.x && mouseX < button.x + button.width && mouseY > button.y
							&& mouseY < button.y + button.height) {
						System.out.println("click");
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent me) {
			}

			@Override
			public void mouseExited(MouseEvent me) {
			}

			@Override
			public void mousePressed(MouseEvent me) {
			}

			@Override
			public void mouseReleased(MouseEvent me) {
			}
		});

		buttons.add(new Button(1050, 350, 128, 64, 0));
		buttons.add(new Button(1050, 425, 128, 64, 2));

		AudioPlayer music = new AudioPlayer("OpenSource.wav");
		music.play();
	}

	/**
	 * Start the timer
	 */
	public void start() {

		// font = Font.createFont(Font.TRUETYPE_FONT,
		// this.getClass().getClassLoader().getResourceAsStream("PiratesBay.ttf").deriveFont(50f);
		try {
			pirateFont = Font
					.createFont(Font.TRUETYPE_FONT,
							this.getClass().getClassLoader().getResourceAsStream("ConvincingPirate.ttf"))
					.deriveFont(26f);
		} catch (FontFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		isRunning = true;
		long then = System.nanoTime();
		long now = then;
		long dt;
		loadLevel(getLevel(progress));
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

		// print(dt);

		if (lock)
			return;
		lock = true;

		if (startBars >= 0 && System.currentTimeMillis() - loadTime > 3500) {
			startBars = startBars - dt * 150;
		}
		// Put code for user interface before camera update, so slowdowns
		// don't affect UI elements.
		dt = camera.update(dt); // dt changes values here based on camera speed
		for (int i = 0; i < sprites.size(); i++) {
			GameObject g = sprites.get(i);
			if (g.kill) {
				if (g instanceof Ship) {
					grid[g.x][g.y] = null;
					sprites.remove(g);
					i--;
				}
				if (g instanceof Tile) {
					sprites.remove(g);
				}
			}
		}
		Collections.sort(sprites); // Draw in order of y position
		// Update GameObject graphics
		for (int i = 0; i < sprites.size(); i++) {
			sprites.get(i).update(this, dt);
		}
		if (!yourTurn) {
			theirTurn();
			yourTurn = true;
		}
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
		xfoc = -WIDTH / 3;
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

		g2.scale(1 / camera.get_zoom(), 1 / camera.get_zoom());
		g2.translate((int) -(camera.get_x_pos() + WIDTH / (2 * camera.get_zoom())),
				(int) -(camera.get_y_pos() + HEIGHT / (2 * camera.get_zoom())));

		// black stuff
		if (startBars > 0) {
			g.setColor(Color.DARK_GRAY);
			g.fillRect(0, (int) startBars - 170 - 17, WIDTH, 170);
			g.fillRect(0, HEIGHT - (int) startBars, WIDTH, 170);

			g.setColor(Color.WHITE);
			g.setFont(pirateFont);

			g.drawString(levelText, 110, HEIGHT + 60 - (int) startBars);
			g.drawString(levelText2, 110, HEIGHT + 100 - (int) startBars);
		}

		for (Button button : buttons) {
			button.draw(g);
		}

		lock = false;
	}

	public void takeTurn(KeyEvent ke) {
		if (player.kill)
			return;
		switch (ke.getKeyCode()) {
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

	public void theirTurn() {
		for (int i = 0; i < sprites.size(); i++) {
			GameObject g = sprites.get(i);
			if (g instanceof Ship && !g.equals(player)) {
				((Ship) g).move();
			}
		}
	}

	public void loadLevel(String name) {

		loadTime = System.currentTimeMillis();

		try {
			Scanner in = new Scanner(new FileReader(name + ".txt"));
			grid = new GameObject[W][H];
			int y = 0;
			while (in.hasNext() && y < 12) {
				String s = in.nextLine();
				for (int x = 0; x < s.length(); x++) {
					char c = s.charAt(x);
					switch (c) {
					case '1':
						sprites.add(new Ship(x, y, 0, 1));
						sprites.add(new Tile(x, y, 1));
						break;
					case '2':
						sprites.add(new Ship(x, y, 90, 1));
						sprites.add(new Tile(x, y, 1));
						break;
					case '3':
						sprites.add(new Ship(x, y, 180, 1));
						sprites.add(new Tile(x, y, 1));
						break;
					case '4':
						sprites.add(new Ship(x, y, 270, 1));
						sprites.add(new Tile(x, y, 1));
						break;
					case 'q':
						sprites.add(new Ship(x, y, 0, 2));
						sprites.add(new Tile(x, y, 1));
						break;
					case 'w':
						sprites.add(new Ship(x, y, 90, 2));
						sprites.add(new Tile(x, y, 1));
						break;
					case 'e':
						sprites.add(new Ship(x, y, 180, 2));
						sprites.add(new Tile(x, y, 1));
						break;
					case 'r':
						sprites.add(new Ship(x, y, 270, 2));
						sprites.add(new Tile(x, y, 1));
						break;
					case 'a':
						sprites.add(new Ship(x, y, 0, 3));
						sprites.add(new Tile(x, y, 1));
						break;
					case 's':
						sprites.add(new Ship(x, y, 90, 3));
						sprites.add(new Tile(x, y, 1));
						break;
					case 'd':
						sprites.add(new Ship(x, y, 180, 3));
						sprites.add(new Tile(x, y, 1));
						break;
					case 'f':
						sprites.add(new Ship(x, y, 270, 3));
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
						sprites.add(new Tile(x, y, 1));
						break;
					case 'm':
						sprites.add(new Tile(x, y, 4));
						sprites.add(new Tile(x, y, 1));
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
					case '_':
						sprites.add(new Tile(x, y, 9));
						break;
					default:
						// if (levelText == null) {
						// levelText = c+ "";
						// } else {
						// levelText = levelText + c;
						// }
						break;
					}
				}
				y++;
			}
			if (in.hasNext()) {
				levelText = in.nextLine();
			}
			if (in.hasNext()) {
				levelText2 = in.nextLine();
			}

			System.out.println(levelText);
			startBars = 170;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public String getLevel(int lvNum) {

		switch (lvNum) {

		case 1:
			return ("1.1");
		case 2:
			return ("1.2");
		case 3:
			return ("1.3");
		case 4:
			return ("1.4");
		case 5:
			return ("2.1");
		case 6:
			return ("2.2");
		case 7:
			return ("3.1");
		case 8:
			return ("3.2");
		case 9:
			return ("3.3");
		case 10:
			return ("4.1");
		case 11:
			return ("4.2");
		case 12:
			return ("4.3");
		default:
			return ("");

		}

	}
}
