package CustomUI;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Orientation;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

public class TagBlockControl extends Control {
    private final static double TAG_WIDTH_PADDING_DEFAULT = 3.0,
            TAG_HEIGHT_PADDING_DEFAULT = 3.0,
            TAG_RADIUS_DEFAULT = 2.5;

    private DoubleProperty tagX, tagY, tagWidth, tagHeight, tagWidthPadding, tagHeightPadding, tagRadius, zoomScale, lineWidth;
    private Color pointColor, lineColor;
    private TagState state;
    private TextField textField;

    /*
     *初始化各属性值
     */
    private void init() {
        tagX = new SimpleDoubleProperty(0);
        tagY = new SimpleDoubleProperty(0);
        tagWidth = new SimpleDoubleProperty(0);
        tagHeight = new SimpleDoubleProperty(0);
        tagWidthPadding = new SimpleDoubleProperty(TAG_WIDTH_PADDING_DEFAULT);
        tagHeightPadding = new SimpleDoubleProperty(TAG_HEIGHT_PADDING_DEFAULT);
        tagRadius = new SimpleDoubleProperty(TAG_RADIUS_DEFAULT);
        textField = new TextField();
        zoomScale = new SimpleDoubleProperty(1.0);
        lineWidth = new SimpleDoubleProperty(1.0);
        pointColor = Color.BLACK;
        lineColor = Color.BLACK;
        textField.setPrefColumnCount(10);
        state = TagState.CREATING;

        zoomScale.addListener((e) -> updateBlock());
        lineWidth.addListener((e) -> updateBlock());
    }

    /*
     *基本构造函数
     */
    public TagBlockControl() {
        this(0, 0, 0, 0);
    }

    public TagBlockControl(double x, double y) {
        this(x, y, 0, 0);
    }

    public TagBlockControl(double x, double y, double width, double height) {
        this(x, y, width, height, 1.0);
    }

    public TagBlockControl(double x, double y, double width, double height, double zoomScale) {
        init();
        this.tagX.set(x / zoomScale);
        this.tagY.set(y / zoomScale);
        this.tagWidth.set(width / zoomScale);
        this.tagHeight.set(height / zoomScale);
        setSkin(createDefaultSkin());
    }

    /*
     *用于更新重绘控件外观
     */
    public void updateBlock() {
        ((TagBlockSkin) getSkin()).updateBlock();
    }

    public void updateBlock(double width, double height) {
        this.tagWidth.set(width / zoomScale.get());
        this.tagHeight.set(height / zoomScale.get());
        updateBlock();
    }

    public void updateBlockXY(double x, double y) {
        this.tagX.set(x / zoomScale.get());
        this.tagY.set(y / zoomScale.get());
        updateBlock();
    }

    public void updateBlock(double x, double y, double width, double height) {
        this.tagX.setValue(x / zoomScale.get());
        this.tagY.setValue(y / zoomScale.get());
        this.tagWidth.setValue(width / zoomScale.get());
        this.tagHeight.setValue(height / zoomScale.get());
        updateBlock();
    }

    /*
     * Getter
     */
    public double getTagX() {
        return tagX.get();
    }

    public double getTagY() {
        return tagY.get();
    }

    public double getTagWidth() {
        return tagWidth.get();
    }

    public double getTagHeight() {
        return tagHeight.get();
    }

    public double getTagWidthPadding() {
        return tagWidthPadding.get();
    }

    public double getTagHeightPadding() {
        return tagHeightPadding.get();
    }

    public double getTagRadius() {
        return tagRadius.get();
    }

    public TagState getState() {
        return state;
    }

    public TextField getTextField() {
        return textField;
    }

    public String getText() {
        return this.textField.getText();
    }

    public double getZoomScale() {
        return zoomScale.get();
    }

    public Color getPointColor() {
        return pointColor;
    }

    public Color getLineColor() {
        return lineColor;
    }

    public double getLineWidth() {
        return lineWidth.get();
    }

    /*
     * Property
     */
    public DoubleProperty tagXProperty() {
        return tagX;
    }

    public DoubleProperty tagYProperty() {
        return tagY;
    }

    public DoubleProperty tagWidthProperty() {
        return tagWidth;
    }

    public DoubleProperty tagHeightProperty() {
        return tagHeight;
    }

    public DoubleProperty tagWidthPaddingProperty() {
        return tagWidthPadding;
    }

    public DoubleProperty tagHeightPaddingProperty() {
        return tagHeightPadding;
    }

    public DoubleProperty tagRadiusProperty() {
        return tagRadius;
    }

    public DoubleProperty zoomScaleProperty() {
        return zoomScale;
    }

    public DoubleProperty lineWidthProperty() {
        return lineWidth;
    }

    /*
     * Setter
     */
    public void setTagX(double tagX) {
        this.tagX.set(tagX);
    }

    public void setTagY(double tagY) {
        this.tagY.set(tagY);
    }

    public void setTagWidth(double tagWidth) {
        this.tagWidth.set(tagWidth);
    }

    public void setTagHeight(double tagHeight) {
        this.tagHeight.set(tagHeight);
    }

    public void setTagWidthPadding(double tagWidthPadding) {
        this.tagWidthPadding.set(tagWidthPadding);
    }

    public void setTagHeightPadding(double tagHeightPadding) {
        this.tagHeightPadding.set(tagHeightPadding);
    }

    public void setTagRadius(double tagRadius) {
        this.tagRadius.set(tagRadius);
    }

    public void setState(TagState state) {
        this.state = state;
    }

    public void setText(String text) {
        this.textField.setText(text);
    }

    public void setPointColor(Color pointColor) {
        this.pointColor = pointColor;
        updateBlock();
    }

    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
        updateBlock();
    }

    public void setLineWidth(double lineWidth) {
        this.lineWidth.set(lineWidth);
    }

    public Boolean isCreationDone() {
        return state != TagState.CREATING;
    }

    public void creationDone() {
        this.state = TagState.CREATION_DONE;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new TagBlockSkin(this);
    }

    /*
     *This component do not need a width/height dependency.
     */
    @Override
    public Orientation getContentBias() {
        return null;
    }

    @Override
    public String toString() {
        return "x:" + this.tagX.getValue() + " y:" + tagY.getValue() + " width:" + tagWidth.getValue() + " height:" + tagHeight.getValue();
    }
}