package com.example.demo1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.awt.*;
import java.io.*;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import java.net.URL;
import java.sql.SQLOutput;
import java.util.*;

public class Report_controller {
    Integer lines;
    public int Attendance_count(String filename,Integer label)throws FileNotFoundException
    {
        Scanner input = new Scanner( new File( filename ) );
        //String[] lines = filename.split(System.getProperty("line.separator"));
        // char someChar = 'a';
        //int t=lines.length;
        int count = 0;
        this.lines=0;
        while (input.hasNext() )
        {
            lines+=1;
            String answer = input.nextLine();
            // answer = answer.toLowerCase();
            String val = answer.substring(11);
            String split_arr[] = val.split("/");
            for ( String s:split_arr)
            {
                //System.out.print(answer.charAt(i));
                if ( s.equals(label.toString()) )
                {
                    count++;
                }
            }
            System.out.println();
        }
        // input.close();
        return count;
    }
    @FXML private Label Name;
    @FXML private Label T_C;
    @FXML private Label C_A;
    @FXML private TextField student_name;
    @FXML private TextField Roll_no;
    public void joinClassHandler(ActionEvent e) throws IOException
    {
        Name.setText(student_name.getText());
        Integer x = Attendance_count("files/Attendence_records/DAA",Integer.parseInt(Roll_no.getText()));
        C_A.setText(x.toString());
        T_C.setText(lines.toString());

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
