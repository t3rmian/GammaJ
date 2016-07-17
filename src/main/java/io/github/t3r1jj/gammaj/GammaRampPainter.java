package io.github.t3r1jj.gammaj;

import static io.github.t3r1jj.gammaj.controllers.SceneController.GAMMA_CANVAS_BACKGROUND_COLOR;
import static io.github.t3r1jj.gammaj.controllers.SceneController.GAMMA_CANVAS_LINE_COLOR;
import io.github.t3r1jj.gammaj.model.Display;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class GammaRampPainter {

    public void drawGammaRamp(Canvas canvas, Display display) {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.setFill(GAMMA_CANVAS_BACKGROUND_COLOR);
        graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        graphicsContext.setLineWidth(1);
        double[][] gammaRamp = display.getNormalizedGammaRamp();
        for (int i = 0; i < gammaRamp.length; i++) {
            graphicsContext.setStroke(GAMMA_CANVAS_LINE_COLOR[i]);
            graphicsContext.strokeLine(0, (1 - gammaRamp[i][0]) * canvas.getWidth(), 0, (1 - gammaRamp[i][0]) * canvas.getWidth());
            for (int x = 1; x < canvas.getWidth(); x++) {
                graphicsContext.strokeLine(x - 1, (1 - gammaRamp[i][x - 1]) * canvas.getWidth(), x, (1 - gammaRamp[i][x]) * canvas.getWidth());
            }
        }
    }
}
