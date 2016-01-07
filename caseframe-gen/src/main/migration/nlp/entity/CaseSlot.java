package jp.ac.titech.cs.se.nlp.entity;

import java.util.ArrayList;
import java.util.List;

import jp.ac.titech.cs.se.nlp.entity.logic.ConceptLogicalFormula;

/**
 * 格スロット
 * 
 * @author hayashi
 * @author rtakizawa
 */
public class CaseSlot {

    /**
     * 表層格の種類("が", "を", など)
     * 表層格一覧はEDR辞書マニュアル(JCC/MANUAL/Japanese/SJIS/EDR_J07A)に記載
     * (たぶんこれ)
     */
    private String surfaceCase;

    /**
     * 格の概念の見出し
     */
    private String caption;

    /**
     * このスロットに適合する概念の集合を表す論理式
     */
    private ConceptLogicalFormula formula;

    @SuppressWarnings("unchecked")
    public CaseSlot(List<Object> caseExp) {
        List<Object> conceptList;
        surfaceCase = (String) caseExp.get(0);
        if (caseExp.get(1) instanceof List) {
            conceptList = (List<Object>) caseExp.get(1);
        } else {
            // 概念ＩＤのみの場合リストでくるむ
            conceptList = new ArrayList<Object>();
            conceptList.add(caseExp.get(1));
        }
        caption = (String) caseExp.get(2);

        formula = ConceptLogicalFormula.parse(conceptList);
    }

    /**
     * この格スロットにconceptIdの概念が入れるかどうか
     * @param conceptId
     * @return
     */
    public boolean accept(int conceptId) {
        return formula.eval(conceptId);
    }

    // とりあえず
    @Override
    public String toString() {
        return "<" + caption + ">" + surfaceCase;
    }

    public String getSurfaceCase() {
        return surfaceCase;
    }

    public String getCaption() {
        return caption;
    }

    public String getFormulaString() {
        return formula.toString();
    }
}