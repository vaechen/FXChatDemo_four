package org.example.fxchatdemo.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.fxchatdemo.Utils.PBEWithMD5AndDES;
import org.example.fxchatdemo.models.ServerSSLSocketModel;

import java.io.*;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerMainController {

    @FXML
    private Button button_decode;

    @FXML
    private Button button_port;

    @FXML
    private Button button_save;

    @FXML
    private Button button_send;

    @FXML
    private Label label_count;

    @FXML
    private Label label_status;

    @FXML
    private TextArea textArea_message;

    @FXML
    private TextArea textArea_reveive;

    @FXML
    private TextField textField_password;

    @FXML
    private TextField textField_port;
    private int NUM = 0;
    private ServerSSLSocketModel serverSSLSocketModel = new ServerSSLSocketModel();
    public void display_label_count() {
        label_count.setText("当前客户端连接数: " + NUM);
    }
    public void add_label_count(int i) {
        NUM += i;
        label_count.setText("当前客户端连接数: " + NUM);
    }
    public void display_label_status(String status) {
        label_status.setText(status);
    }

    public void append_textArea_reveive(String msg) {
        textArea_reveive.appendText(msg);
    }

    @FXML
    void openThePort(MouseEvent event) {
        if (label_status.getText().equals("当前状态: 服务已开启")) {
            //调用Alert提示一下
        } else {
            serverSSLSocketModel.openThePort(Integer.parseInt(textField_port.getText()));
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
        String strMsg = textArea_message.getText();
        if (!strMsg.isEmpty()) {
            // SimpleDateFormat日期格式化类，指定日期格式为"年-月-日  时:分:秒",例如"2015-11-06 13:50:26"
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            // 获取当前系统时间，并使用日期格式化类格式化为指定格式的字符串
            String strTime = dateFormat.format(new Date());
            // 将时间和信息添加到信息链表集合中
            String msg = "<== " + strTime + " ==>\n[Server]说：" + strMsg;

            textArea_reveive.appendText(msg + "\n");

            serverSSLSocketModel.sendTheMsg(msg + "\n");

            textArea_message.setText("");
        }
    }
}