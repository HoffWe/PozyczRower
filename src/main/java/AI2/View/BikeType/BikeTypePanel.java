package AI2.View.BikeType;

import AI2.Service.BikeTypeService; // Twój serwis napisany wcześniej
import AI2.Util.LanguageManager;
import AI2.View.Components.AppButton;
import AI2.View.Components.SearchPanel;

import javax.swing.*;
import java.awt.*;
import javax.swing.*;

public class BikeTypePanel extends JPanel {

    private final BikeTypeService bikeTypeService;

    /**Tabela z typami rowerów*/
    private JTable bikeTypeTable;

    /**Pole wyszukiwania*/
    private JTextField searchField;

    /**Przyciski stwórz, edytuj i usuń*/
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;


    /**Konstruktor przyjmuje mój serwis typów rowerów*/
    public BikeTypePanel(BikeTypeService bikeTypeService) {
        this.bikeTypeService = bikeTypeService;

        initializeCompoments();
        buildLayout();
        registerListeners();

    }

    /**Inicjalizacja komponentów we froncie*/
    private void initializeCompoments() {

        //tabela z przykładowymi opisami (póki co hardcoded)
        bikeTypeTable = new JTable(

                new Object[][]{
                        {1, "Górski", "Rower z grubymi oponami na trudny teren"},
                        {2, "Miejski", "Wygodny rower z koszykiem do miasta"},
                        {3, "Szosowy", "Szybki, lekki rower na asfalt"}
                },

                new String[]{
                        "ID",
                        "Name",
                        "Description"
                }
        ){
            @Override
            /**Metoda blokująca edycję wszystkich komórek w tableli (double-click)*/
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false; //zwracamy false dla edytowania każdej komórki
            }
        };

        //stylizacja tabeli
        bikeTypeTable.setRowHeight(35);

        bikeTypeTable.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        bikeTypeTable.getTableHeader().setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

        //Inicjalizacja przycisków
        addButton =
                new AppButton(
                        LanguageManager.getString(
                                "button.add"
                        )
                );

        editButton =
                new AppButton(
                        LanguageManager.getString(
                                "button.edit"
                        )
                );

        deleteButton =
                new AppButton(
                        LanguageManager.getString(
                                "button.delete"
                        )
                );

