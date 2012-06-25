package harvard.robobees.simbeeotic.model;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import harvard.robobees.simbeeotic.SimTime;
import harvard.robobees.simbeeotic.algorithms.StaticOccupancyMap;
import harvard.robobees.simbeeotic.model.sensor.*;
import harvard.robobees.simbeeotic.util.HeatMap;
import org.apache.log4j.Logger;

import javax.vecmath.Vector3f;
import java.security.PrivateKey;


public class OccupancyBee extends SimpleBee{

    private Compass compass;
    private LaserRangeSensor laserRangeSensor;
    private StaticOccupancyMap occupancyMap = new StaticOccupancyMap();


    private float maxVel = 1f; //set max velocity to 3 m/s, so that entire map can be mapped.
    private float[] range = new float[181];
    public float beeTheta;
    public int counter = 0;

    private static Logger logger = Logger.getLogger(OccupancyBee.class);



    @Override
    public void initialize() {
        super.initialize();
        setHovering(true);  //make the bee hover at constant height

        compass = getSensor("compass", Compass.class); //compass to find heading
        laserRangeSensor = getSensor("range-sensor", LaserRangeSensor.class); //laser range finder for occupancy mapping

        occupancyMap.initialize();




    }


    @Override
    protected void updateKinematics(SimTime time) {
        Vector3f pos = getTruthPosition();
        Vector3f vel = getTruthLinearVelocity();

        beeTheta = compass.getHeading();

        logger.info("ID: " + getModelId() + "  " +
                "time: " + time.getImpreciseTime() + "  " +
                "pos: " + pos + "  " +
                "vel: " + vel + " ");


        range = laserRangeSensor.getRange();  //get range from the laser-range-finder.

        //occupancyMap.drawRange(range);
        //occupancyMap.polarMap(range, beeTheta);
        //occupancyMap.occupancy(range, pos, beeTheta);
        //occupancyMap.bayesOccupancy(range, pos, beeTheta);
        //occupancyMap.scaledLaserHeading(range, beeTheta);

        occupancyMap.slamMap(range, pos, beeTheta);


        Vector3f difference = new Vector3f();
        Vector3f zeroZeroZero = new Vector3f(0,0,0);

	//move the bee in C-shape, mapping the entire hallway.



        if (counter==0){
            Vector3f target = new Vector3f(0,-40,1);
            turnToward(target);
            Vector3f velDesire = new Vector3f(maxVel, 0, 0);
            setDesiredLinearVelocity(velDesire);

            difference.sub(target, pos);
            double distToGo = difference.length();
            if (distToGo < 1){
                counter++;
                return;
            }
        }


        else if (counter==1){
            Vector3f target = new Vector3f(-25,-40,1);
            turnToward(target);
            Vector3f velDesire = new Vector3f(maxVel, 0, 0);
            setDesiredLinearVelocity(velDesire);

            difference.sub(target, pos);
            double distToGo = difference.length();
            if (distToGo < 1){
                counter++;
            }
        }
        else if (counter==2){
            Vector3f target = new Vector3f(0,-40,1);
            turnToward(target);
            Vector3f velDesire = new Vector3f(maxVel, 0, 0);
            setDesiredLinearVelocity(velDesire);

            difference.sub(target, pos);
            double distToGo = difference.length();
            if (distToGo < 1){
                counter++;
            }
        }
        else if (counter==3){
            Vector3f target = new Vector3f(0,40,1);
            turnToward(target);
            Vector3f velDesire = new Vector3f(maxVel, 0, 0);
            setDesiredLinearVelocity(velDesire);

            difference.sub(target, pos);
            double distToGo = difference.length();
            if (distToGo < 1){
                counter++;
            }
        }
        else if (counter==4){
            Vector3f target = new Vector3f(-25,40,1);
            turnToward(target);
            Vector3f velDesire = new Vector3f(maxVel, 0, 0);
            setDesiredLinearVelocity(velDesire);

            difference.sub(target, pos);
            double distToGo = difference.length();
            if (distToGo < 1){
                counter++;
            }
        }

        else if (counter==5){
            Vector3f target = new Vector3f(0, 40, 1);
            turnToward(target);
            Vector3f velDesire = new Vector3f(maxVel, 0, 0);
            setDesiredLinearVelocity(velDesire);

            difference.sub(target, pos);
            double distToGo = difference.length();
            if (distToGo < 1){
                finish();
            }

        }



       //  turn((float).1);


        //make bee move along Dworkin Hallways
        /*if (counter==0){
            Vector3f target = new Vector3f(35,-1,1);
            turnToward(target);
            Vector3f velDesire = new Vector3f(maxVel, 0, 0);
            setDesiredLinearVelocity(velDesire);

            difference.sub(target, pos);
            double distToGo = difference.length();
            if (distToGo < 1){
                counter++;
                return;
            }
        }


        else if (counter==1){
            Vector3f target = new Vector3f(-16,-1,1);
            turnToward(target);
            Vector3f velDesire = new Vector3f(maxVel, 0, 0);
            setDesiredLinearVelocity(velDesire);

            difference.sub(target, pos);
            double distToGo = difference.length();
            if (distToGo < 1){
                counter++;
            }
        }
        else if (counter==2){
            Vector3f target = new Vector3f(-17,12,1);
            turnToward(target);
            Vector3f velDesire = new Vector3f(maxVel, 0, 0);
            setDesiredLinearVelocity(velDesire);

            difference.sub(target, pos);
            double distToGo = difference.length();
            if (distToGo < 1){
                counter++;
            }
        }
        else if (counter==3){
            Vector3f target = new Vector3f(-26,12,1);
            turnToward(target);
            Vector3f velDesire = new Vector3f(maxVel, 0, 0);
            setDesiredLinearVelocity(velDesire);

            difference.sub(target, pos);
            double distToGo = difference.length();
            if (distToGo < 1){
                counter++;
            }
        }
        else if (counter==4){
            Vector3f target = new Vector3f(5,12,1);
            turnToward(target);
            Vector3f velDesire = new Vector3f(maxVel, 0, 0);
            setDesiredLinearVelocity(velDesire);

            difference.sub(target, pos);
            double distToGo = difference.length();
            if (distToGo < 1){
                finish();
            }
        } */

    }


    @Override
    public void finish() {
    }


    @Inject(optional = true)
    public final void setMaxVelocity(@Named(value = "max-vel") final float vel) {
        this.maxVel = vel;
    }


}