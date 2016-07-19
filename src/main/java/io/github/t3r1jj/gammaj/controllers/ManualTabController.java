/* 
 * Copyright 2016 Damian Terlecki.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.t3r1jj.gammaj.controllers;

import io.github.t3r1jj.gammaj.model.Gamma;
import io.github.t3r1jj.gammaj.model.Gamma.Channel;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;

public class ManualTabController extends AbstractTabController {

    private int lastXIndex;
    private int lastValue;
    boolean canvasInteraction;
    IntegerProperty[][] gammaRampProperties;

    @FXML
    private TableView tableView;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        canvas.setCursor(Cursor.CROSSHAIR);
        drawGammaRamp();
    }

    @Override
    protected void loadLocalProfile() {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void resetColorAdjustment() {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void initializeTabListeners() {
        if (!viewModel.getAssistedAdjustmentProperty().get()) {
            addTabListeners();
        }
        initializeTable();
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                double eventX = event.getX();
                if (eventX < 0) {
                    eventX = 0;
                } else if (eventX > canvas.getWidth()) {
                    eventX = canvas.getWidth();
                }
                double eventY = event.getY();
                if (eventY < 0) {
                    eventY = 0;
                } else if (eventY > canvas.getHeight()) {
                    eventY = canvas.getHeight();
                }
                lastXIndex = (int) ((eventX / canvas.getWidth()) * (Gamma.CHANNEL_VALUES_COUNT - 1));
                lastValue = (int) (((canvas.getHeight() - eventY) / canvas.getHeight()) * Gamma.MAX_WORD);
                handleCanvasEvent(event);
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                handleCanvasEvent(event);
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                handleCanvasEvent(event);
            }
        });
    }

    private void handleCanvasEvent(MouseEvent event) {
        resetProfile();
        double eventX = event.getX();
        if (eventX < 0) {
            eventX = 0;
        } else if (eventX > canvas.getWidth()) {
            eventX = canvas.getWidth();
        }
        double eventY = event.getY();
        if (eventY < 0) {
            eventY = 0;
        } else if (eventY > canvas.getHeight()) {
            eventY = canvas.getHeight();
        }
        int x = (int) ((eventX / canvas.getWidth()) * (Gamma.CHANNEL_VALUES_COUNT - 1));
        int value = (int) (((canvas.getHeight() - eventY) / canvas.getHeight()) * Gamma.MAX_WORD);

        int dx = Math.abs(x - lastXIndex);
        int dy = Math.abs(value - lastValue);

        int sx = (lastXIndex < x) ? 1 : -1;
        int sy = (lastValue < value) ? 1 : -1;

        int error = dx - dy;

        while (true) {

            for (Channel channel : viewModel.getSelectedChannelsProperty()) {
                viewModel.getCurrentDisplayProperty().get().setGammaRampValue(channel, lastXIndex, lastValue);
                gammaRampProperties[channel.getIndex()][lastXIndex].set(lastValue);
            }

            if (lastXIndex == x && lastValue == value) {
                break;
            }

            int doubleError = error + error;

            if (doubleError > -dy) {
                error = error - dy;
                lastXIndex = lastXIndex + sx;
            }

            if (doubleError < dx) {
                error = error + dx;
                lastValue = lastValue + sy;
            }
        }

        viewModel.getCurrentDisplayProperty().get().setDeviceGammaRamp();
        drawGammaRamp();
    }

    @Override
    protected void handleResetButtonAction(ActionEvent event) {
        super.handleResetButtonAction(event);
        loadRampViewModel();
    }

    private void initializeTable() {
        int[][] gammaRamp = viewModel.getCurrentDisplayProperty().get().getGammaRamp();
        gammaRampProperties = new SimpleIntegerProperty[gammaRamp.length][gammaRamp[0].length];
        for (int y = 0; y < gammaRamp.length; y++) {
            for (int x = 0; x < gammaRamp[y].length; x++) {
                gammaRampProperties[y][x] = new SimpleIntegerProperty(gammaRamp[y][x]);
            }
        }

        TableColumn<Integer, String> firstTableColumn = new TableColumn<>("Channel\\Index");
        firstTableColumn.getStyleClass().add("my-header-column");
        firstTableColumn.sortableProperty().set(false);
        firstTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Integer, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Integer, String> param) {
                switch (param.getValue()) {
                    case 0:
                        return new ReadOnlyStringWrapper("Red");
                    case 1:
                        return new ReadOnlyStringWrapper("Green");
                    case 2:
                        return new ReadOnlyStringWrapper("Blue");
                    default:
                        return new ReadOnlyStringWrapper("Invalid");
                }
            }
        });
        tableView.getColumns().add(firstTableColumn);

        for (int i = 0; i < Gamma.CHANNEL_VALUES_COUNT; i++) {
            TableColumn<Integer, Integer> column = new TableColumn<>(String.valueOf(i));
            column.sortableProperty().set(false);
            final int columnIndex = i;
            column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Integer, Integer>, ObservableValue<Integer>>() {

                @Override
                public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Integer, Integer> param) {
                    return gammaRampProperties[param.getValue()][columnIndex].asObject();

                }
            });
            column.setCellFactory(TextFieldTableCell.<Integer, Integer>forTableColumn(new IntegerStringConverter()));
            column.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Integer, Integer>>() {

                @Override
                public void handle(TableColumn.CellEditEvent<Integer, Integer> event) {
                    resetProfile();
                    if (event.getNewValue() < 0) {
                        gammaRampProperties[event.getRowValue()][columnIndex].set(0);
                    } else if (event.getNewValue() > Gamma.MAX_WORD) {
                        gammaRampProperties[event.getRowValue()][columnIndex].set(Gamma.MAX_WORD);
                    } else {
                        gammaRampProperties[event.getRowValue()][columnIndex].set(event.getNewValue());
                    }
                    viewModel.getCurrentDisplayProperty().get().setGammaRampValue(Channel.getChannel(event.getRowValue()), columnIndex, event.getNewValue());
                    viewModel.getCurrentDisplayProperty().get().setDeviceGammaRamp();
                    drawGammaRamp();
                }
            });
            column.setEditable(true);
            column.setPrefWidth(60);
            tableView.getColumns().add(column);
        }
        tableView.getItems().addAll(Arrays.asList(new Integer[]{0, 1, 2}));
        tableView.setEditable(true);
        tableView.setFixedCellSize(25);
        tableView.prefHeightProperty().bind(Bindings.size(tableView.getItems()).multiply(tableView.getFixedCellSize()).add(40));
    }

    private void loadRampViewModel() {
        int[][] gammaRamp = viewModel.getCurrentDisplayProperty().get().getColorProfile().getGammaRamp();
        for (int y = 0; y < gammaRamp.length; y++) {
            for (int x = 0; x < gammaRamp[y].length; x++) {
                gammaRampProperties[y][x].set(gammaRamp[y][x]);
            }
        }
    }

    @Override
    protected void handleInvertButtonAction(ActionEvent event) {
        if (!loadingProfile) {
            System.out.println("WUT");
            resetProfile();
            for (Gamma.Channel channel : viewModel.getSelectedChannelsProperty()) {
                for (int x = 0; x < Gamma.CHANNEL_VALUES_COUNT; x++) {
                    gammaRampProperties[channel.getIndex()][x].set(Gamma.MAX_WORD - gammaRampProperties[channel.getIndex()][x].get());
                    viewModel.getCurrentDisplayProperty().get().setGammaRampValue(channel, x, gammaRampProperties[channel.getIndex()][x].get());
                }
            }
            viewModel.getCurrentDisplayProperty().get().setDeviceGammaRamp();
            drawGammaRamp();
        }
    }
}
