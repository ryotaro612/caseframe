package jp.ac.titech.cs.se.nlp.entity.logic;

import java.util.List;

/**
 * 概念集合を表す論理式を表す抽象クラス
 * 
 * @author rtakizawa
 */
public abstract class ConceptLogicalFormula {

    /**
     * conceptIdに対する論理式の真偽値を返す
     * 
     * @param conceptId
     * @return
     */
    public abstract boolean eval(int conceptId);

    @Override
    public abstract String toString();

    /**
     * conceptLogicListをパースして論理式オブジェクトを返す
     * 
     * @param conceptLogicList
     * 格に対応する概念の集合を表す論理式を表すリスト
     * 例： ("|" 378040 ("&" 101030 ("!" 302410)) 202130)
     *  => 378040 または　(101030 かつ (302410でない))　または 202130の概念か、その下位概念の集合を表す
     * 
     * @return 論理式オブジェクト
     */
    @SuppressWarnings("unchecked")
    public static ConceptLogicalFormula parse(List<Object> conceptLogicList) {
        ConceptCompoundFormula formula;
        Object head = conceptLogicList.get(0);

        if (head instanceof Number) {
            return new ConceptAtomicFormula(((Number) head).intValue());
        }

        if (head.equals("!")) {
            formula = new ConceptLogicalNot();
        } else if (head.equals("&")) {
            formula = new ConceptLogicalAnd();
        } else if (head.equals("|")) {
            formula = new ConceptLogicalOr();
        } else {
            throw new IllegalStateException();
        }

        for (Object o : conceptLogicList) {
            if (o instanceof Number) {
                formula.add(new ConceptAtomicFormula(((Number) o).intValue()));
            } else if (o instanceof List) {
                formula.add(parse((List<Object>) o));
            }
        }

        return formula;
    }

}
