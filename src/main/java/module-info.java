module org.eloy.gymfit_eloy {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.eloy.gymfit_eloy to javafx.fxml;
    exports org.eloy.gymfit_eloy;
}