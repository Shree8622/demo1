package com.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.*;

import javafx.stage.Stage;

public class Report_controller {


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
