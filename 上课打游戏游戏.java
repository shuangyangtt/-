//这是一个用于上课摸鱼的游戏
//S即Study代表学习，G即Game代表游戏
//当游戏里的老师靠近时记得立刻按S，否则会死
//但是按S会扣分，所以请谨慎使用
//主要用于上JAVA课摸鱼使用
//后续可能会更新，但是暂时还是以摸鱼为主
//Shuang Yang 2024.12.07
//这个游戏只有0.006mb，所以不要要求太高


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class 上课打游戏游戏 {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Classroom Game");
        GamePanel gamePanel = new GamePanel();

        frame.add(gamePanel);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        gamePanel.startGame();
    }
}

class GamePanel extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private Teacher teacher;
    private ArrayList<Student> students;
    private Player player;
    private int score;
    private boolean isStudying;
    private Random random;

    public GamePanel() {
        this.setBackground(new Color(245, 245, 220)); // 设置教室背景为米色
        this.setFocusable(true);
        this.addKeyListener(this);

        teacher = new Teacher(350, 50);
        students = new ArrayList<>();
        for (int i = 0; i < 30; i++) { // 减少学生数量到30个，并增加间距
            int row = i / 6;
            int col = i % 6;
            students.add(new Student(100 + col * 120, 100 + row * 120));
        }
        player = new Player(100, 100); // 控制一个学生
        score = 0;
        isStudying = false;
        random = new Random();

        timer = new Timer(20, this); // 增加刷新频率，老师移动更快
    }

    public void startGame() {
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 绘制教室背景
        g.setColor(new Color(220, 220, 220));
        g.fillRect(50, 50, 700, 500); // 教室范围

        g.setColor(new Color(139, 69, 19));
        g.fillRect(350, 50, 100, 40); // 讲台

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Teacher's Desk", 355, 75);

        // 绘制老师
        teacher.draw(g);

        // 绘制学生
        for (Student student : students) {
            student.draw(g);
        }

        // 绘制玩家（学生）
        player.draw(g);

        // 绘制分数
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 10, 20);

        // 显示规则
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Rules: Press S to Study, G to Play. Don't get caught playing!", 10, 50);

        g.setFont(new Font("Arial", Font.ITALIC, 14));
        g.drawString("Tip: Keep an eye on the teacher!", 10, 70);

        // 显示玩家状态
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(isStudying ? new Color(34, 139, 34) : new Color(255, 69, 0));
        g.drawString(isStudying ? "Studying" : "Playing", 700, 20);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // 更新老师位置
        teacher.update();

        // 检测老师是否靠近玩家
        if (teacher.isNearPlayer(player)) {
            if (!isStudying) {
                timer.stop();
                JOptionPane.showMessageDialog(this, "You got caught playing! Final Score: " + score);
                System.exit(0);
            }
        }

        // 根据玩家状态更新分数
        if (isStudying) {
            score = Math.max(0, score - 1); // 学习扣分，最低为0
        } else {
            score += 5; // 玩游戏加分
        }

        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_S) {
            isStudying = true;
        } else if (key == KeyEvent.VK_G) {
            isStudying = false;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}

class Teacher {
    private int x, y;
    private int dx, dy;
    private Random random;

    public Teacher(int x, int y) {
        this.x = x;
        this.y = y;
        this.dx = 20; // 提高老师移动速度
        this.dy = 20; // 提高垂直移动速度
        this.random = new Random();
    }

    public void update() {
        x += dx;
        y += dy;

        // 保持老师在教室范围内移动
        if (x < 50 || x > 700) {
            dx = -dx;
        }
        if (y < 50 || y > 500) {
            dy = -dy;
        }
    }

    public void draw(Graphics g) {
        g.setColor(new Color(255, 69, 0));
        g.fillRect(x, y, 40, 40); // 用矩形表示老师
        g.setColor(Color.WHITE);
        g.fillRect(x + 10, y + 10, 20, 20); // 老师的脸部窗口
        g.setColor(Color.BLACK);
        g.drawString("T", x + 15, y + 30); // 标示为老师
    }

    public boolean isNearPlayer(Player player) {
        return Math.abs(this.x - player.getX()) < 50 && Math.abs(this.y - player.getY()) < 50;
    }
}

class Player {
    private int x, y;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics g) {
        g.setColor(new Color(30, 144, 255));
        g.fillRect(x, y, 40, 40); // 用矩形表示玩家
        g.setColor(Color.WHITE);
        g.fillRect(x + 10, y + 10, 20, 20); // 学生的脸部窗口
        g.setColor(Color.BLACK);
        g.drawString("P", x + 15, y + 30); // 标示为玩家
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

class Student {
    private int x, y;

    public Student(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics g) {
        g.setColor(new Color(124, 252, 0));
        g.fillRect(x, y, 40, 40); // 用矩形表示其他学生
        g.setColor(Color.WHITE);
        g.fillRect(x + 10, y + 10, 20, 20); // 学生的脸部窗口
        g.setColor(Color.BLACK);
        g.drawString("S", x + 15, y + 30); // 标示为学生
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
