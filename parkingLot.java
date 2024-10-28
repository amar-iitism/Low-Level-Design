import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Enum for Vehicle Types
public enum VehicleType {
    CAR,
    TRUCK,
    MOTORCYCLE,
    // You can add more vehicle types as needed here
}

// Abstract Vehicle Class
public abstract class Vehicle {
    protected String licensePlate;
    protected VehicleType type;

    public Vehicle(String licensePlate, VehicleType type) {
        this.licensePlate = licensePlate;
        this.type = type;
    }

    public VehicleType getType() {
        return type;
    }

    public String getLicensePlate() {
        return licensePlate;
    }
}

// Concrete Vehicle Subclasses
public class Car extends Vehicle {
    public Car(String licensePlate) {
        super(licensePlate, VehicleType.CAR);
    }
}

public class Truck extends Vehicle {
    public Truck(String licensePlate) {
        super(licensePlate, VehicleType.TRUCK);
    }
}

public class Motorcycle extends Vehicle {
    public Motorcycle(String licensePlate) {
        super(licensePlate, VehicleType.MOTORCYCLE);
    }
}

// ParkingSpot Class
public class ParkingSpot {
    private final int spotNumber;
    private final VehicleType vehicleType;
    private Vehicle parkedVehicle;

    public ParkingSpot(int spotNumber, VehicleType vehicleType) {
        this.spotNumber = spotNumber;
        this.vehicleType = vehicleType;
    }

    public synchronized boolean isAvailable() {
        return parkedVehicle == null;
    }

    public synchronized void parkVehicle(Vehicle vehicle) {
        if (isAvailable() && vehicle.getType() == vehicleType) {
            parkedVehicle = vehicle;
        } else {
            throw new IllegalArgumentException("Invalid vehicle type or spot already occupied.");
        }
    }

    public synchronized void unparkVehicle() {
        parkedVehicle = null;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public Vehicle getParkedVehicle() {
        return parkedVehicle;
    }

    public int getSpotNumber() {
        return spotNumber;
    }
}

// Level Class
public class Level {
    private final int floor;
    private final List<ParkingSpot> parkingSpots;
    private final Map<VehicleType, Integer> vehicleTypeCapacity;

    public Level(int floor, Map<VehicleType, Integer> vehicleTypeCapacity) {
        this.floor = floor;
        this.vehicleTypeCapacity = new HashMap<>(vehicleTypeCapacity); // Copy to avoid modification
        this.parkingSpots = new ArrayList<>();
        initializeParkingSpots();
    }

    private void initializeParkingSpots() {
        int spotNumber = 1;
        for (Map.Entry<VehicleType, Integer> entry : vehicleTypeCapacity.entrySet()) {
            VehicleType type = entry.getKey();
            int count = entry.getValue();

            for (int i = 0; i < count; i++) {
                parkingSpots.add(new ParkingSpot(spotNumber++, type));
            }
        }
    }

    public synchronized boolean parkVehicle(Vehicle vehicle) {
        for (ParkingSpot spot : parkingSpots) {
            if (spot.isAvailable() && spot.getVehicleType() == vehicle.getType()) {
                spot.parkVehicle(vehicle);
                return true;
            }
        }
        return false;
    }

    public synchronized boolean unparkVehicle(Vehicle vehicle) {
        for (ParkingSpot spot : parkingSpots) {
            if (spot.getParkedVehicle() != null && spot.getParkedVehicle().getLicensePlate().equals(vehicle.getLicensePlate())) {
                spot.unparkVehicle();
                return true;
            }
        }
        return false;
    }

    public void displayAvailability() {
        System.out.println("Level " + floor + " Availability:");
        for (ParkingSpot spot : parkingSpots) {
            String status = spot.isAvailable() ? "Available for " : "Occupied by ";
            System.out.println("Spot " + spot.getSpotNumber() + ": " + status + spot.getVehicleType());
        }
    }

    public int getTotalCapacity() {
        return parkingSpots.size();
    }

    public int getCapacityForVehicleType(VehicleType type) {
        return vehicleTypeCapacity.getOrDefault(type, 0);
    }
}

// Singleton ParkingLot Class
public class ParkingLot {
    private static ParkingLot instance;
    private final List<Level> levels;

    private ParkingLot() {
        levels = new ArrayList<>();
    }

    public static synchronized ParkingLot getInstance() {
        if (instance == null) {
            instance = new ParkingLot();
        }
        return instance;
    }

    public void addLevel(Level level) {
        levels.add(level);
    }

    public boolean parkVehicle(Vehicle vehicle) {
        for (Level level : levels) {
            if (level.parkVehicle(vehicle)) {
                System.out.println("Vehicle " + vehicle.getLicensePlate() + " parked successfully.");
                return true;
            }
        }
        System.out.println("Could not park vehicle " + vehicle.getLicensePlate());
        return false;
    }

    public boolean unparkVehicle(Vehicle vehicle) {
        for (Level level : levels) {
            if (level.unparkVehicle(vehicle)) {
                System.out.println("Vehicle " + vehicle.getLicensePlate() + " unparked successfully.");
                return true;
            }
        }
        System.out.println("Vehicle " + vehicle.getLicensePlate() + " is not found.");
        return false;
    }

    public void displayAvailability() {
        for (Level level : levels) {
            level.displayAvailability();
        }
    }
}

// Demo Class
public class ParkingLotDemo {
    public static void main(String[] args) {
        ParkingLot parkingLot = ParkingLot.getInstance();

        // Define vehicle type capacities for each level
        Map<VehicleType, Integer> level1Capacity = new HashMap<>();
        level1Capacity.put(VehicleType.CAR, 40);
        level1Capacity.put(VehicleType.TRUCK, 30);
        level1Capacity.put(VehicleType.MOTORCYCLE, 30);

        Map<VehicleType, Integer> level2Capacity = new HashMap<>();
        level2Capacity.put(VehicleType.CAR, 50);
        level2Capacity.put(VehicleType.TRUCK, 20);
        level2Capacity.put(VehicleType.MOTORCYCLE, 10);

        // Add levels to the parking lot with defined capacities
        parkingLot.addLevel(new Level(1, level1Capacity));
        parkingLot.addLevel(new Level(2, level2Capacity));

        // Create vehicles
        Vehicle car = new Car("ABC123");
        Vehicle truck = new Truck("XYZ789");
        Vehicle motorcycle = new Motorcycle("M1234");

        // Park vehicles
        parkingLot.parkVehicle(car);
        parkingLot.parkVehicle(truck);
        parkingLot.parkVehicle(motorcycle);

        // Display availability
        parkingLot.displayAvailability();

        // Unpark a vehicle
        parkingLot.unparkVehicle(motorcycle);

        // Display updated availability
        parkingLot.displayAvailability();
    }
}
