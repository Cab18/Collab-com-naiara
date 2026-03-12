import java.time.LocalDate;
import java.util.List;

/**
 * Classe principal para demonstrar todas as operações CRUD da despensa.
 *
 * Dependência necessária no classpath:
 *   mysql-connector-j-8.x.x.jar
 *
 * Compilar:
 *   javac -cp .:mysql-connector-j-8.x.x.jar *.java
 *
 * Executar:
 *   java -cp .:mysql-connector-j-8.x.x.jar Main
 */
public class Main {

    public static void main(String[] args) {

        ItemDAO dao = new ItemDAO();

        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║     SISTEMA DE GERENCIAMENTO         ║");
        System.out.println("║          DE DESPENSA 🥫               ║");
        System.out.println("╚══════════════════════════════════════╝\n");

        // ─── 1. INSERIR itens ────────────────────────────────
        System.out.println("─── 1. INSERINDO ITENS ───────────────────");

        Item arroz    = new Item("Arroz",       "armario",   5,  LocalDate.of(2026, 12, 31));
        Item leite    = new Item("Leite",        "geladeira", 3,  LocalDate.of(2026,  3, 20));
        Item feijao   = new Item("Feijão",       "armario",   2,  LocalDate.of(2026, 10, 15));
        Item manteiga = new Item("Manteiga",     "geladeira", 1,  LocalDate.of(2026,  4,  5));
        Item sal      = new Item("Sal",          "armario",   10, null); // sem validade
        Item iogurte  = new Item("Iogurte",      "geladeira", 2,  LocalDate.of(2026,  3, 12));
        Item macarrao = new Item("Macarrão",     "armario",   4,  LocalDate.of(2027,  6, 30));

        dao.inserir(arroz);
        dao.inserir(leite);
        dao.inserir(feijao);
        dao.inserir(manteiga);
        dao.inserir(sal);
        dao.inserir(iogurte);
        dao.inserir(macarrao);

        // ─── 2. LISTAR TODOS ─────────────────────────────────
        System.out.println("\n─── 2. TODOS OS ITENS ────────────────────");
        List<Item> todos = dao.buscarTodos();
        todos.forEach(item -> System.out.println("  → " + item));

        // ─── 3. BUSCAR POR ID ────────────────────────────────
        System.out.println("\n─── 3. BUSCAR POR ID (id=1) ──────────────");
        Item encontrado = dao.buscarPorId(1);
        System.out.println("  → " + encontrado);

        // ─── 4. BUSCAR POR CATEGORIA ─────────────────────────
        System.out.println("\n─── 4. ITENS NA GELADEIRA ────────────────");
        List<Item> geladeira = dao.buscarPorCategoria("geladeira");
        geladeira.forEach(item -> System.out.println("  🧊 " + item));

        System.out.println("\n─── 4b. ITENS NO ARMÁRIO ─────────────────");
        List<Item> armario = dao.buscarPorCategoria("armario");
        armario.forEach(item -> System.out.println("  🗄️  " + item));

        // ─── 5. ITENS VENCIDOS / PRÓXIMOS DE VENCER ──────────
        System.out.println("\n─── 5. VENCIDOS OU VENCENDO EM 30 DIAS ──");
        List<Item> proximos = dao.buscarVencidosOuProximos(30);
        if (proximos.isEmpty()) {
            System.out.println("  ✅ Nenhum item próximo de vencer.");
        } else {
            proximos.forEach(item -> System.out.println("  ⚠️  " + item));
        }

        // ─── 6. ESTOQUE BAIXO ────────────────────────────────
        System.out.println("\n─── 6. ESTOQUE BAIXO (≤ 2 unidades) ─────");
        List<Item> baixo = dao.buscarEstoqueBaixo(2);
        baixo.forEach(item -> System.out.println("  📉 " + item));

        // ─── 7. ATUALIZAR ITEM COMPLETO ───────────────────────
        System.out.println("\n─── 7. ATUALIZANDO ITEM (Arroz) ──────────");
        arroz.setQuantidade(8);
        arroz.setValidade(LocalDate.of(2027, 6, 30));
        dao.atualizar(arroz);
        System.out.println("  Depois: " + dao.buscarPorId(arroz.getId()));

        // ─── 8. ATUALIZAR APENAS QUANTIDADE ──────────────────
        System.out.println("\n─── 8. ATUALIZANDO QUANTIDADE (Feijão) ───");
        dao.atualizarQuantidade(feijao.getId(), 7);
        System.out.println("  Depois: " + dao.buscarPorId(feijao.getId()));

        // ─── 9. DELETAR ITEM ─────────────────────────────────
        System.out.println("\n─── 9. DELETANDO ITEM (Iogurte) ──────────");
        dao.deletar(iogurte.getId());

        // ─── 10. LISTA FINAL ─────────────────────────────────
        System.out.println("\n─── 10. LISTA FINAL DA DESPENSA ──────────");
        List<Item> final_ = dao.buscarTodos();
        final_.forEach(item -> System.out.println("  ✔ " + item));

        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║         OPERAÇÕES CONCLUÍDAS!        ║");
        System.out.println("╚══════════════════════════════════════╝");
    }
}
