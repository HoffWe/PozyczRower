package AI2.DTO;

/**
 * DTO dla danych typu roweru przekazywanych z formularza.
 *
 * @param name        nazwa polska (główna, używana jako klucz)
 * @param nameEn      nazwa angielska (opcjonalna)
 * @param description opis typu
 * @author Adrian Karpiński
 */
public record BikeTypeDTO(String name, String nameEn, String description) {
}
