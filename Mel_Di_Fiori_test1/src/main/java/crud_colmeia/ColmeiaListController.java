package crud_colmeia;

import dao.DAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import model.Colmeia;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class ColmeiaListController {

    @FXML private TableView<Colmeia> tableColmeias;
    @FXML private TableColumn<Colmeia, Long> colId;
    @FXML private TableColumn<Colmeia, String> colIdentificacao;
    @FXML private TableColumn<Colmeia, String> colLocalizacao;
    @FXML private TableColumn<Colmeia, String> colTipo;
    @FXML private TableColumn<Colmeia, String> colStatus;
    @FXML private TableColumn<Colmeia, String> colData;
    @FXML private TableColumn<Colmeia, Integer> colQuadros;
    @FXML private Label lblTotalPlantas;
    @FXML private Label lblPlantasAtivas;

    private final ObservableList<Colmeia> dados = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarColunas();
        configurarEstilosTabela();
        carregarColmeias();
        atualizarEstatisticas();
    }

    private void configurarColunas() {
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getId()));
        colIdentificacao.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getIdentificacao()));
        colLocalizacao.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getLocalizacao()));
        colTipo.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTipo()));
        colStatus.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus()));

        colData.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(
                c.getValue().getDataInstalacao() != null
                    ? c.getValue().getDataInstalacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    : "Não definida"
            )
        );

        colQuadros.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getNumeroQuadros()));

        // Ajuste de largura das colunas
        colId.setPrefWidth(70);
        colIdentificacao.setPrefWidth(150);
        colLocalizacao.setPrefWidth(150);
        colTipo.setPrefWidth(120);
        colStatus.setPrefWidth(100);
        colData.setPrefWidth(130);
        colQuadros.setPrefWidth(100);
    }

    private void configurarEstilosTabela() {
        // Estilos já aplicados via CSS
    }

    private void carregarColmeias() {
        List<Colmeia> lista = new DAO<>(Colmeia.class).obterTodos(100, 0);
        dados.setAll(lista);
        tableColmeias.setItems(dados);
        atualizarEstatisticas();
    }

    private void atualizarEstatisticas() {
        int total = dados.size();
        int ativas = (int) dados.stream()
                .filter(colmeia -> "Ativa".equals(colmeia.getStatus()))
                .count();
        
        lblTotalPlantas.setText(String.valueOf(total));
        lblPlantasAtivas.setText(String.valueOf(ativas));
    }

    @FXML
    private void abrirCadastro() {
        carregarTelaCadastro(null);
    }

    @FXML
    private void editarColmeia() {
        Colmeia selecionada = tableColmeias.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mostrarAlerta("Atenção", "Seleção Necessária", 
                "Selecione uma planta para editar.", Alert.AlertType.WARNING);
            return;
        }
        carregarTelaCadastro(selecionada);
    }

    private void carregarTelaCadastro(Colmeia colmeia) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/telas/view/TelaCadastroColmeia.fxml"));
            Node tela = loader.load();
            
            ColmeiaCreateController controller = loader.getController();
            
            StackPane painel = (StackPane) tableColmeias.getScene().lookup("#painelConteudo");
            
            if (colmeia != null) {
                controller.setColmeiaParaEdicao(colmeia, painel);
            } else {
                controller.setPainelConteudo(painel);
            }
            
            painel.getChildren().setAll(tela);
            
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Falha ao Carregar Tela", 
                "Erro: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void excluirColmeia() {
        Colmeia selecionada = tableColmeias.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mostrarAlerta("Atenção", "Seleção Necessária", 
                "Selecione uma planta para excluir.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmação de Exclusão");
        confirmacao.setHeaderText("Excluir Planta");
        confirmacao.setContentText("Tem certeza que deseja excluir a planta \"" + 
            selecionada.getIdentificacao() + "\"?\nEsta ação não pode ser desfeita.");
        
        ButtonType btnSim = new ButtonType("Sim, Excluir", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnNao = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmacao.getButtonTypes().setAll(btnSim, btnNao);

        DialogPane dialogPane = confirmacao.getDialogPane();
        dialogPane.getStyleClass().add("dialog-pane");

        Optional<ButtonType> resultado = confirmacao.showAndWait();
        
        if (resultado.isPresent() && resultado.get() == btnSim) {
            try {
                new DAO<>(Colmeia.class).removerPorIdTransacional(selecionada.getId());
                carregarColmeias();
                mostrarAlerta("Sucesso", "Planta Excluída", 
                    "Planta excluída com sucesso!", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta("Erro", "Falha na Exclusão", 
                    "Erro ao excluir planta: " + e.getMessage(), Alert.AlertType.ERROR);
            }
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
}