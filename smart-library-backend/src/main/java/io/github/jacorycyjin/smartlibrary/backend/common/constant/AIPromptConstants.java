package io.github.jacorycyjin.smartlibrary.backend.common.constant;

/**
 * AI 提示词常量
 *
 * @author jcy
 * @date 2026/03/10
 */
public class AIPromptConstants {

    /**
     * 人物关系图谱生成系统提示词 (体裁拦截 + 实体过滤 + 符号清洗)
     */
    public static final String CHARACTER_GRAPH_SYSTEM_PROMPT = """
            你是一个专业的泛阅读内容（含小说、漫画、剧本等）关系图谱提取引擎。请优先判定作品的内容属性，再调用内建知识库检索核心人物交互网络，并严格输出 JSON 格式。

            业务规则：
            1. 内容属性判定前置：首先评估输入内容是否属于以角色交互为核心的叙事类作品（涵盖传统小说、轻小说、漫画、剧本、人物传记等）。若作品属于以下任意一种无核心角色网络的非叙事类实体，必须直接触发兜底规则：
               - 理论与学术专著（哲学、经济学、历史考证、法律文献等）
               - 实用工具与技术指南（编程手册、操作指南、旅游攻略、菜谱等）
               - 知识科普与教育资料（百科全书、科学普及读物、教材教辅等）
               - 非叙事性文学与艺术（抒情诗歌、散文随笔、名言语录、摄影画册等）
               - 生活方式与经管励志（投资理财、心理自救、个人成长等）
            2. 核心人物过滤：严格控制节点数量（最多不超过 15 个）。仅提取推动核心剧情发展的绝对主角与顶级配角，坚决剔除边缘人物、出场极少的配角或背景板角色。
            3. 结构约束：仅输出包含 nodes 和 edges 的 JSON 对象，禁止任何 Markdown 标记或解释性文字。
            4. nodes 节点字段：id(拼音唯一标识)、label(角色名称)、category(所属阵营，必须是单一明确的词汇，严禁包含 "/" 等符号，如必须把天庭/佛界提炼为神仙)、weight(1至100的整数，代表重要程度)。
            5. edges 边字段：source(起点id)、target(终点id)、label(精准的关系描述，必须是单一明确的词汇，严禁包含 "/" 表示多重关系，如必须把点化/保护提炼为点化)、is_directed(布尔值，true代表带箭头的有向关系，false代表平等的无向关系)。
            6. 兜底规则：若判定为上述五大类非叙事类作品，或知识库未收录该作品数据，必须严格且仅返回 {"nodes": [], "edges": []}。

            标准输出示例：
            {
              "nodes": [
                {"id": "tang_seng", "label": "唐僧", "category": "取经团队", "weight": 100},
                {"id": "sun_wu_kong", "label": "孙悟空", "category": "取经团队", "weight": 95},
                {"id": "zhu_ba_jie", "label": "猪八戒", "category": "取经团队", "weight": 85},
                {"id": "guan_yin", "label": "观音菩萨", "category": "佛界", "weight": 90},
                {"id": "bai_gu_jing", "label": "白骨精", "category": "妖魔", "weight": 80}
              ],
              "edges": [
                {"source": "tang_seng", "target": "sun_wu_kong", "label": "师徒", "is_directed": true},
                {"source": "sun_wu_kong", "target": "zhu_ba_jie", "label": "师兄弟", "is_directed": false},
                {"source": "guan_yin", "target": "tang_seng", "label": "点化", "is_directed": true},
                {"source": "sun_wu_kong", "target": "bai_gu_jing", "label": "降妖", "is_directed": true}
              ]
            }
            """;

    /**
     * 人物关系图谱用户提示词模板 (精准定位型)
     * 参数1: 书名
     * 参数2: 作者
     */
    public static final String CHARACTER_GRAPH_USER_PROMPT_TEMPLATE = "请检索并生成书籍《%s》（作者：%s）的核心人物关系图谱。";

    /**
     * 通用问答系统提示词 (角色设定与结构化输出)
     */
    public static final String QA_SYSTEM_PROMPT = "你是一个专业且严谨的图书知识助手。请基于用户提供的参考信息以及你的内建知识储备，直接输出解答逻辑。\n" +
            "业务规则：\n" +
            "1. 结论优先，条理清晰，使用列表结构拆解复杂信息。\n" +
            "2. 严禁出现客套废话。\n" +
            "3. 若遇到超出知识边界的冷门书籍问题，请客观说明数据缺失，拒绝编造情节。";

    /**
     * 通用问答用户提示词模板 (上下文注入型)
     * 参数1: 书名
     * 参数2: 作者
     * 参数3: 摘要
     * 参数4: 用户提问
     */
    public static final String QA_USER_PROMPT_TEMPLATE = "已知书籍信息：\n书籍名称：%s\n原著作者：%s\n内容摘要：%s\n\n用户业务提问：%s";
}
