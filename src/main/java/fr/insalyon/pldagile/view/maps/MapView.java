/*
 * Copyright (c) 2016 - 2018, Gluon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL GLUON BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package fr.insalyon.pldagile.view.maps;

import fr.insalyon.pldagile.controller.Controller;
import fr.insalyon.pldagile.model.CityMap;
import fr.insalyon.pldagile.model.Coordinates;
import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * This is the top UI element of the map component. The center location and the
 * zoom level of the map can be altered by input events (mouse/touch/gestures)
 * or by calling the methods setCenter and setZoom.
 */
public class MapView extends Region implements PropertyChangeListener {

    private static final Logger logger = Logger.getLogger( MapView.class.getName() );
    public static final double ZOOM = 14;
    private final BaseMap baseMap;
    private Timeline timeline;
    private final List<MapLayer> layers = new LinkedList<>();
    private final Rectangle clip;
    private MapPoint centerPoint = null;
    private boolean zooming = false;
    private boolean enableDragging = false;
    private double maxZoomOut = 0.0D;
    private double maxZoomFactor = 0.0D;

    /**
     * Create a MapView component.
     */
    public MapView(Controller controller) {
        baseMap = new BaseMap();
        getChildren().add(baseMap);
        setMaxZoomFactor(0.05);
        //registerInputListeners();
        controller.getPclCityMap().addPropertyChangeListener(this);
        baseMap.centerLat().addListener(o -> markDirty());
        baseMap.centerLon().addListener(o -> markDirty());
        clip = new Rectangle();
        this.setClip(clip);
        this.layoutBoundsProperty().addListener(e -> {
            // in case our assigned space changes, AND in case we are requested
            // to center at a specific point, we need to re-center.
            if (centerPoint != null) {
                // we will set the center to a slightly different location first, in order 
                // to trigger the invalidationListeners.
                setCenter(centerPoint.getLatitude() + .00001, centerPoint.getLongitude() + .00001);
                setCenter(centerPoint);
            }
        });
    }


    private void registerInputListeners() {
        logger.warning("Register listeners" +
                "");
        setOnMousePressed(t -> {
            if (zooming) return;
            baseMap.x0 = t.getX();
            baseMap.y0 = t.getY();
            System.out.println("point :" + t.getX() + "y : " + t.getY());
            centerPoint = null; // once the user starts moving, we don't track the center anymore.
            // dragging is enabled only after a pressed event, to prevent dragging right after zooming
            enableDragging = true;
        });
       setOnMouseDragged(t -> {
            if (zooming || !enableDragging) {
                return;
            }
            baseMap.moveX(baseMap.x0 - t.getX());
            baseMap.x0 = t.getX();
            baseMap.moveY(baseMap.y0 - t.getY());
            baseMap.y0 = t.getY();
        });
        setOnMouseReleased(t -> enableDragging = false);
        setOnZoomStarted(t -> {
            zooming = true;
            enableDragging = false;
        });
        setOnZoomFinished(t -> zooming = false);
        setOnZoom(t -> {
            System.out.println("Zoom factor = " + (t.getZoomFactor() - 1));
            boolean allowDezoom = baseMap.canZoomOut(maxZoomOut);
            boolean isZooming = t.getZoomFactor() > 0.0;
            double zoomFactoring;
            if (t.getZoomFactor() - 1 > 0){
                zoomFactoring = maxZoomFactor;
            } else {
                zoomFactoring =  0 - maxZoomFactor;
            }
            if (isZooming || allowDezoom) {
                baseMap.zoom(zoomFactoring, t.getX(), t.getY());
            }
        });
        setOnScroll(t -> {
            double delta = t.getDeltaY() > 1 ? .1 : t.getDeltaY() < -1 ? -.1 : 0;
            boolean allowDezoom = baseMap.canZoomOut(maxZoomOut);
            boolean isZooming = delta > 0.0;
            // the delta factor get 2 values : 0.1 & -0.1
            // t.getDeltaY gets only 2 values : 17.77 & -17.77
            if (delta > 0){
                delta = maxZoomFactor;
            } else {
                delta = 0 - maxZoomFactor;
            }
            if (isZooming || allowDezoom) {
                baseMap.zoom(delta, t.getX(), t.getY());
            }
        });
    }

    public void setMaxZoomOut(double maxZoomOut) {
        this.maxZoomOut = maxZoomOut;
    }

    public void setMaxZoomFactor(double maxZoomFactor){
        this.maxZoomFactor = maxZoomFactor;    }

