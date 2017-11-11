package com.lc.nlp4han.pos.word;


import com.lc.nlp4han.pos.ConfusionMatrix;

import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerEvaluationMonitor;

public class WordPOSConfusionMatrixBuilder implements POSTaggerEvaluationMonitor
{
    private ConfusionMatrix matrix = new ConfusionMatrix();

    @Override
    public void missclassified(POSSample reference, POSSample prediction)
    {
        String[] refTags = reference.getTags();
        String[] preTags = prediction.getTags();
        
        matrix.add(refTags, preTags);
    }

    @Override
    public void correctlyClassified(POSSample reference, POSSample prediction)
    { 
        String[] refTags = reference.getTags();
        String[] preTags = prediction.getTags();
        
        matrix.add(refTags, preTags);
    }
    
    public ConfusionMatrix getMatrix()
    {
        return matrix;
    }
}
