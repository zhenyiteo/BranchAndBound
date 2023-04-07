import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

class City {
    int index;
    double x, y;

    City(int index, double x, double y) {
        this.index = index;
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return String.valueOf(index + 1);
    }

    double distanceTo(City city) {
        return Math.sqrt(Math.pow(city.x - this.x, 2) + Math.pow(city.y - this.y, 2));
    }
}

class Node implements Comparable<Node> {
    City[] path;
    double lowerBound;
    City lastCity;

    Node(City[] path, double lowerBound, City lastCity) {
        this.path = path;
        this.lowerBound = lowerBound;
        this.lastCity = lastCity;
    }

    public int compareTo(Node other) {
        return Double.compare(this.lowerBound, other.lowerBound);
    }
}

public class TSPBranchAndBound {

    static double[][] distanceMatrix;
    static int numberOfCities;

    public static void main(String[] args) {
        City[] cities = loadCities();
        numberOfCities = cities.length;
        distanceMatrix = new double[numberOfCities][numberOfCities];

        for (int i = 0; i < numberOfCities; i++) {
            for (int j = 0; j < numberOfCities; j++) {
                distanceMatrix[i][j] = cities[i].distanceTo(cities[j]);
            }
        }

        City[] bestPath = tsp(cities);
        double bestPathLength = pathLength(bestPath);
        System.out.println("Best path: " + Arrays.toString(bestPath));
        System.out.println("Path length: " + bestPathLength);
    }

    public static City[] tsp(City[] cities) {
        PriorityQueue<Node> pq = new PriorityQueue<>();
        City[] initialPath = new City[]{cities[0]};
        double initialLowerBound = lowerBound(cities, initialPath);
        pq.add(new Node(initialPath, initialLowerBound, cities[0]));

        double minLength = Double.MAX_VALUE;
        City[] bestPath = null;

        while (!pq.isEmpty()) {
            Node currentNode = pq.poll();

            if (currentNode.path.length == numberOfCities) {
                double currentLength = pathLength(currentNode.path);
                if (currentLength < minLength) {
                    minLength = currentLength;
                    bestPath = currentNode.path;
                }
            } else {
                for (City city : cities) {
                    if (!containsCity(currentNode.path, city)) {
                        City[] newPath = Arrays.copyOf(currentNode.path, currentNode.path.length + 1);
                        newPath[newPath.length - 1] = city;
                        double newLowerBound = currentNode.lowerBound + distanceMatrix[currentNode.lastCity.index][city.index];
                        pq.add(new Node(newPath, newLowerBound, city));
                    }
                }
            }
        }

        return bestPath;
    }

    private static double pathLength(City[] path) {
        double length = 0;
        for (int i = 0; i < path.length - 1; i++) {
            length += distanceMatrix[path[i].index][path[i + 1].index];
        }
        length += distanceMatrix[path[path.length - 1].index][path[0].index];
        return length;
    }

    private static double lowerBound(City[] cities, City[] path) {
        double bound = 0;
        for (int i = 0; i < path.length - 1; i++) {
            bound += distanceMatrix[path[i].index][path[i + 1].index];
        }
        if (path.length < numberOfCities) {
            City lastCityInPath = path[path.length - 1];
            double minOutgoingEdge = Double.MAX_VALUE;
            for (City city : cities) {
                if (!containsCity(path, city)) {
                    double edgeDistance = distanceMatrix[lastCityInPath.index][city.index];
                    if (edgeDistance < minOutgoingEdge) {
                        minOutgoingEdge = edgeDistance;
                    }
                }
            }
            bound += minOutgoingEdge;
        }
        return bound;
    }

    private static boolean containsCity(City[] path, City city) {
        for (City c : path) {
            if (c.index == city.index) {
                return true;
            }
        }
        return false;
    }

    private static City[] loadCities() {
        List<City> cities = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(Paths.get("C:/Users/zheny/Desktop/AIM Coursework/TSP_107.txt"));
            for (String line : lines) {
                String[] parts = line.trim().split("\\s+"); // Assumes city index, x, and y coordinates are separated by whitespace
                int index = Integer.parseInt(parts[0]) - 1; // Subtract 1 from the index
                double x = Double.parseDouble(parts[1]);
                double y = Double.parseDouble(parts[2]);
                cities.add(new City(index, x, y));
            }
        } catch (IOException e) {
            System.err.println("Error reading city coordinates from file: " + e.getMessage());
        }

        return cities.toArray(new City[cities.size()]);
    }
    
    
}