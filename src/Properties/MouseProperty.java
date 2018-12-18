package Properties;

import javafx.scene.input.MouseEvent;

public class MouseProperty {
    private double screenX, screenY, x, y;

    public MouseProperty() {
        screenX = 0;
        screenY = 0;
        x = 0;
        y = 0;
    }

    public MouseProperty(MouseEvent event) {
        set(event);
    }

    public void set(MouseEvent event) {
        this.screenX = event.getScreenX();
        this.screenY = event.getScreenY();
        this.x = event.getX();
        this.y = event.getY();
    }

    public double getScreenX() {
        return screenX;
    }

    public void setScreenX(double screenX) {
        this.screenX = screenX;
    }

    public double getScreenY() {
        return screenY;
    }

    public void setScreenY(double screenY) {
        this.screenY = screenY;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
