import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.IOException;


public class scribble extends Component implements ActionListener, ChangeListener {
    JFrame frame;
    JMenuBar mb;
    JMenu menu;
    JButton colorButton, erasebutt, setBack;
    JToolBar toolBar, toolBar2;
    ScribblePane2 scribblePane;
    Container contentPane;
    JSlider slider;
    int stoke = 10;

    public scribble() throws IOException {
        frame = new JFrame("SCRIBBLE");
        frame.setBounds(250, 110, 1000, 670);
        frame.setVisible(true);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(frame.DO_NOTHING_ON_CLOSE);

        contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());

        mb = new JMenuBar();
        menu = new JMenu("Menu");
        mb.add(menu);
        frame.setJMenuBar(mb);


        toolBar = new JToolBar();
        toolBar.setBackground(new Color(221, 221, 221));
        toolBar.setOrientation(SwingConstants.VERTICAL);
        contentPane.add(toolBar, BorderLayout.WEST);

        toolBar2 = new JToolBar();
        toolBar2.setBackground(new Color(221, 221, 221));
        toolBar2.setOrientation(SwingConstants.HORIZONTAL);
        contentPane.add(toolBar2, BorderLayout.NORTH);

        Action black = new scribble.ColorAction(Color.black);
        Action red = new scribble.ColorAction(Color.red);
        Action blue = new scribble.ColorAction(Color.blue);
        Action yellow = new scribble.ColorAction(Color.yellow);
        Action green = new scribble.ColorAction(Color.green);
        Action pink = new scribble.ColorAction(Color.MAGENTA);
        Action grey = new scribble.ColorAction(Color.darkGray);
        Action brown = new scribble.ColorAction(new Color(138, 76, 35));
        Action orange = new scribble.ColorAction(Color.orange);
        Action white = new scribble.ColorAction(Color.WHITE);
        Action clear = new ClearAction();
        Action quit = new quitAction();

        erasebutt = new JButton("    Erase    ");
        setBack = new JButton("    Background    ");

        slider = new JSlider(0, 50, 10);
        slider.setMinorTickSpacing(2);
        slider.setMajorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setMaximumSize(new Dimension(350, Short.MAX_VALUE));

        toolBar.add(pink);
        toolBar.add(red);
        toolBar.add(orange);
        toolBar.add(yellow);
        toolBar.add(green);
        toolBar.add(blue);
        toolBar.add(brown);
        toolBar.add(grey);
        toolBar.add(black);
        toolBar.add(white);

        toolBar2.add(slider);
        toolBar2.addSeparator();
        toolBar2.add(erasebutt);
        toolBar2.addSeparator();
        toolBar2.add(setBack);
        toolBar2.addSeparator();
        toolBar2.add(clear);
        toolBar2.addSeparator();
        toolBar2.add(quit);

        menu.add(clear);
        menu.addSeparator();
        menu.add(quit);

        colorButton = new JButton();
        Image img = ImageIO.read(new FileInputStream("D:\\colorButton.PNG"));
        colorButton.setIcon(new ImageIcon(img));
        toolBar.add(colorButton);

        scribblePane = new ScribblePane2();
        scribblePane.setBackground(Color.white);
        scribblePane.setBounds(0, 200, 1000, 400);
        contentPane.add(scribblePane, BorderLayout.CENTER);

        //ActonListeners
        colorButton.addActionListener(this);
        erasebutt.addActionListener(this);
        setBack.addActionListener(this);
        slider.addChangeListener(this);
    }

    //main method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new scribble();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == colorButton) {
            Color color = JColorChooser.showDialog(scribble.this,
                    "Select Drawing Color", scribblePane.getColor());
            if (color != null)
                scribblePane.setColor(color);
        }

        if (e.getSource() == erasebutt) {
            scribblePane.color = Color.white;
        }

        if (e.getSource() == setBack) {
            scribblePane.setBackground(scribblePane.color);
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        stoke = slider.getValue();
    }

    class ScribblePane2 extends JPanel {
        public ScribblePane2() {

            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    moveto(e.getX(), e.getY());
                    requestFocus();
                }
            });

            //to draw on current x and y axis
            addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseDragged(MouseEvent e) {
                    lineto(e.getX(), e.getY());
                }

            });


            addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_C) {
                        clear();
                    }
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        quit();
                    }
                }
            });
        }

        protected int last_x, last_y;

        public void moveto(int x, int y) {
            last_x = x;
            last_y = y;
        }


        public void lineto(int x, int y) {
            Graphics2D g = (Graphics2D) getGraphics();
            g.setColor(color);
            g.setStroke(new BasicStroke(stoke));
            g.drawLine(last_x, last_y, x, y);
            moveto(x, y);
        }

        public void clear() {
            scribblePane.setBackground(Color.white);
            repaint();
        }

        public void quit() {
            int response = JOptionPane.showConfirmDialog(scribble.this,
                    "Really Quit?");
            if (response == JOptionPane.YES_NO_OPTION)
                System.exit(0);
        }


        //to change color
        Color color = Color.black;
        public void setColor(Color color) {
            this.color = color;
        }
        public Color getColor() {
            return color;
        }

    }

    class ClearAction extends AbstractAction {
        public ClearAction() {
            super("        Clear        ");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            scribblePane.clear();
        }
    }

    class quitAction extends AbstractAction {
        public quitAction() {
            super("        Quit        ");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            scribblePane.quit();
        }
    }


    class ColorAction extends AbstractAction {
        Color color;

        public ColorAction(Color color) {
            this.color = color;
            putValue(Action.LARGE_ICON_KEY, new scribble.ColorIcon(color)); // specify icon
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            scribblePane.setColor(color);
        }
    }

    static class ColorIcon implements Icon {
        Color color;

        public ColorIcon(Color color) {
            this.color = color;
        }

        public int getIconHeight() {
            return 25;
        }

        public int getIconWidth() {
            return 25;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(color);
            g.fillRect(x, y, 25, 25);
        }
    }
}
