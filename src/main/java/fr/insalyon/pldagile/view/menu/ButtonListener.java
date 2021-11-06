package fr.insalyon.pldagile.view.menu;

import fr.insalyon.pldagile.controller.Controller;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

public class ButtonListener {

    private static Controller controller;

    public ButtonListener(Controller controller) {
        ButtonListener.controller = controller;
    }

    public static void actionPerformed(ActionEvent event) {
        switch (((Button) event.getTarget()).getText()) {
            case ImportView.LOAD_MAP:
                controller.loadMap();
                break;
            case ImportView.LOAD_REQUESTS:
                controller.loadRequests();
                break;
            case ImportView.COMPUTE_TOUR:
                controller.computeTour();
                break;
            case ImportView.GENERATE_ROADMAP:
                controller.generateRoadMap();
                break;
        }

    }
}
