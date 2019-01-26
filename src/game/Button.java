package game;

import game.GameObject.Animations;

public class Button extends GameObject {

	public int onClick;
	public Button(int x, int y, int onClick) {
		super(x, y);
		this.onClick = onClick;
	}

	@Override
	public void update(Game game, double dt) {
		this.sprite.animate(Animations.BUTTON, dt, onClick);
	}

	@Override
    public int compareTo(GameObject obj) {
		return 1;
    }
}