package game;

import java.awt.AlphaComposite;
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

import game.GameObject.Animations;

public class Game {

	private JFrame frame;
	public Camera camera;
	public static final String NAME = "Broadside";
	public static final int WIDTH = 1200; // window width pixels
	public static final int HEIGHT = 900; // window height pixels
	public static final int W = 12; // grid width
	public static final int H = 12; // grid height
	public static final int MAX_STEP = 50000000;
	public static final int FRAME_RATE = 12;
	public static final int mineCost = 5;
	public static final int ramCost = 5;
	public static final int damageCost = 4;
	public static final int rangeCost = 2;
	public static final int actionCost = 5;
	public int MIN_STEP = 20000000;
	public int windowWidth;
	public int windowHeight;
	public boolean isRunning;
	public static Ship player;
	private SpriteSheet hpbar, hpbarheart, gold;

	public String highlight = "0";
	public boolean yourTurn = true;
	public double delay = 5;
	public boolean lock = true;
	public int keyLock = 0;
	public static GameObject[][] grid = new GameObject[W][H];
	public static int[][] currentGrid = new int[W][H];
	public MapShip mapShip;
	public boolean begun = false;

	public int delete_this_variable = 0;
	public double test_local_time = 0;
	public float mapAlpha = 1;
	public double t = 0;
	public int win = 0;
	public int[][] waypoints = new int[][]{{150,170},{251,207},{419,215},{575,220},
		{690,170},{820,150},{882,268},
		{882,392}, {739,400}, {577, 407}, 
		{363,430}, {250,530}, {330,664}, {569,639}, {867,652}};

	public static ArrayList<GameObject> sprites = new ArrayList<GameObject>();
	public static ArrayList<Button> buttons = new ArrayList<Button>();

	public double startBars, loadTime;
	public String levelText = "";
	public String levelText2 = "";
	public String levelText3 = "";
	public Font pirateFont, pirateFontBig, pirateFontGold, pirateFontMed;
	public SpriteSheet map, background, splash;

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
				if(!begun) {
					begun = true;
					return;
				}
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
				int mouseX = me.getX();
				int mouseY = me.getY() - 25;
				
