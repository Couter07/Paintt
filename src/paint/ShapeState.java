package paint;

import java.awt.*;
import paint.ShapeState;

public class ShapeState  {
    public int group;
    private Shape shape; // Hình vẽ lưu trữ
    private Color color; // Màu của nét vẽ
    private int strokeWidth; // Độ dày của nét vẽ
//    public int group = 0;
    public ShapeState(Shape shape, Color color, int strokeWidth, int group) {
        this.shape = shape;
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.group = group;


    }

    public ShapeState(Shape shape, Color currentColor, int strokeWidth) {
    }

    public Shape getShape() {
        return shape;
    }

    public Color getColor() {return color;
    }

    public int getStrokeWidth() {return strokeWidth;
    }
    public int getGroup() {return group;
    }
}
