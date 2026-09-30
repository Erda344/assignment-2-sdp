public class Main {
    public static void main(String[] args) {
        String destination = args.length > 0 ? args[0] : "mars";

        EquipmentFactory<?> factory;

        if (destination.equalsIgnoreCase("mars")) {
            factory = new MarsFactory();
        } else if (destination.equalsIgnoreCase("europa")) {
            factory = new EuropaFactory();
        } else if (destination.equalsIgnoreCase("titan")) {
            factory = new TitanFactory();
        } else {
            System.out.println("Unknown destination: " + destination);
            return;
        }

        ExpeditionClient<?> client = new ExpeditionClient<>(factory);
        client.run(10, 5);
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

interface Suit<F extends Family> {
    String prepare();
}

interface Transport<F extends Family> {
    String travel();
    double travelHours(double distance);
}

interface Analyzer<F extends Family> {
    String analyze();
}

class MarsSuit implements Suit<Mars> {
    @Override
    public String prepare() {
        return "Mars suit protects against dust and thin atmosphere.";
    }
}

class MarsRover implements Transport<Mars> {
    @Override
    public String travel() {
        return "Mars rover drives across the rocky surface.";
    }

    @Override
    public double travelHours(double distance) {
        return distance / 10;
    }
}

class MarsAnalyzer implements Analyzer<Mars> {
    @Override
    public String analyze() {
        return "Mars analyzer checks the soil for minerals.";
    }
}

class EuropaSuit implements Suit<Europa> {
    @Override
    public String prepare() {
        return "Europa suit protects against cold and radiation.";
    }
}

class EuropaSubmarine implements Transport<Europa> {
    @Override
    public String travel() {
        return "Europa submarine travels beneath the ice.";
    }

    @Override
    public double travelHours(double distance) {
        return distance / 5;
    }
}

class EuropaAnalyzer implements Analyzer<Europa> {
    @Override
    public String analyze() {
        return "Europa analyzer checks the water for organic compounds.";
    }
}

class TitanSuit implements Suit<Titan> {
    @Override
    public String prepare() {
        return "Titan suit provides heating in extreme cold.";
    }
}

class TitanRover implements Transport<Titan> {
    @Override
    public String travel() {
        return "Titan rover drives across the frozen terrain.";
    }

    @Override
    public double travelHours(double distance) {
        return distance / 15;
    }
}

class TitanAnalyzer implements Analyzer<Titan> {
    @Override
    public String analyze() {
        return "Titan analyzer checks samples for hydrocarbons.";
    }
}

abstract class TransportCreator<F extends Family> {
    public abstract Transport<F> createTransport();

    public String planRoundTrip(double distance, double availableHours) {
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

        Transport<F> transport = createTransport();
        double requiredHours = transport.travelHours(distance) * 2;

        if (requiredHours > availableHours) {
            throw new IllegalStateException(
                    "Not enough time for the round trip."
            );
        }

        return transport.travel()
                + "\nRound trip time: " + requiredHours + " hours.";
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

class ExpeditionClient<F extends Family> {
    private final EquipmentFactory<F> factory;
    private final Suit<F> suit;
    private final Analyzer<F> analyzer;

    public ExpeditionClient(EquipmentFactory<F> factory) {
        this.factory = factory;
        this.suit = factory.createSuit();
        this.analyzer = factory.createAnalyzer();
    }

    public void run(double distance, double availableHours) {
        System.out.println(suit.prepare());
        System.out.println(
                factory.planRoundTrip(distance, availableHours)
        );
        System.out.println(analyzer.analyze());
    }
}