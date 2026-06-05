package AI2.View.Rent;

import AI2.Service.RentService;
import AI2.Util.LanguageManager;
import AI2.View.Components.AppButton;
import AI2.View.Components.SearchPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RentPanel extends JPanel {

    private final RentService rentService;

    private JTable rentTable;

    private JTextField searchField;

    private JButton addButton;

    private JButton editButton;

    private JButton endButton;

    private JButton deleteButton;

    public RentPanel(RentService rentService) {

        this.rentService = rentService;

        initializeComponents();

        buildLayout();

        registerListeners();
    }

    private void initializeComponents() {

        rentTable = new JTable(
                new Object[][]{
                        {1, "Jan Kowalski", "Kross Hexagon", "2025-06-01 10:00", "2025-06-05 18:00", "ACTIVE"},
                        {2, "Anna Nowak", "Trek Marlin", "2025-06-02 09:00", "2025-06-04 16:00", "ACTIVE"},
                        {3, "Piotr Wiśniewski", "Merida Big Nine", "2025-06-03 12:30", "2025-06-10 12:00", "OVERDUE"},
                        {4, "Maria Zielińska", "Scott Aspect", "2025-06-04 08:00", "2025-06-07 15:00", "FINISHED"},
                        {5, "Tomasz Kowalczyk", "Cube Aim", "2025-06-05 14:00", "2025-06-08 20:00", "ACTIVE"}
                },
                new String[]{
                        "ID",
                        "Client",
                        "Bike",
                        "Rent Date",
                        "Return Date",
                        "Status"
                }
        );

        rentTable.setRowHeight(35);

        rentTable.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        rentTable.getTableHeader().setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

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

        endButton =
                new AppButton(
                        LanguageManager.getString(
                                "button.end"
                        )
                );

        deleteButton =
                new AppButton(
                        LanguageManager.getString(
                                "button.delete"
                        )
                );

        editButton.setEnabled(false);

        endButton.setEnabled(false);

        deleteButton.setEnabled(false);
    }

    private void buildLayout() {

        setLayout(
                new BorderLayout(
                        15,
                        15
                )
        );

        setBackground(
                Color.WHITE
        );

        setBorder(
                BorderFactory.createEmptyBorder(
                        20,
                        20,
                        20,
                        20
                )
        );

        JLabel title =
                new JLabel(
                        LanguageManager.getString(
                                "rent.management"
                        ),
                        SwingConstants.CENTER
                );

        title.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        24
                )
        );

        add(
                title,
                BorderLayout.NORTH
        );

        JPanel centerPanel =
                new JPanel(
                        new BorderLayout(
                                10,
                                10
                        )
                );

        centerPanel.setBackground(
                Color.WHITE
        );

        SearchPanel searchPanel =
                new SearchPanel();

        searchField =
                searchPanel.getSearchField();

        centerPanel.add(
                searchPanel,
                BorderLayout.NORTH
        );

        JScrollPane scrollPane =
                new JScrollPane(
                        rentTable
                );

        centerPanel.add(
                scrollPane,
                BorderLayout.CENTER
        );

        add(
                centerPanel,
                BorderLayout.CENTER
        );

        JPanel buttonPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                10,
                                0
                        )
                );

        buttonPanel.setBackground(
                Color.WHITE
        );

        buttonPanel.add(addButton);

        buttonPanel.add(editButton);

        buttonPanel.add(endButton);

        buttonPanel.add(deleteButton);

        add(
                buttonPanel,
                BorderLayout.SOUTH
        );
    }

    private void registerListeners() {

        rentTable.getSelectionModel()
                .addListSelectionListener(e -> {

                    if (!e.getValueIsAdjusting()) {

                        boolean selected =
                                rentTable.getSelectedRow() != -1;

                        editButton.setEnabled(
                                selected
                        );

                        endButton.setEnabled(
                                selected
                        );

                        deleteButton.setEnabled(
                                selected
                        );
                    }
                });

        rentTable.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            MouseEvent e) {

                        if (e.getClickCount() == 2) {

                            editButton.doClick();
                        }
                    }
                }
        );

        addButton.addActionListener(e -> {

            JDialog dialog =
                    new JDialog(
                            (Frame)
                                    SwingUtilities
                                            .getWindowAncestor(
                                                    this
                                            ),
                            LanguageManager.getString(
                                    "rent.nameAdd"
                            ),
                            true
                    );

            dialog.add(
                    new AddRentPanel(
                            rentService
                    )
            );

            dialog.setSize(
                    550,
                    400
            );

            dialog.setResizable(
                    false
            );

            dialog.setLocationRelativeTo(
                    this
            );

            dialog.setVisible(
                    true
            );
        });

        editButton.addActionListener(e -> {

            int selectedRow =
                    rentTable.getSelectedRow();

            if (selectedRow == -1) {

                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Edit rent row: "
                            + selectedRow
            );
        });

        endButton.addActionListener(e -> {

            int selectedRow =
                    rentTable.getSelectedRow();

            if (selectedRow == -1) {

                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "End rent row: "
                            + selectedRow
            );
        });

        deleteButton.addActionListener(e -> {

            int selectedRow =
                    rentTable.getSelectedRow();

            if (selectedRow == -1) {

                return;
            }

            int result =
                    JOptionPane.showConfirmDialog(
                            this,
                            LanguageManager.getString(
                                    "rent.deleteConfirm"
                            ),
                            LanguageManager.getString(
                                    "button.delete"
                            ),
                            JOptionPane.YES_NO_OPTION
                    );

            if (result == JOptionPane.YES_OPTION) {

                JOptionPane.showMessageDialog(
                        this,
                        "Delete rent row: "
                                + selectedRow
                );
            }
        });
    }

    public JTable getRentTable() {

        return rentTable;
    }

    public JTextField getSearchField() {

        return searchField;
    }
}