package minhadespensa;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author anaca
 */
public class Minhadespensa {

    public class TestaConexao{
  
   
}
     public static void main(String[] args) {
       String url = "jdbc:mysql://localhost:3306/despensa";
       String user = "root";
       String password = "Caca2003";
       
       try{
           //carregar jdbc
           Class.forName("com.mysql.cj.jdbc.Driver");
           
          Connection conexao = DriverManager.getConnection(url,user,password);
          System.out.println("Conexão bem sucedida!");
          conexao.close();
       }catch(ClassNotFoundException e){
           System.out.println("Driver JDBC  não encontrado" + e.getMessage());
       }catch (SQLException e ){
           System.out.println("Erro ao conectar ao banco: " + e.getMessage());
       }
    
}
}