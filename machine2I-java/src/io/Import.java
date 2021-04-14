package io;

// TO CHECK : import des classes Instance, Client, Depot et Point
import instance.Instance;
import instance.Machine;
import instance.Request;
import network.Client;
import network.Depot;
import network.Point;
import io.exception.FileExistException;
import io.exception.FormatFileException;
import io.exception.OpenFileException;
import io.exception.ReaderException;
import network.Tech;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Cette classe permet de lire une instance pour les TPs du cours de LE4-SI
 * POO pour l'optimisation.
 * 
 * Les instances sont fournies sur moodle au format ".vrp".
 * 
 * Pour que le lecteur d'instance fonctionne correctement, il faut que les 
 * signatures des constructeurs des classes Depot, Client, et Instances, ainsi 
 * que la methode ajouterClient de la classe Instance soient bien conformes a 
 * la description dans le sujet du TP.
 * Des commentaires annotes avec 'TO CHECK' vous permettent de facilement reperer
 * dans cette classe les lignes que vous devez verifier et modifier si besoin. 
 *
 */
public class Import {
    /**
     * Le fichier contenant l'instance.
     */
    private File instanceFile;
    
    /**
     * Constructeur par donnee du chemin du fichier d'instance.
     * @param inputPath le chemin du fichier d'instance, qui doit se terminer 
     * par l'extension du fichier (.xml).
     * @throws ReaderException lorsque le fichier n'est pas au bon format ou 
     * ne peut pas etre ouvert.
     */
    public Import(String inputPath) throws ReaderException {
        if (inputPath == null) {
            throw new OpenFileException();
        }
        if (!inputPath.endsWith(".txt")) {
            throw new FormatFileException("txt", "txt");
        }
        this.instanceFile = new File(inputPath);
    }
    
    /**
     * Methode principale pour lire le fichier d'instance.
     * @return l'instance lue
     * @throws ReaderException lorsque les donnees dans le fichier d'instance 
     * sont manquantes ou au mauvais format.
     */
    public Instance readInstance() throws ReaderException {
        try{
            FileReader f = new FileReader(this.instanceFile.getAbsolutePath());
            BufferedReader br = new BufferedReader(f);

            String dataset                 = readDataset(br);
            String name                    = readName(br);
            int days                       = readDays(br);
            int truckCapacity              = readTruckCapacity(br);
            int truckMaxDistance           = readTruckMaxDistance(br);
            int truckDistanceCost          = readTruckDistanceCost(br);
            int truckDayCost               = readTruckDayCost(br);
            int truckCost                  = readTruckCost(br);
            int techDistanceCost           = readTechDistanceCost(br);
            int techDayCost                = readTechDayCost(br);
            int techCost                   = readTechCost(br);
            List<Machine> machines         = readMachines(br);
            Map<Integer, Point> points     = readPoints(br);
            List<Client> clients           = readRequests(br, points);
            List<Tech> techs               = readTechs(br, points, machines);

            Point d = points.get(1);
            Instance instance = new Instance(
                    dataset,
                    name,
                    truckMaxDistance,
                    techCost,
                    new Depot(d.getId(), d.getX(), d.getY()),
                    days,
                    truckCapacity,
                    truckDistanceCost,
                    truckDayCost,
                    truckCost,
                    techDistanceCost,
                    techDayCost
            );

            for (Client c : clients)
                instance.ajouterClient(c);

            for (Tech t : techs)
                instance.ajouterTech(t);

            for (Machine m : machines)
                instance.ajouterMachine(m);

            br.close();
            f.close();

            return instance;

        } catch (FileNotFoundException ex) {
            throw new FileExistException(instanceFile.getName());
        } catch (IOException ex) {
            throw new ReaderException("IO exception", ex.getMessage());
        }
    }

    private String readDataset(BufferedReader br) throws IOException {
        String line = br.readLine();
        while(!line.contains("DATASET ="))
            line = br.readLine();
        return line.replace(" ", "").replace("DATASET=", "");
    }

    private String readName(BufferedReader br) throws IOException {
        String line = br.readLine();
        while(!line.contains("NAME ="))
            line = br.readLine();
        return line.replace(" ", "").replace("NAME=", "");
    }

    private int readDays(BufferedReader br) throws IOException {
        String line = br.readLine();
        while(!line.contains("DAYS ="))
            line = br.readLine();
        return Integer.parseInt(line.replace(" ", "").replace("DAYS=", ""));
    }

    private int readTruckCapacity(BufferedReader br) throws IOException {
        String line = br.readLine();
        while(!line.contains("TRUCK_CAPACITY ="))
            line = br.readLine();
        return Integer.parseInt(line.replace(" ", "").replace("TRUCK_CAPACITY=", ""));
    }

    private int readTruckMaxDistance(BufferedReader br) throws IOException {
        String line = br.readLine();
        while(!line.contains("TRUCK_MAX_DISTANCE ="))
            line = br.readLine();
        return Integer.parseInt(line.replace(" ", "").replace("TRUCK_MAX_DISTANCE=", ""));
    }

    private int readTruckDistanceCost(BufferedReader br) throws IOException {
        String line = br.readLine();
        while(!line.contains("TRUCK_DISTANCE_COST ="))
            line = br.readLine();
        return Integer.parseInt(line.replace(" ", "").replace("TRUCK_DISTANCE_COST=", ""));
    }

    private int readTruckDayCost(BufferedReader br) throws IOException {
        String line = br.readLine();
        while(!line.contains("TRUCK_DAY_COST ="))
            line = br.readLine();
        return Integer.parseInt(line.replace(" ", "").replace("TRUCK_DAY_COST=", ""));
    }

