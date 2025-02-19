package paint;

import java.awt.*;

public class ShapeState {
    private Shape shape; // Hình vẽ lưu trữ

    public ShapeState(Shape shape) {
        this.shape = shape;
    }

    public Shape getShape() {
        return shape;
    }
}
