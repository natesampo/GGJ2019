package game;

public class PlatformingObject extends GameObject{
	
	Animations currentAnim;
	
	float xVelocity = 0;
	float yVelocity = 0;
	
	// Maximum velocities in each direction
	float maxVUp = 500;
	float maxVDown = 500;
	float maxVLeft = 500;
	float maxVRight = 500;
	float effectiveX;
	float effectiveY;
	
	// Platforming parameters
	float gravity = 100;
	float jumpPower = 200;
	float runAccel = 200;

	public PlatformingObject(int x, int y) {
		super(x, y, 2, 100, 100);
		setAnim(Animations.HYDRA);
	}
	
	@Override
	public double[] update(Game game, double dt) {
		this.sprite.animate(currentAnim, dt);
		return applyMovement(dt);
	}
	
	public double[] applyMovement(double dt) {
		
		// Cap velocities at maximum values before applying movement
		effectiveX = Math.max(xVelocity, -maxVLeft);
		effectiveX = Math.min(xVelocity, maxVRight);
		effectiveY = Math.max(yVelocity, -maxVUp);
		effectiveY = Math.max(yVelocity, maxVDown);
		
		effectiveX = xVelocity;
		effectiveY = yVelocity;
		
		double dx = effectiveX*dt;
		double dy = effectiveY*dt;
		
//		x += dx;
//		y += dy;
		
		if (y>640 && effectiveY >= 0) {
			jump();
		}
		
		yVelocity += gravity*dt;
		
		return new double[]{dx, dy};
	}
	
	public void jump() {
		yVelocity = -jumpPower;
	}
	
	public void setAnim(Animations newAnim) {
		currentAnim = newAnim;
	}

	@Override
	public boolean isCollidable(GameObject obj) {
		return false;
	}
	
	
}
