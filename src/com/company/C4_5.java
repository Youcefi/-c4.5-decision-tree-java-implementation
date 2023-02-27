package com.company;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;

public class C4_5 {

    private static ArrayList attribut=new ArrayList(){{add("Visibilite");add("Température");add("Humidité");add("Vent");}};

    /**
     * Méthode permet de se connecter avec la base de donnée
     **/

    private static Connection connect() {

        String url = "jdbc:sqlite:c4.5.db";
        Connection c = null;
        try {
            c = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return c;
    }

    /**
     * Méthode permet de Calculer logarithme base 2
     **/

    private static double log_2(double X){
        double log;
        if (X==0){
            return 0;
        }else {
            log = ((Math.log10(X)) / (Math.log10(2)));
            return log;

        }
    }
    /**
     * Méthode qui calcule l'entropie de l'ensemble de données S [𝑬𝒏𝒕𝒓𝒐𝒑𝒊𝒆(𝑷)= −Σ𝒑𝒊 × 𝒍𝒐𝒈(𝒑𝒊)]
     **/
    private static double Entropie_S(String table) {
        double entropie = 0;
        try (Connection c = connect()) {
            PreparedStatement ps, ps1, ps2;
            double P1;
            double P2;
            ResultSet rc, rc1, rc2;
            String sql_all = "select count(class) from " + table;
            String sql_Yes = "select count(class) from " + table + " where class=\"Oui\"";
            String sql_No  = "select count(class) from " + table + " where class=\"Non\"";

            ps  = c.prepareStatement(sql_all);
            ps1 = c.prepareStatement(sql_Yes);
            ps2 = c.prepareStatement(sql_No);

            rc  = ps.executeQuery();
            rc1 = ps1.executeQuery();
            rc2 = ps2.executeQuery();

            int countAll = rc.getInt(1);
            int countYes = rc1.getInt(1);
            int countNo  = rc2.getInt(1);

            P1 = (double) (countYes) / (countAll);
            P2 = (double) (countNo) / (countAll);
            entropie = -P1 * log_2(P1) - P2 * log_2(P2);
            // System.out.println("L'entopie S est : "+entropie);
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return entropie;
    }
    private static ArrayList<Integer> idOfUnknowValue(String table) {
        ArrayList<Integer> valeurs  = new ArrayList<>();
        Connection c = connect();
        int i=0;
        while (i<attribut.size()) {
            try {
                Statement stmt = c.createStatement();
                String sql = "select id from " + table + " where " + attribut.get(i) + " = '?' ";
                ResultSet rc = stmt.executeQuery(sql);
                while (rc.next()) {
                    int v = rc.getInt(1);
                    valeurs.add(v);
                }

            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
            i++;
        }
        // System.out.println("la liste des id qui ont la valeur inconnue est : " + valeurs);
        return valeurs;
    }


    private static ArrayList noDuplicate(String table) {
        ArrayList listWithDuplicates =idOfUnknowValue(table);
        ArrayList listWithoutDuplicates = new ArrayList<>(new HashSet(listWithDuplicates));
        System.out.println(listWithoutDuplicates);
        return listWithoutDuplicates;
    }
/*
    private int yesUnknowValue(String table){
        int nbYes = 0;
        int i=0;
        Connection c = connect();
        while (i<noDuplicate(table).size()) {
            try {
                Statement stmt = c.createStatement();
                String sqlUnknowValue="select class from " + table + " where id = " + noDuplicate(table).get(i);

                //PreparedStatement psUnknowValue = c.prepareStatement(sqlUnknowValue);
                ResultSet rcUnknowValue  = stmt.executeQuery(sqlUnknowValue);

                if (rcUnknowValue.getString(1)=="Oui"){
                    nbYes++;

                }
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
            i++;
        }
        System.out.println(nbYes);
        return nbYes;
    }*/
    private static String yesUnknowValue(String table,int id) {

        String branch = null;
        int nbYes = 0;
        int i=0;
       // while (i<noDuplicate(table).size()) {
        try (Connection c = connect()) {
            PreparedStatement ps;
            ResultSet rc;
            String sql = "select class from " + table + " where id = " + id;
            ps = c.prepareStatement(sql);
            rc = ps.executeQuery();
            while (rc.next()) {
                branch = rc.getString(1);

            }

        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        System.out.println(branch);
       /* i++;
        }*/

        return branch;
    }

    private static double EntropieSUnknowValue(String table) {
        double entropie = 0;
        try (Connection c = connect()) {
            PreparedStatement ps, ps1, ps2;
            double P1;
            double P2;
            int nbYes = 0,nbNo=0;
            ResultSet rc, rc1, rc2;
            String sql_all = "select count(class) from " + table;
            String sql_Yes = "select count(class) from " + table + " where class=\"Oui\"";
            String sql_No  = "select count(class) from " + table + " where class=\"Non\"";

            ps  = c.prepareStatement(sql_all);
            ps1 = c.prepareStatement(sql_Yes);
            ps2 = c.prepareStatement(sql_No);

             rc  = ps.executeQuery();
            rc1 = ps1.executeQuery();
            rc2 = ps2.executeQuery();
            for (int i=0;i<noDuplicate(table).size();i++){
                if ((yesUnknowValue(table, (Integer) noDuplicate(table).get(i)))=="Oui"){
                    nbYes++;
                }
            }
            System.out.println(nbYes);
            int countAll = rc.getInt(1)-(noDuplicate(table).size());
            int countYes = rc1.getInt(1)-nbYes;
            int countNo  = rc2.getInt(1)-nbNo;

            P1 = (double) (countYes) / (countAll);
            P2 = (double) (countNo) / (countAll);
            entropie = -P1 * log_2(P1) - P2 * log_2(P2);
             System.out.println("L'entopie S est : "+entropie);
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return entropie;
    }

    /**
     * Méthode qui permet de calculer l'entropie pour une valeur donnée (Nominale)
     */
    private double entropie(String attribut,String valeur,String table){
        double p1,p2,entropie =0;
        try (Connection c = connect()) {
            PreparedStatement ps_all, ps_yes, ps_no;
            ResultSet rc_all, rc_yes, rc_no;
            String sql_all = "select count(" + attribut + ") from " + table + " where " + attribut + "='" + valeur + "'";
            String sql_yes = "select count(" + attribut + ") from " + table + " where class=\"Oui\" and " + attribut + "='" + valeur + "'";
            String sql_no = "select count(" + attribut + ") from " + table + " where class=\"Non\" and " + attribut + "='" + valeur + "'";

            ps_all = c.prepareStatement(sql_all);
            ps_yes = c.prepareStatement(sql_yes);
            ps_no  = c.prepareStatement(sql_no);

            rc_all = ps_all.executeQuery();
            rc_yes = ps_yes.executeQuery();
            rc_no  = ps_no.executeQuery();

            int count_all = rc_all.getInt(1);
            int count_yes = rc_yes.getInt(1);
            int count_no = rc_no.getInt(1);

            p1 = (double) (count_yes) / (count_all);
            p2 = (double) (count_no) / (count_all);
            entropie = -p1 * log_2(p1) - p2 * log_2(p2);
            //System.out.println("L'entropie de " + valeur + " est:  " + entropie);
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return entropie;
    }

    /**
     * Méthode qui permet de calculer l'entropie d'un attribut continue inferieure à une valeur donnée
     **/
    private double entropieInf(String attribut,int valeur,String table){
        double p1,p2,entropie =0;
        try (Connection c = connect()) {
            PreparedStatement ps_all, ps_yes, ps_no;
            ResultSet rc_all, rc_yes, rc_no;
            //  select count(Humidité) from Jouer where Humidité<=65;
            String sql_all = "select count(" + attribut + ") from " + table + " where " + attribut + "<=" + valeur;
            //select count(Humidité) from Jouer where Humidité<=65 and Jouer="Oui";
            String sql_yes = "select count(" + attribut + ") from " + table + " where class=\"Oui\" and " + attribut + "<=" + valeur;
            //select count(Humidité) from Jouer where Humidité<=65 and Jouer="Non";
            String sql_no = "select count(" + attribut + ") from " + table + " where class=\"Non\" and " + attribut + "<=" + valeur ;

            ps_all = c.prepareStatement(sql_all);
            ps_yes = c.prepareStatement(sql_yes);
            ps_no  = c.prepareStatement(sql_no);

            rc_all = ps_all.executeQuery();
            rc_yes = ps_yes.executeQuery();
            rc_no  = ps_no.executeQuery();

            int count_all = rc_all.getInt(1);
            int count_yes = rc_yes.getInt(1);
            int count_no = rc_no.getInt(1);

            p1 = (double) (count_yes) / (count_all);
            p2 = (double) (count_no) / (count_all);
            entropie = -p1 * log_2(p1) - p2 * log_2(p2);
            //System.out.println("L'entropie de " + valeur + " est:  " + entropie);
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return entropie;
    }
    /**
     * Méthode qui permet de calculer l'entropie d'un attribut continue superieure à une valeur donnée
     **/

    private double entropieSup(String attribut,int valeur,String table){
        double p1,p2,result = 0,entropie =0;
        try (Connection c = connect()) {
            PreparedStatement ps_all, ps_yes, ps_no;
            ResultSet rc_all, rc_yes, rc_no;
            //  select count(Humidité) from Jouer where Humidité>65;
            String sql_all = "select count(" + attribut + ") from " + table + " where " + attribut + ">" + valeur;
            // select count(Humidité) from Jouer where Humidité>65 and Jouer="Oui";
            String sql_yes = "select count(" + attribut + ") from " + table + " where class=\"Oui\" and " + attribut + ">" + valeur;
            //select count(Humidité) from Jouer where Humidité>65 and Jouer="Non";
            String sql_no = "select count(" + attribut + ") from " + table + " where class=\"Non\" and " + attribut + ">" + valeur ;

            ps_all = c.prepareStatement(sql_all);
            ps_yes = c.prepareStatement(sql_yes);
            ps_no  = c.prepareStatement(sql_no);

            rc_all = ps_all.executeQuery();
            rc_yes = ps_yes.executeQuery();
            rc_no  = ps_no.executeQuery();

            int count_all = rc_all.getInt(1);
            int count_yes = rc_yes.getInt(1);
            int count_no = rc_no.getInt(1);
            if(count_all==0){
                result=0;
            }else {
                p1 = (double) (count_yes) / (count_all);
                p2 = (double) (count_no) / (count_all);
                entropie = -p1 * log_2(p1) - p2 * log_2(p2);
               result=entropie;
            }
            //System.out.println("L'entropie de " + valeur + " est:  " + entropie);
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return result;
    }
    /**
     * Méthode qui permet de calculer l'info(S,T) d'un attribut continue
     **/
    private double info(String table,String attribut ,int valeur){
        double p1,p2,info = 0;
        try (Connection c = connect()) {
            PreparedStatement ps_all, ps_Inf, ps_Sup;
            ResultSet rc_all, rc_Inf, rc_Sup;

            String sql_all = "select count(" + attribut + ") from " + table ;
            String sqlInf = "select count(" + attribut + ") from " + table + " where " + attribut + "<=" + valeur;
            String sqlSup = "select count(" + attribut + ") from " + table + " where " + attribut + ">" + valeur;


            ps_all = c.prepareStatement(sql_all);
            ps_Inf = c.prepareStatement(sqlInf);
            ps_Sup  = c.prepareStatement(sqlSup);

            rc_all = ps_all.executeQuery();
            rc_Inf = ps_Inf.executeQuery();
            rc_Sup  = ps_Sup.executeQuery();

            int count_all = rc_all.getInt(1);
            int count_Inf = rc_Inf.getInt(1);
            int count_Sup = rc_Sup.getInt(1);

            double v1=entropieInf(attribut,valeur,table);
            double v2=entropieSup(attribut,valeur,table);

            p1 =  (double)(count_Inf) / (count_all);
            p2 =  (double)(count_Sup) / (count_all);
            info=(p1 * v1)+(p2 * v2);

        } catch (SQLException e) {
            System.out.println(e.toString());
        }
       // System.out.println("info "+info);
        return info;

    }
    /**
     * Méthode qui permet de calculer le gain d'un attribut continue
     **/
    private double gainContinue(String table,String Attribut ,int valeur){
        //System.out.println("gain valeur Continue est "+gain);
        return Entropie_S(table)-info(table,Attribut,valeur);
    }
    /**
     * Méthode qui permet de retourner la valeur qui a le gain le plus élevé
     **/
    private double bestGainContinue(String table, String Attribut){
        double max=0;

        for (int i=0 ; i<sortValues(table,Attribut).size() ;i++){
            double gain = gainContinue(table,Attribut,sortValues(table,Attribut).get(i));
            if (max<gain) {
                max=gain;

            }
        }

        return max;
    }
    private int nodeBestGainContinue(String table, String Attribut){
        double max=0;
        int node = 0;
        for (int i=0 ; i<sortValues(table,Attribut).size() ;i++){
            double gain = gainContinue(table,Attribut,sortValues(table,Attribut).get(i));
            if (max<gain) {
                max=gain;
                node= sortValues(table,Attribut).get(i);
            }
        }
        //System.out.println("L'attribut "+node+" a le gain le plus élevé sa valeur est " + max);
        return node;
    }
    /**
     * Méthode qui permet de calculer le gain d'un attribut donnée
     */

    private double Gain(String attribut,ArrayList<String>valeurs,String table){
        double gain = 0;
        double entro = 0;
        double P;
        try (Connection c = connect()) {
            PreparedStatement ps1, ps2;
            ResultSet rc1, rc2;
            for (String valeur : valeurs) {
                String sql     = "select count(" + attribut + ") from " + table + " where " + attribut + "='" + valeur + "'";
                String sql_all = "select count(class) from " + table;

                ps1 = c.prepareStatement(sql);
                ps2 = c.prepareStatement(sql_all);

                rc1 = ps1.executeQuery();
                rc2 = ps2.executeQuery();

                int count = rc1.getInt(1);
                int count_all = rc2.getInt(1);

                P = (double) (count) / (count_all);
                entro -= (P * entropie(attribut, valeur, table));
                gain = Entropie_S(table) + entro;
            }
            //System.out.println("Le gain de " + attribut + " est=" + gain + "\n");
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return gain;
    }

    /**
     * Select les valeurs d'un attribut et la mettre dans array list triéé
     **/

    private ArrayList<Integer> sortValues(String table,String Attribut) {
        ArrayList<Integer> valeurs  = new ArrayList<>();
        Connection c = connect();

        try {
            Statement stmt = c.createStatement();
            String sql = "SELECT DISTINCT " + Attribut + " from " + table + " ORDER BY " + Attribut + " ASC;";
            ResultSet rc = stmt.executeQuery(sql);
            while (rc.next()) {
                int trier = rc.getInt(1);
                valeurs.add(trier);
            }

            // System.out.println("la liste triee est : " + valeurs);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return valeurs;
    }
    /**
     * Méthode qui permet de retourner la liste de toute les gain des attribut nominal et continue
     **/

    private ArrayList<Double> listGain(String table){
        double gain;
        ArrayList<Double>list_gain=new ArrayList<>();
        for (int i=0;i<attribut.size();i++){
            String value=(distinct(table, (String) attribut.get(i))).get(0);
        try
        {
           Integer.decode(value);
           gain=bestGainContinue(table, (String) attribut.get(i));
        }catch(NumberFormatException  e)
        {
          gain=Gain((String)attribut.get(i),distinct(table,(String)attribut.get(i)),table);
        }
          list_gain.add(gain);
       }
       //System.out.println("La liste des gains est : "+listGain);
       return list_gain;
    }
    /**
     * Méthode qui permet de vérifier si les valeurs d'un attribut donner est continue ou non
     **/
    private boolean continuousValue(String table,String Attribut) {

        boolean result ;

            String value = (distinct(table, Attribut)).get(0);
            try {
                Integer.decode(value);
                result = true;
            } catch (NumberFormatException e) {
                result = false;
            }
           // System.out.println("result : "+result);
            return result;
    }

    /**
     * Méthode qui permet de décider si on peut jouer ou non
     */
    private boolean JouerOrNot(String table) {
        boolean test = false;
        try (Connection c = connect()) {
            PreparedStatement ps_all, ps_yes, ps_no;
            ResultSet rc_all, rc_yes, rc_no;
            String sql_all = "select count(class) from " + table;
            String sql_yes = "select count(class) from " + table + " where class=\"Oui\" ";
            String sql_no = "select count(class) from " + table + " where class=\"Non\" ";

            ps_all = c.prepareStatement(sql_all);
            ps_yes = c.prepareStatement(sql_yes);
            ps_no  = c.prepareStatement(sql_no);

            rc_all = ps_all.executeQuery();
            rc_yes = ps_yes.executeQuery();
            rc_no  = ps_no.executeQuery();

            int count_all = rc_all.getInt(1);
            int count_yes = rc_yes.getInt(1);
            int count_no  = rc_no.getInt(1);

            double P_yes = (double) count_yes / count_all;
            double P_no  = (double) count_no / count_all;

            if (P_yes == 1) {
                test = true;
            } else if (P_no == 1) {
                test = false;
            }
        } catch (SQLException e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return test;
    }

    /**
     * Méthode permet de retourner la valeur ou l'attribut Jouer égale a Oui d'un noeud donnée
     */
    private String selectValueYes(String table,String noeud) {

        String branch = null;
        try (Connection c = connect()) {
            PreparedStatement ps;
            ResultSet rc;
            String sql =  "select distinct " + noeud + " from " + table + " where class=\"Oui\" ";
            ps = c.prepareStatement(sql);
            rc = ps.executeQuery();
            while (rc.next()) {
                branch = rc.getString(noeud);
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        //System.out.println(branch);
        return branch;
    }


    /**
     * Méthode permet de retourner la valeur ou l'attribut Jouer égale a Non d'un noeud donnée
     */
    private String selectValueNo(String table,String noeud) {

        String branch = null;
        try (Connection conn = connect()) {
            PreparedStatement  ps;
            ResultSet  rc;
            String sql = "select distinct " + noeud + " from " + table + " where class=\"Non\" ";

            ps = conn.prepareStatement(sql);

            rc = ps.executeQuery();
            while (rc.next()) {
                branch = rc.getString(noeud);
            }

        } catch (SQLException e) {
            System.out.println(e.toString());
        }

        return branch;
    }
    private String selectValueContinueInf(String table,String node,int value) {
        // select distinct Jouer from '0' where Humidité <=70;
        String branch = null;
        try (Connection c = connect()) {
            PreparedStatement ps;
            ResultSet rc;
            String sql =  "select distinct class from " + table + " where " + node + " <= " + value;
            ps = c.prepareStatement(sql);
            rc = ps.executeQuery();
            while (rc.next()) {
                branch = rc.getString(1);

            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        //System.out.println(branch);
        return branch;
    }
    private String selectValueContinueSup(String table,String node,int value) {
        // select distinct Jouer from '0' where Humidité >70;
        String branch = null;
        try (Connection c = connect()) {
            PreparedStatement ps;
            ResultSet rc;
            String sql =  "select distinct class from " + table + " where " + node + " > " + value;
            ps = c.prepareStatement(sql);
            rc = ps.executeQuery();
            while (rc.next()) {
                branch = rc.getString(1);

            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        //System.out.println(branch);
        return branch;
    }

    /**
     * Select les valeurs d'un attribut et la mettre dans array list
     **/
    private ArrayList<String> distinct(String table, String attribut) {
        //  String sql = "SELECT DISTINCT " + attribut + "from Jouer";
        ArrayList<String> valeurs = new ArrayList<>();
        Connection c= connect();
        try {
            Statement stmt = c.createStatement();

            String sql=" SELECT DISTINCT " + attribut + " from " + table ;
            ResultSet rc = stmt.executeQuery(sql);
            while (rc.next()){
                String list = rc.getString(1);
                valeurs.add(list);
            }

        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        //System.out.println("la liste est "+ valeurs);
        return valeurs;
    }


    /**
     * Méthode qui permet de retourner le noeud qui a le gain le plus élevé
     */

    private String bestGain(String table){
        double max=0;
        String node = null;
        for (int i=0 ; i<listGain(table).size() ;i++){

            double gain=listGain(table).get(i);
            if (max<gain) {
                max=gain;
                node= (String)attribut.get(i);

            }
        }
        //System.out.println("L'attribut "+node+" a le gain le plus élevé sa valeur est " + max);
        return node;
    }

    /**
     * Méthode permet de retourner la liste des banches d'une table donnée
     */
    private ArrayList<String> Listbranch(String table){
        String node = bestGain(table);
        ArrayList<String> branches = null;
        int i=0;
        while (i < attribut.size()) {
            if(node == attribut.get(i)){
                branches= distinct(table,(String)attribut.get(i));
            }
            i++;
        }
        //System.out.println(branches);
        return branches;

    }


    /**
     * Méthode qui permet de creer une view
     */
    private void createView(String branche,String i,String table) {
        Connection c= connect();
        String racine=bestGain(table);

        try {
            Statement stmt = c.createStatement();
            String sql="create view if not exists '" + i + "' as select * from " + table + " where " + racine + "='" + branche + "'";
            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

    /**
     * Méthode qui permet de classifier l'arbre de décision et exprimé sous forme de règles
     */
    private void classification(String table) {

        int i = 0;
        while (i < Listbranch(table).size()) {
            createView(Listbranch(table).get(i), String.valueOf(i), table);
            if (Entropie_S("'" + i + "'") == 0 && JouerOrNot("'" + i + "'")) {
                System.out.println("Si "+bestGain(table)+"= "+Listbranch(table).get(i)+" Alors Jouer=Oui");
            } else if (Entropie_S("'" + i + "'") == 0 && !JouerOrNot("'" + i + "'")) {
                System.out.println("Si "+bestGain(table)+"= "+Listbranch(table).get(i)+" Alors Jouer=Non");
            }else if (Entropie_S("'" + i + "'")!=0){

                if (!continuousValue("'" + i + "'", bestGain("'" + i + "'"))){

                    System.out.println("Si " + bestGain(table) + "= " +Listbranch(table).get(i) + " et " + bestGain("'" + i + "'")+
                            " = " + selectValueYes("'" + i + "'",bestGain("'" + i + "'")) + " Alors Jouer = Oui" );

                    System.out.println("Si "+bestGain(table)+"= "+Listbranch(table).get(i)+" et "+bestGain("'" + i + "'")+
                        " = " + selectValueNo("'" + i + "'",bestGain("'" + i + "'")) + " Alors Jouer = Non" );
                }else {


                    System.out.println("Si " + bestGain(table) + "= " + Listbranch(table).get(i) + " et " + bestGain("'" + i + "'")+
                            " <= " + nodeBestGainContinue("'" + i + "'",bestGain("'" + i + "'")) + " Alors Jouer = "
                            +(selectValueContinueInf("'" + i + "'", bestGain("'" + i + "'"),nodeBestGainContinue("'" + i + "'",bestGain("'" + i + "'")))));


                    System.out.println("Si " + bestGain(table) + "= " + Listbranch(table).get(i) + " et " + bestGain("'" + i + "'")+
                            " > " + nodeBestGainContinue("'" + i + "'",bestGain("'" + i + "'")) + " Alors Jouer = "
                            +(selectValueContinueSup("'" + i + "'", bestGain("'" + i + "'"),nodeBestGainContinue("'" + i + "'",bestGain("'" + i + "'")))));
                }
            }
            i++;
        }
    }
    public static void main(String[] args) {

        C4_5 tree = new C4_5();
        System.out.println("L'entropie de l'ensemble de données S est : " + Entropie_S("Play")+"\n");
        System.out.println("le noeud racine dans notre arbre de décision est : " + tree.bestGain("Play")+"\n");
        tree.classification("Play");

    }
}