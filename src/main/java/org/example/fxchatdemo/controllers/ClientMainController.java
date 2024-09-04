package org.example.fxchatdemo.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.fxchatdemo.Utils.PBEWithMD5AndDES;
import org.example.fxchatdemo.models.ClientSSLSocketModel;
import java.io.*;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

public class ClientMainController implements Initializable {

    @FXML
    private Button button_connect;

    @FXML
    private Button button_save;

    @FXML
    private Button button_decode;

    @FXML
    private Button button_send;

    @FXML
    private Label label_status;

    @FXML
    private Label label_username;

    @FXML
    private TextArea textArea_message;

    @FXML
    private TextArea textArea_reveive;

    @FXML
    private TextField textField_ip;

    @FXML
    private TextField textField_port;

    @FXML
    private TextField textField_username;

    @FXML
    private TextField textField_password;

    ClientSSLSocketModel clientSSLSocketModel = new ClientSSLSocketModel();

    public ClientMainController() throws UnknownHostException {

    }
    public String get_InetAddress() {
        return clientSSLSocketModel.getIp().getHostAddress();
    }

    public String get_textField_ip() {
        return textField_ip.getText();
    }
    public void display_label_status(String status) {
        label_status.setText(status);
    }
    public String get_textField_username() {
        return textField_username.getText();
    }
    public String get_textArea_message() {
        return textArea_message.getText();
    }
    public void set_textArea_message(String textArea) {
        textArea_message.setText(textArea);
    }
    public void append_textArea_reveive(String msg) {
        textArea_reveive.appendText(msg);
    }

    public void sendTheBye(String msg) {
        clientSSLSocketModel.sendTheBye(msg);
    }

    public void set_textField_username(String username) {
        textField_username.setText(username);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        textArea_message.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                String strMsg = textArea_message.getText();
                if(!strMsg.isEmpty()){
                    try {
                        clientSSLSocketModel.sendTheMsg();
                    } catch (UnknownHostException e) {
                        throw new RuntimeException(e);
                    }
                }
                //keyEvent.consume();   一直是true
            }
        });
    }

    @FXML
    void connectTheServer(MouseEvent event) {
        if (label_status.getText().equals("当前状态：已连接")) {
            //调用Alert提示一下
        } else {
            String host = textField_ip.getText();
            int port = Integer.parseInt(textField_port.getText());
            clientSSLSocketModel.connect(host, port);
        }
    }

    //将文件保存至选择的文件内
    @FXML
    void pressToSave(MouseEvent event) {
        FileChooser chooser = new FileChooser(); // 创建一个文件对话框
        chooser.setTitle("保存文件"); // 设置文件对话框的标题
        chooser.setInitialDirectory(new File(".")); // 设置文件对话框的初始目录
        // 创建一个文件类型过滤器
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("文本文件(*.txt)", "*.txt");
        // 给文件对话框添加文件类型过滤器
        chooser.getExtensionFilters().add(filter);
        File file = chooser.showSaveDialog(new Stage()); // 显示文件保存对话框
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(file.getAbsolutePath(), false);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
            String cText = PBEWithMD5AndDES.enc(textField_password.getText(), textArea_reveive.getText());
            bufferedWriter.write(cText, 0, cText.length());
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void pressToDecode(MouseEvent event) {
        if (!textField_password.getText().isEmpty()) {
            FileChooser chooser = new FileChooser(); // 创建一个文件对话框
            chooser.setTitle("解密文件"); // 设置文件对话框的标题
            chooser.setInitialDirectory(new File(".")); // 设置文件对话框的初始目录
            // 创建一个文件类型过滤器
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("文本文件(*.txt)", "*.txt");
            // 给文件对话框添加文件类型过滤器
            chooser.getExtensionFilters().add(filter);
            File file = chooser.showOpenDialog(new Stage()); // 显示文件打开对话框

            FileInputStream fileInputStream;
            try {
                fileInputStream = new FileInputStream(file.getAbsolutePath());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                    //sb.append(line).append("\n");不能这样用
                }
                String pText = PBEWithMD5AndDES.dec(textField_password.getText(), sb.toString());
                textArea_reveive.appendText(pText);
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                textField_password.setText("口令错误！");
                throw new RuntimeException(e);
            }
        } else {
            textField_password.setText("请输入口令");
        }
    }

    @FXML
    void sendTheMsg(MouseEvent event) throws UnknownHostException {
        clientSSLSocketModel.sendTheMsg();
    }
}