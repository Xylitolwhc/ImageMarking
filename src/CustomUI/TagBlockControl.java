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


public class TagBlockControl extends Control {
    private DoubleProperty x, y, width, height, tagX, tagY, tagWidth, tagHeight;

    private void init() {
        this.x = new SimpleDoubleProperty();
        this.y = new SimpleDoubleProperty();
        this.width = new SimpleDoubleProperty();
        this.height = new SimpleDoubleProperty();
        this.tagX = new SimpleDoubleProperty();
        this.tagY = new SimpleDoubleProperty();
        this.tagWidth = new SimpleDoubleProperty();
        this.tagHeight = new SimpleDoubleProperty();
    }

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

    public void updateBlock(double x, double y, double width, double height) {
        this.x.setValue(x);
        this.y.setValue(y);
        this.width.setValue(width);
        this.height.setValue(height);
        ((TagBlockSkin) getSkin()).updateBlock();
    }

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
