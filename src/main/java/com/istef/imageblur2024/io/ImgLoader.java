package com.istef.imageblur2024.io;

import com.istef.imageblur2024.exceptios.MyException;

import java.awt.image.BufferedImage;

public interface ImgLoader {
    String getInputFileType();
    BufferedImage load(String src) throws MyException;
}
