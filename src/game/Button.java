package game;

import java.awt.Graphics;

public class Button {

	public int x, y, width, height, onClick;
	public SpriteSheet spritesheet;
	public Button(int x, int y, int width, int height, int onClick) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.onClick = onClick;
		
		switch(onClick) {
			case 0:
				this.spritesheet = new SpriteSheet("ButtonPortLoad.png", 1, 1);
				break;
			case 1:
				this.spritesheet = new SpriteSheet("ButtonPortFire.png", 1, 1);
				break;
			case 2:
				this.spritesheet = new SpriteSheet("ButtonStarboardLoad.png", 1, 1);
				break;
			case 3:
				this.spritesheet = new SpriteSheet("ButtonStarboardFire.png", 1, 1);
				break;
			case 4:
				this.spritesheet = new SpriteSheet("ButtonMine.png", 1, 1);
				break;
		}
	}

	public void update(Game game, double dt) {
	}
	
	public void draw(Graphics g) {
		g.drawImage(this.spritesheet.getFrame(0), this.x, this.y, null);
	}
}