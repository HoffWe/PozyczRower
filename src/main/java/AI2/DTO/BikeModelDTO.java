package AI2.DTO;

/**
 * DTO przenoszący dane modelu roweru z warstwy widoku do serwisu.
 *
 * @param brand marka roweru
 * @param model nazwa modelu roweru
 * @author Rafał Wojciechowski
 */
public record BikeModelDTO(String brand, String model) {
}
