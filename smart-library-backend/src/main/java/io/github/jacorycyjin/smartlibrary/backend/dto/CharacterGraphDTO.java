package io.github.jacorycyjin.smartlibrary.backend.dto;

import lombok.Data;

import java.util.List;

/**
 * 人物关系图谱 DTO
 *
 * @author jcy
 * @date 2026/03/10
 */
@Data
public class CharacterGraphDTO {
    
    /**
     * 节点列表
     */
    private List<Node> nodes;
    
    /**
     * 边列表
     */
    private List<Edge> edges;
    
    /**
     * 节点定义
     */
    @Data
    public static class Node {
        /**
         * 节点ID（拼音唯一标识）
         */
        private String id;
        
        /**
         * 节点标签（人物名称）
         */
        private String label;
        
        /**
         * 节点分类（所属阵营）
         */
        private String category;
        
        /**
         * 重要程度（1-100）
         */
        private Integer weight;
    }
    
    /**
     * 边定义
     */
    @Data
    public static class Edge {
        /**
         * 源节点ID
         */
        private String source;
        
        /**
         * 目标节点ID
         */
        private String target;
        
        /**
         * 关系标签（如：父子、师徒、敌对）
         */
        private String label;
        
        /**
         * 是否有向关系（true=有向箭头，false=双向平等）
         */
        private Boolean isDirected;
    }
}
