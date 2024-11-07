package com.zhu.controller;

import com.zhu.exception.ApiException;
import com.zhu.exception.ErrorCode;
import com.zhu.modal.dto.ai.CopyWriterRequest;
import com.zhu.modal.entity.AIResult;
import com.zhu.spark.SparkManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AIController {

    @Resource
    private SparkManager sparkManager;

    @GetMapping("/red/writer")
    public AIResult generateWriting(CopyWriterRequest copyWriterRequest){
        if(copyWriterRequest==null){
            throw new ApiException(ErrorCode.PARAMS_ERROR);
        }
        String themeText = copyWriterRequest.getThemeText();
        if(StringUtils.isEmpty(themeText)){
            throw new ApiException(ErrorCode.PARAMS_ERROR,"笔记主题不能为空");
        }
        String resultText = sparkManager.sendMesToAIUseXingHuo(themeText);
        AIResult aiResult = new AIResult();
        aiResult.setText(resultText);
        return aiResult;
    }

}
