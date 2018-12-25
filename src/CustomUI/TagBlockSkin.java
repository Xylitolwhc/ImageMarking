package CustomUI;

import javafx.animation.FadeTransition;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;


public class TagBlockSkin extends SkinBase<TagBlockControl> {
    private Double minWidthPadding, minHeightPadding, maxWidthPadding, maxHeightPadding;
    private Canvas canvas;
    private Boolean invalidBlock = true;
    private FadeTransition fadeTransition;

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

        fadeTransition = new FadeTransition();
        fadeTransition.setNode(getSkinnable().getTextField());
        fadeTransition.setDuration(new Duration(500));
        fadeTransition.setFromValue(1.00);
        fadeTransition.setToValue(0);
        getSkinnable().getTextField().focusedProperty().addListener((event) -> {
            if (!getSkinnable().getTextField().isFocused()) {
                fadeTransition.playFromStart();
            } else {
                fadeTransition.stop();
                fadeTransition.jumpTo(Duration.ZERO);
                getSkinnable().getTextField().setOpacity(1.00);
            }
        });
    }

    /*
     *绘制标记框
     */
    private void drawTag() {
        if (canvas != null) {
            getChildren().remove(canvas);
        }
        double zoomScale = getSkinnable().getZoomScale(),
                widthPadding = getSkinnable().getTagWidthPadding() * zoomScale,
                heightPadding = getSkinnable().getTagHeightPadding() * zoomScale,
                width = getSkinnable().getTagWidth() * zoomScale,
                height = getSkinnable().getTagHeight() * zoomScale,
                radius = getSkinnable().getTagRadius(),
                lineWidth = getSkinnable().getLineWidth();
        Color lineColor = getSkinnable().getLineColor(),
                pointColor = getSkinnable().getPointColor();

        if (widthPadding <= 0) widthPadding = 0;
        if (heightPadding <= 0) heightPadding = 0;
        if (width <= 0) width = 0;
        if (height <= 0) height = 0;
        if (radius <= 0) radius = 0;

        canvas = new Canvas(width + widthPadding * 2, height + heightPadding * 2);
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        //上下左右四条边框
        graphicsContext.setLineWidth(lineWidth);
        graphicsContext.setFill(lineColor);
        graphicsContext.setStroke(lineColor);
        graphicsContext.strokeLine(widthPadding, heightPadding, widthPadding + width, heightPadding);
        graphicsContext.strokeLine(widthPadding, heightPadding + height, widthPadding + width, heightPadding + height);
        graphicsContext.strokeLine(widthPadding, heightPadding, widthPadding, heightPadding + height);
        graphicsContext.strokeLine(widthPadding + width, heightPadding, widthPadding + width, heightPadding + height);
        //四角上的点
        graphicsContext.setFill(pointColor);
        graphicsContext.setStroke(pointColor);
        graphicsContext.fillOval(widthPadding - radius, heightPadding - radius, radius * 2, radius * 2);
        graphicsContext.fillOval(widthPadding - radius + width, heightPadding - radius, radius * 2, radius * 2);
        graphicsContext.fillOval(widthPadding - radius, heightPadding + height - radius, radius * 2, radius * 2);
        graphicsContext.fillOval(widthPadding + width - radius, heightPadding + height - radius, radius * 2, radius * 2);
        invalidBlock = false;
        bindActionEcent(canvas);
        getChildren().add(canvas);
    }

    /*
     *测试功能
     */
    private void bindActionEcent(Canvas canvas) {
        canvas.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
            getSkinnable().getScene().setCursor(Cursor.HAND);
            fadeTransition.stop();
            getSkinnable().getTextField().setOpacity(1.00);
        });
        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, (e) -> {
            double zoomScale = getSkinnable().getZoomScale(),
                    widthPadding = getSkinnable().getTagWidthPadding() * zoomScale,
                    heightPadding = getSkinnable().getTagHeightPadding() * zoomScale,
                    width = getSkinnable().getTagWidth() * zoomScale,
                    height = getSkinnable().getTagHeight() * zoomScale,
                    radius = getSkinnable().getTagRadius();
            double x = e.getX(), y = e.getY();
            if (x > widthPadding + width - radius * 4
                    && x < widthPadding + width + radius * 4
                    && y > heightPadding + height - radius * 4
                    && y < heightPadding + height + radius * 4) {
                getSkinnable().getScene().setCursor(Cursor.SE_RESIZE);
                getSkinnable().setState(TagState.ATTEMPT_TO_RESIZE_RIGHT_BUTTOM);
            } else if (x < widthPadding + radius * 4
                    && x > 0
                    && y < heightPadding + radius * 4
                    && y > 0) {
                getSkinnable().getScene().setCursor(Cursor.SE_RESIZE);
                getSkinnable().setState(TagState.ATTEMPT_TO_RESIZE_LEFT_UP);
            } else {
                getSkinnable().getScene().setCursor(Cursor.MOVE);
                getSkinnable().setState(TagState.ATTEMPT_TO_MOVE);
            }
            getSkinnable().fireEvent(e);
        });
        canvas.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
            getSkinnable().getScene().setCursor(Cursor.DEFAULT);
            if (!getSkinnable().getTextField().isFocused()) {
                fadeTransition.playFromStart();
            }
        });
    }

    public void updateBlock() {
        invalidBlock = true;
        drawTag();
    }

    @Override
    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
        if (invalidBlock) {
            drawTag();
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
