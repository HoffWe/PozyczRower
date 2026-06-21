package AI2.Util;

import AI2.DTO.BikeModelDTO;
import AI2.DTO.BikeTypeDTO;
import AI2.Enums.BikeStatus;
import AI2.Enums.UserRole;
import AI2.Service.BikeModelService;
import AI2.Service.BikeService;
import AI2.Service.BikeTypeService;
import AI2.Service.ClientService;
import AI2.Service.UserService;

/**
 * Wypełnia repozytoria przykładowymi danymi przy pierwszym uruchomieniu
 * (gdy bazy są puste).
 *
 * @author Tomasz Piłat
 */
public class DataSeeder {

    /**
     * Uruchamia seed tylko gdy wszystkie repozytoria są puste.
     * Kolejność: typy → modele → klienci → rowery.
     *
     * @param bikeTypeService  serwis typów rowerów
     * @param bikeModelService serwis modeli rowerów
     * @param clientService    serwis klientów
     * @param bikeService      serwis rowerów
     * @author Tomasz Piłat
     */
    public static void seedIfEmpty(BikeTypeService bikeTypeService,
                                   BikeModelService bikeModelService,
                                   ClientService clientService,
                                   BikeService bikeService,
                                   UserService userService) {
        // ---- Użytkownicy – domyślny admin (zawsze sprawdzaj, niezależnie od pozostałych danych) ----
        if (userService.isEmpty()) {
            userService.addUser("admin", "admin", UserRole.ADMIN);
        }

        boolean typesEmpty   = bikeTypeService.getAllBikeTypes().isEmpty();
        boolean modelsEmpty  = bikeModelService.getAllBikeModels().isEmpty();
        boolean clientsEmpty = clientService.getAllClients().isEmpty();
        boolean bikesEmpty   = bikeService.getAllBikes().isEmpty();

        if (!typesEmpty && !modelsEmpty && !clientsEmpty && !bikesEmpty) return;

        // ---- Typy rowerów ----
        if (typesEmpty) {
            bikeTypeService.addBikeType(new BikeTypeDTO("Górski",   "Rower terenowy MTB"));
            bikeTypeService.addBikeType(new BikeTypeDTO("Miejski",  "Rower do jazdy po mieście"));
            bikeTypeService.addBikeType(new BikeTypeDTO("Szosowy",  "Rower wyścigowy na asfalt"));
            bikeTypeService.addBikeType(new BikeTypeDTO("BMX",      "Rower wyczynowy / freestyle"));
            bikeTypeService.saveBikeTypes();
        }

        // ---- Modele rowerów ----
        if (modelsEmpty) {
            bikeModelService.addBikeModel(new BikeModelDTO("Trek",       "Marlin 5"));
            bikeModelService.addBikeModel(new BikeModelDTO("Trek",       "X-Caliber 8"));
            bikeModelService.addBikeModel(new BikeModelDTO("Giant",      "Escape 3"));
            bikeModelService.addBikeModel(new BikeModelDTO("Giant",      "Talon 3"));
            bikeModelService.addBikeModel(new BikeModelDTO("Specialized","Allez"));
            bikeModelService.addBikeModel(new BikeModelDTO("GT",         "Slammer"));
            bikeModelService.saveBikeModels();
        }

        // ---- Klienci ----
        if (clientsEmpty) {
            clientService.addClient("Jan",    "Kowalski",   "ABC123456", "Stały klient");
            clientService.addClient("Anna",   "Nowak",      "DEF234567", "");
            clientService.addClient("Piotr",  "Wiśniewski", "GHI345678", "Student");
            clientService.addClient("Maria",  "Wójcik",     "JKL456789", "");
            clientService.addClient("Tomasz", "Zieliński",  "MNO567890", "Wycieczki weekendowe");
        }

        // ---- Rowery ----
        if (bikesEmpty) {
            // Pobierz IDs (generowane przez repozytoria zaczynające od 1)
            int gorski  = idOfType(bikeTypeService, "Górski");
            int miejski = idOfType(bikeTypeService, "Miejski");
            int szosowy = idOfType(bikeTypeService, "Szosowy");
            int bmx     = idOfType(bikeTypeService, "BMX");

            int marlin5   = idOfModel(bikeModelService, "Trek",        "Marlin 5");
            int xcaliber  = idOfModel(bikeModelService, "Trek",        "X-Caliber 8");
            int escape    = idOfModel(bikeModelService, "Giant",       "Escape 3");
            int talon     = idOfModel(bikeModelService, "Giant",       "Talon 3");
            int allez     = idOfModel(bikeModelService, "Specialized", "Allez");
            int slammer   = idOfModel(bikeModelService, "GT",          "Slammer");

            // Górskie
            bikeService.addBike(marlin5,  gorski,  29, BikeStatus.AVAILABLE, "");
            bikeService.addBike(marlin5,  gorski,  29, BikeStatus.AVAILABLE, "Lekka zarysowanie ramy");
            bikeService.addBike(xcaliber, gorski,  27, BikeStatus.AVAILABLE, "");
            bikeService.addBike(talon,    gorski,  29, BikeStatus.AVAILABLE, "");
            // Miejskie
            bikeService.addBike(escape,   miejski, 28, BikeStatus.AVAILABLE, "Koszyk w zestawie");
            bikeService.addBike(escape,   miejski, 28, BikeStatus.AVAILABLE, "");
            bikeService.addBike(escape,   miejski, 26, BikeStatus.AVAILABLE, "");
            // Szosowe
            bikeService.addBike(allez,    szosowy, 28, BikeStatus.AVAILABLE, "");
            bikeService.addBike(allez,    szosowy, 28, BikeStatus.AVAILABLE, "");
            // BMX
            bikeService.addBike(slammer,  bmx,     20, BikeStatus.AVAILABLE, "");
        }
    }

    /**
     * Pomocniczo zwraca ID pierwszego typu pasującego po nazwie.
     *
     * @param service serwis typów
     * @param name    szukana nazwa
     * @return ID lub -1
     * @author Tomasz Piłat
     */
    private static int idOfType(BikeTypeService service, String name) {
        return service.getAllBikeTypes().stream()
                .filter(t -> t.getBikeTypeName().equals(name))
                .mapToInt(t -> t.getBikeTypeId())
                .findFirst().orElse(-1);
    }

    /**
     * Pomocniczo zwraca ID pierwszego modelu pasującego po marce i modelu.
     *
     * @param service serwis modeli
     * @param brand   marka
     * @param model   model
     * @return ID lub -1
     * @author Tomasz Piłat
     */
    private static int idOfModel(BikeModelService service, String brand, String model) {
        return service.getAllBikeModels().stream()
                .filter(m -> m.getBrand().equals(brand) && m.getModel().equals(model))
                .mapToInt(m -> m.getId())
                .findFirst().orElse(-1);
    }
}
