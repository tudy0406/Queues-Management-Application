package pt.project.assignment2.gui;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pt.project.assignment2.businessLogic.SelectionPolicy;
import pt.project.assignment2.businessLogic.SimulationManager;
import pt.project.assignment2.config.Constants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SimulationFrameController {
    @FXML
    public Button simulateBtn;
    public TextField timeLimitField;
    public TextField nbServersField;
    public TextField nbClientsField;
    public TextField minArrivalTimeField;
    public TextField maxArrivalTimeField;
    public TextField minServiceTimeField;
    public TextField maxServiceTimeField;
    public AnchorPane simulationPane;
    public ChoiceBox<String> strategyChoiceBox;
    public TextArea simulationTextArea;

    private SelectionPolicy selectionPolicy = SelectionPolicy.SHORTEST_TIME;
    public void initialize() {
        ObservableList<String> strategyList = FXCollections.observableArrayList(
                "Shortest Time",
                "Shortest Queue"
        );
        strategyChoiceBox.setItems(strategyList);
        strategyChoiceBox.setValue(strategyList.getFirst());
        strategyChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            handleStrategyChange(newValue);
        });
    }

    public void onSimulateBtnClick(){
        if(!checkFields()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all the fields!");
            alert.showAndWait();
        }else{
            try {
                int timeLimit = Integer.parseInt(timeLimitField.getText());
                int nbServers = Integer.parseInt(nbServersField.getText());
                int nbClients = Integer.parseInt(nbClientsField.getText());
                int minArrivalTime = Integer.parseInt(minArrivalTimeField.getText());
                int maxArrivalTime = Integer.parseInt(maxArrivalTimeField.getText());
                int minServiceTime = Integer.parseInt(minServiceTimeField.getText());
                int maxServiceTime = Integer.parseInt(maxServiceTimeField.getText());
                if(nbServers < 0 || nbClients < 0 || minArrivalTime < 0 || maxArrivalTime < 0 || minServiceTime < 0
                        || minArrivalTime > maxArrivalTime || minServiceTime > maxServiceTime || maxArrivalTime > timeLimit || maxServiceTime > timeLimit){
                    AlertController.showAlert("Invalid Input!");
                }else{
                    SimulationManager simulationManager = new SimulationManager(timeLimit, nbServers, nbClients, minArrivalTime, maxArrivalTime, minServiceTime, maxServiceTime, selectionPolicy);
                    Thread thread = new Thread(simulationManager);
                    thread.start();
                    Thread fileWatcher = new Thread(() -> {
                        while (SimulationManager.getRunningState()) {
                            try {
                                String content = Files.readString(Path.of(Constants.FILE_NAME));

                                Platform.runLater(() -> {
                                    simulationTextArea.setText(content);
                                    simulationTextArea.setScrollTop(Double.MAX_VALUE);
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
            }catch(NumberFormatException e){
                AlertController.showAlert("Invalid Input!");
            }

        }
    }
    private boolean checkFields(){
        return !timeLimitField.getText().isEmpty() && !nbServersField.getText().isEmpty() && !nbClientsField.getText().isEmpty() && !minArrivalTimeField.getText().isEmpty() && !maxArrivalTimeField.getText().isEmpty() && !minServiceTimeField.getText().isEmpty() && !maxServiceTimeField.getText().isEmpty();
    }

    private void handleStrategyChange(String newStrategy){
        if(newStrategy.equals("Shortest Time")){
            selectionPolicy = SelectionPolicy.SHORTEST_TIME;
        }else{
            selectionPolicy = SelectionPolicy.SHORTEST_QUEUE;
        }
    }
}
