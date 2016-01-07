package jp.ac.titech.cs.se.nlp.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.ac.titech.cs.se.nlp.entity.CaseFrame;

/**
 * 日本語共起関係テーブルのDAO
 * 
 * @author rtakizawa
 */
public class JCPDao {

    public static final String ID = "id";
    public static final String NAME_COL = "name";
    public static final String ACT_NAME_COL = "act_name";
    public static final String ACT_CONCEPT_COL = "act_concept";
    public static final String SLOT_COL = "slotinfo";

    /**
     * コンセプトIDのリストに対し、候補となる格フレームのリストを返す。
     * 
     * @param conceptIds
     * @return
     */
    public List<CaseFrame> getCaseFrameList(List<Integer> conceptIds) {
        List<CaseFrame> frames = new ArrayList<CaseFrame>();
        try (ResourceManagedConnection con = DBManager.connect()) {
            String query = "select " + ACT_NAME_COL + ", " + SLOT_COL + " from jcp where "
                    + ACT_CONCEPT_COL + " = ?;";
            PreparedStatement ps = con.prepareStatement(query);
            for (int id : conceptIds) {
                ps.setInt(1, id);
                ResultSet result = ps.executeQuery();
                while (result.next()) {
                    String verb = result.getString(ACT_NAME_COL);
                    String slot = result.getString(SLOT_COL);
                    frames.add(new CaseFrame(verb, slot));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return frames;
    }
}
