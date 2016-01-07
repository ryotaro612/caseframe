package jp.ac.titech.cs.se.nlp.entity.logic;

import jp.ac.titech.cs.se.nlp.db.CPCDao;

/**
 * 原子式
 * 
 * @author rtakizawa
 */
public class ConceptAtomicFormula extends ConceptLogicalFormula {

    private int id;
    private CPCDao dao;

    public ConceptAtomicFormula(int id) {
        this.id = id;
        dao = new CPCDao();
    }

    @Override
    public boolean eval(int conceptId) {
        // eval対象の概念が、この原子式がもつ概念もしくはその下位概念であれば真、そうでなければ偽
        return dao.isA(conceptId, id);
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }

}
