package paint;

import javax.swing.*;
import java.awt.*;
import java.util.Stack;

public class PaintApp extends JFrame {
    private DrawPanel drawPanel;
    private Stack<ShapeState> undoStack; // Stack lưu các bước vẽ cho Undo
    private Stack<ShapeState> redoStack; // Stack lưu các bước vẽ cho Redo
    public PaintApp(){
        setTitle("Paint");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
        setLayout(null);

        drawPanel = new DrawPanel();
        drawPanel.setBounds(0, 80, 1200, 650);
        add(drawPanel);

        undoStack = new Stack<>();
        redoStack = new Stack<>();

        createMenu(); // Tạo menu
        createToolPanel(); // Tạo các nút công cụ
    }
    public void createMenu(){
        JMenuBar menuBar= new JMenuBar(); // khởi tạo thanh menu
        JMenu file = new JMenu("File");

        JMenuItem newItem = new JMenuItem("New");
        newItem.addActionListener(e -> {
            drawPanel.clear(); // Xóa bảng vẽ
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

    public void createToolPanel(){
            JPanel toolPanel = new JPanel();
            toolPanel.setLayout(null);
            toolPanel.setBounds(0, 5, 1200, 60);

            // Mảng các tên công cụ vẽ
            String[] tools = {
                    "Bút chì",
                    "Hình chữ nhật",
                    "Hình tròn",
                    "Hình vuông",
                    "Hình thoi",
                    "Tam giác"
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
        toolBox.setBounds(10,  5, 80, 50);

            toolPanel.add(toolBox);

            // Nút Undo

        ImageIcon undoIcon = new ImageIcon("src\\icons\\undo.png");
        ImageIcon scaledUndoIcon = new ImageIcon(undoIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        JButton btnUndo = new JButton(scaledUndoIcon);
            btnUndo.setBounds(105, 15, 50, 30);
            btnUndo.addActionListener(e -> undo());

            // Nút Redo
        ImageIcon redoIcon = new ImageIcon("src\\icons\\redo.png");
        ImageIcon scaledRedoIcon = new ImageIcon(redoIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        JButton btnRedo = new JButton(scaledRedoIcon);
            btnRedo.setBounds(165, 15, 50, 30);
            btnRedo.addActionListener(e -> redo());

        // Nút Tẩy
        ImageIcon eraserIcon = new ImageIcon("src\\icons\\eraser.png");
        ImageIcon scaledEraserIcon = new ImageIcon(eraserIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        JButton btnEraser = new JButton(scaledEraserIcon);
        btnEraser.setBounds(225, 15, 50, 30);

        JLabel strokeSizeText = new JLabel("Stroke Size:");
        strokeSizeText.setBounds(290, 15, 100, 30);
        strokeSizeText.setFont(new Font("Arial", Font.BOLD, 12));

        // Thanh điều chỉnh kích thước nét vẽ
        JSlider strokeSizeSlider = new JSlider(1, 20, 5); // Giá trị từ 1 đến 20, mặc định là 5
        strokeSizeSlider.setBounds(360, 22, 150, 20);
        JLabel strokeSizeLabel = new JLabel("5px");
        strokeSizeLabel.setBounds(520, 15, 40, 30);

        // Nút Chọn màu đường vẽ
        JButton btnStrokeColor = new JButton();
        btnStrokeColor.setBounds(560, 15, 30, 30);
        btnStrokeColor.setBackground(Color.BLACK);

        // Nút Chọn màu nền
        ImageIcon fillIcon = new ImageIcon("src\\icons\\paint-bucket.png");
        ImageIcon scaledFillIcon = new ImageIcon(fillIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        JButton btnFillColor = new JButton(scaledFillIcon);
        btnFillColor.setBounds(600, 15, 50, 30);

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
        if (!undoStack.isEmpty()) {
            ShapeState lastState = undoStack.pop(); // Lấy bước vẽ cuối cùng từ undoStack
            redoStack.push(lastState); // Đẩy vào redoStack

            // Xóa hình vẽ cuối cùng và vẽ lại các hình còn lại
            drawPanel.removeLastShape();
            drawPanel.repaint();
        }
    }

    // Hàm thực hiện Redo
    private void redo() {
        if (!redoStack.isEmpty()) {
            ShapeState lastState = redoStack.pop(); // Lấy bước vẽ cuối cùng từ redoStack
            undoStack.push(lastState); // Đẩy vào undoStack

            // Vẽ lại hình đã redo
            drawPanel.addShape(lastState.getShape());
            drawPanel.repaint();
        }
    }

}
