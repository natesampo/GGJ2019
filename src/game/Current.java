package game;

import java.awt.Graphics;
import java.awt.Graphics2D;

import game.GameObject.Animations;

public class Current extends Tile {
	
	int heading;

	public Current(int x, int y, int heading) {
		super(x, y, 5);
		this.heading = heading;
		this.z = 0;
		Game.currentGrid[x][y] = heading/90+1;
	}
	
	@Override
	public void update(Game game, double dt) {
		this.sprite.animate(Animations.CURRENT, dt);
	}
	
	@Override
	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		if(!visible) return;
		g.translate((int)(xreal*SCALE)+SCALE/2+XOFFSET, (int)(yreal*SCALE)+SCALE/2+YOFFSET);
		g2d.rotate(Math.toRadians(-heading));
		int dx = 0, dy = 0;
		switch(heading) {
		case 0: break;
		case 270:  dx = 16; dy = -16; break;
		case 180:  dx = 0; dy = -32; break;
		case 90:  dx = -16; dy = -16; break;
		}
		g.translate(dx, dy);
		sprite.draw(g, this);
		g.translate(-dx, -dy);
		g2d.rotate(Math.toRadians(heading));
		g.translate(-(int)(xreal*SCALE)-SCALE/2-XOFFSET, -(int)(yreal*SCALE)-SCALE/2-YOFFSET);
	}
}
