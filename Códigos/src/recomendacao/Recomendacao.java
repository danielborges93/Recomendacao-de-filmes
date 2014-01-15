/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recomendacao;

import com.mongodb.BasicDBList;
import com.mongodb.MongoClient;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author daniel_borges_93
 */
public class Recomendacao {

    private MongoClient mongo;
    private final DBCollection moviesCollection;
    private final DBCollection usersCollection;
    private final DBCollection ratesCollection;
    private final DBCollection categoriesCollection;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Recomendacao recomendacao = new Recomendacao();
        recomendacao.recomendar();
    }

    public Recomendacao() {
        try {
            mongo = new MongoClient();
        } catch (UnknownHostException ex) {
            Logger.getLogger(Recomendacao.class.getName()).log(Level.SEVERE, null, ex);
        }

        DB db = mongo.getDB("test");

        //Recuperar todas as intâncias
        moviesCollection = db.getCollection("movies");
        usersCollection = db.getCollection("users");
        ratesCollection = db.getCollection("rates");
        categoriesCollection = db.getCollection("categories");
    }

    /**
     * Filmes para o usuário
     */
    public void recomendar() {

        long startTime = System.currentTimeMillis();
        
        double soma = 0;
        
        //Recuperar todos os usuários
        DBCursor usersCursor = usersCollection.find();
        //Para cada usuáro
        while (usersCursor.hasNext()) {
            long startTimeUser = System.currentTimeMillis();
            
            BasicDBObject user = (BasicDBObject) usersCursor.next();

            //Ver qual o gênero que ele mais assiste (baseado nos rates)
            //e recuperar todos os filmes assistidos pelo user
            DBCursor ratesCursor = ratesCollection.find(new BasicDBObject("userID", user.get("_id")));

            Map<String, Integer> categoriasCount = new HashMap<>();
            BasicDBList moviesAssistidos = new BasicDBList();

            while (ratesCursor.hasNext()) {
                BasicDBObject rate = (BasicDBObject) ratesCursor.next();

                //Recuperar o filme
                Integer movieID = rate.getInt("movieID");
                BasicDBObject movie = (BasicDBObject) moviesCollection.findOne(
                        new BasicDBObject("_id", movieID)
                );
                BasicDBList categories = (BasicDBList) movie.get("categories");

                moviesAssistidos.add(movieID);

                try {
                    //Guardar a contagem
                    for (Object categoryObject : categories) {
                        String category = (String) categoryObject;

                        int count = 0;
                        if (categoriasCount.containsKey(category)) {
                            count = categoriasCount.get(category);
                        }
                        count++;
                        categoriasCount.put(category, count);
                    }
                } catch (NullPointerException ex) {
                    System.out.println(movie.get("name") + " não tem categoria...");
                }

//                    System.out.println("movie: " + movie);
//                    break;
            }

            //Selecionar a melhor categoria
            String melhorCategoria = null;
            int maior = -1;

            for (String category : categoriasCount.keySet()) {
                int count = categoriasCount.get(category);

                if (count > maior) {
                    maior = count;
                    melhorCategoria = category;
                }
            }

            BasicDBObject categoryObject = (BasicDBObject) categoriesCollection.findOne(
                    new BasicDBObject("name", melhorCategoria)
            );
            BasicDBList todosMoviesDaCategoria = (BasicDBList) categoryObject.get("movies");

//                System.out.println("moviesAssistidos: " + moviesAssistidos.size());
//                System.out.println("todosMoviesDaCategoria: " + todosMoviesDaCategoria.size());
            //Selecionar os filmes da categoria que o user não assistiu
            todosMoviesDaCategoria.removeAll(moviesAssistidos);

//                System.out.println("todosMoviesDaCategoria: " + todosMoviesDaCategoria.size());
            BasicDBObject selectObject = new BasicDBObject();
            selectObject.put("movieID", new BasicDBObject("$in", todosMoviesDaCategoria));

            BasicDBObject sortObject = new BasicDBObject();
            sortObject.put("rating", -1);
            sortObject.put("timestamp", -1);

            ratesCursor = ratesCollection.find(selectObject);
            ratesCursor.sort(sortObject);
            ratesCursor.limit(5);

            BasicDBList moviesRecomendados = new BasicDBList();

            while (ratesCursor.hasNext()) {
                BasicDBObject rate = (BasicDBObject) ratesCursor.next();

                Integer movieID = rate.getInt("movieID");
                DBObject movie = moviesCollection.findOne(new BasicDBObject("_id", movieID));
                moviesRecomendados.add(movie);
            }

//            System.out.println("Recomendação para " + user.getInt("_id") + ":");
//            for (Object movie : moviesRecomendados) {
//                System.out.println("\t" + movie);
//            }
//            System.out.println();
            
            long finishTimeUser = System.currentTimeMillis();
            
            System.out.print("Tempo de execução para recomendar o user " + user.getInt("_id") + ": ");
            System.out.println(((double)(finishTimeUser-startTimeUser))/1000.0 + " segundos.");
            
            soma += ((double)(finishTimeUser-startTimeUser))/1000.0;
            
//            break;
        }
        
        System.out.println();
        
        System.out.println("Média de tempo de execução para cada usuário: " + soma/943.0 + " segundos.");
        
        System.out.println();
        
        long finishTime = System.currentTimeMillis();
        
        System.out.print("Tempo de execução do algorítmo: ");
        System.out.println(((double)(finishTime-startTime))/1000.0 + " segundos.");

    }

}
