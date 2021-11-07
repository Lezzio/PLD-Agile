/*
 * Copyright (c) 2016, Gluon
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
import fr.insalyon.pldagile.view.Hideable;
import fr.insalyon.pldagile.view.menu.RequestItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Pair;

import java.util.ArrayList;

/**
 * A layer that allows to visualise points of interest.
 */
public class PointLayer<T extends Node> extends MapLayer implements Hideable {

    private final ObservableList<Pair<MapPoint, T>> points = FXCollections.observableArrayList();

    public PointLayer() { }

    public void addPoint(MapPoint mapPoint, T icon) {
        addPoint(mapPoint, icon, null);
    }

    public void addPoint(MapPoint mapPoint, T icon, Effect effect) {
        icon.setEffect(effect);
        points.add(new Pair<>(mapPoint, icon));
        this.getChildren().add(icon);
        this.markDirty();
    }

    public void clearPoints() {
        points.clear();
        this.getChildren().clear();
        this.markDirty();
    }

    public ObservableList<Pair<MapPoint, T>> getPoints() {
        return points;
    }

    @Override
    protected void layoutLayer() {
        for (Pair<MapPoint, T> candidate : points) {
            MapPoint point = candidate.getKey();
            Node icon = candidate.getValue();
            Point2D mapPoint = getMapPoint(point.getLatitude(), point.getLongitude());
            icon.setVisible(true);
            icon.setTranslateX(mapPoint.getX());
            icon.setTranslateY(mapPoint.getY());
        }
    }

    @Override
    public void hide() {
        this.getChildren().clear();
        this.markDirty();
    }

    @Override
    public void show() {
        points.forEach(point -> this.getChildren().add(point.getValue()));
        this.markDirty();
    }

}
