package com.zhu.spark;

import io.github.briqt.spark4j.SparkClient;
import io.github.briqt.spark4j.constant.SparkApiVersion;
import io.github.briqt.spark4j.model.SparkMessage;
import io.github.briqt.spark4j.model.SparkSyncChatResponse;
import io.github.briqt.spark4j.model.request.SparkRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class SparkManager {

    @Resource
    private SparkClient sparkClient;

    /**
     * AI生成问题的预设条件
     */
    public static final String PRECONDITION = "小红书是现在市面上比较火的种草笔记分享APP。您的任务是根据我给你的小红书笔记主题，然后你按照我下面给你的例文写一篇种草文案，文案越丰富越好。\n" +
            "示例标题:沐浴露 \n" +
            "示例种草文案:【\n" +
            "标题：沐浴露 | 让你的肌肤喝饱水的秘密！" +
            "正文：\n" +
            "亲爱的小伙伴们，今天我要跟大家分享一个我最近发现的小宝藏——超滋润沐浴露！✨" +
            "你是否也经历过那种皮肤干燥到像是沙漠一样的困扰？是不是每次洗完澡都感觉肌肤紧绷绷的，仿佛一触碰就会碎裂？别担心，这款沐浴露就是你的救星！" +
            "经过皮肤科医生推荐，它含有天然保湿因子和多种维生素，能够深入肌肤底层，为你的肌肤补充源源不断的水分。使用后，你会发现你的肌肤变得像婴儿肌一样柔嫩光滑！" +
            "而且，它还通过了敏感肌肤测试，即使是最娇嫩的肌肤也能安心使用。每次洗澡都像是给肌肤做了一次SPA，让你爱上每一次沐浴的时光。" +
            "不仅如此，它的香气也是一大亮点！花香混合果香，清新而不腻，让人心情愉悦。" +
            "相信我，一旦你尝试了它，就再也回不去了！" +
            "所以，快来一起加入“滋润肌肤”的行列吧！让我们一起享受每一次沐浴带来的美妙体验！" +
            "#沐浴露推荐 #滋润肌肤 #SPA级享受】" +
            "下面是笔记主题: \n";

    /**
     * 向星火AI发送请求
     *
     * @param content
     * @return
     */
    public String sendMesToAIUseXingHuo(final String content) {
        // 消息列表，可以在此列表添加历史对话记录
        List<SparkMessage> messages = new ArrayList<>();
        messages.add(SparkMessage.systemContent(PRECONDITION));
        messages.add(SparkMessage.userContent(content));
        // 构造请求
        SparkRequest sparkRequest = SparkRequest.builder()
                // 消息列表
                .messages(messages)
                // 模型回答的tokens的最大长度，非必传，默认为2048
                .maxTokens(2048)
                // 结果随机性，取值越高随机性越强，即相同的问题得到的不同答案的可能性越高，非必传，取值为[0,1]，默认为0.5
                .temperature(0.2)
                // 指定请求版本
                .apiVersion(SparkApiVersion.V3_5)
                .build();
        // 同步调用
        SparkSyncChatResponse chatResponse = sparkClient.chatSync(sparkRequest);
        String responseContent = chatResponse.getContent();
        log.info("星火AI返回的结果{}", responseContent);
        return responseContent;
    }

}

