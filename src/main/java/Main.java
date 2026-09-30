import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public class Main {
    public static void main(String[] args) {
        String destination = args.length > 0 ? args[0] : "mars";

        try {
            EquipmentFactory<?> factory =
                    FactorySelector.select(destination);

            ExpeditionClient<?> client =
                    new ExpeditionClient<>(factory);

            System.out.println(client.survey(10));
            System.out.println(client.collectAndAnalyze());
            System.out.println(client.emergencyReturn());
            System.out.println(client.status());
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Expedition failed: " + e.getMessage());
        }
    }
}

interface Family {
}

final class Mars implements Family {
}

final class Europa implements Family {
}

final class Titan implements Family {
}

final class Venus implements Family {
}

interface Suit<F extends Family> {
    String prepare();
    double remainingHours();
    void consumeOxygen(double hours);
}

interface Transport<F extends Family> {
    String travel();
    double travelHours(double distance);
    double remainingEnergy();
    boolean canTravel(double distance);
    void move(double distance);
    Sample<F> collectSample();
}

interface Analyzer<F extends Family> {
    double analysisHours();
    String analyze(Sample<F> sample);
}

class Sample<F extends Family> {
    private final String material;
    private final double measurement;

    public Sample(String material, double measurement) {
        if (material == null || material.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Sample material is required."
            );
        }

        if (!Double.isFinite(measurement)
                || measurement < 0
                || measurement > 100) {
            throw new IllegalArgumentException(
                    "Measurement must be between 0 and 100."
            );
        }

        this.material = material;
        this.measurement = measurement;
    }

    public String material() {
        return material;
    }

    public double measurement() {
        return measurement;
    }
}

abstract class BaseSuit<F extends Family> implements Suit<F> {
    private double oxygenHours;

    protected BaseSuit(double oxygenHours) {
        this.oxygenHours = oxygenHours;
    }

    @Override
    public double remainingHours() {
        return oxygenHours;
    }

    @Override
    public void consumeOxygen(double hours) {
        if (!Double.isFinite(hours) || hours <= 0) {
            throw new IllegalArgumentException(
                    "Oxygen usage must be positive and finite."
            );
        }

        if (hours > oxygenHours) {
            throw new IllegalStateException("Not enough oxygen.");
        }

        oxygenHours -= hours;
    }
}

class MarsSuit extends BaseSuit<Mars> {
    public MarsSuit() {
        super(8);
    }

    @Override
    public String prepare() {
        return "Mars suit protects against dust and thin atmosphere.";
    }
}

class EuropaSuit extends BaseSuit<Europa> {
    public EuropaSuit() {
        super(6);
    }

    @Override
    public String prepare() {
        return "Europa suit protects against cold and radiation.";
    }
}

class TitanSuit extends BaseSuit<Titan> {
    public TitanSuit() {
        super(10);
    }

    @Override
    public String prepare() {
        return "Titan suit provides heating in extreme cold.";
    }
}

class VenusSuit extends BaseSuit<Venus> {
    public VenusSuit() {
        super(6);
    }

    @Override
    public String prepare() {
        return "Venus suit provides cooling and pressure protection.";
    }
}

abstract class BaseTransport<F extends Family>
        implements Transport<F> {

    private final double speed;
    private final double energyPerKm;
    private double energy = 100;

    protected BaseTransport(double speed, double energyPerKm) {
        this.speed = speed;
        this.energyPerKm = energyPerKm;
    }

    @Override
    public double travelHours(double distance) {
        validateDistance(distance);
        return distance / speed;
    }

    @Override
    public double remainingEnergy() {
        return energy;
    }

    @Override
    public boolean canTravel(double distance) {
        validateDistance(distance);
        return distance * energyPerKm <= energy;
    }

    @Override
    public void move(double distance) {
        if (!canTravel(distance)) {
            throw new IllegalStateException(
                    "Not enough transport energy."
            );
        }

        energy -= distance * energyPerKm;
    }

    private void validateDistance(double distance) {
        if (!Double.isFinite(distance) || distance <= 0) {
            throw new IllegalArgumentException(
                    "Distance must be positive and finite."
            );
        }
    }
}

class MarsRover extends BaseTransport<Mars> {
    public MarsRover() {
        super(10, 2);
    }

    @Override
    public String travel() {
        return "Mars rover drives across the rocky surface.";
    }

    @Override
    public Sample<Mars> collectSample() {
        return new Sample<>("soil", 18);
    }
}

