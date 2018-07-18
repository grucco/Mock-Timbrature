package it.reply.timbrature.timbrature.SupportClasses;

import dagger.Module;
import dagger.Provides;
import it.reply.timbrature.timbrature.Network.TimbratureNetworkModel;
import it.reply.timbrature.timbrature.model.TimbratureService;
import it.reply.timbrature.timbrature.view.LoginView;
import it.reply.timbrature.timbrature.view.MainView;
import it.reply.timbrature.timbrature.view.TimbratureView;
import retrofit2.Retrofit;

/**
 * Created by g.rucco on 24/01/2018.
 */

@Module
public class TimbratureModule {

    private LoginView loginView;
    private MainView mainView;
    private TimbratureView timbratureView;

    public TimbratureModule(LoginView lview) {
        this.loginView = lview;
        this.mainView = null;
    }

    public TimbratureModule(MainView mview) {
        this.mainView = mview;
        this.loginView = null;
    }

    public TimbratureModule(TimbratureView tview) {
        this.loginView = null;
        this.mainView = null;
        this.timbratureView = tview;
    }

    public TimbratureModule(LoginView l, MainView m, TimbratureView t) {
        this.mainView = m;
        this.loginView = l;
        this.timbratureView = t;
    }

    @Provides
    public LoginView provideLoginView(){
        return loginView;
    }

    @Provides
    public MainView provideMainView(){
        return mainView;
    }

    @Provides
    public TimbratureView provideTimbratureView(){
        return timbratureView;
    }

    @Provides
    public static TimbratureService provideTimbratureService(Retrofit retrofit) {
        return retrofit.create(TimbratureService.class);
    }

    @Provides
    public static TimbratureNetworkModel provideTimbratureNetworkService(TimbratureService timbratureService) {
        return new TimbratureNetworkModel(timbratureService);
    }

}
