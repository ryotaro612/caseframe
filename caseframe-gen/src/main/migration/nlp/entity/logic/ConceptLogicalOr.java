package jp.ac.titech.cs.se.nlp.entity.logic;

/**
 * 論理和
 * 
 * @author rtakizawa
 */
public class ConceptLogicalOr extends ConceptCompoundFormula {

    @Override
    public boolean eval(int conceptId) {
        for (ConceptLogicalFormula formula : getFormulas()) {
            if (formula.eval(conceptId)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        String res = "( OR ";
        for (ConceptLogicalFormula formula : getFormulas()) {
            res += formula.toString() + " ";
        }
        res += ")";
        return res;
    }

}
