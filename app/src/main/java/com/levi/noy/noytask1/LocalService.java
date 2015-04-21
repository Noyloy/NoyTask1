package com.levi.noy.noytask1;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import java.util.Random;

public class LocalService extends Service {
    private int boardSize;
    private int point;
    private final Random myGenerator = new Random();

    //Binder given to clients
    private final IBinder myBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        LocalService getService(){
            return LocalService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent){
        Bundle b = intent.getBundleExtra(RuntimeMatrix.BOARD_SIZE_BUNDLE_KEY);
        this.boardSize = b.getInt(RuntimeMatrix.BOARD_SIZE,25);
        return myBinder;
    }

    public interface ISimulatedPlayerListener{
        public void didDecide(int place);
    }

    public int getRandomNumber(){
        return myGenerator.nextInt(boardSize);
    }

    public void makeMove(final ISimulatedPlayerListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 1. Think -> wait
                try {
                    synchronized (this){
                        wait(3000);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 2. Choose a point
                point = getRandomNumber();
                // 3. Send event to listener
                listener.didDecide(point);
            }
        }).start();
    }
}
