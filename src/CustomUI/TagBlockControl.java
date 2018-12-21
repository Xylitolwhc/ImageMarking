package CustomUI;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Orientation;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

public class TagBlockControl extends Control {
    private final static double TAG_WIDTH_PADDING_DEFAULT = 10.0,
            TAG_HEIGHT_PADDING_DEFAULT = 10.0,
            TAG_RADIUS_DEFAULT = 2.5;

    private DoubleProperty tagX, tagY, tagWidth, tagHeight, tagWidthPadding, tagHeightPadding, tagRadius, zoomScale;
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
        pointColor = Color.BLACK;
        lineColor = Color.BLACK;
        textField.setPrefColumnCount(10);
        state = TagState.CREATING;
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
     *getter & setter
     */
    public Double getX() {
        return tagX.get();
    }

    public Double getY() {
        return tagY.get();
    }

    public Double getTagX() {
        return tagX.get();
    }

    public Double getTagY() {
        return tagY.get();
    }

    public Double getTagWidth() {
        return tagWidth.get();
    }

    public Double getTagHeight() {
        return tagHeight.get();
    }

    public void setTagWidthPadding(double tagWidthPadding) {
        this.tagWidthPadding.setValue(tagWidthPadding);
        updateBlock();
    }

    public void setTagHeightPadding(double tagHeightPadding) {
        this.tagHeightPadding.setValue(tagHeightPadding);
        updateBlock();
    }

    public Double getTagWidthPadding() {
        return tagWidthPadding.getValue();
    }

    public Double getTagHeightPadding() {
        return tagHeightPadding.getValue();
    }

    public void setTagRadius(double tagRadius) {
        this.tagRadius.setValue(tagRadius);
        updateBlock();
    }

    public double getTagRadius() {
        return tagRadius.getValue();
    }

    public Boolean isCreationDone() {
        return state != TagState.CREATING;
    }

    public void creationDone() {
        this.state = TagState.CREATION_DONE;
    }

    public void setState(TagState state) {
        this.state = state;
    }

    public TagState getState() {
        return state;
    }

    public TextField getTextField() {
        return textField;
    }

    public void setText(String text) {
        this.textField.setText(text);
    }

    public String getText() {
        return this.textField.getText();
    }

    public double getZoomScale() {
        return zoomScale.get();
    }

    public DoubleProperty zoomScaleProperty() {
        return zoomScale;
    }

    public Color getPointColor() {
        return pointColor;
    }

    public void setPointColor(Color pointColor) {
        this.pointColor = pointColor;
        updateBlock();
    }

    public Color getLineColor() {
        return lineColor;
    }

    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
        updateBlock();
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new TagBlockSkin(this);
    }

    /*
     *计算当前控件大小
     */
    @Override
    protected double computeMinWidth(double height) {
        return super.computeMinWidth(height);
    }

    @Override
    protected double computeMinHeight(double width) {
        return super.computeMinHeight(width);
    }

    @Override
    protected double computeMaxWidth(double height) {
        return super.computeMaxWidth(height);
    }

    @Override
    protected double computeMaxHeight(double width) {
        return super.computeMaxHeight(width);
    }

    @Override
    protected double computePrefWidth(double height) {
        return super.computePrefWidth(height);
    }

    @Override
    protected double computePrefHeight(double width) {
        return super.computePrefHeight(width);
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
