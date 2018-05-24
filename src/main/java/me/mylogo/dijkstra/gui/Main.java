package me.mylogo.dijkstra.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * Created by Dennis Heckmann on 19.05.18
 * Copyright (c) 2018 Dennis Heckmann
 * GitHub: https://github.com/Mylogo
 * Web: http://mylogo.me
 * Mail: contact@mylogo.me | denheckmann@googlemail.com
 * Discord: Mylogo#4884 | Skype: Mylogo55
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("me/mylogo/dijkstra/fxml/hello_frame.fxml"));
        HBox root = loader.load();
        OpeningFrame controller = loader.getController();
        controller.setStage(primaryStage);
        Scene main = new Scene(root);
//        BorderPane root = FXMLLoader.load(getClass().getClassLoader().getResource("me/mylogo/dijkstra/fxml/create_graph.fxml"));
//        Scene main = new Scene(root);
        primaryStage.setTitle("Dijkstra Algorithm");
        primaryStage.setScene(main);
        primaryStage.show();
    }

}
