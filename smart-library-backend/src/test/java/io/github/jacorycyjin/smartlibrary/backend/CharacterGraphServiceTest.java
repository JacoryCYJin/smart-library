package io.github.jacorycyjin.smartlibrary.backend;

import io.github.jacorycyjin.smartlibrary.backend.dto.CharacterGraphDTO;
import io.github.jacorycyjin.smartlibrary.backend.service.CharacterGraphService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * AI 人物关系图谱服务测试
 *
 * @author jcy
 * @date 2026/03/10
 */
@Slf4j
@SpringBootTest
public class CharacterGraphServiceTest {
    
    @jakarta.annotation.Resource
    private CharacterGraphService characterGraphService;
    
    /**
     * 测试生成人物关系图谱
     */
    @Test
    public void testGenerateCharacterGraph() {
        String bookDescription = "《三国演义》是中国古典四大名著之一。故事讲述了东汉末年到西晋初年之间近百年的历史风云。" +
                "主要人物包括：刘备，字玄德，蜀汉开国皇帝，仁德之君；关羽，字云长，刘备结义兄弟，忠义无双；" +
                "张飞，字翼德，刘备结义兄弟，勇猛善战；诸葛亮，字孔明，刘备军师，智谋超群；" +
                "曹操，字孟德，魏国奠基者，雄才大略；孙权，字仲谋，东吴开国皇帝，善于用人。" +
                "刘备、关羽、张飞桃园三结义，结为生死兄弟。诸葛亮辅佐刘备建立蜀汉。曹操与刘备、孙权形成三足鼎立之势。";
        
        log.info("开始测试生成人物关系图谱");
        log.info("输入文本长度: {} 字符", bookDescription.length());
        
        try {
            CharacterGraphDTO graph = characterGraphService.generateCharacterGraph(bookDescription);
            
            log.info("✅ 图谱生成成功！");
            log.info("节点数量: {}", graph.getNodes().size());
            log.info("边数量: {}", graph.getEdges().size());
            
            log.info("\n节点列表:");
            graph.getNodes().forEach(node -> {
                log.info("  - ID: {}, 名称: {}, 分类: {}", node.getId(), node.getLabel(), node.getCategory());
            });
            
            log.info("\n关系列表:");
            graph.getEdges().forEach(edge -> {
                log.info("  - {} -> {} ({})", edge.getSource(), edge.getTarget(), edge.getLabel());
            });
            
        } catch (Exception e) {
            log.error("❌ 图谱生成失败", e);
            throw e;
        }
    }
}
