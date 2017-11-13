package com.lc.nlp4han.dependency;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import opennlp.tools.ml.BeamSearch;
import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.ml.model.SequenceClassificationModel;
import opennlp.tools.util.model.BaseModel;

/**
 * 封装训练得到的模型
 * @author 王馨苇
 *
 */
public class DependencyParseModel extends BaseModel{

	private static final String COMPONENT_NAME = "DependencyME";
	private static final String PHRASE_MODEL_ENTRY_NAME = "dependency.model";
	
	/**
	 * 构造
	 * @param componentName 训练模型的类
	 * @param modelFile 模型文件
	 * @throws IOException IO异常
	 */
	protected DependencyParseModel(String componentName, File modelFile) throws IOException {
		super(COMPONENT_NAME, modelFile);
		
	}

	/**
	 * 构造
	 * @param languageCode 编码
	 * @param posModel 最大熵模型
	 * @param beamSize 大小
	 * @param manifestInfoEntries 配置的信息
	 */
	public DependencyParseModel(String languageCode, MaxentModel posModel, int beamSize,
			Map<String, String> manifestInfoEntries) {
		super(COMPONENT_NAME, languageCode, manifestInfoEntries, null);
		if (posModel == null) {
            throw new IllegalArgumentException("The maxentPosModel param must not be null!");
        }

        Properties manifest = (Properties) artifactMap.get(MANIFEST_ENTRY);
        manifest.setProperty(BeamSearch.BEAM_SIZE_PARAMETER, Integer.toString(beamSize));

        //放入新训练出来的模型
        artifactMap.put(PHRASE_MODEL_ENTRY_NAME, posModel);
        checkArtifactMap();
	}
	

	public DependencyParseModel(String languageCode, SequenceClassificationModel<String> seqPosModel,
			Map<String, String> manifestInfoEntries) {
		super(COMPONENT_NAME, languageCode, manifestInfoEntries, null);
		if (seqPosModel == null) {
            throw new IllegalArgumentException("The maxent wordsegModel param must not be null!");
        }

        artifactMap.put(PHRASE_MODEL_ENTRY_NAME, seqPosModel);
		
	}

	/**
	 * 获取模型
	 * @return 最大熵模型
	 */
	public MaxentModel getModel() {
		if (artifactMap.get(PHRASE_MODEL_ENTRY_NAME) instanceof MaxentModel) {
            return (MaxentModel) artifactMap.get(PHRASE_MODEL_ENTRY_NAME);
        } else {
            return null;
        }
	}
}
