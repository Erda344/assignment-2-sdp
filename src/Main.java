public class Main {
    public static void main(String[] args) {
        String destination = args.length > 0 ? args[0] : "mars";

        if (destination.equalsIgnoreCase("mars")) {
            runExpedition(
                    new MarsSuit(),
                    new MarsRover(),
                    new MarsAnalyzer()
            );
        } else if (destination.equalsIgnoreCase("europa")) {
            runExpedition(
                    new EuropaSuit(),
                    new EuropaSubmarine(),
                    new EuropaAnalyzer()
            );
        } else {
            System.out.println("Unknown destination: " + destination);
        }
    }

    static void runExpedition(
            Suit suit,
            Transport transport,
            Analyzer analyzer
    ) {
        System.out.println(suit.prepare());
        System.out.println(transport.travel());
        System.out.println(analyzer.analyze());
    }
}

interface Suit {
    String prepare();
}

interface Transport {
    String travel();
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
}

class EuropaAnalyzer implements Analyzer {
    @Override
    public String analyze() {
        return "Europa analyzer checks the water for organic compounds.";
    }
}