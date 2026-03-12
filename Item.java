import java.time.LocalDate;

public class Item {
    private int id;
    private String nome;
    private String categoria; // "armario" ou "geladeira"
    private int quantidade;
    private LocalDate validade;

    // Construtor completo
    public Item(int id, String nome, String categoria, int quantidade, LocalDate validade) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.quantidade = quantidade;
        this.validade = validade;
    }

    // Construtor sem ID (para inserção)
    public Item(String nome, String categoria, int quantidade, LocalDate validade) {
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

    public LocalDate getValidade() { return validade; }
    public void setValidade(LocalDate validade) { this.validade = validade; }

    @Override
    public String toString() {
        return String.format("Item{id=%d, nome='%s', categoria='%s', quantidade=%d, validade=%s}",
                id, nome, categoria, quantidade, validade);
    }
}
