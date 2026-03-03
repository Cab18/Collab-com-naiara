package minhadespensa;

import java.sql.Date;

public class Item {
    private int id;
    private String nome;
    private String categoria;
    private int quantidade;
    private Date validade;

    // ✅ Construtor Vazio (Necessário para DAO)
    public Item() {}

    // ✅ Construtor Cheio
    public Item(String nome, String categoria, int quantidade, Date validade) {
        this.nome = nome;
        this.categoria = categoria;
        this.quantidade = quantidade;
        this.validade = validade;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public Date getValidade() { return validade; }
    public void setValidade(Date validade) { this.validade = validade; }
    
    @Override
    public String toString() {
        return "ID: " + id + " | Nome: " + nome + " | Categoria: " + categoria + 
               " | Qtd: " + quantidade + " | Validade: " + validade;
    }
}