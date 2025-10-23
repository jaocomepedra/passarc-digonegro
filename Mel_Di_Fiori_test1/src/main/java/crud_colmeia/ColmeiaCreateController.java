package crud_colmeia;

import dao.DAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import model.Colmeia;

import java.time.LocalDate;

public class ColmeiaCreateController {

    @FXML private TextField txtNumero;
    @FXML private DatePicker dateInstalacao;
    @FXML private TextField txtLocalizacao;
    @FXML private ComboBox<String> comboSituacao;
    @FXML private ComboBox<String> comboTipo;
    @FXML private Spinner<Integer> spinnerNumeroQuadros;
    @FXML private TextArea txtObservacoes;

    private Colmeia colmeiaEditando;
    private StackPane painelConteudo;

    public void setColmeiaParaEdicao(Colmeia colmeia, StackPane painelConteudo) {
        this.colmeiaEditando = colmeia;
        this.painelConteudo = painelConteudo;
        preencherCampos();
    }

    public void setPainelConteudo(StackPane painelConteudo) {
        this.painelConteudo = painelConteudo;
    }

    private void preencherCampos() {
        if (colmeiaEditando != null) {
            txtNumero.setText(colmeiaEditando.getIdentificacao());
            dateInstalacao.setValue(colmeiaEditando.getDataInstalacao());
            txtLocalizacao.setText(colmeiaEditando.getLocalizacao());
            comboSituacao.setValue(colmeiaEditando.getStatus());
            comboTipo.setValue(colmeiaEditando.getTipo());
            spinnerNumeroQuadros.getValueFactory().setValue(colmeiaEditando.getNumeroQuadros());
            txtObservacoes.setText(colmeiaEditando.getObservacoes());
        }
    }

    @FXML
    public void initialize() {
        comboSituacao.getItems().addAll("Ativa", "Inativa");
        comboTipo.getItems().addAll("Terra", "Agua", "Outras Bases");
        
        SpinnerValueFactory<Integer> valueFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 10);
        spinnerNumeroQuadros.setValueFactory(valueFactory);

        aplicarEstilos();
    }

    private void aplicarEstilos() {
        // Estilos agora aplicados via CSS
        txtNumero.getStyleClass().add("form-field");
        txtLocalizacao.getStyleClass().add("form-field");
        comboTipo.getStyleClass().add("form-field");
        comboSituacao.getStyleClass().add("form-field");
        dateInstalacao.getStyleClass().add("form-field");
        spinnerNumeroQuadros.getStyleClass().add("form-field");
        txtObservacoes.getStyleClass().add("form-field");
    }

    private void limparEstiloErro() {
        aplicarEstilos();
    }

    private void colocarBordaVermelha(Control campo) {
        campo.getStyleClass().remove("form-field");
        campo.getStyleClass().add("error-field");
    }

    private boolean validarCamposComVisual() {
        limparEstiloErro();
        boolean valido = true;

        if (txtNumero.getText() == null || txtNumero.getText().trim().isEmpty()) {
            colocarBordaVermelha(txtNumero);
            valido = false;
        }
        if (txtLocalizacao.getText() == null || txtLocalizacao.getText().trim().isEmpty()) {
            colocarBordaVermelha(txtLocalizacao);
            valido = false;
        }
        if (comboTipo.getValue() == null) {
            colocarBordaVermelha(comboTipo);
            valido = false;
        }
        if (comboSituacao.getValue() == null) {
            colocarBordaVermelha(comboSituacao);
            valido = false;
        }
        if (dateInstalacao.getValue() == null) {
            colocarBordaVermelha(dateInstalacao);
            valido = false;
        }

        return valido;
    }

    @FXML
    private void salvarColmeia() {
        try {
            if (!validarCamposComVisual()) {
                mostrarAlerta("Atenção", "Campos Obrigatórios", 
                    "Preencha todos os campos obrigatórios destacados.", Alert.AlertType.WARNING);
                return;
            }

            String numero = txtNumero.getText();
            LocalDate data = dateInstalacao.getValue();
            String local = txtLocalizacao.getText();
            String situacao = comboSituacao.getValue();
            String tipo = comboTipo.getValue();
            int numeroQuadros = spinnerNumeroQuadros.getValue();
            String obs = txtObservacoes.getText();

            if (colmeiaEditando == null) {
                Colmeia nova = new Colmeia(numero, local, tipo, situacao, data, numeroQuadros, obs);
                new DAO<>(Colmeia.class).incluirTransacional(nova);
                mostrarAlerta("Sucesso", "Planta Cadastrada", 
                    "Planta salva com sucesso!", Alert.AlertType.INFORMATION);
            } else {
                colmeiaEditando.setIdentificacao(numero);
                colmeiaEditando.setLocalizacao(local);
                colmeiaEditando.setTipo(tipo);
                colmeiaEditando.setStatus(situacao);
                colmeiaEditando.setDataInstalacao(data);
                colmeiaEditando.setNumeroQuadros(numeroQuadros);
                colmeiaEditando.setObservacoes(obs);

                new DAO<>(Colmeia.class).atualizarTransacional(colmeiaEditando);
                mostrarAlerta("Sucesso", "Planta Atualizada", 
                    "Planta atualizada com sucesso!", Alert.AlertType.INFORMATION);
            }

            voltarParaLista();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Falha na Operação", 
                "Erro: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void voltarParaLista() {
        try {
            if (painelConteudo != null) {
                Node telaLista = FXMLLoader.load(getClass().getResource("/telas/view/TelaListaColmeia.fxml"));
                painelConteudo.getChildren().setAll(telaLista);
            } else {
                Node node = txtNumero.getScene().lookup("#painelConteudo");
                if (node instanceof StackPane) {
                    StackPane painel = (StackPane) node;
                    Node telaLista = FXMLLoader.load(getClass().getResource("/telas/view/TelaListaColmeia.fxml"));
                    painel.getChildren().setAll(telaLista);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Falha ao Voltar", 
                "Erro ao carregar a lista: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String cabecalho, String conteudo, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(cabecalho);
        alerta.setContentText(conteudo);
        
        DialogPane dialogPane = alerta.getDialogPane();
        dialogPane.getStyleClass().add("dialog-pane");
        
        alerta.showAndWait();
    }

    @FXML
    private void limparCampos() {
        txtNumero.clear();
        dateInstalacao.setValue(null);
        txtLocalizacao.clear();
        comboSituacao.setValue(null);
        comboTipo.setValue(null);
        spinnerNumeroQuadros.getValueFactory().setValue(10);
        txtObservacoes.clear();
        colmeiaEditando = null;
        limparEstiloErro();
    }

    @FXML
    private void voltar() {
        voltarParaLista();
    }
}