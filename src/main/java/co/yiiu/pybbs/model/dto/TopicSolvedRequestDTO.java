package co.yiiu.pybbs.model.dto;

public class TopicSolvedRequestDTO {
    private Integer id; // 话题ID
    private Boolean solved; // 解决状态

    // get set
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getSolved() {
        return solved;
    }

    public void setSolved(Boolean solved) {
        this.solved = solved;
    }
}
