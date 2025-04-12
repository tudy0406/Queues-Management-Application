package pt.project.assignment2.gui;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pt.project.assignment2.businessLogic.SelectionPolicy;
import pt.project.assignment2.businessLogic.SimulationManager;

import java.io.IOException;

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
            int timeLimit = Integer.parseInt(timeLimitField.getText());
            int nbServers = Integer.parseInt(nbServersField.getText());
            int nbClients = Integer.parseInt(nbClientsField.getText());
            int minArrivalTime = Integer.parseInt(minArrivalTimeField.getText());
            int maxArrivalTime = Integer.parseInt(maxArrivalTimeField.getText());
            int minServiceTime = Integer.parseInt(minServiceTimeField.getText());
            int maxServiceTime = Integer.parseInt(maxServiceTimeField.getText());
            SimulationManager simulationManager = new SimulationManager(timeLimit, nbServers, nbClients, minArrivalTime, maxArrivalTime, minServiceTime, maxServiceTime, selectionPolicy);
            Thread thread = new Thread(simulationManager);
            thread.start();
            FXMLLoader loader = new FXMLLoader(SimulationFrameController.class.getResource("/pt/project/assignment2/filelog.fxml"));
            try{
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle("View Log");
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.show();
            }catch(IOException e){
                e.printStackTrace();
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
