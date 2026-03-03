package minhadespensa;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import minhadespensa.ItemDAO;

public class Main extends JFrame {
    private ItemDAO itemDAO = new ItemDAO();
    private DefaultTableModel tableModel;
    private JTable tabelaItens;
    private JTextField txtNome, txtQuantidade, txtValidade;
    private JComboBox<String> comboCategoria;
    private JButton btnCadastrar, btnListar, btnEditar, btnExcluir, btnFiltrar;
    private JLabel lblTitulo;

    public Main() {
        setTitle("Controle de Despensa");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        FabricaConexao.testarConexao();

        criarCabecalho();
        criarFormulario();
        criarTabela();
        criarBotoes();

        carregarTabela();

        setVisible(true);
    }

    private void criarCabecalho() {
        lblTitulo = new JLabel(" SISTEMA DE CONTROLE DE DESPENSA", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(41, 128, 185));
        add(lblTitulo, BorderLayout.NORTH);
    }

    private void criarFormulario() {
        JPanel painelFormulario = new JPanel(new GridBagLayout());
        painelFormulario.setBorder(BorderFactory.createTitledBorder(" Cadastro de Item"));
        painelFormulario.setBackground(new Color(240, 240, 240));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        painelFormulario.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1;
        txtNome = new JTextField(20);
        painelFormulario.add(txtNome, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        painelFormulario.add(new JLabel("Categoria:"), gbc);
        gbc.gridx = 1;
        comboCategoria = new JComboBox<>(new String[]{"armario", "geladeira"});
        painelFormulario.add(comboCategoria, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        painelFormulario.add(new JLabel("Quantidade:"), gbc);
        gbc.gridx = 1;
        txtQuantidade = new JTextField(10);
        painelFormulario.add(txtQuantidade, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        painelFormulario.add(new JLabel("Validade (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        txtValidade = new JTextField(15);
        painelFormulario.add(txtValidade, gbc);

        add(painelFormulario, BorderLayout.NORTH);
    }

    private void criarTabela() {
        String[] colunas = {"ID", "Nome", "Categoria", "Quantidade", "Validade"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaItens = new JTable(tableModel);
        tabelaItens.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabelaItens.setFont(new Font("Arial", 12, Font.PLAIN));
        tabelaItens.setRowHeight(25);
        tabelaItens.setSelectionBackground(new Color(200, 230, 255));

        JScrollPane scrollPane = new JScrollPane(tabelaItens);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void criarBotoes() {
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        painelBotoes.setBackground(new Color(240, 240, 240));

        btnCadastrar = new JButton(" Cadastrar");
        btnListar = new JButton("Listar Tudo");
        btnFiltrar = new JButton(" Filtrar por Categoria");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir");

        btnCadastrar.addActionListener(e -> cadastrarItem());
        btnListar.addActionListener(e -> carregarTabela());
        btnFiltrar.addActionListener(e -> filtrarPorCategoria());
        btnEditar.addActionListener(e -> editarItem());
        btnExcluir.addActionListener(e -> excluirItem());

        painelBotoes.add(btnCadastrar);
        painelBotoes.add(btnListar);
        painelBotoes.add(btnFiltrar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);

        add(painelBotoes, BorderLayout.SOUTH);
    }

    private void carregarTabela() {
        tableModel.setRowCount(0);
        List<Item> lista = itemDAO.listar();

        for (Item item : lista) {
            Object[] linha = {
                item.getId(),
                item.getNome(),
                item.getCategoria(),
                item.getQuantidade(),
                item.getValidade() != null ? item.getValidade().toString() : "N/A"
            };
            tableModel.addRow(linha);
        }

        if (lista.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum item cadastrado!", "Informação", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void filtrarPorCategoria() {
        String categoria = (String) comboCategoria.getSelectedItem();
        List<Item> lista = itemDAO.listarPorCategoria(categoria);

        tableModel.setRowCount(0);
        for (Item item : lista) {
            Object[] linha = {
                item.getId(),
                item.getNome(),
                item.getCategoria(),
                item.getQuantidade(),
                item.getValidade() != null ? item.getValidade().toString() : "N/A"
            };
            tableModel.addRow(linha);
        }

        if (lista.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum item nesta categoria!", "Informação", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void cadastrarItem() {
        String nome = txtNome.getText().trim();
        String categoria = (String) comboCategoria.getSelectedItem();
        String qtdStr = txtQuantidade.getText().trim();
        String validadeStr = txtValidade.getText().trim();

        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, " O nome do produto é obrigatório!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int quantidade;
        try {
            quantidade = Integer.parseInt(qtdStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantidade deve ser um número!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Date validade = (validadeStr.isEmpty()) ? null : Date.valueOf(validadeStr);

        Item item = new Item(nome, categoria, quantidade, validade);
        itemDAO.cadastrar(item);

        JOptionPane.showMessageDialog(this, "Item cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        limparFormulario(); // 
        carregarTabela();
    }

    private void editarItem() {
        int selectedRow = tabelaItens.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um item na tabela para editar!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String nome = (String) tableModel.getValueAt(selectedRow, 1);
        String categoria = (String) tableModel.getValueAt(selectedRow, 2);
        int quantidade = (int) tableModel.getValueAt(selectedRow, 3);
        String validadeStr = (String) tableModel.getValueAt(selectedRow, 4);

        String novoNome = JOptionPane.showInputDialog(this, "Novo nome:", nome);
        if (novoNome == null || novoNome.trim().isEmpty()) return;

        String novaCategoria = (String) JOptionPane.showInputDialog(
            this, "Nova categoria:", categoria, JOptionPane.QUESTION_MESSAGE, null,
            new String[]{"armario", "geladeira"}, categoria
        );
        if (novaCategoria == null) return;

        String novaQtdStr = JOptionPane.showInputDialog(this, "Nova quantidade:", String.valueOf(quantidade));
        int novaQuantidade;
        try {
            novaQuantidade = Integer.parseInt(novaQtdStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantidade inválida!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String novaValidadeStr = JOptionPane.showInputDialog(this, "Nova validade (YYYY-MM-DD) ou deixe em branco:", validadeStr);
        Date novaValidade = (novaValidadeStr == null || novaValidadeStr.trim().isEmpty()) ? null : Date.valueOf(novaValidadeStr);

        Item item = new Item(novoNome, novaCategoria, novaQuantidade, novaValidade);
        item.setId(id);
        itemDAO.atualizar(item);

        JOptionPane.showMessageDialog(this, "Item atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        carregarTabela();
    }

    private void excluirItem() {
        int selectedRow = tabelaItens.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, " Selecione um item na tabela para excluir!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Tem certeza que deseja excluir este item?",
            "Confirmar Exclusão",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            itemDAO.excluir(id);
            JOptionPane.showMessageDialog(this, " Item excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            carregarTabela();
        }
    }

    // ✅ MÉTODO FALTANTE - ADICIONADO
    private void limparFormulario() {
        txtNome.setText("");
        txtQuantidade.setText("");
        txtValidade.setText("");
        comboCategoria.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        // Configurar Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Iniciar aplicação
        SwingUtilities.invokeLater(() -> new Main());
    }
}