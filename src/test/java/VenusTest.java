import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VenusTest {
    private static final double EPSILON = 0.000001;

    @Test
    void venusFactoryCreatesCompatibleProducts() {
        VenusFactory factory = new VenusFactory();

        assertInstanceOf(VenusSuit.class, factory.createSuit());
        assertInstanceOf(VenusRover.class, factory.createTransport());
        assertInstanceOf(VenusAnalyzer.class, factory.createAnalyzer());

        Sample<Venus> sample = factory.getTransport().collectSample();

        assertTrue(factory.createAnalyzer().analyze(sample)
                .contains("sulfur-rich"));

        assertEquals(2.5, factory.planRoundTrip(10, 6), EPSILON);
    }

    @Test
    void selectorSupportsVenus() {
        assertInstanceOf(
                VenusFactory.class,
                FactorySelector.select(" VENUS ")
        );
    }

    @Test
    void existingClientCompletesVenusExpedition() {
        ExpeditionClient<Venus> client =
                new ExpeditionClient<>(new VenusFactory());

        client.survey(10);

        assertEquals(10, client.distanceFromBase(), EPSILON);
        assertEquals(4.75, client.remainingOxygenHours(), EPSILON);
        assertEquals(60, client.remainingEnergy(), EPSILON);

        assertTrue(client.collectAndAnalyze().contains("sulfur-rich"));

        client.emergencyReturn();

        assertEquals(0, client.distanceFromBase(), EPSILON);
        assertEquals(2.75, client.remainingOxygenHours(), EPSILON);
        assertEquals(20, client.remainingEnergy(), EPSILON);
    }

    @Test
    void venusRejectsTripWithInsufficientEnergy() {
        ExpeditionClient<Venus> client =
                new ExpeditionClient<>(new VenusFactory());

        IllegalStateException error = assertThrows(
                IllegalStateException.class,
                () -> client.survey(13)
        );

        assertTrue(error.getMessage().contains("energy"));
        assertEquals(6, client.remainingOxygenHours(), EPSILON);
        assertEquals(100, client.remainingEnergy(), EPSILON);
        assertEquals(0, client.distanceFromBase(), EPSILON);
    }

    @Test
    void venusAnalyzerRecognizesSulfurPoorSample() {
        VenusAnalyzer analyzer = new VenusAnalyzer();

        String result = analyzer.analyze(
                new Sample<Venus>("volcanic rock", 5)
        );

        assertTrue(result.contains("sulfur-poor"));
    }
}