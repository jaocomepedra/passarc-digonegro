package tela_main_controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DashboardController {

    @FXML private Label totalPlantasLabel;
    @FXML private Label producaoLabel;
    @FXML private Label vendasLabel;
    @FXML private Label topVendaLabel;

    @FXML private LineChart<String, Number> graficoVendas;
    @FXML private BarChart<String, Number> graficoProducao;

    @FXML private VBox itensPlantas;

    private final String[] months = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun"};

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            int total = safeParseNumber(getTextOrEmpty(totalPlantasLabel));
            int prod  = safeParseNumber(getTextOrEmpty(producaoLabel));
            int vendas = safeParseNumber(getTextOrEmpty(vendasLabel));

            populateLineChartVendas(vendas);
            populateBarChartProducao(prod);
            adjustYAxisIfNumberAxis(Math.max(Math.max(total, prod), vendas));

            setPlantasNaoRegadas(samplePlantasNaoRegadas());

            // ✅ Corrige cores (sem laranja)
            applyChartColors();
        });
    }

    // ----------------- Gráficos -----------------

    private void populateLineChartVendas(int vendasFinal) {
        graficoVendas.getData().clear();
        XYChart.Series<String, Number> s = new XYChart.Series<>();
        s.setName("Vendas");

        int steps = months.length;
        int step = Math.max(1, Math.round(vendasFinal / (float)Math.max(1, steps)));

        for (int i = 0; i < months.length; i++) {
            int val = Math.max(0, vendasFinal - (months.length - 1 - i) * step);
            s.getData().add(new XYChart.Data<>(months[i], val));
        }
        graficoVendas.getData().add(s);
    }

    private void populateBarChartProducao(int prodFinal) {
        graficoProducao.getData().clear();
        XYChart.Series<String, Number> s = new XYChart.Series<>();
        s.setName("Produção");

        int steps = months.length;
        int step = Math.max(1, Math.round(prodFinal / (float)Math.max(1, steps)));

        for (int i = 0; i < months.length; i++) {
            int val = Math.max(0, prodFinal - (months.length - 1 - i) * step);
            s.getData().add(new XYChart.Data<>(months[i], val));
        }
        graficoProducao.getData().add(s);
    }

    private void adjustYAxisIfNumberAxis(int maxValue) {
        if (graficoVendas.getYAxis() instanceof NumberAxis) {
            NumberAxis y = (NumberAxis) graficoVendas.getYAxis();
            double upper = Math.max(10, maxValue * 1.2);
            y.setAutoRanging(false);
            y.setLowerBound(0);
            y.setUpperBound(Math.ceil(upper));
            y.setTickUnit(Math.max(1, Math.ceil(upper / 5.0)));
        }
    }

    private void applyChartColors() {
        // Linha verde
        graficoVendas.lookupAll(".default-color0.chart-series-line")
                .forEach(n -> n.setStyle("-fx-stroke: #388E3C; -fx-stroke-width: 2px;"));
        graficoVendas.lookupAll(".default-color0.chart-line-symbol")
                .forEach(n -> n.setStyle("-fx-background-color: #388E3C, white;"));

        // Barras verde claro
        graficoProducao.lookupAll(".default-color0.chart-bar")
                .forEach(n -> n.setStyle("-fx-bar-fill: #66BB6A;"));
    }

    // ----------------- Lista "Plantas não regadas" -----------------

    public void setPlantasNaoRegadas(List<Plant> plantas) {
        itensPlantas.getChildren().clear();
        if (plantas == null || plantas.isEmpty()) {
            Label vazio = new Label("Nenhuma planta pendente");
            vazio.setStyle("-fx-text-fill: rgba(255,255,255,0.85); -fx-font-size: 13px;");
            itensPlantas.getChildren().add(vazio);
            return;
        }

        for (Plant p : plantas) {
            itensPlantas.getChildren().add(createPlantNode(p));
        }
    }

    private HBox createPlantNode(Plant p) {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setStyle(
            "-fx-background-color: rgba(255,255,255,0.06);" +
            "-fx-padding: 10;" +
            "-fx-background-radius: 8;" +
            "-fx-border-radius: 8;" +
            "-fx-border-color: rgba(255,255,255,0.06);" +
            "-fx-border-width: 1;"
        );

        String dotColor = severityColorForDays(p.daysSinceWatered);

        Label dot = new Label("●");
        dot.setStyle("-fx-font-size: 12px; -fx-text-fill: " + dotColor + ";");

        String nameText = p.name + (p.location != null && !p.location.isEmpty() ? " — " + p.location : "");
        Label name = new Label(nameText);
        name.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 13px; -fx-font-weight: 600;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label days = new Label(p.daysSinceWatered + (p.daysSinceWatered == 1 ? " dia" : " dias"));
        days.setStyle("-fx-text-fill: rgba(255,255,255,0.85); -fx-font-size: 11px; -fx-font-weight: 500;");

        box.getChildren().addAll(dot, name, spacer, days);
        return box;
    }

    private String severityColorForDays(int days) {
        if (days >= 5) return "#ef5350"; // vermelho
        else if (days >= 3) return "#81C784"; // verde médio
        else return "#C8E6C9"; // verde claro
    }

    private List<Plant> samplePlantasNaoRegadas() {
        return Arrays.asList(
            new Plant("Rosa", "vaso 3L", 2),
            new Plant("Samambaia", "canteiro", 1),
            new Plant("Cacto", "vaso 1L", 5),
            new Plant("Manjericão", "vaso 2L", 3),
            new Plant("Eucalipto", "mudas", 4)
        );
    }

    private String getTextOrEmpty(Label l) {
        return l == null || l.getText() == null ? "" : l.getText();
    }

    private int safeParseNumber(String text) {
        if (text == null) return 0;
        try {
            Matcher m = Pattern.compile("(\\d+)").matcher(text);
            if (m.find()) return Integer.parseInt(m.group(1));
        } catch (Exception ignored) {}
        return 0;
    }

    public static class Plant {
        public final String name;
        public final String location;
        public final int daysSinceWatered;

        public Plant(String name, String location, int daysSinceWatered) {
            this.name = name;
            this.location = location;
            this.daysSinceWatered = Math.max(0, daysSinceWatered);
        }
    }
}