				highlight = "";
				for (int i=buttons.size()-1; i>=0; i--) {
					Button button = buttons.get(i);
					if (mouseX > button.x && mouseX < button.x + button.width && mouseY > button.y
							&& mouseY < button.y + button.height) {
						switch(button.onClick) {
							case 0:
								highlight = "port";
								break;
							case 1:
								highlight = "port";
								break;
							case 2:
								highlight = "starboard";
								break;
							case 3:
								highlight = "starboard";
								break;
							case 5:
								highlight = "mine";
								break;
						}
					}
				}
			}
		});
		frame.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent me) {
			}

			@Override
			public void mouseEntered(MouseEvent me) {
			}

			@Override
			public void mouseExited(MouseEvent me) {
			}

			@Override
			public void mousePressed(MouseEvent me) {
				int mouseX = me.getX();
				int mouseY = me.getY() - 25;
				
				System.out.println("mouseX: " + mouseX);
				System.out.println("mouseY: " + mouseY);
				
				for (int i=buttons.size()-1; i>=0; i--) {
					Button button = buttons.get(i);
					if (mouseX > button.x && mouseX < button.x + button.width && mouseY > button.y
							&& mouseY < button.y + button.height) {
						if(delay>0) return;
						switch(button.onClick) {
							case 0:
								if (yourTurn) {
									player.actionsLeft--;
									player.portLoaded = true;
									buttons.add(new Button(1050, 350, 128, 64, 1));
									buttons.remove(i);
									
									if(player.actionsLeft <= 0) {
										yourTurn = false;
										player.followCurrent();
									}
								}
								break;
							case 1:
								if (yourTurn) {
									player.actionsLeft--;
									player.shoot("port");
									buttons.add(new Button(1050, 350, 128, 64, 0));
									buttons.remove(i);
									
									if(player.actionsLeft <= 0) {
										yourTurn = false;
										player.followCurrent();
									}
								}
								break;
							case 2:
								if (yourTurn) {
									player.actionsLeft--;
									player.starboardLoaded = true;
									buttons.add(new Button(1050, 425, 128, 64, 3));
									buttons.remove(i);
									
									if(player.actionsLeft <= 0) {
										yourTurn = false;
										player.followCurrent();
									}
								}
								break;
							case 3:
								if (yourTurn) {
									player.actionsLeft--;
									player.shoot("starboard");
									buttons.add(new Button(1050, 425, 128, 64, 2));
									buttons.remove(i);
									
									if(player.actionsLeft <= 0) {
										yourTurn = false;
										player.followCurrent();
									}
								}
								break;
							case 5:
								if (yourTurn) {								
									int x = player.x;
									int y = player.y;
									switch(player.heading) {
										case 0: x--; break;
										case 90: y++; break;
										case 180: x++; break;
										case 270: y--; break;
									}
									if(player.collideNoShips(x, y)) {
										break;
									}
									player.actionsLeft--;
									sprites.add(new Tile(x, y, 4));
									
									if(player.actionsLeft <= 0) {
										yourTurn = false;
										player.followCurrent();
									}
								}
								break;
							case 6:
								buttons.add(new Button(40, 134, 64, 32, 7));
								buttons.add(new Button(10, 180, 128, 64, 8));
								buttons.add(new Button(10, 258, 128, 64, 9));
								buttons.add(new Button(10, 336, 128, 64, 10));
								buttons.add(new Button(10, 414, 128, 64, 11));
								buttons.add(new Button(10, 492, 128, 64, 12));
								buttons.remove(i);
								break;
							case 7:
								for (int j=buttons.size()-1; j>=0; j--) {
									if (buttons.get(j).onClick == 8 || buttons.get(j).onClick == 9 || buttons.get(j).onClick == 10) {
										buttons.remove(j);
										
										if (j < i) {
											i--;
										}
									}
								}
								
								buttons.add(new Button(40, 134, 64, 32, 6));
								buttons.remove(i);
								break;
							case 8:
								if (player.gold >= damageCost) {
									player.gold -= damageCost;
									player.damage += 1;
								}
								break;
							case 9:
								if (player.gold >= ramCost) {
									player.gold -= ramCost;
									player.ram = true;
									buttons.remove(i);
								}
								break;
							case 10:
								if (player.gold >= mineCost) {
									player.gold -= mineCost;
									
									for (int j=0; j<buttons.size(); j++) {
										if (buttons.get(j).onClick == 4) {
											buttons.remove(j);
											
											if (j < i) {
												i--;
											}
											break;
										}
									}
									
									buttons.add(new Button(1050, 535, 128, 64, 5));
									buttons.remove(i);
								}
								break;
							case 11:
								if (player.gold >= rangeCost) {
									player.gold -= rangeCost;
									player.damage += 1;
								}
								break;
							case 12:
								if (player.gold >= actionCost) {
									player.gold -= actionCost;
									player.actions += 1;
								}
								break;
						}
					}
				}
			}

			@Override
			public void mouseReleased(MouseEvent me) {
			}
		});

		hpbar = new SpriteSheet("hp_bar.png", 1, 1);
		hpbarheart = new SpriteSheet("hp_bar_heart.png", 1, 1);
		gold = new SpriteSheet("Doubloon.png", 1, 1);
		AudioPlayer music = new AudioPlayer("Broadside.wav");
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
		
		try {
			pirateFontMed = Font
					.createFont(Font.TRUETYPE_FONT,
							this.getClass().getClassLoader().getResourceAsStream("ConvincingPirate.ttf"))
					.deriveFont(50f);
		} catch (FontFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			pirateFontBig = Font
					.createFont(Font.TRUETYPE_FONT,
							this.getClass().getClassLoader().getResourceAsStream("ConvincingPirate.ttf"))
					.deriveFont(85f);
		} catch (FontFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			pirateFontGold = Font
					.createFont(Font.TRUETYPE_FONT,
							this.getClass().getClassLoader().getResourceAsStream("ConvincingPirate.ttf"))
					.deriveFont(64f);
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
		mapShip = new MapShip(waypoints[0][0], waypoints[0][1]);
		background = new SpriteSheet("background.png", 1, 1);
		splash = new SpriteSheet("splash_screen.png", 1, 1);
		loadLevel("1.4");
		Bar();
		lock = false;
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
		if(!begun) return;

		// print(dt);

		if (lock)
			return;
		lock = true;
		t += dt;
		delay -= dt;
		if(delay<0) delay = 0;
		if(t>2) mapAlpha-=dt*5;
		if(mapAlpha<0.3) mapAlpha = 0;
		if (startBars >= 0 && System.currentTimeMillis() - loadTime > 5500) {
			startBars = startBars - dt * 550;
		}
		// Put code for user interface before camera update, so slowdowns
		// don't affect UI elements.
		dt = camera.update(dt); // dt changes values here based on camera speed
		for (int i = 0; i < sprites.size(); i++) {
			GameObject g = sprites.get(i);
			if(g==null) return;
			if (g.kill) {
				if (g instanceof Ship) {
					player.gold += ((Ship) g).gold;
					grid[g.x][g.y] = null;
					sprites.remove(g);
					i--;
				}
				if (g instanceof Tile) {
					sprites.remove(g);
				}
			}
		}
		mapShip.update(this, dt);
		Collections.sort(sprites); // Draw in order of y position
		// Update GameObject graphics
		for (int i = 0; i < sprites.size(); i++) {
			sprites.get(i).update(this, dt);
		}
		if (!yourTurn) {
			if(delay==0) {
				theirTurn();
				delay = 0.15;
			}
		}
		// Change level
		if(win==1&&delay==0) {
			win = 0;
			progress++;
			loadLevel(getLevel(progress));
			Bar();
			delay = 5;
		}
		if(win==-1&&delay==0) {
			win = 0;
			loadLevel(getLevel(progress));
		}
		if(player.kill&&win==0) {
			delay = 1;
			win = -1;
		} else if(enemyCount()==0&&win==0) {
			win = 1;
			delay = 1;
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
		if(!begun) {
			if(splash==null) return;
			g.drawImage(splash.getFrame(0),0,0,WIDTH,HEIGHT,null);
			if(System.currentTimeMillis()%1000>500) {
				g.setColor(Color.BLACK);
				g.setFont(pirateFontMed);
				g.drawString("Press any key", 20, 60);
				g.drawString("to begin", 20, 120);
			}
			return;
		}
		if (lock)
			return;
		lock = true;
		g.drawImage(background.getFrame(0),0,-15,WIDTH,HEIGHT,null);
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
//		for (int x : xs) {
//			for (int y : ys) {
//				if ((x + y) % 2 == 0) {
//					g.setColor(Color.lightGray);
//					g.fillRect(sq_size * x, sq_size * y, sq_size, sq_size);
//				} else {
//					g.setColor(Color.WHITE);
//					g.fillRect(sq_size * x, sq_size * y, sq_size, sq_size);
//				}
//			}
//		}
		
		for (GameObject sprite : sprites) {
			if(sprite!=null && sprite instanceof Tile) sprite.draw(g);
		}
		
		switch(highlight) {
			case "port":
				g.translate((int)(player.xreal*GameObject.SCALE)+GameObject.SCALE/2+GameObject.XOFFSET, (int)(player.yreal*GameObject.SCALE)+GameObject.SCALE/2+GameObject.YOFFSET);
				
				if (player.heading == 0) {
					for (int i=0; i<=Math.min(player.range-1, player.y-2); i++) {
						g.setColor(new Color(255, 0, 0, 55));
						g.fillRect(-16 - GameObject.XOFFSET, -16 - GameObject.SCALE*(i+1), GameObject.SCALE, GameObject.SCALE);
						
						if ((grid[player.x][player.y - i - 1] instanceof Tile && ((Tile) grid[player.x][player.y - i - 1]).type == 3) || (grid[player.x][player.y - i - 1] instanceof Ship)) {
							break;
						}
					}
				} else if (player.heading == 90) {
					for (int i=0; i<=Math.min(player.range-1, player.x-1); i++) {
						g.setColor(new Color(255, 0, 0, 55));
						g.fillRect(-16 - GameObject.XOFFSET - GameObject.SCALE*(i+1), -16, GameObject.SCALE, GameObject.SCALE);
						
						if ((grid[player.x - i - 1][player.y] instanceof Tile && ((Tile) grid[player.x - i - 1][player.y]).type == 3) || (grid[player.x - i - 1][player.y] instanceof Ship)) {
							break;
						}
					}
				} else if (player.heading == 180) {
					for (int i=0; i<=Math.min(player.range-1, H-player.y-2); i++) {
						g.setColor(new Color(255, 0, 0, 55));
						g.fillRect(-16 - GameObject.XOFFSET, -16 + GameObject.SCALE*(i+1), GameObject.SCALE, GameObject.SCALE);
						
						if ((grid[player.x][player.y + i + 1] instanceof Tile && ((Tile) grid[player.x][player.y + i + 1]).type == 3) || (grid[player.x][player.y + i + 1] instanceof Ship)) {
							break;
						}
					}
				} else if (player.heading == 270) {
					for (int i=0; i<=Math.min(player.range-1, W-player.x-2); i++) {
						g.setColor(new Color(255, 0, 0, 55));
						g.fillRect(-16 - GameObject.XOFFSET + GameObject.SCALE*(i+1), -16, GameObject.SCALE, GameObject.SCALE);
						
						if ((grid[player.x + i + 1][player.y] instanceof Tile && ((Tile) grid[player.x + i + 1][player.y]).type == 3) || (grid[player.x + i + 1][player.y] instanceof Ship)) {
							break;
						}
					}
				}
				
				g.translate(-(int)(player.xreal*GameObject.SCALE)-GameObject.SCALE/2-GameObject.XOFFSET, -(int)(player.yreal*GameObject.SCALE)-GameObject.SCALE/2-GameObject.YOFFSET);
				break;
			case "starboard":
				g.translate((int)(player.xreal*GameObject.SCALE)+GameObject.SCALE/2+GameObject.XOFFSET, (int)(player.yreal*GameObject.SCALE)+GameObject.SCALE/2+GameObject.YOFFSET);
				
				if (player.heading == 0) {
					for (int i=0; i<=Math.min(player.range-1, H-player.y-2); i++) {
						g.setColor(new Color(255, 0, 0, 55));
						g.fillRect(-16 - GameObject.XOFFSET, -16 + GameObject.SCALE*(i+1), GameObject.SCALE, GameObject.SCALE);
						
						if ((grid[player.x][player.y + i + 1] instanceof Tile && ((Tile) grid[player.x][player.y + i + 1]).type == 3) || (grid[player.x][player.y + i + 1] instanceof Ship)) {
							break;
						}
					}
				} else if (player.heading == 90) {
					for (int i=0; i<=Math.min(player.range-1, W-player.x-2); i++) {
						g.setColor(new Color(255, 0, 0, 55));
						g.fillRect(-16 - GameObject.XOFFSET + GameObject.SCALE*(i+1), -16, GameObject.SCALE, GameObject.SCALE);
						
						if ((grid[player.x + i + 1][player.y] instanceof Tile && ((Tile) grid[player.x + i + 1][player.y]).type == 3) || (grid[player.x + i + 1][player.y] instanceof Ship)) {
							break;
						}
					}
				} else if (player.heading == 180) {
					for (int i=0; i<=Math.min(player.range-1, player.y-2); i++) {
						g.setColor(new Color(255, 0, 0, 55));
						g.fillRect(-16 - GameObject.XOFFSET, -16 - GameObject.SCALE*(i+1), GameObject.SCALE, GameObject.SCALE);
						
						if ((grid[player.x][player.y - i - 1] instanceof Tile && ((Tile) grid[player.x][player.y - i - 1]).type == 3) || (grid[player.x][player.y - i - 1] instanceof Ship)) {
							break;
						}
					}
				} else if (player.heading == 270) {
					for (int i=0; i<=Math.min(player.range-1, player.x-1); i++) {
						g.setColor(new Color(255, 0, 0, 55));
						g.fillRect(-16 - GameObject.XOFFSET - GameObject.SCALE*(i+1), -16, GameObject.SCALE, GameObject.SCALE);
						
						if ((grid[player.x - i - 1][player.y] instanceof Tile && ((Tile) grid[player.x - i - 1][player.y]).type == 3) || (grid[player.x - i - 1][player.y] instanceof Ship)) {
							break;
						}
					}
				}
				
				g.translate(-(int)(player.xreal*GameObject.SCALE)-GameObject.SCALE/2-GameObject.XOFFSET, -(int)(player.yreal*GameObject.SCALE)-GameObject.SCALE/2-GameObject.YOFFSET);
				break;
			case "mine":
				g.translate((int)(player.xreal*GameObject.SCALE)+GameObject.SCALE/2+GameObject.XOFFSET, (int)(player.yreal*GameObject.SCALE)+GameObject.SCALE/2+GameObject.YOFFSET);
				
				if (player.heading == 0) {
					g.setColor(new Color(255, 0, 0, 55));
					g.fillRect(-GameObject.XOFFSET - GameObject.SCALE, -16, GameObject.SCALE, GameObject.SCALE);
				} else if (player.heading == 90) {
					g.setColor(new Color(255, 0, 0, 55));
					g.fillRect(-GameObject.XOFFSET, -16 + GameObject.SCALE, GameObject.SCALE, GameObject.SCALE);
				} else if (player.heading == 180) {
					g.setColor(new Color(255, 0, 0, 55));
					g.fillRect(-GameObject.XOFFSET + GameObject.SCALE, -16, GameObject.SCALE, GameObject.SCALE);
				} else if (player.heading == 270) {
					g.setColor(new Color(255, 0, 0, 55));
					g.fillRect(-GameObject.XOFFSET, -16 - GameObject.SCALE, GameObject.SCALE, GameObject.SCALE);
				}
				
				g.translate(-(int)(player.xreal*GameObject.SCALE)-GameObject.SCALE/2-GameObject.XOFFSET, -(int)(player.yreal*GameObject.SCALE)-GameObject.SCALE/2-GameObject.YOFFSET);
				break;
		}
		
		for (GameObject sprite : sprites) {
			if(sprite!=null && !(sprite instanceof Tile)) sprite.draw(g);
		}

		g2.scale(1 / camera.get_zoom(), 1 / camera.get_zoom());
		g2.translate((int) -(camera.get_x_pos() + WIDTH / (2 * camera.get_zoom())),
				(int) -(camera.get_y_pos() + HEIGHT / (2 * camera.get_zoom())));
		
		for (Button button : buttons) {
			button.draw(g);
		}
		
		try {
			g.drawImage(hpbar.getFrame(0), 8, 8, null);
			g.drawImage(gold.getFrame(0), 8, 68, null);
		} catch (Exception e) {
		}
		
		if (player != null) {
			for (int i=0; i<player.health; i++) {
				g.drawImage(hpbarheart.getFrame(0), 102 + 50*i, 8, null);
			}
			
			g.setColor(new Color(120, 120, 120));
			g.setFont(pirateFontGold);
			g.drawString("" + player.gold, 78, 114);
		}
		
		// black stuff
		if (startBars > 0) {
			g.setColor(Color.DARK_GRAY);
			g.fillRect(0, (int) startBars - 170 - 17, WIDTH, 170);
			g.fillRect(0, HEIGHT - (int) startBars, WIDTH, 170);

			g.setColor(Color.WHITE);
			g.setFont(pirateFont);

			g.drawString(levelText, 110, HEIGHT + 60 - (int) startBars);
			g.drawString(levelText2, 110, HEIGHT + 100 - (int) startBars);
			
			g.setFont(pirateFontBig);

			g.drawString(levelText3, 110, -62 + (int)startBars);
		}
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,mapAlpha);
		((Graphics2D) g).setComposite(ac);
		g.drawImage(map.getFrame(0),0,-15,WIDTH,HEIGHT,null);
		mapShip.draw(g2);
		lock = false;
	}

	public void takeTurn(KeyEvent ke) {
		if (player.kill ||!yourTurn || delay>0)
			return;
		switch (ke.getKeyCode()) {
			case KeyEvent.VK_W:
			case KeyEvent.VK_UP:
				if (player.moveUp()) player.actionsLeft--;
				break;
			case KeyEvent.VK_S:
			case KeyEvent.VK_DOWN:
				if (player.moveDown()) player.actionsLeft--;
				break;
			case KeyEvent.VK_A:
			case KeyEvent.VK_LEFT:
				if (player.moveLeft()) player.actionsLeft--;
				break;
			case KeyEvent.VK_D:
			case KeyEvent.VK_RIGHT:
				if (player.moveRight()) player.actionsLeft--;
				break;
			case KeyEvent.VK_SPACE:
				if (player.portLoaded) {
					player.actionsLeft--;
					player.shoot("port");
					player.portLoaded = false;
					
					for (int i=0; i<buttons.size(); i++) {
						if (buttons.get(i).onClick == 1) {
							buttons.remove(i);
							buttons.add(new Button(1050, 350, 128, 64, 0));
							break;
						}
					}
				}
				if (player.starboardLoaded) {
					player.actionsLeft--;
					player.shoot("starboard");
					player.starboardLoaded = false;
					
					for (int i=0; i<buttons.size(); i++) {
						if (buttons.get(i).onClick == 3) {
							buttons.remove(i);
							buttons.add(new Button(1050, 425, 128, 64, 2));
							break;
						}
					}
				}
				break;
				
			case KeyEvent.VK_P:
				progress++;
				loadLevel(getLevel(progress));
				break;
		}
		if(player.actionsLeft <= 0) {
			yourTurn = false;
			player.followCurrent();
			delay = .5;
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
		yourTurn = true;
		for (int i = 0; i < sprites.size(); i++) {
			GameObject g = sprites.get(i);
			if (g instanceof Ship && !g.equals(player)) {
				if(((Ship) g).actionsLeft>0) {
					((Ship) g).move();
					((Ship) g).actionsLeft--;
				}
				if(((Ship) g).actionsLeft>0) {
					yourTurn = false;
				}
			}
		}
		if(yourTurn) {
			for (int i = 0; i < sprites.size(); i++) {
				GameObject g = sprites.get(i);
				if (g instanceof Ship && !g.equals(player)) {
					((Ship) g).followCurrent();
					((Ship) g).actionsLeft = ((Ship) g).actions;
				}
			}
			player.actionsLeft = player.actions;
		}
	}

	public void loadLevel(String name) {
		mapShip.x = waypoints[progress][0];
		mapShip.y = waypoints[progress][1];
		System.out.println(progress);
		map = new SpriteSheet("Level"+(progress)+"Map.png", 1, 1);
		mapAlpha = 1;
		t = 0;
		loadTime = System.currentTimeMillis();

		try {
			Scanner in = new Scanner(new FileReader(name + ".txt"));
			sprites.clear();
			grid = new GameObject[W][H];
			currentGrid = new int[W][H];
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
						int tempGold = 0;
						boolean tempMines = false, tempRam = false;
						if (player!=null) {
							tempGold = player.gold;
							tempMines = player.mines;
							tempRam = player.ram;
						}
						player = new Ship(x, y, 0, 0);
						player.gold = tempGold;
						player.ram = tempRam;
						sprites.add(player);
						sprites.add(new Tile(x, y, 1));
						buttons = new ArrayList<Button>();
						buttons.add(new Button(1050, 350, 128, 64, 0));
						buttons.add(new Button(1050, 425, 128, 64, 2));
						
						if (tempMines) {
							buttons.add(new Button(1050, 535, 128, 64, 5));
						} else {
							buttons.add(new Button(1050, 535, 128, 64, 4));
						}
						
						buttons.add(new Button(40, 134, 64, 32, 6));
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
						sprites.add(new Current(x, y, 0));
						break;
					case '<':
						sprites.add(new Current(x, y, 180));
						break;
					case '^':
						sprites.add(new Current(x, y, 90));
						break;
					case '/':
						sprites.add(new Current(x, y, 270));
						break;
//					case '_':
//						sprites.add(new Tile(x, y, 9));
//						break;
					default:
						// if (levelText == null) {
						// levelText = c+ "";
						// } else {
						// levelText = levelText + c;
						// }
						sprites.add(new Tile(x, y, 1));
						break;
					}
				}
				y++;
			}
			if (in.hasNext()) {
				levelText = in.nextLine();
			}
			else
			{
				levelText = "";
			}
			if (in.hasNext()) {
				levelText2 = in.nextLine();
			}
			else
			{
				levelText2 = "";
			}
			if (in.hasNext()) {
				levelText3 = in.nextLine();
			}
			else
			{
				levelText3 = "";
			}

			System.out.println(levelText);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public void Bar()
	{
		startBars = 170;
	}
	
	public int enemyCount() {
		int i = 0;
		for(GameObject g:sprites) {
			if(g instanceof Ship && !g.equals(player)) {
				i++;
			}
		}
		return i;
	}

	public String getLevel(int lvNum) {

		switch (lvNum) {

			case 1:
				return ("1.4");
			case 2:
				return ("1.1");
			case 3:
				return ("1.2");
			case 4:
				return ("1.3");//Blunderbuss Bay
			case 5:
				return ("2.1");
			case 6:
				return ("2.2");//Shimmermist Falls
			case 7:
				return ("3.1");
			case 8:
				return ("3.2");//Gritborn Straight
			case 9:
				return ("4.1");
			case 10:
				return ("4.3");
			case 11:
				return ("4.2");
			case 12:
				return ("3.3");//Coral Abyss
			default:
				return ("");

		}
	}
}
