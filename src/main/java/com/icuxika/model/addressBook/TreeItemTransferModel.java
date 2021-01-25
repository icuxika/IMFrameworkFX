package com.icuxika.model.addressBook;

public class TreeItemTransferModel {

    private Long id;

    private Long parentId;

    /**
     * 标记该节点是否是叶子节点
     */
    private Boolean leaf;

    /**
     * 排序
     */
    private Integer rank;

    public TreeItemTransferModel() {
    }

    public TreeItemTransferModel(Long id, Long parentId, Boolean leaf) {
        this.id = id;
        this.parentId = parentId;
        this.leaf = leaf;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Boolean getLeaf() {
        return leaf;
    }

    public void setLeaf(Boolean leaf) {
        this.leaf = leaf;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }
}
