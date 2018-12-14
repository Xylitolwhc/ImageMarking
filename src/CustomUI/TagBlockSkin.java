package CustomUI;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.SkinBase;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

public class TagBlockSkin extends SkinBase<TagBlockControl> {
    private Double minWidthPadding, minHeightPadding, maxWidthPadding, maxHeightPadding;
    private Canvas canvas;
    private Boolean invalidBlock = true;

    /**
     * Constructor for all SkinBase instances.
     *
     * @param control The control for which this Skin should attach to.
     */
    protected TagBlockSkin(TagBlockControl control) {
        super(control);
        minHeightPadding = 20.0;
        minWidthPadding = 20.0;
        maxWidthPadding = 100.0;
        maxHeightPadding = 100.0;
    }

    private void draw() {
        if (canvas != null) {
            getChildren().remove(canvas);
        }
        double x = getSkinnable().getTagX(),
                y = getSkinnable().getTagY(),
                width = getSkinnable().getTagWidth(),
                height = getSkinnable().getTagHeight();
        double radius = 3.0;
        canvas = new Canvas(width, height);
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        //上下左右四条边框
        graphicsContext.setLineWidth(1);
        graphicsContext.setFill(Color.GREEN);
        graphicsContext.setStroke(Color.GREEN);
        graphicsContext.strokeLine(x, y, x + width, y);
        graphicsContext.strokeLine(x, y + height, x + width, y + height);
        graphicsContext.strokeLine(x, y, x, y + height);
        graphicsContext.strokeLine(x + width, y, x + width, y + height);
        //四角上的点
        graphicsContext.setFill(Color.BLUE);
        graphicsContext.setStroke(Color.BLUE);
        graphicsContext.fillOval(x - radius, y - radius, radius * 2, radius * 2);
        graphicsContext.fillOval(x - radius + width, y - radius, radius * 2, radius * 2);
        graphicsContext.fillOval(x - radius, y + height - radius, radius * 2, radius * 2);
        graphicsContext.fillOval(x + width - radius, y + height - radius, radius * 2, radius * 2);
        invalidBlock = false;
        canvas.setOnMouseClicked((e) -> {
            getSkinnable().fireEvent(new ActionEvent());
        });
        getChildren().add(canvas);
    }

    public void updateBlock() {
        invalidBlock = true;
        draw();
    }

    @Override
    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
        if (invalidBlock) {
            draw();
        }

    }

    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return leftInset + rightInset + minWidthPadding;
    }

    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return topInset + bottomInset + minHeightPadding;
    }

    @Override
    protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return leftInset + rightInset + maxWidthPadding;
    }

    @Override
    protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return topInset + bottomInset + maxHeightPadding;
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return computeMaxWidth(height, topInset, rightInset, bottomInset, leftInset);
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return computeMaxHeight(width, topInset, rightInset, bottomInset, leftInset);
    }
}
