package me.mylogo.dijkstra.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import me.mylogo.dijkstra.graph.Graph;
import me.mylogo.dijkstra.point.Point;
import me.mylogo.dijkstra.point.PointGraphParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Dennis Heckmann on 19.05.18
 * Copyright (c) 2018 Dennis Heckmann
 * GitHub: https://github.com/Mylogo
 * Web: http://mylogo.me
 * Mail: contact@mylogo.me | denheckmann@googlemail.com
 * Discord: Mylogo#4884 | Skype: Mylogo55
 */
public class OpeningFrame {

    @FXML
    private Label errorLabel;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void onOpenFileClick(MouseEvent e) {
        FileChooser chooser = new FileChooser();
        chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        File selected = chooser.showOpenDialog(stage);
        if (selected != null) {
            PointGraphParser fileParser = new PointGraphParser();
            try {
                Graph<Point> pointGraph = fileParser.parseFile(new FileInputStream(selected));
                // new CreateGraph scene
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("me/mylogo/dijkstra/fxml/create_graph.fxml"));
                BorderPane root = loader.load();
                CreateGraph controller = loader.getController();
                Scene newScene = new Scene(root);
                controller.setGraph(pointGraph);
                controller.setStage(stage);
                stage.setScene(newScene);
            } catch (FileNotFoundException e1) {
                errorLabel.setText("File not found / inaccessible");
            } catch (IllegalArgumentException e1) {
                errorLabel.setText(e1.getMessage());
            } catch (Exception e1) {
                e1.printStackTrace();
                errorLabel.setText("File could not be read:" + e1.getMessage());
            }
        } else {
            errorLabel.setText("No file selected");
        }
    }

    public void onCreateCustomGraphClick(MouseEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("me/mylogo/dijkstra/fxml/create_graph.fxml"));
            BorderPane root = loader.load();
            CreateGraph controller = loader.getController();
            Scene newScene = new Scene(root);
            controller.setGraph(new Graph<>());
            controller.setStage(stage);
            stage.setScene(newScene);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}
