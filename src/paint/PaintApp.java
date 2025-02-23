package paint;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Stack;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class PaintApp extends JFrame {
    private DrawPanel drawPanel;
    private Stack<ShapeState> undoStack = new Stack<>();
    private Stack<ShapeState> redoStack = new Stack<>();
    private Color currentColor = Color.BLACK; // Khởi tạo màu mặc định
    private String shapeType = "Đường thẳng"; // Khởi tạo loại hình mặc định
    private BufferedImage canvas; // Không dùng
    private Graphics2D graphics2D; // Không dùng
    private int activeTool; // Không dùng
    private int group; // Không dùng
    private boolean eraserMode = false;

    public PaintApp() {
        setTitle("Paint");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        drawPanel = new DrawPanel();
        add(drawPanel);

        createMenu(); // Tạo menu
        createToolPanel(); // Tạo các nút công cụ
        setVisible(true);
    }

    public void createMenu() {
        JMenuBar menuBar = new JMenuBar(); // khởi tạo thanh menu
        JMenu file = new JMenu("File");

        JMenuItem newItem = new JMenuItem("New");
        newItem.addActionListener(e -> {
            clearCanvas(); // Sử dụng hàm clear mới
            undoStack.clear(); // Xóa lịch sử Undo
            redoStack.clear(); // Xóa lịch sử Redo
        });

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0)); // khi nhấn exit thoát khỏi màn hình

        file.add(newItem);
        file.addSeparator();
        file.add(exitItem);

        menuBar.add(file);
        setJMenuBar(menuBar); // Đặt thanh menu cho JFrame
    }

    public void createToolPanel() {
        JPanel toolPanel = new JPanel();
        toolPanel.setLayout(null);
        toolPanel.setBounds(0, 5, 1200, 60);

        // Mảng các tên công cụ vẽ
        String[] tools = {
                "Đường thẳng",
                "Hình chữ nhật",
                "Hình tròn",
                "Hình vuông",
                "Hình thoi",
                "Hình tam giác"
        };
        String[] iconPaths = {
                "src/icons/pen.png",
                "src/icons/rectangle.png",
                "src/icons/circle.png",
                "src/icons/square.png",
                "src/icons/diamond.png",
                "src/icons/triangle.png"
        };
        // Tạo JComboBox với các công cụ vẽ
        JComboBox<ImageIcon> toolBox = new JComboBox<>();
        for (String path : iconPaths) {
            ImageIcon icon = new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
            toolBox.addItem(icon);
        }

        toolBox.setBounds(10, 5, 80, 50);
        toolBox.addActionListener(e -> {
            int selectedIndex = toolBox.getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < tools.length) {
                shapeType = tools[selectedIndex];
                drawPanel.setShapeType(shapeType); // Truyền loại hình vẽ đến DrawPanel
                eraserMode = false; // Tắt chế độ tẩy khi chọn hình dạng
                drawPanel.setEraserMode(false);
            }
        });
        toolPanel.add(toolBox);

        // Nút Undo
        ImageIcon undoIcon = new ImageIcon("src\\icons\\undo.png");
        ImageIcon scaledUndoIcon = new ImageIcon(undoIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        JButton btnUndo = new JButton(scaledUndoIcon);
        btnUndo.setBounds(140, 15, 50, 30);
        btnUndo.addActionListener(e -> undo());

        // Nút Redo
        ImageIcon redoIcon = new ImageIcon("src\\icons\\redo.png");
        ImageIcon scaledRedoIcon = new ImageIcon(redoIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        JButton btnRedo = new JButton(scaledRedoIcon);
        btnRedo.setBounds(200, 15, 50, 30);
        btnRedo.addActionListener(e -> redo());

        // Nút Tẩy
        ImageIcon eraserIcon = new ImageIcon("src\\icons\\eraser.png");
        ImageIcon scaledEraserIcon = new ImageIcon(eraserIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        JButton btnEraser = new JButton(scaledEraserIcon);
        btnEraser.setBounds(260, 15, 50, 30);
        btnEraser.addActionListener(e -> {
            eraserMode = !eraserMode; // Bật/tắt chế độ tẩy
            drawPanel.setEraserMode(eraserMode);
        });

        JLabel strokeSizeText = new JLabel("Stroke Size:");
        strokeSizeText.setBounds(320, 15, 100, 30);
        strokeSizeText.setFont(new Font("Arial", Font.BOLD, 12));

        // Thanh điều chỉnh kích thước nét vẽ
        JSlider strokeSizeSlider = new JSlider(1, 20, 5); // Giá trị từ 1 đến 20, mặc định là 5
        strokeSizeSlider.setBounds(390, 22, 150, 20);
        JLabel strokeSizeLabel = new JLabel("5px");
        strokeSizeLabel.setBounds(550, 15, 40, 30);

        strokeSizeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int strokeSize = strokeSizeSlider.getValue();
                strokeSizeLabel.setText(strokeSize + "px");
                drawPanel.setStrokeWidth(strokeSize); // Cập nhật strokeWidth trong DrawPanel
            }
        });

        // Nút Chọn màu đường vẽ
        JButton btnStrokeColor = new JButton();
        btnStrokeColor.setBounds(590, 15, 30, 30);
        btnStrokeColor.setBackground(currentColor); // Khởi tạo màu nền
        btnStrokeColor.addActionListener(e -> {
            // Mở hộp thoại chọn màu
            Color selectedColor = JColorChooser.showDialog(null, "Chọn màu vẽ", currentColor);
            if (selectedColor != null) {
                currentColor = selectedColor; // Cập nhật màu hiện tại
                drawPanel.setColor(currentColor); // Gửi màu mới đến DrawPanel
                btnStrokeColor.setBackground(currentColor); // Cập nhật màu trên nút
            }
            eraserMode = false;
            drawPanel.setEraserMode(false);
        });

        // Nút Chọn màu nền
        ImageIcon fillIcon = new ImageIcon("src\\icons\\paint-bucket.png");
        ImageIcon scaledFillIcon = new ImageIcon(fillIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        JButton btnFillColor = new JButton(scaledFillIcon);
        btnFillColor.setBounds(630, 15, 50, 30);

        btnFillColor.addActionListener(e -> {
            Color selecteColor = JColorChooser.showDialog(null, "Choose a background color", drawPanel.getBackground()); // Truyền màu nền hiện tại
            if (selecteColor != null) {
                drawPanel.setBackground(selecteColor);
                drawPanel.repaint();
            }
            eraserMode = false;
            drawPanel.setEraserMode(false);
        });

        toolPanel.add(btnUndo);
        toolPanel.add(btnRedo);
        toolPanel.add(strokeSizeText);
        toolPanel.add(strokeSizeSlider);
        toolPanel.add(strokeSizeLabel);
        toolPanel.add(btnEraser);
        toolPanel.add(btnFillColor);
        toolPanel.add(btnStrokeColor);

        add(toolPanel);
        revalidate();
        repaint();
    }

    // Hàm thực hiện Undo
    private void undo() {
        drawPanel.undo(); // Gọi hàm undo của DrawPanel
    }

    // Hàm thực hiện Redo
    private void redo() {
        drawPanel.redo(); // Gọi hàm redo của DrawPanel
    }

    public void setTool(int tool) {
        this.activeTool = tool;
    }

    // Xóa canvas
    public void clearCanvas() {
        drawPanel.clear(); // Gọi hàm clear của DrawPanel
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PaintApp());
    }
}