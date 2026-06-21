package AI2.View.Client;

import AI2.Enums.RentStatus;
import AI2.Model.Bike;
import AI2.Model.BikeModel;
import AI2.Model.Client;
import AI2.Model.Rent;
import AI2.Service.BikeModelService;
import AI2.Service.BikeService;
import AI2.Service.RentService;
import AI2.Util.LanguageManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Okno dialogowe wyświetlające wypożyczenia danego klienta.
 * Domyślnie pokazuje tylko wypożyczenia niezakończone (SCHEDULED, ACTIVE, OVERDUE).
 * Checkbox "Pokaż zakończone" przełącza widoczność FINISHED i CLOSED.
 *
 * @author Tomasz Piłat
 */
public class ClientRentsDialog extends JDialog {

    /** Format daty używany w tabeli. */
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /** Statusy traktowane jako "zakończone". */
    private static final Set<RentStatus> FINISHED_STATUSES =
            Set.of(RentStatus.FINISHED, RentStatus.CLOSED);

    private final List<Rent>      allRents;
    private final BikeService     bikeService;
    private final BikeModelService bikeModelService;
    private final DefaultTableModel tableModel;

    /**
     * Indeksy wierszy odpowiadające wypożyczeniom OVERDUE w aktualnym widoku tabeli.
     * Używane przez renderer do kolorowania na czerwono.
     */
    private final List<Integer> overdueRows = new ArrayList<>();

    /**
     * Tworzy dialog z listą wypożyczeń klienta.
     *
     * @param owner            okno nadrzędne
     * @param client           klient, którego wypożyczenia wyświetlamy
     * @param rentService      serwis wypożyczeń
     * @param bikeService      serwis rowerów
     * @param bikeModelService serwis modeli rowerów
     * @author Tomasz Piłat
     */
    public ClientRentsDialog(Window owner, Client client,
                             RentService rentService,
                             BikeService bikeService,
                             BikeModelService bikeModelService) {
        super(owner,
              LanguageManager.getString("client.rents")
                      + ": " + client.getName() + " " + client.getSurname(),
              ModalityType.APPLICATION_MODAL);

        this.bikeService      = bikeService;
        this.bikeModelService = bikeModelService;
        this.allRents         = rentService.findClientRents(client.getId());

        tableModel = new DefaultTableModel(new String[]{
                LanguageManager.getString("bike.name"),
                LanguageManager.getString("date.startDate"),
                LanguageManager.getString("date.endDate"),
                LanguageManager.getString("rent.status.name")
        }, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Renderer kolorujący wiersze OVERDUE na czerwono
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        t, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setForeground(overdueRows.contains(row) ? Color.RED : t.getForeground());
                }
                return c;
            }
        });

        JCheckBox showFinishedBox = new JCheckBox(
                LanguageManager.getString("client.rents.showFinished"), false);
        showFinishedBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        showFinishedBox.setBackground(Color.WHITE);
        showFinishedBox.addActionListener(e -> rebuildTable(showFinishedBox.isSelected()));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(showFinishedBox);

        setLayout(new BorderLayout(8, 8));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(topPanel,               BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Załaduj dane bez zakończonych
        rebuildTable(false);

        setSize(680, 420);
        setResizable(true);
        setLocationRelativeTo(owner);
    }

    /**
     * Przebudowuje zawartość tabeli uwzględniając flagę widoczności zakończonych.
     *
     * @param showFinished {@code true} – pokaż wszystkie; {@code false} – tylko aktywne
     * @author Tomasz Piłat
     */
    private void rebuildTable(boolean showFinished) {
        tableModel.setRowCount(0);
        overdueRows.clear();

        List<Rent> visible = allRents.stream()
                .filter(r -> showFinished || !FINISHED_STATUSES.contains(r.getStatus()))
                .collect(Collectors.toList());

        int rowIdx = 0;
        for (Rent r : visible) {
            if (r.getStatus() == RentStatus.OVERDUE) overdueRows.add(rowIdx);
            String bikeInfo = "ID:" + r.getBikeId();
            try {
                Bike b = bikeService.getBikeById(r.getBikeId());
                if (b != null && bikeModelService != null) {
                    BikeModel bm = bikeModelService.getBikeModelById(b.getBikeModelId());
                    if (bm != null) {
                        bikeInfo = bm.getBrand() + " " + bm.getModel()
                                + "  " + b.getWheelSize() + "\"";
                    }
                }
            } catch (Exception ignored) { /* zostaje "ID:..." */ }

            tableModel.addRow(new Object[]{
                    bikeInfo,
                    r.getRentDate()   != null ? r.getRentDate().format(FMT)   : "-",
                    r.getReturnTime() != null ? r.getReturnTime().format(FMT) : "-",
                    r.getStatus()     != null ? r.getStatus().getDisplayName() : "-"
            });
            rowIdx++;
        }
    }
}
