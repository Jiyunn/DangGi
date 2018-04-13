package me.jy.danggi;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import io.realm.Realm;

/**
 * application class
 * Created by JY on 2018-01-19.
 */

public class MyApplication extends Application{

    protected String userAgent;

    public void onCreate() {
        super.onCreate();
         Realm.init(this);

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());

        userAgent = Util.getUserAgent(this , "ExoPlayerDemo");
    }

    /**
     * Returns a {@link DataSource.Factory}.
     */
    public DataSource.Factory buildDataSourceFactory( TransferListener<? super DataSource> listener ) {
        return new DefaultDataSourceFactory(this , listener , buildHttpDataSourceFactory(listener));
    }

    /**
     * Returns a {@link HttpDataSource.Factory}.
     */
    public HttpDataSource.Factory buildHttpDataSourceFactory(
            TransferListener<? super DataSource> listener ) {
        return new DefaultHttpDataSourceFactory(userAgent , listener);
    }

}
