package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by bodeng on 9/19/18.
 */

@Autonomous (name = "PIDtesting", group = "PID")
public class PIDtesting extends CustomLinearOpMode {


    public void rightTurn(double angle) {
        double kU = .0185;
        double tU = .55 / 1000;

        double kP = .6 * kU;//0.0075;
        double kI = tU / 2;//0.001;
        double kD = tU / 8;//0.00005;
        //double kD = 0.00005;
        double angleError = imu.getTrueDiff(angle);
        double oldTime = 0;
        double totalError = 0;
        double oldError = 0;
        double newTime = 0;
        double P = 0;
        double I = 0;
        double D = 0;
        ElapsedTime timeStuff = new ElapsedTime();

        while (Math.abs(angleError) > .5 && opModeIsActive()) {
            angleError = imu.getTrueDiff(angle);
            newTime = timeStuff.seconds();
            totalError += (newTime - oldTime) * (angleError + oldError) / 2;

            P = kP * angleError;
            I = totalError * kI;
            D = -(angleError - oldError) / (newTime - oldTime) * kD;

            motorFL.setPower(Range.clip(P + I + D, -1, 1));
            motorBL.setPower(Range.clip(P + I + D, -1, 1));
            motorFR.setPower(Range.clip(-P - I - D, -1, 1));
            motorBR.setPower(Range.clip(-P - I - D, -1, 1));


            oldTime = newTime;
            oldError = angleError;

            telemetry.addLine("angleError: " + angleError);
        }
    }

    @Override
    public void runOpMode() {
        initizialize();
        double turnAngle = 90;
        telemetry.addData("current angle: ", imu.getYaw());
        telemetry.addData("angleError: ", imu.getTrueDiff(turnAngle));
        telemetry.addLine("Init complete");
        telemetry.update();
        waitForStart();
        rightTurn(turnAngle);
        stopDriveMotors();
    }
}
