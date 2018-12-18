package CustomUI;

import javafx.scene.Cursor;
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

    /*
     *绘制标记框
     */
    private void drawTag() {
        if (canvas != null) {
            getChildren().remove(canvas);
        }
        double widthPadding = getSkinnable().getTagWidthPadding(),
                heightPadding = getSkinnable().getTagHeightPadding(),
                width = getSkinnable().getTagWidth(),
                height = getSkinnable().getTagHeight(),
                radius = getSkinnable().getTagRadius();
        if (widthPadding <= 0) widthPadding = 0;
        if (heightPadding <= 0) heightPadding = 0;
        if (width <= 0) width = 0;
        if (height <= 0) height = 0;
        if (radius <= 0) radius = 0;

        canvas = new Canvas(width + widthPadding * 2, height + heightPadding * 2);
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        //上下左右四条边框
        graphicsContext.setLineWidth(1);
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.strokeLine(widthPadding, heightPadding, widthPadding + width, heightPadding);
        graphicsContext.strokeLine(widthPadding, heightPadding + height, widthPadding + width, heightPadding + height);
        graphicsContext.strokeLine(widthPadding, heightPadding, widthPadding, heightPadding + height);
        graphicsContext.strokeLine(widthPadding + width, heightPadding, widthPadding + width, heightPadding + height);
        //四角上的点
        graphicsContext.setFill(Color.WHITE);
        graphicsContext.setStroke(Color.WHITE);
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
        canvas.setOnMouseClicked((e) -> {
            getSkinnable().fireEvent(e);
        });
        canvas.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
            getSkinnable().getScene().setCursor(Cursor.HAND);
        });
        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, (e) -> {
            double widthPadding = getSkinnable().getTagWidthPadding(),
                    heightPadding = getSkinnable().getTagHeightPadding(),
                    width = getSkinnable().getTagWidth(),
                    height = getSkinnable().getTagHeight(),
                    radius = getSkinnable().getTagRadius();
            double x = e.getX(), y = e.getY();
            if (x > widthPadding + width - radius * 4
                    && x < widthPadding + width + radius * 4
                    && y > heightPadding + height - radius * 4
                    && y < heightPadding + height + radius * 4) {
                getSkinnable().getScene().setCursor(Cursor.SE_RESIZE);
                getSkinnable().setState(TagState.ATTEMPT_TO_RESIZE);
            } else if (true) {
                getSkinnable().getScene().setCursor(Cursor.MOVE);
                getSkinnable().setState(TagState.ATTEMPT_TO_MOVE);
            }
            getSkinnable().fireEvent(e);
        });
        canvas.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
            getSkinnable().getScene().setCursor(Cursor.DEFAULT);
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
