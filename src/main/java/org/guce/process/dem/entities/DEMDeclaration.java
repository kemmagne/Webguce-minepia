package org.guce.process.dem.entities;

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
        name = "DEM_TYPE"
)
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@XmlRootElement(
        name = "DECLARATION"
)
@XmlType(
        propOrder = {"declaration"}
)
public class DEMDeclaration extends AbstractEntity implements Serializable {
    @Column(
            name = "DECLARATION"
    )
    private String declaration;

    @OneToMany(
            mappedBy = "itemId"
    )
    private List<DEMDeclarationHistory> historyList;

    @XmlElement(
            name = "DEM_DECLARATION"
    )
    public String getDeclaration() {
        return declaration;
    }

    public void setDeclaration(String declaration) {
        this.declaration = declaration;
    }

    @XmlTransient
    public String getName() {
        return this.getDeclaration();
    }

    @XmlTransient
    public List<DEMDeclarationHistory> getHistoryList() {
        return historyList;
    }

    public void setHistoryList(List<DEMDeclarationHistory> historyList) {
        this.historyList = historyList;
    }
}