class EuropaSubmarine extends BaseTransport<Europa> {
    public EuropaSubmarine() {
        super(5, 3);
    }

    @Override
    public String travel() {
        return "Europa submarine travels beneath the ice.";
    }

    @Override
    public Sample<Europa> collectSample() {
        return new Sample<>("water", 25);
    }
}

class TitanRover extends BaseTransport<Titan> {
    public TitanRover() {
        super(15, 1);
    }

    @Override
    public String travel() {
        return "Titan rover drives across the frozen terrain.";
    }

    @Override
    public Sample<Titan> collectSample() {
        return new Sample<>("hydrocarbons", 85);
    }
}

class VenusRover extends BaseTransport<Venus> {
    public VenusRover() {
        super(8, 4);
    }

    @Override
    public String travel() {
        return "Venus rover drives across the hot volcanic surface.";
    }

    @Override
    public Sample<Venus> collectSample() {
        return new Sample<>("volcanic rock", 30);
    }
}

class MarsAnalyzer implements Analyzer<Mars> {
    @Override
    public double analysisHours() {
        return 0.5;
    }

    @Override
    public String analyze(Sample<Mars> sample) {
        String result = sample.measurement() >= 10
                ? "iron-rich"
                : "iron-poor";

        return sample.material() + ": "
                + sample.measurement() + "% iron, " + result;
    }
}

class EuropaAnalyzer implements Analyzer<Europa> {
    @Override
    public double analysisHours() {
        return 1;
    }

    @Override
    public String analyze(Sample<Europa> sample) {
        String result = sample.measurement() <= 35
                ? "low salinity"
                : "high salinity";

        return sample.material() + ": "
                + sample.measurement() + " g/kg salt, " + result;
    }
}

class TitanAnalyzer implements Analyzer<Titan> {
    @Override
    public double analysisHours() {
        return 0.25;
    }

    @Override
    public String analyze(Sample<Titan> sample) {
        String result = sample.measurement() >= 80
                ? "methane-rich"
                : "methane-poor";

        return sample.material() + ": "
                + sample.measurement() + "% methane, " + result;
    }
}

class VenusAnalyzer implements Analyzer<Venus> {
    @Override
    public double analysisHours() {
        return 0.75;
    }

    @Override
    public String analyze(Sample<Venus> sample) {
        String result = sample.measurement() >= 20
                ? "sulfur-rich"
                : "sulfur-poor";

        return sample.material() + ": "
                + sample.measurement() + "% sulfur, " + result;
    }
}

abstract class TransportCreator<F extends Family> {
    private Transport<F> transport;

    public abstract Transport<F> createTransport();

    public final Transport<F> getTransport() {
        if (transport == null) {
            transport = createTransport();
        }

        return transport;
    }

    public double planRoundTrip(
            double distance,
            double availableHours
    ) {
        if (!Double.isFinite(distance) || distance <= 0) {
            throw new IllegalArgumentException(
                    "Distance must be positive and finite."
            );
        }

        if (!Double.isFinite(availableHours) || availableHours <= 0) {
            throw new IllegalArgumentException(
                    "Available time must be positive and finite."
            );
        }

        Transport<F> vehicle = getTransport();
        double totalDistance = distance * 2;

        if (!Double.isFinite(totalDistance)) {
            throw new IllegalArgumentException("Distance is too large.");
        }

        double requiredHours = vehicle.travelHours(totalDistance);

        if (requiredHours > availableHours) {
            throw new IllegalStateException(
                    "Not enough oxygen time for the round trip."
            );
        }

        if (!vehicle.canTravel(totalDistance)) {
            throw new IllegalStateException(
                    "Not enough energy for the round trip."
            );
        }

        return requiredHours;
    }
}

abstract class EquipmentFactory<F extends Family>
        extends TransportCreator<F> {

    public abstract Suit<F> createSuit();

    @Override
    public abstract Transport<F> createTransport();

    public abstract Analyzer<F> createAnalyzer();
}

class MarsFactory extends EquipmentFactory<Mars> {
    @Override
    public Suit<Mars> createSuit() {
        return new MarsSuit();
    }

    @Override
    public Transport<Mars> createTransport() {
        return new MarsRover();
    }

    @Override
    public Analyzer<Mars> createAnalyzer() {
        return new MarsAnalyzer();
    }
}

class EuropaFactory extends EquipmentFactory<Europa> {
    @Override
    public Suit<Europa> createSuit() {
        return new EuropaSuit();
    }

    @Override
    public Transport<Europa> createTransport() {
        return new EuropaSubmarine();
    }

