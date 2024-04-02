package org.guce.process.atm.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@Entity
@Table(
        name = "TYPEAVTECH"
)
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@XmlRootElement(
        name = "TYPEAVIS"
)
@XmlType(
        propOrder = {"code"}
)
public class TypeAvtech extends AbstractEntity implements Serializable {
    @Column(
            name = "LABEL"
    )
    private String label;

    @Column(
            name = "code",
            length = 155
    )
    private String code;

    @OneToMany(
            mappedBy = "itemId"
    )
    private List<TypeAvtechHistory> historyList;

    @XmlTransient
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @XmlElement(
            name = "Code"
    )
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @XmlTransient
    public String getName() {
        return this.getLabel();
    }

    @XmlTransient
    public List<TypeAvtechHistory> getHistoryList() {
        return historyList;
    }

    public void setHistoryList(List<TypeAvtechHistory> historyList) {
        this.historyList = historyList;
    }
}
