package AI2.View.Rent;

import AI2.Enums.BikeStatus;
import AI2.Model.Bike;
import AI2.Model.BikeModel;
import AI2.Model.BikeType;
import AI2.Service.BikeModelService;
import AI2.Service.BikeService;
import AI2.Service.BikeTypeService;
import AI2.Service.RentService;
import AI2.Util.LanguageManager;
import AI2.View.Components.AppButton;
import AI2.View.Components.SearchPanel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Okno dialogowe wyboru rowerów z wyszukiwarką.
 * Wyświetla dostępne rowery pogrupowane wg modelu, typu i rozmiaru kół.
 * Spinner „Ilość" pozwala wybrać więcej niż jeden rower z tej samej grupy.
 * Rowery już dodane do wypożyczenia (przekazane jako {@code excludedIds})
 * są odejmowane od dostępnej puli.
 *
 * @author Tomasz Piłat
 */
public class BikeSelectDialog extends JDialog {

    /** Wybrane rowery (pusta lista oznacza anulowanie). */
    private List<Bike> selectedBikes = Collections.emptyList();

    /**
     * Pełna lista grup dostępnych rowerów (po filtrowaniu dostępności),
     * niezmodyfikowana — używana jako źródło przy filtrze tekstowym.
     */
    private final List<GroupEntry> allGroups = new ArrayList<>();

    /**
     * Grupy aktualnie widoczne w tabeli (po filtrze tekstowym).
     * {@code confirmSelection()} mapuje wiersz tabeli na ten indeks.
     */
    private final List<GroupEntry> visibleGroups = new ArrayList<>();

    /** Tabela wyświetlająca pogrupowane rowery. */
    private final JTable table;

    /** Model tabeli. */
    private final DefaultTableModel tableModel;

    /** Spinner do wyboru ilości rowerów z zaznaczonej grupy. */
    private final JSpinner quantitySpinner;


    /** Przechowuje jedną grupę rowerów wraz z rozwiązanymi nazwami. */
    private static class GroupEntry {
        final List<Bike> bikes;
        final String typeName;
        final String brand;
        final String model;
        final int wheelSize;

        GroupEntry(List<Bike> bikes, BikeType bt, BikeModel bm) {
            this.bikes = bikes;
            this.typeName = bt != null ? bt.getDisplayName() : "?";
            this.brand = bm != null ? bm.getBrand() : "?";
            this.model = bm != null ? bm.getModel() : "?";
            this.wheelSize = bikes.get(0).getWheelSize();
        }

        boolean matches(String q) {
            return typeName.toLowerCase().contains(q)
                || brand.toLowerCase().contains(q)
                || model.toLowerCase().contains(q)
                || String.valueOf(wheelSize).contains(q);
        }

        Object[] toRow() {
            return new Object[]{ typeName, brand, model, wheelSize + "\"", bikes.size() };
        }
    }


