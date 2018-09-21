package org.firstinspires.ftc.teamcode;


import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import java.io.File;

public class deepLearningTest {

    public static void main(String[] args) {
        String modelFile = "TeamCode\\src\\main\\assets\\testModel1\\hamzaiswear.h5";
        String inputFile = "TeamCode\\src\\main\\assets\\testModel1\\fashion-mnist-sprite.png";

        MultiLayerNetwork model;
        INDArray input;
        INDArray output;

        try {
            //import keras model
            model = KerasModelImport.importKerasSequentialModelAndWeights(modelFile);
            model.init();

            //load input image & run inference
            input = new NativeImageLoader().asMatrix(new File(inputFile));
            output = model.output(input);

            //output results
            System.out.println("Results: " + output.toString());
        }
        catch (Exception e) { System.out.print("Exception: " + e.toString() + " try again."); }
    }
}