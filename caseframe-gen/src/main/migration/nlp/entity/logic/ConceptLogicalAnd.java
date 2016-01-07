package jp.ac.titech.cs.se.nlp.entity.logic;

/**
 * 論理積
 * 
 * @author rtakizawa
 */
public class ConceptLogicalAnd extends ConceptCompoundFormula {
    
    @Override
    public boolean eval(int conceptId) {
        for (ConceptLogicalFormula formula : getFormulas()) {
            if (!formula.eval(conceptId)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        String res = "( AND ";
        for (ConceptLogicalFormula formula : getFormulas()) {
            res += formula.toString() + " ";
        }
        res += ")";
        return res;
    }

}
