package crud_cliente;

import dao.DAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import model.Cliente;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClienteListController {

    @FXML private TableView<Cliente> tableClientes;
    @FXML private TableColumn<Cliente, Long> colId;
    @FXML private TableColumn<Cliente, String> colNome;
    @FXML private TableColumn<Cliente, String> colEmail;
    @FXML private TableColumn<Cliente, String> colTelefone;
    @FXML private TableColumn<Cliente, String> colCidade;
    @FXML private TableColumn<Cliente, String> colEstado;
    @FXML private TableColumn<Cliente, String> colStatus;
    @FXML private TableColumn<Cliente, String> colDataCadastro;

    private final ObservableList<Cliente> dados = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarColunas();
        carregarClientes();
    }

    private void configurarColunas() {
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getId()));
        colNome.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNome()));
        colEmail.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEmail()));
        colTelefone.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTelefone()));
        colCidade.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getCidade()));
        colEstado.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEstado()));
        colStatus.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus()));
        
        colDataCadastro.setCellValueFactory(c -> 
            new javafx.beans.property.SimpleStringProperty(
                c.getValue().getDataCadastro() != null
                    ? c.getValue().getDataCadastro().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    : "Não definida"
            )
        );
    }

    private void carregarClientes() {
        try {
            List<Cliente> lista = new DAO<>(Cliente.class).obterTodos(100, 0);
            dados.setAll(lista);
            tableClientes.setItems(dados);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erro ao carregar clientes: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void abrirCadastro() {
        try {
            Node tela = FXMLLoader.load(getClass().getResource("/telas/view/TelaCadastroCliente.fxml"));
            StackPane painel = (StackPane) tableClientes.getScene().lookup("#painelConteudo");
            painel.getChildren().setAll(tela);
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erro ao carregar tela de cadastro").showAndWait();
        }
    }

    @FXML
    private void editarCliente() {
        Cliente selecionado = tableClientes.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            new Alert(Alert.AlertType.WARNING, "Selecione um cliente para editar.").showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/telas/view/TelaCadastroCliente.fxml"));
            Node tela = loader.load();
            
            // Obtém o controlador da tela de cadastro
            ClienteCreateController controller = loader.getController();
            
            // Configura o cliente para edição
            controller.setClienteParaEditar(selecionado);
            
            StackPane painel = (StackPane) tableClientes.getScene().lookup("#painelConteudo");
            painel.getChildren().setAll(tela);
            
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erro ao carregar tela de edição").showAndWait();
        }
    }   

    @FXML
    private void excluirCliente() {
        Cliente selecionado = tableClientes.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            new Alert(Alert.AlertType.WARNING, "Selecione um cliente para excluir.").showAndWait();
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Exclusão");
        confirmacao.setHeaderText("Excluir Cliente");
        confirmacao.setContentText("Tem certeza que deseja excluir o cliente: " + selecionado.getNome() + "?");
        
        if (confirmacao.showAndWait().get() == ButtonType.OK) {
            try {
                new DAO<>(Cliente.class).removerPorIdTransacional(selecionado.getId());
                carregarClientes();
                new Alert(Alert.AlertType.INFORMATION, "Cliente excluído com sucesso!").showAndWait();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Erro ao excluir cliente: " + e.getMessage()).showAndWait();
            }
        }
    }

    @FXML
    private void atualizarLista() {
        carregarClientes();
    }
}