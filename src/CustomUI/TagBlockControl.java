package CustomUI;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;


public class TagBlockControl extends Control {
    public final static double TAG_WIDTH_PADDING_DEFAULT = 10.0,
            TAG_HEIGHT_PADDING_DEFAULT = 10.0,
            TAG_RADIUS_DEFAULT = 2.5;

    private DoubleProperty x, y, width, height, tagX, tagY, tagWidth, tagHeight, tagWidthPadding, tagHeightPadding, tagRadius, textX, textY;
    private TagState state;
    private TextField textField;
    private AnchorPane anchorPane;

    /*
     *初始化各属性值
     */
    private void init() {
        this.x = new SimpleDoubleProperty();
        this.y = new SimpleDoubleProperty();
        this.width = new SimpleDoubleProperty();
        this.height = new SimpleDoubleProperty();
        this.tagX = new SimpleDoubleProperty();
        this.tagY = new SimpleDoubleProperty();
        this.tagWidth = new SimpleDoubleProperty();
        this.tagHeight = new SimpleDoubleProperty();
        this.tagWidthPadding = new SimpleDoubleProperty();
        this.tagHeightPadding = new SimpleDoubleProperty();
        this.tagRadius = new SimpleDoubleProperty();
        this.textX = new SimpleDoubleProperty();
        this.textY = new SimpleDoubleProperty();
        this.textField = new TextField();

        this.x.setValue(0);
        this.y.setValue(0);
        this.width.setValue(0);
        this.height.setValue(0);
        this.tagX.setValue(0);
        this.tagY.setValue(0);
        this.tagWidth.setValue(0);
        this.tagHeight.setValue(0);
        this.tagWidthPadding.setValue(TAG_WIDTH_PADDING_DEFAULT);
        this.tagHeightPadding.setValue(TAG_HEIGHT_PADDING_DEFAULT);
        this.tagRadius.setValue(TAG_RADIUS_DEFAULT);
        this.textX.setValue(0);
        this.textY.setValue(0);
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
        init();
        this.x.setValue(x);
        this.y.setValue(y);
        this.width.setValue(width);
        this.height.setValue(height);
        this.tagX.setValue(x);
        this.tagY.setValue(y);
        this.tagWidth.setValue(width);
        this.tagHeight.setValue(height);
        setSkin(createDefaultSkin());
    }


    /*
     *用于更新重绘控件外观
     */
    public void updateBlock() {
        ((TagBlockSkin) getSkin()).updateBlock();
    }

    public void updateBlock(double width, double height) {
        this.width.setValue(width);
        this.height.setValue(height);
        updateBlock();
    }

    public void updateBlockXY(double x, double y) {
        this.x.setValue(x);
        this.y.setValue(y);
        this.tagX.setValue(x);
        this.tagY.setValue(y);
        updateBlock();
    }

    public void updateBlock(double x, double y, double width, double height) {
        this.x.setValue(x);
        this.y.setValue(y);
        this.width.setValue(width);
        this.height.setValue(height);
        updateBlock();
    }

    /*
     *getter & setter
     */
    public Double getX() {
        return x.getValue();
    }

    public Double getY() {
        return y.getValue();
    }

    public Double getTagX() {
        return x.getValue();
    }

    public Double getTagY() {
        return y.getValue();
    }

    public Double getTagWidth() {
        return width.getValue();
    }

    public Double getTagHeight() {
        return height.getValue();
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

    public void setTextField(TextField textField) {
        this.textField = textField;
    }

    public void setText(String text) {
        this.textField.setText(text);
    }

    /*
     *添加事件
     */

    public final ObjectProperty<EventHandler<ActionEvent>> onActionProperty() {
        return onAction;
    }

    public EventHandler<ActionEvent> getOnAction() {
        return onActionProperty().get();
    }

    public void setOnAction(EventHandler<ActionEvent> onAction) {
        onActionProperty().set(onAction);
    }

    private ObjectProperty<EventHandler<ActionEvent>> onAction = new ObjectPropertyBase<EventHandler<ActionEvent>>() {
        @Override
        protected void invalidated() {
            setEventHandler(ActionEvent.ACTION, get());
        }

        @Override
        public Object getBean() {
            return TagBlockControl.this;
        }

        @Override
        public String getName() {
            return "onAction";
        }
    };

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
        return "x:" + this.x.getValue() + " y:" + y.getValue() + " width:" + width.getValue() + " height:" + height.getValue();
    }
}
