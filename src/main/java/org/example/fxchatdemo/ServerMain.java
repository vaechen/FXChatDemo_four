package org.example.fxchatdemo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.example.fxchatdemo.controllers.ServerMainController;

import java.io.IOException;

public class ServerMain extends Application {
    private static ServerMainController serverMainController;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ServerMain.class.getResource("ServerMain.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);

        serverMainController = fxmlLoader.getController();

        stage.setTitle("聊天室服务端!");
        stage.setScene(scene);
        stage.getIcons().add(new Image("file:src/main/java/org/example/fxchatdemo/server.png"));
        stage.show();
    }
    @Override
    public void stop() throws Exception {
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch();
    }

    public static ServerMainController getServerMainController() {
        return serverMainController;
    }

    public static void executeAddlabel_count(int i) {
        serverMainController.add_label_count(i);
    }

    public static void executeAppendtextArea_reveive(String msg) {
        serverMainController.append_textArea_reveive(msg);
    }
    
    public static void executeDisplay_label_count() {
        serverMainController.display_label_count();
    }
    public static void executeDisplay_label_status(String status) {
        serverMainController.display_label_status(status);
    }
}