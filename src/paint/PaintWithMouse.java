package paint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class PaintWithMouse extends JPanel {
    private ArrayList<Point> points = new ArrayList<>();
    private Color strokeColor = Color.BLUE; // Màu mặc định
    private int strokeSize = 3; // Độ dày mặc định

    public PaintWithMouse() {
        setBackground(Color.WHITE); // Đặt màu nền (tùy chọn)

        // Lắng nghe sự kiện kéo chuột để vẽ
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                points.add(e.getPoint()); // Lưu vị trí chuột
                repaint(); // Cập nhật lại giao diện
            }
        });
    }

    // Setter cho màu sắc
    public void setStrokeColor(Color color) {
        this.strokeColor = color;
    }

    // Setter cho độ dày
    public void setStrokeSize(int size) {
        this.strokeSize = size;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(strokeColor); // Màu nét vẽ
        g2d.setStroke(new BasicStroke(strokeSize)); // Độ dày nét vẽ

        // Vẽ từng điểm trong danh sách
        for (int i = 1; i < points.size(); i++) {
            g2d.drawLine(points.get(i - 1).x, points.get(i - 1).y,
                    points.get(i).x, points.get(i).y);
        }
    }
}