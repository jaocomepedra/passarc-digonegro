package crud_cliente;

import dao.DAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import model.Cliente;

import java.io.IOException;

public class ClienteCreateController {

    @FXML private TextField txtNome;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefone;
    @FXML private TextField txtEndereco;
    @FXML private TextField txtCidade;
    @FXML private ComboBox<String> comboEstado;
    @FXML private ComboBox<String> comboStatus;

    private Cliente clienteEmEdicao;
    private boolean modoEdicao = false;

    @FXML
    public void initialize() {
        // Configurar estados brasileiros
        comboEstado.getItems().addAll(
            "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", 
            "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", 
            "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO"
        );
        
        comboStatus.getItems().addAll("ATIVO", "INATIVO");
        comboStatus.setValue("ATIVO");
    }

    public void setClienteParaEditar(Cliente cliente) {
        this.clienteEmEdicao = cliente;
        this.modoEdicao = true;
        preencherCamposComDadosCliente();
    }

    private void preencherCamposComDadosCliente() {
        if (clienteEmEdicao != null) {
            txtNome.setText(clienteEmEdicao.getNome());
            txtEmail.setText(clienteEmEdicao.getEmail());
            txtTelefone.setText(clienteEmEdicao.getTelefone());
            txtEndereco.setText(clienteEmEdicao.getEndereco() != null ? clienteEmEdicao.getEndereco() : "");
            txtCidade.setText(clienteEmEdicao.getCidade() != null ? clienteEmEdicao.getCidade() : "");
            
            if (clienteEmEdicao.getEstado() != null) {
                comboEstado.setValue(clienteEmEdicao.getEstado());
            }
            
            if (clienteEmEdicao.getStatus() != null) {
                comboStatus.setValue(clienteEmEdicao.getStatus());
            }
        }
    }

    private void limparEstiloErro() {
        limparBordaVermelha(txtNome);
        limparBordaVermelha(txtEmail);
        limparBordaVermelha(txtTelefone);
    }

    private void colocarBordaVermelha(Control campo) {
        campo.setStyle("-fx-border-color: red; -fx-border-width: 2;");
    }

    private void limparBordaVermelha(Control campo) {
        campo.setStyle("");
    }

    private boolean validarCampos() {
        limparEstiloErro();
        boolean valido = true;

        if (txtNome.getText() == null || txtNome.getText().trim().isEmpty()) {
            colocarBordaVermelha(txtNome);
            valido = false;
        }

        if (txtEmail.getText() == null || txtEmail.getText().trim().isEmpty() || 
            !txtEmail.getText().contains("@")) {
            colocarBordaVermelha(txtEmail);
            valido = false;
        }

        if (txtTelefone.getText() == null || txtTelefone.getText().trim().isEmpty()) {
            colocarBordaVermelha(txtTelefone);
            valido = false;
        }

        return valido;
    }

    @FXML
    private void salvarCliente() {
        try {
            if (!validarCampos()) {
                Alert alerta = new Alert(Alert.AlertType.WARNING);
                alerta.setTitle("Campos Obrigatórios");
                alerta.setHeaderText("Preencha os campos obrigatórios corretamente.");
                alerta.setContentText("Nome, E-mail e Telefone são obrigatórios.");
                alerta.showAndWait();
                return;
            }

            if (modoEdicao && clienteEmEdicao != null) {
                // Modo edição - atualizar cliente existente
                clienteEmEdicao.setNome(txtNome.getText());
                clienteEmEdicao.setEmail(txtEmail.getText());
                clienteEmEdicao.setTelefone(txtTelefone.getText());
                clienteEmEdicao.setEndereco(txtEndereco.getText());
                clienteEmEdicao.setCidade(txtCidade.getText());
                clienteEmEdicao.setEstado(comboEstado.getValue());
                clienteEmEdicao.setStatus(comboStatus.getValue());

                new DAO<>(Cliente.class).atualizarTransacional(clienteEmEdicao);

                Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                alerta.setTitle("Sucesso");
                alerta.setHeaderText("Cliente atualizado com sucesso!");
                alerta.showAndWait();
                
            } else {
                // Modo criação - novo cliente
                Cliente novoCliente = new Cliente(
                    txtNome.getText(),
                    txtEmail.getText(),
                    txtTelefone.getText(),
                    txtEndereco.getText(),
                    txtCidade.getText(),
                    comboEstado.getValue()
                );

                if (comboStatus.getValue() != null) {
                    novoCliente.setStatus(comboStatus.getValue());
                }

                new DAO<>(Cliente.class).incluirTransacional(novoCliente);

                Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                alerta.setTitle("Sucesso");
                alerta.setHeaderText("Cliente salvo com sucesso!");
                alerta.showAndWait();
            }

            voltarParaLista();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Erro");
            alerta.setHeaderText("Falha ao salvar cliente");
            alerta.setContentText("Erro: " + e.getMessage());
            alerta.showAndWait();
        }
    }

    @FXML
    private void limparCampos() {
        txtNome.clear();
        txtEmail.clear();
        txtTelefone.clear();
        txtEndereco.clear();
        txtCidade.clear();
        comboEstado.setValue(null);
        comboStatus.setValue("ATIVO");
        clienteEmEdicao = null;
        modoEdicao = false;
    }

    @FXML
    private void voltarParaLista() {
        try {
            Node tela = FXMLLoader.load(getClass().getResource("/telas/view/TelaListaCliente.fxml"));
            StackPane painel = (StackPane) txtNome.getScene().lookup("#painelConteudo");
            painel.getChildren().setAll(tela);
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erro ao voltar para lista").showAndWait();
        }
    }
}