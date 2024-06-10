package com.istef.imageblur2024;

import com.istef.swing.ImageFilterAppSwing;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Main {

    public static void main(String[] args) {

//        ImageFilterAppCLI imageFilterAppCLI = new ImageFilterAppCLI();
        ImageFilterAppSwing imageFilterAppSwing = new ImageFilterAppSwing();

//        imageFilterAppCLI.run();
        imageFilterAppSwing.run();

    }

}
