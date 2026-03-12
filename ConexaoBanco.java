import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBanco {

    private static final String URL      = "jdbc:mysql://localhost:3306/despensa";
    private static final String USUARIO  = "root";
    private static final String SENHA    = "Caca2003";

    // Retorna uma conexão ativa com o banco
    public static Connection getConexao() throws SQLException {
        try {
            // Carrega o driver MySQL (necessário para versões antigas do JDBC)
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL não encontrado. Adicione mysql-connector-java ao classpath.", e);
        }
    }

    // Fecha a conexão com segurança
    public static void fecharConexao(Connection conexao) {
        if (conexao != null) {
            try {
                conexao.close();
                System.out.println("Conexão encerrada.");
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }
}
