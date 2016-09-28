package dk.bison.rpg.core.weapon;

/**
 * Created by bison on 14-08-2016.
 */
public class WeaponTemplate {
    public static final int SIZE_S = 0;
    public static final int SIZE_M = 1;
    public static final int SIZE_L = 2;

    String name;
    int cost;
    int size;
    int weight;
    String damage;
    int range;
    String category;

    public WeaponTemplate(String name, int cost, int size, int weight, String damage, int range, String category) {
        this.name = name;
        this.cost = cost;
        this.size = size;
        this.weight = weight;
        this.damage = damage;
        this.range = range;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public int getSize() {
        return size;
    }

    public int getWeight() {
        return weight;
    }

    public String getDamage() {
        return damage;
    }

    public int getRange() {
        return range;
    }

    public String getCategory() {
        return category;
    }

    public String getSizeAsString()
    {
        switch(size)
        {
            case SIZE_L:
                return "Large";
            case SIZE_M:
                return "Medium";
            case SIZE_S:
                return "Small";
        }
        return "Unknown";
    }
}
