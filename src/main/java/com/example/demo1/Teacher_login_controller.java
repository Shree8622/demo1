package com.example.demo1;

//package com.example.javafxlogin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;

import java.io.*;

import javafx.stage.Stage;
import javafx.scene.control.TextField;


public class Teacher_login_controller {
    public void BackButton(ActionEvent e) throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        Scene s = new Scene(root);
        stage.setScene(s);
        stage.show();
    }

    public void ReportPage(ActionEvent e) throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getResource("Report.fxml"));
        Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        Scene s = new Scene(root);
        stage.setScene(s);
        stage.show();
    }
    public void TakeAttendence(ActionEvent e) throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getResource("Attendence.fxml"));
        Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        Scene s = new Scene(root);
        stage.setScene(s);
        stage.show();
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