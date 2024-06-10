package com.istef.imageblur2024.io;

import com.istef.imageblur2024.exceptios.MyException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


public class ImgUrlLoader implements ImgLoader {
    private InputStream in = null;
    private String inputFileType = "";

    public String getInputFileType() {
        return inputFileType;
    }

    @Override
    public BufferedImage load(String imgUrl) throws MyException {
        try {
            URL url = new URL(imgUrl);
            inputFileType = url.getPath().substring(url.getPath().lastIndexOf(".") + 1);

            URLConnection con = url.openConnection();
            con.setConnectTimeout(10000);
            con.setReadTimeout(10000);
            in = con.getInputStream();
            return ImageIO.read(in);
        } catch (IllegalArgumentException | IOException e) {
            System.err.println(e.getMessage());
            throw new MyException("Could not load image file from the given URL: " + imgUrl);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }
}
