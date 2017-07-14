package no.daffern.vehicle.server;

import com.esotericsoftware.kryonet.*;
import no.daffern.vehicle.common.Common;
import no.daffern.vehicle.common.Packets;
import no.daffern.vehicle.menu.ServerMenu;
import no.daffern.vehicle.network.MyServer;
import no.daffern.vehicle.server.handlers.ItemHandler;
import no.daffern.vehicle.server.handlers.ServerPlayerHandler;
import no.daffern.vehicle.server.handlers.ServerVehicleHandler;
import no.daffern.vehicle.server.world.WorldHandler;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Daffern on 04.11.2016.
 */
public class ServerMain extends Thread {


    ServerMenu serverMenu;

    private float accumulator = 0;
    double oldTime;

    boolean running = true;

    Object lock = new Object();

    public ServerMain() {
        S.myServer = new MyServer();
        S.worldHandler = new WorldHandler();
        S.itemHandler = new ItemHandler();
        S.playerHandler = new ServerPlayerHandler();
        S.vehicleHandler = new ServerVehicleHandler();
/*
        AbstractInputProcessor abstractInputProcessor = new AbstractInputProcessor() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.UP) {
                    orthographicCamera.translate(0, 10);
                }
                if (keycode == Input.Keys.DOWN) {
                    orthographicCamera.translate(0, -10);
                }
                if (keycode == Input.Keys.LEFT) {
                    orthographicCamera.translate(-10, 0);
                }
                if (keycode == Input.Keys.RIGHT) {
                    orthographicCamera.translate(10, 0);
                }
                orthographicCamera.update();
                return super.keyDown(keycode);
            }


            @Override
            public boolean scrolled(int amount) {
                orthographicCamera.zoom = orthographicCamera.zoom + ( (float)amount * 0.3f);
                if (orthographicCamera.zoom < 0.1f) {
                    orthographicCamera.zoom = 0.1f;
                }
                orthographicCamera.update();
                return super.scrolled(amount);
            }
        };

        PriorityInputHandler.getInstance().addInputProcessor(abstractInputProcessor, 3);
*/
        serverMenu = new ServerMenu();
        serverMenu.loadServerMenu(new ServerMenu.ServerMenuListener() {
            @Override
            public void onHostClicked(int tcpPort, int udpPort) {
                initServer(tcpPort, udpPort);
            }
        });

    }

    //for debugging
    public ServerMain(int tcpPort, int udpPort) {

        S.myServer = new MyServer();
        S.worldHandler = new WorldHandler();
        S.itemHandler = new ItemHandler();
        S.playerHandler = new ServerPlayerHandler();
        S.vehicleHandler = new ServerVehicleHandler();


        initServer(tcpPort, udpPort);

        //S.worldHandler.loadWorld("test.tmx");
        S.worldHandler.loadContinuousWorld();


        start();//star thread
    }

    private void initServer(int tcpPort, int udpPort) {

        S.myServer.register(Packets.networkClasses);

        try {

            S.myServer.bind(tcpPort, udpPort);
            S.myServer.start();

        } catch (IOException e) {
            e.printStackTrace();
            if (serverMenu != null)
                serverMenu.appendConsole("Failed to start on ports: " + tcpPort + " " + udpPort);
        } finally {
            if (serverMenu != null)
                serverMenu.appendConsole("Started server on ports: " + tcpPort + " " + udpPort);
        }
    }

    @Override
    public void run() {


        while (running) {

            double currentTime = (double) System.currentTimeMillis() / 1000.0;
            float delta = (float) (currentTime - oldTime);
            oldTime = currentTime;

            float frameTime = Math.min(delta, 0.25f);
            accumulator += frameTime;


            while (accumulator >= Common.TIME_STEP) {

                synchronized (lock) {

                    S.myServer.dispatchQueues();

                    S.playerHandler.preStep();
                    S.vehicleHandler.preStep();

                    S.worldHandler.worldStep();

                    S.playerHandler.postStep();
                    S.vehicleHandler.postStep();

                }

                accumulator -= Common.TIME_STEP;
            }
        }
    }


    public void render(float deltaTime) {

        synchronized (lock){
            S.worldHandler.debugRender();
        }



        if (serverMenu != null)
            serverMenu.render(deltaTime);


    }

    public void resize(int width, int height) {
        S.worldHandler.camera.viewportWidth = width * Common.pixelToUnits * Common.cameraScaleX;
        S.worldHandler.camera.viewportHeight = height * Common.pixelToUnits * Common.cameraScaleY;
        S.worldHandler.camera.update();
    }

}