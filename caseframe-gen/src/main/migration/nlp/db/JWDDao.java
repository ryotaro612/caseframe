package jp.ac.titech.cs.se.nlp.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 日本語単語辞書テーブルのDAO
 * 
 * @author rtakizawa
 */
public class JWDDao {

    public static final String ID_COL = "id";
    public static final String NAME_COL = "name";
    public static final String READ_COL = "name_read";
    public static final String CONCEPT_COL = "concept";

    /**
     * 日本語単語に対応する概念のIDリストを返す。
     * 
     * @param jWord
     * @return
     */
    public List<Integer> getConceptIdList(String jWord) {
        List<Integer> ids = new ArrayList<Integer>();
        try (ResourceManagedConnection con = DBManager.connect()) {
            String query = "select " + CONCEPT_COL + " from jwd where " + NAME_COL + " like ?;";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, jWord);
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                ids.add(result.getInt(CONCEPT_COL));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return ids;
    }
}
