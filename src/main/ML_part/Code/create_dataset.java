import org.opencv.videoio.VideoCapture; //used for camera access
import org.opencv.objdetect.CascadeClassifier; //Haar cascade
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

import static java.lang.Math.atan;

public class create_dataset {
    static
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    CascadeClassifier face_classifier,leftEye_classifier,rightEye_classifier;
    Scanner sc;
    create_dataset(String face_c_path,String left_eye_path,String right_eye_path)
    {
        face_classifier = new CascadeClassifier(face_c_path);
        leftEye_classifier = new CascadeClassifier(left_eye_path);
        rightEye_classifier = new CascadeClassifier(right_eye_path);
        sc = new Scanner(System.in);
    }
    public static long getFolderSize(File folder)
    {
        long length = 0;
        File[] files = folder.listFiles();
        return files.length;
    }
    Mat Capture()
    {
        VideoCapture cam = new VideoCapture(0);
        Mat img = new Mat();
        cam.read(img);
        cam.release();
        return img;
    }
    Mat crop_img(Mat input,Rect b_box)
    {
        int padding = 5;
        Rect crop = b_box.clone();
        if(crop.x-padding>=0)
            crop.x-=padding;
        if(crop.y-padding>=0)
            crop.y-=padding;
        if(crop.x+crop.width+padding<=input.width())
            crop.width+=padding;
        if(crop.y+crop.height+padding<=input.height())
            crop.height+=padding;

        Mat cropped =  new Mat(input,crop);
        return cropped;
    }
    Mat show_faces(Mat i)
    {
        // Initialising classifier
        MatOfRect faceDetections = new MatOfRect();
        MatOfRect left_eye = new MatOfRect();
        MatOfRect right_eye = new MatOfRect();
        face_classifier.detectMultiScale(i, faceDetections);
        leftEye_classifier.detectMultiScale(i,left_eye);
        rightEye_classifier.detectMultiScale(i,right_eye);
        System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));

        // Drawing boxes

        for (Rect rect : faceDetections.toArray()) {
            Imgproc.rectangle(i,new org.opencv.core.Point(rect.x, rect.y),new org.opencv.core.Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 0, 255), 3);
        }
        for (Rect rect : left_eye.toArray()) {
            Imgproc.rectangle(i,new org.opencv.core.Point(rect.x, rect.y),new org.opencv.core.Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0), 3);
        }
        for (Rect rect : right_eye.toArray()) {
            Imgproc.rectangle(i,new org.opencv.core.Point(rect.x, rect.y),new org.opencv.core.Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255, 0, 0), 3);
        }
        return i;
    }
    void save_faces(String dir_path)
    {
        Mat i = Capture();

        MatOfRect faceDetections = new MatOfRect();
        face_classifier.detectMultiScale(i, faceDetections);

        if(faceDetections.empty())
        {
            System.out.println("Hmm we couldn't see any faces :<\n This is how the image looks... ");
            HighGui.imshow("Image",i);
            HighGui.waitKey(5000);
            System.out.println("Enter Y to continue");

            String c = sc.next();
            if(c.equals("Y"))
                return;
            else
                System.out.println("Exiting from the program..");
            System.exit(1);
        }
        else
        {
            System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));
        }

