package tela_main_controller;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;
import javafx.util.Duration;
import javafx.scene.control.Label;
import java.util.Locale;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 🚀 MainLayoutController
 * Controla o menu lateral e troca dinamicamente as telas no painel central.
 */
public class MainLayoutController {

    @FXML
    private Label labelRelogio;

    @FXML
    private Label labelEstacao;

    // Onde as telas vão ser carregadas
    @FXML
    private StackPane painelConteudo;

    private final DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("dd MMMM, HH:mm", Locale.forLanguageTag("pt-BR"));

    @FXML
    public void initialize() {
        iniciarRelogio();
        abrirDashboard(); // abre o dashboard logo no início
    }

    private void iniciarRelogio() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0), e -> {
            LocalDateTime agora = LocalDateTime.now();
            String textoFormatado = formatter.format(agora);
            String textoComMesMaiusculo = capitalizarMes(textoFormatado);

            labelRelogio.setText(textoComMesMaiusculo);
            labelEstacao.setText(obterEstacao(agora.getMonthValue()));
        }), new KeyFrame(Duration.seconds(60)));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private String capitalizarMes(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        String[] partes = texto.split(" ");
        if (partes.length < 2) return texto;

        String mes = partes[1].replace(",", "");
        mes = mes.substring(0, 1).toUpperCase() + mes.substring(1);
        int indexVirgula = texto.indexOf(",");
        String resto = indexVirgula != -1 ? texto.substring(indexVirgula) : "";

        return partes[0] + " " + mes + resto;
    }

    private String obterEstacao(int mes) {
        if (mes == 12 || mes <= 2) {
            return "Verão – Flores desabrocham em cores vibrantes, atraindo polinizadores.";
        } else if (mes >= 3 && mes <= 5) {
            return "Outono – Plantas se preparam para o repouso, com poda e limpeza.";
        } else if (mes >= 6 && mes <= 8) {
            return "Inverno – Estufas protegem as mudas do frio, garantindo crescimento seguro.";
        } else if (mes >= 9 && mes <= 11) {
            return "Primavera – Brotos novos surgem, anunciando o ciclo da vida.";
        }
        return "";
    }

    // ========== Botões do menu ==========
    @FXML
    public void abrirDashboard() {
        carregarTela("/telas/view/TelaDashboard.fxml");
    }

    @FXML
    public void abrirListaColmeia() {
        carregarTela("/telas/view/TelaListaColmeia.fxml");
    }

    @FXML
    public void abrirRelatorios() {
        carregarTela("/telas/view/TelaClientes.fxml"); // relatório de clientes
    }

    @FXML
public void abrirListaClientes() {
    carregarTela("/telas/view/TelaListaCliente.fxml");
}

    // ========== Funções auxiliares ==========
    private void carregarTela(String caminho) {
        try {
            Node tela = FXMLLoader.load(getClass().getResource(caminho));
            tela.setOpacity(0);
            painelConteudo.getChildren().setAll(tela);

            FadeTransition fade = new FadeTransition(Duration.millis(900), tela);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void sair() {
        Platform.exit();
    }
}
