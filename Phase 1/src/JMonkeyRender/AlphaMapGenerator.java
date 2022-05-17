package jmonkeyrender;

import datastorage.Terrain;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public final class AlphaMapGenerator {
    private static final int DEFAULT_HEIGHT = 1024;

    private static final int DEFAULT_WIDTH = 1024;

    public static void generateAlphaMap(Terrain terrain) {
        final BufferedImage image = new BufferedImage ( DEFAULT_WIDTH, DEFAULT_HEIGHT, BufferedImage.TYPE_INT_ARGB );
        final Graphics2D graphics2D = image.createGraphics ();

        //This sets everything to grass
        graphics2D.setPaint ( Color.RED );
        graphics2D.fillRect ( 0,0,DEFAULT_WIDTH,DEFAULT_HEIGHT );

        //This sets any sandpit that is required
        for(int x=0; x<1024; x++){
            for(int y=0; y<1024; y++){
                if (terrain.isPointInZone(x,y)) {
                    graphics2D.setColor(Color.GREEN);
                    graphics2D.drawLine(x, y, x, y);
                }
            }
        }

        graphics2D.dispose ();
        try {
            ImageIO.write(image, "png", new File(System.getProperty("user.dir") + "/src/main/resources/Terrain/image.png"));
            ImageIO.write(image, "png", new File(System.getProperty("user.dir") + "/target/classes/Terrain/image.png"));
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
