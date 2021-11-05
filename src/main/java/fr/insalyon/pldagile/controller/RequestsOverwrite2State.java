package fr.insalyon.pldagile.controller;

import fr.insalyon.pldagile.model.CityMap;
import fr.insalyon.pldagile.model.PlanningRequest;
import fr.insalyon.pldagile.model.Tour;
import fr.insalyon.pldagile.view.Window;
import fr.insalyon.pldagile.xml.FileChooseOption;
import fr.insalyon.pldagile.xml.XMLDeserializer;
import fr.insalyon.pldagile.xml.XMLFileOpener;

import java.io.File;

public class RequestsOverwrite2State implements State{
    @Override
    public void loadRequests(Controller controller, CityMap cityMap, PlanningRequest planningRequest, Window window) {
        try {
            File importFile = XMLFileOpener.getInstance().open(FileChooseOption.READ);
            if(importFile != null) {
                window.addStateFollow("Loading the new request ...");
                PlanningRequest newPlanningRequest = new PlanningRequest();
                XMLDeserializer.load(newPlanningRequest, cityMap, importFile);
                controller.setPlanningRequest(newPlanningRequest);
                controller.setTour(new Tour());
                window.clearRequest();
                window.clearTour();
                window.updateRequestFileName(importFile.getName());
                window.renderPlanningRequest(newPlanningRequest);
                controller.setCurrentState(controller.requestsDisplayedState);
            } else {
                controller.setCurrentState(controller.tourComputedState);
            }
        } catch(Exception e) {
            window.addStateFollow("Error when reading the XML requests file " +e.getMessage());
            controller.setCurrentState(controller.tourComputedState);
        }
    }

    @Override
    public void confirm(Controller controller, CityMap citymap, PlanningRequest planningRequest,Tour tour,String result, Window window,ListOfCommands listOfCdes) {
        this.loadRequests(controller, citymap,planningRequest, window);
    }

    @Override
    public void cancel(Controller controller,Tour tour,Window window,ListOfCommands listOfCdes) {
        controller.setCurrentState(controller.tourComputedState);
    }
}
