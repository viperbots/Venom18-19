package org.firstinspires.ftc.teamcode;
<<<<<<< HEAD
/*
=======

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Autonomous(name="TrollBotAuto", group="TrollBot")
>>>>>>> c365a6d58f1369aab9669124062f32a918fa98df
public class TrollbotAuto extends CustomLinearOpMode {

    String blockLocation;
    ModernRoboticsI2cRangeSensor rangeSensor;

    /*public boolean isBlock1onLeft() {
        // **needs a new name
        //if first block is on the left
            //return true
        //else
            //return false
        return true;
    }

    public boolean isBlock2onLeft() {
        // **needs a new name
        //if second block is on the left
            //return true
        //else
            //return false
        return false;
    }

    public boolean isBlock2onRight() {
        // **needs a new name
        //if second block is on the right
            //return true
        //else
            //return false
        return true;
    }

    public boolean isBlock2onCenter() {
        // **needs a new name
        //if second block is on the center
            //return true
        //else
            //return false
        return false;
    }

    public boolean isBlock1onRight() {
        // **needs a new name
        //if first block is on the right
            //return true
        //else
            //return false
        return true;
    }

    public boolean isBlock1onCenter() {
        // **needs a new name
        //if first block is on the center
            //return true
        //else
            //return false
        return true;
    } */


    @Override
    public void runOpMode() {

        initizialize();
        rangeSensor = hardwareMap.get(ModernRoboticsI2cRangeSensor.class, "rangeSensor");

        try {
            release();
        } catch(Exception e) {
            stop();
        }

        while(getDist() < 60) {
            driveForward();
        }
        stopDriveMotors();

        knockFirstBlock();

        goToSecondBlocks();

        knockSecondBlock();


        blockLocation = getBlockLocation();
        if (blockLocation.equals("LEFT")) {
            goForward(25.4558);
            turn(45.0);
            goForward(36);
            turn(90.0);
            goForward(24);
            depositMarker();
            turn(180.0);
            goForward(48.0);

            if (blockLocation.equals("LEFT")){
                turn(90.0);
                goForward(12.0);
                turn(-90.0);
                goForward(36.0);
                // You are on the crater wall
                //You could turn around a little more
                // than 45 degrees right or something and go like
                //12 sqrt 2 inches to be around the center of the crater,
                // could turn another 90 degrees right to be
                // facing the rover
            }

            else if (blockLocation.equals("RIGHT")){
                turn(90.0);
                goForward(36.0);
                turn(-90.0);
                goForward(60.0);
                // You are on the crater wall
                //You could turn a little less than 45 degrees right
                // and go backwards around 24 sqrt 2 inches to be
                // around the center of the crater, could turn another
                // 90 degrees right to be facing the rover
            }

            else if (blockLocation.equals("CENTER")){
                turn(90.0);
                goForward(24.0);
                turn(-90.0);
                goForward(48.0);
                // You are on the crater wall
                //You could turn a little less than 45 degrees
                // right and go backwards around 12 sqrt 2 inches
                // to be around the center of the crater, could
                // turn another 90 degrees right to be facing the rover
            }

        }

        if (blockLocation.equals("RIGHT")){
            goForward(25.4558);
            turn(-45.0);
            goForward(24.0);
            turn(90.0);
            goForward(36.0);
            turn(-90.0);
            depositMarker();
            turn(180.0);
            goForward(48.0);

            if (blockLocation.equals("LEFT")){
                turn(90.0);
                goForward(12.0);
                turn(-90.0);
                goForward(36.0);
                // You are on the crater wall
                //You could turn around a little more than 45
                // degrees right or something and go like 12
                // sqrt 2 inches to be around the center of the crater,
                // could turn another 90 degrees right to be facing the rover
            }

            else if (blockLocation.equals("RIGHT")){
                turn(90.0);
                goForward(36.0);
                turn(-90.0);
                goForward(60.0);
                // You are on the crater wall
                // You could turn a little less than 45 degrees right
                // and go backwards around 24 sqrt 2 inches to be around
                // the center of the crater, could turn another 90 degrees
                // right to be facing the rover
            }

            else if (blockLocation.equals("CENTER")){
                turn(90.0);
                goForward(24.0);
                turn(-90.0);
                goForward(48.0);
                // You are on the crater wall
                //You could turn a little less than 45 degrees right
                // and go backwards around 12 sqrt 2 inches to be
                // around the center of the crater, could turn another
                // 90 degrees right to be facing the rover
            }

        }

        if (blockLocation.equals("CENTER")){
            goForward(59.397);
            turn(45.0);
            goForward(12.0);
            turn(-90.0);
            depositMarker();
            turn(180);
            goForward(48.0);

            if (blockLocation.equals("LEFT")){
                turn(90);
                goForward(12.0);
                turn(-90.0);
                goForward(36.0);
                // You are on the crater wall
                //You could turn around a little more than
                // 45 degrees right or something and go like
                //12 sqrt 2 inches to be around the center of the crater,
                // could turn another 90 degrees right to be facing the rover
            }

            else if (blockLocation.equals("RIGHT")){
                turn(90);
                goForward(36.0);
                turn(-90.0);
                goForward(60.0);
                // You are on the crater wall
                //You could turn around a little more than
                // 45 degrees right or something and go like
                //12 sqrt 2 inches to be around the center of the
                // crater, could turn another 90 degrees right to be
                // facing the rover
            }

            else if (blockLocation.equals("CENTER")){
                turn(90.0);
                goForward(24.0);
                turn(-90.0);
                goForward(48.0);
                // You are on the crater wall
                //You could turn a little less than 45 degrees
                // right and go backwards around 12 sqrt 2 inches
                // to be around the center of the crater,
                // could turn another 90 degrees right to be facing the rover
            }

        }


    }

    // starts 75 inches from the wall (flush with the line)
    public void knockFirstBlock() {
        if (blockLocation.equals("CENTER")) {
            moveToDistance(24);

        } else if (blockLocation.equals("RIGHT")) {
            turn(-45);

            moveToDistance(6);

            turn(90);

            moveToDistance(12);

            depositMarker();


        } else if (blockLocation.equals("LEFT")) {
            goForward(25.4558);
            turn(45.0);
            goForward(36);
            turn(90.0);
            goForward(24);
            depositMarker();
            turn(180.0);
            goForward(48.0);
        }
    }

    public void goToSecondBlocks() {
        while(getDist() < 96) {
            driveBackward();
        }

        stopDriveMotors();
        turn(-90);

        while(getDist() > 36) {
            driveForward();
        }
        stopDriveMotors();

    }

    public void knockSecondBlock() {
        if(blockLocation.equals("CENTER")) {
            while (getDist() > 36 ) {
                driveForward();
            }
            stopDriveMotors();
        } else if(blockLocation.equals("RIGHT")){

        } else if(blockLocation.equals("LEFT")) {

        }
    }

    public void depositMarker() {
        // deposits the marker in the thing
    }

    public String getBlockLocation() {
        return "CENTER";
    }

    public void goForward(double distance){
        // goes foward a certain distance after we add the sensor in
        // distance is in inches
    }
    public double getDist() {
        return rangeSensor.getDistance(DistanceUnit.INCH);
    }
    public void moveToDistance(double dist) {
        while(getDist() > dist) {
            driveForward();
        }
        while(getDist() < dist) {
            driveBackward();
        }
        stopDriveMotors();
    }
}
*/