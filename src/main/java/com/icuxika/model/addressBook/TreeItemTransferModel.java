package com.icuxika.model.addressBook;

public class TreeItemTransferModel {

    private Long id;

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

    public TreeItemTransferModel(Long id, Boolean leaf) {
        this.id = id;
        this.leaf = leaf;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
