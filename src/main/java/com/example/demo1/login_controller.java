package com.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class login_controller {
    @FXML
    private Button CancelButton;
    @FXML
    private Label LoginMessageLabel;
    @FXML
    private TextField UsernameTextField;

    @FXML
    private PasswordField PasswordPasswordField;

    public void LoginButtonOnAction(ActionEvent e) throws IOException
    {
        boolean ret=false;
        if(UsernameTextField!=null && PasswordPasswordField!=null)
        {
            if(UsernameTextField.getText().isBlank()==false&& PasswordPasswordField.getText().isBlank()==false)
            {
                LoginMessageLabel.setText("Invalid username and password");
                ret = validateLogin(UsernameTextField.getText(),PasswordPasswordField.getText());
            }
            else
            {
                LoginMessageLabel.setText("Pls enter Username and password");
            }
        }
        if(ret)
        {
            Parent root = FXMLLoader.load(getClass().getResource("Teacher_login.fxml"));
            Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
            Scene s = new Scene(root);
            stage.setScene(s);
            stage.show();
        }
        else
        {
            if(LoginMessageLabel!=null) LoginMessageLabel.setText("Authenticaiton failed");
        }
    }
    public boolean validateLogin(String user,String pwd) throws IOException
    {
        File f = new File("/home/shree/Downloads/demo1/files/Login");
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
            if(temp[0].equals(user) && temp[1].equals(pwd))
                return true;
        }
        return false;
    }
    public void CancelButtonOnAction(ActionEvent e) {
        Stage  stage=(Stage) CancelButton.getScene().getWindow();
        stage.close();
    }
}
