package ITC;
import robocode.*;
import java.awt.Color;
import static robocode.util.Utils.normalRelativeAngleDegrees;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * ITC_DMC - a robot by Dion, Michael, and Callum team D.M.C muthaFucka
 */
public class ITC_Dion_Michael_Callum extends Robot
{	
	boolean movedToCentre = false;
	boolean dodgeType = false;
	boolean lockedOn = false;
	double setFire = 0;
	int speed = 20;
	int radarTurnAmmount =0;
	int bulletMisses = 0;

	/**
	 * run: ITC_DMC's default behavior
	 */
	public void run() {
		// Initialization of the robot should be put here
		setAdjustGunForRobotTurn(true);
		setColors(Color.black,Color.black,Color.black); // body,gun,radar

		// Robot main loop
		while(true)
		{
			lockedOn = false;
			//IF we spawn outside the safe zone, move in
			if(movedToCentre == false)
				{
		            rushCentre(getBattleFieldWidth() / 2, getBattleFieldWidth() / 2);
				}
			turnRadarRight(8);
			turnRadarLeft(8);		//scan small enough to keep track of the enemy
			if (!lockedOn)
			{
				turnRadarRight(360);
			}
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
				movedToCentre = true;

		if(movedToCentre == true)
		{
			//this will be the fireing mechanics
	 
			if(!e.isSentryRobot())
			{
				lockedOn = true;
				// this Linear targetting system will try lock scanner onto the enemy
				double globalBearing = getHeading() + e.getBearing(); // this gets a global or absolute bearing adding our direction to enemy angle
				//Turn gun to the enemy, hopefully keeping it locked on. 13 is used since that's roughly how many ticks we can estimate the enemies new poision
				//using it's current velocity.
				turnGunRight(normalRelativeAngleDegrees(globalBearing - getGunHeading() + (e.getVelocity() * Math.sin(e.getHeading() - globalBearing)/13)));
				
				// if the gun heat is ok shoot and if its greater than 100 shoot a small one
				if(getGunHeat() == 0.0)
				{
					bulletWithMissCalcuLations();
				}				
				turnLeft(normalise(90 - e.getBearing()));
				ahead(speed);
			}	
		}
	}
	
	// function to make the bearing movement effecient. i.e not going the long way around
	double normalise(double angle)
	{
		while(angle > 180)
		{
			angle -= 360;
		}
		while(angle < -180)
		{	
			angle += 360;
		}
		return angle;
	}


	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e)
   {
		speed = speed *-1;
		movedToCentre = true;
   }
   
   public void rushCentre(double w, double h)
   {
	   //Calculating the distance between us and the centre of the battle area
	   //use standard formula for distance between two points for this
	   double distanceToCentre = Math.sqrt(Math.pow((w - getX()), 2) + Math.pow((h - getY()), 2));
	   // get the bearing by rotating aroudn to face the centre
	   double bearing = Math.PI / 2 - Math.atan2(h - getY(), w -getX());
	   //convert angle of rotation from radians to degrees
	   bearing = bearing * (180 / Math.PI);
	   bearing = normalise(bearing - getHeading());
		
		turnRight(bearing);
		ahead(distanceToCentre /2);			
		movedToCentre = true;
   }
		
	public void onHitRobot(HitRobotEvent e) {
		movedToCentre = true;
       if (e.getBearing() > -90 && e.getBearing() <= 90) {
           back(speed);
       } else {
           ahead(speed);
       }
	   speed = speed *-1;
   }
	public void onHitByBullet(HitByBulletEvent e)
	{
		movedToCentre = true;
	}
	
	public void bulletWithMissCalcuLations() //calculates how many bullets weve missed and adjusts power relitively
	{ 
		//will fire a big bullet if we havnt missed and at the start
		if(bulletMisses < 4) 
		{
			fire(3);
		}
		
		//if we miss a couple of shots we lower the strength
		else if (bulletMisses < 6)
		{
			fire(2);
		}
		
		//this is unlikely to happen but will stop ourselves killing ouselves
		else
		{
			fire(0.1);
		}
	}
	
	public void onBulletMissed(BulletMissedEvent e) 
	{
		//increamenst the bulletsMissed counter if we miss
       bulletMisses = bulletMisses + 1; 
   	}
	
    public void onBulletHit(BulletHitEvent e) //
	{  
       //resets the bullet miss counter if we hit
		bulletMisses = 0;
    }







}
