package fr.insalyon.pldagile.controller;


import fr.insalyon.pldagile.model.CityMap;
import fr.insalyon.pldagile.model.PlanningRequest;
import fr.insalyon.pldagile.model.Tour;
import fr.insalyon.pldagile.view.Window;

public interface State {

    public default void loadMap(Controller controller, CityMap citymap, Window window) {};

    public default void loadRequests(Controller controller,CityMap cityMap, PlanningRequest planningRequest, Window window){};

    public default void computeTour(Controller controller, CityMap cityMap, PlanningRequest planningRequest, Tour tour, Window window){};

    public default void confirm(Controller controller, CityMap citymap, PlanningRequest planningRequest, Window window){};

    public default void cancel(Controller controller){};

    public default void modify(Controller controller, Window window){};
}