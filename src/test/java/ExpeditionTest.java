import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExpeditionTest {
    private static final double EPSILON = 0.000001;

    @Test
    void marsFactoryCreatesCompatibleProducts() {
        MarsFactory factory = new MarsFactory();

        assertInstanceOf(MarsSuit.class, factory.createSuit());
        assertInstanceOf(MarsRover.class, factory.createTransport());
        assertInstanceOf(MarsAnalyzer.class, factory.createAnalyzer());

        Sample<Mars> sample = factory.getTransport().collectSample();

        assertTrue(factory.createAnalyzer().analyze(sample)
                .contains("iron-rich"));

        assertEquals(2, factory.planRoundTrip(10, 8), EPSILON);
    }

    @Test
    void europaFactoryCreatesCompatibleProducts() {
        EuropaFactory factory = new EuropaFactory();

        assertInstanceOf(EuropaSuit.class, factory.createSuit());
        assertInstanceOf(
                EuropaSubmarine.class, factory.createTransport()
        );
        assertInstanceOf(
                EuropaAnalyzer.class, factory.createAnalyzer()
        );

        Sample<Europa> sample = factory.getTransport().collectSample();

        assertTrue(factory.createAnalyzer().analyze(sample)
                .contains("low salinity"));

        assertEquals(4, factory.planRoundTrip(10, 6), EPSILON);
    }

    @Test
    void titanFactoryCreatesCompatibleProducts() {
        TitanFactory factory = new TitanFactory();

        assertInstanceOf(TitanSuit.class, factory.createSuit());
        assertInstanceOf(TitanRover.class, factory.createTransport());
        assertInstanceOf(TitanAnalyzer.class, factory.createAnalyzer());

        Sample<Titan> sample = factory.getTransport().collectSample();

        assertTrue(factory.createAnalyzer().analyze(sample)
                .contains("methane-rich"));

        assertEquals(
                20.0 / 15,
                factory.planRoundTrip(10, 10),
                EPSILON
        );
    }

    @Test
    void selectorReturnsCorrectFactories() {
        assertInstanceOf(
                MarsFactory.class, FactorySelector.select("mars")
        );
        assertInstanceOf(
                EuropaFactory.class, FactorySelector.select("europa")
        );
        assertInstanceOf(
                TitanFactory.class, FactorySelector.select("titan")
        );
    }

    @Test
    void selectorAcceptsSpacesAndUppercase() {
        assertInstanceOf(
                MarsFactory.class, FactorySelector.select(" MARS ")
        );
    }

    @Test
    void selectorRejectsUnknownDestination() {
        assertThrows(
                IllegalArgumentException.class,
                () -> FactorySelector.select("unknown")
        );
    }

    @Test
    void selectorRejectsNullDestination() {
        assertThrows(
                IllegalArgumentException.class,
                () -> FactorySelector.select(null)
        );
    }

    @Test
    void surveyConsumesOutboundResources() {
        ExpeditionClient<Mars> client =
                new ExpeditionClient<>(new MarsFactory());

        client.survey(10);

        assertEquals(10, client.distanceFromBase(), EPSILON);
        assertEquals(7, client.remainingOxygenHours(), EPSILON);
        assertEquals(80, client.remainingEnergy(), EPSILON);
    }

    @Test
    void analysisConsumesOxygenWithoutMovingTransport() {
        ExpeditionClient<Mars> client =
                new ExpeditionClient<>(new MarsFactory());

        client.survey(10);
        String result = client.collectAndAnalyze();

        assertTrue(result.contains("iron-rich"));
        assertEquals(6.5, client.remainingOxygenHours(), EPSILON);
        assertEquals(80, client.remainingEnergy(), EPSILON);
        assertEquals(10, client.distanceFromBase(), EPSILON);
    }

    @Test
    void returnConsumesResourcesAndReachesBase() {
        ExpeditionClient<Mars> client =
                new ExpeditionClient<>(new MarsFactory());

        client.survey(10);
        client.collectAndAnalyze();
        client.emergencyReturn();

        assertEquals(0, client.distanceFromBase(), EPSILON);
        assertEquals(5.5, client.remainingOxygenHours(), EPSILON);
        assertEquals(60, client.remainingEnergy(), EPSILON);
    }

    @Test
    void analysisBeforeSurveyIsRejected() {
        ExpeditionClient<Mars> client =
                new ExpeditionClient<>(new MarsFactory());

        assertThrows(
                IllegalStateException.class,
                client::collectAndAnalyze
        );

        assertEquals(8, client.remainingOxygenHours(), EPSILON);
        assertEquals(100, client.remainingEnergy(), EPSILON);
    }

    @Test
    void returnBeforeSurveyIsRejected() {
        ExpeditionClient<Mars> client =
                new ExpeditionClient<>(new MarsFactory());

        assertThrows(
                IllegalStateException.class,
                client::emergencyReturn
        );
    }

    @Test
    void anotherSurveyRequiresReturningToBase() {
        ExpeditionClient<Mars> client =
                new ExpeditionClient<>(new MarsFactory());

        client.survey(10);

        assertThrows(
                IllegalStateException.class,
                () -> client.survey(5)
        );

        assertEquals(10, client.distanceFromBase(), EPSILON);
        assertEquals(7, client.remainingOxygenHours(), EPSILON);
        assertEquals(80, client.remainingEnergy(), EPSILON);
    }

    @Test
    void insufficientOxygenRejectsSurveyWithoutConsumingResources() {
        ExpeditionClient<Mars> client =
                new ExpeditionClient<>(new MarsFactory());

        IllegalStateException error = assertThrows(
                IllegalStateException.class,
                () -> client.survey(41)
        );

        assertTrue(error.getMessage().contains("oxygen"));
        assertEquals(8, client.remainingOxygenHours(), EPSILON);
        assertEquals(100, client.remainingEnergy(), EPSILON);
        assertEquals(0, client.distanceFromBase(), EPSILON);
    }

    @Test
    void insufficientEnergyRejectsSurveyWithoutConsumingResources() {
        ExpeditionClient<Mars> client =
                new ExpeditionClient<>(new MarsFactory());

        IllegalStateException error = assertThrows(
                IllegalStateException.class,
                () -> client.survey(26)
        );

        assertTrue(error.getMessage().contains("energy"));
        assertEquals(8, client.remainingOxygenHours(), EPSILON);
        assertEquals(100, client.remainingEnergy(), EPSILON);
        assertEquals(0, client.distanceFromBase(), EPSILON);
    }

    @Test
    void invalidDistancesAreRejected() {
        ExpeditionClient<Mars> client =
                new ExpeditionClient<>(new MarsFactory());

        double[] distances = {
                0,
                -1,
                Double.NaN,
                Double.POSITIVE_INFINITY
        };

        for (double distance : distances) {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> client.survey(distance)
            );
        }

        assertEquals(8, client.remainingOxygenHours(), EPSILON);
        assertEquals(100, client.remainingEnergy(), EPSILON);
    }

    @Test
    void invalidAvailableTimesAreRejected() {
        MarsFactory factory = new MarsFactory();

        double[] times = {
                0,
                -1,
                Double.NaN,
                Double.POSITIVE_INFINITY
        };

        for (double time : times) {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> factory.planRoundTrip(10, time)
            );
        }
    }

    @Test
    void analysisPreservesOxygenNeededForReturn() {
        ExpeditionClient<Europa> client =
                new ExpeditionClient<>(new EuropaFactory());

        client.survey(15);

        assertThrows(
                IllegalStateException.class,
                client::collectAndAnalyze
        );

        assertEquals(3, client.remainingOxygenHours(), EPSILON);
        assertEquals(55, client.remainingEnergy(), EPSILON);

        client.emergencyReturn();

        assertEquals(0, client.distanceFromBase(), EPSILON);
        assertEquals(0, client.remainingOxygenHours(), EPSILON);
        assertEquals(10, client.remainingEnergy(), EPSILON);
    }

    @Test
    void creatorReusesTransportAndItsRemainingEnergy() {
        MarsFactory factory = new MarsFactory();
        Transport<Mars> transport = factory.getTransport();

        transport.move(10);

        assertSame(transport, factory.getTransport());
        assertEquals(
                80,
                factory.getTransport().remainingEnergy(),
                EPSILON
        );

        assertThrows(
                IllegalStateException.class,
                () -> factory.planRoundTrip(21, 8)
        );
    }

    @Test
    void analyzersClassifyDifferentMeasurements() {
        MarsAnalyzer mars = new MarsAnalyzer();
        EuropaAnalyzer europa = new EuropaAnalyzer();
        TitanAnalyzer titan = new TitanAnalyzer();

        assertTrue(mars.analyze(new Sample<Mars>("soil", 5))
                .contains("iron-poor"));

        assertTrue(europa.analyze(new Sample<Europa>("water", 50))
                .contains("high salinity"));

        assertTrue(titan.analyze(new Sample<Titan>("hydrocarbons", 40))
                .contains("methane-poor"));
    }

    @Test
    void clientWorksWithCustomFactoryThroughAbstractions() {
        EquipmentFactory<TestFamily> factory =
                new EquipmentFactory<TestFamily>() {
                    @Override
                    public Suit<TestFamily> createSuit() {
                        return new BaseSuit<TestFamily>(4) {
                            @Override
                            public String prepare() {
                                return "Test suit ready.";
                            }
                        };
                    }

                    @Override
                    public Transport<TestFamily> createTransport() {
                        return new BaseTransport<TestFamily>(20, 1) {
                            @Override
                            public String travel() {
                                return "Test transport moving.";
                            }

                            @Override
                            public Sample<TestFamily> collectSample() {
                                return new Sample<>("test material", 7);
                            }
                        };
                    }

                    @Override
                    public Analyzer<TestFamily> createAnalyzer() {
                        return new Analyzer<TestFamily>() {
                            @Override
                            public double analysisHours() {
                                return 0.25;
                            }

                            @Override
                            public String analyze(
                                    Sample<TestFamily> sample
                            ) {
                                return "Custom measurement: "
                                        + sample.measurement();
                            }
                        };
                    }
                };

        ExpeditionClient<TestFamily> client =
                new ExpeditionClient<>(factory);

        assertTrue(client.survey(10).contains("Test transport"));
        assertTrue(client.collectAndAnalyze()
                .contains("Custom measurement: 7.0"));

        client.emergencyReturn();

        assertEquals(0, client.distanceFromBase(), EPSILON);
        assertEquals(2.75, client.remainingOxygenHours(), EPSILON);
        assertEquals(80, client.remainingEnergy(), EPSILON);
    }

    private static final class TestFamily implements Family {
    }
}