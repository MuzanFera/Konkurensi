import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    
    // DataModel: Kelas yang berfungsi untuk menyimpan dan mengelola data
    static class DataModel extends AbstractTableModel {
        private List<String> data;

        public DataModel() {
            data = new ArrayList<>();
            // Simulasi data awal
            for (int i = 1; i <= 100; i++) {
                data.add("Item " + i);
            }
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public int getColumnCount() {
            return 1; // Hanya satu kolom (Nama Item)
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return data.get(rowIndex);
        }

        public void addData(String item) {
            data.add(item);
            fireTableRowsInserted(data.size() - 1, data.size() - 1);
        }
    }

    // DataView: Kelas untuk mengatur tampilan GUI
    static class DataView {
        private JFrame frame;
        private JTable table;
        private JButton loadButton;
        private JProgressBar progressBar;
        private JLabel statusLabel;

        public DataView(DataModel model) {
            frame = new JFrame("Aplikasi MVC dengan SwingWorker");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            frame.setLayout(new BorderLayout());

            table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);
            frame.add(scrollPane, BorderLayout.CENTER);

            loadButton = new JButton("Muati Data");
            progressBar = new JProgressBar(0, 100);
            statusLabel = new JLabel("Siap untuk memuat data", JLabel.CENTER);

            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.add(loadButton, BorderLayout.CENTER);
            bottomPanel.add(progressBar, BorderLayout.NORTH);
            bottomPanel.add(statusLabel, BorderLayout.SOUTH);

            frame.add(bottomPanel, BorderLayout.SOUTH);
            frame.setLocationRelativeTo(null); // Center frame
        }

        public JFrame getFrame() {
            return frame;
        }

        public JTable getTable() {
            return table;
        }

        public JButton getLoadButton() {
            return loadButton;
        }

        public JProgressBar getProgressBar() {
            return progressBar;
        }

        public JLabel getStatusLabel() {
            return statusLabel;
        }
    }

    // DataController: Kelas untuk mengontrol logika aplikasi
    static class DataController {
        private DataModel model;
        private DataView view;

        public DataController(DataModel model, DataView view) {
            this.model = model;
            this.view = view;

            // Menambahkan aksi untuk tombol "Muati Data"
            view.getLoadButton().addActionListener(e -> startLoadingData());
        }

        private void startLoadingData() {
            view.getLoadButton().setEnabled(false); // Nonaktifkan tombol saat memuat
            view.getStatusLabel().setText("Memuat data...");
            view.getProgressBar().setValue(0);

            // Gunakan SwingWorker untuk melakukan pekerjaan berat secara terpisah
            SwingWorker<Void, Integer> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    // Simulasi pemrosesan data
                    for (int i = 0; i < 100; i++) {
                        Thread.sleep(50);  // Simulasi penundaan
                        model.addData("Item " + (i + 101));  // Menambah data baru
                        publish(i + 1);  // Memperbarui progress
                    }
                    return null;
                }

                @Override
                protected void process(List<Integer> chunks) {
                    // Memperbarui progress bar
                    int latestProgress = chunks.get(chunks.size() - 1);
                    view.getProgressBar().setValue(latestProgress);
                }

                @Override
                protected void done() {
                    // Aksi setelah selesai
                    view.getLoadButton().setEnabled(true);
                    view.getStatusLabel().setText("Data selesai dimuat!");
                }
            };

            worker.execute();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Inisialisasi Model, View, dan Controller
            DataModel model = new DataModel();
            DataView view = new DataView(model);
            DataController controller = new DataController(model, view);

            // Tampilkan View
            view.getFrame().setVisible(true);
        });
    }
}
