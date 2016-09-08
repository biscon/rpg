package dk.bison.rpg.core.armor;

/**
 * Created by bison on 16-08-2016.
 */
public class ArmorTemplate {
    public static final int CHEST = 0;
    public static final int SHIELD = 1;

    String name;
    int cost;
    int weight;
    int AC;
    int type;

    public ArmorTemplate(String name, int cost, int weight, int AC, int type) {
        this.name = name;
        this.cost = cost;
        this.weight = weight;
        this.AC = AC;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getTypeAsString()
    {
        switch(type)
        {
            case CHEST:
                return "Chest";
            case SHIELD:
                return "Shield";
        }
        return "Unknown";
    }

    public int getCost() {
        return cost;
    }

    public int getWeight() {
        return weight;
    }

    public int getAC() {
        return AC;
    }

    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        return "ArmorTemplate{" +
                "name='" + name + '\'' +
                ", cost=" + cost +
                ", weight=" + weight +
                ", AC=" + AC +
                ", type=" + type +
                '}';
    }
}
