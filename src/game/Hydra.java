package game;

public class Hydra extends GameObject {
	
	public Hydra(int x, int y) {
		super(x, y, 2, 100, 100);
	}

	@Override
	public double[] update(Game game, double dt) {
		this.sprite.animate(Animations.HYDRA, dt);
		return new double[]{0,0};
	}

	@Override
	public boolean isCollidable(GameObject obj) {
		return false;
	}
}