package org.firstinspires.ftc.teamcode;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Environment;
import android.widget.ImageView;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.vuforia.Image;
import com.vuforia.PIXEL_FORMAT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by bodeng on 10/19/18.
 */

@Autonomous (name = "moveTests", group = "Autonomous")
public class moveTests extends CustomLinearOpMode {    //test for red double depot side

    private ElapsedTime time = new ElapsedTime();
    private char blockPos = 'C';

    @Override
    public void runOpMode() throws InterruptedException {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

        // VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection   = CAMERA_CHOICE;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        Vuforia.setFrameFormat(PIXEL_FORMAT.RGB565, true);
        vuforia.setFrameQueueCapacity(1);

        initizialize();
        telemetry.addLine("Vuforia initialization complete");

        waitForStart();

        moveToLineP(64, 0, 5000);


        // At this point, front of robot should align with corner of lander
    }

    private void Pturn(double angle, int msTimeout) {
        double kP = .5/90;
        time.reset();

        while (Math.abs(imu.getTrueDiff(angle)) > .5 && time.milliseconds() < msTimeout && opModeIsActive()) {
            double angleError = imu.getTrueDiff(angle);
            double minSpeed = .2;

            double PIDchange = kP * angleError;

            if (PIDchange > 0 && PIDchange < minSpeed)
                PIDchange = minSpeed;
            else if (PIDchange < 0 && PIDchange > -minSpeed)
                PIDchange = -minSpeed;

            motorBL.setPower(-PIDchange);
            motorFL.setPower(-PIDchange);
            motorBR.setPower(PIDchange);
            motorFR.setPower(PIDchange);

            telemetry.addData("angleError: ", angleError);
            telemetry.addData("PIDCHANGE: ", PIDchange);
            telemetry.update();
        }
        stopMotors();
    }

    public void moveTime(double msTime, double leftPow, double rightPow) throws InterruptedException {
        time.reset();
        motorBL.setPower(leftPow);
        motorFL.setPower(leftPow);
        motorBR.setPower(rightPow);
        motorFR.setPower(rightPow);
        while (time.milliseconds() < msTime) {}
        stopMotors();
    }

    public void stopMotors() {
        motorBL.setPower(0);
        motorFL.setPower(0);
        motorBR.setPower(0);
        motorFR.setPower(0);
    }

    public void moveToDistP(double inches, double angle, double timeout) {
        double kPdist = .03;
        double kPangle = 2.5/90;

        double minDrive = .1;
        double maxDrive = .5;

        while ((Math.abs(getDistB() - inches) > .25 || imu.getTrueDiff(angle) > .5) && opModeIsActive()) {

            double distError = inches - getDistB();
            double PIDchangeDist = -kPdist * distError;

            if (PIDchangeDist < minDrive && PIDchangeDist > 0) {
                PIDchangeDist = minDrive;
            } else if (PIDchangeDist > -minDrive && PIDchangeDist < 0) {
                PIDchangeDist = -minDrive;
            }

            double angleError = imu.getTrueDiff(angle);
            double PIDchangeAngle = kPangle * angleError;

            motorBL.setPower(Range.clip(PIDchangeDist - PIDchangeAngle, -maxDrive, maxDrive));
            motorFL.setPower(Range.clip(PIDchangeDist - PIDchangeAngle, -maxDrive, maxDrive));
            motorBR.setPower(Range.clip(PIDchangeDist + PIDchangeAngle, -maxDrive, maxDrive));
            motorFR.setPower(Range.clip(PIDchangeDist + PIDchangeAngle, -maxDrive, maxDrive));
        }
        stopMotors();
    }

    public void moveToEncoder(double encoder, double power, double angle) {
        motorFL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        idle();
        motorFL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        double kPangle = 2.5/90;
        telemetry.addData("motorFL: ", motorFL.getCurrentPosition());

        if (encoder > 0) {
            while (motorFL.getCurrentPosition() < encoder && opModeIsActive()) {

                double angleError = imu.getTrueDiff(angle);
                double PIDchangeAngle = kPangle * angleError;

                motorBL.setPower(Range.clip(power - PIDchangeAngle, -1, 1));
                motorFL.setPower(Range.clip(power - PIDchangeAngle, -1, 1));
                motorBR.setPower(Range.clip(power + PIDchangeAngle, -1, 1));
                motorFR.setPower(Range.clip(power + PIDchangeAngle, -1, 1));
            }
        }
        else {
            while (motorFL.getCurrentPosition() > encoder && opModeIsActive()) {

                double angleError = imu.getTrueDiff(angle);
                double PIDchangeAngle = kPangle * angleError;

                motorBL.setPower(Range.clip(-power + PIDchangeAngle, -1, 1));
                motorFL.setPower(Range.clip(-power + PIDchangeAngle, -1, 1));
                motorBR.setPower(Range.clip(-power - PIDchangeAngle, -1, 1));
                motorFR.setPower(Range.clip(-power - PIDchangeAngle, -1, 1));
                telemetry.addData("motorBL: ", motorFL.getCurrentPosition());
                telemetry.update();
            }
        }
        stopMotors();
    }

