package minhadespensa;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class FabricaConexao {
    
    // Configurações do banco
    private static final String URL = "jdbc:mysql://localhost:3306/despensa";
    private static final String USUARIO = "root";
    private static final String SENHA = "password";

    // Método estático para conexão
    public static Connection conectar() throws SQLException {
        try {
            // Carregar o driver JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver do MySQL não encontrado: " + e.getMessage(), e);
        }
    }

    // Método para testar conexão
    public static void testarConexao() {
        try {
            Connection conn = conectar();
            if (conn != null && !conn.isClosed()) {
                System.out.println("Conexão estabelecida com sucesso!");
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("❌ Erro ao conectar: " + e.getMessage());
            System.out.println("Dica: Verifique se o MySQL está rodando e as credenciais estão corretas.");
        }
    }

    public static void main(String[] args) {
        testarConexao();
    }

}
