package jp.ac.titech.cs.se.nlp.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 概念一覧テーブルのDAO
 * 
 * @author rtakizawa
 */
public class CPHDao {

    public static final String ID_COL = "id";
    public static final String IDENT_COL = "ident";
    public static final String NAME_EN_COL = "name_en";
    public static final String NAME_JA_COl = "name_ja";
    public static final String NAME_JA_READ_COL = "namne_ja_read";
    public static final String DESC_EN_COL = "desc_en";
    public static final String DESC_JA_COL = "desc_ja";

    /**
     * conceptIdの説明を返す
     * @param conceptId
     * @return
     */
    public String getJConceptDescription(int conceptId) {
        String res = null;
        try (ResourceManagedConnection con = DBManager.connect()) {
            String query = "select " + DESC_JA_COL + " from cph where id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, conceptId);
            ResultSet rs = ps.executeQuery();
            res = (rs.next()) ? rs.getString(DESC_JA_COL) : "";
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

}
