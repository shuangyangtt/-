//这是一个用于上课摸鱼的游戏
//上下左右控制方向，空格发射子弹
//S键可以发射环形子弹
//如果老师下来了，长按S键可以保证自己不死
//一个方块10分
//主要用于上JAVA课摸鱼使用
//后续可能会更新，但是暂时还是以摸鱼为主
//Shuang Yang 2024.12.07
//这个游戏只有0.0092mb，所以不要要求太高

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

// 主类
public class 打钻块摸鱼游戏 {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Shooting Game");
        GamePanel gamePanel = new GamePanel();

        frame.add(gamePanel);
        frame.setSize(400, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        gamePanel.startGame();
    }
}

// 游戏面板
class GamePanel extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private Player player;
    private ArrayList<Bullet> bullets;
    private ArrayList<Enemy> enemies;
    private Random random;
    private int score;

    public GamePanel() {
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(this);

        player = new Player(200, 500);
        bullets = new ArrayList<>();
        enemies = new ArrayList<>();
        random = new Random();
        score = 0;

        timer = new Timer(16, this); // 每16ms刷新一次
    }

    public void startGame() {
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 绘制玩家
        player.draw(g);

        // 绘制子弹
        for (Bullet bullet : bullets) {
            bullet.draw(g);
        }

        // 绘制敌机
        for (Enemy enemy : enemies) {
            enemy.draw(g);
        }

        // 绘制分数
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 10, 20);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // 更新玩家
        player.update();

        // 更新子弹
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.update();

            if (bullet.y < 0) {
                bulletIterator.remove();
            }
        }

        // 更新敌机
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            enemy.update();

            if (enemy.y > getHeight()) {
                enemyIterator.remove();
            }
        }

        // 检测碰撞
        bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            enemyIterator = enemies.iterator();
            while (enemyIterator.hasNext()) {
                Enemy enemy = enemyIterator.next();
                if (bullet.getBounds().intersects(enemy.getBounds())) {
                    enemy.takeDamage(bullet.getDamage());
                    bulletIterator.remove();
                    if (enemy.getHealth() <= 0) {
                        enemyIterator.remove();
                        score += enemy.getPoints();
                    }
                    break;
                }
            }
        }

        // 检测玩家与敌机的碰撞
        enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            if (player.getBounds().intersects(enemy.getBounds())) {
                timer.stop(); // 游戏结束
                JOptionPane.showMessageDialog(this, "Game Over! Your Score: " + score);
                System.exit(0);
            }
        }

        // 生成新敌机
        if (random.nextInt(100) < 5) { // 增加敌机生成概率
            enemies.add(new Enemy(random.nextInt(getWidth() - 40), 0, random.nextInt(3) + 1));
        }

        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            player.setDx(-5);
        } else if (key == KeyEvent.VK_RIGHT) {
            player.setDx(5);
        } else if (key == KeyEvent.VK_SPACE) {
            bullets.add(player.shoot());
        } else if (key == KeyEvent.VK_CONTROL) { // 增加特殊子弹
            bullets.add(player.shootSpecial());
        } else if (key == KeyEvent.VK_S) { // 环形子弹技能
            bullets.addAll(player.shootRing());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT) {
            player.setDx(0);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}

// 玩家类
class Player {
    int x, y, width, height, dx;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        this.width = 40;
        this.height = 40;
        this.dx = 0;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public void update() {
        x += dx;
        if (x < 0) x = 0;
        if (x > 360) x = 360; // 防止超出边界
    }

    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillOval(x, y, width, height); // 使用椭圆来表示玩家的飞机
        g.setColor(Color.WHITE);
        g.fillRect(x + 15, y + 30, 10, 10); // 添加飞机的驾驶舱窗口
    }

    public Bullet shoot() {
        return new Bullet(x + width / 2 - 2, y);
    }

    public SpecialBullet shootSpecial() {
        return new SpecialBullet(x + width / 2 - 5, y);
    }

    public ArrayList<Bullet> shootRing() {
        ArrayList<Bullet> ringBullets = new ArrayList<>();
        for (int i = 0; i < 360; i += 30) { // 每30度发射一颗子弹
            double angle = Math.toRadians(i);
            int bulletX = x + width / 2;
            int bulletY = y;
            int dx = (int) (10 * Math.cos(angle));
            int dy = (int) (10 * Math.sin(angle));
            ringBullets.add(new RingBullet(bulletX, bulletY, dx, dy));
        }
        return ringBullets;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}

// 子弹类
class Bullet {
    int x, y, width, height;

    public Bullet(int x, int y) {
        this.x = x;
        this.y = y;
        this.width = 4;
        this.height = 10;
    }

    public void update() {
        y -= 10; // 子弹上移
    }

    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillRect(x, y, width, height);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getDamage() {
        return 1;
    }
}

// 特殊子弹类
class SpecialBullet extends Bullet {
    public SpecialBullet(int x, int y) {
        super(x, y);
        this.width = 10;
        this.height = 20;
    }

    @Override
    public void update() {
        y -= 15; // 特殊子弹更快
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.CYAN);
        g.fillRect(x, y, width, height);
    }

    @Override
    public int getDamage() {
        return 3; // 特殊子弹伤害更高
    }
}

// 环形子弹类
class RingBullet extends Bullet {
    int dx, dy;

    public RingBullet(int x, int y, int dx, int dy) {
        super(x, y);
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public void update() {
        x += dx;
        y += dy;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillRect(x, y, width, height);
    }
}

// 敌机类
class Enemy {
    int x, y, width, height, dy;
    private int health;

    public Enemy(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.width = 40;
        this.height = 40;
        this.dy = 2;

        switch (type) {
            case 1:
                health = 1;
                break;
            case 2:
                health = 3;
                break;
            case 3:
                health = 5;
                break;
        }
    }

    public void update() {
        y += dy;
    }

    public void draw(Graphics g) {
        g.setColor(health == 1 ? Color.RED : (health == 3 ? Color.ORANGE : Color.MAGENTA));
        g.fillRect(x, y, width, height); // 使用矩形表示敌机
        g.setColor(Color.BLACK);
        g.fillRect(x + 10, y + 10, 20, 20); // 添加敌机的窗口
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void takeDamage(int damage) {
        health -= damage;
    }

    public int getHealth() {
        return health;
    }

    public int getPoints() {
        return 10; // 每击毁一架敌机得10分
    }
}