package jp.ac.titech.cs.se.nlp.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 概念間関係(上位概念、下位概念)のテーブルのDAO
 * 
 * @author rtakizawa
 */
public class CPCDao {

    public static final String ID_COL = "id";
    public static final String UPPER_COL = "upper_concept";
    public static final String LOWER_COL = "lower_concept";

    /**
     * upperConceptがlowerConceptの上位概念であるかどうか。
     * 同一である場合も真を返す。
     * 
     * @param lowerConcept 下位概念のID
     * @param upperConcept 上位概念のID
     * @return
     */
    public boolean isA(int lowerConcept, int upperConcept) {
        if (lowerConcept == upperConcept) {
            return true;
        }

        try (ResourceManagedConnection con = DBManager.connect()) {
            String query = "select " + UPPER_COL + " from cpc where " + LOWER_COL + " = ?;";
            PreparedStatement ps = con.prepareStatement(query);

            Queue<Integer> lowers = new LinkedList<Integer>();
            lowers.add(lowerConcept);
            while (!lowers.isEmpty()) {
                int lower = lowers.remove();
                ps.setInt(1, lower);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    int upper = rs.getInt(UPPER_COL);
                    if (upper == upperConcept) {
                        return true;
                    }
                    lowers.add(rs.getInt(UPPER_COL));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;
    }
}
