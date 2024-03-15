module com.example.chatenligne {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;

    opens com.example.chatenligne to javafx.fxml;
    exports com.example.chatenligne;
}