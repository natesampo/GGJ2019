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
				this.spritesheet = new SpriteSheet("ButtonMineDisabled.png", 1, 1);
				break;
			case 5:
				this.spritesheet = new SpriteSheet("ButtonMine.png", 1, 1);
				break;
			case 6:
				this.spritesheet = new SpriteSheet("ButtonShopOpen.png", 1, 1);
				break;
			case 7:
				this.spritesheet = new SpriteSheet("ButtonShopClose.png", 1, 1);
				break;
			case 8:
				this.spritesheet = new SpriteSheet("ButtonBuyDamage.png", 1, 1);
				break;
			case 9:
				this.spritesheet = new SpriteSheet("ButtonBuyRam.png", 1, 1);
				break;
			case 10:
				this.spritesheet = new SpriteSheet("ButtonBuyRamDisabled.png", 1, 1);
				break;
			case 11:
				this.spritesheet = new SpriteSheet("ButtonBuyMines.png", 1, 1);
				break;
			case 12:
				this.spritesheet = new SpriteSheet("ButtonBuyMinesDisabled.png", 1, 1);
				break;
			case 13:
				this.spritesheet = new SpriteSheet("ButtonBuyRange.png", 1, 1);
				break;
			case 14:
				this.spritesheet = new SpriteSheet("ButtonBuyActions.png", 1, 1);
				break;
		}
	}

	public void update(Game game, double dt) {
	}
	
	public void draw(Graphics g) {
		g.drawImage(this.spritesheet.getFrame(0), this.x, this.y, null);
	}
}