package com.example.demo1;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.*;

import static com.example.demo1.create_dataset.getFolderSize;

public class Register_controller {
    @FXML
    private Label result;

    @FXML
    private  TextField enrollStudentname;
    @FXML
    private  TextField enrollStudentId;
    public boolean Student_reg()
    {
        try
        {
            FileWriter t = new FileWriter("files/Student_details",true);
            String s_Name = enrollStudentname.getText();
            String s_id = enrollStudentId.getText();
//            System.out.println(s_id+":"+s_Name);
            if(!duplicate_id(s_id,s_Name))
            {
                t.write(s_id+":"+s_Name+"\n");
                File f = new File("files/Dataset/"+s_id);
                f.mkdir();
                FileWriter f1 = new FileWriter("files/temp");
                f1.write(s_id);
                f1.close();
                result.setText("Student Added!");
            }
            else
            {
                result.setText("Duplicate id/Name! Retry again!");
                return false;
            }
            t.close();
        }
        catch (Exception t)
        {
            System.out.println(t);
        }
        return true;
    }

    boolean duplicate_id(String id,String name) throws IOException
    {
        File f = new File("files/Student_details");
        BufferedReader br=null;
        try
        {
            br = new BufferedReader(new FileReader(f));
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        String s;
        while((s=br.readLine())!=null)
        {
//            System.out.println(s);
            String temp[] = s.split(":");
            if(temp.length>0)
            {
                if(temp[0].equals(id)||temp[1].equals(name))
                    return true;
            }
        }
        return false;
    }
    @FXML
    private TextField removeStudentId,removeName;
    @FXML
    private Label result1;
    public static void deleteDirectory(File file)
    {
        for (File subfile : file.listFiles()) {
            if (subfile.isDirectory()) {
                deleteDirectory(subfile);
            }
            subfile.delete();
        }
        file.delete();
    }
    public void Student_del_call(ActionEvent e) throws IOException
    {
        String id = removeStudentId.getText(),name=removeName.getText();
        boolean val = Student_del(id,name);
        if(val)
        {
            deleteDirectory(new File("files/Dataset/"+id));
            result1.setText("Student removed successfully!");

        }
        else
        {
            result1.setText("Removal failed!");
        }
//        System.out.println(val);
    }
    public boolean Student_del(String id,String name) throws IOException
    {
        File inputFile = new File("files/Student_details");
        File tempFile = new File("files/Student_details_temp");

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        String lineToRemove = id+":"+name;
        String currentLine;
        boolean chk = false;
        while((currentLine = reader.readLine()) != null) {
            // trim newline when comparing with lineToRemove
            String trimmedLine = currentLine.trim();
            if(trimmedLine.equals(lineToRemove))
            {
                chk=true;
                continue;
            }
            writer.write(currentLine + System.getProperty("line.separator"));
        }
        writer.close();
        reader.close();
        inputFile.delete();
        boolean successful = tempFile.renameTo(inputFile);
        return chk&successful;
    }
    public void Face_reg_page_enter(ActionEvent e)throws IOException
    {
        // Enter the details into the database
        boolean ret = Student_reg();
        if(ret)
        {
            Parent root = FXMLLoader.load(getClass().getResource("Face_Register.fxml"));
            Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
            Scene s = new Scene(root);
            stage.setScene(s);
            stage.show();
        }
    }
    public void RegBackButton(ActionEvent e) throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getResource("Teacher_login.fxml"));
        Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        Scene s = new Scene(root);
        stage.setScene(s);
        stage.show();
    }
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

    long count=0;
    public void captureImage(ActionEvent e)
    {
        //capture the image from the camera feed
        int s;
        chk=false;
        int id=-1;
        try
        {
            BufferedReader br = new BufferedReader(new FileReader("files/temp"));
            String str = br.readLine();
            id = Integer.valueOf(str);
        }
        catch(Exception ae)
        {
            System.out.println(ae);
        }
        String[]  size = new File("files/Dataset"+id).list();
        if(size==null)
            s=0;
        else
            s=size.length+1;

        System.out.println(id);
        saveImage("files/Dataset/"+id+"/"+(s+count)+".png");
        count+=1;
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
    public void saveImage(String f) {
//        String f = "target/classes/com/example/demo1/im1.png";
        Imgcodecs imageCodecs = new Imgcodecs();
        imageCodecs.imwrite(f, matrix);
    }
    public void Student_login(ActionEvent e) throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getResource("Register.fxml"));
        Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        Scene s = new Scene(root);
        stage.setScene(s);
        stage.show();
    }
}
