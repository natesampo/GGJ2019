package game;

public class Hydra extends GameObject {
	
	public Hydra(int x, int y) {
		super(x, y);
	}

	@Override
	public void update(Game game, double dt) {
		this.sprite.animate(Animations.HYDRA, dt);
	}
}