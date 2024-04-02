package org.guce.process.atm.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.guce.core.entities.CoreProcessing;
import org.guce.core.entities.CoreUser;

@Entity
@Table(
        name = "ATM_AVISTECH_HY"
)
public class AvisTechHistory implements Serializable {
    @Id
    @SequenceGenerator(
            name = "ATM_AVISTECH_HY_SEQ",
            sequenceName = "ATM_AVISTECH_HY_SEQ",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ATM_AVISTECH_HY_SEQ"
    )
    private String id;

    @Column(
            name = "BEFORE_MODIFICATION"
    )
    @Lob
    private String beforeModification;

    @Column(
            name = "AFTER_MODIFICATION"
    )
    @Lob
    private String afterModification;

    @ManyToOne
    @JoinColumn(
            name = "ITEM_ID"
    )
    private AvisTech itemId;

    @ManyToOne
    @JoinColumn(
            name = "PROCESSING_ID"
    )
    private CoreProcessing processingId;

    @Column(
            name = "MODIFY_DATE"
    )
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifyDate;

    @ManyToOne
    @JoinColumn(
            name = "MODIFY_USER"
    )
    private CoreUser modifyUser;

    @Column(
            name = "TYPE"
    )
    private String type;

    public String getBeforeModification() {
        return this.beforeModification;
    }

    public void setBeforeModification(String beforeModification) {
        this.beforeModification = beforeModification;
    }

    public String getAfterModification() {
        return this.afterModification;
    }

    public void setAfterModification(String afterModification) {
        this.afterModification = afterModification;
    }

    public AvisTech getItemId() {
        return this.itemId;
    }

    public void setItemId(AvisTech itemId) {
        this.itemId = itemId;
    }

    public Date getModifyDate() {
        return this.modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public CoreUser getModifyUser() {
        return this.modifyUser;
    }

    public void setModifyUser(CoreUser modifyUser) {
        this.modifyUser = modifyUser;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
