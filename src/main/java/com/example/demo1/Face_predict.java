package com.example.demo1;

import org.opencv.core.*;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.INTER_AREA;

public class Face_predict {
    static
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    ArrayList<Mat> images;
    ArrayList<Integer> labels;
    public static LBPHFaceRecognizer e;
    Face_predict()
    {
        images = new ArrayList<>();
        labels = new ArrayList<>();
        e = LBPHFaceRecognizer.create();
    }
    void createArraylists(String logfile_path)
    {
        BufferedReader log=null;
        try
        {
            log  = new BufferedReader(new FileReader(logfile_path));
        }
        catch(Exception e)
        {
            System.out.println("File doesn't exist!");
        }
        String temp=null;
        do {
            try
            {
                temp = log.readLine();
            }
            catch(Exception e)
            {
                System.out.println(e);
            }
            if(temp!=null)
            {
                String a[] = temp.split(";");
                Mat img = Imgcodecs.imread(a[0]);
                Imgproc.cvtColor(img,img,COLOR_BGR2GRAY);
                Mat resizeimage = new Mat();
                Size scaleSize = new Size(500,500);
                Imgproc.resize(img, resizeimage, scaleSize , 0, 0, INTER_AREA);
                images.add(resizeimage);
                labels.add(Integer.parseInt(a[1]));
            }
        }while(temp!=null);
    }
    public void train()
    {
        int[] allLabels = new int[labels.size()];
        for (int i = 0; i < labels.size(); i++) {
            allLabels[i] = labels.get(i);
        }
        e.train(images,new MatOfInt(allLabels));
    }
    public void predict(Mat ip_img,Mat op_img)
    {
        String xmlFile = "/media/shree/College/Sem_4/OOPS/OOPS_project/opencv-4.5.5/data/haarcascades/haarcascade_frontalface_alt2.xml";
        CascadeClassifier classifier = new CascadeClassifier(xmlFile);
        MatOfRect faceDetections = new MatOfRect();
        classifier.detectMultiScale(ip_img, faceDetections);
        System.out.println(String.format("Detected %s faces",
                faceDetections.toArray().length));
        // Drawing boxes
        Mat[] ans = new Mat[faceDetections.toArray().length];
        for (Rect rect : faceDetections.toArray()) {
            Imgproc.rectangle(ip_img,new org.opencv.core.Point(rect.x, rect.y),new org.opencv.core.Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 0, 255), 3);
            int[] predicted_label = new int[1];
            double[] confidence = new double[1];
            if(rect.x>20)
                rect.x-=20;
            if(rect.y>20)
                rect.y-=20;
            rect.width+=40;
            rect.height+=40;
            Mat roi = new Mat(ip_img,rect);
            Mat resized = new Mat();
            Size scaleSize = new Size(500,500);
            Imgproc.resize(roi, resized, scaleSize , 0, 0, INTER_AREA);

            e.predict(resized,predicted_label,confidence);

            Imgproc.rectangle(op_img,new org.opencv.core.Point(rect.x, rect.y),new org.opencv.core.Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 255), 3);
            Imgproc.putText(op_img,"Cl: "+predicted_label[0],new org.opencv.core.Point(rect.x,rect.y+rect.height+10),Imgproc.FONT_HERSHEY_COMPLEX,0.5,new Scalar(0,0,255),1);
            Imgproc.putText(op_img,"Confi "+Math.round(confidence[0]),new org.opencv.core.Point(rect.x,rect.y+rect.height+40),Imgproc.FONT_HERSHEY_COMPLEX,0.5,new Scalar(0,0,255),1);
            System.out.println("Predicted label:"+predicted_label[0]);
            System.out.println("Confidence:"+confidence[0]);
        }
        HighGui.imshow("Predicted image:",op_img);
        HighGui.resizeWindow("Predicted image",500,500);
        HighGui.waitKey(0);

    }
    public void save(String file)
    {
        e.save(file);
    }
    public static void main(String[] args) {
        Face_predict obj = new Face_predict();
        obj.createArraylists("/media/shree/College/Sem_4/OOPS/OOPS_project/Image_dataset/Logs.txt");
        obj.train();
//        obj.e.read("/media/shree/College/Sem_4/OOPS/OOPS_project/Models/LBPH_500.dat");
        Mat img = Imgcodecs.imread("target/classes/com/example/demo1/frame.png");
        Mat resizeimage = new Mat();
        Size scaleSize= new Size(1024,1024);
        Imgproc.resize(img, resizeimage, scaleSize , 0, 0, INTER_AREA);
        Mat color_resized = resizeimage.clone();
        Imgproc.cvtColor(resizeimage,resizeimage,COLOR_BGR2GRAY);
        obj.save("/media/shree/College/Sem_4/OOPS/OOPS_project/Models/Fischer_500.dat");
        obj.predict(resizeimage,color_resized);

    }
    public static Mat predict_final(Mat ip_img, Mat op_img,ArrayList<Integer> label,ArrayList<Double> confi)
    {
        e = LBPHFaceRecognizer.create();
        e.read("/media/shree/College/Sem_4/OOPS/OOPS_project/Models/LBPH_500.dat");
        String xmlFile = "/media/shree/College/Sem_4/OOPS/OOPS_project/opencv-4.5.5/data/haarcascades/haarcascade_frontalface_alt2.xml";
        CascadeClassifier classifier = new CascadeClassifier(xmlFile);
        MatOfRect faceDetections = new MatOfRect();
        classifier.detectMultiScale(ip_img, faceDetections);
        System.out.println(String.format("Detected %s faces",
                faceDetections.toArray().length));
        // Drawing boxes

        Mat[] ans = new Mat[faceDetections.toArray().length];
        for (Rect rect : faceDetections.toArray()) {
            Imgproc.rectangle(ip_img,new Point(rect.x, rect.y),new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 0, 255), 3);
            int[] predicted_label = new int[1];
            double[] confidence = new double[1];
            if(rect.x>20)
                rect.x-=20;
            if(rect.y>20)
                rect.y-=20;
            rect.width+=40;
            rect.height+=40;
            Mat roi = new Mat(ip_img,rect);
            Mat resized = new Mat();
            Size scaleSize = new Size(500,500);
            Imgproc.resize(roi, resized, scaleSize , 0, 0, INTER_AREA);

            e.predict(resized,predicted_label,confidence);

            Imgproc.rectangle(op_img,new Point(rect.x, rect.y),new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 255), 3);
            Imgproc.putText(op_img,"Cl: "+predicted_label[0],new Point(rect.x,rect.y+rect.height+10),Imgproc.FONT_HERSHEY_COMPLEX,0.5,new Scalar(0,0,255),1);
            Imgproc.putText(op_img,"Confi "+Math.round(confidence[0]),new Point(rect.x,rect.y+rect.height+40),Imgproc.FONT_HERSHEY_COMPLEX,0.5,new Scalar(0,0,255),1);
            System.out.println("Predicted label:"+predicted_label[0]);
            System.out.println("Confidence:"+confidence[0]);
            label.add(predicted_label[0]);
            confi.add(confidence[0]);
        }
//        HighGui.imshow("Predicted image:",op_img);
//        HighGui.resizeWindow("Predicted image",500,500);
//        HighGui.waitKey(0);
        return op_img;
    }
}

