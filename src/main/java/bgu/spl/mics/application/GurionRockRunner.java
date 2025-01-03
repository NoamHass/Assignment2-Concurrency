package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;

import java.sql.Time;

/**
 * The main entry point for the GurionRock Pro Max Ultra Over 9000 simulation.
 * <p>
 * This class initializes the system and starts the simulation by setting up
 * services, objects, and configurations.
 * </p>
 */
public class GurionRockRunner {

    /**
     * The main method of the simulation.
     * This method sets up the necessary components, parses configuration files,
     * initializes services, and starts the simulation.
     *
     * @param args Command-line arguments. The first argument is expected to be the path to the configuration file.
     */
    public static void main(String[] args) {
        String configurationPath = args[0];
        DataLoader.loadConfigurationData(configurationPath);

        System.out.println(Config.getLidarDataBasePath());
        System.out.println(Config.getCamerasDataPath());
        System.out.println(Config.getTotalEvents());
        System.out.println(Config.getCameras().get(0).getTerminationTime());

        System.out.println(Config.getLidarDataBasePath());

        SensorManagerService sms = new SensorManagerService(Config.getTotalEvents());
        Thread sensorManagerThread = new Thread(sms);
        sensorManagerThread.start();

        for(Camera camera : Config.getCameras()){
            CameraService cameraService = new CameraService(camera);
            Thread cameraThread = new Thread(cameraService);
            cameraThread.start();
            sms.AddSensor(cameraService);
        }

        for(LiDarWorkerTracker liDarWorkerTracker : Config.getLidarWorkers()){
            LiDarService liDarService = new LiDarService(liDarWorkerTracker);
            Thread lidarThread = new Thread(liDarService);
            lidarThread.start();
            sms.AddSensor(liDarService);
        }


        Thread poseThread = new Thread(new PoseService(new GPSIMU()));
        poseThread.start();

        Thread Fusionthread = new Thread(new FusionSlamService(FusionSlam.getInstance()));
        Fusionthread.start();

        Thread timeThread = new Thread(new TimeService(Config.getTickTime(), Config.getDuration()));
        timeThread.start();



        // TODO: Parse configuration file.
        // TODO: Initialize system components and services.
        // TODO: Start the simulation.
    }
}