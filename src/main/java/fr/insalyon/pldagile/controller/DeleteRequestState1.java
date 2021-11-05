package fr.insalyon.pldagile.controller;

import fr.insalyon.pldagile.model.CityMap;
import fr.insalyon.pldagile.model.PlanningRequest;
import fr.insalyon.pldagile.model.Request;
import fr.insalyon.pldagile.model.Tour;
import fr.insalyon.pldagile.view.Window;

public class DeleteRequestState1 implements State{
    @Override
    public void deleteRequest(Controller controller,CityMap citymap, Tour tour,  Request request, Window window,ListOfCommands listOfCdes) {
        if(request != null){
            listOfCdes.add(new DeleteRequestCommand(citymap,tour,request));
            window.renderTour(tour);
            window.orderListRequests(tour.getSteps(), tour.getRequests(), tour.getDepot());
            window.activeItemListener();
            controller.setCurrentState(controller.tourComputedState);
            window.showWarningAlert("Modification", "Suppresion successfully completed", null);
        } else {
            window.showWarningAlert("How to delete a request", "Request number unknown, please try again", null);
        }
    }

    @Override
    public void modifyClick(Controller controller, Long idRequest, String type, int stepIndex, Window window) {
        if(idRequest != -1){
            controller.deleteRequest(idRequest);
        }
    }
}