    public void getBlock() throws InterruptedException {
        //blockPos = 'C';

        Bitmap bitmap = takePic();


        // basic brute force counter
        int startRow = 12;
        int endRow = 18;
        int leftSrow = 11;
        int leftErow = 17;
        int centerSrow = 34;
        int centerErow = 40;
        int rightSrow = 54;
        int rightErow = 60;

        BoundingBox left = new BoundingBox(startRow, leftSrow, endRow, leftErow);    //look at images taken from consistent
        BoundingBox center = new BoundingBox(startRow, centerSrow, endRow, centerErow);  //spot in auto and get pixel range
        BoundingBox right = new BoundingBox(startRow, rightSrow, endRow, rightErow);   //of left center and right

        if (yellowValOfBox(bitmap, left) > yellowValOfBox(bitmap, center)) {
            blockPos = 'L';
            if (yellowValOfBox(bitmap, right) > yellowValOfBox(bitmap, left))
                blockPos = 'R';
        }
        else if (yellowValOfBox(bitmap, right) > yellowValOfBox(bitmap, center))
            blockPos = 'R';

        if (blockPos == 'L')
            saveBox(bitmap, left);
        else if (blockPos == 'C')
            saveBox(bitmap, center);
        else
            saveBox(bitmap, right);

        // multi location pixel scanner (better but much slower)

        /*int N = 4; // the approx height and width of an object

        for (int r = 0; r < */
    }

    public void getBlockAcc() throws InterruptedException {
        Bitmap bitmap = takePic();

        int N = 4;
        BoundingBox block = null;
        BoundingBox ball1 = null;
        BoundingBox ball2 = null;
        int yellow = -1;
        int white = -1;
        int white2 = -1;
        for (int r = 0; r < bitmap.getHeight() - N; r++) {
            for (int c = 0; c < bitmap.getWidth() - N; c++) {
                BoundingBox testBox = new BoundingBox(r, c, r+4, c+4);
                int testYellow = yellowValOfBox(bitmap, testBox);
                int testWhite = whiteValOfBox(bitmap, testBox);
                if (testYellow > yellow) {
                    yellow = testYellow;
                    block = testBox;
                }
                if (testWhite > white) {
                    white = testWhite;
                    ball1 = testBox;
                }
                else if (testWhite > white2 && Math.abs(testBox.startCol - ball1.startCol) > 10) {
                    white2 = testWhite;
                    ball2 = testBox;
                }
            }
        }

        if (block.startCol < ball1.startCol && block.startCol < ball2.startCol)
            blockPos = 'L';
        else if (block.startCol > ball1.startCol && block.startCol > ball2.startCol)
            blockPos = 'R';
        else
            blockPos = 'C';


    }

    public int whiteValOfBox(Bitmap bmp, BoundingBox bb) {
        int whiteSum = 0;

        for (int r = bb.startRow; r < bb.endRow; r++) {
            for (int c = bb.startCol;  c < bb.endCol; c++) {
                int color = bmp.getPixel(c, r);
                int R = (color >> 16) & 0xff;
                int G = (color >>  8) & 0xff;
                int B = color & 0xff;
                int white = Math.min(R, Math.min(G, B));
                whiteSum += white;
            }
        }

        return whiteSum;
    }

    public int yellowValOfBox(Bitmap bmp, BoundingBox bb) {
        int ySum = 0;

        //scans bounding box
        for (int r = bb.startRow; r < bb.endRow; r++) {
            for (int c = bb.startCol;  c < bb.endCol; c++) {
                int color = bmp.getPixel(c, r);
                int R = (color >> 16) & 0xff;
                int G = (color >>  8) & 0xff;
                int B = color & 0xff;
                int yellow = Math.min(R, G) - B / 2 > 0 ? Math.min(R, G) - B / 2 : 0;
                ySum += yellow;
            }
        }

        //scans entire bitmap
        /*or (int r = 0; r < bmp.getHeight(); r++) {
            for (int c = 0;  c < bmp.getWidth(); c++) {
                int color = bmp.getPixel(c, r);
                int R = (color >> 16) & 0xff;
                int G = (color >>  8) & 0xff;
                int yellow = Math.min(R, G);
                ySum += yellow;
            }
        }*/
        return ySum;
    }

    private class BoundingBox {
        int startRow;
        int endRow;
        int startCol;
        int endCol;

