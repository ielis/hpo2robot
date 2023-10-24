package org.monarchinitiative.controller;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeView;
import javafx.scene.web.WebView;
import org.monarchinitiative.Hpo2RobotManager;
import org.monarchinitiative.view.ValidatingPane;
import org.monarchinitiative.view.ValidatingTextEntryPane;
import org.monarchinitiative.view.ViewFactory;

import java.net.URL;
import java.util.ResourceBundle;


public class MainWindowController extends BaseController implements Initializable {



    @FXML
    private TreeView<String> emailTreeview;

    @FXML
    public ValidatingPane termLabelValidator;

    @FXML
    public ValidatingTextEntryPane definitionPane;
    @FXML
    public ValidatingTextEntryPane commentPane;

    @FXML
    private TableView<?> emailsTableview;

    public MainWindowController(Hpo2RobotManager emailManager, ViewFactory viewFactory, String fxmlName) {
        super(emailManager, viewFactory, fxmlName);
    }

    @FXML
    void optionsAction() {
        this.viewFactory.showOptionsWindow();
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Main init");
        termLabelValidator.setFieldLabel("New Term Label");
        definitionPane.initializeButtonText(ValidatingTextEntryPaneController.CREATE_DEFINITION);
        commentPane.initializeButtonText(ValidatingTextEntryPaneController.CREATE_COMMENT);
    }

    private void setUpEmailTreeview() {
        emailTreeview.setRoot(emailManager.getFoldersRoot());
        emailTreeview.setShowRoot(false);
    }
}
