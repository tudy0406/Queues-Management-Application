package pt.project.assignment2.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import pt.project.assignment2.businessLogic.SimulationManager;
import pt.project.assignment2.config.Constants;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileLogController {
    @FXML
    public TextArea logTextArea;

    public void initialize(){

        Thread fileWatcher = new Thread(() -> {
            while (SimulationManager.getRunningState()) {
                try {
                    String content = Files.readString(Path.of(Constants.FILE_NAME));

                    Platform.runLater(() -> {
                        logTextArea.setText(content);
                        logTextArea.setScrollTop(Double.MAX_VALUE);
                    });

                    Thread.sleep(Constants.SLEEP_TIME/2);
                } catch (IOException e) {
                    System.err.println("Error reading file: " + e.getMessage());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        fileWatcher.setDaemon(true);
        fileWatcher.start();
    }

}
