package CustomUI;

import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;


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
        minHeightPadding = 0.0;
        minWidthPadding = 0.0;
        maxWidthPadding = 0.0;
        maxHeightPadding = 0.0;
    }

    private void draw() {
        if (canvas != null) {
            getChildren().remove(canvas);
        }
        double widthPadding = getSkinnable().getTagWidthPadding(),
                heightPadding = getSkinnable().getTagHeightPadding(),
                width = getSkinnable().getTagWidth(),
                height = getSkinnable().getTagHeight();
        double radius = 3.0;
        canvas = new Canvas(width + widthPadding * 2, height + heightPadding * 2);
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        //上下左右四条边框
        graphicsContext.setLineWidth(1);
        graphicsContext.setFill(Color.GREEN);
        graphicsContext.setStroke(Color.GREEN);
        graphicsContext.strokeLine(widthPadding, heightPadding, widthPadding + width, heightPadding);
        graphicsContext.strokeLine(widthPadding, heightPadding + height, widthPadding + width, heightPadding + height);
        graphicsContext.strokeLine(widthPadding, heightPadding, widthPadding, heightPadding + height);
        graphicsContext.strokeLine(widthPadding + width, heightPadding, widthPadding + width, heightPadding + height);
        //四角上的点
        graphicsContext.setFill(Color.BLUE);
        graphicsContext.setStroke(Color.BLUE);
        graphicsContext.fillOval(widthPadding - radius, heightPadding - radius, radius * 2, radius * 2);
        graphicsContext.fillOval(widthPadding - radius + width, heightPadding - radius, radius * 2, radius * 2);
        graphicsContext.fillOval(widthPadding - radius, heightPadding + height - radius, radius * 2, radius * 2);
        graphicsContext.fillOval(widthPadding + width - radius, heightPadding + height - radius, radius * 2, radius * 2);
        invalidBlock = false;
        canvas.setOnMouseClicked((e) -> {
            getSkinnable().fireEvent(e);
        });
        canvas.addEventHandler(MouseEvent.MOUSE_ENTERED,(e)->{
            getSkinnable().getScene().setCursor(Cursor.HAND);
        });
        canvas.addEventHandler(MouseEvent.MOUSE_EXITED,(e)->{
            getSkinnable().getScene().setCursor(Cursor.DEFAULT);
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
