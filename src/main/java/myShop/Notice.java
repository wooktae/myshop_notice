package myShop;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="Notice_table")
public class Notice {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long orderId;
    private String notiStatus;

    @PostPersist
    public void onPostPersist(){
        NoticeSended noticeSended = new NoticeSended();
        BeanUtils.copyProperties(this, noticeSended);
        noticeSended.publishAfterCommit();


        CancelNoticeSended cancelNoticeSended = new CancelNoticeSended();
        BeanUtils.copyProperties(this, cancelNoticeSended);
        cancelNoticeSended.publishAfterCommit();


    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    public String getNotiStatus() {
        return notiStatus;
    }

    public void setNotiStatus(String notiStatus) {
        this.notiStatus = notiStatus;
    }




}
