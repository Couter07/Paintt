package paint;

import java.awt.*;

public class ShapeState  {
    private Shape shape; // Hình vẽ lưu trữ
    private Color color; // Màu của nét vẽ
    private int strokeWidth; // Độ dày của nét vẽ
    private int group; // Nhóm của hình vẽ

    public ShapeState(Shape shape, Color color, int strokeWidth, int group) {
        this.shape = shape;
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.group = group;
    }

    public Shape getShape() {
        return shape;
    }

    public Color getColor() {
        return color;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public int getGroup() {
        return group;
    }
}