        // Skoro na starcie żaden wiersz w tabeli nie jest zaznaczony,
        // to nie możemy niczego edytować ani usunąć. Blokujemy te przyciski.
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);


    }

    /**Układanie komponentów*/
    private void buildLayout() {

        //Układ i kolor tła
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);

        //Marginesy (od krawędzi okna)
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));


        // --------------------------------------------------------
        // 1. TYTUŁ NA GÓRZE
        // --------------------------------------------------------

        JLabel title = new JLabel(
            LanguageManager.getString("bikeType.management"),
                SwingConstants.CENTER
        );

        title.setFont(
                new Font("Segoe UI", Font.BOLD, 24)
        );
        add(title, BorderLayout.NORTH);


        // --------------------------------------------------------
        // 2. ŚRODKOWA SEKCJA (WYSZUKIWARKA + TABELA)
        // --------------------------------------------------------

        JPanel centerPanel = new JPanel(
                new BorderLayout(10, 10)
        );
        centerPanel.setBackground(Color.WHITE);

        //Wyszukiwarka
        SearchPanel searchPanel = new SearchPanel();
        searchField = searchPanel.getSearchField();
        centerPanel.add(searchPanel, BorderLayout.CENTER);

        //Tabela (wrzucamy tabelę do JScrollPane aby wyświetlała)
        JScrollPane scrollPane = new JScrollPane(bikeTypeTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        //wrzucamy środkowy panel na główny panel
        add(centerPanel, BorderLayout.CENTER);


        // --------------------------------------------------------
        // 3. DOLNA SEKCJA (PRZYCISKI)
        // --------------------------------------------------------

        //FlowLayout: układ elementów na panelu od lewej do prawej
        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 0)
        );
        buttonPanel.setBackground(Color.WHITE);

        //Kładziemy przyciski na panelu button
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        //Wrzucamy pasek z przyciskami na główny panel na sam dół
        add(buttonPanel, BorderLayout.SOUTH);

    }

    /**Podpięcie akcji pod przyciski*/
    private void registerListeners() {

        // --------------------------------------------------------
        // 1. NASŁUCHIWANIE TABELI (Odblokowywanie przycisków)
        // --------------------------------------------------------

        bikeTypeTable.getSelectionModel().addListSelectionListener(e -> {
            // e.getValueIsAdjusting() ignoruje "pośrednie" kliknięcia podczas przeciągania myszką
             if(!e.getValueIsAdjusting()) {

                 //-1 oznacza brak oznaczenia
                 boolean isRowSelected = bikeTypeTable.getSelectedRow() != -1;

                 editButton.setEnabled(isRowSelected);
                 deleteButton.setEnabled(isRowSelected);
             }
        });


        // --------------------------------------------------------
        // 2. PODWÓJNE KLIKNIĘCIE W WIERSZ (Szybka edycja)
        // --------------------------------------------------------

        bikeTypeTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {

                if(e.getClickCount() == 2) {
                    editButton.doClick();
                }

            }
        });



        // --------------------------------------------------------
        // 3. AKCJA: PRZYCISK DODAJ
        // --------------------------------------------------------

        addButton.addActionListener(e -> {

            JDialog dialog = new JDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    LanguageManager.getString("bikeType.nameAdd"),
                    true //oznacza, że okno jest modalne (blokuje klikanie w aplikację w tle)
            );

            dialog.add(new AddBikeTypePanel(bikeTypeService));

            dialog.setSize(500, 250);
            dialog.setResizable(false);
            dialog.setLocationRelativeTo(this); //okienko pojawi sue na srodku

            //pokazujemy okno
            dialog.setVisible(true);
        });



        // --------------------------------------------------------
        // 3. AKCJA: PRZYCISK EDYTUJ
        // --------------------------------------------------------

        editButton.addActionListener(e -> {

            int selectedRow = bikeTypeTable.getSelectedRow();
            int selectedRows = bikeTypeTable.getSelectedRows().length;

                //if jako zabezpieczenie przed wybraniem dwoch wierszy do edycji
            if(selectedRows > 1) {
                JOptionPane.showMessageDialog(this,"Zaznacz tylko jedną pozycję do edycji typu roweru!");
            }
            else if(selectedRows == 1) {
                Object id = bikeTypeTable.getValueAt(selectedRow, 0);
                String name = bikeTypeTable.getValueAt(selectedRow, 1).toString();

                JOptionPane.showMessageDialog(this, "Edytujemy typ roweru o ID: " + id + " (" + name + ")");
            }

        });

        // --------------------------------------------------------
        // 5. AKCJA: PRZYCISK USUŃ
        // --------------------------------------------------------

        deleteButton.addActionListener(e -> {

            int selectedRow = bikeTypeTable.getSelectedRow();
            int selectedRows = bikeTypeTable.getSelectedRows().length;

            //tutaj pomyśleć o usunięciu kilku rowerów jednocześnie
            if(selectedRows > 1) {
                JOptionPane.showMessageDialog(this,"Zaznacz tylko jedną pozycję do usuwania typu roweru!");
            }

            else if(selectedRows == 1) {
                int result = JOptionPane.showConfirmDialog(
                        this,
                        "Cay aby na pewno chcesz usunąć wybrany typ roweru?",
                        LanguageManager.getString("button.delete"),
                        JOptionPane.YES_NO_OPTION
                );

                if (result == JOptionPane.YES_OPTION) {
                    Object id = bikeTypeTable.getValueAt(selectedRow, 0);
                    JOptionPane.showMessageDialog(this, "Fizyczne usuwanie roweru o ID: " + id);
                }
            }



        });

    }

}
