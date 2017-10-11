import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;


public class populate {
    private static Connection conn;

    public static void main(String[] args) {

        String url = "jdbc:oracle:thin:@localhost:49161:xe";
        String user = "longling";
        String password = "longling";

        try {
            conn = DriverManager.getConnection(url, user, password);

            createMovies();
            createGenres();
            createCountries();
            createActors();
            createDirectors();
            createTags();
            createMovie_Tag();
            createUser_rating();
            createAggList();
            createMovies_helper();

//             create index for the columns
            Statement index = conn.createStatement();
            index.executeUpdate("CREATE INDEX genres_index ON genres (genre)");
            index.executeUpdate("CREATE INDEX country_index ON countries (country)");
            index.executeUpdate("CREATE INDEX actor_index ON actors (actorName)");
            index.executeUpdate("CREATE INDEX director_index ON directors (directorName)");
            index.executeUpdate("CREATE INDEX movieYear_index ON movies_helper (year)");
            index.executeUpdate("CREATE INDEX userID_index ON user_rating (userID)");
            index.executeUpdate("CREATE INDEX userRating_index ON user_rating (rating)");
            index.executeUpdate("CREATE INDEX ratingDate_index ON user_rating (rating_date)");
            index.executeUpdate("CREATE INDEX ratings_index ON movies_helper (ratings)");
            index.executeUpdate("CREATE INDEX ratingNumber_index ON movies_helper (rating_Number)");

            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void createGenres() throws SQLException {
        String filePath = "/Users/longlingwang/Documents/280database/HW/3/hetrec2011-movielens-2k-v2/movie_genres.dat";
        String sql = "INSERT INTO genres (movieID, genre) values (?, ?)";
        Statement clean = conn.createStatement();
        clean.executeUpdate("DELETE FROM genres");
        System.out.println("Table genres was cleared.");
        helper(sql, filePath, 2);
        System.out.println("Table genres was built with data.");
    }

    private static void createCountries() throws SQLException {
        String filePath = "/Users/longlingwang/Documents/280database/HW/3/hetrec2011-movielens-2k-v2/movie_countries.dat";
        String sql = "INSERT INTO countries (movieID, country) values (?, ?)";
        PreparedStatement statement = conn.prepareStatement(sql);
        Statement clean = conn.createStatement();

        clean.executeUpdate("DELETE FROM countries");
        System.out.println("Table countries was cleared.");
        try {
            File file = new File(filePath);
            Scanner input = new Scanner(file);
            input.nextLine();
            while (input.hasNext()) {
                String nextLine = input.nextLine();
                String[] splited = nextLine.split("\\t");
                statement.setString(1, splited[0]);
                if (splited.length > 1) {
                    statement.setString(2, splited[1]);
                } else {
                    statement.setString(2, null);
                }
                statement.executeUpdate();
            }
            System.out.println("Table countries was built with data.");
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }
    }

    private static void createMovies() throws SQLException {
        String filePath = "/Users/longlingwang/Documents/280database/HW/3/hetrec2011-movielens-2k-v2/movies.dat";
        String sql = "INSERT INTO movies (mid, title, year, rtAllCriticsRating, rtAllCriticsNumReviews, " +
                "rtTopCriticsRating, rtTopCriticsNumReviews, rtAudienceRating, rtAudienceNumRatings) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = conn.prepareStatement(sql);
        Statement clean = conn.createStatement();
        clean.executeUpdate("DELETE FROM movies");
        System.out.println("Table movies was cleared.");
        Charset charset = StandardCharsets.UTF_8;
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(filePath), charset));
            String nextLine = reader.readLine();
            nextLine = reader.readLine();
            while (nextLine != null) {
                String[] splited = nextLine.split("\\t");
                int[] order = {0, 1, 5, 7, 8, 12, 13, 17, 18};
                int i = 1;
                for (int each: order) {
                    if (splited[each].equals(new String("\\N"))) {
                        statement.setString(i, null);
                    } else {
                        statement.setString(i, splited[each]);
                    }
                    i++;
                }
                statement.executeUpdate();
                nextLine = reader.readLine();
            }
            System.out.println("Table movies was built with data.");
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private static void createActors() throws SQLException {
        String filePath = "/Users/longlingwang/Documents/280database/HW/3/hetrec2011-movielens-2k-v2/movie_actors.dat";
        String sql = "INSERT INTO actors (movieID, actorID, actorName, ranking) values (?,?,?,?)";
        Statement clean = conn.createStatement();
        clean.executeUpdate("DELETE FROM actors");
        System.out.println("Table actors was cleared.");
        helper(sql, filePath, 4);
        System.out.println("Table actors was built with data.");
    }

    private static void createDirectors() throws SQLException {
        String filePath = "/Users/longlingwang/Documents/280database/HW/3/hetrec2011-movielens-2k-v2/movie_directors.dat";
        String sql = "INSERT INTO directors (movieID, directorID, directorName) values (?,?,?)";
        Statement clean = conn.createStatement();
        clean.executeUpdate("DELETE FROM directors");
        System.out.println("Table directors was cleared.");
        helper(sql, filePath, 3);
        System.out.println("Table directors was built with data.");
    }

    private static void createTags() throws SQLException {
        String filePath = "/Users/longlingwang/Documents/280database/HW/3/hetrec2011-movielens-2k-v2/tags.dat";
        String sql = "INSERT INTO tags (tid, value) values (?,?)";
        Statement clean = conn.createStatement();
        clean.executeUpdate("DELETE FROM tags");
        System.out.println("Table tags was cleared.");
        helper(sql, filePath, 2);
        System.out.println("Table tags was built with data.");
    }

    private static void createMovie_Tag() throws SQLException {
        String filePath = "/Users/longlingwang/Documents/280database/HW/3/hetrec2011-movielens-2k-v2/movie_tags.dat";
        String sql = "INSERT INTO movie_tag (movieID, tagID, tagWeight) values (?, ?, ?)";
        Statement clean = conn.createStatement();
        clean.executeUpdate("DELETE FROM movie_tag");
        System.out.println("Table movie_tag was cleared.");
        helper(sql, filePath, 3);
        System.out.println("Table movie_tag was built with data.");
    }

    private static void createUser_rating() throws SQLException {
        String filePath = "/Users/longlingwang/Documents/280database/HW/3/hetrec2011-movielens-2k-v2/user_ratedmovies.dat";
        String sql = "INSERT INTO user_rating (userID, movieID, rating, rating_date) values (?, ?, ?, ?)";
        Statement clean = conn.createStatement();
        clean.executeUpdate("DELETE FROM user_rating");
        System.out.println("Table user_rating was cleared.");
        try {
            FileReader f = new FileReader(new File(filePath));
            BufferedReader bf = new BufferedReader(f);
            Scanner input = new Scanner(bf);
            PreparedStatement statement = conn.prepareStatement(sql);
            input.nextLine();
            while (input.hasNext()) {
                String nextLine = input.nextLine();
                String[] splited = nextLine.split("\\t");
                for (int i = 0; i < 3; i++) {
                    statement.setString(i + 1, splited[i]);
                }
                String date = splited[5] + "-" + splited[4] + "-" + splited[3];
                try {
                    java.util.Date utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
                    java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                    statement.setDate(4, sqlDate);
                    statement.executeUpdate();
                } catch (ParseException e) {
                    System.out.println(e);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }
        System.out.println("Table user_rating was built with data.");
    }

    private static void createMovies_helper() throws SQLException {
        String sql = "CREATE TABLE movies_helper\n" +
                "AS \n" +
                "SELECT M.mid, M.title, M.year, \n" +
                "       ROUND((M.rtAllCriticsRating + M.rtTopCriticsRating + M.rtAudienceRating)/3, 1) AS ratings,\n" +
                "       ROUND((M.rtAllCriticsNumReviews + M.rtTopCriticsNumReviews + M.rtAudienceNumRatings)/3, 0) AS rating_Number \n" +
                "FROM movies M";
        Statement create = conn.createStatement();
        create.execute(sql);
        System.out.println("Table movies_helper was built with data.");
    }

    private static void createAggList() throws SQLException {
        String sql = "CREATE TABLE Agg_List AS\n" +
                "SELECT M.mid, table1.genreList, table2.tagList\n" +
                "FROM movies M\n" +
                "LEFT JOIN (SELECT G.movieID, LISTAGG(G.genre, ', ') WITHIN GROUP (ORDER BY G.genre) AS genreList\n" +
                "           FROM Genres G\n" +
                "           GROUP BY G.movieID) table1\n" +
                "ON table1.movieID = M.mid\n" +
                "LEFT JOIN (SELECT MT.movieID, LISTAGG(T.value, ', ') WITHIN GROUP (ORDER BY T.tid) AS tagList\n" +
                "           FROM movie_tag MT, tags T\n" +
                "           WHERE MT.tagID = T.tid\n" +
                "           GROUP BY MT.movieID) table2\n" +
                "ON M.mid = table2.movieID";
        Statement create = conn.createStatement();
        create.execute(sql);
        System.out.println("Table Agg_List was built with data.");
    }

    private static void helper(String sql, String filePath, int num) throws SQLException {
        try {
            FileReader f = new FileReader(new File(filePath));
            BufferedReader bf = new BufferedReader(f);
            Scanner input = new Scanner(bf);
            PreparedStatement statement = conn.prepareStatement(sql);
            input.nextLine();
            while (input.hasNext()) {
                String nextLine = input.nextLine();
                String[] splited = nextLine.split("\\t");
                for (int i = 0; i < num; i++) {
                    statement.setString(i + 1, splited[i]);
//                System.out.println(splited[i]);
                }
                statement.executeUpdate();
            }
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }
    }
}


