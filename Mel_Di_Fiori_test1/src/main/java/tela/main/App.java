package tela.main;

// Importa a classe base do JavaFX para iniciar a aplicação
import javafx.application.Application;

// Importa classes para carregar arquivos FXML (interfaces visuais)
import javafx.fxml.FXMLLoader;

// Importa classes para criar a cena e o layout da interface
import javafx.scene.Scene;
import javafx.scene.Parent;

// Importa a classe da janela principal
import javafx.stage.Stage;

/**
 * 🚀 App.java
 * --------------------------------------------------------
 * Classe principal do sistema de apicultura "Mel Di Fiori".
 *
 * Esta é a porta de entrada da aplicação JavaFX.
 * Ela carrega o layout principal (MainLayout.fxml),
 * aplica a folha de estilos (style.css),
 * configura a janela principal e inicia o programa.
 */
public class App extends Application {

    /**
     * Este método é automaticamente chamado ao iniciar a aplicação.
     * Ele configura a janela principal (Stage), a cena (Scene),
     * carrega a interface FXML e aplica a folha de estilo CSS.
     *
     * @param primaryStage A janela principal da aplicação
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carrega o layout principal da interface a partir do FXML
        // Esse FXML contém a estrutura base da interface (menu lateral + painel de conteúdo)
        Parent root = FXMLLoader.load(getClass().getResource("/telas/view/MainLayout.fxml"));

        // Cria uma cena (área visível) com o layout carregado e define o tamanho da janela
        Scene scene = new Scene(root, 900, 600);

        // Adiciona o arquivo CSS para estilizar os componentes da interface
        scene.getStylesheets().add(getClass().getResource("/globalStyle/style.css").toExternalForm());

        // Define o título da janela principal
        primaryStage.setTitle("Flor & Ser - Sistema de Floricultura");

        // Define a cena que será exibida dentro da janela
        primaryStage.setScene(scene);

        primaryStage.setMaximized(true); // 🔥 Faz a janela iniciar maximizada

        // Exibe a janela principal na tela
        primaryStage.show();
    }

    /**
     * Método main, chamado quando o programa é executado.
     * Ele chama o método launch(), que inicializa o JavaFX.
     *
     * @param args Argumentos passados por linha de comando (se houver)
     */
    public static void main(String[] args) {
        launch(args); // Inicia a aplicação JavaFX (chama o start())
    }
}
