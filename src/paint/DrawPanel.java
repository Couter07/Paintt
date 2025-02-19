package paint;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DrawPanel extends JPanel {
    private ArrayList<Shape> shapes = new ArrayList<>(); // Lưu trữ các hình đã vẽ
    private ArrayList<Shape> undoneShapes = new ArrayList<>(); // Lưu trữ các hình bị Undo

    public DrawPanel() {
        setBackground(Color.WHITE);
    }

    // Thêm hình vào danh sách và vẽ lại
    public void addShape(Shape shape) {
        shapes.add(shape);
    }

    // Xóa hình vẽ cuối cùng
    public void removeLastShape() {
        if (!shapes.isEmpty()) {
            shapes.remove(shapes.size() - 1);
        }
    }
    // Xóa tất cả các hình
    public void clear() {
        shapes.clear();
        undoneShapes.clear();
        repaint();
    }
}