package dk.bison.rpg.core.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bison on 22-08-2015.
 * A-Star implementation, crappy and slow
 */
public class PathFinder {
    PathFinderMap map;
    List<Node> openList = new ArrayList<>();
    List<Node> closedList = new ArrayList<>();
    double normalCost = 10;
    double diagonalCost = 14;

    Coord[] dirVectors = {new Coord(0,-1),new Coord(1,-1),new Coord(1,0),
            new Coord(1,1),new Coord(0,1),new Coord(-1,1),
            new Coord(-1,0),new Coord(-1,-1)};

    public PathFinder(PathFinderMap map) {
        this.map = map;
    }

    private boolean isDirIndexDiagonal(int dir_index)
    {
        if(dir_index == CompassDirections.NE && dir_index == CompassDirections.NW
                && dir_index == CompassDirections.SE && dir_index == CompassDirections.SW)
            return true;
        else
            return false;
    }

    private Node findLowestFCost()
    {
        if(openList.isEmpty())
            return null;
        Node lowest = openList.get(0);
        for(Node n : openList)
        {
            if(n.F_cost < lowest.F_cost)
                lowest = n;
        }
        return lowest;
    }

    private Node findInClosedList(Coord pos)
    {
        for(Node n : closedList)
        {
            if(n.position.x == pos.x && n.position.y == pos.y)
                return n;
        }
        return null;
    }

    private Node findInOpenList(Coord pos)
    {
        for(Node n : openList)
        {
            if(n.position.x == pos.x && n.position.y == pos.y)
                return n;
        }
        return null;
    }

    private Node buildNode(Node selected_node, Coord dirPos, double level_scale, double val, int index, Coord dst)
    {
        Node new_node = new Node();
        new_node.position = dirPos;
        new_node.value = val;
        new_node.parent = selected_node;
        double level_cost = level_scale * Math.abs(new_node.parent.value - new_node.value);
        //Log.e("part", "level_cost = " + level_cost);
        if (isDirIndexDiagonal(index))
            new_node.G_cost += diagonalCost + new_node.parent.G_cost + level_cost;
        else
            new_node.G_cost += normalCost + new_node.parent.G_cost + level_cost;
        new_node.H_cost = distanceEuclidian(new_node.position, dst);
        //Log.e("PathFinder", "New node H_COST = " + new_node.H_cost);
        new_node.F_cost = new_node.G_cost + new_node.H_cost;
        return new_node;
    }

    private void calcGCost(Node pot_node, double level_scale, int index)
    {
        double level_cost = level_scale * Math.abs(pot_node.parent.value - pot_node.value);
        if (isDirIndexDiagonal(index))
            pot_node.G_cost += diagonalCost + pot_node.parent.G_cost + level_cost;
        else
            pot_node.G_cost += normalCost + pot_node.parent.G_cost + level_cost;
    }

    public ArrayList<Coord> findPath(Coord src, Coord dst) {
        Log.e("PathFinder", "Attempting to find path between " + src + " and " + dst);
        openList.clear();
        closedList.clear();
        double level_cost;
        double level_scale = 2000;
        Node start_node = new Node();
        start_node.position = new Coord(src.x, src.y);
        start_node.H_cost = distanceEuclidian(start_node.position, dst);
        start_node.F_cost = start_node.H_cost;

        int index = 0;
        boolean done = false;
        Node selected_node = start_node;
        while (!done) {
            closedList.add(selected_node);
            if(selected_node.position.x == dst.x && selected_node.position.y == dst.y)
            {
                done = true;
                Log.e("PathFinder", "path found");
                break;
            }
            for (Coord vector : dirVectors) {
                Coord dirPos = new Coord(selected_node.position.x + vector.x, selected_node.position.y + vector.y);
                boolean passable = map.sample(dirPos.x, dirPos.y);
                double cost = map.cost(dirPos.x, dirPos.y);
                // can we go there?
                if (passable) {
                    // not in closed list
                    if (findInClosedList(dirPos) == null) {
                        Node pot_node = findInOpenList(dirPos);
                        if (pot_node == null) {
                            Node new_node = buildNode(selected_node, dirPos, level_scale, cost, index, dst);
                            openList.add(new_node);
                        } else //w as already in the open list
                        {
                            double pot_g_cost = 0;
                            double pot_f_cost = 0;
                            double pot_h_cost = 0;

                            level_cost = level_scale * Math.abs(selected_node.value - pot_node.value);
                            if (isDirIndexDiagonal(index))
                                pot_g_cost = diagonalCost + selected_node.G_cost + level_cost;
                            else
                                pot_g_cost = normalCost + selected_node.G_cost + level_cost;
                            pot_h_cost = distanceEuclidian(pot_node.position, dst);
                            pot_f_cost = pot_g_cost + pot_h_cost;
                            //Log.e("PathFinder", "G=" + pot_g_cost + ", H=" + pot_h_cost + ", F=" + pot_f_cost + ", level_cost=" + level_cost);
                            // would this result in a potentially shorter route?
                            if (pot_f_cost < pot_node.F_cost) {
                                // set selected node as parent and recalculate costs
                                pot_node.parent = selected_node;
                                calcGCost(pot_node, level_scale, index);
                                pot_node.F_cost = pot_node.G_cost + pot_node.H_cost;
                            }
                        }
                    }
                }
                index++;
            }
            selected_node = findLowestFCost();
            if(selected_node == null)
            {
                Log.e("PathFinder", "openList is empty, no path found (closedList = " + closedList.size() + ")");
                done = true;
                break;
            }
            openList.remove(selected_node);
            //Log.e("PathFinder", "openlist " + openList.size() + "  closedList "+ closedList.size());
        }
        if(selected_node == null)
            return null;
        Node cur_node = selected_node;
        ArrayList<Coord> path = new ArrayList<>();
        while(cur_node != start_node)
        {
            if(cur_node == null)
                break;
            if(cur_node.position != null) {
                path.add(cur_node.position);
            }
            cur_node = cur_node.parent;
        }
        path.add(start_node.position);
        //Log.e("PathFinder", "done me hearties pathsize " + path.size());
        return path;
    }

    // manhattan (fast) distance
    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    // euclidian distance
    public static double distanceEuclidian(Coord first, Coord second) {
        double deltaX = (double) first.y - (double) second.y;
        double deltaY = (double) first.x - (double) second.x;
        double result = Math.sqrt(deltaX*deltaX + deltaY*deltaY);
        return result;
    }
}

class Node
{
    public Coord position;
    public Node parent = null;
    public double value;
    public double G_cost = 0;
    public double H_cost = 0;
    public double F_cost = 0;

    @Override
    public String toString() {
        return "Node{" +
                "position=" + position +
                ", parent=" + parent +
                ", value=" + value +
                ", G_cost=" + G_cost +
                ", H_cost=" + H_cost +
                ", F_cost=" + F_cost +
                '}';
    }
}
