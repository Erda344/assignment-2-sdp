public class Main {
    public static void main(String[] args) {
        String destination = args.length > 0 ? args[0] : "mars";

        if (destination.equalsIgnoreCase("mars")) {
            runExpedition(
                    new MarsSuit(),
                    new MarsTransportCreator(),
                    new MarsAnalyzer()
            );
        } else if (destination.equalsIgnoreCase("europa")) {
            runExpedition(
                    new EuropaSuit(),
                    new EuropaTransportCreator(),
                    new EuropaAnalyzer()
            );
        } else {
            System.out.println("Unknown destination: " + destination);
        }
    }

    static void runExpedition(
            Suit suit,
            TransportCreator transportCreator,
            Analyzer analyzer
    ) {
        System.out.println(suit.prepare());
        System.out.println(transportCreator.planRoundTrip(10, 5));
        System.out.println(analyzer.analyze());
    }
}

interface Suit {
    String prepare();
}

interface Transport {
    String travel();
    double travelHours(double distance);
}

interface Analyzer {
    String analyze();
}

class MarsSuit implements Suit {
    @Override
    public String prepare() {
        return "Mars suit protects against dust and thin atmosphere.";
    }
}

class MarsRover implements Transport {
    @Override
    public String travel() {
        return "Mars rover drives across the rocky surface.";
    }

    @Override
    public double travelHours(double distance) {
        return distance / 10;
    }
}

class MarsAnalyzer implements Analyzer {
    @Override
    public String analyze() {
        return "Mars analyzer checks the soil for minerals.";
    }
}

class EuropaSuit implements Suit {
    @Override
    public String prepare() {
        return "Europa suit protects against cold and radiation.";
    }
}

class EuropaSubmarine implements Transport {
    @Override
    public String travel() {
        return "Europa submarine travels beneath the ice.";
    }

    @Override
    public double travelHours(double distance) {
        return distance / 5;
    }
}

class EuropaAnalyzer implements Analyzer {
    @Override
    public String analyze() {
        return "Europa analyzer checks the water for organic compounds.";
    }
}

class TitanRover implements Transport {
    @Override
    public String travel() {
        return "Titan rover drives across the frozen terrain.";
    }

    @Override
    public double travelHours(double distance) {
        return distance / 15;
    }
}

abstract class TransportCreator {
    protected abstract Transport createTransport();

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

        Transport transport = createTransport();
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

class MarsTransportCreator extends TransportCreator {
    @Override
    protected Transport createTransport() {
        return new MarsRover();
    }
}

class EuropaTransportCreator extends TransportCreator {
    @Override
    protected Transport createTransport() {
        return new EuropaSubmarine();
    }
}

class TitanTransportCreator extends TransportCreator {
    @Override
    protected Transport createTransport() {
        return new TitanRover();
    }
}