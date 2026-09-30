public class Main {
    public static void main(String[] args) {
        String destination = args.length > 0 ? args[0] : "mars";

        if (destination.equalsIgnoreCase("mars")) {
            MarsSuit suit = new MarsSuit();
            MarsRover rover = new MarsRover();
            MarsAnalyzer analyzer = new MarsAnalyzer();

            System.out.println(suit.prepare());
            System.out.println(rover.travel());
            System.out.println(analyzer.analyze());
        } else if (destination.equalsIgnoreCase("europa")) {
            EuropaSuit suit = new EuropaSuit();
            EuropaSubmarine submarine = new EuropaSubmarine();
            EuropaAnalyzer analyzer = new EuropaAnalyzer();

            System.out.println(suit.prepare());
            System.out.println(submarine.travel());
            System.out.println(analyzer.analyze());
        } else {
            System.out.println("Unknown destination: " + destination);
        }
    }
}

class MarsSuit {
    String prepare() {
        return "Mars suit protects against dust and thin atmosphere.";
    }
}

class MarsRover {
    String travel() {
        return "Mars rover drives across the rocky surface.";
    }
}

class MarsAnalyzer {
    String analyze() {
        return "Mars analyzer checks the soil for minerals.";
    }
}

class EuropaSuit {
    String prepare() {
        return "Europa suit protects against cold and radiation.";
    }
}

class EuropaSubmarine {
    String travel() {
        return "Europa submarine travels beneath the ice.";
    }
}

class EuropaAnalyzer {
    String analyze() {
        return "Europa analyzer checks the water for organic compounds.";
    }
}