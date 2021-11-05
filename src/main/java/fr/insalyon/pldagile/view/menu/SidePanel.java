package fr.insalyon.pldagile.view.menu;

import fr.insalyon.pldagile.controller.Controller;
import javafx.scene.layout.*;

public class SidePanel extends Region {
    private Controller controller;
    private GridPane mainBorderPane;

    public SidePanel(Controller controller) {
        this.controller = controller;
    }

    public void MainSidePanel() {
        ImportView importView = new ImportView(controller);
        RequestMenuView requestView = new RequestMenuView(controller);
        mainBorderPane = new GridPane();
        mainBorderPane.add(importView, 0, 0, 1, 1);
        mainBorderPane.add(requestView, 0, 1, 1, 1);
        mainBorderPane.getStyleClass().add("side-panel");
        this.getChildren().add(mainBorderPane);
    }

    public void ModifyPanel() {
        ModifyView modify = new ModifyView(controller);
        mainBorderPane = new GridPane();
        mainBorderPane = new GridPane();
        mainBorderPane.add(modify, 0, 0, 1, 1);
        mainBorderPane.getStyleClass().add("side-panel");
        this.getChildren().add(mainBorderPane);
    }
}
