# Space Expedition

Software Design Patterns — Assignment 2

## Domain

This application simulates expeditions to different space environments.

Each expedition requires three product types:

- Suit
- Transport
- Analyzer

The original families are Mars, Europa and Titan.
Venus was added as the fourth family.

The system contains 12 concrete equipment products.
All resource values are simplified simulation parameters.

## Product families

| Family | Suit | Transport | Analyzer |
|--------|------|-----------|----------|
| Mars | MarsSuit | MarsRover | MarsAnalyzer |
| Europa | EuropaSuit | EuropaSubmarine | EuropaAnalyzer |
| Titan | TitanSuit | TitanRover | TitanAnalyzer |
| Venus | VenusSuit | VenusRover | VenusAnalyzer |

Families differ in oxygen capacity, transport speed,
energy consumption, analysis duration and sample classification.

## Initial implementation

The first version created concrete products directly in Main
using new and if/else.

This caused several design problems:

1. Main depended on concrete equipment classes.
2. Adding a family required changing the selection branches.
3. Expedition output logic was duplicated.
4. Equipment from different families could be combined accidentally.

The initial implementation is preserved in Git history.

## Factory Method

TransportCreator is the Creator.
Transport is the Product abstraction.

The factory method is createTransport().
Concrete factories override it to create their own transport.

TransportCreator also contains business logic in planRoundTrip().
It calculates the time required for an outward and return journey
and checks available oxygen time and transport energy.

getTransport() calls the factory method on first use and keeps
the transport instance so that later operations share its energy state.

This is Factory Method because object creation is delegated
to an overridden instance method in subclasses.

## Abstract Factory

EquipmentFactory is the Abstract Factory.

It declares:

- createSuit()
- createTransport()
- createAnalyzer()

MarsFactory, EuropaFactory, TitanFactory and VenusFactory
create complete equipment families.

EquipmentFactory extends TransportCreator.
Therefore, each concrete factory also acts as a Concrete Creator
for the Factory Method part.

## Compatibility

Family consistency is expressed through Java generics.

Suit<F>, Transport<F>, Analyzer<F>, Sample<F>,
EquipmentFactory<F> and ExpeditionClient<F>
use the same family parameter.

For example, MarsFactory returns Suit<Mars>,
Transport<Mars> and Analyzer<Mars>.

Returning EuropaSuit from MarsFactory.createSuit()
would cause a compilation error.

Similarly, Analyzer<Mars> cannot accept Sample<Europa>.

The client receives one factory and obtains its equipment
from that factory. It does not accept separately selected products.

This provides consistency in normally typed Java code.
Raw types and unchecked casts are not used.

## Runtime selection

The first command-line argument selects the family:

- mars
- europa
- titan
- venus

Mars is used when no argument is provided.

FactorySelector stores factory suppliers in a registry.
It accepts uppercase names and surrounding spaces.
Unknown or null destinations are rejected.

Main and ExpeditionClient work through abstractions after selection.

A new factory is created for each independent expedition
because the factory retains a transport with mutable energy state.

## Business operations

### Survey

survey(distance) checks that oxygen and transport energy
are sufficient for the round trip.

The transport then moves to the survey point.
The suit consumes oxygen for the outward journey.

### Sample collection and analysis

collectAndAnalyze() obtains a family-specific sample
from the transport and passes it to the matching analyzer.

The suit supplies oxygen during analysis.
The operation checks that enough oxygen remains for returning.

### Emergency return

emergencyReturn() checks the remaining oxygen and energy,
moves the transport back and sets the distance from base to zero.

Operations that require being away from base are rejected
when the expedition has not started.

## Adding Venus

The following files were changed or added:

| File | Change |
|------|--------|
| src/main/java/Main.java | Added Venus, VenusSuit, VenusRover, VenusAnalyzer, VenusFactory and the venus registry entry |
| src/test/java/VenusTest.java | Added five tests for the new family |

The following existing classes were not modified:

- ExpeditionClient
- TransportCreator
- EquipmentFactory
- BaseSuit
- BaseTransport
- Original product families

ExpeditionTest.java and pom.xml were also unchanged by this extension.

The existing business operations support Venus through
the same abstractions.

## Automated tests

The project contains 26 JUnit tests:

- 21 tests in ExpeditionTest
- 5 tests in VenusTest

Tests cover:

- Original family creation
- Concrete product types
- Compatible sample analysis
- Runtime factory selection
- Resource consumption in business operations
- Invalid input and incorrect operation order
- Insufficient oxygen and energy
- Reuse of the transport instance
- Preservation of oxygen for returning
- Different analyzer classifications
- Client operation with a custom test factory
- Venus creation and business behavior

The custom factory test demonstrates that the client
does not depend on the concrete production families.

All 26 tests passed in IntelliJ.

## Running

Requirements: Java 17 or newer.

In IntelliJ, run Main and set a destination
in the Program arguments field.

Example argument:

venus

To run tests, right-click src/test/java and select Run All Tests.

With Maven available in a terminal:

mvn test

To compile and run from the project root:

mvn compile
java -cp target/classes Main venus

## UML

The UML class diagram is provided in diagram.puml.

It includes product abstractions, concrete products,
the Creator, concrete creators, the Abstract Factory,
concrete factories and the client.

The Factory Method and Abstract Factory parts are marked.