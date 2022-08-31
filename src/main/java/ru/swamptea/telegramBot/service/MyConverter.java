package ru.swamptea.telegramBot.service;


import ru.swamptea.telegramBot.image.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.URL;

public class MyConverter implements TextGraphicsConverter
{
    private int maxWidth = Integer.MAX_VALUE;
    private int maxHeight = Integer.MAX_VALUE;
    private double maxRatio = Double.MAX_VALUE;
    private TextColorSchema schema = new MySchema();

    @Override
    public String convert(String url) throws IOException, BadImageSizeException {
        BufferedImage img = ImageIO.read(new URL(url));

        int width = img.getWidth();
        int height = img.getHeight();
        double heightRatio = (double)width/height;
        double widthRatio = (double)height/width;

        if(heightRatio > maxRatio){
            throw new BadImageSizeException(heightRatio, maxRatio);
        }
        if(widthRatio > maxRatio){
            throw new BadImageSizeException(widthRatio, maxRatio);
        }

        int newWidth = width;
        int newHeight = height;

        if(width > maxWidth){
            double nHeight = height/((double)width/maxWidth);
            newHeight = (int)nHeight;
            newWidth = maxWidth;
        }
        if(height > maxHeight){
            double nWidth = width/((double)height/maxHeight);
            newWidth = (int) nWidth;
            newHeight = maxHeight;
        }

        Image scaledImage = img.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOTH);

        BufferedImage bwImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);

        Graphics2D graphics = bwImg.createGraphics();

        graphics.drawImage(scaledImage, 0, 0, null);

        // Теперь в bwImg у нас лежит чёрно-белая картинка нужных нам размеров.

        WritableRaster bwRaster = bwImg.getRaster();

        int[] newArray = new int[3];
        char[][] array = new char[bwRaster.getWidth()][bwRaster.getHeight()];
        for(int w = 0; w < bwRaster.getWidth(); w++) {
            for(int h = 0; h < bwRaster.getHeight(); h++) {
                int color = bwRaster.getPixel(w, h, newArray)[0];
                char c = schema.convert(color);
                array[w][h] = c;
            }
        }

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < newHeight; i++){
            for(int j = 0; j < newWidth; j++){
                char c = array[j][i];
                sb.append(c);
                sb.append(c);
            }
            sb.append("\n");
        }

        return sb.toString(); // Возвращаем собранный текст.
    }

    @Override
    public void setMaxWidth(int width) {
        maxWidth = width;
    }

    @Override
    public void setMaxHeight(int height) {
        maxHeight = height;
    }

    @Override
    public void setMaxRatio(double maxRatio) {
        this.maxRatio = maxRatio;
    }

    @Override
    public void setTextColorSchema(TextColorSchema schema) {
        this.schema = schema;
    }
}
