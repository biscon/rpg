package dk.bison.rpg.core.faction;

/**
 * Created by bison on 18-08-2016.
 */
public class FactionTemplate {
    String name;
    boolean playerFaction;

    public FactionTemplate(String name, boolean playerFaction) {
        this.name = name;
        this.playerFaction = playerFaction;
    }
}
