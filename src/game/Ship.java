package game;

import java.awt.Graphics;

public class Ship extends GameObject {
	public int heading, type;
	public double bounceX = 0, bounceY = 0;
	public int health = 1;
	public int actions = 3;
	
	/**
	 * Creates a new ship. 
	 * @param x - Leftmost square = 0
	 * @param y - Topmost square = 0
	 * @param heading - Right = 0, up = 90, left = 180, down = 270
	 * @param type 0 = player, 1 = easy, 2 = medium, 3 = hard
	 */
	public Ship(int x, int y, int heading, int type) {
		super(x, y);
		this.heading = heading;
		this.type = type;
		this.z = 2;
		this.health = type;
		this.sprite.framerate = 8;
		if(type==0) {
			this.z = 3;
			this.health = 1;
		}
		Game.grid[x][y] = this;
	}

	@Override
	public void update(Game game, double dt) {
		if(bounceX!=0||bounceY!=0) {
			xreal += Math.signum(x+bounceX-xreal)*Math.max(Math.abs(x+bounceX-xreal)*kspeed, minspeed);
			yreal += Math.signum(y+bounceY-yreal)*Math.max(Math.abs(y+bounceY-yreal)*kspeed, minspeed);
			if((bounceX>=0&&xreal>=x+bounceX) || (bounceX<=0&&xreal<=x+bounceX)) {
				if((bounceY>=0&&yreal>=y+bounceY) || (bounceY<=0&&yreal<=y+bounceY)) {
					bounceX = 0;
					bounceY = 0;
				}
			}
		} else {
			xreal += Math.signum(x-xreal)*Math.max(Math.abs(x-xreal)*kspeed, minspeed);
			yreal += Math.signum(y-yreal)*Math.max(Math.abs(y-yreal)*kspeed, minspeed);
			if(Math.abs(xreal-x)<minspeed) xreal = x;
			if(Math.abs(yreal-y)<minspeed) yreal = y;
		}
		switch(type) {
			case 0: this.sprite.animate(Animations.SHIP, dt, heading/90); break;
			case 1: this.sprite.animate(Animations.ENEMY1, dt, heading/90); break;
			case 2: this.sprite.animate(Animations.ENEMY2, dt, heading/90); break;
			case 3: this.sprite.animate(Animations.ENEMY3, dt, heading/90); break;
		}
		
	}
	
	public boolean moveLeft() {
		if(heading == 0 || x == 0) return false;
		translate(-1, 0);
		heading = 180;
		return true;
	}
	
	public boolean moveRight() {
		if(heading == 180 || x == Game.W-1) return false;
		translate(1, 0);
		heading = 0;
		return true;
	}
	
	public boolean moveUp() {
		if(heading == 270 || y == 0) return false;
		translate(0, -1);
		heading = 90;
		return true;
	}
	
	public boolean moveDown() {
		if(heading == 90 || y == Game.H-1) return false;
		translate(0,1);
		heading = 270;
		return true;
	}
	
	public void shoot() {
		Game.sprites.add(new Boom(this, heading, true));
		Game.sprites.add(new Boom(this, heading, false));
		if(heading%180==90) { // horizontal
			for(int i=x+1;i<Game.W;i++) {
				if(getHit(i,y)) break;
			}
			for(int i=x-1;i>=0;i--) {
				if(getHit(i,y)) break;
			}
		} else { // vertical
			for(int i=y+1;i<Game.H;i++) {
				if(getHit(x,i)) break;
			}
			for(int i=y-1;i>=0;i--) {
				if(getHit(x,i)) break;
			}
		}
	}
	
	public boolean getHit(int x, int y) {
		if(Game.grid[x][y] != null) {
			GameObject obj = Game.grid[x][y];
			if(obj instanceof Tile) {
				if(((Tile)obj).type == 3) {
					return true;
				}
			}
			if(obj instanceof Ship) {
				((Ship) obj).hit();
				return true;
			}
		}
		return false;
	}
	
	public void hit() {
		health--;
		if(health <= 0) {
			kill = true;
		}
		System.out.println("BOOM!!!");
	}
	
	public void translate(int dx, int dy) {
		Game.grid[x][y] = null;
//		xreal = x;
//		yreal = y;
		x += dx;
		y += dy;
		if(collide(x, y)) {
			if(Game.grid[x][y] instanceof Tile) {
				hit();
				if(((Tile)Game.grid[x][y]).type==4) {
					Game.grid[x][y].kill = true;
					Game.grid[x][y] = this;
					return;
				}
			}
			bounceX = Math.signum(dx)*0.5;
			bounceY = Math.signum(dy)*0.5;
			x -= dx;
			y -= dy;
		}
		Game.grid[x][y] = this;
	}

	public boolean collide(int x, int y) {
		if(Game.grid[x][y]!=null) {
			GameObject obj = Game.grid[x][y];
			if(obj instanceof Tile) {
				switch(((Tile)obj).type) {
					case 2: return true;
					case 3: return true;
					case 4: return true;
				}
			}
			if(obj instanceof Ship) {
				return true;
			}
		}
		return false;
	}
	

	public boolean collideNoMines(int x, int y) {
		if(Game.grid[x][y]!=null) {
			GameObject obj = Game.grid[x][y];
			if(obj instanceof Tile) {
				switch(((Tile)obj).type) {
					case 2: return true;
					case 3: return true;
				}
			}
			if(obj instanceof Ship) {
				return true;
			}
		}
		return false;
	}
	
	public void move() {
		Ship p = Game.player;
		int dx = p.x-x;
		int dy = p.y-y;
		if(dx == 0 && heading%180==0 || dy == 0 && heading%180==90) {
			shoot();
			return;
		}
		if(Math.abs(dx) < Math.abs(dy)) { // move horizontal
			if(tryHorizontalMove(dx)) return;
			if(tryVerticalMove(dy)) return;
			if(tryVerticalMove(-dy)) return;
			if(tryHorizontalMove(-dx)) return;
		} else {
			if(tryVerticalMove(dy)) return;
			if(tryHorizontalMove(dx)) return;
			if(tryHorizontalMove(-dx)) return;
			if(tryVerticalMove(-dy)) return;
		}
	}
	
	public void followCurrent() {
		switch(Game.currentGrid[x][y]) {
		case 1: translate(1,0); break;
		case 2: translate(0,-1); break;
		case 3: translate(-1,0); break;
		case 4: translate(0,1); break;
		}
	}

	public boolean tryHorizontalMove(int dx) {
		if(!(dx>0&&(heading==180||x==Game.W-1) || dx<0&&(heading==0||x==0))) {
			if(!collideNoMines(x+(int)Math.signum(dx),y)) {
				if(dx>0) {
					moveRight();
				} else {
					moveLeft();
				}
				return true;
			}
		}
		return false;
	}

	public boolean tryVerticalMove(int dy) {
		if(!(dy>0&&(heading==90||y==Game.H-1) || dy<0&&(heading==270||y==0))) {
			if(!collideNoMines(x,y+(int)Math.signum(dy))) {
				if(dy>0) {
					moveDown();
				} else {
					moveUp();
				}
				return true;
			}
		}
		return false;
	}
}