        public BoundingBox(int startRow, int startCol, int endRow, int endCol) {
            this.startRow = startRow;
            this.endRow = endRow;
            this.startCol = startCol;
            this.endCol = endCol;
        }
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    public Bitmap takePic() throws InterruptedException {
        File dir = Environment.getExternalStorageDirectory();
        File[] files = dir.listFiles();
        int highestFileNum = -1;
        for (File f : files) {
            if (f.getName().contains("BoTest_pic_")) {
                highestFileNum = Math.max(highestFileNum, Integer.parseInt(f.getName().replaceAll("\\D", "")));
            }
        }
        int currFileNum = highestFileNum + 1;

        VuforiaLocalizer.CloseableFrame frame = vuforia.getFrameQueue().take();

        telemetry.addData("Num images in frame", "" + frame.getNumImages());
        telemetry.update();
        //for (int i = 0; i < frame.getNumImages() && opModeIsActive(); i++) {
        Image image = frame.getImage(0);

        int imageWidth = image.getWidth(), imageHeight = image.getHeight();
        Bitmap bmp = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.RGB_565);
        bmp.copyPixelsFromBuffer(image.getPixels());
        try {
            File sdCard = Environment.getExternalStorageDirectory();
            telemetry.addLine(sdCard.getAbsolutePath());
            telemetry.update();
            //File dir = new File(sdCard.getAbsolutePath() + "/dir1");
            //dir.mkdirs();

            File file = new File(sdCard, "BoTest_pic_" + currFileNum++ + ".png");

            FileOutputStream fos = new FileOutputStream(file);

            bmp = getResizedBitmap(bmp, 78, 59);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);

        } catch (FileNotFoundException ex) {
            telemetry.addLine(ex.toString());
            telemetry.update();
        }
        //ByteBuffer byteBuffer = image.getPixels();
                    /*if (frameBuffer == null) {
                        frameBuffer = new byte[byteBuffer.capacity()];
                    }
                    byteBuffer.get(frameBuffer);
                    if (this.frame == null) {
                        this.frame = new Mat(imageHeight, imageWidth, CvType.CV_8UC3);

                        if (overlayView != null) {
                            overlayView.setImageSize(imageWidth, imageHeight);
                        }
                    }
                    this.frame.put(0, 0, frameBuffer);

                    Imgproc.cvtColor(this.frame, this.frame, Imgproc.COLOR_RGB2BGR);

                    if (parameters.cameraDirection == VuforiaLocalizer.CameraDirection.FRONT) {
                        Core.flip(this.frame, this.frame, 1);
                    }

                    onFrame(this.frame, vuforiaFrame.getTimeStamp());*/
        return bmp;
    }

    public Bitmap saveBox(Bitmap bmp, BoundingBox bb) throws InterruptedException{
        File dir = Environment.getExternalStorageDirectory();
        File[] files = dir.listFiles();
        int highestFileNum = -1;
        for (File f : files) {
            if (f.getName().contains("pixel_match_")) {
                highestFileNum = Math.max(highestFileNum, Integer.parseInt(f.getName().replaceAll("\\D", "")));
            }
        }
        int currFileNum = highestFileNum + 1;

        try {
            File sdCard = Environment.getExternalStorageDirectory();
            telemetry.addLine(sdCard.getAbsolutePath());
            telemetry.update();
            //File dir = new File(sdCard.getAbsolutePath() + "/dir1");
            //dir.mkdirs();

            File file = new File(sdCard, "pixel_match_" + currFileNum++ + ".png");

            FileOutputStream fos = new FileOutputStream(file);

            bmp = getResizedBitmap(bmp, 78, 59);
            Bitmap drawnBmp = bmp.copy(Bitmap.Config.RGB_565, true);

            Canvas canvas = new Canvas(drawnBmp);
            Paint p = new Paint();
            p.setStyle(Paint.Style.FILL_AND_STROKE);
            p.setAntiAlias(true);
            p.setFilterBitmap(true);
            p.setDither(true);
            p.setColor(Color.RED);

            canvas.drawLine(bb.startCol, bb.startRow, bb.startCol, bb.endRow, p);//left
            canvas.drawLine(bb.startCol, bb.startRow, bb.endCol, bb.startRow, p);//top
            canvas.drawLine(bb.endCol, bb.startRow, bb.endCol, bb.endRow, p);//right
            canvas.drawLine(bb.startCol, bb.endRow, bb.endCol, bb.endRow, p);//bottom

// rect ...
//canvas.drawRect(/*all of my end coordinates*/, p);

            //ImageView iView = (ImageView)findViewById(R.id.imageViewPreview);
            //iView.setImageBitmap(drawnBmp);
            //iView.draw(canvas);

            drawnBmp.compress(Bitmap.CompressFormat.PNG, 100, fos);

        } catch (FileNotFoundException ex) {
            telemetry.addLine(ex.toString());
            telemetry.update();
        }

        return bmp;
    }
}