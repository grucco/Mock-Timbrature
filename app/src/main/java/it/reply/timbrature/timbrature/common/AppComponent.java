package it.reply.timbrature.timbrature.common;

import javax.inject.Singleton;

import dagger.Component;
import it.reply.timbrature.timbrature.SupportClasses.TimbratureModule;
import it.reply.timbrature.timbrature.SupportClasses.TimbratureSubComponent;

/**
 * Created by vinayaprasadn on 19/4/17.
 */
@Singleton
@Component(modules = {AndroidModule.class,AppModule.class})
public interface AppComponent {
    TimbratureSubComponent plus(TimbratureModule timbratureModule);
}
