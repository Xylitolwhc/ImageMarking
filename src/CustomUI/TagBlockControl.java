package CustomUI;

import javafx.geometry.Orientation;
import javafx.scene.control.Control;

public class TagBlockControl extends Control {
    public TagBlockControl() {

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
}
