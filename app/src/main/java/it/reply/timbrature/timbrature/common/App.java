package it.reply.timbrature.timbrature.common;

import android.app.Application;


/**
 * Created by vinayaprasadn on 19/4/17.
 */

public class App extends Application {

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder()
                .androidModule(new AndroidModule(this))
                .appModule(new AppModule())
                .build();
    }

    public AppComponent getAppComponent(){
        return appComponent;
    }

}
