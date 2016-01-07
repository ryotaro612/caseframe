package jp.ac.titech.cs.se.nlp.strategy;

import jp.ac.titech.cs.se.nlp.cabocha.Sentence;
import jp.ac.titech.cs.se.nlp.entity.CaseFrame;

/**
 * 格とchunksの点数づけの戦略
 * 
 * @author rtakizawa
 */
public interface NlpStrategy {

    /**
     * 評価関数
     * @param frame 格フレーム
     * @param s 原文
     * @return
     */
    public double eval(CaseFrame frame, Sentence s);

}
