module cinemauq.cinemauq {
    requires javafx.controls;
    requires javafx.fxml;

    // FXML necesita entrar a los viewController para llenar los @FXML
    opens cinemauq.cinemauq to javafx.fxml;
    opens cinemauq.cinemauq.viewController to javafx.fxml;

    // JUnit necesita entrar al modelo y a los controller para las pruebas
    opens cinemauq.cinemauq.model to org.junit.platform.commons;
    opens cinemauq.cinemauq.controller to org.junit.platform.commons;

    exports cinemauq.cinemauq;
    exports cinemauq.cinemauq.viewController;
}
