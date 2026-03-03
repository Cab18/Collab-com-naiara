package minhadespensa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import minhadespensa.Item;

public class ItemDAO {

    // 1. CADASTRAR
    public void cadastrar(Item item) {
        String sql = "INSERT INTO itens (nome, categoria, quantidade, validade) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = FabricaConexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, item.getNome());
            stmt.setString(2, item.getCategoria());
            stmt.setInt(3, item.getQuantidade());
            stmt.setDate(4, item.getValidade());
            stmt.executeUpdate();
            System.out.println("✓ Item cadastrado com sucesso!");
            
        } catch (SQLException e) {
            System.out.println("✗ Erro ao cadastrar: " + e.getMessage());
        }
    }

    // 2. LISTAR TODOS
    public List<Item> listar() {
        List<Item> lista = new ArrayList<>();
        String sql = "SELECT * FROM itens";
        
        try (Connection conn = FabricaConexao.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                // ✅ CORREÇÃO: Item maiúsculo + variável com nome diferente
                Item itemObj = new Item();
                itemObj.setId(rs.getInt("id"));
                itemObj.setNome(rs.getString("nome"));
                itemObj.setCategoria(rs.getString("categoria"));
                itemObj.setQuantidade(rs.getInt("quantidade"));
                itemObj.setValidade(rs.getDate("validade"));
                lista.add(itemObj);
            }
        } catch (SQLException e) {
            System.out.println("✗ Erro ao listar: " + e.getMessage());
        }
        return lista;
    }

    // 3. EDITAR
    public void atualizar(Item item) {
        String sql = "UPDATE itens SET nome=?, categoria=?, quantidade=?, validade=? WHERE id=?";
        
        try (Connection conn = FabricaConexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, item.getNome());
            stmt.setString(2, item.getCategoria());
            stmt.setInt(3, item.getQuantidade());
            stmt.setDate(4, item.getValidade());
            stmt.setInt(5, item.getId());
            stmt.executeUpdate();
            System.out.println("✓ Item atualizado com sucesso!");
            
        } catch (SQLException e) {
            System.out.println("✗ Erro ao atualizar: " + e.getMessage());
        }
    }

    // 4. EXCLUIR
    public void excluir(int id) {
        String sql = "DELETE FROM itens WHERE id=?";
        
        try (Connection conn = FabricaConexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("✓ Item excluído com sucesso!");
            
        } catch (SQLException e) {
            System.out.println("✗ Erro ao excluir: " + e.getMessage());
        }
    }
    
    // 5. LISTAR POR CATEGORIA
    public List<Item> listarPorCategoria(String categoria) {
        List<Item> lista = new ArrayList<>();
        String sql = "SELECT * FROM itens WHERE categoria = ?";
        
        try (Connection conn = FabricaConexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, categoria);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                // ✅ CORREÇÃO: Item maiúsculo + variável com nome diferente
                Item itemObj = new Item();
                itemObj.setId(rs.getInt("id"));
                itemObj.setNome(rs.getString("nome"));
                itemObj.setCategoria(rs.getString("categoria"));
                itemObj.setQuantidade(rs.getInt("quantidade"));
                itemObj.setValidade(rs.getDate("validade"));
                lista.add(itemObj);
            }
        } catch (SQLException e) {
            System.out.println("✗ Erro ao filtrar: " + e.getMessage());
        }
        return lista;
    }
}