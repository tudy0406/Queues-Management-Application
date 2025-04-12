module pt.project.assignment2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;

    opens pt.project.assignment2 to javafx.fxml;
    exports pt.project.assignment2;
    exports pt.project.assignment2.gui;
    opens pt.project.assignment2.gui to javafx.fxml;


}