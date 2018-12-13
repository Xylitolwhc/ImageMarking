package CustomUI;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.SkinBase;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

public class TagBlockSkin extends SkinBase<TagBlockControl> {
    private Double minWidthPadding, minHeightPadding, maxWidthPadding, maxHeightPadding;
    private Canvas canvas;
    private DoubleProperty width = new SimpleDoubleProperty(),
            height = new SimpleDoubleProperty(),
            heightPadding = new SimpleDoubleProperty(),
            widthPadding = new SimpleDoubleProperty();
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
        width.setValue(200);
        height.setValue(200);
        widthPadding.setValue(10);
        heightPadding.setValue(10);
    }

    private void draw() {
        if (canvas != null) {
            getChildren().remove(canvas);
        }
        double radius = 3.0;
        canvas = new Canvas(300, 300);
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        //上下左右四条边框
        graphicsContext.setLineWidth(1);
        graphicsContext.setFill(Color.GREEN);
        graphicsContext.setStroke(Color.GREEN);
        graphicsContext.strokeLine(widthPadding.getValue(), heightPadding.getValue(), widthPadding.getValue() + width.getValue(), heightPadding.getValue());
        graphicsContext.strokeLine(widthPadding.getValue(), heightPadding.getValue() + height.getValue(), widthPadding.getValue() + width.getValue(), heightPadding.getValue() + height.getValue());
        graphicsContext.strokeLine(widthPadding.getValue(), heightPadding.getValue(), widthPadding.getValue(), heightPadding.getValue() + height.getValue());
        graphicsContext.strokeLine(widthPadding.getValue() + width.getValue(), heightPadding.getValue(), widthPadding.getValue() + width.getValue(), heightPadding.getValue() + height.getValue());
        //四角上的点
        graphicsContext.setFill(Color.BLUE);
        graphicsContext.setStroke(Color.BLUE);
        graphicsContext.fillOval(widthPadding.getValue() - radius, heightPadding.getValue() - radius, radius * 2, radius * 2);
        graphicsContext.fillOval(widthPadding.getValue() - radius + width.getValue(), heightPadding.getValue() - radius, radius * 2, radius * 2);
        graphicsContext.fillOval(widthPadding.getValue() - radius, heightPadding.getValue() + height.getValue() - radius, radius * 2, radius * 2);
        graphicsContext.fillOval(widthPadding.getValue() + width.getValue() - radius, heightPadding.getValue() + height.getValue() - radius, radius * 2, radius * 2);
        invalidBlock = false;
        getChildren().add(canvas);
    }

    public void updateBlock(double x, double y, double width, double height) {
        widthPadding.setValue(x);
        heightPadding.setValue(y);
        this.width.setValue(width);
        this.height.setValue(height);
        invalidBlock = true;
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
