package org.example.fxchatdemo.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.example.fxchatdemo.ClientMain;
import org.example.fxchatdemo.Users.User;

public class ClientLogController {

    @FXML
    private Button button_login;

    @FXML
    private Button button_register;

    @FXML
    private TextField textField_QQmail;

    @FXML
    private PasswordField textField_password;

    @FXML
    private TextField textField_username;

    @FXML
    private Label label_prompt;

    @FXML
    void loginTheClient(MouseEvent event) throws ClassNotFoundException {
        User user = new User();
        if(user.selectUser(textField_QQmail.getText(), textField_username.getText(), textField_password.getText())){
            ClientMain.switchToSessionScene();
        } else {
            label_prompt.setText("输入错误或不存在！");
        }
    }

    @FXML
    void registerTheUser(MouseEvent event) throws ClassNotFoundException {
        User user = new User();
        int n = user.registerUser(textField_QQmail.getText(), textField_username.getText(), textField_password.getText());
        if (1 == n) {
            label_prompt.setText("注册成功！");
        } else if(2 == n){
            label_prompt.setText("该邮箱已注册！");
        } else {
            label_prompt.setText("注册失败！");
        }
    }
    public String deliver_ToMainController() {
        return textField_username.getText();
    }
}
