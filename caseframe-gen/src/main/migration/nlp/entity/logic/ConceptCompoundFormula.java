package jp.ac.titech.cs.se.nlp.entity.logic;

import java.util.ArrayList;
import java.util.List;

/**
 * 合成式
 * 
 * @author rtakizawa
 */
public abstract class ConceptCompoundFormula extends ConceptLogicalFormula {

    /**
     * 内包する論理式のリスト
     */
    protected List<ConceptLogicalFormula> formulas;

    public ConceptCompoundFormula() {
        formulas = new ArrayList<ConceptLogicalFormula>();
    }

    /**
     * 内包する論理式を追加する
     * 
     * @param formula
     */
    public void add(ConceptLogicalFormula formula) {
        formulas.add(formula);
    }

    public List<ConceptLogicalFormula> getFormulas() {
        return formulas;
    }

}
