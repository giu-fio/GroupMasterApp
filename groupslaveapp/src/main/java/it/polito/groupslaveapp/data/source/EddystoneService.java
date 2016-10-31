package it.polito.groupslaveapp.data.source;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.greenrobot.eventbus.EventBus;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import it.polito.groupslaveapp.event.BeaconEvents;


public class EddystoneService extends Service implements BeaconConsumer, RangeNotifier {

    private static final String TAG = "EddystoneService";

    // Eddystone frame types
    public static final byte TYPE_UID = 0x00;
    public static final byte TYPE_TLM = 0x20;

    private static int COUNT = 0;

    private BeaconManager mBeaconManager;
    private boolean started;
    private Region mRegion;
    private List<Beacon> lastBeacons;

    public EddystoneService() {
    }


    public static void startScanAction(Context context) {
        Intent intent = new Intent(context, EddystoneService.class);
        context.startService(intent);
        COUNT++;
        Log.d(TAG, "startScanAction: " + COUNT);
    }

    public static void stopScanAction(Context context) {
        Log.d(TAG, "stopScanAction: " + COUNT);
        COUNT--;
        if (COUNT == 0) {
            Intent intent = new Intent(context, EddystoneService.class);
            context.stopService(intent);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");
        super.onCreate();
        lastBeacons = new LinkedList<>();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        if (started) try {
            mBeaconManager.stopRangingBeaconsInRegion(mRegion);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mBeaconManager.unbind(this);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");

        if (!started) {
            mBeaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());

            // Detect UID frame:
            mBeaconManager.getBeaconParsers().add(new BeaconParser().
                    setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
            // Detect TLM frame:
            mBeaconManager.getBeaconParsers().add(new BeaconParser().
                    setBeaconLayout("x,s:0-1=feaa,m:2-2=20,d:3-3,d:4-5,d:6-7,d:8-11,d:12-15"));

            mBeaconManager.setBackgroundMode(false);
            // mBeaconManager.setForegroundScanPeriod(0L);
            mBeaconManager.setForegroundBetweenScanPeriod(0L);
            mBeaconManager.bind(this);

        }
        return START_STICKY;
    }

    @Override
    public void onBeaconServiceConnect() {
        Log.d(TAG, "onBeaconServiceConnect: ");

        started = true;
        mRegion = new Region("all-beacons-region", null, null, null);
        try {
            mBeaconManager.startRangingBeaconsInRegion(mRegion);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mBeaconManager.addRangeNotifier(this);
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        Log.d(TAG, "didRangeBeaconsInRegion: " + beacons.size());

        List<Beacon> lostBeacons = new LinkedList<>(this.lastBeacons);
        this.lastBeacons.clear();
        List<Beacon> newBeacons = new LinkedList<>();

        for (Beacon beacon : beacons) {
            if (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == TYPE_UID) {
                if (!lostBeacons.contains(beacon)) {
                    newBeacons.add(beacon);
                } else {
                    lostBeacons.remove(beacon);
                }
                lastBeacons.add(beacon);
            }
        }

        EventBus eventBus = EventBus.getDefault();
        eventBus.post(new BeaconEvents.VisibleBeaconsEvent(new LinkedList<>(beacons)));

        if (!newBeacons.isEmpty()) {
            EventBus.getDefault().post(new BeaconEvents.NewBeaconsFoundEvent(newBeacons));
        }

        if (!lostBeacons.isEmpty()) {
            EventBus.getDefault().post(new BeaconEvents.BeaconsLostEvent(newBeacons));
        }
    }

}