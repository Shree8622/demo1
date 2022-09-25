package com.example.demo1;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.*;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;

import javafx.embed.swing.SwingFXUtils;

import com.example.demo1.Face_predict;

import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.INTER_AREA;

public class Attendence_controller {
    @FXML
    private ImageView Img;
    Boolean chk=true;
    Service<Void> ser = new Service<Void>() {
        @Override protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override protected Void call() throws InterruptedException {
                    // You code you want to execute in service backgroundgoes here
                    while(chk)
                    {
                        Image i = capureSnapShot();
                        Img.setImage(i);
                    }
                    return null;
                }
            };
        }
    };

    public void showcam()
    {
        chk=true;
        ser.setOnSucceeded((WorkerStateEvent event) -> {
            // Anything which you want to update on javafx thread (GUI) after completion of background process.
            System.out.println("Image captured!");
            ser.reset();
        });
        ser.start();
    }
    public void captureImage(ActionEvent e)
    {
        //capture the image from the camera feed
        chk=false;
        saveImage();
    }
    public void submitImages(ActionEvent e)
    {
        //call the predict function of the model
        Mat img = Imgcodecs.imread("target/classes/com/example/demo1/frame.png");
        Mat resizeimage = new Mat();
        Size scaleSize= new Size(500,500);
        Imgproc.resize(img, resizeimage, scaleSize , 0, 0, INTER_AREA);
        Mat color_resized = resizeimage.clone();
        Imgproc.cvtColor(resizeimage,resizeimage,COLOR_BGR2GRAY);
        ArrayList<Integer> label = new ArrayList<>();
        ArrayList<Double> confi = new ArrayList<>();
        Mat ret = Face_predict.predict_final(resizeimage,color_resized,label,confi);

        //converting return Mat to image
        BufferedImage image = new BufferedImage(ret.width(),
                ret.height(), BufferedImage.TYPE_3BYTE_BGR);
        WritableRaster raster = image.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        byte[] d = dataBuffer.getData();
        ret.get(0, 0, d);
        WritableImage img1 = SwingFXUtils.toFXImage(image, null);

        //Updating the image
        Img.setImage(img1);

        //Updating the attendence files
        try
        {
            update_attendence(label,"DAA");
        }
        catch(Exception ae)
        {
            System.out.println(ae);
        }
    }
    public void back(ActionEvent e) throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getResource("Teacher_login.fxml"));
        Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        Scene s = new Scene(root);
        stage.setScene(s);
        stage.show();
    }
    Mat matrix = null;
    public Image capureSnapShot() {
        WritableImage WritableImage = null;
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
        VideoCapture c = new VideoCapture(0);
        Mat matrix = new Mat();
//        c.read(matrix);
        if( c.isOpened()) {
            if (c.read(matrix)) {
                BufferedImage image = new BufferedImage(matrix.width(),
                        matrix.height(), BufferedImage.TYPE_3BYTE_BGR);

                WritableRaster raster = image.getRaster();
                DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
                byte[] d = dataBuffer.getData();
                matrix.get(0, 0, d);
                this.matrix = matrix;
                WritableImage = SwingFXUtils.toFXImage(image, null);
            }
        }
        c.release();
        return WritableImage;
    }
    public void saveImage() {
        String f = "target/classes/com/example/demo1/frame.png";
        Imgcodecs imageCodecs = new Imgcodecs();
        imageCodecs.imwrite(f, matrix);
    }
    public void update_attendence(ArrayList<Integer> label,String sub) throws IOException
    {
        LocalDate date = LocalDate.now();
//        System.out.println("Current Date: "+date);
        File inputFile = new File("files/Attendence_records/"+sub);
        File tempFile = new File("files/Attendence_records/"+sub+"_temp");

        System.out.println("Printing labels");
        for(Integer i:label)
        {
            System.out.println(i);
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
            String currentLine;
            boolean chk = false;
            while((currentLine = reader.readLine()) != null) {
                String[] S = currentLine.split(":");
                if(S.length>1)
                {
                    boolean a = false;
                    if(S[0].equals(date.toString()))
                    {
//                        System.out.println(S[1]);
                        for(String j:S[1].split("/"))
                        {
                            if(j.equals(i.toString()))
                            {
                                a = true;
                                break;
                            }
                        }
                        if(!a)
                        writer.write(currentLine+"/"+i+"\n");
                        else
                            writer.write(currentLine+"\n");
                        chk=true;
                    }
                    else
                    {
                        writer.write(currentLine+"\n");
                    }

                }
                else
                {
                    break;
                }
            }
            if(!chk)
            {
                writer.write(date+":"+i+"\n");
            }
            writer.close();
            reader.close();
            inputFile.delete();
            boolean successful = tempFile.renameTo(inputFile);
            System.out.println(successful);
        }

    }
}