    @Override
    public Analyzer<Europa> createAnalyzer() {
        return new EuropaAnalyzer();
    }
}

class TitanFactory extends EquipmentFactory<Titan> {
    @Override
    public Suit<Titan> createSuit() {
        return new TitanSuit();
    }

    @Override
    public Transport<Titan> createTransport() {
        return new TitanRover();
    }

    @Override
    public Analyzer<Titan> createAnalyzer() {
        return new TitanAnalyzer();
    }
}

class VenusFactory extends EquipmentFactory<Venus> {
    @Override
    public Suit<Venus> createSuit() {
        return new VenusSuit();
    }

    @Override
    public Transport<Venus> createTransport() {
        return new VenusRover();
    }

    @Override
    public Analyzer<Venus> createAnalyzer() {
        return new VenusAnalyzer();
    }
}

class FactorySelector {
    private static final Map<
            String, Supplier<EquipmentFactory<?>>
            > FACTORIES = new HashMap<>();

    static {
        FACTORIES.put("mars", MarsFactory::new);
        FACTORIES.put("europa", EuropaFactory::new);
        FACTORIES.put("titan", TitanFactory::new);
        FACTORIES.put("venus", VenusFactory::new);
    }

    public static EquipmentFactory<?> select(String destination) {
        if (destination == null) {
            throw new IllegalArgumentException(
                    "Destination is required."
            );
        }

        String name = destination.trim().toLowerCase(Locale.ROOT);
        Supplier<EquipmentFactory<?>> supplier = FACTORIES.get(name);

        if (supplier == null) {
            throw new IllegalArgumentException(
                    "Unknown destination: " + destination
            );
        }

        return supplier.get();
    }
}

class ExpeditionClient<F extends Family> {
    private final EquipmentFactory<F> factory;
    private final Suit<F> suit;
    private final Transport<F> transport;
    private final Analyzer<F> analyzer;

    private double distanceFromBase;

    public ExpeditionClient(EquipmentFactory<F> factory) {
        if (factory == null) {
            throw new IllegalArgumentException("Factory is required.");
        }

        this.factory = factory;
        this.suit = factory.createSuit();
        this.transport = factory.getTransport();
        this.analyzer = factory.createAnalyzer();
    }

    public String survey(double distance) {
        if (distanceFromBase > 0) {
            throw new IllegalStateException(
                    "Return to base before starting another survey."
            );
        }

        double roundTripHours = factory.planRoundTrip(
                distance, suit.remainingHours()
        );

        double outwardHours = transport.travelHours(distance);

        transport.move(distance);
        suit.consumeOxygen(outwardHours);
        distanceFromBase = distance;

        return suit.prepare()
                + "\n" + transport.travel()
                + "\nSurvey point reached: " + distance + " km."
                + "\nReserved round trip time: "
                + roundTripHours + " hours.";
    }

    public String collectAndAnalyze() {
        requireAwayFromBase();

        double analysisHours = analyzer.analysisHours();
        double returnHours = transport.travelHours(distanceFromBase);

        if (analysisHours + returnHours > suit.remainingHours()) {
            throw new IllegalStateException(
                    "Not enough oxygen for analysis and return."
            );
        }

        Sample<F> sample = transport.collectSample();
        String result = analyzer.analyze(sample);

        suit.consumeOxygen(analysisHours);

        return "Sample analysis: " + result;
    }

    public String emergencyReturn() {
        requireAwayFromBase();

        double returnHours = transport.travelHours(distanceFromBase);

        if (returnHours > suit.remainingHours()) {
            throw new IllegalStateException(
                    "Not enough oxygen to return."
            );
        }

        if (!transport.canTravel(distanceFromBase)) {
            throw new IllegalStateException(
                    "Not enough energy to return."
            );
        }

        transport.move(distanceFromBase);
        suit.consumeOxygen(returnHours);
        distanceFromBase = 0;

        return "Returned to base.";
    }

    public double remainingOxygenHours() {
        return suit.remainingHours();
    }

    public double remainingEnergy() {
        return transport.remainingEnergy();
    }

    public double distanceFromBase() {
        return distanceFromBase;
    }

    public String status() {
        return "Oxygen remaining: " + remainingOxygenHours()
                + " hours.\nTransport energy: " + remainingEnergy()
                + "%.\nDistance from base: " + distanceFromBase
                + " km.";
    }

    private void requireAwayFromBase() {
        if (distanceFromBase == 0) {
            throw new IllegalStateException(
                    "Start a survey before performing this operation."
            );
        }
    }
}