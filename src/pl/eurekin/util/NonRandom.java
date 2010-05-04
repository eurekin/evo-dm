package pl.eurekin.util;

//
// nonrandom - Tim Tyler 1998.
//
import java.awt.*;
import java.util.Random;

public class NonRandom extends java.applet.Applet implements Runnable {

    int[][] out_grid;
    int a_atgrid = 0;
    boolean RepaintAll = true;
    Thread calculate;
    Image image_Image;
    Graphics main_image;
    int lastx = -1, lasty = -1;
    final static int xoff = 0, yoff = 0;
    int side = 100;
    int x, y;
    //
    // TT_Random TT_rnd = new TT_Random();
    Random rnd = new Random();
    // MersenneTwisterFast rnd = new MersenneTwisterFast();
    int hside = 256;
    int vside = 256;
    long start = 256;
    long stop = 256;
    long diff_a;
    long diff_b;
    long diff_c;
    int i;
    ///
    int gridsize = 1;
    int new_size;
    int generation = 0;
    int width;
    int height;
    Color WorldColor = new Color(64, 64, 64);
    Color GridColor = Color.black;
    Color foreground_Color = Color.white;

    // ...........................Methods........................
    @Override
    public void init() {
        if (getParameter("h") != null) {
            hside = (new Integer(getParameter("h"))).intValue();
        }
        if (getParameter("v") != null) {
            vside = (new Integer(getParameter("v"))).intValue();
        }
        if (getParameter("g") != null) {
            gridsize = (new Integer(getParameter("g"))).intValue();
        }
        new_size = (gridsize > 1) ? gridsize : 2;
        // tell browser...
        width = hside * gridsize;
        height = vside * gridsize;
        resize(width + xoff, height + yoff);
        out_grid = new int[hside][vside];
        rnd.setSeed(99);
        start();
        calculate = new Thread(this);
        calculate.start();
    }

    private void docalculate() {
        generation++;
        for (x = 0; x < hside; x++) {
            for (y = 0; y < vside; y++) {
                // *very* poor quality
                 out_grid[x][y] = (int) rnd.nextLong();
                // out_grid[x][y] = (int) TT_rnd.nextInt();
                // poor quality
                // out_grid[x][y] = rnd.nextInt();
                // much more reasonable output
                //out_grid[x][y] = (int) (rnd.nextDouble() * 128);
            }
        }

        // System.out.println("wwwibejdbiu");
    }

    public void redraw_grid(Graphics g) {
        int _width = hside * gridsize;

        g.setColor(WorldColor);
        g.fillRect(0, 0, _width, _width);

        if (gridsize > 1) {
            g.setColor(GridColor);
            for (x = 0; x <= _width; x += gridsize) {
                g.drawLine(x, 0, x, _width);
            }

            g.setColor(GridColor);
            for (y = 0; y <= _width; y += gridsize) {
                g.drawLine(0, y, _width, y);
            }
        }
    }

    // .....................Run Method.......................
    @Override
    public void run() {
        while (true) {
            docalculate();
            update_image(main_image);
            repaint();
            updateStatus();
        }
    }

    @Override
    public void start() {
        image_Image = createImage(hside * gridsize + 1, vside * gridsize + 1);
        main_image = image_Image.getGraphics();
        RepaintAll = true;
    }


    @Override
    public void update(Graphics g) {
        paint(g);
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(image_Image, xoff, yoff, this);
    }

    public void update_image(Graphics g) {
        if (RepaintAll) {
            RepaintAll = false;
            redraw_grid(g);
        }
        for (x = 0; x < hside; x++) {
            for (y = 0; y < vside; y++) {
                if ((out_grid[x][y] & 1) != 0) {
                    g.setColor(foreground_Color);
                } else {
                    g.setColor(WorldColor);
                }
                g.fillRect(x * gridsize + 1, y * gridsize + 1, new_size - 1, new_size - 1);
            }
        }
    }

    public void updateStatus() {
        showStatus("Generation:" + generation);
    }
}
