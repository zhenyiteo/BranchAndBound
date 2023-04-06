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
        return String.valueOf(index + 1); // Add 1 to the index to match the original input
    }

    double distanceTo(City city) {
        return Math.sqrt(Math.pow(city.x - this.x, 2) + Math.pow(city.y - this.y, 2));
    }
}

class Node implements Comparable<Node> {
    List<City> path;
    double lowerBound;
    City lastCity;

    Node(List<City> path, double lowerBound, City lastCity) {
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
        List<City> cities = loadCities(); // Load the city coordinates here
        numberOfCities = cities.size();
        distanceMatrix = new double[numberOfCities][numberOfCities];

        for (int i = 0; i < numberOfCities; i++) {
            for (int j = 0; j < numberOfCities; j++) {
                distanceMatrix[i][j] = cities.get(i).distanceTo(cities.get(j));
            }
        }

        List<City> bestPath = tsp(cities);
        double bestPathLength = pathLength(bestPath);
        System.out.println("Best path: " + bestPath);
        System.out.println("Path length: " + bestPathLength);
    }

    public static List<City> tsp(List<City> cities) {
        PriorityQueue<Node> pq = new PriorityQueue<>();
        List<City> initialPath = new ArrayList<>();
        initialPath.add(cities.get(0));
        double initialLowerBound = lowerBound(cities, initialPath);
        pq.add(new Node(initialPath, initialLowerBound, cities.get(0)));

        double minLength = Double.MAX_VALUE;
        List<City> bestPath = null;

        while (!pq.isEmpty()) {
            Node currentNode = pq.poll();

            if (currentNode.path.size() == numberOfCities) {
                double currentLength = pathLength(currentNode.path);
                if (currentLength < minLength) {
                    minLength = currentLength;
                    bestPath = currentNode.path;
                }
            } else {
                for (City city : cities) {
                    if (!currentNode.path.contains(city)) {
                        List<City> newPath = new ArrayList<>(currentNode.path);
                        newPath.add(city);
                        double newLowerBound = currentNode.lowerBound + distanceMatrix[currentNode.lastCity.index][city.index];
                        pq.add(new Node(newPath, newLowerBound, city));
                    }
                }
            }
        }

        return bestPath;
    }

    private static double pathLength(List<City> path) {
        double length = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            length += distanceMatrix[path.get(i).index][path.get(i + 1).index];
        }
        length += distanceMatrix[path.get(path.size() - 1).index][path.get(0).index];
        return length;
    }
    
    private static double lowerBound(List<City> cities, List<City> path) {
        double bound = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            bound += distanceMatrix[path.get(i).index][path.get(i + 1).index];
        }
        if (path.size() < numberOfCities) {
            City lastCityInPath = path.get(path.size() - 1);
            double minOutgoingEdge = Double.MAX_VALUE;
            for (City city : cities) {
                if (!path.contains(city)) {
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

    private static List<City> loadCities() {
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

        return cities;
    }
}