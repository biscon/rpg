package dk.bison.rpg;

import android.app.Application;
import android.content.Context;

import dk.bison.rpg.core.armor.ArmorFactory;
import dk.bison.rpg.core.character.CharacterManager;
import dk.bison.rpg.core.combat.CombatCategoryManager;
import dk.bison.rpg.core.faction.FactionFactory;
import dk.bison.rpg.core.monster.MonsterFactory;
import dk.bison.rpg.core.weapon.WeaponFactory;
import dk.bison.rpg.net.server.DiscoveryThread;
import dk.bison.rpg.net.UDPClient;

/**
 * Created by joso on 12/05/16.
 */
public class App extends Application {
    static App appInstance;
    public boolean DEBUG;
    Thread discoveryThread;
    Thread udpThread;
    UDPClient udpClient;

    @Override
    protected void attachBaseContext(Context base) {
        //MultiDex.install(this);
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DEBUG = true;
        appInstance = this;
        FactionFactory.init();
        ArmorFactory.init();
        WeaponFactory.init();
        MonsterFactory.init();
        CharacterManager.instance().load(this);
        CombatCategoryManager.instance().load(this);
        udpClient = new UDPClient();
    }

    public static App instance()
    {
        return appInstance;
    }

    public void startDiscoveryThread()
    {
        discoveryThread = new Thread(DiscoveryThread.getInstance());
        discoveryThread.start();
    }

    public void findServer()
    {
        udpThread = new Thread(udpClient);
        udpThread.start();
    }

}
