package be.vdab.project.meetingroomreservations.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Yannick on 21/02/14.
 */
public class RefreshReceiver extends BroadcastReceiver {

    public RefreshReceiver() {
        super();
    }

    @Override
    public void onReceive( Context context, Intent intent ) {

        Intent myIntent = new Intent( context, DataRefreshService.class );
        context.startService( myIntent );
    }
}
