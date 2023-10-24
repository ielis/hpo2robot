package org.monarchinitiative.controller;


import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.ResourceBundle;


/**
 * Provide user entry text area for the definition of comment together with some quality control.
 * Note that this widget expects the controller to set the initial name of the button to ewither
 * "Create definition" or "Create comment"; once the user has entered some text, the buttons names
 * will change to "Edit definition" or "Edit comment". This is currently a bit of a hack and could
 * be generalized.
 * @author Peter Robinson
 */
public class ValidatingTextEntryPaneController implements Initializable {
    @FXML
    private Label errorLabel;

    @FXML
    private Button validatingButton;

    @FXML
    private Label textSummary;
    
    private StringProperty userText;

    private BooleanProperty isValidProperty;

    public static String CREATE_DEFINITION = "Create definition";
    public static String EDIT_DEFINITION = "Edit definition";
    public static String CREATE_COMMENT = "Create comment";
    public static String EDIT_COMMENT = "Edit comment";



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        isValidProperty = new SimpleBooleanProperty(false);
        userText = new SimpleStringProperty("");
        validatingButton.setOnAction(e -> {
            String text = getUserStringFromTextArea("Enter text", "Enter the text of the definition (comment) here. " +
                    "Stray white space and newlines will be remove automatically.");
            userText.set(text);
            int N = Math.min(50, text.length());
            textSummary.setText(text.substring(0,N) + (text.length() > N ? "..." : ""));
            validateText(text);
            validatingButton.setText("Edit Definition");
        });
        validatingButton.setText("Create Definition");
        // red text for error messages
        errorLabel.setTextFill(Color.color(1, 0, 0));
    }

    private void validateText(String text) {
        byte[] bytes = text.getBytes(StandardCharsets.US_ASCII);
        String decodedLine = new String(bytes);
        boolean nonStandardChar = !text.equals(decodedLine);

        if (text.isEmpty()) {
            setInvalid("Enter new term label");
        } else if (text.contains("  ")) {
            setInvalid("Label must not contain multiple consecutive spaces.");
        } else if (text.startsWith(" ")) {
            setInvalid("Label must not start with space.");
        } else if (! text.endsWith(".")) {
            setInvalid("Text must end end with period.");
        } else if (nonStandardChar) {
            setInvalid("Text contains a non-standard character encoding. Please remove it.");
        } else {
            setValid();
        }
        if (validatingButton.getText().equals(CREATE_DEFINITION)) {
            validatingButton.setText(EDIT_DEFINITION);
        } else if (validatingButton.getText().equals(CREATE_COMMENT)) {
            validatingButton.setText(EDIT_COMMENT);
        }
    }

    /**
     * Gets a string from the user that the user can enter into a TextArea.
     * Automatically removes new lines and extra whitespace and trims trailing/leading white
     * space. If there is a problem, return an empty string.
     * @param title Title of the window
     * @param header - explanatory text
     * @return user-entered string
     */
    private String getUserStringFromTextArea(String title, String header) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(20, 150, 10, 10));

        final TextArea userTextField = new TextArea();
        userTextField.setPrefRowCount(10);
        userTextField.setPrefColumnCount(100);
        userTextField.setWrapText(true);
        userTextField.setPrefWidth(150);
        userTextField.textProperty().addListener( // ChangeListener
                (observable, oldValue, newValue) -> {
                    String txt = newValue.replaceAll("\\n", " ");
                    txt = txt.replaceAll("  ", " ");
                    txt = txt.trim();
                    userTextField.setText(txt);
                });
        userTextField.setText(this.userText.get());

        //Node okButton = dialog.getDialogPane().lookupButton(okButtonType);
        vbox.getChildren().add(userTextField);
        dialog.getDialogPane().setContent(vbox);
        Platform.runLater(userTextField::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return userTextField.getText();
            } else {
                return "";
            }
        });
        Optional<String> opt = dialog.showAndWait();
        return opt.orElse("");
    }




    private void setInvalid(String message) {
        isValidProperty.set(false);
        errorLabel.setText(message);
        validatingButton.setStyle("-fx-text-box-border: red; -fx-focus-color: red ;");
    }

    private void setValid() {
        isValidProperty.set(true);
        errorLabel.setText("");
        validatingButton.setStyle("-fx-text-box-border: green; -fx-focus-color: green ;");
    }


    public Label getErrorLabel() {
        return errorLabel;
    }

    public Button getValidatingButton() {
        return validatingButton;
    }

    public Label getTextSummary() {
        return textSummary;
    }


    public StringProperty userTextProperty() {
        return userText;
    }

    public boolean isIsValidProperty() {
        return isValidProperty.get();
    }

    public BooleanProperty isValidPropertyProperty() {
        return isValidProperty;
    }

    public void initButtonLabel(String label) {
        validatingButton.setText(label);
    }
}
