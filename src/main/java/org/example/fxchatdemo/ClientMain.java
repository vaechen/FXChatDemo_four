package org.example.fxchatdemo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.example.fxchatdemo.controllers.ClientLogController;
import org.example.fxchatdemo.controllers.ClientMainController;

import java.io.IOException;

public class ClientMain extends Application {
    private static ClientMainController clientMainController;
    private static ClientLogController clientLogController;

    public static Scene scene_log;
    public static Scene scene_session;
    public static Stage currentStage;

    @Override
    public void init() throws Exception {
        super.init();
        FXMLLoader fxmlLoader_log = new FXMLLoader(getClass().getResource("ClientLog.fxml"));
        scene_log = new Scene(fxmlLoader_log.load());
        clientLogController = fxmlLoader_log.getController();

        FXMLLoader fxmlLoader_session = new FXMLLoader(getClass().getResource("ClientMain.fxml"));
        scene_session = new Scene(fxmlLoader_session.load(), 600, 400);
        clientMainController = fxmlLoader_session.getController();
    }

    @Override
    public void start(Stage stage) throws IOException {
        currentStage = stage;

        stage.setTitle("聊天室客户端!");
        stage.setScene(scene_log);
        stage.getIcons().add(new Image("file:src/main/java/org/example/fxchatdemo/client.png"));
        stage.show();
    }
    @Override
    public void stop() throws Exception {
        try {
            String strIP = "本地IP为:[" + clientMainController.get_InetAddress() + "]的用户退出聊天室";
            clientMainController.sendTheBye(strIP);
            Platform.exit();
            System.exit(0);
        } catch (Exception e) {
            Platform.exit();
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        launch();
    }

    public static void switchToSessionScene() {
        currentStage.setScene(scene_session);
        // 注意，clientMainController的textField_username控件的值要在这里带，因为其初始化是在FXML加载的时候，
        // 带值要找准时机
        clientMainController.set_textField_username(clientLogController.deliver_ToMainController());
    }

    public static String executeGet_textField_ip() {
        return clientMainController.get_textField_ip();
    }
    public static void executeDisplay_label_status(String status) {
        clientMainController.display_label_status(status);
    }
    public static String executeGet_textField_username() {
        return clientMainController.get_textField_username();
    }
    public static String executeGet_textArea_message() {
        return clientMainController.get_textArea_message();
    }
    public static void executeSet_textArea_message(String textArea) {
        clientMainController.set_textArea_message(textArea);
    }
    public static void executeAppend_textArea_reveive(String msg) {
        clientMainController.append_textArea_reveive(msg);
    }
}