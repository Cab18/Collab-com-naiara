import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) para a tabela 'itens' da despensa.
 * Contém todas as operações CRUD + consultas especiais.
 */
public class ItemDAO {

    // =========================================================
    // CREATE — Inserir novo item
    // =========================================================
    public void inserir(Item item) {
        String sql = "INSERT INTO itens (nome, categoria, quantidade, validade) VALUES (?, ?, ?, ?)";

        try (Connection conexao = ConexaoBanco.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, item.getNome());
            stmt.setString(2, item.getCategoria());
            stmt.setInt(3, item.getQuantidade());
            stmt.setDate(4, item.getValidade() != null ? Date.valueOf(item.getValidade()) : null);

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas > 0) {
                ResultSet chaveGerada = stmt.getGeneratedKeys();
                if (chaveGerada.next()) {
                    item.setId(chaveGerada.getInt(1));
                }
                System.out.println("✅ Item inserido com sucesso! ID: " + item.getId());
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro ao inserir item: " + e.getMessage());
        }
    }

    // =========================================================
    // READ — Buscar todos os itens
    // =========================================================
    public List<Item> buscarTodos() {
        List<Item> itens = new ArrayList<>();
        String sql = "SELECT * FROM itens ORDER BY nome";

        try (Connection conexao = ConexaoBanco.getConexao();
             Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                itens.add(mapearItem(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar itens: " + e.getMessage());
        }

        return itens;
    }

    // =========================================================
    // READ — Buscar item por ID
    // =========================================================
    public Item buscarPorId(int id) {
        String sql = "SELECT * FROM itens WHERE id = ?";

        try (Connection conexao = ConexaoBanco.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearItem(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar item por ID: " + e.getMessage());
        }

        return null;
    }

    // =========================================================
    // READ — Buscar por categoria (armario / geladeira)
    // =========================================================
    public List<Item> buscarPorCategoria(String categoria) {
        List<Item> itens = new ArrayList<>();
        String sql = "SELECT * FROM itens WHERE categoria = ? ORDER BY nome";

        try (Connection conexao = ConexaoBanco.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, categoria);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                itens.add(mapearItem(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar por categoria: " + e.getMessage());
        }

        return itens;
    }

    // =========================================================
    // READ — Buscar itens vencidos ou próximos de vencer
    // =========================================================
    public List<Item> buscarVencidosOuProximos(int diasAntecedencia) {
        List<Item> itens = new ArrayList<>();
        String sql = "SELECT * FROM itens WHERE validade IS NOT NULL AND validade <= ? ORDER BY validade";

        try (Connection conexao = ConexaoBanco.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            LocalDate limiteData = LocalDate.now().plusDays(diasAntecedencia);
            stmt.setDate(1, Date.valueOf(limiteData));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                itens.add(mapearItem(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar itens por validade: " + e.getMessage());
        }

        return itens;
    }

    // =========================================================
    // READ — Buscar itens com estoque baixo
    // =========================================================
    public List<Item> buscarEstoqueBaixo(int quantidadeMinima) {
        List<Item> itens = new ArrayList<>();
        String sql = "SELECT * FROM itens WHERE quantidade <= ? ORDER BY quantidade";

        try (Connection conexao = ConexaoBanco.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, quantidadeMinima);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                itens.add(mapearItem(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar estoque baixo: " + e.getMessage());
        }

        return itens;
    }

    // =========================================================
    // UPDATE — Atualizar item completo
    // =========================================================
    public void atualizar(Item item) {
        String sql = "UPDATE itens SET nome = ?, categoria = ?, quantidade = ?, validade = ? WHERE id = ?";

        try (Connection conexao = ConexaoBanco.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, item.getNome());
            stmt.setString(2, item.getCategoria());
            stmt.setInt(3, item.getQuantidade());
            stmt.setDate(4, item.getValidade() != null ? Date.valueOf(item.getValidade()) : null);
            stmt.setInt(5, item.getId());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                System.out.println("✅ Item atualizado com sucesso!");
            } else {
                System.out.println("⚠️ Nenhum item encontrado com ID: " + item.getId());
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro ao atualizar item: " + e.getMessage());
        }
    }

    // =========================================================
    // UPDATE — Atualizar apenas a quantidade
    // =========================================================
    public void atualizarQuantidade(int id, int novaQuantidade) {
        String sql = "UPDATE itens SET quantidade = ? WHERE id = ?";

        try (Connection conexao = ConexaoBanco.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, novaQuantidade);
            stmt.setInt(2, id);

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                System.out.println("✅ Quantidade atualizada para " + novaQuantidade);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro ao atualizar quantidade: " + e.getMessage());
        }
    }

    // =========================================================
    // DELETE — Remover item por ID
    // =========================================================
    public void deletar(int id) {
        String sql = "DELETE FROM itens WHERE id = ?";

        try (Connection conexao = ConexaoBanco.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                System.out.println("✅ Item removido com sucesso!");
            } else {
                System.out.println("⚠️ Nenhum item encontrado com ID: " + id);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro ao deletar item: " + e.getMessage());
        }
    }

    // =========================================================
    // Método auxiliar — Mapear ResultSet para objeto Item
    // =========================================================
    private Item mapearItem(ResultSet rs) throws SQLException {
        int id            = rs.getInt("id");
        String nome       = rs.getString("nome");
        String categoria  = rs.getString("categoria");
        int quantidade    = rs.getInt("quantidade");
        Date validadeSQL  = rs.getDate("validade");
        LocalDate validade = validadeSQL != null ? validadeSQL.toLocalDate() : null;

        return new Item(id, nome, categoria, quantidade, validade);
    }
}
