package fr.insalyon.pldagile.view.menu;

import fr.insalyon.pldagile.PickyApplication;
import fr.insalyon.pldagile.controller.Controller;
import fr.insalyon.pldagile.xml.FileChooseOption;
import fr.insalyon.pldagile.xml.XMLDeserializer;
import fr.insalyon.pldagile.xml.XMLFileOpener;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.File;

public class ImportView extends Region {

    private static Controller controller = null;

    private Button importMapButton;
    private Button importPickupButton;
    private Button computeButton;
    private Label importMapLabel;
    private Label importPickupLabel;

    protected static final String LOAD_MAP = "Import map";
    protected static final String LOAD_REQUESTS = "Import Requests";
    protected static final String COMPUTE_TOUR = "Compute tour";


    private final String[] buttonTexts = new String[]{LOAD_MAP, LOAD_REQUESTS, COMPUTE_TOUR};

    public ImportView (Controller controller) {
        ImportView.controller = controller;


        //TODO Move outside of constructor with function calls
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        Label titleLabel = new Label("Import");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        gridPane.add(titleLabel, 0,0,2,1);
        GridPane.setHalignment(titleLabel, HPos.CENTER);
        GridPane.setMargin(titleLabel, new Insets(0, 0,10,0));

        importMapButton = new Button(LOAD_MAP);
        importPickupButton = new Button(LOAD_REQUESTS);
        gridPane.add(importMapButton, 0, 1, 1, 1);
        gridPane.add(importPickupButton, 1, 1, 1, 1);

        importMapLabel = new Label("No file imported yet");
        importPickupLabel = new Label("No file imported yet");
        gridPane.add(importMapLabel, 0, 2, 1, 1);
        gridPane.add(importPickupLabel, 1, 2, 1, 1);


        computeButton = new Button(COMPUTE_TOUR);
        computeButton.setPrefHeight(40);
        computeButton.setDefaultButton(true);
        computeButton.setPrefWidth(100);
        gridPane.add(computeButton, 0, 3, 2, 1);
        GridPane.setHalignment(computeButton, HPos.CENTER);
        GridPane.setMargin(computeButton, new Insets(10, 0,0,0));

        //TODO Start TSP resolution algorithm
        computeButton.setOnAction(this::actionPerformed);

        this.getChildren().add(gridPane);


        importMapButton.setOnAction(this::actionPerformed);
        importPickupButton.setOnAction(this::actionPerformed);

    }
    public Button getImportMapButton() {
        return importMapButton;
    }

    public Button getImportPickupButton() {
        return importPickupButton;
    }

    public Button getComputeButton() {
        return computeButton;
    }

    private void actionPerformed(ActionEvent event){
        switch (((Button) event.getTarget()).getText()){
            case LOAD_MAP:
                controller.loadMap(); break;
            case LOAD_REQUESTS:
                controller.loadRequests(); break;
            case COMPUTE_TOUR:
                controller.computeTour(); break;
        }

    }

}