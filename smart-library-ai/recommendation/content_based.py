"""
基于内容的推荐算法（Content-Based Filtering）
使用 TF-IDF 和 Jaccard 相似度分析图书简介

@author JacoryCyJin
@date 2025/05/07
"""
import logging
import re
import math
from typing import Dict, List, Tuple, Set
from collections import Counter
from sqlalchemy import create_engine, text
from config import RecommendConfig

# 尝试导入 jieba 分词
try:
    import jieba
    JIEBA_AVAILABLE = True
    logger = logging.getLogger(__name__)
    logger.info("✅ jieba 分词库已加载")
except ImportError:
    JIEBA_AVAILABLE = False
    logger = logging.getLogger(__name__)
    logger.warning("⚠️  jieba 未安装，使用简化版分词")

logging.basicConfig(level=logging.INFO)


class ContentBasedRecommender:
    """基于内容的推荐器"""
    
    def __init__(self):
        self.config = RecommendConfig()
        self.engine = create_engine(self.config.get_db_url())
        
        # 缓存数据
        self.resources = {}  # {resource_id: {'title': '', 'summary': '', 'keywords': []}}
        self.tf_idf_vectors = {}  # {resource_id: {word: tfidf_score}}
        self.idf_scores = {}  # {word: idf_score}
        
        # 中文停用词（简化版）
        self.stopwords = self._load_stopwords()
    
    def _load_stopwords(self) -> Set[str]:
        """加载中文停用词表"""
        # 简化版停用词表（实际项目中应从文件加载）
        stopwords = {
            '的', '了', '在', '是', '我', '有', '和', '就', '不', '人', '都', '一', '一个',
            '上', '也', '很', '到', '说', '要', '去', '你', '会', '着', '没有', '看', '好',
            '自己', '这', '那', '里', '为', '以', '个', '用', '来', '作', '地', '于', '出',
            '而', '与', '中', '及', '其', '或', '等', '被', '从', '由', '对', '所', '可以',
            '这个', '那个', '什么', '怎么', '为什么', '哪里', '如何', '因为', '所以', '但是',
            '然而', '虽然', '如果', '那么', '这样', '那样', '已经', '还是', '只是', '可能',
            '应该', '必须', '能够', '通过', '根据', '按照', '关于', '对于', '由于', '为了'
        }
        return stopwords
    
    def load_resources(self):
        """从数据库加载资源数据"""
        logger.info("加载资源数据...")
        
        with self.engine.connect() as conn:
            query = text("""
                SELECT resource_id, title, summary, author_name
                FROM resource
                WHERE deleted = 0 
                  AND summary IS NOT NULL 
                  AND summary != ''
                ORDER BY ctime DESC
            """)
            
            result = conn.execute(query)
            
            for row in result:
                resource_id = row[0]
                title = row[1] or ''
                summary = row[2] or ''
                author_name = row[3] or ''
                
                # 合并标题、作者、简介作为文本内容
                full_text = f"{title} {author_name} {summary}"
                
                # 分词和清洗
                keywords = self._tokenize_and_clean(full_text)
                
                self.resources[resource_id] = {
                    'title': title,
                    'summary': summary,
                    'author_name': author_name,
                    'keywords': keywords
                }
        
        logger.info(f"加载了 {len(self.resources)} 个资源")
        
        if len(self.resources) == 0:
            logger.error("没有可用的资源数据！")
            return False
        
        return True
    
    def _tokenize_and_clean(self, text: str) -> List[str]:
        """
        分词和清洗文本
        
        优先使用 jieba 分词，如果未安装则使用简化版分词
        """
        if JIEBA_AVAILABLE:
            return self._tokenize_with_jieba(text)
        else:
            return self._tokenize_simple(text)
    
    def _tokenize_with_jieba(self, text: str) -> List[str]:
        """
        使用 jieba 进行专业分词
        
        步骤：
        1. 使用 jieba 分词
        2. 去除停用词
        3. 过滤单字符和标点
        """
        # 1. jieba 分词
        words = jieba.cut(text)
        
        # 2. 清洗和过滤
        cleaned_words = []
        for word in words:
            # 去除空格
            word = word.strip()
            
            # 过滤条件：
            # - 长度至少2个字符
            # - 不在停用词表中
            # - 不是纯数字
            # - 不是纯标点
            if (len(word) >= 2 and 
                word not in self.stopwords and
                not word.isdigit() and
                not re.match(r'^[^\w]+$', word)):
                cleaned_words.append(word)
        
        return cleaned_words
    
    def _tokenize_simple(self, text: str) -> List[str]:
        """
        简化版分词（当 jieba 未安装时使用）
        
        步骤：
        1. 提取中文、英文、数字
        2. 简单分词：按空格和标点分割
        3. 提取中文词组（2-4字）
        4. 去除停用词
        """
        # 1. 提取中文、英文、数字
        text = re.sub(r'[^\u4e00-\u9fa5a-zA-Z0-9\s]', ' ', text)
        
        # 2. 简单分词：按空格和标点分割
        words = text.split()
        
        # 3. 提取中文词组（2-4字）
        chinese_words = []
        for word in words:
            if re.match(r'^[\u4e00-\u9fa5]+$', word):
                # 中文词：提取2-4字的子串
                for i in range(len(word)):
                    for length in [2, 3, 4]:
                        if i + length <= len(word):
                            chinese_words.append(word[i:i+length])
        
        # 4. 合并英文单词和中文词组
        all_words = []
        for word in words:
            if re.match(r'^[a-zA-Z]+$', word) and len(word) >= 2:
                all_words.append(word.lower())
        all_words.extend(chinese_words)
        
        # 5. 去除停用词和单字符
        cleaned_words = [
            word for word in all_words 
            if word not in self.stopwords and len(word) >= 2
        ]
        
        return cleaned_words
    
    def calculate_tf_idf(self):
        """计算 TF-IDF 权重"""
        logger.info("计算 TF-IDF 权重...")
        
        if not self.resources:
            logger.error("资源数据为空，无法计算 TF-IDF！")
            return False
        
        # 1. 计算 IDF（逆文档频率）
        total_docs = len(self.resources)
        word_doc_count = Counter()  # 统计每个词出现在多少个文档中
        
        for resource_id, data in self.resources.items():
            unique_words = set(data['keywords'])
            for word in unique_words:
                word_doc_count[word] += 1
        
        # IDF = log(总文档数 / (包含该词的文档数 + 1))
        for word, doc_count in word_doc_count.items():
            self.idf_scores[word] = math.log(total_docs / (doc_count + 1))
        
        # 2. 计算 TF-IDF
        for resource_id, data in self.resources.items():
            keywords = data['keywords']
            
            # TF（词频）
            word_count = Counter(keywords)
            total_words = len(keywords)
            
            if total_words == 0:
                self.tf_idf_vectors[resource_id] = {}
                continue
            
            # TF-IDF = TF * IDF
            tf_idf_vector = {}
            for word, count in word_count.items():
                tf = count / total_words
                idf = self.idf_scores.get(word, 0)
                tf_idf_vector[word] = tf * idf
            
            self.tf_idf_vectors[resource_id] = tf_idf_vector
        
        logger.info(f"计算了 {len(self.tf_idf_vectors)} 个资源的 TF-IDF 向量")
        logger.info(f"词汇表大小: {len(self.idf_scores)}")
        
        return True
    
    def calculate_cosine_similarity(self, resource_id1: str, resource_id2: str) -> float:
        """
        计算两个资源的余弦相似度
        
        余弦相似度 = (A·B) / (|A| * |B|)
        """
        vector1 = self.tf_idf_vectors.get(resource_id1, {})
        vector2 = self.tf_idf_vectors.get(resource_id2, {})
        
        if not vector1 or not vector2:
            return 0.0
        
        # 计算点积
        common_words = set(vector1.keys()) & set(vector2.keys())
        dot_product = sum(vector1[word] * vector2[word] for word in common_words)
        
        # 计算模长
        magnitude1 = math.sqrt(sum(score ** 2 for score in vector1.values()))
        magnitude2 = math.sqrt(sum(score ** 2 for score in vector2.values()))
        
        if magnitude1 == 0 or magnitude2 == 0:
            return 0.0
        
        return dot_product / (magnitude1 * magnitude2)
    
    def calculate_jaccard_similarity(self, resource_id1: str, resource_id2: str) -> float:
        """
        计算两个资源的 Jaccard 相似度
        
        Jaccard 相似度 = |A ∩ B| / |A ∪ B|
        """
        keywords1 = set(self.resources.get(resource_id1, {}).get('keywords', []))
        keywords2 = set(self.resources.get(resource_id2, {}).get('keywords', []))
        
        if not keywords1 or not keywords2:
            return 0.0
        
        intersection = len(keywords1 & keywords2)
        union = len(keywords1 | keywords2)
        
        if union == 0:
            return 0.0
        
        return intersection / union
    
    def calculate_hybrid_similarity(self, resource_id1: str, resource_id2: str, 
                                   cosine_weight: float = 0.7, 
                                   jaccard_weight: float = 0.3) -> float:
        """
        计算混合相似度（余弦相似度 + Jaccard 相似度）
        
        Args:
            resource_id1: 资源1的ID
            resource_id2: 资源2的ID
            cosine_weight: 余弦相似度权重（默认0.7）
            jaccard_weight: Jaccard相似度权重（默认0.3）
        
        Returns:
            混合相似度分数
        """
        cosine_sim = self.calculate_cosine_similarity(resource_id1, resource_id2)
        jaccard_sim = self.calculate_jaccard_similarity(resource_id1, resource_id2)
        
        return cosine_sim * cosine_weight + jaccard_sim * jaccard_weight
    
    def get_similar_items(self, resource_id: str, top_n: int = 10, 
                         similarity_threshold: float = 0.1) -> List[Tuple[str, float]]:
        """
        获取与指定资源最相似的物品
        
        Args:
            resource_id: 资源ID
            top_n: 返回Top-N个相似物品
            similarity_threshold: 相似度阈值
        
        Returns:
            [(resource_id, similarity_score), ...]
        """
        if resource_id not in self.resources:
            logger.warning(f"资源 {resource_id} 不存在")
            return []
        
        similarities = []
        
        for other_id in self.resources.keys():
            if other_id == resource_id:
                continue
            
            # 计算混合相似度
            similarity = self.calculate_hybrid_similarity(resource_id, other_id)
            
            if similarity >= similarity_threshold:
                similarities.append((other_id, similarity))
        
        # 排序并返回 Top-N
        similarities.sort(key=lambda x: x[1], reverse=True)
        
        return similarities[:top_n]
    
    def recommend_for_user_by_history(self, user_id: str, top_n: int = 10) -> List[Tuple[str, float]]:
        """
        基于用户浏览历史推荐
        
        策略：
        1. 获取用户浏览过的资源
        2. 找到与这些资源相似的其他资源
        3. 聚合相似度分数并排序
        
        Args:
            user_id: 用户ID
            top_n: 推荐数量
        
        Returns:
            [(resource_id, score), ...]
        """
        # 1. 获取用户浏览历史
        with self.engine.connect() as conn:
            query = text("""
                SELECT DISTINCT resource_id
                FROM user_browse_history
                WHERE user_id = :user_id
                  AND deleted = 0
                ORDER BY mtime DESC
                LIMIT 20
            """)
            
            result = conn.execute(query, {"user_id": user_id})
            browsed_ids = [row[0] for row in result]
        
        if not browsed_ids:
            logger.warning(f"用户 {user_id} 没有浏览历史")
            return []
        
        # 2. 聚合相似资源的分数
        candidate_scores = {}
        
        for browsed_id in browsed_ids:
            similar_items = self.get_similar_items(browsed_id, top_n=20)
            
            for similar_id, similarity in similar_items:
                # 跳过已浏览的资源
                if similar_id in browsed_ids:
                    continue
                
                # 累加相似度分数
                if similar_id in candidate_scores:
                    candidate_scores[similar_id] += similarity
                else:
                    candidate_scores[similar_id] = similarity
        
        # 3. 排序并返回 Top-N
        sorted_candidates = sorted(candidate_scores.items(), key=lambda x: x[1], reverse=True)
        
        return sorted_candidates[:top_n]
    
    def save_content_similarity_to_db(self):
        """
        将内容相似度矩阵保存到数据库（可选）
        
        注：由于相似度矩阵可能很大，建议只保存相似度较高的配对
        """
        logger.info("保存内容相似度到数据库...")
        
        with self.engine.connect() as conn:
            # 创建临时表（如果不存在）
            conn.execute(text("""
                CREATE TABLE IF NOT EXISTS content_similarity (
                    resource_id1 VARCHAR(50) NOT NULL,
                    resource_id2 VARCHAR(50) NOT NULL,
                    similarity DECIMAL(5, 4) NOT NULL,
                    ctime DATETIME DEFAULT CURRENT_TIMESTAMP,
                    PRIMARY KEY (resource_id1, resource_id2),
                    INDEX idx_res1 (resource_id1),
                    INDEX idx_res2 (resource_id2)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """))
            
            # 清空旧数据
            conn.execute(text("TRUNCATE TABLE content_similarity"))
            
            # 插入新数据
            count = 0
            for resource_id in self.resources.keys():
                similar_items = self.get_similar_items(resource_id, top_n=20, similarity_threshold=0.1)
                
                for similar_id, similarity in similar_items:
                    conn.execute(text("""
                        INSERT INTO content_similarity (resource_id1, resource_id2, similarity)
                        VALUES (:res1, :res2, :sim)
                    """), {
                        "res1": resource_id,
                        "res2": similar_id,
                        "sim": float(similarity)
                    })
                    count += 1
                
                if count % 100 == 0:
                    logger.info(f"已保存 {count} 条相似度记录")
            
            conn.commit()
        
        logger.info(f"内容相似度保存完成，共 {count} 条记录")
    
    def generate_content_based_recommendations(self):
        """生成基于内容的推荐（主流程）"""
        # 1. 加载资源数据
        if not self.load_resources():
            return
        
        # 2. 计算 TF-IDF
        if not self.calculate_tf_idf():
            return
        
        # 3. 保存相似度矩阵到数据库
        self.save_content_similarity_to_db()
        
        logger.info("基于内容的推荐生成完成！")
