package com.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.awt.*;
import java.io.*;

import javafx.stage.Stage;

import javax.swing.text.TableView;
import java.util.*;

public class Report_controller {

    public int Attendance_count(String filename,Integer label)throws FileNotFoundException
    {
        Scanner input = new Scanner( new File( filename ) );
        //String[] lines = filename.split(System.getProperty("line.separator"));
        // char someChar = 'a';
        //int t=lines.length;
        int count = 0;
        while (input.hasNext() )
        {
            String answer = input.nextLine();
            // answer = answer.toLowerCase();
            for ( int i = 11; i < answer.length(); i++ )
            {
                if ( answer.charAt( i ) == label )
                {
                    count++;
                }
            }
//            System.out.println( answer );
        }
        // input.close();
        return count;
    }
    public void joinClassHandler(ActionEvent e)
    {
        System.out.println(e);
    }
    public void backHandler(ActionEvent e) throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getResource("Teacher_login.fxml"));
        Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        Scene s = new Scene(root);
        stage.setScene(s);
        stage.show();
    }
}