    /**
     * Get the position on the map represented by a given coordinate
     *
     * @param sceneX x coordinate
     * @param sceneY y coordinate
     * @return map position
     */
    public MapPoint getMapPosition(double sceneX, double sceneY) {
        return baseMap.getMapPosition(sceneX, sceneY);
    }

    /**
     * Request the map to set its zoom level to the specified value. The map
     * considers this request, but it does not guarantee the zoom level will be
     * set to the provided value
     *
     * @param zoom the requested zoom level
     */
    public void setZoom(double zoom) {
        baseMap.setZoom(zoom);
    }

    /**
     * Returns the preferred zoom level of this map.
     *
     * @return the zoom level
     */
    public double getZoom() {
        return baseMap.getZoom();
    }

    /**
     * Request the map to position itself around the specified center
     *
     * @param mapPoint
     */
    public void setCenter(MapPoint mapPoint) {
        setCenter(mapPoint.getLatitude(), mapPoint.getLongitude());
    }

    /**
     * Returns the center point of this map
     *
     * @return the center point
     */
    public MapPoint getCenter() {
        Point2D center = baseMap.getCenter();
        return new MapPoint(center.getX(), center.getY());
    }

    /**
     * Request the map to position itself around the specified center
     *
     * @param lat
     * @param lon
     */
    public void setCenter(double lat, double lon) {
        this.centerPoint = new MapPoint(lat, lon);
        baseMap.setCenter(lat, lon);
    }

    /**
     * Add a new layer on top of this map. Layers are displayed in order of
     * addition, with the last added layer to be on top
     *
     * @param child
     */
    public void addLayer(MapLayer child) {
        child.setBaseMap(this.baseMap);
        layers.add(child);
        this.getChildren().add(child);
    }

    /**
     * Removes the specified layer from the map
     *
     * @param child
     */
    public void removeLayer(MapLayer child) {
        layers.remove(child);
        this.getChildren().remove(child);
    }

    /**
     * Wait a bit, then move to the specified mapPoint in seconds time
     *
     * @param waitTime the time to wait before we start moving
     * @param mapPoint the destination of the move
     * @param seconds  the time the move should take
     */
    public void flyTo(double waitTime, MapPoint mapPoint, double seconds) {
        if ((timeline != null) && (timeline.getStatus() == Status.RUNNING)) {
            timeline.stop();
        }
        double currentLat = baseMap.centerLat().get();
        double currentLon = baseMap.centerLon().get();
        timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(baseMap.prefCenterLat(), currentLat), new KeyValue(baseMap.prefCenterLon(), currentLon)),
                new KeyFrame(Duration.seconds(waitTime), new KeyValue(baseMap.prefCenterLat(), currentLat), new KeyValue(baseMap.prefCenterLon(), currentLon)),
                new KeyFrame(Duration.seconds(waitTime + seconds), new KeyValue(baseMap.prefCenterLat(), mapPoint.getLatitude()), new KeyValue(baseMap.prefCenterLon(), mapPoint.getLongitude(), Interpolator.EASE_BOTH))
        );
        timeline.play();
    }

    /**
     * Set a supplier of an Image that can be used as placeholder by the Tile
     * while the final image is being retrieved
     *
     * @param supplier a supplier that provides a placeholder Image
     */
    public static void setPlaceholderImageSupplier(Supplier<Image> supplier) {
        TileImageView.setPlaceholderImageSupplier(supplier);
    }

    private boolean dirty = false;

    protected void markDirty() {
        dirty = true;
        this.setNeedsLayout(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void layoutChildren() {
        final double w = getWidth();
        final double h = getHeight();

        if (dirty) {
            for (MapLayer layer : layers) {
                layer.layoutLayer();
            }
        }
        super.layoutChildren();
        dirty = false;

        // we need to get these values or we won't be notified on new changes
        baseMap.centerLon().get();
        baseMap.centerLat().get();

        // update clip
        clip.setWidth(w);
        clip.setHeight(h);
    }

    /**
     * Receives a cityMapUpdate event and centers on the cityMap center, zooms on it.
     * @param evt
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        System.out.println("CityMapViewCenter event " + evt);
        CityMap newCityMap = (CityMap) evt.getNewValue();
        Coordinates center = newCityMap.getCenter();
        MapPoint mapCenter = new MapPoint(center.getLatitude(), center.getLongitude());
        setCenter(mapCenter);
        System.out.println("zoom : " + newCityMap.getOptimalZoom());
        double optimalZoom = newCityMap.getOptimalZoom() + 1;
        setZoom(optimalZoom);
        setMaxZoomOut(optimalZoom);
        registerInputListeners();
    }
}
