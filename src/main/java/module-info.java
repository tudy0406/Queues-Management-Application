module pt.project.assignment2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens pt.project.assignment2 to javafx.fxml;
    exports pt.project.assignment2;
}