    private int readTruckCost(BufferedReader br) throws IOException {
        String line = br.readLine();
        while(!line.contains("TRUCK_COST ="))
            line = br.readLine();
        return Integer.parseInt(line.replace(" ", "").replace("TRUCK_COST=", ""));
    }

    private int readTechDistanceCost(BufferedReader br) throws IOException {
        String line = br.readLine();
        while(!line.contains("TECHNICIAN_DISTANCE_COST ="))
            line = br.readLine();
        return Integer.parseInt(line.replace(" ", "").replace("TECHNICIAN_DISTANCE_COST=", ""));
    }

    private int readTechDayCost(BufferedReader br) throws IOException {
        String line = br.readLine();
        while(!line.contains("TECHNICIAN_DAY_COST ="))
            line = br.readLine();
        return Integer.parseInt(line.replace(" ", "").replace("TECHNICIAN_DAY_COST=", ""));
    }

    private int readTechCost(BufferedReader br) throws IOException {
        String line = br.readLine();
        while(!line.contains("TECHNICIAN_COST ="))
            line = br.readLine();
        return Integer.parseInt(line.replace(" ", "").replace("TECHNICIAN_COST=", ""));
    }

    private List<Machine> readMachines(BufferedReader br) throws IOException {
        List<Machine> machines = new ArrayList<>();
        String line = br.readLine();
        while(!line.contains("MACHINES =")) {
            Machine m = readMachine(line);
            if (m != null)
                machines.add(m);
            line = br.readLine();
        }
        return machines;
    }

    private Map<Integer,Point> readPoints(BufferedReader br) throws IOException {
        Map<Integer, Point> points = new LinkedHashMap<>();
        String ligne = br.readLine();
        while(!ligne.contains("LOCATIONS =")) {
            Point p = readPoint(ligne);
            if (p != null)
                points.put(p.getId(), p);
            ligne = br.readLine();
        }
        return points;
    }

    private List<Client> readRequests(BufferedReader br, Map<Integer, Point> points) throws IOException {
        List<Client> clients = new ArrayList<>();
        String line = br.readLine();
        while(!line.contains("REQUESTS =")) {
            Client c = readClient(line, points);
            if (c != null)
                clients.add(c);
            line = br.readLine();
        }
        return clients;
    }

    private List<Tech> readTechs(BufferedReader br, Map<Integer, Point> points, List<Machine> machines) throws IOException {
        List<Tech> techs = new ArrayList<>();
        String line = br.readLine();
        while(!line.contains("REQUESTS =")) {
            Tech t = readTech(line, points, machines);
            if (t != null)
                techs.add(t);
            line = br.readLine();
        }
        return techs;
    }

    private Machine readMachine(String line) throws IOException {
        if (line.isEmpty() || line.isBlank()) return null;

        String[] val = line.strip().split("\\s+|\t+");

        if (val.length != 3) {
            System.out.println("Something went wrong, a machine data has been dropped");
            return null;
        }
        System.out.printf("WHOOP WHOOP I READ MACHINES");

        int id      = Integer.parseInt(val[0]);
        int size    = Integer.parseInt(val[1]);
        int penalty = Integer.parseInt(val[2]);

        return new Machine(id, size, penalty);
    }

    private Point readPoint(String line) throws IOException {
        if (line.isEmpty() || line.isBlank()) return null;

        String[] val = line.strip().split("\\s+|\t+");

        if (val.length != 3) {
            System.out.println("Something went wrong, a Point data has been dropped");
            return null;
        }

        int id = Integer.parseInt(val[0]);
        int x  = Integer.parseInt(val[1]);
        int y  = Integer.parseInt(val[2]);

        return new Client(null, id, x, y);
    }

    private Client readClient(String line, Map<Integer, Point> points) throws IOException {
        if (line.isEmpty() || line.isBlank()) return null;

        String[] val = line.strip().split("\\s+|\t+");

        if (val.length != 6) {
            System.out.println("Something went wrong, a Client data has been dropped");
            return null;
        }

        int id        = Integer.parseInt(val[0]);
        int idClient  = Integer.parseInt(val[1]);
        int dayOne    = Integer.parseInt(val[2]);
        int lastDay   = Integer.parseInt(val[3]);
        int idMachine = Integer.parseInt(val[4]);
        int nbMachine = Integer.parseInt(val[5]);

        Point p   = points.get(idClient);
        Request r = new Request(id, idClient, dayOne, lastDay, idMachine, nbMachine);

        return new Client(r, p.getId(), p.getX(), p.getY());
    }

    private Tech readTech(String line, Map<Integer, Point> points, List<Machine> machines) throws IOException {
        if (line.isEmpty() || line.isBlank()) return null;

        String[] val = line.strip().split("\\s+|\t+");

        if (val.length <= 4 + machines.size()) {
            System.out.println("Something went wrong, a Technician data has been dropped");
            return null;
        }

        System.out.println(machines.size());
        System.out.println(line);

        int id = Integer.parseInt(val[0]);
        int idPoint = Integer.parseInt(val[1]);
        int maxDist = Integer.parseInt(val[2]);
        int maxReq = Integer.parseInt(val[3]);

        List<Integer> abilities = new ArrayList<>();
        for (int i = 4; i < val.length; i++)
            abilities.add(Integer.parseInt(val[i]));

        Point p = points.get(idPoint);

        return new Tech(p.getId(), p.getX(), p.getY(), id, maxDist, maxReq, abilities);
    }

    public static void main(String[] args) {
        try {
            Import reader = new Import("instances/ORTEC-early/VSC2019_ORTEC_early_01.txt");
            reader.readInstance();
            System.out.println("Instance lue avec success !");
        } catch (ReaderException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
