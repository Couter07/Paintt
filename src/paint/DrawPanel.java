package paint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.awt.event.MouseEvent;

public class DrawPanel extends JPanel {
    private ArrayList<Shape> shape = new ArrayList<>();
    private ArrayList<Shape> undoneShapes = new ArrayList<>();
    private ArrayList<ShapeState> shapes = new ArrayList<>();
    private Color currentColor = Color.BLACK;
    private int strokeWidth = 2;
    private Point startPoint, endPoint;
    private String shapeType = "Hình chữ nhật"; // Mặc định tránh null
    private ArrayList<Point> points = new ArrayList<>();

    //private
    public DrawPanel() {
        setLayout(null);
        setBounds(0, 80, 1200, 650);
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint();// lưu điểm bắt đầu
                points.add(startPoint);
                repaint();

            }

            @Override
            public void mouseReleased(MouseEvent e) {
//                endPoint = e.getPoint();// lưu điêm kết thúc
//                if (startPoint.equals(endPoint)) return;
                if (startPoint == null || endPoint == null || startPoint.equals(endPoint)) {
                    return; // Không vẽ gì nếu không kéo chuột
                }
                    Shape shape = createShape(startPoint, endPoint);

                    if (shape != null) {
                        shapes.add(new ShapeState(shape, currentColor, strokeWidth));
//                        repaint();
                    }
                    //thêm diểm đầu và điểm cuối để k nối liền nét vẽ
                shapes.add(new ShapeState(new Ellipse2D.Double(startPoint.x - 2, startPoint.y - 2, 4, 4), currentColor, strokeWidth));
                shapes.add(new ShapeState(new Ellipse2D.Double(endPoint.x - 2, endPoint.y - 2, 4, 4), currentColor, strokeWidth));

                startPoint = null; // Reset để không nối nét tiếp theo
                endPoint = null;
                repaint();


//                    if (points.size() > 1) {
//                        for (int i = 0; i < points.size() - 1; i++) {
//                            Shape line = new Line2D.Float(points.get(i), points.get(i + 1));
//                            shapes.add(new ShapeState(line, currentColor, strokeWidth));
//                        }
//                    }
//                    points.clear(); // Reset danh sách điểm sau mỗi lần vẽ xong
//                    repaint();
                }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
//            public void mouseDragged(MouseEvent e) {
//                super.mouseDragged(e);
//                points.add(e.getPoint());
//                repaint();
            public void mouseDragged(MouseEvent e) {
                if (!points.isEmpty()) {
                    Point endPoint = points.get(points.size() - 1);
                    Shape tempLine = new Line2D.Float(endPoint, e.getPoint());
                    shapes.add(new ShapeState(tempLine, currentColor, strokeWidth));
                    points.add(e.getPoint());
                    repaint();
            }}
        });
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
                return new Rectangle(x, y, width, height);
            case "Hình tam giác":
                int[] xPoints = {x, x + width / 2, x + width};
                int[] yPoints = {y + height, y, y + height};
                return new Polygon(xPoints, yPoints, 3);
            default:
                return new Rectangle(x, y, width, height);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g);
        g2d.setColor(currentColor); // Màu nét vẽ
        g2d.setStroke(new BasicStroke(3)); // Độ dày nét vẽ

        // Vẽ từng điểm trong danh sách
        for (int i = 1; i < points.size(); i++) {
            g2d.drawLine(points.get(i - 1).x, points.get(i - 1).y,
                    points.get(i).x, points.get(i).y);
        }
    }
// chưa có nd
    public void clear() {
        shapes.clear();
        undoneShapes.clear();
        repaint();

    }

    public void setColor(Color selecteColor) {
//        Color color;
        this.currentColor = selecteColor;
    }

    public void removeLastShape() {
    }

    public void addShape(Shape s) {
        shape.add(s);
    }
}
