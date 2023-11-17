package org.monarchinitiative.hpo2robot.view;

import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.monarchinitiative.hpo2robot.Launcher;
import org.monarchinitiative.hpo2robot.controller.MainWindowController;
import org.monarchinitiative.hpo2robot.controller.OptionsWindowController;
import org.monarchinitiative.hpo2robot.controller.SynonymPaneController;
import org.monarchinitiative.hpo2robot.model.Options;
import org.monarchinitiative.hpo2robot.controller.BaseController;
import org.monarchinitiative.hpo2robot.model.Synonym;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class ViewFactory {

    private Options options;

    private final HostServices hostServices;

    public ViewFactory(Options options, HostServices services) {
        this.options = options;
        this.hostServices = services;
    }

    /**
     * This constructor is used to initialize the Options the very first time, and
     * is not used by the main app (it is used by
     * {@link org.monarchinitiative.hpo2robot.controller.services.GetOptionsService} ).
     * This is a little ugly, TODO - refactor.
     */
    public ViewFactory() {
        this(new Options(), null); // initialize to default options (empty)
    }




    private Optional<Parent>  initializeBaseStage(BaseController controller) {
        String fxmlDir = "view";
        URL location = getLocation(fxmlDir, controller.getFxmlName());
        FXMLLoader loader = new FXMLLoader(location);
        // This is used to customize the creation of controller injected by javaFX when defining them with fx:controller attribute inside FXML files
        // Using the controller factory instead of setting the controller directly with fxmlLoader.setController() allows us to keep the fx:controller
        // attribute inside FXML files. This makes it easier for IDE to link fxml with controllers and check for errors
        Callback<Class<?>, Object> controllerFactory = type -> {
            // Any controller that needs custom constructor behavior needs to be defined above this check
            if (BaseController.class.isAssignableFrom(type)) {
                // A default behavior for controllerFactory for all classes extends from base controller.
                return controller ;
            } else {
                // default behavior for controllerFactory:
                try {
                    return type.getDeclaredConstructor().newInstance();
                } catch (Exception exc) {
                    exc.printStackTrace();
                    throw new RuntimeException(exc); // fatal, just bail...
                }
            }};

        loader.setControllerFactory(controllerFactory);
        // loader.setController(controller);
        Parent parent;
        try {
            parent = loader.load();
            return  Optional.of(parent);
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private void initializeStage(BaseController controller) {
        Optional<Parent> opt = initializeBaseStage(controller);
        if (opt.isEmpty()) {
            System.err.println("[ERROR] could not initialize stage");
            return;
        }
        Parent parent = opt.get();
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }



    private void initializeStageAndWait(BaseController controller, String title) {
        Optional<Parent> opt = initializeBaseStage(controller);
        if (opt.isEmpty()) {
            System.err.println("[ERROR] could not initialize stage");
            return;
        }
        Parent parent = opt.get();
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setScene(scene);
        if (title != null) {
            stage.setTitle(title);
        }
        stage.showAndWait();
    }

    private void initializeStageAndWait(BaseController controller) {
        initializeStageAndWait(controller, null);
    }


    public void showMainWindow() {
        BaseController controller = new MainWindowController(this, "MainWindow.fxml");
        initializeStage(controller);
    }


    public void showOptionsWindow() {
        OptionsWindowController controller = new OptionsWindowController( this, "OptionsWindow.fxml");
        initializeStageAndWait(controller, "Settings");
        this.options = controller.getOptions();
    }


    /**
     * Retrieves the URL corresponding to the basename of an FXML file
     * @param dir directory where FXML files live
     * @param fxmlName Name of the FXML file
     * @return corresponding URL
     */
    public URL getLocation(String dir, String fxmlName) {
        String path = dir + File.separator + fxmlName;
        return Launcher.class.getResource(path);
    }

    public void closeStage(Stage stage) {
        stage.close();
    }



    public Options getOptions() {
        return this.options;
    }

    public Optional<HostServices> getHostervicesOpt() {
        return Optional.ofNullable(this.hostServices);
    }

    public Optional<Synonym> showAddSynonymWindow() {
        SynonymPaneController controller = new SynonymPaneController( this, "SynonymPane.fxml");
        initializeStageAndWait(controller);
        return controller.getSynonym();
    }

}
