package jp.ac.titech.cs.se.nlp.entity.logic;

/**
 * 否定
 * 
 * @author rtakizawa
 */
public class ConceptLogicalNot extends ConceptCompoundFormula {
    
    @Override
    public boolean eval(int conceptId) {
        if (getFormulas().size() != 1) {
            throw new IllegalStateException();
        }
        ConceptLogicalFormula formula = getFormulas().get(0);
        return !formula.eval(conceptId);
    }
    
    @Override
    public String toString() {
        String res = "( NOT ";
        for (ConceptLogicalFormula formula : getFormulas()) {
            res += formula.toString() + " ";
        }
        res += ")";
        return res;
    }

}
