package paint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Stack;
import java.awt.geom.Path2D;

public class DrawPanel extends JPanel {
    private Stack<ShapeState> shapes = new Stack<>();
    private Stack<ShapeState> undoneShapes = new Stack<>();
    private Color currentColor = Color.BLACK;
    private int strokeWidth = 5;
    private Point startPoint, currentPoint;
    private String shapeType = "Đường thẳng";
    private boolean drawing = false;
    private boolean eraserMode = false;
    private int eraserSize = 10;
    private Path2D.Float currentPath = null;  // Đường dẫn đang vẽ (cho vẽ tự do)

    public DrawPanel() {
        setLayout(null);
        setBounds(0, 80, 1200, 650);
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint();
                currentPoint = startPoint;
                drawing = true;

                if (eraserMode) {
                    erase(e.getX(), e.getY());
                } else {
                    if (shapeType.equals("Đường thẳng")) {
                        currentPath = new Path2D.Float();
                        currentPath.moveTo(startPoint.x, startPoint.y);
                    }
                }
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                drawing = false;
                if (eraserMode) {
                    erase(e.getX(), e.getY());
                } else {
                    if (shapeType.equals("Đường thẳng") && currentPath != null) {
                        // Fix: Check if path is empty before creating ShapeState
                        if (!currentPath.getPathIterator(null).isDone()) {
                            ShapeState shapeState = new ShapeState(currentPath, currentColor, strokeWidth);
                            shapes.push(shapeState);
                            undoneShapes.clear();
                        }
                        currentPath = null;  // Reset đường dẫn hiện tại
                        repaint();
                    } else if (startPoint != null && currentPoint != null) {
                        Shape shape = createShape(startPoint, currentPoint);
                        if (shape != null) {
                            ShapeState shapeState = new ShapeState(shape, currentColor, strokeWidth);
                            shapes.push(shapeState);
                            undoneShapes.clear();
                            repaint();
                        }
                    }
                }
                startPoint = null;
                currentPoint = null;
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                currentPoint = e.getPoint();
                if (drawing) {
                    if (eraserMode) {
                        erase(e.getX(), e.getY());
                    } else {
                        if (shapeType.equals("Đường thẳng") && currentPath != null) {
                            currentPath.lineTo(currentPoint.x, currentPoint.y);
                            repaint();  // Vẽ ngay lập tức khi kéo chuột
                        }
                    }
                }
            }
        });
    }

    private void erase(int x, int y) {
        for (int i = shapes.size() - 1; i >= 0; i--) {  // Lặp ngược để xoá hình trên cùng trước
            ShapeState shapeState = shapes.get(i);
            if (shapeState.shape.intersects(x - eraserSize / 2, y - eraserSize / 2, eraserSize, eraserSize)) {
                undoneShapes.push(shapes.remove(i)); // Move to undone stack
                repaint();
                return;  // Xoá tối đa một hình mỗi lần click/drag
            }
        }
    }


    private Shape createShape(Point p1, Point p2) {
        if (p1 == null || p2 == null) return null;

        int x = Math.min(p1.x, p2.x);
        int y = Math.min(p1.y, p2.y);
        int width = Math.abs(p1.x - p2.x);
        int height = Math.abs(p1.y - p2.y);

        switch (shapeType) {
            case "Hình tròn":
                return new Ellipse2D.Float(x, y, width, height);
            case "Hình chữ nhật":
                return new Rectangle2D.Float(x, y, width, height);
            case "Hình vuông":
                int side = Math.max(width, height);
                return new Rectangle2D.Float(x, y, side, side);
            case "Hình thoi":
                int[] xPoints = {x + width / 2, x + width, x + width / 2, x};
                int[] yPoints = {y, y + height / 2, y + height, y + height / 2};
                return new Polygon(xPoints, yPoints, 4);
            case "Hình tam giác":
                int[] xPointsTriangle = {x, x + width / 2, x + width};
                int[] yPointsTriangle = {y + height, y, y + height};
                return new Polygon(xPointsTriangle, yPointsTriangle, 3);
            default:
                return null; // Should not happen
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Vẽ các hình đã hoàn thành
        for (ShapeState shapeState : shapes) {
            g2d.setColor(shapeState.color);
            g2d.setStroke(new BasicStroke(shapeState.strokeWidth));
            g2d.draw(shapeState.shape);
        }

        // Vẽ đường dẫn hiện tại (nếu có)
        if (shapeType.equals("Đường thẳng") && currentPath != null) {
            g2d.setColor(currentColor);
            g2d.setStroke(new BasicStroke(strokeWidth));
            g2d.draw(currentPath);
        }

        // Vẽ hình tròn cục tẩy (optional)
        if (eraserMode && startPoint != null) {
            g2d.setColor(Color.WHITE);  // Hoặc màu nền của DrawPanel
            g2d.setStroke(new BasicStroke(1));
            g2d.drawOval(currentPoint.x - eraserSize / 2, currentPoint.y - eraserSize / 2, eraserSize, eraserSize);
        }
    }

    public void clear() {
        shapes.clear();
        undoneShapes.clear();
        repaint();
    }

    public void setColor(Color color) {
        this.currentColor = color;
    }

    public Color getColor() {
        return this.currentColor;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public void setShapeType(String shapeType) {
        this.shapeType = shapeType;
    }

    public void setEraserMode(boolean eraserMode) {
        this.eraserMode = eraserMode;
    }

    public void undo() {
        if (!shapes.isEmpty()) {
            undoneShapes.push(shapes.pop());
            repaint();
        }
    }

    public void redo() {
        if (!undoneShapes.isEmpty()) {
            shapes.push(undoneShapes.pop());
            repaint();
        }
    }

    public static class ShapeState {
        public Shape shape;
        public Color color;
        public int strokeWidth;

        public ShapeState(Shape shape, Color color, int strokeWidth) {
            this.shape = shape;
            this.color = color;
            this.strokeWidth = strokeWidth;
        }
    }
}