    /**
     * Tworzy okno dialogowe wyboru rowerów z filtrowaniem okresu i wykluczaniem
     * już wybranych rowerów.
     *
     * @param owner            okno nadrzędne
     * @param bikeService      serwis rowerów
     * @param bikeModelService serwis modeli rowerów
     * @param bikeTypeService  serwis typów rowerów
     * @param rentService      serwis wypożyczeń
     * @param periodStart      początek okresu wypożyczenia
     * @param periodEnd        koniec okresu wypożyczenia
     * @param excludedIds      identyfikatory rowerów już dodanych (pomijane w puli)
     * @author Tomasz Piłat
     */
    public BikeSelectDialog(Window owner,
                            BikeService bikeService,
                            BikeModelService bikeModelService,
                            BikeTypeService bikeTypeService,
                            RentService rentService,
                            LocalDateTime periodStart,
                            LocalDateTime periodEnd,
                            Set<Integer> excludedIds) {
        super(owner, LanguageManager.getString("dialog.select.bike"),
                ModalityType.APPLICATION_MODAL);

        tableModel = new DefaultTableModel(new String[]{
                LanguageManager.getString("bike.type"),
                LanguageManager.getString("bikeModel.brand"),
                LanguageManager.getString("bikeModel.model"),
                LanguageManager.getString("bike.wheelSize"),
                LanguageManager.getString("bike.available")
        }, 0) {
            @Override public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        List<Bike> available = bikeService.getAllBikes().stream()
                .filter(b -> {
                    if (b.getStatus() != BikeStatus.AVAILABLE) return false;
                    if (excludedIds.contains(b.getBikeId()))   return false;
                    try {
                        return rentService.isBikeAvailableInPeriod(
                                b.getBikeId(), periodStart, periodEnd);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());

        Map<String, List<Bike>> grouped = new LinkedHashMap<>();
        for (Bike b : available) {
            String key = b.getBikeModelId() + "|" + b.getBikeTypeId() + "|" + b.getWheelSize();
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(b);
        }

        for (List<Bike> bikes : grouped.values()) {
            Bike      first = bikes.get(0);
            BikeModel bm    = bikeModelService.getBikeModelById(first.getBikeModelId());
            BikeType  bt    = bikeTypeService.getBikeTypeById(first.getBikeTypeId());
            allGroups.add(new GroupEntry(bikes, bt, bm));
        }

        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1, 1));
        quantitySpinner.setEnabled(false);
        quantitySpinner.setPreferredSize(new Dimension(60, 30));

        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = table.getSelectedRow();
            if (row >= 0 && row < visibleGroups.size()) {
                int max = visibleGroups.get(row).bikes.size();
                SpinnerNumberModel m = (SpinnerNumberModel) quantitySpinner.getModel();
                m.setMaximum(max);
                m.setValue(1);
                quantitySpinner.setEnabled(true);
            } else {
                quantitySpinner.setEnabled(false);
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) confirmSelection();
            }
        });

        JButton selectButton = new AppButton(LanguageManager.getString("button.select"));
        selectButton.addActionListener(e -> confirmSelection());

        SearchPanel searchPanel = new SearchPanel();
        JTextField  searchField = searchPanel.getSearchField();
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { filter(searchField.getText()); }
            public void removeUpdate(DocumentEvent e)  { filter(searchField.getText()); }
            public void changedUpdate(DocumentEvent e) { filter(searchField.getText()); }
        });

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        south.add(new JLabel(LanguageManager.getString("bike.quantity") + ":"));
        south.add(quantitySpinner);
        south.add(selectButton);

        setLayout(new BorderLayout(10, 10));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(searchPanel,            BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(south,                  BorderLayout.SOUTH);

        filter("");

        setSize(620, 440);
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    /**
     * Wygodny konstruktor bez wykluczeń (np. z EditRentPanel).
     *
     * @param owner okno nadrzędne
     * @param bikeService serwis rowerów
     * @param bikeModelService serwis modeli rowerów
     * @param bikeTypeService serwis typów rowerów
     * @param rentService serwis wypożyczeń
     * @param periodStart początek okresu wypożyczenia
     * @param periodEnd koniec okresu wypożyczenia
     * @author Tomasz Piłat
     */
    public BikeSelectDialog(Window owner,
                            BikeService bikeService,
                            BikeModelService bikeModelService,
                            BikeTypeService bikeTypeService,
                            RentService rentService,
                            LocalDateTime periodStart,
                            LocalDateTime periodEnd) {
        this(owner, bikeService, bikeModelService, bikeTypeService,
                rentService, periodStart, periodEnd, Collections.emptySet());
    }

    /**
     * Przebudowuje tabelę pokazując tylko grupy pasujące do zapytania.
     * Filtruje po typie, marce, modelu i rozmiarze koła.
     *
     * @param query tekst wyszukiwania
     * @author Tomasz Piłat
     */
    private void filter(String query) {
        String q = query.trim().toLowerCase();
        tableModel.setRowCount(0);
        visibleGroups.clear();
        quantitySpinner.setEnabled(false);

        List<GroupEntry> filtered = q.isEmpty() ? allGroups : allGroups.stream()
                .filter(g -> g.matches(q))
                .collect(Collectors.toList());

        for (GroupEntry g : filtered) {
            visibleGroups.add(g);
            tableModel.addRow(g.toRow());
        }
    }

    /**
     * Zatwierdza wybór: pobiera pierwsze {@code quantity} rowerów z zaznaczonej grupy.
     *
     * @author Tomasz Piłat
     */
    private void confirmSelection() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= visibleGroups.size()) return;
        int qty  = (Integer) quantitySpinner.getValue();
        List<Bike> bikes = visibleGroups.get(row).bikes;
        selectedBikes = bikes.subList(0, Math.min(qty, bikes.size()));
        dispose();
    }

    /**
     * Zwraca listę wybranych rowerów (pusta = anulowano).
     *
     * @return wybrane rowery
     * @author Tomasz Piłat
     */
    public List<Bike> getSelectedBikes() {
        return selectedBikes;
    }

    /**
     * Wygodna metoda zwracająca pierwszy wybrany rower (dla EditRentPanel).
     *
     * @return pierwszy wybrany rower albo {@code null}
     * @author Tomasz Piłat
     */
    public Bike getSelectedBike() {
        return selectedBikes.isEmpty() ? null : selectedBikes.get(0);
    }
}
