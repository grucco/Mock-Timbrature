package it.reply.timbrature.timbrature.SupportClasses;

import dagger.Subcomponent;
import it.reply.timbrature.timbrature.LoginActivity;
import it.reply.timbrature.timbrature.MainActivity;
import it.reply.timbrature.timbrature.TimbraturaActivity;

/**
 * Created by g.rucco on 24/01/2018.
 */

@Subcomponent(modules = TimbratureModule.class)
public interface TimbratureSubComponent {

    void inject(LoginActivity loginActivity);
    void inject(MainActivity mainActivity);
    void inject(TimbraturaActivity timbraturaActivity);
}