//        Mat faces = i.clone();
//        for (Rect rect : faceDetections.toArray()) {
//            Imgproc.rectangle(faces,new org.opencv.core.Point(rect.x, rect.y),new org.opencv.core.Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 0, 255), 3);
//        }
//        System.out.println("Close the window to continue!");
//        HighGui.imshow("Detected Faces",faces);
//        HighGui.waitKey(0);

        File folder = new File(dir_path);
        long  size = getFolderSize(folder);

        Mat[] ans = new Mat[faceDetections.toArray().length];
        int j=0;
        for (Rect rect : faceDetections.toArray()) {
            Rect t = rect;
            ans[j] = crop_img(i,t);
        }
        for(Mat t :ans)
        {
            Imgcodecs.imwrite(dir_path+"/"+size+".png",t);
            size+=1;
        }
    }
    void create_LogEntry(String path)
    {
        File dir;
        FileWriter txt = null;
        dir = new File("/media/shree/College/Sem_4/OOPS/OOPS_project/Image_dataset/Preprocessed_images");
        try {
            new File(path+"/Logs.txt").createNewFile();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }

        try {
            txt = new FileWriter(path+"/Logs.txt");
        }
        catch(Exception e)
        {
            System.out.println(e);
        }

        for(File i:dir.listFiles())
        {
            if(i.isDirectory())
            {
                for(File j:i.listFiles())
                {
                    try {
                        txt.write(j.getPath());
                        txt.write(";"+i.getName()+"\n");
                        //System.out.println(j.getPath()+";"+i.getName()+"\n");
                    }
                    catch(Exception e)
                    {
                        System.out.println(e);
                    }
                }
            }
        }
        try {
            txt.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
    Mat rotate_img(Mat img)
    {
        MatOfRect l_eye = new MatOfRect();
        MatOfRect r_eye = new MatOfRect();

        leftEye_classifier.detectMultiScale(img,l_eye);
        rightEye_classifier.detectMultiScale(img,r_eye);

        Rect[] l = l_eye.toArray();
        Rect[] r = l_eye.toArray();

        Point center = new Point(img.cols() / 2, img.rows() / 2);
        double deg = atan( (double)(l[0].y - r[0].y) / (l[0].x - r[0].x) );
        System.out.println(deg);
        Mat rotMat = Imgproc.getRotationMatrix2D( center, deg, 1.0 );
        //Mat res = new Mat();
        Mat res=new Mat(img.rows(),img.cols(),img.type());
        Imgproc.warpAffine( img, res, rotMat, img.size() );
        HighGui.imshow("Rotated",  res);
        HighGui.waitKey(0);
        return res;
    }
    void preprocess_images(String class_dir)
    {
        File f = new File(class_dir);
        File[] list = f.listFiles();
        for(File i:list)
        {
            if(i.isDirectory())
            {
                File[] imgs = i.listFiles();
                for(File j:imgs)
                {
                    Mat img = Imgcodecs.imread(class_dir+"/"+i.getName()+"/"+j.getName());
//                    Mat rotated = rotate_img(img);

                    Mat resized = new  Mat();
                    Imgproc.resize(img,resized,new Size(1024,1024),Imgproc.INTER_LINEAR);

                    Mat grayscale = new Mat();
                    Imgproc.cvtColor(resized,grayscale,Imgproc.COLOR_BGR2GRAY);
                    System.out.println("/media/shree/College/Sem_4/OOPS/OOPS_project/Image_dataset/Preprocessed_images/"+i.getName()+"/"+j.getName());
                    Imgcodecs.imwrite("/media/shree/College/Sem_4/OOPS/OOPS_project/Image_dataset/Preprocessed_images/"+i.getName()+"/"+j.getName(),grayscale);
                }
            }
        }

    }
    public static void main(String[] args) {
        String dir_path = "/media/shree/College/Sem_4/OOPS/OOPS_project/Image_dataset/Classes";
        create_dataset obj = new create_dataset(
                "/media/shree/College/Sem_4/OOPS/OOPS_project/opencv-4.5.5/data/haarcascades/haarcascade_frontalface_alt2.xml",
                "/media/shree/College/Sem_4/OOPS/OOPS_project/opencv-4.5.5/data/haarcascades/haarcascade_lefteye_2splits.xml",
                "/media/shree/College/Sem_4/OOPS/OOPS_project/opencv-4.5.5/data/haarcascades/haarcascade_righteye_2splits.xml"
        );
        System.out.println("Enter your choice\n1.Add to an existing class\n2.Create a new class\n3.Create log entry\n4.Exit");
        int ch;
        ch = obj.sc.nextInt();
        if(ch==1)
        {
            String class_name;
            System.out.println("Enter the existing class name");
            class_name = obj.sc.next();
            boolean found = false;
            File dir = new File(dir_path);
            File files[] = dir.listFiles();
            for(File i:files)
            {
                if(i.getName().equals(class_name))
                {
                    found=true;
                    break;
                }
            }
            if(found)
            {
                System.out.println("Class found!\n Capturing and detecting faces!");
                dir_path+="/"+class_name;
                System.out.println("Capturing for the next 20s!");
                int i=20;
                while(i>0)
                {
                    i--;
                    obj.save_faces(dir_path);
                    try
                    {
                        Thread.currentThread().sleep(2000);
                    }
                    catch(Exception e)
                    {
                        System.out.println("Sleep exception!");
                    }
                }

            }
            else
                System.out.println("Class not found!");
            return;
        }
        else if(ch==2)
        {
            String new_class;
            System.out.println("Enter the new class name");
            new_class = obj.sc.next();
            dir_path+="/"+new_class;
            new File(dir_path).mkdir();
            int i=0;
            while(i>0)
            {
                i--;
                obj.save_faces(dir_path);
                try
                {
                    Thread.currentThread().sleep(2000);
                }
                catch(Exception e)
                {
                    System.out.println("Sleep exception!");
                }
            }
        }
        else if(ch==3)
        {
            obj.create_LogEntry("/media/shree/College/Sem_4/OOPS/OOPS_project/Image_dataset");
        }
        else if(ch==4)
        {
            obj.preprocess_images("/media/shree/College/Sem_4/OOPS/OOPS_project/Image_dataset/Classes");
        }
    }
}